# Full Sport Trac App Shippability Audit

**Audit date:** 2026-09-02
**Repository:** [`Gtownrter77/Ford-`](https://github.com/Gtownrter77/Ford-)
**Target:** 2004 Ford Explorer Sport Trac 4WD, 4.0L VIN K Flex Fuel
**Audit standard:** Three-level verification

## Executive determination

The repository contains substantial, usable teaching content and several source packages that are verified at the repository level. The **complete Android application cannot honestly be labeled fully verified and shippable yet** because this environment cannot execute the project’s Android build: there is no Gradle wrapper in the checkout and no Android SDK/toolchain available here. The latest project evidence also explicitly withholds physical-device crash/ANR proof and live retailer checkout proof.

The most accurate status is therefore **conditionally shippable as a source repository and teaching-content package, but not release-verified as an installable Android application**.

> “Verified and shippable” in this report means that the relevant source, data, asset, and behavior evidence exists and passed the applicable checks. It does not mean that every mechanical statement is VIN-complete, every route has been tested on a physical phone, or that the app is ready for Play Store release.

## Verification levels

| Level | Meaning | Evidence used |
|---|---|---|
| **Level 1 — Source/static** | Files, identifiers, data relationships, safety text, source labels, and repository structure are internally consistent. | Kotlin/source inspection, repository validators, static scripts, `git diff --check`, test inventory. |
| **Level 2 — Source/asset integrity** | Factory-source packages, checksums, diagrams, model files, and archive contents are present and structurally valid. | CHARM package validator, full manual archive count/checksum, Blender GLB import, source-page cross-reference. |
| **Level 3 — Build/runtime** | The app compiles, tests execute, routes launch, and behavior is verified on a device or emulator. | Gradle/Android build, unit tests, instrumentation tests, physical-device logs, and route walkthroughs. |

## Section-by-section status

| Section | Level 1 | Level 2 | Level 3 | Status |
|---|---|---|---|---|
| Repository and documentation | Pass | Pass | Pass for GitHub state | **Verified/shippable as repository content** |
| Complete local factory-manual archive | Pass | Pass | Not applicable | **Verified/shippable as source archive; not 4WD-specific** |
| 4WD wiring source package | Pass | Pass | Not applicable | **Verified/shippable as an embedded source package** |
| Canonical component/data registry | Pass with provenance caveats | Partial | Blocked | **Partially verified** |
| Rear-shock teaching workflow | Pass | Pass for linked data/model | Blocked | **Partially verified; source-ready, not runtime-verified** |
| A/C diagnostic workflow | Pass based on prior feature verification | Partial source coverage | Blocked | **Partially verified** |
| 4WD drivetrain and electrical teaching data | Partial | Pass for embedded transfer-case diagram family | Blocked | **Partially verified** |
| 3D vehicle/model viewer | Pass for asset references | Pass: GLB imports | Blocked for app route | **Partially verified** |
| Parts catalog and shopping recommendations | Pass as static catalog | Pass for data integrity | Not implemented for checkout | **Verified as educational catalog only** |
| Diagnostics, mentor, voice, AR, camera, and advanced interactive tools | Source present | Varies by feature | Blocked/unverified | **Not release-verified** |
| Android navigation and launch behavior | Pass by static policy | Not applicable | Blocked | **Not shippable as a verified APK** |
| Showcase website | Previously TypeScript/build verified | Assets/checkpoint present | Not re-tested in this audit | **Previously verified; current audit does not re-open it** |

## Verified and shippable sections

### Repository state and audit artifacts

The repository is connected to the expected GitHub project, has the complete-manual archive package, the source audit index, the 4WD wiring catalog, rear-shock documentation, and repeatable validation scripts. The latest manual-archive upload was pushed in commit `fa0a60a256110701de39dfa9b1632b09c290edc2`, and its working tree was clean at the time of verification.

The archive package is stored at `docs/source_archives/ford_explorer_sport_trac_2004_manual.tar.gz`, with its README and SHA-256 file beside it. The package contains 9,188 HTML pages and 16,111 source files. The compressed file is approximately 95 MB and was accepted by GitHub, which issued a large-file advisory because it exceeds GitHub’s recommended 50 MB size.

### 4WD wiring source package

The embedded CHARM package is the strongest 4WD-specific section in the repository. `tools/validate_charm_source_package.py` passed the required-file, source-count, HTTP-status, 4WD-URL, index-reference, PNG-header, checksum, catalog-linkage, diff, provenance, and required-transfer-case-plate checks. The package includes 147 HTML source pages and 357 PNG plates, including transfer-case electrical plates 34-1, 34-2, and 34-3.

The source package is shippable as an **offline teaching reference**. It is not a claim that every exposed diagram is a terminal-by-terminal connector pinout or that every mechanical procedure is 4WD-specific. That limitation is documented in `docs/SPORT_TRAC_4WD_WIRING_CATALOG.md` and the package README.

### Rear-shock data package

The rear-shock feature is internally linked and passes `tools/validate_rear_shock.py`. The component ID, parts-catalog ID, readiness package, safety gates, and exact torque values are connected. The verified 4WD workshop values are 23 N·m (17 lb-ft) for the upper shock-to-frame nuts and 63 N·m (46 lb-ft) for the lower shock bolt. The implementation is located in `app/src/main/java/com/example/data/SportTracData.kt`, `SportTracPartsCatalog.kt`, `SportTracPartsReadiness.kt`, and `docs/REAR_SHOCK_REPLACEMENT_4WD.md`.

This section is source-ready and suitable for inclusion in the repository, but it is not Level-3 shippable until the Android build and the actual repair route are exercised on a device or emulator.

### Parts catalog as an educational reference

The parts catalog has static entries, fitment caveats, source labels, and ranking/readiness tests. It is shippable as a **teaching and research catalog**. It is not a live commerce system: prices are not guaranteed current, checkout is not implemented, and no retailer order has been completed. The dual-exhaust guide correctly identifies the factory muffler reference `5L2Z-5230-A` as a single-muffler record, not a verified dual-outlet conversion part.

### 3D GLB asset integrity

The repository model at `app/src/main/assets/models/ford_explorer_sport_trac_2004.glb` is a valid glTF binary and imports successfully through Blender 4.0.2. The current validator reports 166 mesh objects, 15 materials, non-empty geometry, and an 8,544,536-byte file. The original 4WD export at `/home/ubuntu/ford_explorer_sport_trac_2004/explorer_sport_trac_teaching_model_4wd.glb` produces the same validation result.

This verifies **file integrity and scene presence**, not complete factory dimensional accuracy. The model-build documentation states that the teaching geometry still requires manual reconciliation against each exploded-view source, object-level torque metadata, complete connector/pin data, and measured/OEM geometry. Those items remain below 100 percent.

## Sections that are not 100 percent, with exact locations

### Android build and release packaging — blocked at Level 3

The checkout has no executable `gradlew` or Gradle wrapper, and the sandbox has no Android SDK/toolchain configured for this project. The attempted commands were `./gradlew test --no-daemon --stacktrace` and `./gradlew :app:assembleDebug --no-daemon --stacktrace`; both were blocked because the wrapper is absent. The exact evidence is retained in `full_app_build_test.log`.

The repository’s own `docs/SHIP_PASS_2026-08-26.md` says that the sandbox has no Android SDK and explicitly does not claim physical-device ANR/logcat proof. Until an Android build environment runs the project, the APK is **not release-verified**.

### Physical-device and route verification — not complete

`app/src/main/java/com/example/navigation/FeatureRoutePolicy.kt` marks Lounge, Repair Manual, Maintenance, Diagnostics, Parts Cart, and View 3D as enabled, but static enablement is not runtime proof. The same file caps the safe 3D scene to eight components when a system filter is active and permits a focused component in the all-components case. That policy is a risk-containment mechanism, not evidence that every route is stable with the full data set.

The repository contains `SafeSceneLoadGate.kt`, `SafeProcedural3DRoute.kt`, `VehicleAccuracyBlockedScreen.kt`, and earlier crash-containment documentation. These indicate that route safety and 3D loading have been active engineering concerns. The exact missing evidence is a successful device/emulator walkthrough of every enabled route with logcat capture.

### 3D mechanical accuracy — incomplete despite valid GLB

The GLB passes import and geometry integrity checks, but the model is not fully factory-validated. The gap is documented in `docs/source_manual_extraction/2wd_bundle/model_build_summary.md` and `docs/BLENDER_GRAPHICS_CONTRACT.md`. Missing or incomplete items include manual reconciliation for every modeled assembly, measured/OEM geometry, complete connector/pin databases, and object-level torque metadata for all fasteners.

The 4WD correction is present in the current model export and repository placement, but the local factory archive used for much of the original extraction is labeled 2WD. Therefore the model is **4WD-labeled and structurally valid**, but not proven to be a complete VIN-specific 4WD dimensional model.

### Complete factory manual archive — complete but configuration-mismatched

The archive upload is complete as a file package, but the archive itself is titled 2WD VIN K. It cannot be silently treated as a 4WD workshop manual. The mismatch is documented in `source_notes.md`, `docs/source_archives/README.md`, and the extracted manual audit files. The 4WD-specific wiring evidence instead comes from the embedded CHARM package and its live-source provenance.

### A/C and diagnostic tools — feature evidence exists, runtime evidence is incomplete

The A/C diagnostic subsystem was previously inspected and its source/data path was verified, but this audit found no available Android build or physical-device run to independently re-prove the current route. Diagnostic and mentor components are numerous, including `AcSystemWorkbenchDialog.kt`, `DiagnosticWizardDialog.kt`, `SymptomTroubleshootingDialog.kt`, and `MentorKnowledgeTest.kt`. Their presence and tests do not establish that every dialog opens without a crash on the target APK.

### 4WD drivetrain teaching claims — source coverage is uneven

The repository contains 4WD transfer-case, driveshaft, differential, hub, and wiring data. The 4WD transfer-case diagram family is source-verified. However, `source_notes.md` documents that several repository torque strings were originally stored without units and required exact workshop-page verification. Any such value not explicitly linked to a verified 4WD source page must remain a teaching-index value rather than a factory specification.

The 4WD electrical package is more strongly verified than the complete mechanical drivetrain data. The drivetrain data should be classified as **partially verified** until every torque and fitment statement is paired with a 4WD VIN K source.

### Dual-exhaust conversion — custom fabrication, not a factory-shippable procedure

`docs/CUSTOM_DUAL_EXHAUST_GUIDE.md` is a source-first guide, but the manual archive does not document a factory conversion from one rear tailpipe to two. The archive documents the dual-converter Y-pipe, dual catalytic converters, muffler/resonator assembly, factory muffler service, exhaust alignment, and 40 N·m factory fasteners. It does not provide a verified dual-outlet aftermarket part number or 4WD-specific custom-tailpipe dimensions.

Accordingly, the dual-exhaust section is shippable as **annotated fabrication guidance**, not as a guaranteed bolt-on parts kit. The final route requires vehicle-specific measurement, heat shielding, suspension-travel checks, fuel-tank clearance checks, leak testing, and emissions/legal review.

### Live commerce, OBD, FORScan, and community media — not implemented or not verified

The repository’s own shipment document lists live OBD/FORScan sessions, live retailer checkout, licensed wreck GLB provenance, and VIN-verified community videos as unclaimed. These are not defects in the offline teaching core, but they prevent the app from being described as a complete live diagnostic, purchasing, or media platform.

## Three-level verification record

### Level 1 — Source/static: passed with documented caveats

The repository inventory, route policy inspection, component and parts linkage checks, rear-shock validator, CHARM package validator, source-note review, and whitespace checks completed. The static scan also identified the expected safety and provenance caveats rather than silently treating placeholders or approximate geometry as factory truth.

### Level 2 — Source/asset integrity: passed for packages, partial for mechanical completeness

The complete archive checksum passed. The CHARM package validator passed with zero missing index references and zero non-200 category responses. All three transfer-case plates are present. Blender 4.0.2 imported both the repository GLB and the original 4WD GLB and reported valid mesh geometry and materials.

Level 2 does not certify that every model component is dimensionally factory-accurate or that the 2WD local archive is interchangeable with a 4WD manual. Those remain explicit limitations.

### Level 3 — Build/runtime: blocked, so the app is not fully release-shippable

The Android build and tests could not be run because the Gradle wrapper and Android SDK are absent in the current environment. No fresh device/emulator route walkthrough or logcat record was produced during this audit. This is the decisive reason the **entire app** cannot be marked 100 percent verified and shippable.

## Required actions to reach 100 percent

| Priority | Required action | Exact area |
|---:|---|---|
| 1 | Provide or restore the Gradle wrapper, Android SDK, and project build configuration, then run unit, instrumentation, and debug-APK builds. | Repository root; `app/`; `full_app_build_test.log` currently records the blocker. |
| 2 | Walk every enabled tab and high-risk dialog on an emulator or physical device with logcat capture. | `FeatureRoutePolicy.kt`, all files under `ui/screens/` and `ui/components/`. |
| 3 | Reconcile all 3D assemblies against 4WD VIN K exploded views and record dimensions/torques as object metadata. | `app/src/main/assets/models/`; `docs/source_manual_extraction/2wd_bundle/model_build_summary.md`. |
| 4 | Pair every mechanical torque and fitment statement with a verified 4WD VIN K source page. | `SportTracData.kt`, `SportTracPartsCatalog.kt`, `source_notes.md`. |
| 5 | Add a real 4WD-specific exhaust-routing survey before presenting dual exhaust as a parts kit. | `docs/CUSTOM_DUAL_EXHAUST_GUIDE.md`; new 4WD source and measurement records. |
| 6 | Keep commerce, OBD/FORScan, community video, and licensed-model claims explicitly disabled or labeled as unverified until implemented. | Parts, diagnostic, media, and model UI. |

## Final classification

The **verified and shippable repository portions** are the embedded 4WD wiring source package, the complete uploaded manual archive as a source artifact, the rear-shock data linkage and documentation, the static parts/readiness catalog, the source audit scripts, and the structurally valid 4WD GLB asset.

The **not-100-percent portions** are the Android build and runtime, physical-device route behavior, complete VIN-specific mechanical accuracy of the 3D model, full 4WD verification of every mechanical data value, the custom dual-exhaust fitment, live commerce, live OBD/FORScan, and unverified media/licensing claims. Their exact locations are listed above.

The project should therefore be described as **a substantial, source-annotated vocational teaching repository with several verified offline packages—not yet a fully release-verified Android application**.
