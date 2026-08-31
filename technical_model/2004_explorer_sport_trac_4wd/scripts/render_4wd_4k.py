import bpy, os
from mathutils import Vector
OUT='/home/ubuntu/ford_explorer_sport_trac_2004'
scene=bpy.context.scene; scene.render.resolution_x=3840; scene.render.resolution_y=2160; scene.render.resolution_percentage=100; scene.render.image_settings.file_format='PNG'
cam=bpy.data.objects.get('Technical Camera')
def track(obj,point): obj.rotation_euler=(Vector(point)-obj.location).to_track_quat('-Z','Y').to_euler()
for name,loc,target in [('front_3_4',(7.4,-8.5,5.2),(0,0,1.0)),('side',(0,-10.5,3.0),(0,0,1.1)),('rear_3_4',(-7.0,7.5,4.8),(0,0,1.1))]:
    cam.location=loc; track(cam,target); scene.render.filepath=os.path.join(OUT,name+'_4wd_4k.png'); bpy.ops.render.render(write_still=True)
bpy.ops.wm.save_as_mainfile(filepath=os.path.join(OUT,'explorer_sport_trac_teaching_model_4wd.blend'))
print('4WD_RENDER_COMPLETE')
