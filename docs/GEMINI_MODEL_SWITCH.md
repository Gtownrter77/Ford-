# Gemini Model Switch: `gemini-3.5-flash` to `gemini-2.5-flash`

## Exact source edit

Edit this file:

```text
app/src/main/java/com/example/api/GeminiApiService.kt
```

Replace the `@POST` path on **line 44**:

```kotlin
@POST("v1beta/models/gemini-3.5-flash:generateContent")
```

with:

```kotlin
@POST("v1beta/models/gemini-2.5-flash:generateContent")
```

The required stable model identifier is:

```text
gemini-2.5-flash
```

## Request compatibility

No other change is required for the current text-only request shape. The existing Retrofit call already targets the Gemini Developer API `models.generateContent` route and already sends supported fields used by this app: `contents`, `systemInstruction`, and `generationConfig`. The model name is the only value embedded in the current endpoint path. [1] [2]

The current acoustic UI does **not** upload recorded audio to Gemini. It sends a text/numeric summary containing values such as peak frequency, RMS dBFS, and the local spectral match. Switching this model string does not change that behavior. [1]

## Known model/API notes

| Item | Current repository behavior | Gemini 2.5 Flash switch note |
|---|---|---|
| Model path | `gemini-3.5-flash:generateContent` | Replace only the model segment with `gemini-2.5-flash`. |
| REST operation | `v1beta/models/{model}:generateContent` | The same `generateContent` operation remains applicable. [1] |
| API key | `BuildConfig.GEMINI_API_KEY`; placeholder falls back to local rules | No key-name change is required. A non-placeholder key is still required before a cloud call is attempted. |
| Existing request body | Text `contents`, text `systemInstruction`, `generationConfig` | No schema change is required for this current use. [1] |
| Audio | The app sends only an acoustic text/numeric summary | Gemini 2.5 Flash accepts audio input but does not provide audio generation. This repository does not currently send an audio payload. [2] |

> This document is a switch scaffold only. It does not change the configured endpoint, test a cloud request, or establish that a production key or model access is available.

## References

[1]: [Google Gemini API — GenerateContent reference](https://ai.google.dev/api/generate-content)

[2]: [Google Gemini API — Gemini 2.5 Flash model card](https://ai.google.dev/gemini-api/docs/models/gemini-2.5-flash)
