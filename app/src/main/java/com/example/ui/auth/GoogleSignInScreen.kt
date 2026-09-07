package com.example.ui.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.auth.GoogleAuthManager
import kotlinx.coroutines.launch

@Composable
fun GoogleSignInScreen(onSignedIn: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val manager = remember(context) { GoogleAuthManager(context) }
    var status by remember { mutableStateOf("Sign in with Google to access the Mentor and vehicle tools.") }
    var isBusy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ford Sport Trac Mentor", style = MaterialTheme.typography.headlineSmall)
            Text("Account sign-in required", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            Text(status, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 18.dp))
            Button(
                enabled = !isBusy && activity != null,
                onClick = {
                    val host = activity ?: return@Button
                    scope.launch {
                        isBusy = true
                        status = "Opening Google sign-in…"
                        manager.signIn(host).onSuccess { email -> status = "Signed in as $email"; onSignedIn() }.onFailure { status = it.message ?: "Google sign-in failed." }
                        isBusy = false
                    }
                }
            ) { Text(if (isBusy) "Signing in…" else "Continue with Google") }
            Text("No anonymous access. Diagnostic records will be associated with the signed-in account.", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 18.dp))
        }
    }
}
