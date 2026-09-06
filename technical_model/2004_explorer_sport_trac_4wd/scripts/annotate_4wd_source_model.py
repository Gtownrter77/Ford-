import bpy
from pathlib import Path

BLEND = Path('/home/ubuntu/ford_sport_trac_repo/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_teaching_model_4wd.blend')
GLB = Path('/home/ubuntu/ford_sport_trac_repo/app/src/main/assets/models/ford_explorer_sport_trac_2004.glb')

bpy.ops.wm.open_mainfile(filepath=str(BLEND))
scene = bpy.context.scene
scene['manual_source'] = 'Local LEMON archive (2WD VIN K, 9,188 HTML pages) plus embedded Operation CHARM 4WD VIN K source package'
scene['manual_html_pages'] = 9188
scene['manual_source_boundary'] = 'The local large archive is 2WD; 4WD-specific claims require the embedded CHARM 4WD source pages.'
scene['model_scope'] = 'Teaching-grade major-system representation; dimensions are inferred or approximated where factory CAD is unavailable.'
scene['model_accuracy_status'] = 'Structural Blender validation passed; complete per-component factory dimensional reconciliation is not claimed.'
scene['fitment'] = '2004 Ford Explorer Sport Trac 4WD, V6 4.0L VIN K Flex Fuel'
scene['drive_configuration'] = '4WD transfer case, front propeller shaft, front differential, and front halfshafts represented; verify against 4WD source before field use.'

source_map = {
    '4WD transfer case': 'CHARM 4WD VIN K transfer-case plates 34-1 through 34-3; docs/SPORT_TRAC_4WD_WIRING_CATALOG.md',
    'Front differential': 'CHARM 4WD VIN K driveline source; geometry remains teaching envelope',
    'Front propeller shaft': 'CHARM 4WD VIN K driveline source; geometry remains teaching envelope',
    'Front halfshaft': 'CHARM 4WD VIN K driveline source; geometry remains teaching envelope',
    'Rear axle housing': 'Factory rear axle source; 4WD fitment must be confirmed against VIN-specific page',
    'Shock absorber': 'CHARM 4WD VIN K rear suspension specification; upper 23 N-m, lower 63 N-m',
}
for obj in bpy.data.objects:
    for token, ref in source_map.items():
        if token.lower() in obj.name.lower():
            obj['source_reference'] = ref
            obj['fitment'] = '4WD VIN K teaching reference'
            obj['geometry_status'] = 'Reference geometry; not OEM CAD'
            if 'shock absorber' in token.lower() and obj.location.x < -1.0:
                obj['service_component'] = 'rear_shock_absorber_4wd'
                obj['service_torque_upper_nm'] = 23
                obj['service_torque_lower_nm'] = 63
                obj['safety_gate'] = 'Frame on rated stands; support axle; never rely on floor jack alone'

# Correct visible legacy labels while retaining the source boundary.
for obj in bpy.data.objects:
    if obj.type == 'FONT' and obj.data.body:
        obj.data.body = obj.data.body.replace('2WD | V6 4.0L | VIN K | FLEX FUEL', '4WD | V6 4.0L | VIN K | FLEX FUEL').replace('Rear axle / 2WD', 'Rear axle / 4WD teaching reference')

# Make the source boundary visible in the scene metadata without pretending all objects are factory CAD.
for obj in bpy.data.objects:
    if obj.type == 'FONT' and obj.data.body:
        obj['source_boundary'] = 'Teaching annotation; consult source page before service.'

bpy.ops.wm.save_as_mainfile(filepath=str(BLEND))
bpy.ops.object.select_all(action='SELECT')
bpy.ops.export_scene.gltf(filepath=str(GLB), export_format='GLB', use_selection=True)
bpy.ops.wm.save_as_mainfile(filepath=str(BLEND))
print('ANNOTATION_COMPLETE', scene['manual_html_pages'], len(bpy.data.objects), GLB.stat().st_size)
