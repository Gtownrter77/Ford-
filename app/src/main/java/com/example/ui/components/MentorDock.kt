package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.mentor.MentorKnowledge
import com.example.model.Component3DModel

/**
 * Mentor lives on the 3D hub. Selecting a part should brief the user from
 * packaged Sport Trac knowledge, then open the full hands-free procedure.
 */
@Composable
fun MentorDock(
    component: Component3DModel?,
    modifier: Modifier = Modifier
) {
    var showMentor by remember { mutableStateOf(false) }
    if (component == null) return

    val brief = remember(component.id) { MentorKnowledge.briefing(component) }
    val failure = brief.knownFailures.firstOrNull()

    if (showMentor) {
        MentorModeDialog(
            component = component,
            onDismiss = { showMentor = false }
        )
    }

    Surface(
        color = Color(0xF0141824),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFF10B981)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("mentor_dock")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "MENTOR — ${brief.vehicleLine}",
                    color = Color(0xFF34D399),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(brief.componentName, color = Color.White, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text("OEM ${brief.oemPartNumber}  •  ${brief.difficulty}  •  ${brief.estimatedMinutes} min", color = Color(0xFF93C5FD), style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                text = failure?.let { "${it.title}. ${it.probableCause}" } ?: brief.location,
                color = Color(0xFFD1FAE5),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(10.dp))
            Row {
                Button(
                    onClick = { showMentor = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669), contentColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mentor_dock_open_procedure")
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Walk this part", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { showMentor = true },
                    modifier = Modifier.testTag("mentor_dock_ask")
                ) {
                    Text("Ask Mentor")
                }
            }
        }
    }
}
