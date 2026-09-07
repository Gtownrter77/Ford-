package com.example.auth

import android.app.Activity
import android.content.Context
import com.example.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GoogleAuthManager(private val context: Context) {
    private val auth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()
    private val credentialManager = CredentialManager.create(context)

    fun isSignedIn(): Boolean = auth?.currentUser != null
    fun accountLabel(): String? = auth?.currentUser?.email

    suspend fun signIn(activity: Activity): Result<String> = runCatching {
        val firebaseAuth = auth ?: error("Firebase Auth is not configured. Add google-services.json and Firebase project settings.")
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        require(clientId.isNotBlank() && !clientId.startsWith("REPLACE_")) { "Google web client ID is not configured." }
        val googleOption = GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setFilterByAuthorizedAccounts(false)
            .build()
        val result = credentialManager.getCredential(
            context = activity,
            request = GetCredentialRequest(listOf(googleOption))
        )
        val credential = result.credential
        val tokenCredential = when (credential) {
            is CustomCredential -> {
                require(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) { "Google account credential was not returned." }
                GoogleIdTokenCredential.createFrom(credential.data)
            }
            else -> error("Unsupported Google credential type.")
        }
        awaitFirebaseSignIn(firebaseAuth, tokenCredential.idToken)
        firebaseAuth.currentUser?.email ?: "Google account"
    }

    fun signOut() { auth?.signOut() }

    private suspend fun awaitFirebaseSignIn(firebaseAuth: FirebaseAuth, idToken: String) = suspendCancellableCoroutine { continuation ->
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnSuccessListener { continuation.resume(Unit) }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }
}
