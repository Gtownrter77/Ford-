package com.example.localai

/**
 * JNI declaration scaffold only.
 *
 * No System.loadLibrary call is present, so this class cannot invoke native code
 * until an approved CMake/NDK integration phase adds the native library.
 */
class LlamaNative {
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
