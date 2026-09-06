import bpy, os, math
from mathutils import Vector
from math import radians

SRC = '/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_teaching_model_4wd.blend'
OUT = '/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_corrected_exterior.blend'
RENDER_DIR = '/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/renders'

bpy.ops.wm.open_mainfile(filepath=SRC)
body = bpy.data.collections.get('Body')
if body is None:
    body = bpy.data.collections.new('Body')
    bpy.context.scene.collection.children.link(body)
for obj in list(body.objects):
    bpy.data.objects.remove(obj, do_unlink=True)

# Materials

def material(name, color, metallic=0.0, roughness=0.4):
    m = bpy.data.materials.get(name) or bpy.data.materials.new(name)
    m.diffuse_color = (*color, 1.0)
    m.use_nodes = True
    bs = m.node_tree.nodes.get('Principled BSDF')
    bs.inputs['Base Color'].default_value = (*color, 1.0)
    bs.inputs['Metallic'].default_value = metallic
    bs.inputs['Roughness'].default_value = roughness
    return m

M = {
    'silver': material('Corrected Silver Metallic Paint', (0.42, 0.45, 0.47), 0.72, 0.24),
    'dark': material('Corrected Exterior Black Polymer', (0.012, 0.015, 0.018), 0.05, 0.3),
    'glass': material('Corrected Smoked Safety Glass', (0.018, 0.055, 0.075), 0.12, 0.12),
    'chrome': material('Corrected Chrome', (0.65, 0.68, 0.7), 0.92, 0.16),
    'lamp': material('Corrected Headlamp Lens', (0.86, 0.92, 0.95), 0.15, 0.08),
    'amber': material('Corrected Amber Turn Signal', (0.95, 0.22, 0.02), 0.05, 0.22),
    'red': material('Corrected Tail Lamp Red', (0.7, 0.018, 0.01), 0.05, 0.2),
    'tire': material('Corrected Tire Rubber', (0.006, 0.007, 0.008), 0.0, 0.72),
    'rim': material('Corrected Alloy Wheel', (0.3, 0.33, 0.36), 0.82, 0.22),
    'blue': material('Ford Badge Blue', (0.015, 0.08, 0.3), 0.25, 0.2),
}

def link(obj):
    for c in list(obj.users_collection): c.objects.unlink(obj)
    body.objects.link(obj)
    return obj

def cube(name, loc, scale, mat, bevel=0.0, rot=(0,0,0)):
    bpy.ops.mesh.primitive_cube_add(location=loc, rotation=rot)
    o=bpy.context.object; o.name=name; o.scale=scale
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    if bevel:
        mod=o.modifiers.new('Realistic panel edge','BEVEL'); mod.width=bevel; mod.segments=4
    o.data.materials.append(M[mat]); return link(o)

def uv(name, loc, scale, mat):
    bpy.ops.mesh.primitive_uv_sphere_add(segments=48, ring_count=24, location=loc)
    o=bpy.context.object; o.name=name; o.scale=scale
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    o.data.materials.append(M[mat]); return link(o)

def cyl(name, loc, radius, depth, mat, rot=(0,0,0), verts=48):
    bpy.ops.mesh.primitive_cylinder_add(vertices=verts, radius=radius, depth=depth, location=loc, rotation=rot)
    o=bpy.context.object; o.name=name; o.data.materials.append(M[mat]); return link(o)

def torus(name, loc, major, minor, mat, rot=(radians(90),0,0)):
    bpy.ops.mesh.primitive_torus_add(major_radius=major, minor_radius=minor, major_segments=64, minor_segments=20, location=loc, rotation=rot)
    o=bpy.context.object; o.name=name; o.data.materials.append(M[mat]); return link(o)

def front_wheel(x, y):
    torus('Sport Trac tire', (x,y,0.73), 0.39, 0.17, 'tire')
    cyl('Six spoke alloy wheel', (x,y,0.73), 0.28, 0.14, 'rim', rot=(radians(90),0,0))
    cyl('Wheel center cap', (x,y*1.01,0.73), 0.085, 0.16, 'chrome', rot=(radians(90),0,0))
    for a in range(6):
        ang=2*math.pi*a/6
        cube('Wheel spoke', (x+0.12*math.cos(ang), y*1.01, 0.73+0.12*math.sin(ang)), (0.018,0.085,0.028), 'chrome', 0.01, rot=(0,0,ang))

# Realistic 2001-2005 Sport Trac silhouette, dimensions in metres, approx. 5.2 m long.
# Lower body, crew cab and pickup bed
cube('Lower rocker and body shell', (0.0,0,1.00), (2.48,0.88,0.34), 'silver', 0.13)
cube('Crew cab roof and pillars', (0.52,0,1.83), (1.35,0.84,0.62), 'silver', 0.18)
cube('Pickup bed side structure', (-1.55,0,1.34), (0.86,0.86,0.49), 'silver', 0.12)
cube('Bed floor', (-1.55,0,1.78), (0.78,0.77,0.035), 'dark', 0.02)
cube('Bed tonneau cover', (-1.55,0,1.88), (0.80,0.80,0.045), 'dark', 0.035)
# Hood and front nose with sloping windshield
cube('Long hood', (1.47,0,1.98), (0.86,0.83,0.10), 'silver', 0.08, rot=(0,radians(-3),0))
cube('Front fascia', (2.34,0,1.18), (0.16,0.86,0.42), 'silver', 0.08)
cube('Lower dark bumper', (2.46,0,0.87), (0.12,0.88,0.16), 'dark', 0.08)
cube('Rear dark bumper', (-2.47,0,0.92), (0.12,0.88,0.15), 'dark', 0.07)
# Fenders over the wheels
for x in (-1.62,1.56):
    for y in (-0.84,0.84):
        uv('Rounded wheel arch fender', (x,y,0.86), (0.48,0.10,0.53), 'silver')
