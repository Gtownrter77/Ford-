package com.example.ui.components

import android.content.Context
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.audio.AndroidTtsMentorAudioPlayer
import com.example.audio.MentorAudioPlayer
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Flagship feature addition: a SceneView/Filament repair-inspection realm.
 *
 * Cold-launch contract: this composable is never reached from Lounge. It must be composed only
 * from a guarded 3D route after [SAFE_SHELL_MODE] is deliberately released and a device test is
 * authorized. Catalog parsing, GLB validation, SceneView creation, raycasts, and TTS all begin
 * here—not at startup.
 *
 * Current asset truth: [DEFAULT_WRECK_GLB_PATH] is a contract for a future licensed Sport Trac
 * wreck asset. The repository intentionally contains no substitute model. Until that asset is
 * supplied, the realm presents an explicit asset-required state instead of pretending to render
 * a Ford vehicle.
 */
private const val DEFAULT_PARTS_CATALOG_PATH = "parts_data.json"
private const val DEFAULT_WRECK_GLB_PATH = "models/ford_explorer_sport_trac_2004_wreck.glb"
private const val CAMERA_TRANSITION_MS = 900L

private val RealmInk = Color(0xFF05070C)
private val RealmPanel = Color(0xEE0D1420)
private val RealmSlate = Color(0xFF192437)
private val RealmSteel = Color(0xFF94A3B8)
private val RealmAmber = Color(0xFFF59E0B)
private val RealmAmberDim = Color(0xFF92400E)
private val RealmCyan = Color(0xFF38BDF8)

data class RepairCameraPose(
    val position: Float3,
    val lookAt: Float3
)

data class RepairPart(
    val id: String,
    val nodeNames: Set<String>,
    val partName: String,
    val oemPartNumber: String,
    val aftermarketPartNumber: String,
    val focusCamera: RepairCameraPose,
    val replacementSteps: List<String>,
    val mentorScript: String
)

data class RepairVehicleCatalog(
    val vehicleId: String,
    val displayName: String,
    val modelAssetPath: String,
    val overviewCamera: RepairCameraPose,
    val parts: List<RepairPart>
)

/**
 * JSON contract loader. It is intentionally separate from Room and the ExplorerViewModel so
 * a vehicle can be swapped simply by packaging a different GLB and catalog pair.
 */
private object RepairCatalogLoader {
    suspend fun load(context: Context, catalogPath: String): RepairVehicleCatalog = withContext(Dispatchers.IO) {
        val root = context.assets.open(catalogPath).bufferedReader().use { it.readText() }.let(::JSONObject)
        val vehicle = root.getJSONObject("vehicle")
        RepairVehicleCatalog(
            vehicleId = vehicle.getString("vehicleId"),
            displayName = vehicle.getString("displayName"),
            modelAssetPath = vehicle.getString("modelAssetPath"),
            overviewCamera = vehicle.getJSONObject("overviewCamera").toCameraPose(),
            parts = root.getJSONArray("parts").toParts()
        )
    }

    private fun JSONArray.toParts(): List<RepairPart> = List(length()) { index ->
        val item = getJSONObject(index)
        RepairPart(
            id = item.getString("id"),
            nodeNames = item.getJSONArray("nodeNames").toStringSet(),
            partName = item.getString("partName"),
            oemPartNumber = item.getString("oemPartNumber"),
            aftermarketPartNumber = item.getString("aftermarketPartNumber"),
            focusCamera = item.getJSONObject("focusCamera").toCameraPose(),
            replacementSteps = item.getJSONArray("replacementSteps").toStringList(),
            mentorScript = item.getString("mentorScript")
        )
    }

    private fun JSONArray.toStringSet(): Set<String> = List(length()) { getString(it) }.toSet()
    private fun JSONArray.toStringList(): List<String> = List(length()) { getString(it) }

    private fun JSONObject.toCameraPose(): RepairCameraPose = RepairCameraPose(
        position = getJSONArray("position").toFloat3(),
        lookAt = getJSONArray("lookAt").toFloat3()
    )

    private fun JSONArray.toFloat3(): Float3 = Float3(
        getDouble(0).toFloat(),
        getDouble(1).toFloat(),
        getDouble(2).toFloat()
    )
}

private sealed interface RepairRealmLoadState {
    data object Preparing : RepairRealmLoadState
    data class Ready(val catalog: RepairVehicleCatalog) : RepairRealmLoadState
    data class AssetRequired(val catalog: RepairVehicleCatalog) : RepairRealmLoadState
    data class Error(val message: String) : RepairRealmLoadState
}

/**
 * Future-ready VR contract. This UI switch deliberately does not claim stereoscopic or
 * head-tracked rendering is active: Android Cardboard/OpenXR needs a dedicated XR renderer,
 * device capability check, and physical validation. Keeping the state here allows that adapter
 * to be added without changing vehicle metadata, raycasts, cards, or mentor audio.
 */
private data class VrModeState(
    val requested: Boolean = false,
    val rendererReady: Boolean = false
)

