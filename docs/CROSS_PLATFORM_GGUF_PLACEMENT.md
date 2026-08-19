# Cross-Platform GGUF Placement Guide

**Model file:** `DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf`

**Expected size:** approximately 1.12 GB.

> Do not place this file in Git, Android `res/`, Android `assets/`, or an iOS source bundle while the app is being developed. Keep the distribution artifact outside source control and copy it into app-private storage only through an approved model-install flow.

| Platform | Developer source/staging location | Runtime destination | Current scaffold status |
|---|---|---|---|
| Shared source | `/home/ubuntu/Downloads/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf` | None | Download source only; do not commit. |
| Android | `/home/ubuntu/Downloads/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf` | `<Context.filesDir>/models/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf` | JNI has no file-copy, loading, or storage implementation. |
| iOS | `/Users/<developer>/Downloads/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf` | `<Application Support>/models/DeepSeek-R1-Distill-Qwen-1.5B-Q4_K_M.gguf` | Swift/Objective-C++ bridge has no file-copy or loading implementation. |

## Required Runtime Contract

1. Verify the exact downloaded filename, file size, checksum, and upstream/license notices before an installer copies the model.
2. Copy the model into the platform’s app-private runtime location; do not expose the raw path as a hard-coded source-tree dependency.
3. Pass the resulting private runtime path to `LlamaInference.loadModel`, Android `nativeLoadModel`, or iOS `loadModelAtPath` only after llama.cpp is intentionally linked and the model-install flow is approved.
4. Keep the model binary out of the repository and release source package. Record its version, source URL, checksum, and license in the app’s third-party notices when a shipping package is selected.
