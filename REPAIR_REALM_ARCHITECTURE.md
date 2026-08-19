# Interactive Repair Realm Architecture

## Purpose

The Interactive Repair Realm is a **SceneView/Filament inspection experience**, not a replacement for the existing procedural Compose Canvas training renderer. It is activated only from an explicit 3D route after the safe-shell gate is lifted. The Lounge cold launch must never construct the renderer, load a GLB, initialize text-to-speech, parse the parts catalog, or resolve the ViewModel.

## Portable asset boundary

| Concern | Contract |
|---|---|
| Vehicle model | A licensed GLB at `assets/models/ford_explorer_sport_trac_2004_wreck.glb` |
| Vehicle identity | `vehicleId` in `parts_data.json`; it can be replaced for a different vehicle or engine |
| Inspectable part | `partId` and `nodeNames` map GLB node names to repair metadata |
| Camera target | `focus` and `overview` values in JSON describe the camera pose and look target |
| Inspection card | Compose overlay reads the selected `RepairPart` only; it knows nothing about Filament nodes |
| Raycast | SceneView hit node name resolves to a `RepairPart` via `nodeNames` |
| Mentor audio | `MentorAudioPlayer` is created only when the listener taps the explicit audio control |
| VR | A local `VrModeState` is a rendering-ready UX contract only; Android Cardboard/OpenXR stereo output requires a dedicated XR backend and device validation |

## Safety and current truth

The repository does **not** currently contain a licensed 2004 Ford Explorer Sport Trac wreck GLB. The first realm build therefore presents an explicit asset-required state when the protected 3D route is later enabled. It must not substitute a different vehicle model or claim that an external GLB is already integrated.

The expected GLB node names in the sample catalog are `front_bumper`, `front_left_wheel`, `driver_front_door`, `engine_assembly`, and `rear_bumper`. Imported assets must either retain these node names or update `parts_data.json` to match their actual scene hierarchy.

## Runtime sequence

1. The user explicitly enters the future 3D repair-realm route after safe-shell release.
2. `InteractiveRepairViewer` validates the GLB presence and parses `parts_data.json` on a background dispatcher.
3. SceneView/Filament loads the GLB on demand; no model work occurs at cold launch.
4. A tap raycasts from the SceneView camera into the GLB node graph. A matched node selects its `RepairPart`.
5. The camera animates toward the metadata focus pose, then the repair card presents correct part metadata and repair steps.
6. Mentor guidance is opt-in. Reset returns the camera to the vehicle overview pose.

## Verification boundary

Build success can verify source integration only. The renderer, GLB compatibility, part-node naming, tap raycasts, camera animation, audio playback, and future VR behavior require physical-device testing after a real licensed model is packaged.
