# ANR Repair Build Evidence — Ford Sport Trac 3D

## Repair scope

The repaired source no longer starts the app on the full procedural 3D route. The ViewModel starts at the Lounge. Entering the 3D tab presents an explicit safe-scene gate rather than immediately composing the full 56-component scene.

After the user chooses **Load safe interactive scene**, the renderer uses a bounded component set: one selected component for the ALL filter, or up to eight components for a selected system. Hardware subassemblies and bloom remain off by default. Hardware detail is an explicit follow-up action, and loading it does not automatically re-enable bloom.

## Build evidence

| Check | Result |
|---|---|
| Command | `:app:assembleDebug :app:testDebugUnitTest --no-daemon --console=plain` |
| Gradle result | `BUILD SUCCESSFUL` |
| Build duration | 1 minute 16 seconds |
| Unit tests | 19 total; 0 skipped; 0 failures; 0 errors |
| Rebuilt debug APK | `ford-sport-trac-safe-scene-anr-repair-debug.apk` |
| APK size | 57 MB |
| SHA-256 | `0efe297a00aa169c9d2ef3466bc1963ede8093ede6f1bc1f945415e26abc644d` |
| Archive check | Contains `AndroidManifest.xml` and `classes.dex` |

## Build-log note

During the successful Gradle run, KSP emitted an `AWT-EventQueue-0` `NullPointerException` after compilation began. Gradle continued, assembled the APK, ran all listed unit tests, and ended `BUILD SUCCESSFUL`. This warning did not fail the build, but it should remain recorded rather than being treated as invisible.

## Required phone re-test

1. Install this APK over the previous debug build, then use **Close app** if the old ANR dialog is still present.
2. Launch the app. Expected initial route: **Lounge**, not the 3D model.
3. Tap the **3D Model** navigation tab. Expected result: the safe-scene gate; no immediate 56-part Canvas render.
4. Wait at least 15 seconds on the gate and confirm no ANR.
5. Tap **Load safe interactive scene**. Expected initial content: a bounded scene, hardware detail off, bloom off.
6. Rotate/tap the safe scene for 30 seconds. Record ANR/crash/lag behavior.
7. Optionally tap **Load hardware detail** and repeat the observation. This is the stress step; it must be recorded separately from safe-scene behavior.
8. Capture logcat if an ANR reappears. A successful sandbox build does not verify real-phone performance.

## Truth boundary

This artifact is **source-present, sandbox-built, unit-tested, and package-verified**. It is not phone-verified and is not release-ready until the re-test above is completed on the actual Android device.
