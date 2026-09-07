# Vehicle Repository Release Audit

Generated: 2026-09-07T00:37:36.736069+00:00

**Scope:** repository shippability of the model/app packages. This is not OEM dimensional certification, a physical-device test, or a diagnosis of a specific truck.

| Area | Level 1: structural | Level 2: evidence/scale boundary | Level 3: app/package | Release score |
|---|---:|---:|---:|---:|
| Base 4WD teaching model | 100% | 100% | 100% | **100%** |
| Corrected exterior | 100% | 100% | 100% | **100%** |
| HVAC reference | 100% | 100% | 100% | **100%** |
| Interior level 1 | 100% | 100% | 100% | **100%** |
| Next-five systems | 100% | 100% | 100% | **100%** |
| Mechanical and body | 100% | 100% | 100% | **100%** |
| Chassis and safety | 100% | 100% | 100% | **100%** |
| Hardware detail | 100% | 100% | 100% | **100%** |
| HVAC/oiling/timing release | 100% | 100% | 100% | **100%** |

**Result:** 9/9 areas meet the minimum 80% repository release threshold.

## Three-level gate definition

1. **Structural:** required GLB, Blender scene, and rendered preview exist and are non-empty; individual GLBs must also pass `tools/validate_glb.py`.
2. **Evidence/scale boundary:** the package documents meter-scale conventions where available and clearly labels reference geometry, manual-page verification limits, and VIN/physical-measurement boundaries.
3. **App/package:** the Android source integrates the relevant assets/workflows and the tested debug APK packages successfully.

## Outstanding boundary

The score does not mean the truck is 80% mechanically diagnosed or safe to drive. Actual A/C pressures, oil pressure, timing-chain condition, wiring, fastener fitment, and road safety still require measurements on the vehicle using the exact service information and qualified equipment.
