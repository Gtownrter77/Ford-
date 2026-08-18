# Ford Sport Trac Mentor App — Project Handoff Record

**Last updated:** August 17, 2026  
**Project location:** `/home/ubuntu/ford-sport-trac`  
**Vehicle target:** 2004 Ford Explorer Sport Trac, 4.0L SOHC V6, 4WD  
**Purpose:** A gift that lets the owner learn, practice, diagnose, plan, and repair with guidance that still feels personal when family cannot be present.

> **This document is a factual handoff record.** It separates what is in the source and validated from what is proposed or still unfinished. Do not turn an item from “pending” into “complete” without validating it.

## 1. The user’s design direction

The app is not meant to be an ordinary parts catalog. The two centerpieces are the **to-scale educational 3D Ford Sport Trac model** and the **Mentor**. The model is the place to practice the repair first. The Mentor is the voice that helps the person think through the job, particularly when the pre-filled catalog does not already have the exact answer.

The user described the Mentor correctly as a **“travel agent for parts.”** It should have a conversation, listen to the repair goal, symptoms, budget, available tools, skill/confidence, timeline, and preferences, then deliver a sensible route: what to inspect, what to practice on the 3D model, what part/tool options fit the choice, what requires VIN or capacity verification, and where the person may choose to shop. It must guide; it does not take the decision away from the customer.

| Design requirement | Meaning in the app |
|---|---|
| **Practice first** | The 3D model is used to learn the steps, component location, hardware, and safety boundaries before work begins on the truck. |
| **Mentor is the focal point** | Mentor Mode, voice guidance, and the 3D model carry more importance than the shopping list. The catalog is a useful starting template, not the intelligence of the app. |
| **A/C is urgent** | A/C and extreme-heat readiness are the first-priority repair path. The app includes an A/C Workbench and A/C sound-reference patterns. |
| **Pre-filled abundance template** | The Part Store begins with common wear items and readiness packages that sell abundantly: ignition, brakes, wheel/hub, cooling, A/C, filters, electrical, drivetrain, visibility/body, and related supplies. |
| **Mentor handles the rest** | Uncommon, specialty, configuration-dependent, or unclear needs are handled through Mentor guidance and fitment verification—not by inventing a generic part number. |
| **Quality-first default** | The starting view should favor durable, professional-grade, built-for-life choices and the strongest recorded backing/warranty. It is not a “cheapest first” recommendation. |
| **Customer keeps control** | A customer can choose premium, warranty, low delivered total, local pickup, marketplace/used, American Made, or other fit/taste/budget preferences. A person may rationally choose remanufactured or used parts, including used brake pads, after considering condition, fitment, warranty, core charge, and safety. |
| **No shame / no blocking** | The app makes safety-critical condition checks visible, but it does not block, shame, or override the customer’s chosen route. |
| **Privacy and no auto-ordering** | No stored retailer account, payment information, automatic order, simulated purchase, push notification, email, or text alert. Links take a customer to a retailer’s own page for any checkout. |
| **Reputable affiliates are acceptable** | Preferred brands and reputable affiliates may be featured and may earn revenue. The relationship must be plainly identified, and sponsorship must not create false fitment, safety, warranty, or origin claims. |
| **American Made is item-level** | Only verified item-level “Made in USA” evidence may be shown as verified. Brand headquarters, a U.S. factory, or a mixed-origin catalog is not enough. |

## 2. What is actually present now

### 2.1 Source-verified 3D model coverage

The current `SportTracData.kt` source contains **56 modeled component records** grouped across the systems below. This is substantial coverage, but it is **not a representation that every bolt, every specialized subpart, every model year variation, or every repair procedure is complete**.

