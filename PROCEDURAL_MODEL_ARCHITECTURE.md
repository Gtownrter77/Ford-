# Current Procedural Training Model Architecture

**Project:** 2004 Ford Explorer Sport Trac Android app  
**Status:** Source-backed procedural training model; not a completed Blender/GLTF asset or every-fastener replica.  
**Author:** Manus AI

## 1. Architecture at a glance

The current model is built from four layers:

| Layer | Current implementation | Responsibility |
|---|---|---|
| Domain geometry types | `app/src/main/java/com/example/model/Component3DModel.kt` | Stores vertices, faces, offsets, subassemblies, fasteners, torque data, tools, symptoms, and repair steps. |
| Vehicle registry | `app/src/main/java/com/example/data/SportTracData.kt` | Creates the component catalog and generates simple primitive meshes such as boxes and cylinders. |
| Compose renderer | `app/src/main/java/com/example/ui/components/Interactive3DViewport.kt` | Transforms 3D points, projects them to 2D, shades faces, sorts faces by depth, and draws them with Compose `Canvas`. |
| Screen and navigation | `app/src/main/java/com/example/ui/screens/Model3DScreen.kt` plus `MainActivity.kt` / ViewModel | Supplies the selected component, system filter, callbacks, dialogs, and model-screen controls. |

The data flow is:

```text
SportTracData component registry
        |
        v
List<Component3DModel>
        |
        v
Model3DScreen
        |
        v
Interactive3DViewport
        |
        +--> camera rotation / zoom / explode state
        +--> component filtering
        +--> point rotation and orthographic-style projection
        +--> per-face lighting and depth sorting
        +--> Compose Canvas paths and overlays
        |
        v
User-visible procedural training viewport
```

## 2. Core geometry and training data types

The foundational geometry is intentionally simple: a point is three floating-point coordinates and a face is an indexed polygon.

```kotlin
// Component3DModel.kt

data class Point3D(
    val x: Float,
    val y: Float,
    val z: Float
)

data class Face3D(
    val vertexIndices: List<Int>,
    val colorHex: String? = null
)
```

A component is more than geometry. It carries the repair-training record that the Mentor and detail panels can use.

```kotlin
data class Component3DModel(
    val id: String,
    val name: String,
    val system: VehicleSystem,
    val oemPartNumber: String,
    val description: String,
    val locationDescription: String,
    val difficulty: String,
    val estimatedTimeMinutes: Int,
    val vertices: List<Point3D>,
    val faces: List<Face3D>,
    val centerOffset: Point3D,
    val explodeVector: Point3D,
    val torqueSpecs: List<TorqueSpec>,
    val requiredTools: List<String>,
    val repairSteps: List<RepairStep>,
    val commonSymptoms: List<String>,
    val replacementIntervalMiles: Int? = null,
    val fasteners: List<FastenerInventoryItem> = emptyList(),
    val subAssemblies: List<SubAssemblyPart> = emptyList(),
    val metallicFactor: Float = 0.85f,
    val roughnessFactor: Float = 0.30f,
    val serialNumber: String = "SN-2004-ST-" + oemPartNumber.replace("-", ""),
    val manualSectionRef: String = "Section 303-01A: " + name + " Service"
)
```

`SubAssemblyPart` is the model’s current representation for connected training hardware such as bolts, screws, washers, gaskets, belts, O-rings, and spark-plug assemblies.

```kotlin
data class SubAssemblyPart(
    val id: String,
    val name: String,
    val type: SubAssemblyType,
    val vertices: List<Point3D>,
    val faces: List<Face3D>,
    val localOffset: Point3D = Point3D(0f, 0f, 0f),
    val explodeDirection: Point3D = Point3D(0f, 1f, 0f),
    val explodeDistanceMultiplier: Float = 1.0f,
    val specDetails: String = "",
    val metallicFactor: Float = 0.85f,
    val roughnessFactor: Float = 0.25f
)
```

## 3. Vehicle registry and primitive mesh generation

`SportTracData` is an object-level registry. It contains vehicle specifications and a large list of `Component3DModel` records. The current source uses procedural primitive helpers rather than imported CAD or Blender geometry.

The box helper creates eight vertices and six quad faces:

```kotlin
private fun createBoxMesh(
    width: Float,
    height: Float,
    depth: Float,
    center: Point3D,
    colorHex: String
): Pair<List<Point3D>, List<Face3D>> {
    val w = width / 2f
    val h = height / 2f
    val d = depth / 2f

    val vertices = listOf(
        Point3D(center.x - w, center.y - h, center.z - d),
        Point3D(center.x + w, center.y - h, center.z - d),
        Point3D(center.x + w, center.y + h, center.z - d),
        Point3D(center.x - w, center.y + h, center.z - d),
        Point3D(center.x - w, center.y - h, center.z + d),
        Point3D(center.x + w, center.y - h, center.z + d),
        Point3D(center.x + w, center.y + h, center.z + d),
        Point3D(center.x - w, center.y + h, center.z + d)
    )

    val faces = listOf(
        Face3D(listOf(0, 1, 2, 3), colorHex),
        Face3D(listOf(4, 5, 6, 7), colorHex),
        Face3D(listOf(0, 4, 7, 3), colorHex),
        Face3D(listOf(1, 5, 6, 2), colorHex),
        Face3D(listOf(3, 2, 6, 7), colorHex),
        Face3D(listOf(0, 1, 5, 4), colorHex)
    )

    return Pair(vertices, faces)
}
```

The cylinder helper creates a bottom center, top center, paired ring vertices, side quads, and end-cap triangles. Components in the registry call these helpers directly, for example:

```kotlin
val (v, f) = createCylinderMesh(
    radius = 0.65f,
    height = 1.4f,
    segments = 8,
    center = Point3D(0.6f, 0.4f, 1.2f),
    colorHex = "#06B6D4"
)

Component3DModel(
    id = "ac_compressor_pressure_controls",
    name = "A/C Compressor, Electromagnetic Clutch & High/Low Pressure Cut-off Switches",
    system = VehicleSystem.AIR_CONDITIONING,
    oemPartNumber = "1L2Z-19703-AA",
    vertices = v,
    faces = f,
    centerOffset = Point3D(0.6f, 0.4f, 1.2f),
    explodeVector = Point3D(1.4f, 0.8f, 1.6f),
    // training metadata follows...
)
```

The source currently contains approximately 56 component records in the primary registry, with primitive calls such as `createBoxMesh` and `createCylinderMesh` repeated throughout the catalog. The exact visual complexity varies by component; the records are not equivalent to detailed manufactured CAD parts.

## 4. Renderer state and interaction

`Interactive3DViewport` receives the component list and callbacks from the screen:

```kotlin
@Composable
fun Interactive3DViewport(
    components: List<Component3DModel>,
    selectedComponent: Component3DModel?,
    activeSystemFilter: VehicleSystem,
    onComponentSelect: (Component3DModel) -> Unit,
    onOpenDetailManual: (Component3DModel) -> Unit,
    modifier: Modifier = Modifier
)
```

The viewport keeps camera and training state in Compose state:

```kotlin
var cameraYaw by remember { mutableFloatStateOf(45f) }
var cameraPitch by remember { mutableFloatStateOf(28f) }
var cameraZoom by remember { mutableFloatStateOf(1.0f) }
var explodeFactor by remember { mutableFloatStateOf(0.0f) }
var clipPlaneSlice by remember { mutableFloatStateOf(1.0f) }
var isBiltStepMode by remember { mutableStateOf(true) }
var currentBiltStepIndex by remember { mutableIntStateOf(0) }
```

The visible list is derived from the active vehicle-system filter and layer-controller visibility state:

```kotlin
val visibleComponents = remember(components, activeSystemFilter, layerControllerState) {
    components.filter { comp ->
        val systemMatch =
            activeSystemFilter == VehicleSystem.ALL ||
            comp.system == activeSystemFilter
        systemMatch && layerControllerState.isPartVisible(comp)
    }
}
```

The current version uses a stable marker glow value rather than an always-running pulse transition on the default viewport. Explicit BILT and Blender-style animation controls still have their own state and frame loops when the user starts them.

## 5. Point transformation and projection

The renderer uses a manual rotation and 2D projection path inside `Canvas`. For each component, the explode vector is added to the component’s authored position. Yaw rotates around the vertical axis, pitch rotates the result, and the resulting X/Y values are scaled into screen coordinates.