@Composable
fun InteractiveRepairViewer(
    modifier: Modifier = Modifier,
    catalogAssetPath: String = DEFAULT_PARTS_CATALOG_PATH,
    onExit: () -> Unit = {}
) {
    val context = LocalContext.current
    var loadState by remember { mutableStateOf<RepairRealmLoadState>(RepairRealmLoadState.Preparing) }
    var selectedPart by remember { mutableStateOf<RepairPart?>(null) }
    var sceneView by remember { mutableStateOf<SceneView?>(null) }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }
    var vrState by remember { mutableStateOf(VrModeState()) }
    var mentorAudio by remember { mutableStateOf<MentorAudioPlayer?>(null) }
    val latestSceneView by rememberUpdatedState(sceneView)
    val latestModelNode by rememberUpdatedState(modelNode)
    val latestMentorAudio by rememberUpdatedState(mentorAudio)

    LaunchedEffect(catalogAssetPath) {
        loadState = try {
            val catalog = RepairCatalogLoader.load(context, catalogAssetPath)
            if (isAssetPresent(context, catalog.modelAssetPath)) {
                RepairRealmLoadState.Ready(catalog)
            } else {
                RepairRealmLoadState.AssetRequired(catalog)
            }
        } catch (error: Exception) {
            RepairRealmLoadState.Error(error.message ?: "The repair catalog could not be read.")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            latestMentorAudio?.release()
            latestSceneView?.let { view ->
                latestModelNode?.let(view::removeChildNode)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(RealmInk, Color(0xFF101827))))
            .testTag("interactive_repair_realm")
    ) {
        when (val state = loadState) {
            RepairRealmLoadState.Preparing -> RealmLoadingState()
            is RepairRealmLoadState.Error -> RepairRealmErrorState(state.message, onExit)
            is RepairRealmLoadState.AssetRequired -> RepairRealmAssetRequiredState(state.catalog, onExit)
            is RepairRealmLoadState.Ready -> {
                SceneViewRealmSurface(
                    catalog = state.catalog,
                    onSceneViewReady = { sceneView = it },
                    onModelReady = { modelNode = it },
                    onPartRaycast = { hitNode ->
                        findPartForNode(hitNode, state.catalog.parts)?.let { part ->
                            selectedPart = part
                            sceneView?.focusCinematically(part.focusCamera)
                        }
                    }
                )

                RealmHud(
                    catalog = state.catalog,
                    vrState = vrState,
                    onVrChange = { requested -> vrState = vrState.copy(requested = requested) },
                    onReset = {
                        selectedPart = null
                        sceneView?.focusCinematically(state.catalog.overviewCamera)
                    },
                    onExit = onExit
                )

                selectedPart?.let { part ->
                    RepairInspectionCard(
                        part = part,
                        onListen = {
                            val player = mentorAudio ?: AndroidTtsMentorAudioPlayer(context).also { mentorAudio = it }
                            player.speak(part.partName, part.mentorScript)
                        },
                        onDismiss = {
                            mentorAudio?.stop()
                            selectedPart = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SceneViewRealmSurface(
    catalog: RepairVehicleCatalog,
    onSceneViewReady: (SceneView) -> Unit,
    onModelReady: (ModelNode) -> Unit,
    onPartRaycast: (Node?) -> Unit
) {
    var modelRequested by remember(catalog.modelAssetPath) { mutableStateOf(false) }
    var renderError by remember { mutableStateOf<String?>(null) }

    AndroidView(
        factory = { viewContext ->
            SceneView(viewContext).apply {
                onSceneViewReady(this)
                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        onPartRaycast(cameraNode.hitTest(event).firstOrNull()?.node)
                    }
                    false
                }
            }
        },
        update = { view ->
            if (!modelRequested) {
                modelRequested = true
                try {
                    view.modelLoader.loadModelInstanceAsync(catalog.modelAssetPath) { instance ->
                        instance?.let { loadedInstance ->
                            ModelNode(loadedInstance, false, null, null).apply {
                                name = catalog.vehicleId
                                isTouchable = true
                                scaleToUnitCube()
                                centerOrigin()
                                view.addChildNode(this)
                                view.focusCinematically(catalog.overviewCamera, immediate = true)
                                onModelReady(this)
                            }
                        }
                    }
                } catch (error: Exception) {
                    renderError = error.message ?: "Filament could not request the wreck model."
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .testTag("repair_realm_sceneview")
    )

    renderError?.let { error ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            RepairRealmErrorState(error, onExit = {})
        }
    }
}

/** Maps a SceneView raycast node, or any of its parents, to a catalog part. */
private fun findPartForNode(node: Node?, parts: List<RepairPart>): RepairPart? {
    var current = node
    while (current != null) {
        val nodeName = current.name
        parts.firstOrNull { part -> part.nodeNames.any { it.equals(nodeName, ignoreCase = true) } }?.let { return it }
        current = current.parent
    }
    return null
}

/** Smooths position with SceneView's node animator and frames the target immediately. */
private fun SceneView.focusCinematically(pose: RepairCameraPose, immediate: Boolean = false) {
    if (immediate) {
        cameraNode.position = pose.position
    } else {
        cameraNode.animatePositions(cameraNode.position, pose.position).apply {
            duration = CAMERA_TRANSITION_MS
            interpolator = android.view.animation.DecelerateInterpolator()
            start()
        }
    }
    cameraNode.lookAt(pose.lookAt, Float3(0f, 1f, 0f), false, 1f)
}

private fun isAssetPresent(context: Context, assetPath: String): Boolean = runCatching {
    context.assets.open(assetPath).close()
    true
}.getOrDefault(false)

@Composable
private fun RealmHud(
    catalog: RepairVehicleCatalog,
    vrState: VrModeState,
    onVrChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 16.dp, end = 16.dp)
    ) {
        Surface(
            color = RealmPanel,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ViewInAr, null, tint = RealmAmber, modifier = Modifier.size(21.dp))
                Spacer(Modifier.width(9.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("REPAIR REALM", color = RealmAmber, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp)
                    Text(catalog.displayName, color = Color.White, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Text("FILAMENT // LIVE", color = RealmCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(Modifier.height(9.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = RealmPanel, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("VR MODE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Switch(checked = vrState.requested, onCheckedChange = onVrChange)
                }
            }
            OutlinedButton(
                onClick = onReset,
                border = androidx.compose.foundation.BorderStroke(1.dp, RealmAmber),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RealmAmber)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(5.dp))
                Text("RESET")
            }
            OutlinedButton(
                onClick = onExit,
                border = androidx.compose.foundation.BorderStroke(1.dp, RealmSteel),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) { Icon(Icons.Default.Close, "Exit Repair Realm", modifier = Modifier.size(16.dp)) }
        }
        if (vrState.requested) {
            Text(
                "VR requested — stereo/head tracking adapter is not included in this build.",
                color = RealmSteel,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun RepairInspectionCard(part: RepairPart, onListen: () -> Unit, onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(animationSpec = tween(420, easing = FastOutSlowInEasing)) { it / 2 } + fadeIn(),
        exit = slideOutVertically { it / 2 } + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFA101827)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RealmAmberDim, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PART INSPECTION", color = RealmAmber, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(part.partName, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    }
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close part inspection",
                        tint = RealmSteel,
                        modifier = Modifier.clickable(onClick = onDismiss).padding(4.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                PartNumberRow("OEM", part.oemPartNumber)
                PartNumberRow("AFTERMARKET", part.aftermarketPartNumber)
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF334155))
                Spacer(Modifier.height(12.dp))
                Text("REPLACEMENT PATH", color = RealmCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.7.sp)
                Spacer(Modifier.height(7.dp))
                part.replacementSteps.forEachIndexed { index, step ->
                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                        Text("${index + 1}", color = RealmAmber, fontWeight = FontWeight.Black, modifier = Modifier.width(22.dp))
                        Text(step, color = Color(0xFFD7E0EA), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onListen,
                    colors = ButtonDefaults.buttonColors(containerColor = RealmAmber, contentColor = Color(0xFF111827)),
                    modifier = Modifier.fillMaxWidth().testTag("listen_to_mentor_button")
                ) {
                    Icon(Icons.Default.Headphones, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("LISTEN TO MENTOR", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun PartNumberRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
        Text(label, color = RealmSteel, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(96.dp))
        Text(value, color = Color.White, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun RealmLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = RealmAmber)
            Spacer(Modifier.height(14.dp))
            Text("PREPARING REPAIR REALM", color = Color.White, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Text("Loading the local inspection catalog…", color = RealmSteel, fontSize = 12.sp)
        }
    }
}

@Composable
private fun RepairRealmAssetRequiredState(catalog: RepairVehicleCatalog, onExit: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Card(colors = CardDefaults.cardColors(containerColor = RealmPanel), shape = RoundedCornerShape(22.dp)) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("REPAIR REALM ASSET REQUIRED", color = RealmAmber, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Text(catalog.displayName, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(10.dp))
                Text(
                    "The inspection metadata is packaged, but this build does not contain a licensed Sport Trac wreck GLB. Add the selected GLB at:\nassets/${catalog.modelAssetPath}",
                    color = Color(0xFFD7E0EA),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Required node contract: front_bumper, front_left_wheel, driver_front_door, engine_assembly, rear_bumper. Update parts_data.json if the imported GLB uses different node names.",
                    color = RealmSteel,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(18.dp))
                OutlinedButton(onClick = onExit, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Text("RETURN TO LOUNGE")
                }
            }
        }
    }
}

@Composable
private fun RepairRealmErrorState(message: String, onExit: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Card(colors = CardDefaults.cardColors(containerColor = RealmPanel), shape = RoundedCornerShape(22.dp)) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("REPAIR REALM NOT AVAILABLE", color = RealmAmber, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(10.dp))
                Text(message, color = Color.White)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onExit, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Text("RETURN TO LOUNGE")
                }
            }
        }
    }
}