| Current modeled system | Component records | Examples currently represented |
|---|---:|---|
| Electrical | 12 | Alternator/coil pack, engine and dash harnesses, fuse box, windshield/wipers, rear glass, sunroof pieces, SRS/airbag components. |
| Air conditioning | 8 | Compressor/clutch, heater core, blower/resistor, blend door actuator, evaporator/accumulator/orifice tube, condenser/lines, service ports/controls. |
| Brakes and chassis | 8 | Front brakes/torsion-bar suspension, ABS master cylinder/pump, rear brakes/parking brake, wheels/tires, hub/bearing, control arms/ball joints, steering rack. |
| Interior/dash | 6 | Instrument cluster, center stack, steering/airbag column, overhead console, Mach 500 radio, door speakers/subwoofer. |
| Engine | 5 | Engine block/heads, exhaust manifolds/Y-pipe, cat-back, oxygen sensors, valvetrain. |
| Air intake / fuel | 3 | Intake manifold, throttle body/MAF, fuel tank/pump module. |
| Cooling | 3 | Thermostat housing, radiator/fan clutch, water pump. |
| Transmission | 3 | 5R55E transmission, valve-body solenoids/filter, transfer case/driveshafts. |
| Drivetrain / 4WD | 3 | Transfer-case shift motor, rear driveshaft/U-joints, Ford 8.8 differential. |
| Roof / rear glass | 3 | Power sunroof, sunroof motor/drive, power drop-down rear window. |
| Lighting/body | 2 | Headlight/fog assemblies and tail/reverse lamp assemblies. |

The visual coverage chart is saved at:

- `/home/ubuntu/deliverables/ford-sport-trac-current-model-coverage.png`

### 2.2 Existing instructional and diagnostic capabilities

| Capability | Current status | Notes |
|---|---|---|
| To-scale 3D component model | Present | The source uses model components, locations, subassemblies, repair steps, torque details, and service hardware. |
| Detailed service hardware | Present | Procedural meshes cover threaded hex bolts, Torx screws, washers, seals, belts, spark plugs, and hardware catalog enrichment. |
| Mentor Mode | Present | Component-specific dialog with hands-free TTS, voice commands, step progression, torque readout, mute/settings, and saved repair checklist state. |
| A/C Workbench | Present | Six symptom paths, practice-first rehearsal, safety boundaries, 3D component links, and Part Finder handoff. |
| Acoustic comparison | Present | Live-recording workflow is kept separate from simulated references; it includes conservative A/C compressor/pulley, clutch-cycling, and belt-squeal pattern references. |
| Part Store / readiness | Present | Six readiness packages cover the 55-entry catalog plus a pending-fitment queue for items that need capacity/VIN/trim confirmation. |
| Weekly price-watch controls | Present | Saved schedule/control design for O’Reilly, RockAuto, Amazon, eBay, Facebook Marketplace, and other online sources. The source does not perform automatic purchasing. |
| Readiness dashboard | Present | In-app dashboard for weekly review, maintenance windows, seasonal A/C preparation, and fitment queue alerts. No push/email/text notifications. |
| Retailer comparison links | Present | Generates search links by exact part number. Prices are saved/reference records, not live scraped quotes. |
| Recommendation tests | Present | Unit-test coverage for quote integrity includes fitment outranking a cheaper unverified listing and preservation of an “other online” route. |

### 2.3 Current Part Store state

The source contains the private, no-checkout Part Store update. It includes a customer-controlled catalog ranking selector and origin-record display on each part. The default ranking enum has been changed in source to **“Built for life”**, meaning durable professional-grade choices with the strongest recorded warranty first and premium brands as a tie-breaker.

The available preference types in source are:

| Customer preference | Purpose |
|---|---|
| Built for life | Default quality/warranty-led view. |
| Best verified fit | Fitment/evidence first. |
| Lowest delivered total | Saved price, shipping, and core charge comparison. |
| Fastest availability | Source availability / local fields where recorded. |
| Best warranty | Warranty evidence first. |
| Local pickup | Known local-pickup availability first. |
| Marketplace / used | Marketplace and used-listing route, with condition and seller review. |
| American Made | Verified U.S.-origin claim first; assembled/unknown claims remain distinct. |

The user explicitly wants the app to support remanufactured and used choices when that is the buyer’s preference. For safety-critical parts, the app should state the condition checks and facts; the decision remains the customer’s.

## 3. What is incomplete or needs correction

This section is important. These are **not completed features**.

