package com.example.localai

/**
 * JNI entry points for the locally built `sporttrac_llama` shared library.
 */
private object LlamaNativeLibrary {
    init {
        System.loadLibrary("sporttrac_llama")
    }
}

class LlamaNative {
    init {
        LlamaNativeLibrary
    }

    external fun nativeCreate(): Long

    external fun nativeLoadModel(handle: Long, modelPath: String): Boolean

    external fun nativeGenerate(
        handle: Long,
        prompt: String,
        maxTokens: Int,
        temperature: Float
    ): String

    external fun nativeDestroy(handle: Long)
}
