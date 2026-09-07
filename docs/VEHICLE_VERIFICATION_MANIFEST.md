# 2004 Ford Explorer Sport Trac 4WD VIN-K Verification Manifest

**Target configuration:** 2004 Ford Explorer Sport Trac, 4.0L SOHC V6, 4WD, VIN K Flex Fuel. **Purpose:** trace each repository release area to the available dimensional, workshop, modeling, and application evidence. This manifest is a release-control document; it is not a Ford license, a physical scan, a roadworthiness certificate, or a diagnosis of a particular truck.

## Verification levels

| Level | Meaning | Pass condition |
|---|---|---|
| **L1 — Structural** | Digital artifact integrity | The mapped GLB, Blender scene, and preview exist; the GLB passes the repository validator. |
| **L2 — Configuration/evidence** | Configuration and evidence traceability | The area is mapped to the VIN-K/4WD target, scale convention, and at least one documented source or explicit evidence boundary. |
| **L3 — Application/release** | App/package readiness | The area is represented in the Android asset/workflow package and the current unit-test/debug-APK gate passes. |

## Area manifest

| Area | Repository artifact | Configuration/evidence anchor | Remaining physical/manual check |
|---|---|---|---|
| Base 4WD teaching model | `ford_explorer_sport_trac_2004.glb`; teaching-model `.blend` | `technical_model/.../README.md`; `docs/SPORT_TRAC_OG_SCALE.md`; dimensions from the P207 Owner Guide table | Confirm the individual truck’s VIN, tire/wheel package, ride height, and body condition. |
| Corrected exterior | `ford_explorer_sport_trac_2004_corrected_exterior.glb` | Owner Guide dimensions and the corrected-exterior build script; reference geometry is not a licensed body scan | Compare body panels, trim, bumper, lamps, and bed hardware against the actual truck or validated scan. |
| HVAC reference | `ford_explorer_sport_trac_2004_hvac_complete_reference.glb` | `docs/2004_SPORT_TRAC_CHARM_WORKSHOP.md` HVAC links; A/C Workbench safety workflow | Recover/test refrigerant with approved equipment; verify compressor, pressures, leak path, heater flow, and blend/mode operation. |
| Interior Level 1 | `ford_explorer_sport_trac_2004_interior_level1.glb` | Sport Trac component registry and interior build script | Confirm trim level, connector routing, switch variants, and fastener locations on the truck. |
| Next-five systems | `ford_explorer_sport_trac_2004_next5_systems.glb` | VIN-K/4WD package, systems build script, and repository reference catalog | Verify engine-option, transmission, transfer-case, cooling, fuel, electrical, and lighting variants. |
| Mechanical/body | `ford_explorer_sport_trac_2004_next5_mechanical_body.glb` | `docs/2004_SPORT_TRAC_CHARM_WORKSHOP.md` repair links and model source registry | Confirm installed parts, wear, clearances, corrosion, and service history before ordering or repair. |
| Chassis/safety | `ford_explorer_sport_trac_2004_next5_chassis_safety.glb` | 4WD workshop reference, brake/steering/safety data, and safety boundary notes | Measure brake, steering, wheel-bearing, suspension, tire, restraint, and frame condition in person. |
| Hardware detail | `ford_explorer_sport_trac_2004_next5_hardware_detail.glb` | Hardware catalog, washer/seal/connector build script, meter-scale convention | Verify thread pitch, grade, length, torque, seal material, connector keying, and corrosion against the exact manual/part. |
| HVAC/oiling/timing release | `ford_explorer_sport_trac_2004_hvac_oil_timing_release.glb` | Critical diagnostic protocol; HVAC/oiling/timing release script; VIN-K/4WD workshop links | Measure oil pressure mechanically, test A/C pressures/leaks, and inspect timing guides/tensioners/marks before diagnosis or driving. |

## Source hierarchy and boundaries

1. **Vehicle configuration:** the target is VIN K, 4.0L SOHC, 4WD. A different VIN, engine, drivetrain, wheel package, or later repair supersedes generic assumptions.
2. **Dimensional reference:** the repository uses meters and a documented P207 Owner Guide dimension table. The reference hull is educational geometry, not a licensed CAD scan or photogrammetry capture.
3. **Workshop evidence:** the repository links the 2004 Sport Trac 4WD VIN-K workshop source package and records where 2WD links must not be substituted. Torque, capacities, connector pinouts, and service values must be checked against the exact applicable manual page before field use.
4. **Diagnostic evidence:** model confidence scores and symptom matches prioritize tests; they do not prove a failed part. Mechanical gauge, pressure, scan, leak, temperature, timing-mark, and physical inspection results outrank a visual match.
5. **Release status:** digital artifacts are release-ready when the three repository gates pass. Physical-truck safety remains unverified until the vehicle is inspected and measured.

## Current release statement

The manifest is intended to be consumed with `RELEASE_AUDIT.md` and `release_audit.json`. The current audit establishes that all mapped digital areas meet the repository’s minimum 80% release threshold. It does **not** authorize driving a truck with an oil-pressure warning, overheating, active leak, smoke, or severe timing-chain noise; those conditions require engine-off and tow escalation.
