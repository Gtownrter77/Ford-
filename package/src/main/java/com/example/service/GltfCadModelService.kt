package com.example.service

import com.example.model.*
import com.example.util.GltfMeshNode
import com.example.util.GltfModelParser
import com.example.util.PbrMaterial
import com.example.util.SubAssemblyMeshGenerator
import kotlin.math.cos
import kotlin.math.sin

/**
 * CAD 3D Transform Definition for High-Fidelity GLTF Node Animations
 */
data class CadTransform(
    val position: Point3D = Point3D(0f, 0f, 0f),
    val rotationDeg: Point3D = Point3D(0f, 0f, 0f),
    val scale: Point3D = Point3D(1f, 1f, 1f)
)

/**
 * Keyframe entry in a GLTF CAD animation track for Exploded Views & Step Animations
 */
data class CadAnimationKeyframe(
    val progress: Float, // 0.0f to 1.0f
    val transform: CadTransform,
    val opacity: Float = 1.0f,
    val explodeVector: Point3D = Point3D(0f, 0f, 0f)
)

/**
 * Keyframe Track for a specific CAD Node in the GLTF scene hierarchy
 */
data class CadAnimationTrack(
    val nodeId: String,
    val keyframes: List<CadAnimationKeyframe>
)

/**
 * Named CAD Animation Sequence (e.g. "Exploded_Assembly", "Disassembly_Step_1")
 */
data class CadAnimationSequence(
    val id: String,
    val name: String,
    val durationMs: Long = 2000L,
    val tracks: List<CadAnimationTrack> = emptyList()
)

/**
 * Runtime Component Visibility & Animation State Container for CAD Nodes
 */
data class CadNodeVisibilityState(
    val nodeId: String,
    val isVisible: Boolean = true,
    val isIsolated: Boolean = false,
    val opacity: Float = 1.0f
)

/**
 * High-Fidelity GLTF CAD Asset Representation
 */
data class GltfCadAsset(
    val id: String,
    val name: String,
    val oemPartNumber: String,
    val system: VehicleSystem,
    val cadFormat: String = "GLTF 2.0 (PBR Metallic-Roughness)",
    val boundingBoxSize: Point3D = Point3D(2.0f, 1.5f, 1.2f),
    val meshNodes: List<GltfMeshNode>,
    val subAssemblies: List<SubAssemblyPart> = emptyList(),
    val animationSequences: List<CadAnimationSequence> = emptyList()
)

/**
 * Service module that defines data structures, animation states, and controls for
 * high-fidelity GLTF assets, supporting Exploded Views and component-level visibility toggling.
 */
object GltfCadModelService {

    private val assetCache = mutableMapOf<String, GltfCadAsset>()
    private val visibilityStates = mutableMapOf<String, CadNodeVisibilityState>()

    /**
     * Retrieves or builds a high-fidelity GLTF CAD asset for a given component ID.
     */
    fun getCadAsset(componentId: String): GltfCadAsset {
        return assetCache.getOrPut(componentId) {
            generateHighFidelityGltfAsset(componentId)
        }
    }

    /**
     * Toggles visibility for a specific GLTF CAD node or sub-assembly part.
     */
    fun setNodeVisibility(nodeId: String, isVisible: Boolean) {
        val current = visibilityStates[nodeId] ?: CadNodeVisibilityState(nodeId)
        visibilityStates[nodeId] = current.copy(isVisible = isVisible)
    }

    /**
     * Checks whether a specific node is currently visible.
     */
    fun isNodeVisible(nodeId: String): Boolean {
        return visibilityStates[nodeId]?.isVisible ?: true
    }

    /**
     * Sets isolated mode: makes target node visible, sets all other nodes in the asset to dimmed/hidden.
     */
    fun isolateNode(assetId: String, targetNodeId: String?) {
        val asset = assetCache[assetId] ?: return
        if (targetNodeId == null) {
            // Reset all
            asset.meshNodes.forEach { visibilityStates[it.name] = CadNodeVisibilityState(it.name, isVisible = true) }
            asset.subAssemblies.forEach { visibilityStates[it.id] = CadNodeVisibilityState(it.id, isVisible = true) }
        } else {
            asset.meshNodes.forEach {
                visibilityStates[it.name] = CadNodeVisibilityState(
                    nodeId = it.name,
                    isVisible = (it.name == targetNodeId),
                    isIsolated = (it.name == targetNodeId),
                    opacity = if (it.name == targetNodeId) 1.0f else 0.15f
                )
            }
            asset.subAssemblies.forEach {
                visibilityStates[it.id] = CadNodeVisibilityState(
                    nodeId = it.id,
                    isVisible = (it.id == targetNodeId),
                    isIsolated = (it.id == targetNodeId),
                    opacity = if (it.id == targetNodeId) 1.0f else 0.15f
                )
            }
        }
    }

