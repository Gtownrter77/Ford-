#include "LlamaInference.h"

namespace sporttrac::llama {

bool LlamaInference::loadModel(const std::string& modelPath) {
    // TODO: Wire llama.cpp model loading in the native-inference implementation phase.
    modelPath_ = modelPath;
    loaded_ = false;
    return false;
}

InferenceResult LlamaInference::generate(
    const std::string& /* prompt */,
    const GenerationConfig& /* config */
) {
    return InferenceResult{
        "",
        "LlamaInference is a scaffold; llama.cpp is not linked or initialized.",
        false
    };
}

void LlamaInference::unloadModel() {
    modelPath_.clear();
    loaded_ = false;
}

bool LlamaInference::isLoaded() const {
    return loaded_;
}

const std::string& LlamaInference::modelPath() const {
    return modelPath_;
}

} // namespace sporttrac::llama
