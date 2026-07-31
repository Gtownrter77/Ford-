package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Component3DModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDetailSheet(
    component: Component3DModel,
    onDismiss: () -> Unit,
    onAddToCart: ((Component3DModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        contentColor = Color.White,
        tonalElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("component_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header with System Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = component.system.color.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, component.system.color),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(component.system.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = component.system.displayName.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = component.system.color
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Component Title & OEM Part Number
            Text(
                text = component.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ford OEM Part #: ${component.oemPartNumber}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF38BDF8)
                    )
                }

                if (onAddToCart != null) {
                    Button(
                        onClick = { onAddToCart(component) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("btn_add_component_to_cart")
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to Cart", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Quick Stats Row (Time, Difficulty)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("EST. TIME", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text("${component.estimatedTimeMinutes} Mins", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("DIFFICULTY", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(component.difficulty, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 8.dp))

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Description & Location
                item {
                    Column {
                        Text("DESCRIPTION & LOCATION", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(component.description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(component.locationDescription, style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                        }
                    }
                }

                // Torque Specifications Table
                if (component.torqueSpecs.isNotEmpty()) {
                    item {
                        Column {
                            Text("TORQUE SPECIFICATIONS (OEM)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    component.torqueSpecs.forEachIndexed { idx, ts ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1.5f)) {
                                                Text(ts.fastenerName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
                                                if (ts.notes.isNotEmpty()) {
                                                    Text(ts.notes, style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                                }
                                            }
                                            Text(
                                                text = "${ts.torqueFtLbs} ft-lbs (${ts.torqueNm})",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF38BDF8)
                                            )
                                        }
                                        if (idx < component.torqueSpecs.size - 1) {
                                            HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Required Tools List
                item {
                    Column {
                        Text("REQUIRED TOOLS & SUPPLIES", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = Color(0xFF10B981))
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            component.requiredTools.forEach { tool ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(tool, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE2E8F0))
                                }
                            }
                        }
                    }
                }

                // Step-by-Step DIY Repair Manual
                item {
                    Column {
                        Text("DIY REPAIR PROCEDURE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = Color(0xFFFF6F00))
                        Spacer(modifier = Modifier.height(8.dp))

                        component.repairSteps.forEach { step ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFF6F00)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${step.stepNumber}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = step.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = step.instruction,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFCBD5E1)
                                    )

                                    if (step.warning != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            color = Color(0xFF7F1D1D),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(step.warning, style = MaterialTheme.typography.bodySmall, color = Color(0xFFFECACA))
                                            }
                                        }
                                    }

                                    if (step.tip != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            color = Color(0xFF064E3B),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(step.tip, style = MaterialTheme.typography.bodySmall, color = Color(0xA1D1D80))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
