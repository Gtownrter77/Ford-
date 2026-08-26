package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.VehicleAsset

/**
 * Honest hub state. The product wants Blender-level accuracy. That file is missing.
 */
@Composable
fun VehicleAccuracyBlockedScreen(
    onOpenSchematicPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF05070C),
        modifier = modifier
            .fillMaxSize()
            .testTag("vehicle_accuracy_blocked")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            Text(
                "3D HUB — ACCURACY BLOCKED",
                color = Color(0xFFF59E0B),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                VehicleAsset.DISPLAY_NAME,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Blender-level graphics and 100% vehicle accuracy are not finished. There is no licensed wreck GLB in this APK. Filament is wired and will load the truck the moment this file is packaged:",
                color = Color(0xFFCBD5E1),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))
            Surface(color = Color(0xFF111827), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    "app/src/main/assets/${VehicleAsset.WRECK_GLB_PATH}",
                    color = Color(0xFF38BDF8),
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Required Blender export: glTF 2.0 .glb, Principled BSDF, metallic/roughness maps, nodes front_bumper, front_left_wheel, driver_front_door, engine_assembly, rear_bumper.",
                color = Color(0xFF94A3B8),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onOpenSchematicPractice,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_schematic_practice_btn")
            ) {
                Text("Open schematic practice layer (not accurate)", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                Text("Filament wreck model — waiting on GLB")
            }
        }
    }
}