    /**
     * Interpolates exploded view transformations across all CAD mesh nodes in an asset
     * based on an explosion factor (0.0f = Assembled, 1.0f = Fully Exploded).
     */
    fun computeExplodedNodes(
        asset: GltfCadAsset,
        explodeFactor: Float,
        typeFilter: SubAssemblyType? = null
    ): List<GltfMeshNode> {
        val factor = explodeFactor.coerceIn(0f, 1f)

        return asset.meshNodes.mapNotNull { node ->
            // Check visibility state
            if (!isNodeVisible(node.name)) return@mapNotNull null

            val offset = Point3D(
                x = node.centerOffset.x + node.explodeVector.x * factor,
                y = node.centerOffset.y + node.explodeVector.y * factor,
                z = node.centerOffset.z + node.explodeVector.z * factor
            )

            val transformedVerts = node.vertices.map { v ->
                Point3D(v.x + offset.x, v.y + offset.y, v.z + offset.z)
            }

            node.copy(
                vertices = transformedVerts,
                centerOffset = offset
            )
        }
    }

    /**
     * Interpolates keyframes in an animation track for a given time progress.
     */
    fun interpolateKeyframe(track: CadAnimationTrack, progress: Float): CadTransform {
        if (track.keyframes.isEmpty()) return CadTransform()
        if (track.keyframes.size == 1) return track.keyframes.first().transform

        val sorted = track.keyframes.sortedBy { it.progress }
        val p = progress.coerceIn(0f, 1f)

        if (p <= sorted.first().progress) return sorted.first().transform
        if (p >= sorted.last().progress) return sorted.last().transform

        for (i in 0 until sorted.size - 1) {
            val k1 = sorted[i]
            val k2 = sorted[i + 1]
            if (p in k1.progress..k2.progress) {
                val t = (p - k1.progress) / (k2.progress - k1.progress)
                val pos1 = k1.transform.position
                val pos2 = k2.transform.position
                val interpPos = Point3D(
                    x = pos1.x + (pos2.x - pos1.x) * t,
                    y = pos1.y + (pos2.y - pos1.y) * t,
                    z = pos1.z + (pos2.z - pos1.z) * t
                )
                return CadTransform(position = interpPos)
            }
        }
        return sorted.last().transform
    }

