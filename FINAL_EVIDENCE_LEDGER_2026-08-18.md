# Ford Sport Trac Android App — Final Evidence Ledger

**Recipient:** Ry  
**Date:** 2026-08-18  
**Reviewer:** Independent AI source-and-build audit; not a human supervisor and not the specifically described fictionalized reviewer.

## Executive finding

The Android project is present locally at `/home/ubuntu/ford-sport-trac`. The source compiles, the debug unit-test task completes, the post-fix APK is structurally valid and APK-signature verified, and a minimal startup responsiveness mitigation has been applied. The mitigation moves the automatic ViewModel initialization sequence to `Dispatchers.IO`.

The app is **not physically device-verified** in this environment. The sandbox has no usable `adb` command and no emulator binary. Therefore, the reported phone ANR/closing behavior remains a user-reported runtime issue, not a resolved fact. The new APK should be described as **sandbox-built and package-verified, awaiting real-device verification**, not “ready” or “fixed on the phone.”

## Evidence table

| Area | Direct evidence | Truthful status |
|---|---|---|
| Authoritative source | `/home/ubuntu/ford-sport-trac/` contains the Android project, Kotlin/Compose source, Gradle configuration, tests, handoff, and research records. | Present locally. |
| Default launch tab | `ExplorerViewModel.kt` initializes `_currentTab` as `MainTab.VIEW_3D`. | Source-verified. |
| Procedural model | `Interactive3DViewport.kt` contains a Compose `Canvas` renderer that projects vertices and faces from `Component3DModel` values. | Source-verified procedural renderer. |
| Model registry | `SportTracData.kt` contains 56 `Component3DModel` entries by bounded source scan. | Source-verified count; not a claim of complete real-vehicle coverage. |
| Blender/GLTF language | A separate SceneView/GLTF path and Blender-labeled controls exist in source. | Source presence only; not proof of imported Blender assets or device behavior. |
| Mentor | Mentor-related dialogs and repair-step code exist for selected components. | Partially implemented; not yet a complete conversational travel-agent workflow. |
| A/C | A/C model data, workbench, acoustic references, and repair guidance exist. | Source-verified training content; not proof of a real refrigerant diagnosis or repair. |
| Audio diagnosis | `AudioAnalysisPipeline.kt` and acoustic repository files exist. | Source-verified code path; no device microphone session or sound-classification accuracy was verified. |
| Part Store | Static catalog, ranking logic, readiness controls, and explicit no-auto-order language exist. | Source-verified local planning UI; no live universal retailer scraping was verified. |
| Privacy boundary | The Part Store text states that it does not submit orders, collect payment, or charge an account. | Source-verified stated boundary. |
| Automatic startup initialization | `ExplorerViewModel.init` calls maintenance initialization, acoustic seeding, and offline-cache seeding. | Source-verified. |
| Original startup risk | The original init coroutine used the default `viewModelScope` dispatcher. Some initialization/orchestration could begin on the main thread. | Credible source-backed ANR risk, not device-proven. |
| Applied mitigation | The init coroutine now uses `viewModelScope.launch(Dispatchers.IO)`. | Source-verified change. |
| Room configuration | `AppDatabase` uses Room’s normal builder and does not enable `allowMainThreadQueries()`. | Source-verified. |
| Device verification | No `adb` command or emulator was available in the sandbox. | Not performed. |

## Build and package verification

The first rebuild attempt failed only because the restored checkout lacked `local.properties` pointing to the installed SDK. A local-only file was added with `sdk.dir=/home/ubuntu/android-sdk`; this does not change app behavior or belong in the recovery archive.

Using Gradle 9.3.1 from `/home/ubuntu/tools/gradle-9.3.1/bin/gradle`, the following tasks completed successfully after the mitigation:

```text
:app:assembleDebug
:app:testDebugUnitTest
BUILD SUCCESSFUL in 1m 14s
49 actionable tasks: 9 executed, 40 up-to-date
```

The build output APK is:

```text
/home/ubuntu/ford-sport-trac/app/build/outputs/apk/debug/app-debug.apk
SHA-256: ff38e34d80b05305749093726301d1ed417959a7d84ffd046b33c9ce42dfb97a
```

The prior user-facing post-fix APK was:

```text
/home/ubuntu/deliverables/ford-sport-trac-io-startup-fix-debug.apk
SHA-256: 8f6186f5ab37ce0ceefac241e66adc78dd17f87619c8061292a73f9c5e68f055
```

The current renderer-hardening APK was:

