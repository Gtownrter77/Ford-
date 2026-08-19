import Foundation

/// Swift-facing scaffold. It does not invoke llama.cpp until the bridge target is wired.
final class LocalLlama {
    private let bridge = LlamaInferenceBridge()

    func load(modelPath: String) throws {
        var error: NSError?
        guard bridge.loadModel(atPath: modelPath, error: &error) else {
            throw error ?? NSError(
                domain: "com.sporttrac.localai.llama",
                code: 1,
                userInfo: [NSLocalizedDescriptionKey: "Llama bridge is not wired."]
            )
        }
    }

    func generate(prompt: String, maxTokens: Int = 256, temperature: Float = 0.2) throws -> String {
        var error: NSError?
        let response = bridge.generate(
            forPrompt: prompt,
            maxTokens: maxTokens,
            temperature: temperature,
            error: &error
        )
        if let error {
            throw error
        }
        return response
    }

    func unload() {
        bridge.unloadModel()
    }
}
