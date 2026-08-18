# Pending Ford Sport Trac Project Tasks

**Prepared:** 2026-08-18  
**Basis:** Unchecked items in `todo.md`, reconciled against the current handoff and evidence records.

## Immediate release blockers

| Priority | Pending task | Why it matters | Current boundary |
|---|---|---|---|
| 1 | Install the latest APK on a real Android phone and execute `PHYSICAL_DEVICE_VERIFICATION_PROTOCOL.md`. | This is the only way to verify launch, ANR/closing behavior, first-frame performance, navigation, and actual interaction. | No phone result, `adb`, emulator, logcat, bugreport, or gfxinfo evidence is currently recorded. |
| 2 | If the APK cannot be installed, complete the transfer diagnosis: finish the ZIP download, extract the APK, and verify the phone-side checksum. | An incomplete ZIP can make a valid APK appear broken. | A partial-download case is documented but not closed with phone-side evidence. |
| 3 | If the app still ANRs or closes, capture logcat, bugreport, CPU/memory, gfxinfo, battery, and thermal data and isolate whether startup or first-frame rendering dominates. | Without traces, further performance edits would be speculation. | Source mitigations exist but are not device-proven. |
| 4 | Build a minimal launch path and test the 3D screen independently if the full app still fails on the device. | This separates the 3D renderer from Lounge, Part Store, Room, and other startup work. | The minimal-path experiment has not been completed on a physical device. |

## Current feature-completion work

### Destination and navigation

The Lounge still needs to become the first-visit front door for vehicle setup, skill level, privacy, voice, ranking, budget, and user preferences. The Shop needs to connect the 3D model, Repair Manual, Diagnostics, and Mentor workbench as one practice-first environment. The Part Store needs its private planning-room presentation completed. The Body Shop remains a customization room requiring a defined concept-generation workflow, saved builds, and a clear concept-only/fitment-verification boundary.

### Mentor

The remaining Mentor work is to add a guided needs-and-budget intake that can hand off from Part Store to Mentor Mode; connect Mentor routing to a real model-backed practice component and repair lesson; visibly distinguish completed work, drafts, and customer confirmation; and preserve hands-free guidance without implying that a message or order was sent when it was only drafted. The latest source has opt-in microphone behavior, but the current combined artifact still requires final reconciliation and device testing.

### A/C Workbench

The A/C Workbench needs further content-depth review after the completed per-step 3D rehearsal gate. Remaining work includes checking the full component graph, diagnostic branches, repair-step metadata, and model-backed lessons; adding deterministic regression coverage where useful; and running the physical-device A/C workflow. Refrigerant recovery, evacuation, leak testing, and charging must remain qualified-service boundaries.

### Part Store and commerce

The Part Store needs continued strengthening of ranking, warranty, fitment, country-of-origin, quote status, seller identity, price freshness, and source provenance. The customer-controlled filters must remain intact. Live retailer connectors, universal scraping, O’Reilly commercial pricing, Amazon/RockAuto/eBay/Facebook Marketplace results, automatic ordering, payment storage, affiliate revenue, and customer reviews remain unimplemented or unverified and must not be represented as complete.

### Audio diagnosis

The audio-analysis path needs a real device microphone test, a documented input/session/result/confidence/uncertainty contract, and evidence about classification accuracy. Source repositories and acoustic reference structures exist, but that does not prove microphone capture or useful diagnosis on the phone.

### Procedural model

The project still needs a static render using the actual procedural Sport Trac geometry and renderer inputs, clearly labeled as a source-backed static render. The model needs incremental coverage expansion, especially for A/C-related assemblies and other repair-priority systems. The project does not yet have a verified Blender/GLTF/OBJ/FBX/Blend every-fastener replica.

### Foundation and four-stream execution

The Foundation Stream needs its readiness contract integrated where useful rather than remaining documentation-only. The Primary Stream needs the final Mentor opt-in artifact reconciled. The A/C Stream needs its next content pass. The Parts/Commerce Stream needs the provenance contract integrated into the relevant UI or data path. All four streams need one combined build, test result, APK signature check, ZIP integrity check, updated handoff, updated evidence ledger, and a refreshed recovery archive.

## Delivery and repository tasks

The standalone Destination Concept roadmap/room-map archive still needs to be packaged and verified, with the large visual concept delivered separately so it cannot block the document download. Ry has now authorized a GitHub push after the documentation-only reconciliation. The remaining repository steps are to inspect the configured remote and authorization state, commit the current local project state without running a build, push it to the confirmed repository, and verify the resulting remote reference. The uploaded/server-side ZIP still needs checksum and content verification; a phone-downloaded copy requires its own checksum or re-upload before it can be directly verified.

## Consolidated status

The completed work is substantial: source audits, startup-dispatch mitigation, renderer hardening, material-aware approximation, projected-center recomposition reduction, A/C rehearsal gate, Mentor opt-in voice source change, foundation readiness contracts, Parts/Commerce truth utility, unit-test runs, APK signature checks, ZIP integrity checks, handoff documents, evidence ledgers, physical-device protocol, and recovery archives.

The decisive unfinished item is **real-device verification**. Until that is complete, the app should be described as source-present, sandbox-built, unit-tested, and package-verified—not phone-verified or production-ready.
