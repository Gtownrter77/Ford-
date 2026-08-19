# Safe Shell Route-Isolation Evidence — Ford Sport Trac

## Purpose

This artifact is a diagnostic containment build for the reported physical-device open–crash cycle. It is not a production build and does not claim that the individual functions are repaired.

## What is enabled

The Lounge is the only active feature route. All other bottom-navigation destinations—3D Model, Repair Manual, Diagnostics, Service Schedule, and Part Store—now use the same explicit **under physical-device review** screen instead of constructing their original feature composables.

Voice settings, skill settings, microphone control, and other optional top-bar actions are disabled in this isolation artifact. The intent is to determine whether the crash loop survives when none of those function screens or actions is created.

## Build evidence

| Check | Result |
|---|---|
| Build command | `:app:assembleDebug :app:testDebugUnitTest --no-daemon --console=plain` |
| Gradle result | `BUILD SUCCESSFUL` |
| Unit tests | 19 total; 0 skipped; 0 failures; 0 errors |
| APK | `ford-sport-trac-safe-shell-route-isolation-debug.apk` |
| APK size | 57 MB |
| SHA-256 | `6f14cf335082511810e8f9458845df4491ee097a70bda0a529d31d90df3dfe0a` |

As in earlier local builds, a non-fatal KSP `AWT-EventQueue-0` warning appeared in the Gradle log. The build and all test tasks completed successfully. It is not evidence of a phone-side route crash.

## Exact phone test

1. Install this APK over the previous one, then force-stop the old app if Android still shows a crash dialog.
2. Open the app. The expected result is the Lounge. Wait 30 seconds without tapping anything.
3. Tap each bottom tab, one at a time: 3D Model, Manual, Diagnostics, Schedule, and Parts Cart.
4. Expected result for each: a card that states the function is under physical-device review and offers **Return to Lounge**.
5. Tap **Return to Lounge** after each route. The app must not crash, freeze, restart, or show an Android ANR dialog.
6. Do not tap the microphone, Voice Settings, or skill badge in this diagnostic build; they are intentionally disabled.

## Interpretation

If this safe-shell build still crashes before or during these placeholder route transitions, the failure is below the individual feature screens—likely Activity, Compose runtime, ViewModel, database initialization, device/installation state, or another shared application path. Capture logcat immediately.

If the safe-shell build remains stable, the next build will re-enable exactly one original route at a time, starting with the lowest-risk route, while leaving 3D disabled until later.

## Truth boundary

This APK is sandbox-built, unit-tested, and package-verified. It is not phone-verified until the above test is completed. Its function routes are intentionally unavailable, so it must not be described as feature-complete or release-ready.
