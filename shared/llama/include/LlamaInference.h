#pragma once

#include <cstdint>
#include <memory>
#include <string>

namespace sporttrac::llama {

struct GenerationConfig {
    std::int32_t maxTokens = 256;
    float temperature = 0.2F;
    std::int32_t contextTokens = 4096;
};

struct InferenceResult {
    std::string text;
    std::string error;
    bool completed = false;
};

/**
 * Shared llama.cpp inference contract.
 *
 * The implementation loads a caller-provided GGUF path. It does not download,
 * embed, install, or otherwise distribute a model file.
 */
class LlamaInference {
public:
    LlamaInference();
    ~LlamaInference();

    LlamaInference(const LlamaInference&) = delete;
    LlamaInference& operator=(const LlamaInference&) = delete;

    bool loadModel(const std::string& modelPath);
    InferenceResult generate(const std::string& prompt, const GenerationConfig& config);
    void unloadModel();

    [[nodiscard]] bool isLoaded() const;
    [[nodiscard]] const std::string& modelPath() const;

private:
    struct Impl;
    std::unique_ptr<Impl> impl_;
    std::string modelPath_;
    bool loaded_ = false;
};

} // namespace sporttrac::llama
