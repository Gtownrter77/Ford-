package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PartData
import com.example.data.PartDataLoader

/**
 * Minimal data adapter. It leaves InteractiveRepairViewer's rendering and mentor-audio paths
 * unchanged, listening only to its public onPartTap(partId) event.
 */
@Composable
fun RepairViewerWithData(
    loader: PartDataLoader,
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {}
) {
    var inspectionData by remember { mutableStateOf<PartData?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        InteractiveRepairViewer(
            modifier = Modifier.fillMaxSize(),
            onExit = onExit,
            onPartTap = { partId ->
                inspectionData = loader.getPartData(partId) ?: PartData(
                    id = partId,
                    name = "Unknown Part",
                    system = "Unknown",
                    description = "No inspection data is available for \"$partId\"."
                )
            }
        )

        inspectionData?.let { data ->
            PartDataInspectionCard(
                data = data,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun PartDataInspectionCard(data: PartData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF20D1420))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(data.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(data.system, color = Color(0xFFF59E0B), style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(6.dp))
            Text(data.description, color = Color(0xFFD7E0EA), style = MaterialTheme.typography.bodySmall)
        }
    }
}

/*
Sample usage:

val loader = remember { PartDataLoader.fromAssets(context) }
RepairViewerWithData(loader = loader, onExit = onReturnToLounge)
*/
