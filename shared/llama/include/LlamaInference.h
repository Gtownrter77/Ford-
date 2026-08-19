#pragma once

#include <cstdint>
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
 * Shared llama.cpp-facing contract only.
 *
 * This class does not include llama.h, link llama.cpp, allocate a model context,
 * read a GGUF file, or perform inference until a native integration phase wires it.
 */
class LlamaInference {
public:
    bool loadModel(const std::string& modelPath);
    InferenceResult generate(const std::string& prompt, const GenerationConfig& config);
    void unloadModel();

    [[nodiscard]] bool isLoaded() const;
    [[nodiscard]] const std::string& modelPath() const;

private:
    std::string modelPath_;
    bool loaded_ = false;
};

} // namespace sporttrac::llama
