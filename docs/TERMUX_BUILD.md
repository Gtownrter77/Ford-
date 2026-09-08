# Termux Build Guide

## Decision

The complete desktop build includes the native `sporttrac_llama` C++/JNI library and requires Android NDK `28.2.13676358`. Google does not provide that NDK as an ARM64 Termux package. The project now has a deliberate Termux profile that skips only the native offline llama library so the Kotlin/Compose app can be assembled on an ARM64 phone.

The normal build remains unchanged and continues to include the native engine when run on a supported desktop/CI Android toolchain.

## Termux build

From the repository root, after `ANDROID_HOME` points to an SDK containing `platforms/android-36/android.jar` and the Termux Android tools are on `PATH`:

```bash
./gradlew testDebugUnitTest assembleDebug -PtermuxBuild=true --no-daemon
```

The resulting APK is:

```text
app/build/outputs/apk/debug/app-debug.apk
```

This Termux APK retains Google sign-in, Gemini/API Mentor paths, Compose UI, 3D assets, diagnostics, manual content, and OBD-II/FORScan source. It does not package the local offline llama JNI engine because native C++ compilation is intentionally disabled by the property.

## Complete build

On a supported Android development machine with the Android NDK installed:

```bash
./gradlew testDebugUnitTest assembleDebug --no-daemon
```

Do not pass `-PtermuxBuild=true` for the complete build.

## Important limitation

The Termux profile is a packaging workaround, not a claim that the phone has the native offline AI engine. The app must not silently present that engine as available. Any future UI that exposes local-model controls should check native-library availability and show an explicit unavailable state.

## Firebase configuration

The Termux profile still requires the same Firebase setup for Google sign-in:

- Place `app/google-services.json` in the app module.
- Set `GOOGLE_WEB_CLIENT_ID` in the local Secrets environment.
- Rebuild after configuration.

Do not commit Firebase project files, OAuth secrets, or private keys.

## Why this is the recommended phone path

This preserves the complete source tree and normal release path while giving Termux a reproducible, clearly labeled build mode. It avoids pretending that a missing NDK license or an ARM64-incompatible host toolchain can produce the native library.
