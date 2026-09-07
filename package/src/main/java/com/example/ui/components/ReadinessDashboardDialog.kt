package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PartsReadinessPackage
import com.example.data.SportTracPartsReadiness
import com.example.model.DashboardAlertKind
import com.example.model.DashboardAlertRule

@Composable
fun ReadinessDashboardDialog(
    onDismiss: () -> Unit,
    onReviewPackage: (PartsReadinessPackage) -> Unit
) {
    val context = LocalContext.current
    val packages = remember { SportTracPartsReadiness.packages }
    val preferences = remember {
        context.getSharedPreferences("weekly_price_watch", Context.MODE_PRIVATE)
    }
    val watchReadyCount = remember {
        SportTracPartsReadiness.allPreparedPartIds.count { preferences.getBoolean("enabled_$it", false) }
    }
    val rules = remember { dashboardRules(packages) }
    val totalPending = remember { packages.sumOf { it.pendingFitmentItems.size } }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF071B2A),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFE2E8F0),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF0284C7), shape = RoundedCornerShape(9.dp)) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp).size(20.dp))
                }
                Spacer(modifier = Modifier.width(9.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vehicle Readiness Dashboard", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text("All systems · all-inclusive planning", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFF0C2B3F),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("ONE PLACE TO STAY READY", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 0.5.sp), color = Color(0xFFBAE6FD))
                        Text(
                            text = "$watchReadyCount watched part records, $totalPending VIN/capacity lookup items, and ${packages.size} organized vehicle-system packages. The dashboard only prepares, compares, reminds, and links back to the model. It cannot buy anything.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE0F2FE)
                        )
                    }
                }

                Text("ACTIVE READINESS RULES", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black), color = Color(0xFFBAE6FD))
                rules.forEach { rule -> DashboardRuleRow(rule) }

                Text("SYSTEM PACKAGES", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black), color = Color(0xFFFDE68A))
                packages.forEach { readinessPackage ->
                    val isAc = readinessPackage.id == SportTracPartsReadiness.defaultWatchPackage.id
                    Surface(
                        color = if (isAc) Color(0xFF064E3B) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(11.dp),
                        border = BorderStroke(1.dp, if (isAc) Color(0xFF34D399) else Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReviewPackage(readinessPackage) }
                            .testTag("dashboard_package_${readinessPackage.id}")
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isAc) Icons.Default.AcUnit else Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = if (isAc) Color(0xFF6EE7B7) else Color(0xFF7DD3FC),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(readinessPackage.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                Text(
                                    text = "${readinessPackage.partIds.size} watch-ready · ${readinessPackage.pendingFitmentItems.size} verification queue",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Icon(Icons.Default.NavigateNext, contentDescription = "Open package", tint = Color(0xFF7DD3FC), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier.testTag("readiness_dashboard_done")
            ) { Text("Done") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun DashboardRuleRow(rule: DashboardAlertRule) {
    val (icon, color) = when (rule.kind) {
        DashboardAlertKind.WEEKLY_PRICE_REVIEW -> Icons.Default.Schedule to Color(0xFF38BDF8)
        DashboardAlertKind.MAINTENANCE_WINDOW -> Icons.Default.Event to Color(0xFFF59E0B)
        DashboardAlertKind.SEASONAL_PREP -> Icons.Default.AcUnit to Color(0xFF6EE7B7)
        DashboardAlertKind.FITMENT_QUEUE -> Icons.Default.VerifiedUser to Color(0xFFC4B5FD)
    }
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(rule.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text(rule.detail, style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                if (rule.requiresVerification) {
                    Text("Verification required before order", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFFDE68A))
                }
            }
        }
    }
}

private fun dashboardRules(packages: List<PartsReadinessPackage>): List<DashboardAlertRule> {
    val acPackage = SportTracPartsReadiness.defaultWatchPackage
    return listOf(
        DashboardAlertRule(
            id = "weekly_price_review",
            kind = DashboardAlertKind.WEEKLY_PRICE_REVIEW,
            title = "Weekly parts review",
            detail = "Review enabled parts against saved O'Reilly Pro, RockAuto, Amazon, eBay, Facebook Marketplace, and other-source records. Compare delivered total, shipping, core charge, seller, and fitment evidence.",
            priority = 1
        ),
        DashboardAlertRule(
            id = "ac_seasonal_prep",
            kind = DashboardAlertKind.SEASONAL_PREP,
            title = "A/C and cooling seasonal preparation",
            detail = "Before sustained hot weather, practice the A/C diagnosis flow: airflow, blower/resistor, clutch-command, belt, condenser visual condition, and cooling-system visual checks. This is not an automatic refrigerant-charge instruction.",
            packageId = acPackage.id,
            priority = 1,
            requiresVerification = true
        ),
        DashboardAlertRule(
            id = "maintenance_window",
            kind = DashboardAlertKind.MAINTENANCE_WINDOW,
            title = "Maintenance readiness window",
            detail = "Keep oil/filter, spark-plug, brake, tire, cooling, battery/charging, and visibility packages ready for the mileage and calendar intervals you record in the app.",
            priority = 2
        ),
        DashboardAlertRule(
            id = "vin_fitment_queue",
            kind = DashboardAlertKind.FITMENT_QUEUE,
            title = "VIN and capacity verification queue",
            detail = "Complete the queued fitment items—such as fuel pump, water pump, radiator/hoses, starter, air filter, wire set, fuses, coolant, and A/C controls—before a price watch can become a purchase candidate.",
            priority = 2,
            requiresVerification = true
        ),
        DashboardAlertRule(
            id = "system_coverage",
            kind = DashboardAlertKind.FITMENT_QUEUE,
            title = "All-system readiness",
            detail = "${packages.size} system packages keep the catalog organized across climate, maintenance, brakes, 4WD/drivetrain, visibility/electrical, body/cabin, and audio/reference work.",
            priority = 3
        )
    )
}
