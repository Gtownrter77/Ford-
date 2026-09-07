package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SportTracData
import com.example.data.local.MaintenanceEntity
import com.example.model.MaintenanceScheduleItem
import com.example.model.VehicleSystem

@Composable
fun VehicleHealthDashboard(
    currentMileage: Int,
    maintenanceLogs: List<MaintenanceEntity>,
    schedules: List<MaintenanceScheduleItem> = SportTracData.defaultMaintenanceSchedules,
    onLogServiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Calculate individual system health percentages based on mileage since last log
    val systemHealths = remember(currentMileage, maintenanceLogs) {
        VehicleSystem.values().map { sys ->
            val sysSchedules = schedules.filter { it.system == sys }
            if (sysSchedules.isEmpty()) {
                sys to 100f
            } else {
                val totalPercent = sysSchedules.map { item ->
                    val lastLog = maintenanceLogs.firstOrNull { it.scheduleItemId == item.id || it.title.contains(item.title, ignoreCase = true) }
                    val lastServiceMileage = lastLog?.mileageAtService ?: (currentMileage - (item.intervalMiles * 0.4).toInt())
                    val milesSince = (currentMileage - lastServiceMileage).coerceAtLeast(0)
                    ((1.0f - (milesSince.toFloat() / item.intervalMiles.toFloat())) * 100).coerceIn(0f, 100f)
                }.average().toFloat()
                sys to totalPercent
            }
        }.toMap()
    }

    val overallHealth = remember(systemHealths) {
        systemHealths.values.average().toFloat().coerceIn(0f, 100f)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. OVERALL VEHICLE HEALTH RADIAL GAUGE CARD
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("health_gauge_card"),
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VEHICLE HEALTH SCORE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "2004 Ford Explorer Sport Trac",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Surface(
                        color = when {
                            overallHealth > 75f -> Color(0xFF10B981).copy(alpha = 0.2f)
                            overallHealth > 50f -> Color(0xFFFFD700).copy(alpha = 0.2f)
                            else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            when {
                                overallHealth > 75f -> Color(0xFF10B981)
                                overallHealth > 50f -> Color(0xFFFFD700)
                                else -> Color(0xFFEF4444)
                            }
                        )
                    ) {
                        Text(
                            text = when {
                                overallHealth > 75f -> "EXCELLENT"
                                overallHealth > 50f -> "GOOD STANDING"
                                else -> "ATTENTION NEEDED"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when {
                                overallHealth > 75f -> Color(0xFF10B981)
                                overallHealth > 50f -> Color(0xFFFFD700)
                                else -> Color(0xFFEF4444)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Radial Gauge Canvas
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(180.dp)
                ) {
                    val animatedHealth by animateFloatAsState(
                        targetValue = overallHealth,
                        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                        label = "gauge_anim"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 18.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        val arcSize = Size(diameter, diameter)

                        // Background track arc (240 degrees)
                        drawArc(
                            color = Color(0xFF0F172A),
                            startAngle = 150f,
                            sweepAngle = 240f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Foreground health arc
                        val sweep = (animatedHealth / 100f) * 240f
                        val gaugeColor = when {
                            animatedHealth > 75f -> Color(0xFF10B981)
                            animatedHealth > 50f -> Color(0xFFFFD700)
                            else -> Color(0xFFEF4444)
                        }

                        drawArc(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0284C7), gaugeColor)
                            ),
                            startAngle = 150f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${overallHealth.toInt()}%",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                        Text(
                            text = "HEALTH INDEX",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Odometer", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text("%,d mi".format(currentMileage), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Logs Recorded", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text("${maintenanceLogs.size} Services", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Monitored Parts", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text("${schedules.size} Systems", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFFFD700))
                    }
                }
            }
        }

        // 2. SYSTEM HEALTH BREAKDOWN (BAR CHART CANVAS)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("system_health_bars"),
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SYSTEM HEALTH BREAKDOWN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color(0xFF38BDF8)
                )

                Spacer(modifier = Modifier.height(12.dp))

                systemHealths.forEach { (system, health) ->
                    val barColor = when {
                        health > 75f -> Color(0xFF10B981)
                        health > 50f -> Color(0xFFFFD700)
                        else -> Color(0xFFEF4444)
                    }

                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(system.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = system.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "${health.toInt()}%",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = barColor
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Animated Bar Track
                        val animatedBarFraction by animateFloatAsState(
                            targetValue = (health / 100f).coerceIn(0f, 1f),
                            animationSpec = tween(durationMillis = 1000),
                            label = "bar_anim"
                        )

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                        ) {
                            // Background Track
                            drawRect(
                                color = Color(0xFF0F172A),
                                size = size
                            )
                            // Progress Bar
                            drawRect(
                                color = barColor,
                                size = Size(size.width * animatedBarFraction, size.height)
                            )
                        }
                    }
                }
            }
        }

        // 3. UPCOMING MAINTENANCE TIMELINE GRAPH (GANTT MILEAGE AXIS)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("maintenance_timeline_card"),
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MILEAGE MAINTENANCE TIMELINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "Next 40,000 Miles Horizon",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Button(
                        onClick = onLogServiceClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Service", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Timeline Nodes List
                schedules.sortedBy { item ->
                    val lastLog = maintenanceLogs.firstOrNull { it.scheduleItemId == item.id || it.title.contains(item.title, ignoreCase = true) }
                    val lastServiceMileage = lastLog?.mileageAtService ?: (currentMileage - (item.intervalMiles * 0.5).toInt())
                    lastServiceMileage + item.intervalMiles
                }.take(6).forEach { item ->
                    val lastLog = maintenanceLogs.firstOrNull { it.scheduleItemId == item.id || it.title.contains(item.title, ignoreCase = true) }
                    val lastServiceMileage = lastLog?.mileageAtService ?: (currentMileage - (item.intervalMiles * 0.5).toInt())
                    val nextDueMileage = lastServiceMileage + item.intervalMiles
                    val milesRemaining = nextDueMileage - currentMileage

                    val isOverdue = milesRemaining <= 0
                    val isDueSoon = milesRemaining in 1..3000

                    val statusColor = when {
                        isOverdue -> Color(0xFFEF4444)
                        isDueSoon -> Color(0xFFFFD700)
                        else -> Color(0xFF10B981)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Timeline Node Bullet
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(statusColor.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Spec: ${item.fluidTypeOrSpec} • Interval: %,d mi".format(item.intervalMiles),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%,d mi".format(nextDueMileage),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = if (isOverdue) "OVERDUE by %,d mi".format(-milesRemaining)
                                else if (isDueSoon) "DUE IN %,d mi".format(milesRemaining)
                                else "In %,d mi".format(milesRemaining),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                    }
                    Divider(color = Color(0xFF334155), thickness = 0.5.dp)
                }
            }
        }
    }
}
