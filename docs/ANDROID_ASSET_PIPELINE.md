# Android asset pipeline — Ford- investigation

## What this app actually uses

Four separate pipelines. They are not interchangeable.

| Pipeline | Location | Reader | Packaged today |
|---|---|---|---|
| Repair catalog | `app/src/main/assets/parts_data.json` + `parts.tsv` | `AssetManager.open()` | Yes (~7 KB) |
| Sound index | `assets/SOUND_LIBRARY_MANIFEST.json` | acoustic repo | Manifest only, no audio files |
| Wreck GLB | `assets/models/*.glb` | SceneView / Filament | Folder exists, file missing |
| Offline LLM | `filesDir/models/*.gguf` | llama.cpp JNI | Not packaged; sidecar install |
| Cached 3D rows | Room `Cached3DAssetEntity` | offline cache | Metadata only |
| User CAD import | SAF / `CadStepIngestionService` | runtime files | Not an APK asset |

Gradle had no `noCompress` list before 2026-08-26. That is now set for `glb`, `gltf`, `bin`, `gguf`, `ktx`, `filamat`.

## Android packaging rules that matter here

1. `assets/` keeps original paths. Use `context.assets.open("models/foo.glb")`. No `R.` id.
2. `res/raw` gets an `R.raw` id and loses subfolders. Wrong place for a named wreck model.
3. `res/drawable` is processed by aapt. The 948 KB `ford_explorer_icon_*.jpg` in drawable is a photo, not a vector. It should live in `assets/` or `res/raw` if kept.
4. aapt2 compresses most asset files. On many devices `AssetManager.open()` fails for a *compressed* file over ~1 MB. GLB and GGUF must be listed in `androidResources.noCompress`.
5. GitHub rejects blobs over 100 MB. A Blender wreck GLB plus textures often exceeds that. Use Git LFS or do not commit the binary.
6. Play install size: keep the base module lean. A Q4 1.5B GGUF is ~1 GB and must never ship inside the APK. This repo already does that correctly via `ModelInstallPath`.
7. Play Asset Delivery (install-time / fast-follow / on-demand packs) is the right next step if the wreck GLB is 20–150 MB. Not wired in this Gradle file.

## Correct drop rules for this product

- Catalog JSON/TSV: keep in `assets/`.
- Wreck GLB under ~50 MB: `assets/models/ford_explorer_sport_trac_2004_wreck.glb` + Git LFS + `noCompress`.
- Wreck GLB 50–150 MB: Play Asset Delivery install-time pack named `vehicle_glb`, copy or map into Filament.
- Wreck GLB larger, or licensed file you cannot put on GitHub: copy to `filesDir/models/` the same way GGUF is installed, then point SceneView at `file:///...`.
- GGUF: stay on `context.filesDir/models/`. Never `assets/`.
- Sound clips: `assets/sounds/*.ogg` with `noCompress += "ogg"`, or `res/raw`.

## SceneView load path

`InteractiveRepairViewer` calls `modelLoader.loadModelInstanceAsync(catalog.modelAssetPath)` with `models/ford_explorer_sport_trac_2004_wreck.glb`. That string is an AssetManager relative path. If the file is later moved to `filesDir`, the loader must switch to a `file://` URI. Do not assume the same string works for both.