# Windows and pillars, with windshield rake
cube('Windshield', (1.10,0,2.10), (0.05,0.69,0.34), 'glass', 0.035, rot=(0,radians(-17),0))
for y in (-0.855,0.855):
    cube('Front side window', (0.48,y,2.02), (0.39,0.025,0.31), 'glass', 0.025)
    cube('Rear side window', (-0.31,y,2.02), (0.40,0.025,0.31), 'glass', 0.025)
    cube('A pillar', (0.94,y,1.98), (0.055,0.035,0.43), 'dark', 0.018, rot=(0,radians(-12),0))
    cube('B pillar', (0.05,y,2.0), (0.055,0.035,0.43), 'dark', 0.018)
    cube('C pillar', (-0.72,y,1.98), (0.065,0.035,0.43), 'dark', 0.018, rot=(0,radians(10),0))
# Doors and body crease
for x in (0.48,-0.35):
    for y in (-0.88,0.88):
        cube('Sport Trac door skin', (x,y,1.34), (0.38,0.035,0.43), 'silver', 0.045)
        cube('Door lower protective molding', (x,y*1.005,1.12), (0.36,0.022,0.035), 'dark', 0.015)
        cube('Door handle', (x+0.10,y*1.02,1.63), (0.085,0.025,0.025), 'chrome', 0.015)
# Roof rails and running boards
for y in (-0.62,0.62):
    cube('Roof rail', (0.0,y,2.56), (1.20,0.035,0.045), 'dark', 0.025)
    cube('Black running board', (-0.05,y*1.04,0.72), (1.35,0.10,0.07), 'dark', 0.04)
# Grille, lamps, and fog lamps
cube('Black honeycomb grille', (2.515,0,1.48), (0.025,0.48,0.20), 'dark', 0.025)
for y in (-0.52,0.52):
    cube('Headlight housing', (2.51,y,1.68), (0.035,0.24,0.16), 'lamp', 0.035)
    cube('Amber turn signal', (2.52,y*0.72,1.68), (0.038,0.055,0.15), 'amber', 0.02)
    cyl('Round fog lamp', (2.535,y*0.66,1.02), 0.095, 0.035, 'lamp', rot=(0,radians(90),0))
# Ford badge and mirrors
cube('Ford oval badge', (2.55,0,1.49), (0.015,0.12,0.055), 'blue', 0.04)
for y in (-0.98,0.98):
    uv('Side mirror', (0.88,y,1.86), (0.13,0.07,0.10), 'dark')
# Tailgate and tail lamps
cube('Tailgate', (-2.52,0,1.38), (0.035,0.78,0.40), 'silver', 0.035)
for y in (-0.59,0.59):
    cube('Vertical tail lamp', (-2.555,y,1.50), (0.035,0.13,0.25), 'red', 0.025)
# Wheels
for x in (-1.62,1.56):
    for y in (-0.92,0.92): front_wheel(x,y)

scene=bpy.context.scene
scene['exterior_correction']='2026-09-06: replaced blockout exterior with recognizable 2004 Sport Trac silhouette and signature details'
scene['model_scope']='Exterior correction pass; existing 4WD teaching assemblies retained; not OEM CAD or last-washer-complete.'
scene['fitment']='2004 Ford Explorer Sport Trac, 4.0L V6, 4WD teaching configuration'
scene.render.engine='BLENDER_EEVEE'
scene.render.resolution_x=1600; scene.render.resolution_y=1000; scene.render.resolution_percentage=100
scene.render.image_settings.file_format='PNG'
scene.world.color=(0.006,0.008,0.012)
# Replace camera and lights for readable preview
for o in list(bpy.data.objects):
    if o.type in {'CAMERA','LIGHT'}:
        bpy.data.objects.remove(o, do_unlink=True)
bpy.ops.object.camera_add(location=(7.2,-8.5,4.3))
cam=bpy.context.object; cam.name='Corrected Exterior Camera'; scene.camera=cam; cam.data.lens=58
cam.rotation_euler=(Vector((0,0,1.25))-cam.location).to_track_quat('-Z','Y').to_euler()
for loc,energy,size,color in [((4,-4,7),1300,5,(1.0,0.92,0.82)),((-4,-3,4),900,4,(0.55,0.7,1.0)),((1,5,3),1100,4,(1.0,0.35,0.18))]:
    bpy.ops.object.light_add(type='AREA', location=loc)
    l=bpy.context.object; l.data.energy=energy; l.data.shape='DISK'; l.data.size=size; l.data.color=color
    l.rotation_euler=(Vector((0,0,1.0))-l.location).to_track_quat('-Z','Y').to_euler()
scene.render.filepath=os.path.join(RENDER_DIR,'corrected_exterior_front_3_4.png')
bpy.ops.wm.save_as_mainfile(filepath=OUT)
bpy.ops.render.render(write_still=True)
# Export corrected exterior plus retained teaching assemblies
bpy.ops.object.select_all(action='SELECT')
bpy.ops.export_scene.gltf(filepath='/home/ubuntu/ford-sport-trac/app/src/main/assets/models/ford_explorer_sport_trac_2004_corrected_exterior.glb', export_format='GLB', use_selection=True)
print('CORRECTED_EXTERIOR_COMPLETE', OUT)
print('RENDER', scene.render.filepath)