| Item | Honest status | Required next action |
|---|---|---|
| Full “travel agent for parts” conversation | **Not built yet.** Mentor Mode teaches an already selected component well, but it does not yet intake budget, tools on hand, urgency, preference, symptoms, and goal in one conversation, then produce a personalized route. | Add a guided conversation intake and route planner that hands off to the right existing 3D component and Mentor Mode. |
| Universal repair coverage | **Not complete.** The model has broad coverage but not every specialty repair, every fastener, every trim variation, or every OEM part path. | Add systems and detail in priority order, starting with real user repair paths. |
| Live price scraping / live commercial pricing | **Not built.** Current catalog prices are saved/reference values. | Only add live data through authorized retailer APIs or user-entered quote evidence, never by pretending reference prices are live. |
| Customer ranking selector inside each quote dialog | **Partly pending.** Ranking models and comparator exist; the detailed dialog wiring and American Made quote-level field work were listed as next steps before this handoff. | Wire the preference selector through `PriceWatchDialog`, add verified origin to quote records, and test it. |
| Pending-fitment queue display | **Partly pending.** The data model exists but per-package collapsible display needed verification/implementation. | Show VIN/capacity/trim lookup items clearly in the Readiness Dashboard. |
| Preferred-brand sponsor placements | **Not implemented.** Policy and research exist, but no real affiliate account, tracking tag, or sponsor placement has been added. | Obtain an actual partner agreement first; make the placement plainly labeled. |
| Latest APK after startup, renderer, and A/C ID hardening | **Packaged and sandbox-verified.** Automatic initialization runs on `Dispatchers.IO`, the Canvas uses automatic rather than forced offscreen compositing, and the duplicate A/C component ID was corrected. `:app:testDebugUnitTest` and `:app:assembleDebug` passed afterward. No physical-device verification is available. | Install and exercise `/home/ubuntu/deliverables/ford-sport-trac-ac-id-fix-debug.apk` on Ry’s phone; capture logcat if the ANR or close persists. |
| Duplicate A/C component ID | **Corrected in source.** The actual compressor remains `ac_compressor`; the separate compressor/pressure-control record is now `ac_compressor_pressure_controls`, and the source scan reports no duplicate component IDs. | Verify affected Mentor/deep-link routes on a real device. |

## 4. Recovery and launch-failure evidence

The source inspection found that `ExplorerViewModel` automatically launches maintenance initialization, acoustic-database seeding, and offline-cache seeding from `init`. The original coroutine used the default `viewModelScope` dispatcher. The offline cache method internally switches to `Dispatchers.IO`, but the other initialization and orchestration work could begin on the main thread. The minimal fix changes only that init launch to `viewModelScope.launch(Dispatchers.IO)`. It does not remove the model, change the navigation default, submit orders, or store payment data.

The current default tab remains `MainTab.VIEW_3D`. The first frame uses a procedural Compose Canvas renderer and receives the full 56-component source registry. The Canvas compositing strategy was changed from forced offscreen to automatic after source inspection found no blend-mode or save-layer requirement. Generated subassembly meshes now use their stored metallic and roughness fields through the tested `MaterialResponse` utility to shape a conservative highlight approximation. The Canvas projected-center cache no longer writes snapshot state on every draw; it keeps the latest centers in a remembered mutable holder for tap hit-testing. The A/C Workbench practice flow now requires a per-step explicit `Mark 3D Step Rehearsed` acknowledgment before the user can advance. This remains an unproven device-performance risk and is not true GPU PBR. The expanded source regression suite passes; physical-device behavior remains unverified. The sandbox has no `adb` command or emulator, so the phone-reported ANR/closing behavior remains unverified by device logcat.

## 5. Build and delivery status

The restored Android project uses Kotlin/Compose, Android API 36, Gradle 9.3.1, Java 21, and the local Android SDK.

