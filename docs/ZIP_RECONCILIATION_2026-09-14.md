# ZIP Reconciliation Note

**Audit date:** 2026-09-14

Mentor.zip was re-audited as the newly supplied snapshot. It contains **1,084 files** and expands to approximately **49,950,392 bytes**. Its root is `ford-sport-trac/` and contains the same current project structure represented by GitHub `main`.

## Current findings

- Core Android/build files checked by Git blob SHA match GitHub `main`, including `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`, `MainActivity.kt`, `SportTracData.kt`, `Interactive3DViewport.kt`, `MentorKnowledge.kt`, mentor-web source, workflow, `.gitignore`, `.env.example`, and `docs/CUSTOM_DUAL_EXHAUST_GUIDE.md`.
- The ZIP contains the expected canonical GLB assets: `ford_explorer_sport_trac_2004.glb` (8,544,536 bytes) and `ford_explorer_sport_trac_2004_corrected_exterior.glb` (10,300,468 bytes).
- The repository source-index validator passes and reports **9,560 pages**, **0 empty pages**, **0 duplicate body hashes**, and all 9,560 pages labeled **2004 Sport Trac 4WD V6-4.0L VIN K Flex Fuel** with `preferred` eligibility. This is stronger current evidence than the older 2WD-only audit note.
- The ZIP does **not** contain the native CMake/Llama implementation referenced by `app/build.gradle.kts`. GitHub `main` likewise has the JNI bridge but no `shared/llama` tree or `LlamaInference.h`; therefore the normal native offline-Llama build path remains incomplete.
- The Termux/reduced build path is intentionally designed to exclude that native library with `-PtermuxBuild=true`.
- A local Gradle build remains environment-blocked when the sandbox cannot obtain the required Gradle/Android toolchain; no claim of a successful fresh Android build is made here.

## Repository handling

GitHub `main` remains the permanent home/source of truth. The ZIP's `package/` subtree contains older snapshot material and must not blindly overwrite newer root project files.

## Bottom line

The new ZIP does not reveal a missing 4WD source corpus; the canonical VIN-K source index is present and validates cleanly. The remaining major technical blocker identified by this reconciliation is the missing native `shared/llama` implementation required for the complete offline-AI build, plus the still-unfinished Level-3 Android/device verification.