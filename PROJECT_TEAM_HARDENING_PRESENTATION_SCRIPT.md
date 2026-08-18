# Project-Team Hardening Pass
## Presentation Script

**Audience:** Ford Sport Trac Android project team  
**Estimated speaking time:** 5–7 minutes  
**Purpose:** Explain the latest hardening changes, why they were made, what passed, and what remains unverified.

> **Presenter note:** This script is deliberately precise. The current artifact is sandbox-built and package-verified; it has not been verified on the reported physical device.

## Opening

“Today I’m going to summarize the latest hardening pass on the Ford Explorer Sport Trac Android training app. This was not a feature-expansion pass. It was a reliability and evidence pass focused on startup work, procedural Canvas rendering, material-aware fastener shading, component routing, and regression coverage.

“The central principle was simple: we should improve the code where the source gives us a defensible reason, and we should not describe a phone issue as fixed until the app has been run on the actual device with usable Android diagnostics.” [1]

## 1. What problem were we addressing?

“The project’s default launch path opens the `VIEW_3D` experience. That screen uses a procedural Compose Canvas renderer. It projects component vertices and faces into 2D, sorts faces by depth, applies hand-written lighting, and supports camera interaction and tap-based component selection.

“The renderer is substantial, but it is not a finished imported Blender or GLTF vehicle asset. The source registry contains 56 component records, with generated reference hardware and subassembly structures. That is useful training-model infrastructure, but it is not proof that every physical bolt, washer, trim variation, or vehicle configuration has been represented.” [1]

## 2. Startup hardening: moving initialization off the UI path

“The first hardening change addressed automatic startup initialization. `ExplorerViewModel` launches maintenance initialization, acoustic-database seeding, and offline-cache seeding from its `init` block. The original coroutine used the default `viewModelScope` dispatcher. Even though the database layer uses Room’s normal builder and does not enable main-thread queries, orchestration and list-construction work could still begin on the main thread.

“The minimal change was to launch that initialization sequence explicitly on `Dispatchers.IO`. We did not remove the model, change navigation, submit orders, or change the privacy boundary. This is a source-backed mitigation for a credible startup responsiveness risk, not proof that the phone ANR is resolved.” [1]

## 3. Renderer hardening: reducing unnecessary recomposition

“The latest hardening change addressed a specific Compose interaction. During each Canvas draw, the renderer calculated projected component centers for tap hit-testing and wrote a newly allocated list into Compose snapshot state. Because Canvas drawing can already be triggered by camera, animation, or other invalidations, that state write could cause avoidable recompositions.

“We changed the implementation so the latest projected centers are stored in a remembered mutable holder rather than snapshot state. The tap handler still receives current centers, and the existing pointer-input keys continue to recreate the gesture handler when the visible component set or camera state changes. The intended selection behavior is preserved while removing the per-draw snapshot-state write.” [1]

## 4. Renderer hardening: material-aware fastener highlights

“The generated fastener and subassembly records already carried metallic and roughness values, but the active Canvas branch did not originally consume those fields. We extracted the response calculation into a small deterministic `MaterialResponse` utility and wired the renderer to use it.

“In the current approximation, metallic factor increases highlight strength and roughness changes the response curve so the highlight is broader rather than behaving like a sharp polished reflection. This improves consistency between authored material metadata and visible behavior.

“We need to use accurate language here. This is material-aware Canvas shading; it is not a true GPU PBR pipeline. There is no verified BRDF implementation, image-based lighting, normal mapping, or physically accurate material calibration in this path.” [1]

## 5. Source correctness: the A/C identifier collision

“The audit also found a concrete source defect in the A/C data. Two distinct records shared the identifier `ac_compressor`: one represented the compressor, while the other represented a combined compressor and pressure-control assembly.

“We kept `ac_compressor` on the actual compressor record and assigned the second record the unique identifier `ac_compressor_pressure_controls`. Existing compressor service and diagnostic routes remain pointed at the real compressor record. A regression test now verifies that the component registry has unique IDs and that the pressure-control record retains its corrected identifier.” [1]

## 6. Regression coverage and verification

“The hardening work is now covered by deterministic source-level tests. The expanded test suite checks that away-facing surfaces produce no highlight, higher metallic values produce a stronger response, roughness broadens the response at the selected reflection sample, component IDs are unique, and the corrected A/C pressure-control record uses the expected ID.

“The final build command was `:app:assembleDebug :app:testDebugUnitTest`. It completed successfully with 14 tests and zero failures. The packaged APK passed APK signature v2 and v3 verification, and ZIP integrity verification reported no compressed-data errors.

“The current projected-center hardening APK is `ford-sport-trac-projected-center-hardening-debug.apk`. Its SHA-256 is:

```text
e61a2eb5d10d4870eac9d82dd58e6c9b356e2ca5ac3d944a41747116becf3cae
```

“The complete recovery archive was also rebuilt and ZIP-verified. The source, tests, handoff, evidence ledger, and APK are retained together so the team can recover the exact state of this pass.” [1]

## 7. What this pass does not prove

“There are several limits we need to state clearly.

“First, the phone-reported ANR or closing behavior remains unverified because this environment did not have a usable `adb` connection or emulator. We therefore cannot call the phone issue solved.

“Second, the model remains a procedural Compose Canvas training model with generated reference hardware. It is not a completed every-fastener, dimensionally calibrated Blender or GLTF replica.

“Third, the material change is a conservative highlight approximation, not true GPU PBR.

“Finally, the project contains source paths for Mentor guidance, A/C training, audio analysis, and Part Store planning, but those should not be described as a complete conversational parts travel agent, universal live retailer scraper, automatic ordering system, or fully verified real-world diagnostic service.” [1]

## Closing

“The result of this pass is not a promise that every runtime problem is gone. The result is a more defensible build: startup initialization is explicitly off the UI dispatcher, the Canvas avoids one unnecessary per-draw snapshot-state write, material metadata now affects the generated fastener highlight approximation, the A/C identifier collision is corrected, and the source-level regression suite passes.

“The next responsible step is a physical-device test using the new APK. If the phone still hangs or closes, the next evidence we need is logcat or a reproducible device trace. At that point we should diagnose the observed failure rather than continue making unsupported source guesses.”

## Suggested team discussion questions

| Topic | Discussion prompt |
|---|---|
| Device verification | Who can run the current APK on the physical device and capture the first-launch result plus logcat? |
| Renderer behavior | Does camera drag, component selection, and the initial `VIEW_3D` frame behave acceptably on the target hardware? |
| Model scope | Which vehicle subsystem should receive the next detailed geometry pass, and what dimensions or reference material will support it? |
| Training accuracy | Which A/C repair sequence should be reviewed by a qualified technician before being treated as instructional authority? |
| Evidence discipline | Which claims should remain labeled “source-verified,” “sandbox-verified,” “partially implemented,” or “device-pending”? |

## References

[1]: ./FINAL_EVIDENCE_LEDGER_2026-08-18.md "Ford Sport Trac Android App — Final Evidence Ledger"
[2]: ./PROJECT_HANDOFF.md "Ford Sport Trac Android Project Handoff"
[3]: ./app/src/main/java/com/example/ui/components/Interactive3DViewport.kt "Procedural Compose Canvas renderer"
[4]: ./app/src/main/java/com/example/util/MaterialResponse.kt "Material-aware Canvas highlight utility"
[5]: ./app/src/test/java/com/example/model/ProceduralModelRegressionTest.kt "Procedural model regression tests"
