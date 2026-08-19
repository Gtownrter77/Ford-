# Complete Project Inventory — Sport Trac 3D

**Project:** 2004 Ford Explorer Sport Trac repair-training companion

**Purpose of this record:** This document is the permanent scope record for the Android project. It separates what exists in source, what has been sandbox-built or physically observed, what is only a stub or local prototype, and what has been discussed but is not yet implemented. It does not convert a design discussion into a completed feature.

> **Status vocabulary.** “Source-present” means code or repository content exists. “Sandbox-built” means the recorded Gradle assembly and unit-test command passed. “Device-verified” means a physical-phone result was actually reported or observed. “Stub” means an intentionally limited interface or placeholder is present. “Absent” means no implementation was identified in the repository audit.

## Inventory Baseline

The app is a privacy-first, practice-first learning tool centered on a 2004 Ford Explorer Sport Trac. Its intended use is to help the owner inspect a component, rehearse a repair, understand diagnostic evidence, make transparent parts choices, and retain control of real-world decisions. It is not an automatic-ordering system, a payment wallet, a verified universal repair manual, or a replacement for professional diagnosis. [1] [2]

The current source has two distinct 3D directions. The earlier path is a procedural Compose Canvas representation with a broad component catalog. The newer repair-realm path is a guarded SceneView/Filament composable that expects a separately supplied GLB asset. The repair realm does not currently contain a licensed 2004 Sport Trac wreck GLB in the repository. [2] [3]

---

# Part 1 — Core Features Already Built

The table below records only items that currently exist in the repository. The verification column states the strongest known level; it is deliberately not a release claim.

