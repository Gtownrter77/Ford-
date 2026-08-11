package com.example.data

import com.example.model.Component3DModel
import com.example.model.Point3D
import com.example.model.SubAssemblyPart
import com.example.model.SubAssemblyType
import com.example.model.VehicleSystem
import com.example.util.GltfModelParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * High-fidelity CAD GLTF Asset Data Structures and Animation State Controller.
 * Enables CAD-derived 3D model parsing, exploded view state interpolation,
 * and granular component-level visibility toggles for bolts, gaskets, belts, and sub-assemblies.
 */
data class GltfCadNode(
    val id: String,
    val name: String,
    val type: SubAssemblyType,
    val initialTranslation: Point3D = Point3D(0f, 0f, 0f),
    val explodedDirection: Point3D = Point3D(0f, 1f, 0f),
    val explodedMultiplier: Float = 2.5f,
    val isVisible: Boolean = true,
    val specSheet: String = "",
    val parentNodeId: String? = null
)

data class GltfCadAsset(
    val assetId: String,
    val title: String,
    val system: VehicleSystem,
    val gltfSourceUri: String,
    val nodes: List<GltfCadNode>,
    val metadata: Map<String, String> = emptyMap()
)

data class ExplodedAnimationState(
    val progress: Float = 0.0f, // 0.0f = fully assembled, 1.0f = maximum exploded separation
    val isAnimating: Boolean = false,
    val isolatedComponentId: String? = null,
    val activeHardwareFilters: Set<SubAssemblyType> = SubAssemblyType.values().toSet()
)

class GltfCadAssetService {

    private val _cadAssets = MutableStateFlow<List<GltfCadAsset>>(emptyList())
    val cadAssets: StateFlow<List<GltfCadAsset>> = _cadAssets.asStateFlow()

    private val _explodedState = MutableStateFlow(ExplodedAnimationState())
    val explodedState: StateFlow<ExplodedAnimationState> = _explodedState.asStateFlow()

    private val _visibilityMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val visibilityMap: StateFlow<Map<String, Boolean>> = _visibilityMap.asStateFlow()

    init {
        loadSampleCadRegistry()
    }

    private fun loadSampleCadRegistry() {
        val engineCadNodes = listOf(
            GltfCadNode(
                id = "node_cylinder_head_left",
                name = "Left Cylinder Head Casting",
                type = SubAssemblyType.MAIN_BODY,
                initialTranslation = Point3D(-0.4f, 0.4f, 0.2f),
                explodedDirection = Point3D(-0.5f, 0.8f, 0.2f),
                explodedMultiplier = 1.8f,
                specSheet = "Aluminum Cast Cylinder Head Assembly"
            ),
            GltfCadNode(
                id = "node_head_gasket_left",
                name = "Left Head Multi-Layer Steel Gasket",
                type = SubAssemblyType.GASKET,
                initialTranslation = Point3D(-0.4f, 0.35f, 0.2f),
                explodedDirection = Point3D(-0.5f, 0.8f, 0.2f),
                explodedMultiplier = 2.2f,
                specSheet = "MLS 3-Ply Gasket OEM #1L2Z-6051-BA"
            ),
            GltfCadNode(
                id = "node_head_bolt_1",
                name = "Cylinder Head TTY Flange Bolt M11",
                type = SubAssemblyType.BOLT,
                initialTranslation = Point3D(-0.6f, 0.7f, 0.5f),
                explodedDirection = Point3D(-0.6f, 1.2f, 0.5f),
                explodedMultiplier = 3.6f,
                specSheet = "M11x1.50 TTY Head Bolt"
            ),
            GltfCadNode(
                id = "node_head_washer_1",
                name = "Head Bolt Lock Washer",
                type = SubAssemblyType.WASHER,
                initialTranslation = Point3D(-0.6f, 0.55f, 0.5f),
                explodedDirection = Point3D(-0.6f, 1.0f, 0.5f),
                explodedMultiplier = 2.8f,
                specSheet = "M11 Belleville Washer"
            )
        )

        val engineAsset = GltfCadAsset(
            assetId = "cad_40l_sohc_engine",
            title = "Ford 4.0L SOHC V6 CAD Model",
            system = VehicleSystem.ENGINE,
            gltfSourceUri = "assets/models/40l_sohc_engine.gltf",
            nodes = engineCadNodes,
            metadata = mapOf("cadFormat" to "STEP / GLTF 2.0", "vertexCount" to "14,820", "tolerance" to "±0.02mm")
        )

        _cadAssets.value = listOf(engineAsset)

        val initialVisibility = engineCadNodes.associate { it.id to true }
        _visibilityMap.value = initialVisibility
    }

    /**
     * Calculates the interpolated 3D position of a node based on the current exploded view factor.
     */
    fun calculateExplodedPosition(node: GltfCadNode, progress: Float): Point3D {
        val factor = progress.coerceIn(0f, 1f)
        return Point3D(
            x = node.initialTranslation.x + node.explodedDirection.x * factor * node.explodedMultiplier,
            y = node.initialTranslation.y + node.explodedDirection.y * factor * node.explodedMultiplier,
            z = node.initialTranslation.z + node.explodedDirection.z * factor * node.explodedMultiplier
        )
    }

    /**
     * Toggles component-level visibility for a specific GLTF node ID.
     */
    fun toggleNodeVisibility(nodeId: String) {
        _visibilityMap.update { current ->
            val updated = current.toMutableMap()
            updated[nodeId] = !(updated[nodeId] ?: true)
            updated
        }
    }

    /**
     * Shows or hides all sub-assemblies matching a specific hardware category (e.g. Bolts, Washers, Gaskets).
     */
    fun toggleHardwareFilter(type: SubAssemblyType) {
        _explodedState.update { current ->
            val updatedFilters = current.activeHardwareFilters.toMutableSet()
            if (updatedFilters.contains(type)) {
                updatedFilters.remove(type)
            } else {
                updatedFilters.add(type)
            }
            current.copy(activeHardwareFilters = updatedFilters)
        }
    }

    /**
     * Updates the global exploded separation factor (0.0f..1.0f).
     */
    fun setExplodeProgress(progress: Float) {
        _explodedState.update { it.copy(progress = progress.coerceIn(0f, 1f)) }
    }

    /**
     * Isolates a single sub-assembly component, hiding surrounding parts for detailed view.
     */
    fun isolateComponent(nodeId: String?) {
        _explodedState.update { it.copy(isolatedComponentId = nodeId) }
    }
}
