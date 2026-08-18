# Fastener Mesh Rendering and PBR Truth

## Short answer

Generated fastener meshes are rendered by the same manual Compose `Canvas` pipeline as the parent component. The renderer anchors each subassembly to the parent component, applies the explode offset, rotates and projects every vertex, builds a `Path` for each face, computes a simple face normal and directional-light response, stores the result in a `ProjectedFace`, depth-sorts all faces, and finally calls `drawPath` for the fill and outline.

The material system is **PBR-inspired but not a true PBR shader** in the active Canvas path. `metallicFactor` and `roughnessFactor` are now consumed by the subassembly draw loop to shape a hand-written specular-like highlight: metallic increases highlight strength and roughness broadens/reduces it. This is still not a GPU PBR shader, BRDF, image-based lighting system, or normal-mapped material pipeline.

## 1. How generated fasteners enter a component

`VehicleHardwareCatalog` augments a component by copying it and adding generated inventory and subassembly meshes:

```kotlin
return component.copy(
    fasteners = buildInventory(component, profile),
    subAssemblies = component.subAssemblies + addedParts
)
```

`SubAssemblyMeshGenerator` supplies generated reference meshes for categories including threaded hex bolts, washers, Torx screws, gaskets, and serpentine belts. Each generated part contains vertices, faces, type, local/explode information, and material metadata such as `metallicFactor` and `roughnessFactor`.

## 2. How the active Canvas renderer positions fasteners

Inside `Interactive3DViewport`, the renderer loops over `comp.subAssemblies`. It filters by the selected hardware type and anchors each part once to the parent’s world center:

```kotlin
val subExplodedX = comp.centerOffset.x + explodedX +
    subPart.explodeDirection.x * animatedExplode *
    subPart.explodeDistanceMultiplier

val subExplodedY = comp.centerOffset.y + explodedY +
    subPart.explodeDirection.y * animatedExplode *
    subPart.explodeDistanceMultiplier

val subExplodedZ = comp.centerOffset.z + explodedZ +
    subPart.explodeDirection.z * animatedExplode *
    subPart.explodeDistanceMultiplier
```

Each subassembly vertex then follows the same yaw, pitch, scale, and 2D projection as the parent component:

```kotlin
val subProjVerts = subPart.vertices.map { v ->
    val vx = v.x + subExplodedX
    val vy = v.y + subExplodedY
    val vz = v.z + subExplodedZ

    val rx = vx * cosY - vz * sinY
    val rz = vx * sinY + vz * cosY
    val ry = vy * cosP - rz * sinP
    val finalZ = vy * sinP + rz * cosP

    val px = centerX + rx * baseScale
    val py = centerY - ry * baseScale
    Triple(px, py, finalZ)
}
```

## 3. How fastener colors are chosen

The active renderer selects a fixed semantic color from the subassembly type. Selection overrides the normal color with gold.

```kotlin
val subBaseColor = when {
    isSubSelected -> Color(0xFFFFD700)
    subPart.type == SubAssemblyType.GASKET -> Color(0xFF38BDF8)
    subPart.type == SubAssemblyType.SEAL_O_RING -> Color(0xFFF97316)
    subPart.type == SubAssemblyType.BOLT ||
        subPart.type == SubAssemblyType.SCREW -> Color(0xFFE2E8F0)
    subPart.type == SubAssemblyType.WASHER -> Color(0xFFCBD5E1)
    subPart.type == SubAssemblyType.BELT -> Color(0xFF334155)
    subPart.type == SubAssemblyType.SPARK_PLUG -> Color(0xFFF8FAFC)
    else -> Color(0xFF94A3B8)
}
```

This is a visual category convention. It is not a physically measured material-color system.

## 4. What the renderer calls PBR

For each face, the renderer calculates a normal from two edges, takes a dot product with a fixed directional light, and calculates a specular-like term:

```kotlin
val dotLight = (
    normX * lightVector.x +
    normY * lightVector.y +
    normZ * lightVector.z
).coerceIn(-1.0f, 1.0f)

val diffuse = max(0.35f, (dotLight + 1.0f) / 2.0f)
val reflectZ = 2f * dotLight * normZ - lightVector.z
val specGloss = if (reflectZ > 0f) {
    reflectZ.pow(10f) * 0.45f
} else {
    0f
}
```

The fill color is then built from the fixed base color, diffuse intensity, and specular-like highlight:

```kotlin
val shadedFill = Color(
    red = (subBaseColor.red * diffuse + specGloss).coerceIn(0f, 1f),
    green = (subBaseColor.green * diffuse + specGloss).coerceIn(0f, 1f),
    blue = (subBaseColor.blue * diffuse + specGloss).coerceIn(0f, 1f),
    alpha = 0.98f
)
```

The result is stored in `ProjectedFace`, depth-sorted, and drawn:

```kotlin
facesToDraw.sortBy { it.avgZ }

facesToDraw.forEach { pf ->
    drawPath(pf.path, color = pf.color)
    drawPath(
        pf.path,
        color = pf.strokeColor,
        style = Stroke(width = pf.strokeWidthPx, cap = StrokeCap.Round)
    )
}
```

A later pass adds a glow/bloom-like effect to faces with sufficiently high `specGloss`, selected parts, and current BILT-step parts. This is a second Canvas draw pass, not a physically based post-processing pipeline.

## 5. What `metallicFactor` and `roughnessFactor` actually do today

| Data or feature | Active fastener Canvas path |
|---|---|
| `SubAssemblyPart.metallicFactor` | Used in the current Canvas branch to scale highlight strength. |
| `SubAssemblyPart.roughnessFactor` | Used in the current Canvas branch to shape highlight sharpness and reduce strength as roughness rises. |
| `SubAssemblyType` | Used to select a fixed base color and some outline colors. |
| Face normal | Used for directional diffuse and specular-like calculations. |
| `PbrMaterial` in GLTF/CAD services | Used by GLTF-oriented parsing/service structures; this is separate from the active Canvas subassembly branch. |
| Bloom/glow | Implemented as additional Canvas paths for high-specular or selected faces. |
| True metallic BRDF, image-based lighting, normal maps, roughness-controlled highlights | Not verified in the active Compose Canvas renderer. |

## 6. Accurate conclusion

The current material-aware revision uses the following conservative approximation:

```kotlin
val metallic = subPart.metallicFactor.coerceIn(0f, 1f)
val roughness = subPart.roughnessFactor.coerceIn(0f, 1f)
val highlightPower = (20f - roughness * 14f).coerceIn(6f, 20f)
val highlightStrength = (0.14f + metallic * 0.52f) * (1f - roughness * 0.35f)
val specGloss = if (reflectZ > 0f) {
    reflectZ.pow(highlightPower) * highlightStrength
} else {
    0f
}
```

The honest technical description is:

> The app renders generated fastener meshes as polygonal subassembly faces in a software-defined Compose Canvas pipeline. It performs 3D-to-2D transforms, face-normal lighting, painter-style depth sorting, outlines, and a glow pass. The model records carry PBR-style metallic and roughness fields, and the project has separate GLTF/CAD material abstractions, and the active fastener Canvas branch now uses the stored metallic and roughness values for a conservative highlight approximation, rather than a true metallic/roughness PBR shader.

This is useful for interactive repair-training visualization. It should not be described as full physically based rendering or as proof that the generated fastener geometry is dimensionally accurate to the physical truck.