    /**
     * Generates a high-fidelity GLTF asset representation for a specified component.
     */
    private fun generateHighFidelityGltfAsset(componentId: String): GltfCadAsset {
        val nodes = mutableListOf<GltfMeshNode>()
        val subAssemblies = mutableListOf<SubAssemblyPart>()

        val pbrCastIron = PbrMaterial(
            name = "OEM Cast Iron",
            baseColorHex = "#3B82F6",
            metallicFactor = 0.85f,
            roughnessFactor = 0.30f
        )
        val pbrAluminum = PbrMaterial(
            name = "Billet Aluminum",
            baseColorHex = "#0284C7",
            metallicFactor = 0.90f,
            roughnessFactor = 0.20f
        )
        val pbrSteel = PbrMaterial(
            name = "Zinc Plated Steel",
            baseColorHex = "#CBD5E1",
            metallicFactor = 0.95f,
            roughnessFactor = 0.15f
        )

        when (componentId) {
            "engine_block" -> {
                // Main Block Housing
                val (blockVerts, blockFaces) = GltfModelParser.createHighDensityCylinder(
                    radius = 0.8f, height = 1.0f, segments = 16, colorHex = "#0284C7"
                )
                nodes.add(
                    GltfMeshNode(
                        name = "Engine Block Core",
                        vertices = blockVerts,
                        normals = GltfModelParser.calculateNormals(blockVerts, blockFaces),
                        faces = blockFaces,
                        material = pbrCastIron,
                        explodeVector = Point3D(0f, 0f, 0f)
                    )
                )

                // Cylinder Heads
                val (headVerts, headFaces) = GltfModelParser.createHighDensityCylinder(
                    radius = 0.5f, height = 0.4f, segments = 12, colorHex = "#38BDF8"
                )
                nodes.add(
                    GltfMeshNode(
                        name = "Left Cylinder Head",
                        vertices = headVerts.map { Point3D(it.x - 0.5f, it.y + 0.6f, it.z) },
                        normals = GltfModelParser.calculateNormals(headVerts, headFaces),
                        faces = headFaces,
                        material = pbrAluminum,
                        centerOffset = Point3D(-0.5f, 0.6f, 0f),
                        explodeVector = Point3D(-0.8f, 1.2f, 0.3f)
                    )
                )

                subAssemblies.addAll(
                    listOf(
                        SubAssemblyMeshGenerator.createGasketSubAssembly(
                            id = "head_gasket_left",
                            name = "Left MLS Cylinder Head Gasket",
                            width = 1.6f, depth = 0.9f, thickness = 0.03f,
                            localOffset = Point3D(-0.4f, 0.4f, 0.2f),
                            explodeDir = Point3D(-0.5f, 0.8f, 0.2f),
                            explodeMultiplier = 1.8f,
                            colorHex = "#38BDF8"
                        ),
                        SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                            id = "head_bolt_1",
                            name = "TTY M11 Cylinder Head Bolt",
                            headRadius = 0.12f, shankRadius = 0.06f, shankLength = 0.5f,
                            localOffset = Point3D(-0.6f, 0.7f, 0.5f),
                            explodeDir = Point3D(-0.6f, 1.2f, 0.5f),
                            explodeMultiplier = 3.6f,
                            colorHex = "#CBD5E1"
                        )
                    )
                )
            }
            "thermostat_housing" -> {
                val (housingVerts, housingFaces) = GltfModelParser.createHighDensityCylinder(
                    radius = 0.35f, height = 0.5f, segments = 12, colorHex = "#0284C7"
                )
                nodes.add(
                    GltfMeshNode(
                        name = "Thermostat Housing Shell",
                        vertices = housingVerts,
                        normals = GltfModelParser.calculateNormals(housingVerts, housingFaces),
                        faces = housingFaces,
                        material = pbrAluminum,
                        explodeVector = Point3D(0f, 0.5f, 0f)
                    )
                )

                subAssemblies.addAll(
                    listOf(
                        SubAssemblyMeshGenerator.createGasketSubAssembly(
                            id = "stat_o_ring",
                            name = "Viton Thermostat Housing O-Ring",
                            width = 0.45f, depth = 0.45f, thickness = 0.03f,
                            localOffset = Point3D(0f, -0.1f, 0f),
                            explodeDir = Point3D(0f, 1.2f, 0f),
                            explodeMultiplier = 1.7f,
                            colorHex = "#F97316"
                        ),
                        SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                            id = "stat_bolt_1",
                            name = "M6 Flange Mounting Bolt",
                            headRadius = 0.09f, shankRadius = 0.04f, shankLength = 0.30f,
                            localOffset = Point3D(-0.25f, 0.25f, 0.2f),
                            explodeDir = Point3D(-0.3f, 1.4f, 0.2f),
                            explodeMultiplier = 3.4f,
                            colorHex = "#CBD5E1"
                        )
                    )
                )
            }
            else -> {
                val (genVerts, genFaces) = GltfModelParser.createHighDensityCylinder(
                    radius = 0.4f, height = 0.6f, segments = 12, colorHex = "#0284C7"
                )
                nodes.add(
                    GltfMeshNode(
                        name = "Main Housing Body",
                        vertices = genVerts,
                        normals = GltfModelParser.calculateNormals(genVerts, genFaces),
                        faces = genFaces,
                        material = pbrSteel,
                        explodeVector = Point3D(0f, 0.4f, 0f)
                    )
                )

                subAssemblies.addAll(
                    listOf(
                        SubAssemblyMeshGenerator.createGasketSubAssembly(
                            id = "${componentId}_gasket",
                            name = "Molded Seal Gasket",
                            width = 0.6f, depth = 0.4f, thickness = 0.02f,
                            localOffset = Point3D(0f, -0.05f, 0f),
                            explodeDir = Point3D(0f, 1.2f, 0f),
                            colorHex = "#38BDF8"
                        ),
                        SubAssemblyMeshGenerator.createHexBoltSubAssembly(
                            id = "${componentId}_bolt",
                            name = "OEM Assembly Bolt",
                            headRadius = 0.09f, shankRadius = 0.04f, shankLength = 0.3f,
                            localOffset = Point3D(-0.2f, 0.2f, 0.2f),
                            explodeDir = Point3D(-0.2f, 1.4f, 0.2f),
                            colorHex = "#CBD5E1"
                        )
                    )
                )
            }
        }

        val animSequence = CadAnimationSequence(
            id = "explode_seq_1",
            name = "Exploded View Disassembly",
            durationMs = 2500L,
            tracks = nodes.map { node ->
                CadAnimationTrack(
                    nodeId = node.name,
                    keyframes = listOf(
                        CadAnimationKeyframe(0.0f, CadTransform(), 1.0f, Point3D(0f, 0f, 0f)),
                        CadAnimationKeyframe(1.0f, CadTransform(position = node.explodeVector), 1.0f, node.explodeVector)
                    )
                )
            }
        )

        return GltfCadAsset(
            id = componentId,
            name = componentId.replace("_", " ").uppercase(),
            oemPartNumber = "GLTF-CAD-$componentId-V2",
            system = VehicleSystem.ENGINE,
            meshNodes = nodes,
            subAssemblies = subAssemblies,
            animationSequences = listOf(animSequence)
        )
    }
}
