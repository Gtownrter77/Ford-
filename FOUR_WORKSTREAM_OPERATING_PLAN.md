# Four-Workstream Operating Plan

**Project:** 2004 Ford Explorer Sport Trac reference and repair-training Android app  
**Status:** Authorized execution

> These are separately labeled AI workstreams, not fabricated people. Each stream owns a narrow domain, produces inspectable evidence, and reports limitations explicitly.

## Ownership and boundaries

| Workstream | Owns | Does not claim | Verification gate |
|---|---|---|---|
| **Primary Stream** | Mentor lifecycle, opt-in voice control, performance streamlining, build health, release reconciliation, handoff and evidence ledger. | A complete conversational AI backend or physical-device success. | Gradle build/tests, APK signature/ZIP integrity, then device protocol when available. |
| **Foundation Stream** | Cross-app readiness vocabulary, provenance/uncertainty contracts, domain inventory, shared contract tests. | Live services, complete model coverage, or device verification. | Contract tests and evidence notes for every readiness state. |
| **A/C Stream** | A/C Workbench content, model-first practice, safety boundaries, component routing, rehearsal gates. | Real-truck diagnosis, refrigerant handling authorization, or exact calibration without service evidence. | A/C source checks, build/tests, and physical-device protocol. |
| **Parts/Commerce Stream** | Local catalog, ranking preferences, provenance, readiness, privacy boundaries, future connector contracts. | Live prices, universal scraping, automatic orders, payment storage, reviews, or affiliate revenue. | Ranking/integrity tests and provider/freshness evidence for any live connector.

## Current executed changes

The Primary Stream changed Mentor microphone listening from automatic startup to explicit user opt-in. Opening Mentor no longer silently starts speech recognition. The user can start or stop listening with the microphone control, and cleanup stops listening and destroys the voice manager when the dialog closes.

The Foundation Stream added `FeatureReadiness.kt`, `FoundationTrackCatalog`, `FeatureReadinessContractTest.kt`, and `FOUNDATION_TRACK_CONTRACT_MAP.md`. These structures distinguish source presence, local function, sandbox evidence, device-pending status, partial implementation, and conceptual work.

The A/C Stream has already added the per-step `Mark 3D Step Rehearsed` gate. The Parts/Commerce Stream is assigned to inventory and strengthen local ranking and provenance boundaries before any live connector work.

## Reconciliation rules

A stream may edit its owned files, but shared release documents are updated only during reconciliation. No source status becomes “device verified” without an observed device run. A successful build proves compilation and test execution; APK signature and ZIP checks prove package integrity only. The procedural Canvas model remains a substantial procedural training model, not a complete Blender/GLTF every-fastener replica.

## References

[1]: ./FINAL_EVIDENCE_LEDGER_2026-08-18.md "Final Evidence Ledger"
[2]: ./PROJECT_HANDOFF.md "Project Handoff"
[3]: ./FOUNDATION_TRACK_CONTRACT_MAP.md "Foundation Track Contract Map"

## Documentation hold status — 2026-08-18

The four-stream plan is documented and authorized as an operating structure, but the current build is held. No stream may treat a started or planned build as a completed combined release. Primary/Mentor has a separately recorded packaged artifact; Foundation and Parts/Commerce source contracts were added after that artifact and require a future authorized combined build before they can be called combined-build verified. A/C has a separately recorded practice-gate artifact.

The four streams are not persistent hidden agents. They are scoped work lanes whose outputs are reconciled by the primary project process. The only current authority for physical-device status is observed device evidence, which remains absent.