| Feature | Present implementation | Evidence / primary paths | Current verification state |
|---|---|---|---|
| **Cold-launch Lounge shell** | A zero-argument `LoungeScreen()` composes a minimal Lounge surface without Room, shared-preference, navigation, coroutine, voice, or renderer initialization. It includes a local sign-out confirmation/placeholder flow. | `app/src/main/java/com/example/ui/screens/LoungeScreen.kt` | **Source-present; sandbox-built.** The owner reported the cold-launch shell as passing a 10/10 phone verification. That result does not verify the rest of the application. |
| **Safe-shell route isolation** | `SAFE_SHELL_MODE` routes non-Lounge tabs to an under-review screen. This intentionally prevents feature surfaces from composing while startup/device stability is isolated. | `app/src/main/java/com/example/MainActivity.kt` | **Source-present; sandbox-built.** It remains a containment boundary, not evidence that feature routes are device-verified. |
| **Lazy feature-data boundary** | Room-backed repositories, cache flows, acoustic reference seeding, and related feature data are deferred through `ensureFeatureData()` until an explicit `VIEW_3D` request. | `app/src/main/java/com/example/ui/viewmodel/ExplorerViewModel.kt` | **Source-present; sandbox-built.** |
| **Procedural vehicle-learning model** | The legacy training path uses a Compose Canvas renderer and a 2004 Sport Trac component catalog, repair steps, torque data, and generated hardware/subassembly concepts. | `app/src/main/java/com/example/ui/components/Interactive3DViewport.kt`; `app/src/main/java/com/example/data/SportTracData.kt`; `app/src/main/java/com/example/data/VehicleHardwareCatalog.kt` | **Source-present; previously implicated in device instability.** It is not a Blender/GLTF vehicle replica. |
| **Guarded Interactive Repair Viewer** | A SceneView/Filament-oriented inspection surface includes GLB asset loading, node-name-to-part matching, camera focus/reset controls, an inspection card, a dark/amber HUD, a mentor-narration action, and an asset-required state. | `app/src/main/java/com/example/ui/components/InteractiveRepairViewer.kt`; `REPAIR_REALM_ARCHITECTURE.md` | **Source-present; sandbox-built.** No licensed wreck GLB is packaged, and live raycast/camera/audio behavior is not device-verified. |
| **Repair-viewer data adapter** | A wrapper maps the viewer’s tapped part ID through `PartDataLoader`, displays name/system/description for a known ID, and displays an `Unknown Part` fallback otherwise. | `app/src/main/java/com/example/ui/components/RepairViewerWithData.kt`; `app/src/main/java/com/example/ui/components/InteractiveRepairViewer.kt` | **Source-present; sandbox-built.** |
| **Part-data loader** | A dependency-free Kotlin loader reads `assets/parts.tsv` or the JSON equivalent, returns `PartData` by ID, and includes a five-part sample set. | `app/src/main/java/com/example/data/PartDataLoader.kt`; `app/src/main/assets/parts.tsv`; `app/src/main/assets/parts_data.json` | **Source-present; sandbox-built.** |
| **Mentor audio stub** | A repair-realm narration interface and Android TTS implementation speak only after explicit caller action. | `app/src/main/java/com/example/audio/MentorAudioPlayer.kt` | **Stub; source-present; sandbox-built.** It is TTS, not a recorded Rhetorician audio library. |
| **Mentor repair guidance** | Mentor Mode reads selected repair steps aloud, handles step progression and torque narration, manages mute/settings, and persists repair-checklist progress locally. | `app/src/main/java/com/example/ui/components/MentorModeDialog.kt`; `app/src/main/java/com/example/util/MentorTtsManager.kt`; `app/src/main/java/com/example/data/MentorVoiceSettingsRepository.kt` | **Source-present.** TTS exists; full device behavior remains separate from source presence. |
| **Voice-command input** | Android `SpeechRecognizer` captures opt-in commands and exposes recognized text/state. Mentor Mode starts listening only when its local voice-listening state is enabled. | `app/src/main/java/com/example/util/VoiceCommandManager.kt`; `app/src/main/java/com/example/ui/components/MentorModeDialog.kt` | **Source-present.** This is command input, not a verified hands-free repair session. |
| **A/C Workbench** | A local diagnostic/practice UI contains symptom paths, safety wording, step rehearsal controls, component links, and Parts handoff. | `app/src/main/java/com/example/ui/components/AcSystemWorkbenchDialog.kt` | **Source-present; sandbox-built in prior recorded work.** Physical feature-flow verification remains distinct. |
| **Acoustic workbench** | The app can request microphone permission, capture PCM with `AudioRecord`, calculate an FFT, compare spectrum data against local Room records, display ranked matches, and run explicitly marked synthetic training scenarios. | `app/src/main/java/com/example/service/AudioAnalysisPipeline.kt`; `app/src/main/java/com/example/util/FastFourierTransform.kt`; `app/src/main/java/com/example/data/AcousticDiagnosticRepository.kt`; `app/src/main/java/com/example/ui/components/AcousticSoundDiagnosticDialog.kt` | **Source-present.** It has no bundled WAV/MP3 reference files and no recorded-audio persistence. Similarity output is explicitly presented as an inspection clue, not a diagnosis. [4] |
| **Diagnostics and FORScan-style UI** | The app has a dialog with local PID/DTC records, random live-value simulation, pasted-log text matching, and component navigation. | `app/src/main/java/com/example/ui/components/ForscanDialog.kt`; `app/src/main/java/com/example/data/ForscanData.kt`; `app/src/main/java/com/example/model/ForscanModel.kt`; `app/src/main/java/com/example/ui/screens/DiagnosticsScreen.kt` | **Source-present local UI/data.** It is not a live FORScan or OBD transport. |
| **Gemini-oriented diagnostic chat** | A Retrofit client is configured for `gemini-3.5-flash`; the diagnostic repository submits text when a non-placeholder API key is available and otherwise uses embedded local fallback analysis. | `app/src/main/java/com/example/api/GeminiApiService.kt`; `app/src/main/java/com/example/data/GeminiDiagnosticRepository.kt`; `app/src/main/java/com/example/ui/components/GeminiChatView.kt`; `.env.example` | **Source-present.** No `gemini-2.5` identifier is present, and a live key/session was not verified by this project record. [4] |
| **Part Store and readiness planning** | The repository contains an offline catalog, comparison/ranking models, readiness packages, search-link behavior, fitment evidence states, and no-ordering safeguards. | `app/src/main/java/com/example/data/SportTracPartsCatalog.kt`; `app/src/main/java/com/example/data/SportTracPartsReadiness.kt`; `app/src/main/java/com/example/data/PartStoreCatalogRanking.kt`; `app/src/main/java/com/example/ui/screens/PartsShoppingScreen.kt`; `RECOMMENDATION_INTEGRITY.md` | **Source-present.** Catalog price records are not live retailer quotes, and no payment or automatic order flow exists. [1] |
| **VR UI contract** | The repair realm exposes a future-facing VR toggle/state boundary and labels true stereoscopic/head-tracked rendering as requiring a separate adapter. | `app/src/main/java/com/example/ui/components/InteractiveRepairViewer.kt`; `REPAIR_REALM_ARCHITECTURE.md` | **Stub / UI contract only.** It is not a Cardboard or OpenXR implementation. |
| **Project truth and release records** | Build evidence, route-isolation evidence, ANR analysis, a physical-device checklist, architecture notes, and handoff records are kept in the repository. | `ANR_RESOLUTION_REPORT.md`; `DEVICE_VERIFICATION_PROTOCOL.md`; `SAFE_SHELL_ROUTE_ISOLATION_EVIDENCE_2026-08-19.md`; `PROJECT_HANDOFF.md`; `REPAIR_REALM_ARCHITECTURE.md` | **Documentation present.** Documents do not replace physical-device validation. |

