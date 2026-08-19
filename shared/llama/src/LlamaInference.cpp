#include "LlamaInference.h"

#include <algorithm>
#include <filesystem>
#include <mutex>
#include <vector>

#include <llama.h>

namespace sporttrac::llama {

namespace {

std::once_flag llamaBackendOnce;

void initializeLlamaBackend() {
    llama_backend_init();
}

std::string tokenToPiece(const llama_vocab* vocab, llama_token token) {
    char initialBuffer[256];
    auto pieceLength = llama_token_to_piece(
        vocab,
        token,
        initialBuffer,
        sizeof(initialBuffer),
        0,
        true
    );

    if (pieceLength >= 0) {
        return std::string(initialBuffer, static_cast<std::size_t>(pieceLength));
    }

    std::vector<char> fullBuffer(static_cast<std::size_t>(-pieceLength));
    pieceLength = llama_token_to_piece(
        vocab,
        token,
        fullBuffer.data(),
        fullBuffer.size(),
        0,
        true
    );
    return pieceLength >= 0
        ? std::string(fullBuffer.data(), static_cast<std::size_t>(pieceLength))
        : std::string();
}

} // namespace

struct LlamaInference::Impl {
    llama_model* model = nullptr;
    mutable std::mutex mutex;
};

LlamaInference::LlamaInference() : impl_(std::make_unique<Impl>()) {}

LlamaInference::~LlamaInference() {
    unloadModel();
}

bool LlamaInference::loadModel(const std::string& modelPath) {
    std::lock_guard<std::mutex> lock(impl_->mutex);

    if (impl_->model != nullptr) {
        llama_model_free(impl_->model);
        impl_->model = nullptr;
    }
    modelPath_.clear();
    loaded_ = false;

    if (modelPath.empty() || !std::filesystem::is_regular_file(modelPath)) {
        return false;
    }

    std::call_once(llamaBackendOnce, initializeLlamaBackend);

    auto modelParams = llama_model_default_params();
    modelParams.n_gpu_layers = 0; // Portable CPU baseline; backend acceleration is a later platform choice.
    impl_->model = llama_model_load_from_file(modelPath.c_str(), modelParams);
    if (impl_->model == nullptr) {
        return false;
    }

    modelPath_ = modelPath;
    loaded_ = true;
    return true;
}

InferenceResult LlamaInference::generate(
    const std::string& prompt,
    const GenerationConfig& config
) {
    std::lock_guard<std::mutex> lock(impl_->mutex);
    if (!loaded_ || impl_->model == nullptr) {
        return {"", "No GGUF model is loaded.", false};
    }
    if (prompt.empty()) {
        return {"", "Prompt must not be empty.", false};
    }

    const auto* vocab = llama_model_get_vocab(impl_->model);
    const auto promptTokenCount = -llama_tokenize(
        vocab,
        prompt.c_str(),
        static_cast<int32_t>(prompt.size()),
        nullptr,
        0,
        true,
        true
    );
    if (promptTokenCount <= 0) {
        return {"", "Prompt tokenization failed.", false};
    }

    std::vector<llama_token> promptTokens(static_cast<std::size_t>(promptTokenCount));
    if (llama_tokenize(
            vocab,
            prompt.c_str(),
            static_cast<int32_t>(prompt.size()),
            promptTokens.data(),
            promptTokenCount,
            true,
            true
        ) < 0) {
        return {"", "Prompt tokenization failed.", false};
    }

    const auto requestedMaxTokens = std::max<int32_t>(1, config.maxTokens);
    const auto minimumContext = promptTokenCount + requestedMaxTokens + 8;
    const auto contextSize = std::max<int32_t>(config.contextTokens, minimumContext);

    auto contextParams = llama_context_default_params();
    contextParams.n_ctx = static_cast<uint32_t>(contextSize);
    contextParams.n_batch = static_cast<uint32_t>(std::max<int32_t>(promptTokenCount, 512));
    contextParams.no_perf = true;

    llama_context* context = llama_init_from_model(impl_->model, contextParams);
    if (context == nullptr) {
        return {"", "Unable to create llama.cpp context.", false};
    }

    auto samplerParams = llama_sampler_chain_default_params();
    samplerParams.no_perf = true;
    llama_sampler* sampler = llama_sampler_chain_init(samplerParams);
    if (sampler == nullptr) {
        llama_free(context);
        return {"", "Unable to create llama.cpp sampler.", false};
    }

    if (config.temperature <= 0.0F) {
        llama_sampler_chain_add(sampler, llama_sampler_init_greedy());
    } else {
        llama_sampler_chain_add(sampler, llama_sampler_init_temp(config.temperature));
        llama_sampler_chain_add(sampler, llama_sampler_init_dist(LLAMA_DEFAULT_SEED));
    }

    llama_batch batch = llama_batch_get_one(promptTokens.data(), promptTokenCount);
    std::string response;
    bool completed = false;
    std::string error;

    for (int32_t generated = 0; generated < requestedMaxTokens; ++generated) {
        if (llama_decode(context, batch) != 0) {
            error = "llama.cpp decode failed.";
            break;
        }

        const auto nextToken = llama_sampler_sample(sampler, context, -1);
        if (llama_vocab_is_eog(vocab, nextToken)) {
            completed = true;
            break;
        }

        const auto piece = tokenToPiece(vocab, nextToken);
        if (piece.empty()) {
            error = "llama.cpp token conversion failed.";
            break;
        }
        response.append(piece);
        batch = llama_batch_get_one(&nextToken, 1);

        if (generated == requestedMaxTokens - 1) {
            completed = true;
        }
    }

    llama_sampler_free(sampler);
    llama_free(context);
    return {response, error, completed && error.empty()};
}

void LlamaInference::unloadModel() {
    std::lock_guard<std::mutex> lock(impl_->mutex);
    if (impl_->model != nullptr) {
        llama_model_free(impl_->model);
        impl_->model = nullptr;
    }
    modelPath_.clear();
    loaded_ = false;
}

bool LlamaInference::isLoaded() const {
    std::lock_guard<std::mutex> lock(impl_->mutex);
    return loaded_;
}

const std::string& LlamaInference::modelPath() const {
    return modelPath_;
}

} // namespace sporttrac::llama
