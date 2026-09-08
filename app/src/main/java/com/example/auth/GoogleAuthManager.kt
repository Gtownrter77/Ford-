package com.example.auth

import android.app.Activity
import android.content.Context

/**
 * Local-only stub. Firebase Auth was removed on 2026-09-08.
 * The app no longer gates Mentor, 3D, diagnostics, or records behind a Google account.
 */
class GoogleAuthManager(@Suppress("UNUSED_PARAMETER") context: Context) {
    fun isSignedIn(): Boolean = true
    fun accountLabel(): String? = "local"
    suspend fun signIn(@Suppress("UNUSED_PARAMETER") activity: Activity): Result<String> =
        Result.success("local")
    fun signOut() {}
}
