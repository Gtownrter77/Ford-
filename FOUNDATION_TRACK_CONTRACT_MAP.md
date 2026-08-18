# Far-End Foundation Track Contract Map

**Workstream:** Independent AI foundation track  
**Date:** 2026-08-18  
**Purpose:** Establish the remaining app foundation without pretending that partial source paths are finished services.

## Foundation status

The remaining app vision is not one missing feature. It is a set of domains with different levels of source maturity. The foundation track therefore starts with explicit contracts and readiness states rather than immediately adding UI that implies unsupported backend behavior.

| Domain | Existing source evidence | Current truth status | Foundation next step |
|---|---|---|---|
| Procedural 3D model | `Component3DModel`, `SportTracData`, `Interactive3DViewport`, generated hardware and subassembly services. | Implemented procedural renderer; incomplete vehicle coverage. | Add coverage/readiness metadata and prioritize A/C-related assemblies. |
| A/C Workbench | Diagnostic paths, practice steps, safety boundaries, component links, practice gate. | Substantially implemented local training flow; device behavior pending. | Keep safety and transfer boundaries explicit; add deterministic step-state coverage. |
| Mentor | `MentorModeDialog`, Room checklist persistence, TTS, voice commands, torque and repair-step context. | Partially implemented guided repair mode; not a complete conversational parts agent. | Make voice activation opt-in, then define context/question/answer contracts. |
| Audio diagnosis | `AudioAnalysisPipeline`, acoustic repository, reference entities, voice/audio UI. | Source path exists; no verified microphone session or classification accuracy. | Define input/session/result/uncertainty contract before claiming diagnosis. |
| Part Store | Local catalog, ranking preferences, readiness, retailer search-link models, price-watch UI. | Local planning UI; no verified universal live scraping, commercial pricing, or ordering. | Define provider-agnostic listing and provenance contract with privacy boundary. |
| Offline/cache foundation | Room database, cache entities, `OfflineCacheRepository`, startup seeding. | Implemented local persistence and seed path; runtime device behavior pending. | Track cache version, source provenance, and failure state explicitly. |
| Diagnostics | Symptom flows, service-manual data, FORScan models, Gemini-oriented repository. | Mixed local diagnostic flows and service abstractions; external/LLM behavior not device-verified. | Separate deterministic local guidance from optional external analysis results. |
| Body Shop/customization | Some UI/component scaffolding and model/scene abstractions. | Partial or conceptual; no complete customization pipeline established. | Define an asset-generation/import contract before promising generated body kits. |
| Live commerce/affiliate | Retailer links and price-watch UI references. | Not verified as live scraping, ordering, or revenue integration. | Keep as future connector contract; no automatic order or payment path. |

## First foundation improvement

The first safe foundation improvement is a typed readiness contract that lets the app and documentation distinguish `SOURCE_PRESENT`, `LOCALLY_FUNCTIONAL`, `SANDBOX_VERIFIED`, `DEVICE_PENDING`, `PARTIAL`, and `CONCEPTUAL` without turning a source placeholder into a completed feature claim. This contract is intentionally informational and does not imply that a backend, live retailer connector, or physical device has been verified.

## Non-goals for this track

The foundation track will not fabricate customer reviews, simulate retailer prices as live data, claim universal parts coverage, silently activate microphones, submit orders, collect payment data, or label the procedural Canvas model as a complete Blender/GLTF vehicle replica.

## References

[1]: ./FINAL_EVIDENCE_LEDGER_2026-08-18.md "Ford Sport Trac Android App — Final Evidence Ledger"
[2]: ./PROJECT_HANDOFF.md "Ford Sport Trac Android Project Handoff"
[3]: ./PROCEDURAL_MODEL_ARCHITECTURE.md "Procedural Model Architecture"