---

# Part 2 — Pending Features Discussed for the Project

The following items have been discussed, requested, named in source comments/docs, or are necessary to complete an existing stub. They are not recorded here as completed features.

| Pending feature | Current state | Evidence of discussion or partial work | Release placement |
|---|---|---|---|
| **Real FORScan integration** | **Absent.** The current feature is local UI/data and pasted-log matching. | `ForscanDialog.kt`; `ForscanData.kt`; `FOUNDATION_TRACK_CONTRACT_MAP.md` | Later release / external integration. |
| **OBD2 binary and Bluetooth feed** | **Absent.** Bluetooth permissions exist, but no adapter discovery, socket, ELM327 protocol, PID command, CAN/binary parser, or read loop exists. | `AndroidManifest.xml`; forensic audit record | Later release / external integration. |
| **Live acoustic diagnostic validation** | **Partially implemented source path; unverified as a real diagnostic service.** The mic/FFT code exists, but profile quality, device behavior, and diagnostic accuracy are not validated in this record. | `AudioAnalysisPipeline.kt`; `AcousticSoundDiagnosticDialog.kt`; `FOUNDATION_TRACK_CONTRACT_MAP.md` | MVP validation work before any diagnostic-confidence claim. |
| **Gemini 2.5 repair intelligence** | **Absent.** Current source names `gemini-3.5-flash`, not Gemini 2.5. | `GeminiApiService.kt`; forensic audit record | Later configuration/integration decision. |
| **Verified live LLM service behavior** | **Unverified.** Cloud calls are conditional on an injected key; local fallback text is embedded in source. | `GeminiDiagnosticRepository.kt`; `.env.example` | MVP validation if cloud intelligence is retained. |
| **Full Mentor “travel agent for parts” conversation** | **Absent.** Existing Mentor Mode teaches a selected component but does not conduct a full goal/budget/tools/timeline/fitment route-planning conversation. | `PROJECT_HANDOFF.md` | MVP core experience. |
| **Live/recorded Mentor voice library** | **Absent.** Current narration is Android TTS; no Rhetorician audio files are included. | `MentorAudioPlayer.kt`; no packaged audio assets in audit | Later release. |
| **VIN entry and fitment workflow** | **Discussed; partial data-model/concept support only.** No verified VIN lookup/service integration is recorded. | `PROJECT_HANDOFF.md`; `SHOP_DESTINATION_CONCEPT.md`; fitment/readiness source models | MVP only if a real fitment source is authorized. |
| **Actual VR mode** | **Absent.** Current implementation is an explicit UI contract only. | `InteractiveRepairViewer.kt`; `REPAIR_REALM_ARCHITECTURE.md` | Later release. |
| **Parts ordering commission / affiliate program** | **Not implemented.** No checkout, payment storage, affiliate account, tracking tag, or sponsor placement is recorded. | `PROJECT_HANDOFF.md`; `FUTURE_AFFILIATE_POLICY.md` | Later release after actual partner agreements. |
| **Live retailer pricing or scraping** | **Absent.** Current values are saved/reference catalog data and outbound search-link paths. | `PROJECT_HANDOFF.md`; Parts Store source | Later release, only through authorized data paths. |
| **Expanded parts catalog and repair coverage** | **Partial.** The current catalog and vehicle records are substantial but not universal for trim, year, system, fastener, or specialty-repair coverage. | `PROJECT_HANDOFF.md`; `SportTracData.kt`; `SportTracPartsCatalog.kt` | Ongoing content expansion after MVP. |
| **Licensed 2004 Sport Trac wreck GLB asset** | **Absent.** The repair viewer expects an external asset and node-name contract. | `InteractiveRepairViewer.kt`; `REPAIR_REALM_ARCHITECTURE.md` | MVP blocker for live SceneView inspection. |
| **Device verification of repair realm** | **Pending.** No device evidence in this record verifies GLB loading, raycast selection, cinematic camera movement, mentor audio, or VR adapter behavior. | `DEVICE_VERIFICATION_PROTOCOL.md`; `REPAIR_REALM_ARCHITECTURE.md` | MVP validation work. |
| **Body Shop / body-kit generation** | **Discussed/design direction only.** No completed generator, saved-build model, or fitment handoff is recorded. | `SHOP_DESTINATION_CONCEPT.md`; `PROJECT_HANDOFF.md` | Later release. |

