# Blender-level graphics contract

The product wraps around a 3D truck. Phone-quality graphics at Blender level
are **Filament / SceneView rendering a licensed GLB**, not the Compose Canvas
procedural mesh.

## What the app can render today

| Path | Quality | Status |
|---|---|
| Compose Canvas procedural parts | Training schematic | Live behind the safe-scene gate |
| SceneView / Filament GLB | PBR, IBL, real materials | Code present; licensed Sport Trac wreck GLB is **not packaged** |
| Blender `.blend` opened in-app | Not an Android runtime format | Export GLB first |

Canvas is the ANR-safe practice layer. It will never look like a Blender Cycles
hero shot. Do not describe it as Blender-level.

## Required asset to get Blender-level look

1. License a 2004 Explorer Sport Trac wreck or high-detail truck GLB.
2. In Blender: Principled BSDF only, `metallic` / `roughness` maps, no
   Cycles-only nodes.
3. Export glTF 2.0 Binary (`.glb`), Y-up, +Z forward, applied scale.
4. Keep these node names so Mentor and parts_data.json can bind:

```
front_bumper
front_left_wheel
driver_front_door
engine_assembly
rear_bumper
```

5. Place the file at:

```
app/src/main/assets/models/ford_explorer_sport_trac_2004_wreck.glb
```

6. Rebuild. `InteractiveRepairViewer` already refuses to fake a truck if that
   file is missing.

## Mentor binding

Selecting a GLB node must open the Mentor dock for the matching
`Component3DModel` / `parts_data.json` part. The Mentor speaks packaged Sport
Trac knowledge (torque, TSB-style failure patterns, tools). It does not invent
OEM data that is not in the catalog.