| Artifact / status | Location or detail |
|---|---|
| Latest full sandbox-validated Android build | `:app:assembleDebug :app:testDebugUnitTest` finished successfully after the IO-dispatch fix on 2026-08-18. |
| Latest built APK | `/home/ubuntu/ford-sport-trac/app/build/outputs/apk/debug/app-debug.apk` (SHA-256: `ff38e34d80b05305749093726301d1ed417959a7d84ffd046b33c9ce42dfb97a`) |
| Current packaged A/C practice-gate APK | `/home/ubuntu/deliverables/ford-sport-trac-ac-practice-gate-debug.apk` (APK signature v2/v3 verified; ZIP integrity verified; SHA-256: `661046972dc18cf5b2d5b7974b66555e781b3c503757b49af6849149fd839a91`) |
| Previous packaged projected-center hardening APK | `/home/ubuntu/deliverables/ford-sport-trac-projected-center-hardening-debug.apk` (APK signature v2/v3 verified; ZIP integrity verified; SHA-256: `e61a2eb5d10d4870eac9d82dd58e6c9b356e2ca5ac3d944a41747116becf3cae`) |
| Previous packaged regression-covered APK | `/home/ubuntu/deliverables/ford-sport-trac-regression-covered-debug.apk` (APK signature v2/v3 verified; ZIP integrity verified; SHA-256: `885c5da5b6890b922be978c0daab994c789fcdb92a554b3af8c1d707c36df0f8`) |
| Previous packaged material-aware APK | `/home/ubuntu/deliverables/ford-sport-trac-material-aware-debug.apk` (APK signature v2/v3 verified; ZIP integrity verified; SHA-256: `b33bac82ada3c758bdd60e958dd71347448fd64660ca16fa766288f9be0adc33`) |
| Previous packaged A/C ID-fix APK | `/home/ubuntu/deliverables/ford-sport-trac-ac-id-fix-debug.apk` (APK signature v2/v3 verified; ZIP integrity verified; SHA-256: `dff762949f83688074ed605609e90a27c2daacef38f6442335b35c3cfe7c0454`) |
| Earlier verified A/C source checkpoint | `/home/ubuntu/deliverables/ford-sport-trac-ac-training-checkpoint.zip` |
| Latest current source | `/home/ubuntu/ford-sport-trac/` |
| No local Git metadata | The restored checkout does not contain `.git`; pushing to GitHub needs to be done from Android Studio or after re-establishing a remote repository. |

## 6. Key project files

| File | Why it matters |
|---|---|
| `app/src/main/java/com/example/data/SportTracData.kt` | Main 3D vehicle component catalog and repair data. |
| `app/src/main/java/com/example/data/VehicleHardwareCatalog.kt` | Service hardware added across assemblies. |
| `app/src/main/java/com/example/data/SportTracPartsReadiness.kt` | Readiness packages and pending-fitment queue. |
| `app/src/main/java/com/example/data/SportTracPartsCatalog.kt` | 55-entry parts catalog with reference price records. |
| `app/src/main/java/com/example/data/PartStoreCatalogRanking.kt` | Premium/warranty and customer-preference catalog sorting. |
| `app/src/main/java/com/example/model/PartStoreRanking.kt` | Customer ranking types and origin claim labels. |
| `app/src/main/java/com/example/model/RecommendationIntegrity.kt` | Evidence-based quote comparison safeguards. |
| `app/src/main/java/com/example/ui/components/MentorModeDialog.kt` | Existing voice/TTS repair Mentor for a chosen model component. |
| `app/src/main/java/com/example/ui/components/AcSystemWorkbenchDialog.kt` | Priority A/C diagnostic and practice flow. |
| `app/src/main/java/com/example/ui/components/PriceWatchDialog.kt` | Quote evidence and weekly watch controls. |
| `app/src/main/java/com/example/ui/components/ReadinessDashboardDialog.kt` | In-app readiness dashboard. |
| `app/src/main/java/com/example/ui/screens/PartsShoppingScreen.kt` | Private Part Store UI and current catalog controls. |
| `app/src/test/java/com/example/model/RecommendationIntegrityTest.kt` | Recommendation integrity release-gate unit test. |
| `app/src/test/java/com/example/data/PartStoreCatalogRankingTest.kt` | Premium-first and verified-origin ranking test coverage. |
| `RECOMMENDATION_INTEGRITY.md` | Current recommendation-ranking rules. |
| `FUTURE_AFFILIATE_POLICY.md` | Future sponsor/affiliate guardrails. This needs wording alignment with the user’s explicit acceptance of reputable affiliates and visible preferred placements. |
| `research/Tool_Sponsorship_Shortlist.md` | Published sponsorship/affiliate research and outreach pitch. |

## 7. Immediate next steps—only if the owner wants them