---

# Part 3 — Memory Catch-Up: Discussed, Implied, or Assumed Scope

This section records additional project scope that surfaced in conversation or planning but is not fully captured as a scheduled implementation item. “Built,” “stubbed,” and “absent” below describe the present repository state, not a promise of product readiness.

| Item | What it is | Origin | Current state | MVP or later release |
|---|---|---|---|---|
| **Owner onboarding flow** | A Lounge-first introduction that records only user-approved truck, skill, privacy, and preference information before entering the Shop. | Discussed and documented. | **Partially built / mostly absent:** the current safe Lounge is intentionally static; broader onboarding concepts are documented. | MVP. |
| **Lounge as persistent home base** | The Lounge is meant to be the return point for owner setup, saved work, upcoming work, and preferences. | Discussed. | **Partially built:** cold-launch shell and local sign-out state exist; full home-base data presentation is not confirmed. | MVP. |
| **Settings screen** | A consolidated place to manage vehicle, skill, Mentor, privacy, and parts preferences. | Discussed. | **Partial:** Mentor voice settings exist; a complete Lounge settings surface is not documented as built. | MVP. |
| **Mentor personality customization** | Choice of mentor voice behavior, pace, pitch, warnings-first order, torque repetition, sensitivity, ducking, and haptic preferences. | Discussed and source-present. | **Partially built:** local settings repository and dialog exist; this is not a recorded-character voice system. | MVP refinement. |
| **Mentor reasoning boundaries** | Mentor guidance should state uncertainty, favor inspection and practice, and not claim universal answers or take decisions away from the owner. | Discussed and documented. | **Partial:** source includes local guidance and evidence warnings; comprehensive enforcement is not independently verified. | MVP principle. |
| **Cross-device sync** | Owner settings, checklists, maintenance, and project progress being available across devices. | Implied by a durable personal companion. | **Absent:** no authenticated sync service is recorded. | Later release. |
| **Backup and restoration** | Owner-controlled export/backup/recovery of non-sensitive local app state. | Implied by the gift/long-lived project and explicit handoff/archive practice. | **Absent for user data:** repository/APK archival exists, but a user-facing backup flow is not recorded. | Later release. |
| **Offline-first expectation** | Core learning, local data, repair steps, and cached references should remain useful without a cloud connection. | Discussed and documented. | **Partial:** Room, local catalogs, cache entities, and local fallbacks exist; end-to-end offline behavior is not device-verified. | MVP architecture principle. |
| **Localization and language support** | User-facing translation and locale-aware learning content. | Implied by a broadly usable educational app. | **Absent:** no project-specific localization plan or translated content is recorded. | Later release. |
| **Accessibility: voice control** | Optional hands-free command input for repair situations. | Discussed. | **Partially built:** Android speech recognition and Mentor command handling exist; device accessibility testing is not recorded. | MVP accessibility aid. |
| **Accessibility: high contrast / visual clarity** | Legible repair UI with strong contrast, clear state labels, and minimal clutter. | Discussed/implied by the dark moody design and learning use case. | **Partial:** source uses dark surfaces, amber/teal status colors, and content descriptions in places; no formal contrast or assistive-tech audit is recorded. | MVP quality requirement. |
| **Accessibility: screen-reader support** | Semantic labels and navigable controls for TalkBack or other screen readers. | Implied. | **Absent as a documented feature:** no accessibility audit or acceptance evidence is recorded. | MVP quality requirement. |
| **Privacy-first data handling** | No payment storage, retailer-account storage, automatic ordering, hidden marketing enrollment, or silent microphone activation. | Explicitly discussed and documented. | **Partially enforced in source/documentation:** Mentor listening is opt-in; no checkout flow is present. A complete privacy audit is not recorded. | MVP non-negotiable. |
| **No automatic ordering** | The app may help plan and link out, but the user remains responsible for retailer checkout. | Explicitly discussed. | **Built as a boundary:** no current ordering/payment implementation is present. | MVP non-negotiable. |
| **Quality-first parts recommendations** | Default presentation should favor durable, professional-grade and warranty-backed options rather than cheapest-first results. | Explicitly discussed and documented. | **Partially built:** ranking/readiness models exist; live retail evidence is not present. | MVP. |
| **Used/remanufactured and marketplace choice** | The owner may choose used, remanufactured, local-pickup, marketplace, or other options after transparent condition/fitment review. | Explicitly discussed. | **Partially built:** preference/ranking concepts and source models exist; no live marketplace integration is recorded. | MVP planning behavior. |
| **Fitment evidence and VIN-gated decisions** | The app should distinguish known fitment from items that need VIN, capacity, trim, or configuration confirmation. | Discussed and documented. | **Partial:** pending-fitment models/records exist; no verified VIN service is present. | MVP safety/accuracy behavior. |
| **A/C heat-readiness priority** | A/C diagnosis, conservative safety language, and practice-before-service are priority learning flows. | Explicitly discussed. | **Partially built:** A/C Workbench and acoustic reference entries exist; full device validation remains pending. | MVP priority. |
| **Safety-first repair boundaries** | The app should warn before risky A/C, electrical, rotating-engine, or refrigerant work and teach before action. | Discussed and documented. | **Partial:** safety wording and rehearsal gates exist in source; no formal safety-content review is recorded. | MVP non-negotiable. |
| **Destination-style information architecture** | The product experience is conceived as Lounge, Shop, Diagnostics Bay, Part Store, and later Body Shop instead of unrelated tabs. | Discussed and documented. | **Design/partial:** current app retains tabs and safe-shell isolation; the destination redesign is not complete. | MVP navigation refinement; Body Shop later. |
| **Cinematic, dark repair-realm visual language** | The SceneView realm should use dramatic, focused lighting and a minimal dark/amber interface around the wreck. | Explicitly discussed. | **Partial:** repair-realm HUD/source structure exists, but no wreck GLB or final cinematic scene is present. | MVP visual target after asset import. |
| **Reusable vehicle/engine architecture** | Vehicles, engines, parts, camera targets, metadata, and scene nodes should be modular so the realm can support more than one vehicle later. | Explicitly discussed. | **Partial:** JSON/TSV metadata, part loader, node-alias contract, and architecture document exist. | MVP foundation; multi-vehicle expansion later. |
| **Sketchfab/third-party asset licensing discipline** | Every external 3D asset must be acquired through its actual license/download path and correctly attributed or restricted. | Discussed during asset research. | **Absent as imported assets:** research was performed, but no third-party vehicle/character/environment asset is in the repository. | MVP prerequisite for any imported asset. |
| **Rhetorician mentor representation** | The Rhetorician was considered as a mentor visual/audio asset for the realm. | Discussed. | **Absent:** no Rhetorician model or recorded voice asset is packaged; only TTS stubs/managers exist. | Later release unless a licensed asset is selected. |
| **Showroom / warehouse environment** | A workshop or showroom could frame the truck in the repair realm. | Discussed. | **Absent:** no environment model is imported. | Later release after the wreck asset. |
| **Body Shop visualization** | An imaginative layer for paint, wheels, stance, lighting, trim, interior, and body-kit concepts. | Discussed and documented. | **Absent / conceptual.** | Later release. |
| **Physical-device protocol and crash evidence** | New APKs must be tested on Ry’s actual phone, with launch observation, tab/lifecycle tests, and evidence capture for crashes or ANRs. | Explicitly discussed and documented. | **Partially performed:** cold-launch shell has a reported 10/10 result; no complete repair-realm or feature-suite device sign-off is recorded. | MVP release gate. |
| **ANR containment discipline** | Startup work must remain bounded, feature data must defer, and unsafe surfaces must remain guarded until real-device evidence supports opening them. | Explicitly discussed and documented. | **Built as current architecture:** pure Lounge, safe-shell routing, and deferred feature data are present. | MVP release gate. |
| **Logcat / diagnostic observability** | A device failure should be accompanied by the triggering action, error screenshot, and logcat when available. | Discussed. | **Absent in current record:** no captured device stack trace is recorded. | MVP validation tooling. |
| **Build/package/source ownership** | Source, deliverables, handoffs, checksums, and GitHub history should remain available to the owner. | Explicitly discussed. | **Partially built:** source is in the GitHub repository and APK deliverables/checksums have been created; a formal release process is not recorded. | MVP operational requirement. |
| **Truth-status reporting** | Every claim should distinguish source-present, sandbox-built, package-verified, device-pending, and device-verified. | Explicitly discussed after earlier trust failures. | **Documented and partially practiced:** handoff/evidence records and this inventory use status boundaries; no automated release-status system is recorded. | MVP governance requirement. |
| **External API assumptions** | Gemini may be conditionally called with a user-provided secret; retailer links may leave the app; FORScan/OBD would require a real adapter transport; 3D models require a licensed asset. | Discussed/implied and partly source-present. | **Mixed:** Gemini HTTP client exists; all other named third-party integrations remain absent or link-only. | MVP only where a real, authorized service is selected. |
| **No personal/payment data storage** | The app should avoid storing payment details, retailer credentials, and unnecessary personal information. | Explicitly discussed. | **Boundary present in docs and absence of checkout source; full privacy/security assessment not recorded.** | MVP non-negotiable. |

