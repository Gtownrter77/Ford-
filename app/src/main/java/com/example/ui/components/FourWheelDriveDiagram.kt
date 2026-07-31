package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Component3DModel
import com.example.model.VehicleSystem
import kotlin.math.cos
import kotlin.math.sin

enum class SportTrac4WdMode(
    val title: String,
    val subtitle: String,
    val frontTorquePct: Int,
    val rearTorquePct: Int,
    val gearRatio: String,
    val description: String,
    val usageScenario: String,
    val activeColor: Color
) {
    AUTO(
        title = "4X4 AUTO",
        subtitle = "On-Demand Electromagnetic Clutch",
        frontTorquePct = 15, // Variable 0-100%
        rearTorquePct = 85,
        gearRatio = "1.00:1 High Range",
        description = "System runs in 2WD (rear wheels) under normal dry road conditions. When ABS speed sensors detect rear wheel slip, 4WD Control Module energizes the transfer case electromagnetic clutch in milliseconds to send up to 100% torque to front wheels.",
        usageScenario = "Everyday driving, rain, patchy ice, light gravel, paved highways.",
        activeColor = Color(0xFF0284C7)
    ),
    HIGH(
        title = "4X4 HIGH",
        subtitle = "Locked 50/50 Torque Split",
        frontTorquePct = 50,
        rearTorquePct = 50,
        gearRatio = "1.00:1 High Range",
        description = "Electromagnetic clutch is fully energized continuously, locking front and rear driveshafts in a solid 50/50 torque split. Disables 4x4 AUTO slip modulation.",
        usageScenario = "Deep snow, mud, sand, loose off-road trails. NOT for dry pavement (causes drivetrain binding).",
        activeColor = Color(0xFFFF6F00)
    ),
    LOW(
        title = "4X4 LOW",
        subtitle = "2.48:1 Low Range Reduction",
        frontTorquePct = 50,
        rearTorquePct = 50,
        gearRatio = "2.48:1 Low Gear Ratio",
        description = "Electric shift encoder motor rotates transfer case shift cam to lock front/rear shafts AND engage 2.48:1 planetary gear set. Multiplies wheel torque by 2.48x for maximum crawling power.",
        usageScenario = "Severe off-roading, rock crawling, pulling heavy loads out of deep mud, steep boat ramps. Max speed 25 MPH. Must shift transmission to Neutral to engage.",
        activeColor = Color(0xFFEF4444)
    )
}