```text
/home/ubuntu/deliverables/ford-sport-trac-render-hardening-debug.apk
SHA-256: 255e17917d17ef4ddcf69d27a2b51a94bc98e31f2be5431c07703fba475aa506
```

The previous A/C identifier-fix APK was:

```text
/home/ubuntu/deliverables/ford-sport-trac-ac-id-fix-debug.apk
SHA-256: dff762949f83688074ed605609e90a27c2daacef38f6442335b35c3cfe7c0454
```

The current material-aware APK is:

```text
/home/ubuntu/deliverables/ford-sport-trac-material-aware-debug.apk
SHA-256: b33bac82ada3c758bdd60e958dd71347448fd64660ca16fa766288f9be0adc33
```

It passed APK signature v2/v3 verification and `unzip -tq` integrity verification. The active subassembly Canvas branch now consumes `metallicFactor` and `roughnessFactor` to shape a conservative specular-like highlight response. This is still not a true GPU PBR shader, and no physical-device run is verified.

`apksigner verify --verbose` reported valid v2 and v3 signatures with no errors, and `unzip -tq` reported no compressed-data errors. The package metadata identifies application ID `com.aistudio.fordexplorer2004.trac3d`, version code `1`, version name `1.0`, minimum SDK `24`, target SDK `36`, and compile SDK `36`.

## Recovery archive

The recovery archive is being refreshed after this hardening pass so it includes both the prior startup-fix APK and the new renderer-hardening APK.

The complete recovery archive is:

```text
/home/ubuntu/deliverables/ford-sport-trac-recovery-2026-08-18.zip
The final archive SHA-256 and byte size are recorded with the delivered attachment because embedding the archive’s own checksum or size would be self-referential.
```

The archive passed `unzip -tq`. It includes the Android source tree, project documentation, the final handoff, the final evidence ledger, the recovery checklist, prior research records, the post-fix APK, earlier APK checkpoints, and the A/C checkpoint archive. Build caches and generated build directories were excluded to keep the archive recoverable without carrying unnecessary artifacts.

## Remaining limits

The IO-dispatch change is a reasonable minimal mitigation for a main-thread startup risk, the automatic compositing change is a conservative renderer-hardening measure, the duplicate A/C ID correction removes a concrete source-routing defect, and the material-aware change wires stored metallic/roughness values into the active Canvas highlight approximation. None can be represented as a proven cure for the phone’s runtime behavior, and the material-aware change must not be described as true GPU PBR, without a real-device run. The full procedural 3D renderer remains a separate performance risk because the default screen receives the complete component registry and projects faces during Canvas drawing. If the phone still shows an ANR, the next evidence required is Android logcat or a reproducible device trace; further source guesses would not meet the project’s truthfulness standard.

The project is not a complete universal repair database. The Mentor is not yet a complete conversational parts travel agent. Live price scraping, live commercial pricing, universal retailer coverage, automatic ordering, and affiliate revenue are not verified implementations. The app does not currently establish that every bolt, washer, trim variant, or repair path for every 2004 Explorer Sport Trac configuration is represented.

## Files that must remain together

The authoritative source is `/home/ubuntu/ford-sport-trac/`. The primary APK for the next physical test is `/home/ubuntu/deliverables/ford-sport-trac-material-aware-debug.apk`; prior hardening APKs remain available for A/B comparison. The recovery archive and this ledger should be retained together. The separate WebDev prototype at `/home/ubuntu/ry-contractor-workflow-lab` is not part of this Android gift app.

## Regression-covered material revision

The material-aware highlight calculation is now isolated in `app/src/main/java/com/example/util/MaterialResponse.kt` so the active Canvas renderer and unit tests share the same deterministic function. The renderer passes each generated subassembly's `metallicFactor` and `roughnessFactor` into that utility.

A new `ProceduralModelRegressionTest` covers four source-level behaviors: away-facing surfaces produce no specular response, higher metallic values produce a stronger response, roughness broadens the response at a moderate reflection sample, the 56-record component registry has unique IDs, and the corrected A/C pressure-control record uses `ac_compressor_pressure_controls`. The expanded `:app:assembleDebug :app:testDebugUnitTest` task completed successfully with 14 tests and 0 failures. An initial test assumption about roughness was corrected after the first run exposed the actual response curve; the final run passed.

The regression-covered APK is:

```text
/home/ubuntu/deliverables/ford-sport-trac-regression-covered-debug.apk
SHA-256: 885c5da5b6890b922be978c0daab994c789fcdb92a554b3af8c1d707c36df0f8
```

