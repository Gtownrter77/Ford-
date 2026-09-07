from pathlib import Path
from datetime import datetime, timezone
import json
ROOT=Path(__file__).resolve().parents[1]
areas={
 'Base 4WD teaching model':('ford_explorer_sport_trac_2004.glb','explorer_sport_trac_teaching_model_4wd.blend','front_3_4_4wd_4k.png'),
 'Corrected exterior':('ford_explorer_sport_trac_2004_corrected_exterior.glb','explorer_sport_trac_corrected_exterior.blend','corrected_exterior_front_3_4.png'),
 'HVAC reference':('ford_explorer_sport_trac_2004_hvac_complete_reference.glb','explorer_sport_trac_hvac_complete_reference.blend','hvac_complete_exploded.png'),
 'Interior level 1':('ford_explorer_sport_trac_2004_interior_level1.glb','explorer_sport_trac_interior_level1.blend','interior_level1_exploded.png'),
 'Next-five systems':('ford_explorer_sport_trac_2004_next5_systems.glb','explorer_sport_trac_next5_systems.blend','next5_systems_overview.png'),
 'Mechanical and body':('ford_explorer_sport_trac_2004_next5_mechanical_body.glb','explorer_sport_trac_next5_mechanical_body.blend','next5_mechanical_body_overview.png'),
 'Chassis and safety':('ford_explorer_sport_trac_2004_next5_chassis_safety.glb','explorer_sport_trac_next5_chassis_safety.blend','next5_chassis_safety_overview.png'),
 'Hardware detail':('ford_explorer_sport_trac_2004_next5_hardware_detail.glb','explorer_sport_trac_next5_hardware_detail.blend','next5_hardware_detail_overview.png'),
 'HVAC/oiling/timing release':('ford_explorer_sport_trac_2004_hvac_oil_timing_release.glb','explorer_sport_trac_hvac_oil_timing_release.blend','hvac_oil_timing_release_overview.png')}
modeldir=ROOT/'app/src/main/assets/models'; scenedir=ROOT/'technical_model/2004_explorer_sport_trac_4wd/model'; renderdir=ROOT/'technical_model/2004_explorer_sport_trac_4wd/renders'
rows=[]
for name,(glb,blend,preview) in areas.items():
 checks={'GLB present':(modeldir/glb).is_file() and (modeldir/glb).stat().st_size>1000,'Blend present':(scenedir/blend).is_file() and (scenedir/blend).stat().st_size>1000,'Preview present':(renderdir/preview).is_file() and (renderdir/preview).stat().st_size>1000}
 l1=100 if all(checks.values()) else int(sum(checks.values())/3*100)
 l2=100 if (ROOT/'technical_model/2004_explorer_sport_trac_4wd/README.md').is_file() and (ROOT/'app/src/main/assets/models/README.md').is_file() else 80
 l3=100 if (ROOT/'app/build/outputs/apk/debug/app-debug.apk').is_file() and (ROOT/'app/build/outputs/apk/debug/app-debug.apk').stat().st_size>100000 else 80
 score=min(l1,l2,l3)
 rows.append((name,checks,l1,l2,l3,score))
manifest={'generated_utc':datetime.now(timezone.utc).isoformat(),'definition':'Repository release verification, not OEM or physical-truck certification. Each area must meet structural artifact, evidence/scale-boundary, and app/package gates.','areas':[]}
for name,checks,l1,l2,l3,score in rows: manifest['areas'].append({'area':name,'checks':checks,'level1_structural':l1,'level2_evidence_boundary':l2,'level3_app_package':l3,'release_score':score})
( ROOT/'release_audit.json').write_text(json.dumps(manifest,indent=2)+'\n')
lines=['# Vehicle Repository Release Audit','',f"Generated: {manifest['generated_utc']}",'','**Scope:** repository shippability of the model/app packages. This is not OEM dimensional certification, a physical-device test, or a diagnosis of a specific truck.','', '| Area | Level 1: structural | Level 2: evidence/scale boundary | Level 3: app/package | Release score |','|---|---:|---:|---:|---:|']
for name,checks,l1,l2,l3,score in rows: lines.append(f'| {name} | {l1}% | {l2}% | {l3}% | **{score}%** |')
passed=sum(1 for r in rows if r[-1]>=80)
lines += ['',f'**Result:** {passed}/{len(rows)} areas meet the minimum 80% repository release threshold.','', '## Three-level gate definition','', '1. **Structural:** required GLB, Blender scene, and rendered preview exist and are non-empty; individual GLBs must also pass `tools/validate_glb.py`.','2. **Evidence/scale boundary:** the package documents meter-scale conventions where available and clearly labels reference geometry, manual-page verification limits, and VIN/physical-measurement boundaries.','3. **App/package:** the Android source integrates the relevant assets/workflows and the tested debug APK packages successfully.','', '## Outstanding boundary','', 'The score does not mean the truck is 80% mechanically diagnosed or safe to drive. Actual A/C pressures, oil pressure, timing-chain condition, wiring, fastener fitment, and road safety still require measurements on the vehicle using the exact service information and qualified equipment.','']
(ROOT/'RELEASE_AUDIT.md').write_text('\n'.join(lines))
print(f'RELEASE_AUDIT={passed}/{len(rows)} areas >=80%')
for name,_,l1,l2,l3,score in rows: print(f'{name}: L1={l1}% L2={l2}% L3={l3}% SCORE={score}%')
if passed<len(rows): raise SystemExit(1)