```kotlin
val explodedX = comp.explodeVector.x * animatedExplode
val explodedY = comp.explodeVector.y * animatedExplode
val explodedZ = comp.explodeVector.z * animatedExplode

val rxCenter = centerWorldX * cosY - centerWorldZ * sinY
val rzCenter = centerWorldX * sinY + centerWorldZ * cosY
val ryCenter = centerWorldY * cosP - rzCenter * sinP
val finalZCenter = centerWorldY * sinP + rzCenter * cosP

val projXCenter = centerX + rxCenter * baseScale
val projYCenter = centerY - ryCenter * baseScale
```

Each vertex follows the same transform:

```kotlin
val projectedVertices = comp.vertices.map { v ->
    val vx = v.x + explodedX
    val vy = v.y + explodedY
    val vz = v.z + explodedZ

    val rx = vx * cosY - vz * sinY
    val rz = vx * sinY + vz * cosY
    val ry = vy * cosP - rz * sinP
    val finalZ = vy * sinP + rz * cosP

    val px = centerX + rx * baseScale
    val py = centerY - ry * baseScale

    Triple(px, py, finalZ)
}
```

This is a hand-written orthographic-style projection. It is not a GPU 3D engine, a physics engine, a CAD kernel, or a GLTF runtime.

## 6. Face construction, shading, and depth ordering

For every face, the renderer creates a Compose `Path`, computes the average transformed Z value, calculates a surface normal from three source vertices, applies a directional-light dot product, and creates a `ProjectedFace` record.

```kotlin
private data class ProjectedFace(
    val path: Path,
    val avgZ: Float,
    val color: Color,
    val strokeColor: Color,
    val strokeWidthPx: Float,
    val isSelected: Boolean,
    val isCurrentStepPart: Boolean,
    val componentId: String,
    val normalZ: Float,
    val specGloss: Float = 0f,
    val centerPos: Offset = Offset.Zero
)
```

The face normal is calculated using a cross product, then the renderer derives diffuse and specular terms:

```kotlin
val edge1 = Point3D(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z)
val edge2 = Point3D(v2.x - v0.x, v2.y - v0.y, v2.z - v0.z)

val nx = edge1.y * edge2.z - edge1.z * edge2.y
val ny = edge1.z * edge2.x - edge1.x * edge2.z
val nz = edge1.x * edge2.y - edge1.y * edge2.x
val nLen = sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(0.001f)

val normX = nx / nLen
val normY = ny / nLen
val normZ = nz / nLen

val dotLight = (
    normX * lightVector.x +
    normY * lightVector.y +
    normZ * lightVector.z
).coerceIn(-1.0f, 1.0f)

val diffuse = max(0.25f, (dotLight + 1.0f) / 2.0f)
```

The renderer stores projected faces and sorts them by depth before drawing. Selection and BILT-step state change fill and stroke colors, while the active render style changes the shading branch.

## 7. Touch interaction and hit testing

Drag gestures update yaw and pitch. Tap gestures compare the tap location against projected component centers and select the nearest component within a fixed radius.

```kotlin
.pointerInput(Unit) {
    detectDragGestures { change, dragAmount ->
        change.consume()
        cameraYaw = (cameraYaw + dragAmount.x * 0.45f) % 360f
        cameraPitch = (cameraPitch - dragAmount.y * 0.45f)
            .coerceIn(-85f, 85f)
    }
}
```

The tap path is center-based rather than polygon-accurate:

```kotlin
val hit = projectedCenters
    .filter {
        sqrt(
            (it.screenPos.x - tapOffset.x).pow(2) +
            (it.screenPos.y - tapOffset.y).pow(2)
        ) < 90f
    }
    .minByOrNull {
        sqrt(
            (it.screenPos.x - tapOffset.x).pow(2) +
            (it.screenPos.y - tapOffset.y).pow(2)
        )
    }
```

That means the current hit testing is useful for training navigation, but it is not exact mesh-surface selection.

## 8. What the current model is and is not