`apksigner verify --verbose` reported valid v2 and v3 signatures, and `unzip -tq` reported no compressed-data errors. This remains sandbox evidence only. It does not establish successful physical-device launch, frame rate, or ANR resolution.

## Projected-center recomposition hardening

The active Canvas previously stored freshly computed projected component centers in Compose snapshot state on every draw. That write could trigger avoidable recompositions while the Canvas was already invalidating for camera or animation changes. The revised renderer keeps the latest centers in a remembered mutable holder for tap hit-testing; existing pointer-input keys still recreate the gesture handler when the visible component set or camera state changes. This preserves selection behavior while removing the per-draw snapshot-state write.

The revised `:app:assembleDebug :app:testDebugUnitTest` task completed successfully. The existing expanded regression suite remains at 14 tests with 0 failures.

The projected-center hardening APK is:

```text
/home/ubuntu/deliverables/ford-sport-trac-projected-center-hardening-debug.apk
SHA-256: e61a2eb5d10d4870eac9d82dd58e6c9b356e2ca5ac3d944a41747116becf3cae
```

`apksigner verify --verbose` reported valid v2 and v3 signatures, and `unzip -tq` reported no compressed-data errors. This is a source/build/package improvement only; physical-device launch, frame rate, and ANR behavior remain unverified.

## Physical-device verification phase

The exact procedure for verifying the latest APK is documented in `PHYSICAL_DEVICE_VERIFICATION_PROTOCOL.md`. The protocol targets `/home/ubuntu/deliverables/ford-sport-trac-projected-center-hardening-debug.apk` with SHA-256 `e61a2eb5d10d4870eac9d82dd58e6c9b356e2ca5ac3d944a41747116becf3cae`. It requires checksum verification, `adb devices` confirmation, clean installation, cold-launch timing, first-frame observation, navigation and 3D interaction checks, A/C/Mentor/Part Store smoke checks, and logcat or bugreport capture for any ANR, crash, or repeated restart.

This protocol is a test plan, not test evidence. Until a tester performs it on the actual phone and returns the results, the latest APK remains sandbox-built and package-verified, while physical-device launch and ANR behavior remain unverified.

## A/C Workbench practice-first gate

The A/C Workbench practice sequence now requires an explicit session-level rehearsal acknowledgment for each step. The user can open the matching 3D assembly, then select `Mark 3D Step Rehearsed`. Until that acknowledgment is made, the confirmation/advance button remains disabled and is labeled `Mark 3D Rehearsal First`. Advancing to the next step resets the acknowledgment, so each practice step must be marked separately. Exiting practice clears the session acknowledgment.

This is a training-flow guard, not proof that the user physically performed the repair or that a real vehicle is safe to service. The A/C content continues to separate model rehearsal from real-vehicle transfer and keeps refrigerant recovery, evacuation, leak testing, and charging outside the casual practice flow.

The A/C practice-gate revision completed `:app:assembleDebug :app:testDebugUnitTest` successfully. The existing unit-test suite passed; the APK passed v2/v3 signature verification and ZIP integrity verification.

```text
/home/ubuntu/deliverables/ford-sport-trac-ac-practice-gate-debug.apk
SHA-256: 661046972dc18cf5b2d5b7974b66555e781b3c503757b49af6849149fd839a91
```

Physical-device launch and A/C interaction remain unverified until the device protocol is executed.

## Documentation-only reconciliation — 2026-08-18

This entry records documentation alignment only. No build, APK packaging, archive refresh, or source-code change was performed during this pass.

The current operating structure has four separately labeled workstreams: Primary/Mentor, Foundation, A/C Workbench, and Parts/Commerce. They are project workstreams, not persistent human agents. The Primary/Mentor stream contains the opt-in voice change. The Foundation stream contains the readiness contract and foundation catalog. The A/C stream contains the model-first rehearsal gate. The Parts/Commerce stream contains the evidence-aware `PartListingTruth` utility and tests.

The recorded Mentor opt-in artifact remains the latest separately packaged artifact with documented v2/v3 signature and ZIP integrity verification before the build hold. A combined build was started after Foundation and Parts/Commerce source additions, but its final result is not certified by this documentation-only pass. No new checksum is created here.

The real-device verification gap remains open. No phone install result, launch timing, ANR result, logcat, bugreport, gfxinfo, thermal, battery, or frame-rate evidence is present in this session. No documentation change should be read as device verification.
