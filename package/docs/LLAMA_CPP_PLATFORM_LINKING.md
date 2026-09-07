# llama.cpp Platform Linking

This guide builds the native inference library only. It does **not** download, bundle, or install `DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf`.

> The CMake file downloads llama.cpp source through CMake `FetchContent` at the pinned revision `fe8156f789011f6ea0baf6917ea09f88b89d9554` during configuration. The model is a separate runtime artifact; follow `CROSS_PLATFORM_GGUF_PLACEMENT.md` for its private-storage location.

## Android: Gradle and CMake

| Step | Required change |
|---|---|
| 1 | Add `externalNativeBuild.cmake` in `app/build.gradle.kts` pointing to `../../shared/llama/CMakeLists.txt`, with CMake version `3.22.1` or newer. |
| 2 | Set the Android ABI filter to `arm64-v8a` first. Add other ABIs only after testing each native backend. |
| 3 | In the Android library that owns `LlamaNative`, call `System.loadLibrary("sporttrac_llama")` before creating `LlamaNative`. |
| 4 | Copy an approved GGUF through a model-install flow into `<Context.filesDir>/models/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf`; pass that runtime path to `nativeLoadModel`. |

```kotlin
// app/build.gradle.kts
android {
    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags += listOf("-std=c++17")
                arguments += listOf("-DANDROID_STL=c++_shared")
                abiFilters += listOf("arm64-v8a")
            }
        }
    }
    externalNativeBuild {
        cmake {
            path = file("../../shared/llama/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}
```

```kotlin
// The owner of LlamaNative, after the native build is enabled.
init {
    System.loadLibrary("sporttrac_llama")
}
```

The existing `LlamaNative.kt` intentionally does not call `System.loadLibrary`; add the load call only in the feature owner once the Android native build is enabled. `nativeGenerate` returns either generated text or a native error string in this first bridge version.

## iOS: Static Library and XCFramework

| Step | Required change |
|---|---|
| 1 | Configure the shared CMake project once for `iphoneos` and once for `iphonesimulator` through a CMake iOS toolchain file. |
| 2 | Build `sporttrac_llama` for both SDKs. CMake emits `libsporttrac_llama.a` because `APPLE` builds use a static target. |
| 3 | Package the device and simulator archives as `SportTracLlama.xcframework` with `xcodebuild -create-xcframework`. |
| 4 | Add the XCFramework to the iOS target’s **Frameworks, Libraries, and Embedded Content** list; add `LlamaInferenceBridge.mm` to **Compile Sources**; set `SportTracAI-Bridging-Header.h` as the target’s Objective-C Bridging Header. |
| 5 | Copy an approved GGUF through an iOS model-install flow into `<Application Support>/models/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf`; pass that path to `LlamaInferenceBridge`. |

```bash
# Supply a CMake iOS toolchain file that defines PLATFORM. Run once per SDK.
# `<path-to-ios.toolchain.cmake>` is intentionally a local build-machine input.
cmake -S shared/llama -B build/ios-device \
  -DCMAKE_TOOLCHAIN_FILE=<path-to-ios.toolchain.cmake> \
  -DPLATFORM=OS64 \
  -DCMAKE_BUILD_TYPE=Release
cmake --build build/ios-device --target sporttrac_llama --config Release

cmake -S shared/llama -B build/ios-simulator \
  -DCMAKE_TOOLCHAIN_FILE=<path-to-ios.toolchain.cmake> \
  -DPLATFORM=SIMULATORARM64 \
  -DCMAKE_BUILD_TYPE=Release
cmake --build build/ios-simulator --target sporttrac_llama --config Release

xcodebuild -create-xcframework \
  -library build/ios-device/libsporttrac_llama.a \
  -headers shared/llama/include \
  -library build/ios-simulator/libsporttrac_llama.a \
  -headers shared/llama/include \
  -output build/SportTracLlama.xcframework
```

## Shared API Boundary

`LlamaInference.loadModel(path)` checks that the caller-provided path is a regular file and calls `llama_model_load_from_file`. `generate(prompt, config)` tokenizes the raw prompt, creates a context for that request, samples up to `maxTokens`, then frees request-level context and sampler state. The caller owns prompt formatting; the model-specific DeepSeek chat template is not injected by this generic shared class.

## Build Boundary

Do not run the Android native build, iOS archive build, model install, or runtime load until the target phone/device RAM policy, native-backend selection, GGUF checksum, and model-distribution license record are approved.
