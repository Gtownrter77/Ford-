# Four-Stream Split Screen

**Project:** 2004 Ford Explorer Sport Trac repair-training app  
**View type:** Current workflow status board  
**Important:** This is a concrete project artifact showing four separately labeled AI workstreams. It is not a live multi-agent chat window and does not imply that four human agents are present.

| **PRIMARY STREAM** | **FOUNDATION STREAM** |
|---|---|
| **Mission:** streamline the app, finish Mentor voice privacy hardening, manage lifecycle/performance, and reconcile releases. | **Mission:** build shared readiness, provenance, uncertainty, and evidence contracts for the remaining app vision. |
| **Current work:** Mentor microphone listening changed from automatic startup to explicit opt-in. Closing the dialog stops listening and destroys the voice manager. | **Current work:** `FeatureReadiness.kt`, `FoundationTrackCatalog`, `FeatureReadinessContractTest.kt`, and `FOUNDATION_TRACK_CONTRACT_MAP.md` were added. |
| **Owned areas:** `MentorModeDialog.kt`, `VoiceCommandManager.kt`, handoff, ledger, APK packaging. | **Owned areas:** readiness models, cross-domain status documents, contract tests, evidence vocabulary. |
| **Evidence:** Mentor opt-in build completed successfully before the later Foundation and Parts/Commerce edits; the packaged Mentor APK passed v2/v3 signature and ZIP checks. | **Evidence:** Foundation contract source and tests are present; a later combined build was started, but this documentation-only pass does not certify its final result. |
| **Boundary:** No claim of a complete conversational AI backend or physical-device success. | **Boundary:** No claim of live services, complete model coverage, or device verification. |

| **A/C STREAM** | **PARTS / COMMERCE STREAM** |
|---|---|
| **Mission:** deepen the A/C Workbench, model-first practice, safety boundaries, and A/C component routing. | **Mission:** strengthen the private Part Store’s local planning, ranking, provenance, readiness, and future connector boundaries. |
| **Current work:** A/C practice now requires `Mark 3D Step Rehearsed` before the user can advance to the next practice step. The acknowledgment resets for each step. | **Current work:** `PartListingTruth.kt` adds a shared evidence-aware summary for quote status, fitment evidence, seller identity, live-price presentation, and no-ordering behavior. |
| **Owned areas:** `AcSystemWorkbenchDialog.kt`, A/C records in `SportTracData.kt`, A/C IDs, safety text, practice tests. | **Owned areas:** `PartStoreRanking.kt`, `PriceWatchModels.kt`, `PartStoreCatalogRanking.kt`, retailer-link and listing-truth models/tests. |
| **Evidence:** A/C practice-gate revision rebuilt successfully and its APK passed v2/v3 signature and ZIP checks. | **Evidence:** `PartListingTruthTest.kt` is present and covers saved-catalog versus live-authorized quote labels, fitment truth, seller text, and the invariant that ordering remains disabled; a final combined-build result is pending certification. |
| **Boundary:** No real-truck diagnosis, refrigerant release, or qualified MVAC work is authorized by the app. | **Boundary:** No live universal scraping, automatic orders, payment storage, fabricated reviews, or affiliate-revenue claim. |

## Reconciliation gate

All four streams are reconciled only through one combined Gradle build and unit-test task. The final APK must pass signature verification and ZIP integrity verification. A combined build was started after the latest Foundation and Parts/Commerce additions, but this documentation-only pass neither reruns nor certifies it because the build is held. A successful build verifies compilation and tests; it does not verify launch, ANR resolution, frame rate, microphone behavior, or workflow performance on Ry’s physical device.

## Current overall truth

The app contains substantial source implementation across all four domains. The current active model is a procedural Compose Canvas training model with generated reference hardware, not a completed Blender/GLTF every-fastener replica. The Mentor, A/C, Foundation, and Parts/Commerce changes are source-backed; physical-device verification remains pending.

## References

[1]: ./FOUR_WORKSTREAM_OPERATING_PLAN.md "Four-Workstream Operating Plan"
[2]: ./FOUNDATION_TRACK_CONTRACT_MAP.md "Foundation Track Contract Map"
[3]: ./PROJECT_HANDOFF.md "Project Handoff"
[4]: ./FINAL_EVIDENCE_LEDGER_2026-08-18.md "Final Evidence Ledger"