1. **Install and exercise the post-fix APK on the physical phone.** The IO-dispatch mitigation is sandbox-built and package-verified, but not device-verified.
2. **Capture Android logcat if the ANR or close persists.** The remaining leading source risk is the full procedural 3D first frame; do not guess further without runtime evidence.
3. **Resolve the duplicate A/C component ID.** This is important before routing Mentor sessions by component ID.
4. **Build the Mentor Travel Agent in small, observable stages.** Start with a guided intake for repair goal, urgency, budget, tools on hand, and experience; have it choose from existing 3D components first. Do not pretend it understands every possible repair from day one.
5. **Finish the quote/ranking UI.** Connect the customer dropdown, warranty evidence, verified item-origin status, marketplace/used context, and fitment queue to the dialogs that already have data models.
6. **Only add sponsors after an actual agreement.** Label them visibly and do not add affiliate tracking or claim benefits that are not negotiated.
7. **Make a new checkpoint ZIP and APK after validation.** Attach both to the next delivery so this record and the source can be recovered easily.

## 8. Retrieval guide

If this environment is interrupted or another person takes over, start here:

1. Read this file: `/home/ubuntu/ford-sport-trac/PROJECT_HANDOFF.md`.
2. Read the current code and the release notes in `/home/ubuntu/deliverables/`.
3. Confirm the source’s current build state with the known Gradle command below.
4. Do not say the whole model is complete. Use the “Incomplete or needs correction” table above.
5. Preserve the user’s core rule: **quality and warranty are the default; the customer decides if price, used, remanufactured, local pickup, marketplace, or another preference better fits their situation.**

```bash
cd /home/ubuntu/ford-sport-trac
ANDROID_HOME=/home/ubuntu/android-sdk \
ANDROID_SDK_ROOT=/home/ubuntu/android-sdk \
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 \
/home/ubuntu/tools/gradle-9.3.1/bin/gradle \
  --no-daemon --no-configuration-cache --max-workers=2 \
  -Dorg.gradle.jvmargs='-Xmx2g -Dfile.encoding=UTF-8' \
  --console=plain :app:testDebugUnitTest :app:assembleDebug
```

## 9. Handoff commitment

Every material instruction from the owner should be recorded in this document or a dated handoff update before a session ends. The next person must be able to tell the difference between a working source feature, a validated APK feature, a researched idea, and an unbuilt promise.

## Physical-device verification protocol

The exact test procedure for the current APK is documented in `PHYSICAL_DEVICE_VERIFICATION_PROTOCOL.md`. It requires checksum verification for `ford-sport-trac-projected-center-hardening-debug.apk`, clean installation, `adb` device confirmation, cold-launch timing, first-frame and 3D interaction checks, A/C/Mentor/Part Store smoke checks, and logcat/bugreport capture for any ANR, crash, or repeated restart. The protocol defines a device-verified launch pass without claiming that a successful launch proves complete vehicle-model coverage, true GPU PBR, or physical repair accuracy.

## Documentation-only reconciliation — 2026-08-18

This reconciliation updates the record without running a build, packaging an APK, refreshing an archive, or changing application source code.

The current project work is organized into four separately labeled AI workstreams: **Primary/Mentor**, **Foundation**, **A/C Workbench**, and **Parts/Commerce**. These are operating lanes within the same project, not four persistent human agents or hidden autonomous participants.

The Primary/Mentor source change makes microphone listening opt-in rather than automatic when Mentor opens. The Foundation track added an evidence-aware readiness contract and domain catalog. The A/C Workbench has a per-step `Mark 3D Step Rehearsed` gate before practice advancement. The Parts/Commerce track added a pure `PartListingTruth` summary that keeps saved catalog prices, live-authorized quotes, fitment evidence, seller identity, and no-ordering behavior distinct.

The most recent completed sandbox build before this documentation hold was the Mentor opt-in revision. It passed the recorded Gradle build and unit-test task, and its separately packaged APK passed v2/v3 signature and ZIP integrity checks. A later combined build was started for the Foundation and Parts/Commerce additions, but this documentation pass does not rerun or certify that build. The build is currently held by authorization.

The physical-device state remains unchanged: no `adb`, emulator, phone logcat, bugreport, gfxinfo capture, launch result, ANR result, or frame-rate result is recorded in this environment. The app must therefore remain described as source-present, sandbox-built for the recorded artifacts, unit-tested for the recorded runs, and package-verified where explicitly documented—not physically verified or production-ready.

The complete pending-task list is recorded in `PENDING_TASKS_2026-08-18.md`. The four-stream operating boundaries are recorded in `FOUR_WORKSTREAM_OPERATING_PLAN.md`, and the side-by-side workflow view is recorded in `FOUR_STREAM_SPLIT_SCREEN.md`.
