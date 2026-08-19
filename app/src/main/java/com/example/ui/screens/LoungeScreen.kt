package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Track 1 cold-launch boundary. This first-render surface is intentionally local-only:
 * no SharedPreferences, Room state, ViewModel, callbacks to feature routes, coroutines,
 * navigation, or voice services. The sign-out placeholder uses Compose state only.
 */
@Composable
fun LoungeScreen(
    modifier: Modifier = Modifier
) {
    var showSignOutConfirmation by remember { mutableStateOf(false) }
    var isSignedOut by remember { mutableStateOf(false) }

    if (isSignedOut) {
        SignedOutLoungeScreen(
            modifier = modifier,
            onReturnToLounge = { isSignedOut = false }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
            .padding(24.dp)
            .testTag("lounge_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "THE LOUNGE",
            color = Color(0xFFF59E0B),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Your Sport Trac is waiting.",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF172554)),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("lounge_cold_launch_notice")
        ) {
            Text(
                text = "SAFE START\nThe Lounge is open. Feature rooms remain offline until they are individually cleared for device testing.",
                color = Color(0xFFDBEAFE),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        OutlinedButton(
            onClick = { showSignOutConfirmation = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
            border = BorderStroke(1.dp, Color(0xFFF59E0B)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("lounge_sign_out_button")
        ) {
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }
    }

    if (showSignOutConfirmation) {
        LoungeSignOutDialog(
            onConfirm = {
                // This safe shell has no persisted user session to clear. Reset local UI only.
                showSignOutConfirmation = false
                isSignedOut = true
            },
            onDismiss = { showSignOutConfirmation = false }
        )
    }
}

@Composable
private fun LoungeSignOutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Sign out?",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to sign out? This safe build does not store a user session, so this returns only the Lounge to its initial local state.",
                color = Color(0xFFCBD5E1)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF59E0B),
                    contentColor = Color(0xFF111827)
                ),
                modifier = Modifier.testTag("lounge_sign_out_confirm")
            ) {
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
                modifier = Modifier.testTag("lounge_sign_out_cancel")
            ) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF172554),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFCBD5E1)
    )
}

@Composable
private fun SignedOutLoungeScreen(
    modifier: Modifier,
    onReturnToLounge: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
            .padding(24.dp)
            .testTag("lounge_signed_out_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SIGNED OUT",
            color = Color(0xFFF59E0B),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "The Lounge has been reset.",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No account or payment session is stored in this safe-shell build.",
            color = Color(0xFFCBD5E1),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onReturnToLounge,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF59E0B),
                contentColor = Color(0xFF111827)
            ),
            modifier = Modifier.testTag("lounge_return_from_signed_out")
        ) {
            Text("Return to Lounge", fontWeight = FontWeight.Bold)
        }
    }
}
