# Independent Audit Working Record — 2026-08-18

This is an interim evidence record, not a final device-verification report.

## Scope and environment

The authoritative Android project is `/home/ubuntu/ford-sport-trac`. The separate WebDev project at `/home/ubuntu/ry-contractor-workflow-lab` is unrelated to the Sport Trac Android gift app and is excluded from this audit.

The sandbox has no `adb` command and no Android emulator binary, so physical-device runtime behavior cannot be reproduced here. Any statement about Ry’s phone remains user-reported unless supported by a device log.

## Reproduced build evidence

The checkout initially lacked `local.properties`, so Gradle could not locate the installed Android SDK. A local-only `local.properties` pointing to `/home/ubuntu/android-sdk` restored the environment. Using `/home/ubuntu/tools/gradle-9.3.1/bin/gradle`, both `:app:assembleDebug` and `:app:testDebugUnitTest` completed successfully on 2026-08-18. The resulting APK is `app/build/outputs/apk/debug/app-debug.apk` and the package name is `com.aistudio.fordexplorer2004.trac3d`.

This proves source compilation, unit-test task completion, and APK generation in the sandbox. It does **not** prove launch success on a physical phone.

## Startup findings

`MainActivity` obtains `ExplorerViewModel` with the Android ViewModel delegate. `ExplorerViewModel` constructs `AppDatabase`, `MaintenanceRepository`, `AcousticDiagnosticRepository`, `RepairChecklistRepository`, and `OfflineCacheRepository` as properties.

`AppDatabase` uses `Room.databaseBuilder(...).fallbackToDestructiveMigration().build()`. There is no `allowMainThreadQueries()` and no Room callback that seeds the database synchronously.

`OfflineCacheRepository.seedAndSyncOfflineCache()` is wrapped in `withContext(Dispatchers.IO)`, but `ExplorerViewModel` also calls it from its `init` coroutine after `initializeDefaultDataIfEmpty()` and `seedInitialDatabaseIfEmpty()`. The original init coroutine used the default `viewModelScope` dispatcher, so list construction and orchestration in those seed methods could begin on the main thread even though the offline repository’s inner body switches to IO. This is a credible startup responsiveness risk. The applied fix changes the init launch to `viewModelScope.launch(Dispatchers.IO)`, moving the complete startup initialization sequence off the main thread.

The default tab is explicitly `MainTab.VIEW_3D`, not Lounge or Part Store.

## First-frame 3D findings

`_selectedComponent` initializes from `SportTracData.components.firstOrNull()`. `filteredComponents` initializes from `SportTracData.components`. The registry contains 56 `Component3DModel` entries according to a bounded source scan.

`MainActivity` passes `filteredComponents` to `Model3DScreen`, which passes the complete list to `Interactive3DViewport`. The Canvas renderer iterates visible components, projects each component’s vertices, iterates each component’s faces, and builds projected-face data during drawing. The renderer also uses an offscreen compositing layer. This is a credible first-frame/per-frame performance risk on a physical device, but it is not yet a proven ANR cause because no device trace or frame timing is available.

The implementation is a procedural Compose Canvas renderer. The inspected files do not establish that the live default viewport is a Blender-imported or photorealistic static asset. A separate SceneView/GLTF path exists in source, but source presence alone is not device verification.

## Part Store findings

`PartsShoppingScreen` uses remembered local preferences, static readiness packages, and remembered sorting for cart items. The inspected top-level code does not show a network scrape or automatic order submission. The screen explicitly displays a boundary that the app does not submit orders, collect payment, or charge an account. A full composition performance audit remains pending.

## Current conclusion

The source-backed launch risks are the automatic initialization sequence and the full procedural 3D first frame with per-frame face projection. The minimal applied mitigation moves automatic initialization to `Dispatchers.IO` without removing content or changing navigation. The rebuilt APK and tests pass in the sandbox. No APK should be called device-verified without an actual Android device or `adb`/logcat evidence.
