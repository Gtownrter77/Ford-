import bpy, os
path='/home/ubuntu/ford_explorer_sport_trac_2004/explorer_sport_trac_teaching_model_4wd.blend'
scene=bpy.context.scene
scene['manual_source']='LEMON Manuals bundle: 2004 Ford Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel'
scene['fitment']='4WD, V6 4.0L VIN K Flex Fuel'
bpy.ops.wm.save_as_mainfile(filepath=path)
print('4WD_METADATA_FIXED')
