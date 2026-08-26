package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mentor.OnModelInstruction
import com.example.mentor.OnModelInstructions
import com.example.model.Component3DModel

@Composable
fun OnModelInstructionOverlay(
    component: Component3DModel?,
    modifier: Modifier = Modifier
) {
    if (component == null) return
    val instructions = OnModelInstructions.forComponent(component)
    if (instructions.isEmpty()) return
    var activeIndex by rememberSaveable(component.id) { mutableIntStateOf(0) }
    val safeIndex = activeIndex.coerceIn(0, instructions.lastIndex)
    val active = instructions[safeIndex]

    Surface(
        color = Color(0xF2101827),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("on_model_instruction_overlay")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "ON MODEL — ${component.name}",
                color = Color(0xFFFBBF24),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 0.6.sp)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                instructions.forEachIndexed { index, step ->
                    val selected = index == safeIndex
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (selected) Color(0xFFF59E0B) else Color(0xFF1E293B))
                            .clickable { activeIndex = index }
                            .testTag("on_model_step_pin_${step.index}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${step.index}",
                            color = if (selected) Color(0xFF111827) else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            InstructionCallout(active)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { if (safeIndex > 0) activeIndex = safeIndex - 1 },
                    enabled = safeIndex > 0,
                    modifier = Modifier.testTag("on_model_prev_step")
                ) { Text("Prev") }
                Button(
                    onClick = { if (safeIndex < instructions.lastIndex) activeIndex = safeIndex + 1 },
                    enabled = safeIndex < instructions.lastIndex,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B), contentColor = Color(0xFF111827)),
                    modifier = Modifier.testTag("on_model_next_step")
                ) { Text("Next on model", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun InstructionCallout(step: OnModelInstruction) {
    Column {
        Text(
            "STEP ${step.index}  •  ${step.title}",
            color = Color.White,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(4.dp))
        Text(step.body, color = Color(0xFFE2E8F0), style = MaterialTheme.typography.bodySmall)
        step.warning?.let {
            Spacer(Modifier.height(6.dp))
            Text("WARNING: $it", color = Color(0xFFFECACA), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        }
        step.tip?.let {
            Spacer(Modifier.height(4.dp))
            Text("TIP: $it", color = Color(0xFFA7F3D0), style = MaterialTheme.typography.labelSmall)
        }
    }
}