| Claim | Current truth |
|---|---|
| Procedural geometry exists | **Yes.** Source creates primitive meshes and stores vertices/faces in component records. |
| Interactive rotation, zoom, explode state, filters, selection | **Implemented in source.** Device behavior is not independently verified here. |
| Repair-training metadata is attached to components | **Yes.** Torque specs, tools, repair steps, symptoms, fasteners, and manual references are fields in the model records. |
| Every bolt, screw, washer, and trim variant is modeled | **No.** Some subassemblies and fasteners are represented, but universal coverage is not established. |
| Blender or GLTF asset is present | **No evidence in the current architecture.** The renderer is Compose Canvas code despite some UI labels using Blender/GLTF language. |
| Physically to-scale model | **Not verified.** Coordinates are procedural relative positions and are not demonstrated as calibrated physical dimensions. |
| Real-device runtime success | **Not verified.** Sandbox build, tests, APK signing, and ZIP integrity passed; no device logcat or `adb` run is available. |

## 9. Primary files

| File | Role |
|---|---|
| `app/src/main/java/com/example/model/Component3DModel.kt` | Geometry and repair-training data structures. |
| `app/src/main/java/com/example/data/SportTracData.kt` | Vehicle registry, primitive mesh generation, and component records. |
| `app/src/main/java/com/example/ui/components/Interactive3DViewport.kt` | Compose Canvas projection, shading, depth sorting, overlays, and touch handling. |
| `app/src/main/java/com/example/ui/screens/Model3DScreen.kt` | Screen-level composition and dialogs around the viewport. |
| `PROJECT_HANDOFF.md` | Project truth record and implementation-status distinctions. |
| `FINAL_EVIDENCE_LEDGER_2026-08-18.md` | Build, artifact, audit, and verification evidence. |

## 10. Additional coverage findings from the follow-up audit

The first registry count does not tell the whole story. A separate runtime hardware layer augments components with generated fastener inventories and generated subassembly meshes.

`VehicleHardwareCatalog` returns a copied component rather than replacing the original geometry:

```kotlin
return component.copy(
    fasteners = buildInventory(component, profile),
    subAssemblies = component.subAssemblies + addedParts
)
```

The hardware profile assigns reference counts and geometry for bolts, screws, washers, seals, and related hardware by vehicle system. The source includes constructors in `SubAssemblyMeshGenerator` for threaded hex bolts, washers, Torx screws, gaskets, and serpentine belts. This means the project has a real generated-hardware layer, but the counts are catalog/profile rules and reference geometry; they are not evidence that every physical fastener in the truck has been measured and modeled.

The follow-up audit also found a `GltfCadModelService` containing GLTF-shaped data structures such as `GltfMeshNode`, `PbrMaterial`, animation tracks, and a method named `generateHighFidelityGltfAsset`. That service constructs an in-memory `GltfCadAsset` representation from generated nodes and subassemblies. The source tree still contains no bundled `.glb`, `.gltf`, `.obj`, `.fbx`, or `.blend` model file, and the active viewport remains the Compose `Canvas` renderer described above. Therefore the accurate statement is:

> The project contains a GLTF-oriented service abstraction and generated in-memory CAD-style records, but the currently audited visible model path is procedural Compose Canvas rendering, not a loaded external GLTF/Blender asset.

The updated coverage picture is:

| Coverage area | Source evidence | Accurate interpretation |
|---|---|---|
| Primary component registry | 56 `Component3DModel` records in `SportTracData.kt` | Broad system-level component catalog. |
| Primitive mesh construction | 34 box-helper calls and 24 cylinder-helper calls; no sphere-helper calls found in the registry | Mostly low-complexity procedural solids. |
| Repair-training data | 237 `RepairStep` constructor calls and 127 `TorqueSpec` constructor calls in the source file | Substantial embedded instructional metadata, not universal repair coverage. |
| Hardware/subassembly generation | `VehicleHardwareCatalog` plus `SubAssemblyMeshGenerator` | Generated reference hardware attached to components at runtime. |
| External model asset | No `.glb`, `.gltf`, `.obj`, `.fbx`, or `.blend` file in `app/src/main` | No bundled imported 3D asset was verified. |
| GLTF-oriented abstraction | `GltfCadModelService` and related data classes | An architecture/service layer exists, but it does not establish that a finished external model is loaded by the visible screen. |

## 11. Current improvement target

The most honest next model-development target is not to claim completion; it is to replace the broadest primitive components with measured, system-specific geometry one repair path at a time. The A/C workbench is the natural first target because its current training metadata is comparatively detailed, but its compressor, clutch, pressure-control, lines, service ports, orifice tube, accumulator, condenser, evaporator, and HVAC controls still need individually verified geometry and fitment relationships before they can be described as a complete to-scale A/C model.
