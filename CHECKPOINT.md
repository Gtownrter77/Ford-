# Ford Explorer Sport Trac 3D Model Checkpoint

**Checkpoint date:** 2026-08-17 EDT

This checkpoint contains the working local copy of the public repository `Gtownrter77/Ford-`, plus the changes made during this session. Nothing has been pushed to GitHub.

## Current implementation work

The project is an Android/Kotlin application with a Compose-based interactive 3D technical viewport. The changes in this checkpoint improve the service-hardware layer of the existing 2004 Ford Explorer Sport Trac model.

| File | Change |
| --- | --- |
| `app/src/main/java/com/example/util/SubAssemblyMeshGenerator.kt` | Replaced basic bolt/washer primitives with higher-detail threaded hex bolts, Torx screws, complete washer geometry, seals, belts, and spark-plug meshes. |
| `app/src/main/java/com/example/data/VehicleHardwareCatalog.kt` | Added a structured per-system service-hardware catalogue that creates individual renderable hardware instances and an explicit hardware inventory for every existing model assembly. |
| `app/src/main/java/com/example/data/SportTracData.kt` | Applies the new hardware catalogue to the complete component list. |
| `app/src/main/java/com/example/ui/components/Interactive3DViewport.kt` | Corrected component/subassembly coordinate handling so hardware is not offset twice in normal or exploded views. |
| `app/src/main/java/com/example/ui/components/ComponentDetailSheet.kt` | Shows displayed hardware-unit counts and a VIN/workshop-manual verification warning. |
| `app/src/main/java/com/example/ui/components/AcSystemWorkbenchDialog.kt` | Adds an A/C-specific diagnostic workbench, five-step practice-first repair rehearsal, heat and refrigerant safety boundaries, 3D component links, and a cost-aware Parts Finder handoff. |
| `app/src/main/java/com/example/ui/screens/DiagnosticsScreen.kt` | Adds a priority A/C Workbench entry point to guided diagnostics. |
| `app/src/main/java/com/example/data/SportTracData.kt` | Adds a separate A/C service-port/pressure-protection/clutch-command model assembly and removes unverified A/C charge, oil, vacuum, and clutch-gap values in favor of under-hood-label/VIN verification. |
| `app/src/main/java/com/example/service/AudioAnalysisPipeline.kt` | Adds A/C training patterns, preserves live recording as distinct from simulated audio, and recasts ranking as conservative reference-pattern similarity rather than fault certainty. |
| `app/src/main/java/com/example/data/AcousticDiagnosticRepository.kt` | Adds A/C compressor/pulley, clutch-cycling, and belt-squeal comparison profiles with required corroborating inspection steps. |
| `app/src/main/java/com/example/ui/components/AcousticSoundDiagnosticDialog.kt` | Adds a visible evidence boundary explaining live versus training audio and directing ranked matches to model-based inspection rather than automatic part replacement. |

## Scope and verification note

The current app has procedural reference geometry, not a scanned/OEM CAD dataset. The added hardware is therefore explicitly labelled as **service-scope visual reference data**. The A/C practice flow is designed for rehearsal of component order, access, tools, warnings, and decision points before working on the truck; exact dimensions, clearances, fastener counts, part numbers, coatings, torque values, refrigerant charge, oil balance, and installation sequences must be verified against the vehicle VIN, under-hood label, and Ford workshop/parts documentation before physical repair or restoration work.

The sound-comparison system records live microphone audio and ranks spectral similarity against the app’s local reference profiles. It is an inspection aid, not a diagnosis, and its simulated patterns are labeled as training content. Refrigerant recovery, evacuation, leak testing, and charging remain professional-equipment tasks.

## Validation status

`git diff --check` passed with no whitespace errors. The Android project was compiled successfully with `:app:compileDebugKotlin` and packaged successfully with `:app:assembleDebug`.

The repository does not include a Gradle wrapper. Validation used **Gradle 9.3.1**, which satisfies Android Gradle Plugin `9.1.1`, Java 21, and Android SDK Platform 36.1. The generated debug APK is `app/build/outputs/apk/debug/app-debug.apk` (approximately 57 MB). A local standard debug keystore was generated at `debug.keystore` because the repository signing configuration expects that file and does not include it.

The build emitted only pre-existing deprecation warnings and a non-blocking missing-`google-services.json` warning. The project still assembled successfully as a debug APK.

## Recovering the work

The archive includes the application source, the locally generated standard debug keystore needed by this repository's debug signing configuration, the assembled debug APK, and this checkpoint note. The accompanying patch can be applied to a clean clone of the same repository:

```bash
git apply ford-sport-trac-hardware-checkpoint.patch
```

Then open the project in Android Studio or use a Gradle 9.3.1+ wrapper with a configured Android SDK.
