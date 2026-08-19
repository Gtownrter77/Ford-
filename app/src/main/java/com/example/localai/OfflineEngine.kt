package com.example.localai

import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * App-private local model owner. It neither downloads nor packages a GGUF file.
 * The model installer must copy an approved model to [modelFile] before [loadModel].
 */
class OfflineEngine(context: Context) : AutoCloseable {

    companion object {
        const val MODEL_FILE_NAME = "DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf"
    }

    private val appContext = context.applicationContext
    private val native = LlamaNative()
    private val lifecycleLock = Any()

    @Volatile
    private var handle: Long = native.nativeCreate()

    private val modelFile: File
        get() = ModelInstallPath.deepSeekQ4Km(appContext)

    suspend fun loadModel(): Boolean = withContext(Dispatchers.Default) {
        synchronized(lifecycleLock) {
            check(handle != 0L) { "OfflineEngine is closed." }
            check(modelFile.isFile) { "GGUF not found at ${modelFile.absolutePath}" }
            native.nativeLoadModel(handle, modelFile.absolutePath)
        }
    }

    /**
     * The current JNI function returns a completed native response, so this flow
     * emits one response chunk. Native token callbacks are required for token-level
     * streaming and are intentionally not fabricated here.
     */
    suspend fun generate(
        prompt: String,
        maxTokens: Int = 256,
        temperature: Float = 0.2F
    ): Flow<String> = flow {
        require(maxTokens > 0) { "maxTokens must be greater than zero." }
        require(temperature >= 0.0F) { "temperature cannot be negative." }

        emit(
            withContext(Dispatchers.Default) {
                synchronized(lifecycleLock) {
                    check(handle != 0L) { "OfflineEngine is closed." }
                    native.nativeGenerate(
                        handle = handle,
                        prompt = prompt,
                        maxTokens = maxTokens,
                        temperature = temperature
                    )
                }
            }
        )
    }

    override fun close() {
        synchronized(lifecycleLock) {
            if (handle != 0L) {
                native.nativeDestroy(handle)
                handle = 0L
            }
        }
    }
}
