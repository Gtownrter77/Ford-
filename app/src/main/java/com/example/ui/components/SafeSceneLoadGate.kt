package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Style reminder: this is a transparent performance gate, not a fake loading screen.
 * It keeps the full procedural scene out of the first frame until the user explicitly asks.
 */
@Composable
fun SafeSceneLoadGate(
    availableComponentCount: Int,
    onLoadSafeScene: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xF0142030),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.75f)),
        shadowElevation = 12.dp,
        modifier = modifier
            .padding(20.dp)
            .testTag("safe_scene_load_gate")
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(color = Color(0xFF38BDF8).copy(alpha = 0.16f), shape = RoundedCornerShape(18.dp), modifier = Modifier.size(58.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ViewInAr, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(30.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("Interactive 3D is ready when you are", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = "$availableComponentCount catalog parts are available. The first scene starts with a limited, low-effect view to protect phone responsiveness.",
                color = Color(0xFFCBD5E1),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(7.dp))
                Text("Hardware detail and bloom stay off at first.", color = Color(0xFFFDE68A), style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color(0xFF0284C7),
                contentColor = Color.White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLoadSafeScene)
                    .testTag("load_safe_interactive_scene_btn")
            ) {
                Text(
                    "Load safe interactive scene",
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
