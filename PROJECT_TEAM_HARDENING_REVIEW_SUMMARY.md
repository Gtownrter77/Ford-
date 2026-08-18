# Project-Team Hardening Review Summary

**Project:** 2004 Ford Explorer Sport Trac Android training app  
**Review scope:** Latest startup, renderer, source-integrity, and regression-hardening pass  
**Current artifact:** `ford-sport-trac-projected-center-hardening-debug.apk`  
**Artifact SHA-256:** `e61a2eb5d10d4870eac9d82dd58e6c9b356e2ca5ac3d944a41747116becf3cae`

## Executive summary

The latest pass made one conservative renderer change on top of the previously verified startup, material, and A/C routing fixes. The procedural Compose Canvas renderer no longer writes freshly projected component centers into Compose snapshot state on every draw. It retains the latest centers in a remembered mutable holder for tap hit-testing, reducing an avoidable recomposition trigger while preserving the existing selection path.

The current source also includes the earlier startup mitigation that moves automatic ViewModel initialization to `Dispatchers.IO`, the material-aware fastener highlight utility, and the corrected unique A/C pressure-control identifier. The full sandbox build and unit-test task completed successfully with **14 tests and 0 failures**. The packaged APK passed v2/v3 signature verification and ZIP integrity verification. [1]

> **Important boundary:** This is sandbox-built and package-verified. It has not been verified on the reported physical device. The phone ANR or closing behavior must remain classified as unresolved pending a real-device run and logcat or equivalent trace.

## Change summary

| Change | Why it was made | Status and evidence |
|---|---|---|
| ViewModel startup coroutine moved to `Dispatchers.IO` | Reduce the risk that maintenance, acoustic, and offline-cache initialization begins on the UI dispatcher. | Source-verified mitigation; not proof of phone ANR resolution. |
| Forced Canvas offscreen compositing changed to automatic strategy | Avoid an unnecessary full-viewport compositing buffer because the audited Canvas path did not require blend-mode or save-layer behavior. | Source-verified renderer hardening; device performance remains unverified. |
| Material-aware fastener highlights | Make authored metallic and roughness values affect the generated subassembly highlight response. | Source-verified conservative Canvas approximation; not true GPU PBR. |
| Duplicate A/C identifier corrected | Separate the actual compressor record from the compressor/pressure-control assembly. | `ac_compressor` remains the compressor; `ac_compressor_pressure_controls` is unique and regression-tested. |
| Projected-center state write removed from draw path | Avoid a newly allocated snapshot-state update on every Canvas draw while retaining current tap-hit-test centers. | Source-verified in `Interactive3DViewport.kt`; build and tests passed. |
| Regression coverage added | Protect material response and component-ID integrity with deterministic unit tests. | 14 tests passed, 0 failures. |

## Evidence matrix

| Claim | Evidence source | Truth classification |
|---|---|---|
| The Android source is present and reproducible in the sandbox. | `/home/ubuntu/ford-sport-trac/`; Gradle build output. | Sandbox-verified. |
| The procedural model uses a Compose Canvas renderer. | `Interactive3DViewport.kt`; `Component3DModel.kt`. | Source-verified. |
| The default tab is `VIEW_3D`. | `ExplorerViewModel.kt`. | Source-verified. |
| The model registry contains 56 component records. | Bounded scan of `SportTracData.kt`. | Source-verified count, not complete-vehicle coverage. |
| The latest build passes. | `:app:assembleDebug :app:testDebugUnitTest`. | Sandbox-verified. |
| The latest unit-test result is 14 passed and 0 failed. | Gradle test task output and `ProceduralModelRegressionTest.kt`. | Sandbox-verified. |
| The APK is structurally valid and signed. | `apksigner verify --verbose`; `unzip -tq`. | Package-verified. |
| The projected-center hardening APK is the latest artifact. | `/home/ubuntu/deliverables/ford-sport-trac-projected-center-hardening-debug.apk`. | Artifact-verified. |
| The phone ANR is fixed. | No device run, `adb`, emulator, or logcat is available in the sandbox. | **Not verified; do not claim fixed.** |
| The app contains a completed every-fastener Blender/GLTF model. | No bundled `.glb`, `.gltf`, `.obj`, `.fbx`, or `.blend` asset was established in the audited source tree. | **Not verified; do not claim complete.** |
| The active fastener renderer is true PBR. | Canvas approximation uses material-aware highlight logic, not a verified GPU BRDF pipeline. | **Not true PBR; do not claim it.** |

## Recommended next test

The next responsible test is a physical-device installation of the current APK followed by a cold launch into `VIEW_3D`. The team should record whether the first frame appears, whether camera drag and component selection respond, and whether the process closes or becomes unresponsive. If the failure persists, capture Android logcat around process start and the first frame. Further source-only guesses should wait until that evidence exists.

## Files for review

| File | Purpose |
|---|---|
| `ford-sport-trac-projected-center-hardening-debug.apk` | Latest package for the next physical-device test. |
| `FINAL_EVIDENCE_LEDGER_2026-08-18.md` | Detailed source/build/package evidence and limitations. |
| `PROJECT_HANDOFF.md` | Master project status and artifact record. |
| `PROJECT_TEAM_HARDENING_PRESENTATION_SCRIPT.md` | Spoken presentation script for the team. |
| `Interactive3DViewport.kt` | Active procedural Canvas renderer. |
| `MaterialResponse.kt` | Tested material-aware highlight utility. |
| `ProceduralModelRegressionTest.kt` | Regression coverage for material response and ID integrity. |

## References

[1]: ./FINAL_EVIDENCE_LEDGER_2026-08-18.md "Ford Sport Trac Android App — Final Evidence Ledger"
[2]: ./PROJECT_HANDOFF.md "Ford Sport Trac Android Project Handoff"
[3]: ./app/src/main/java/com/example/ui/components/Interactive3DViewport.kt "Procedural Compose Canvas renderer"
[4]: ./app/src/main/java/com/example/util/MaterialResponse.kt "Material-aware Canvas highlight utility"
[5]: ./app/src/test/java/com/example/model/ProceduralModelRegressionTest.kt "Procedural model regression tests"