enum class DrivelineNode(val title: String, val system: String, val specSummary: String) {
    ENGINE_TRANS("4.0L V6 & 5R55E Trans", "Powertrain", "Produces 210 HP & 254 lb-ft Torque"),
    TRANSFER_CASE("BorgWarner 4411 Transfer Case", "4WD System", "Electric Shift / EMC Clutch (MERCON V ATF)"),
    SHIFT_MOTOR("4x4 Electric Encoder Motor", "Electrical / Actuator", "Mounted on T-Case Rear Housing"),
    FRONT_DIFF("Dana 35 SLA Front Axle", "Front Driveline", "Independent Front Diff (75W-90 Synthetic)"),
    FRONT_CV_AXLES("Front CV Axle Shafts & Hubs", "Front Wheels", "Constant Velocity Joints & 32mm Axle Nuts"),
    REAR_DIFF("Ford 8.8\" Rear Axle", "Rear Driveline", "Solid Axle / 3.73 Limited Slip (75W-140)"),
    CONTROL_MODULE("4WD Control Module (GEM)", "Electronics", "Behind Passenger Kick Panel / ABS Speed Inputs")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FourWheelDriveDiagram(
    components: List<Component3DModel>,
    onSelectComponent: (Component3DModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(SportTrac4WdMode.AUTO) }
    var selectedNode by remember { mutableStateOf<DrivelineNode?>(DrivelineNode.TRANSFER_CASE) }
    var showTroubleshooting by remember { mutableStateOf(false) }

    // Pulsing animation for torque power flow
    val infiniteTransition = rememberInfiniteTransition(label = "power_flow")
    val flowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow_offset"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 1. TOP 4WD DASHBOARD MODE SWITCHER
        Surface(
            color = Color(0xFF1E293B),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(activeMode.activeColor.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.TireRepair, contentDescription = null, tint = activeMode.activeColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CONTROL TRAC 4WD SYSTEM DIAGRAM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = activeMode.activeColor
                            )
                            Text(
                                text = "2004 Ford Explorer Sport Trac (BorgWarner 44-11)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        color = if (showTroubleshooting) Color(0xFFFF6F00) else Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .clickable { showTroubleshooting = !showTroubleshooting }
                            .testTag("toggle_4wd_troubleshooting")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showTroubleshooting) "Diagram View" else "4x4 Diagnostics",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selector Buttons (4x4 AUTO / 4x4 HIGH / 4x4 LOW)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SportTrac4WdMode.values().forEach { mode ->
                        val isSelected = activeMode == mode

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { activeMode = mode }
                                .testTag("mode_switch_${mode.name}"),
                            color = if (isSelected) mode.activeColor else Color(0xFF0F172A),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) Color.White else Color(0xFF334155)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = mode.gearRatio,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Mode Status Information Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0B132B),
            border = BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${activeMode.title}: ${activeMode.subtitle}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = activeMode.activeColor
                    )
                    Text(
                        text = activeMode.usageScenario,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Torque Split Pill Badge
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, activeMode.activeColor)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${activeMode.frontTorquePct}% Front / ${activeMode.rearTorquePct}% Rear",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFFFD700)
                        )
                        Text(
                            text = "TORQUE SPLIT",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        if (showTroubleshooting) {
            // 4x4 TROUBLESHOOTING & DIAGNOSTIC CHECKLIST VIEW
            FourWheelDriveTroubleshootingView(
                components = components,
                onSelectComponent = onSelectComponent,
                modifier = Modifier.weight(1f)
            )
        } else {
            // 2. CANVAS SCHEMATIC POWER FLOW DIAGRAM VIEW
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxWidth()
                    .background(Color(0xFF070B14))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("4wd_canvas_diagram")
                ) {
                    val w = size.width
                    val h = size.height

                    // Grid Background lines
                    val gridStep = 60.dp.toPx()
                    var gx = 0f
                    while (gx < w) {
                        drawLine(Color(0xFF1E293B).copy(alpha = 0.3f), Offset(gx, 0f), Offset(gx, h), 1f)
                        gx += gridStep
                    }
                    var gy = 0f
                    while (gy < h) {
                        drawLine(Color(0xFF1E293B).copy(alpha = 0.3f), Offset(0f, gy), Offset(w, gy), 1f)
                        gy += gridStep
                    }

                    // Key Positions on Canvas
                    val engineX = w * 0.5f
                    val engineY = h * 0.18f

                    val transX = w * 0.5f
                    val transY = h * 0.38f

                    val tcaseX = w * 0.5f
                    val tcaseY = h * 0.55f

                    val frontDiffX = w * 0.5f
                    val frontDiffY = h * 0.82f

                    val rearDiffX = w * 0.5f
                    val rearDiffY = h * 0.32f // Rear driveline extended

                    val frontLeftWheelX = w * 0.18f
                    val frontRightWheelX = w * 0.82f
                    val frontAxleY = frontDiffY

                    val rearLeftWheelX = w * 0.18f
                    val rearRightWheelX = w * 0.82f
                    val rearAxleY = h * 0.22f

                    val controlModuleX = w * 0.82f
                    val controlModuleY = h * 0.55f

                    // 1. REAR DRIVESHAFT (Transfer Case to Rear Differential)
                    drawLine(
                        color = Color(0xFF10B981),
                        start = Offset(tcaseX, tcaseY),
                        end = Offset(rearDiffX, rearAxleY),
                        strokeWidth = 8.dp.toPx()
                    )
                    // Animated power dash on Rear Driveshaft
                    drawLine(
                        color = Color.White.copy(alpha = pulseGlow),
                        start = Offset(tcaseX, tcaseY - flowOffset.dp.toPx() % 60f),
                        end = Offset(rearDiffX, rearAxleY),
                        strokeWidth = 4.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), flowOffset)
                    )

                    // 2. FRONT DRIVESHAFT (Transfer Case to Front Differential)
                    val frontPowerColor = if (activeMode == SportTrac4WdMode.AUTO) Color(0xFF0284C7) else activeMode.activeColor
                    drawLine(
                        color = frontPowerColor,
                        start = Offset(tcaseX, tcaseY),
                        end = Offset(frontDiffX, frontDiffY),
                        strokeWidth = if (activeMode == SportTrac4WdMode.AUTO) 5.dp.toPx() else 8.dp.toPx()
                    )
                    drawLine(
                        color = Color.White.copy(alpha = pulseGlow),
                        start = Offset(tcaseX, tcaseY),
                        end = Offset(frontDiffX, frontDiffY),
                        strokeWidth = 4.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), -flowOffset)
                    )

                    // 3. FRONT CV AXLE SHAFTS
                    drawLine(frontPowerColor, Offset(frontDiffX, frontAxleY), Offset(frontLeftWheelX, frontAxleY), 6.dp.toPx())
                    drawLine(frontPowerColor, Offset(frontDiffX, frontAxleY), Offset(frontRightWheelX, frontAxleY), 6.dp.toPx())

                    // 4. REAR AXLE SHAFTS
                    drawLine(Color(0xFF10B981), Offset(rearDiffX, rearAxleY), Offset(rearLeftWheelX, rearAxleY), 6.dp.toPx())
                    drawLine(Color(0xFF10B981), Offset(rearDiffX, rearAxleY), Offset(rearRightWheelX, rearAxleY), 6.dp.toPx())

                    // 5. CONTROL MODULE SIGNAL PATHS (Dashed Yellow Lines)
                    drawPath(
                        path = Path().apply {
                            moveTo(controlModuleX, controlModuleY)
                            lineTo(tcaseX + 50.dp.toPx(), tcaseY)
                        },
                        color = Color(0xFFFFD700),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), flowOffset)
                        )
                    )

                    // DRAW WHEELS
                    // Front Left Wheel
                    drawRoundRect(Color(0xFF334155), Offset(frontLeftWheelX - 15.dp.toPx(), frontAxleY - 30.dp.toPx()), Size(30.dp.toPx(), 60.dp.toPx()), CornerRadius(8.dp.toPx()))
                    // Front Right Wheel
                    drawRoundRect(Color(0xFF334155), Offset(frontRightWheelX - 15.dp.toPx(), frontAxleY - 30.dp.toPx()), Size(30.dp.toPx(), 60.dp.toPx()), CornerRadius(8.dp.toPx()))
                    // Rear Left Wheel
                    drawRoundRect(Color(0xFF334155), Offset(rearLeftWheelX - 15.dp.toPx(), rearAxleY - 30.dp.toPx()), Size(30.dp.toPx(), 60.dp.toPx()), CornerRadius(8.dp.toPx()))
                    // Rear Right Wheel
                    drawRoundRect(Color(0xFF334155), Offset(rearRightWheelX - 15.dp.toPx(), rearAxleY - 30.dp.toPx()), Size(30.dp.toPx(), 60.dp.toPx()), CornerRadius(8.dp.toPx()))

                    // DRAW DRIVELINE NODES
                    // Engine & Transmission Box
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(engineX - 70.dp.toPx(), engineY - 25.dp.toPx()),
                        size = Size(140.dp.toPx(), 90.dp.toPx()),
                        cornerRadius = CornerRadius(12.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Transfer Case Box
                    drawRoundRect(
                        color = activeMode.activeColor,
                        topLeft = Offset(tcaseX - 60.dp.toPx(), tcaseY - 30.dp.toPx()),
                        size = Size(120.dp.toPx(), 60.dp.toPx()),
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )

                    // Front Differential Box
                    drawRoundRect(
                        color = Color(0xFF0F172A),
                        topLeft = Offset(frontDiffX - 45.dp.toPx(), frontDiffY - 25.dp.toPx()),
                        size = Size(90.dp.toPx(), 50.dp.toPx()),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f)))
                    )

                    // Rear Differential Box
                    drawRoundRect(
                        color = Color(0xFF10B981),
                        topLeft = Offset(rearDiffX - 45.dp.toPx(), rearAxleY - 25.dp.toPx()),
                        size = Size(90.dp.toPx(), 50.dp.toPx()),
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )

                    // 4WD Control Module Box
                    drawRoundRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(controlModuleX - 45.dp.toPx(), controlModuleY - 25.dp.toPx()),
                        size = Size(90.dp.toPx(), 50.dp.toPx()),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Overlay Interactive Component Click Buttons over Canvas Coordinates
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Rear Axle Label
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                            modifier = Modifier.clickable { selectedNode = DrivelineNode.REAR_DIFF }
                        ) {
                            Text(
                                text = "Ford 8.8\" Rear Axle (3.73 Limited Slip)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF10B981),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Engine & Trans Label
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.clickable { selectedNode = DrivelineNode.ENGINE_TRANS }
                        ) {
                            Text(
                                text = "4.0L V6 / 5R55E Trans",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Center BorgWarner Transfer Case Highlight Button
                    Surface(
                        color = activeMode.activeColor,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier
                            .clickable { selectedNode = DrivelineNode.TRANSFER_CASE }
                            .testTag("node_transfer_case")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BORGWARNER 44-11 TRANSFER CASE",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Electromagnetic Clutch & 2.48:1 Low Range",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Front Differential Label
                        Surface(
                            color = Color(0xFF0284C7).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF0284C7)),
                            modifier = Modifier.clickable { selectedNode = DrivelineNode.FRONT_DIFF }
                        ) {
                            Text(
                                text = "Dana 35 SLA Front Axle & CV Shafts",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // 4WD Control Module Label
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFD700)),
                            modifier = Modifier.clickable { selectedNode = DrivelineNode.CONTROL_MODULE }
                        ) {
                            Text(
                                text = "4x4 Control Module (GEM)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFD700),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // 3. BOTTOM COMPONENT DETAIL INSPECTOR PANEL
            selectedNode?.let { node ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("4wd_node_detail_card"),
                    color = Color(0xFF1E293B),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFF0284C7).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = node.system,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = node.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            // Match with 3D model if available
                            val matching3dModel = components.find {
                                it.id == "driveshaft_4x4" || it.name.contains("Transfer Case", ignoreCase = true) || it.name.contains("Driveshaft", ignoreCase = true)
                            }

                            if (matching3dModel != null) {
                                Button(
                                    onClick = { onSelectComponent(matching3dModel) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("View 3D Model", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = node.specSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Torque & Fluid Specs Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (node) {
                                            DrivelineNode.TRANSFER_CASE -> "Fill/Drain: 22 ft-lbs | Flanges: 83 ft-lbs"
                                            DrivelineNode.FRONT_DIFF -> "Fill Plug: 24 ft-lbs | Axle Nuts: 184 ft-lbs"
                                            DrivelineNode.REAR_DIFF -> "Diff Cover: 33 ft-lbs (75W-140 Oil)"
                                            else -> "OEM Factory Torque Specs Applied"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FourWheelDriveTroubleshootingView(
    components: List<Component3DModel>,
    onSelectComponent: (Component3DModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DIAGNOSING FLASHING 4X4 HIGH / LOW DASHBOARD LIGHTS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFEF4444)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A common issue on 2004 Sport Trac models is the 4WD HIGH / LOW lights flashing 6 times on start or while driving. This indicates the 4WD Control Module lost position encoder feedback or speed sensor pulse.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Diagnostic Steps
                    val steps = listOf(
                        "Step 1: Check 4WD Fuse #25 (15A) and Fuse #115 (20A) in Battery Junction Box.",
                        "Step 2: Inspect 4x4 Shift Motor Encoder Harness on Transfer Case rear housing for corrosion or frayed wires.",
                        "Step 3: Tap shift motor body gently with rubber mallet while an assistant rotates switch to 4x4 HIGH (frees stuck motor brushes).",
                        "Step 4: Verify ABS Front/Rear Wheel Speed Sensor outputs with OBD-II Live Scanner.",
                        "Step 5: Replace 4x4 Electric Shift Encoder Motor (OEM #1L2Z-7G360-A)."
                    )

                    steps.forEach { stp ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stp, style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Opacity, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BORGWARNER 44-11 TRANSFER CASE FLUID SERVICE",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Capacity: 1.5 Quarts (1.4L) MERCON V Automatic Transmission Fluid",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFFFD700)
                    )

                    Text(
                        text = "Service Interval: Every 60,000 Miles (30,000 Miles if towing or heavy off-roading). ALWAYS remove upper FILL plug before removing lower DRAIN plug to ensure you can refill fluid!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}
