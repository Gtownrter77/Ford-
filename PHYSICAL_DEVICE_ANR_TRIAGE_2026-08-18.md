# Physical-Device ANR Triage — Sport Trac 3D

## Evidence reviewed

The supplied phone screenshots show a real Android **Application Not Responding** dialog: “Sport Trac 3D isn’t responding.” The device first displays the app’s 3D Model screen, with the cube/3D tab active, **`PARTS LAYER (56)`**, and the 4.0L selection; Android then presents the ANR dialog and, in another frame, the frozen system dialog over a blackened app surface.

This is not evidence of a normal application crash. The process stayed alive long enough for Android to detect that it was not returning to the main event loop. Installation is therefore verified, but the app is **not release-ready**.

## Confirmed visible state

| Observation | What it establishes |
|---|---|
| 3D model tab is active | The failing path is the Compose 3D viewport, not the Lounge or a background-only screen. |
| `PARTS LAYER (56)` is visible | The viewport has been asked to prepare/render the full 56-component vehicle catalog. |
| Ford Explorer header and 4.0L selection appear before the ANR | Initial Compose UI has begun rendering; this is not a failure before the activity draws its first screen. |
| Android shows “isn’t responding,” with Close app / Wait | The app’s UI thread has not serviced the system event loop for the Android ANR window. |
| Bottom navigation labels are heavily wrapped | There is also a narrow-width/font-scale layout defect. It makes the screen harder to use but does not, by itself, explain an ANR. |

## Most likely technical trigger

**Confidence: high for the category; medium for the exact call stack.**

The source opens directly into the 3D tab (`ExplorerViewModel.kt`, `MainTab.VIEW_3D`) with the **ALL** system filter and the first component selected. `Model3DScreen.kt` immediately composes `Interactive3DViewport` for that state. The renderer then synchronously processes every visible component on the Compose draw path.

For each component, `Interactive3DViewport.kt` transforms vertices, constructs a new `Path` for each face, computes face normals, diffuse/specular values, allocates `ProjectedFace` records, depth-sorts the complete face list, and draws filled and stroked paths. It then performs a second bloom pass across selected/high-specular faces. Hardware subassemblies repeat nearly the same projection, normal, path, and material math for bolts, washers, gaskets, belts, and other generated parts.

The hardware is not a small icon layer. `SubAssemblyMeshGenerator.kt` creates threaded bolt meshes with six hex faces, transition faces, ten threaded rings with ten segments each, and cap faces—roughly **114 faces per generated threaded bolt** before the main component faces and additional hardware are considered. On a physical phone, the full 56-part default scene can therefore cause a burst of synchronous allocations, CPU geometry work, sorting, draw recording, and bloom overdraw on the UI/render path. If that work monopolizes the main thread long enough, Android raises exactly the dialog shown in the screenshots.

> The precise blocking method and line are **not yet proven**. An ANR trace or logcat is required to distinguish renderer CPU saturation from a hidden lock, GC/memory stall, or a different main-thread call. The screenshots and source do, however, strongly localize the failure to the default full-scene 3D rendering route.

## Trigger chain from the app’s perspective

1. The activity starts with `VIEW_3D` selected and the `ALL` filter active.
2. `Model3DScreen` composes the interactive Canvas viewport immediately.
3. The viewport attempts to render all 56 components and their subassemblies, instead of a lightweight initial scene.
4. Per-face geometry, path allocation, normal/material calculations, z-sorting, filled/stroked drawing, and optional bloom execute synchronously for the frame.
5. Rendering falls behind badly enough that Android cannot get a timely response from the app’s main loop.
6. Android shows the ANR dialog. The partial screen behind it is the last frame that finished or partially finished drawing.

## What is *not* the primary cause

The startup database seeding was moved to `Dispatchers.IO` in `ExplorerViewModel.kt`; that is a positive hardening change, but it does not remove the initial full-scene Canvas draw. Likewise, `CompositingStrategy.Auto` and removal of one projected-center snapshot-state write reduce specific costs, but they do not change the fundamental amount of per-frame 3D work in the default 56-component scene.

## Fix-first priorities

| Priority | Required change | Why it matters |
|---|---|---|
| P0 | Do **not** launch directly into the full 56-part interactive viewport. Start in Lounge or a lightweight static/model-summary state, with an explicit **Load interactive 3D model** action. | Removes the failing workload from the launch path. |
| P0 | Default the interactive scene to one system or a limited low-detail set. | Prevents a full-catalog frame from being the first render. |
| P0 | Disable hardware subassemblies and bloom by default on phones; load them only for the selected component or explicit detail mode. | Cuts the most expensive face, allocation, and overdraw paths. |
| P1 | Add level-of-detail limits and frame budgeting: cache immutable mesh data/normals, reduce temporary list/path allocation, and cap visible faces before draw. | Prevents repeated CPU/GC pressure during interaction. |
| P1 | Provide a static procedural preview fallback if the interactive render misses a defined frame-time threshold. | Keeps the app responsive instead of forcing an ANR. |
| P1 | Redesign the six-item bottom navigation for the device width and current font scale. | Fixes the visible label-wrap defect and reduces UI crowding. |

## Required confirmation evidence

1. Connect the phone by USB and capture `adb logcat` during one reproducible freeze.
2. Capture the ANR trace / system event timing if the device permits it.
3. Re-test with the full scene, then with hardware/bloom disabled, then with a single system visible.
4. Record launch-to-first-frame and interaction latency after the P0 changes.

Until those steps pass on the actual phone, the correct release assessment is **FIX FIRST**.
