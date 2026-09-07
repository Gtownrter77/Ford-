package com.example.localai

import android.content.Context
import java.io.File

object ModelInstallPath {
    private const val MODEL_DIRECTORY = "models"

    fun deepSeekQ4Km(context: Context): File {
        return File(
            File(context.filesDir, MODEL_DIRECTORY),
            OfflineEngine.MODEL_FILE_NAME
        )
    }
}
