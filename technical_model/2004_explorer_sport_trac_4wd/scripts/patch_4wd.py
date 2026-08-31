import bpy, os
from mathutils import Vector
from math import radians
OUT='/home/ubuntu/ford_explorer_sport_trac_2004'
# Existing collections/materials
col=bpy.data.collections.get('Transmission')
ch=bpy.data.collections.get('Suspension')
ann=bpy.data.collections.get('Annotations')
M={m.name:m for m in bpy.data.materials}
def move(o,c):
    for old in list(o.users_collection): old.objects.unlink(o)
    bpy.data.collections[c].objects.link(o)
    return o
def cube(name,loc,scale,mat,collection,bevel=0.0):
    bpy.ops.mesh.primitive_cube_add(location=loc); o=bpy.context.object; o.name=name; o.scale=scale; bpy.ops.object.transform_apply(location=False,rotation=False,scale=True); o.data.materials.append(M[mat]);
    if bevel: mod=o.modifiers.new('Edge softening','BEVEL'); mod.width=bevel; mod.segments=2
    return move(o,collection)
def cyl(name,loc,radius,depth,mat,collection,rot=(0,0,0),verts=32):
    bpy.ops.mesh.primitive_cylinder_add(vertices=verts,radius=radius,depth=depth,location=loc,rotation=rot); o=bpy.context.object; o.name=name; o.data.materials.append(M[mat]); return move(o,collection)
def curve(name,pts,bevel,mat,collection):
    cu=bpy.data.curves.new(name,'CURVE'); cu.dimensions='3D'; cu.bevel_depth=bevel; cu.bevel_resolution=3; sp=cu.splines.new('BEZIER'); sp.bezier_points.add(len(pts)-1)
    for b,p in zip(sp.bezier_points,pts): b.co=p; b.handle_left_type='AUTO'; b.handle_right_type='AUTO'
    o=bpy.data.objects.new(name,cu); bpy.data.collections[collection].objects.link(o); o.data.materials.append(M[mat]); return o
def label(text,loc):
    cu=bpy.data.curves.new('Label_'+text,'FONT'); cu.body=text; cu.align_x='CENTER'; cu.size=.11; cu.extrude=.003; cu.bevel_depth=.001; o=bpy.data.objects.new('Label_'+text,cu); bpy.data.collections['Annotations'].objects.link(o); o.location=loc; o.rotation_euler=(radians(72),0,0); o.data.materials.append(M['Warning Yellow']); return o
# 4WD drivetrain: transfer case behind transmission, front propeller shaft, front differential and halfshafts
cube('4WD transfer case',( -0.48,0,0.98),(0.24,0.30,0.24),'Cast Aluminum','Transmission',.06)
curve('Front propeller shaft',[(-0.62,0,0.98),(0.05,0,0.72),(0.82,0,0.62)],.065,'Zinc/Steel','Transmission')
cube('Front differential',(1.02,0,0.62),(0.20,0.48,0.16),'Cast Iron','Suspension',.06)
for y in (-0.46,0.46):
    curve('Front halfshaft',[(1.02,0,0.62),(1.34,y*.55,0.66),(1.55,y*.85,0.72)],.045,'Zinc/Steel','Suspension')
    cyl('Front CV joint',(1.47,y*.82,0.70),.075,.12,'Zinc/Steel','Suspension',rot=(radians(90),0,0))
# transfer-case crossmember and fasteners
cube('Transfer case crossmember',(-0.50,0,0.54),(.12,.64,.06),'Cast Iron','Chassis',.02)
for y in (-0.46,0.46): cyl('4WD mount bolt',(-0.50,y,0.64),.035,.08,'Zinc/Steel','Fasteners',rot=(radians(90),0,0),verts=6)
label('4WD transfer case',(-0.48,-1.02,1.30))
label('Front differential',(1.02,-1.02,0.88))
label('Front propeller shaft',(0.12,-1.02,0.82))
# update scene metadata and any matching text objects
scene=bpy.context.scene
scene['fitment']='4WD, V6 4.0L VIN K Flex Fuel'
scene['drive_configuration']='4WD teaching representation with transfer case, front propeller shaft, front differential, and front halfshafts'
for o in bpy.data.objects:
    if o.type=='FONT' and o.data.body:
        o.data.body=o.data.body.replace('2WD','4WD').replace('Rear axle / 2WD','4WD front/rear driveline')
# save and export updated GLB
bpy.ops.wm.save_as_mainfile(filepath=os.path.join(OUT,'explorer_sport_trac_teaching_model_4wd.blend'))
bpy.ops.object.select_all(action='SELECT')
bpy.ops.export_scene.gltf(filepath=os.path.join(OUT,'explorer_sport_trac_teaching_model_4wd.glb'), export_format='GLB', use_selection=True)
bpy.ops.wm.save_as_mainfile(filepath=os.path.join(OUT,'explorer_sport_trac_teaching_model_4wd.blend'))
print('PATCH_4WD_COMPLETE')