---

## Scope Boundaries That Must Remain Explicit

The following statements are part of the project record because they prevent false capability claims:

1. The existing procedural model is not a complete Blender/GLTF replica of the vehicle. [1]
2. The guarded SceneView repair realm is source-present, but it has no licensed wreck GLB in the repository and no device-verified inspection session. [3]
3. The FORScan screen is not a live scanner integration, and Bluetooth permissions do not create an OBD feed. [4]
4. Acoustic matching is a microphone/FFT/reference-metadata pipeline. It is not a validated diagnostic engine, does not store recordings, and does not include bundled reference audio files. [4]
5. Current Mentor narration is Android TTS. It is not a recorded Rhetorician voice library. [3]
6. Current Gemini-related code targets `gemini-3.5-flash` only when a non-placeholder key is available; it falls back to local rule-based responses. No Gemini 2.5 model identifier is present. [4]
7. The Part Store is planning and outbound-link support, not automatic ordering, payment collection, or live retail scraping. [1]
8. A successful sandbox build is not the same as physical-device verification, and a successful cold launch is not the same as verification of all feature routes. [3]

---

## References

[1]: ../PROJECT_HANDOFF.md "Ford Sport Trac Mentor App — Project Handoff Record"

[2]: ../SHOP_DESTINATION_CONCEPT.md "Sport Trac Destination Concept"

[3]: ../REPAIR_REALM_ARCHITECTURE.md "Repair Realm Architecture"

[4]: ../FOUNDATION_TRACK_CONTRACT_MAP.md "Far-End Foundation Track Contract Map"

[5]: ../ANR_RESOLUTION_REPORT.md "ANR Resolution Report"

[6]: ../DEVICE_VERIFICATION_PROTOCOL.md "Physical Device Verification Protocol"
