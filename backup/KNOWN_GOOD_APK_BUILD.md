# Known-Good APK Build Marker

## Status

**PASSED**

A successful Android APK build has been confirmed for the supplied build artifact.

## Preserved artifact identity

Source package:

- File: `mentor-sport-trac-debug-reduced.zip`
- SHA-256: `942cb273665665b6e2892ea7429e14f3ab24c41837a8000b0c8d1f669d5c51bf`

Contained APK:

- File: `app-debug.apk`
- SHA-256: `838d592adbe3c53f09d10ab40fa2cf59a0ddeb2fa0c87ad69b494ad4ed78962c`
- Extracted size: 57,297,500 bytes

## Rule

Treat this artifact as a known-good build reference.

Do not change build configuration merely because earlier KSP or GitHub Actions failures existed. Any future build change should preserve a reproducible passing path and be validated independently.

## Continuity note

The binary APK and its source ZIP are preserved in the chat attachment context. This repository marker records their exact SHA-256 identities so the same artifacts can be verified later.

