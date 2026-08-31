import bpy, math, os, json
from mathutils import Vector
from math import radians

OUT = '/home/ubuntu/ford_explorer_sport_trac_2004'
SCENE_NAME = '2004 Ford Explorer Sport Trac 2WD 4.0L VIN K Flex Fuel - Teaching Model'

# Clean scene
bpy.ops.object.select_all(action='SELECT')
bpy.ops.object.delete(use_global=False)
for datablocks in (bpy.data.meshes, bpy.data.curves, bpy.data.materials, bpy.data.cameras, bpy.data.lights):
    pass

# Collections
collections = {}
for name in ['Engine','Transmission','Chassis','Suspension','Wiring','Fasteners','Interior','Body','Annotations','Presentation']:
    c = bpy.data.collections.new(name)
    bpy.context.scene.collection.children.link(c)
    collections[name] = c

def move_to(obj, col):
    for c in list(obj.users_collection): c.objects.unlink(obj)
    collections[col].objects.link(obj)
    return obj

def mat(name, color, metallic=0.0, rough=0.45, emission=None):
    m = bpy.data.materials.get(name) or bpy.data.materials.new(name)
    m.diffuse_color = (*color, 1)
    m.use_nodes = True
    bs = m.node_tree.nodes.get('Principled BSDF')
    bs.inputs['Base Color'].default_value = (*color,1)
    bs.inputs['Metallic'].default_value = metallic
    bs.inputs['Roughness'].default_value = rough
    if emission:
        bs.inputs['Emission Color'].default_value = (*emission,1)
        bs.inputs['Emission Strength'].default_value = 4.0
    return m

M = {
 'body': mat('Ford Oxford White Body', (0.72,0.76,0.78), 0.15, 0.28),
 'dark': mat('Black Polymer', (0.018,0.022,0.025), 0.0, 0.38),
 'rubber': mat('Tire Rubber', (0.012,0.012,0.012), 0.0, 0.7),
 'glass': mat('Safety Glass', (0.035,0.12,0.18), 0.05, 0.12),
 'steel': mat('Zinc/Steel', (0.22,0.25,0.27), 0.85, 0.24),
 'aluminum': mat('Cast Aluminum', (0.42,0.45,0.47), 0.65, 0.32),
 'iron': mat('Cast Iron', (0.075,0.085,0.09), 0.75, 0.32),
 'red': mat('Ignition Red', (0.32,0.015,0.01), 0.2, 0.32),
 'yellow': mat('Warning Yellow', (0.95,0.58,0.03), 0.05, 0.3),
 'copper': mat('Copper Wiring', (0.42,0.11,0.025), 0.4, 0.28),
 'bluewire': mat('Electrical Blue', (0.015,0.06,0.5), 0.1, 0.3),
 'greenwire': mat('Electrical Green', (0.02,0.35,0.08), 0.1, 0.3),
 'orange': mat('High Visibility Orange', (1.0,0.16,0.015), 0.0, 0.32),
 'white': mat('Annotation White', (0.9,0.95,1.0), 0.0, 0.25),
 'glow': mat('Lamp Glow', (0.7,0.85,1.0), 0.0, 0.2, (0.35,0.65,1.0)),
}

def cube(name, loc, scale, material, col, bevel=0.0, rot=(0,0,0)):
    bpy.ops.mesh.primitive_cube_add(location=loc, rotation=rot)
    o=bpy.context.object; o.name=name; o.scale=scale
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    if bevel:
        mod=o.modifiers.new('Edge softening','BEVEL'); mod.width=bevel; mod.segments=2
    o.data.materials.append(M[material] if isinstance(material,str) else material)
    return move_to(o,col)

def cyl(name, loc, radius, depth, material, col, rot=(0,0,0), verts=32):
    bpy.ops.mesh.primitive_cylinder_add(vertices=verts, radius=radius, depth=depth, location=loc, rotation=rot)
    o=bpy.context.object; o.name=name; o.data.materials.append(M[material] if isinstance(material,str) else material)
    return move_to(o,col)

def uv(name, loc, scale, material, col):
    bpy.ops.mesh.primitive_uv_sphere_add(segments=32, ring_count=16, location=loc)
    o=bpy.context.object; o.name=name; o.scale=scale; bpy.ops.object.transform_apply(location=False, rotation=False, scale=True); o.data.materials.append(M[material]); return move_to(o,col)

def torus(name, loc, major, minor, material, col, rot=(radians(90),0,0)):
    bpy.ops.mesh.primitive_torus_add(major_radius=major, minor_radius=minor, major_segments=48, minor_segments=12, location=loc, rotation=rot)
    o=bpy.context.object; o.name=name; o.data.materials.append(M[material]); return move_to(o,col)

def curve_obj(name, pts, bevel, material, col):
    cu=bpy.data.curves.new(name,'CURVE'); cu.dimensions='3D'; cu.bevel_depth=bevel; cu.bevel_resolution=3
    sp=cu.splines.new('BEZIER'); sp.bezier_points.add(len(pts)-1)
    for b,p in zip(sp.bezier_points,pts): b.co=p; b.handle_left_type='AUTO'; b.handle_right_type='AUTO'
    o=bpy.data.objects.new(name,cu); collections[col].objects.link(o); o.data.materials.append(M[material]); return o

def label(text, loc, size=0.22, color='white', col='Annotations', align='CENTER'):
    cu=bpy.data.curves.new('Label_'+text,'FONT'); cu.body=text; cu.align_x=align; cu.size=size; cu.extrude=0.004; cu.bevel_depth=0.002
    o=bpy.data.objects.new('Label_'+text,cu); collections[col].objects.link(o); o.location=loc; o.rotation_euler=(radians(72),0,0); o.data.materials.append(M[color]); return o

def bolt(name, loc, radius=0.055, length=0.035, col='Fasteners', exploded=False):
    o=cyl(name, loc, radius, length, 'steel', col, rot=(radians(90),0,0), verts=6)
    if exploded: o['exploded_offset']=tuple((Vector(loc)*0.12))
    return o

# Coordinates: X longitudinal, Y lateral, Z vertical. Wheelbase 3.0 m, body length 5.1 m.
# Chassis/frame
cube('Frame left rail', (0,-0.73,0.48), (2.45,0.075,0.09), 'iron','Chassis',0.03)
cube('Frame right rail', (0,0.73,0.48), (2.45,0.075,0.09), 'iron','Chassis',0.03)
for x in (-1.72,-0.75,0.45,1.65): cube('Crossmember', (x,0,0.47), (0.10,0.78,0.08), 'iron','Chassis',0.025)
# body lower, cab, bed
cube('Body floor', (0.20,0,0.70), (2.25,0.82,0.13), 'body','Body',0.08)
cube('Cab shell', (0.40,0,1.45), (1.23,0.84,0.78), 'body','Body',0.16)
cube('Pickup bed', (-1.55,0,1.22), (0.80,0.84,0.58), 'body','Body',0.08)
cube('Bed inner floor', (-1.55,0,1.77), (0.76,0.78,0.035), 'dark','Body',0.02)
# hood, grille, bumpers
cube('Hood', (1.42,0,2.19), (0.78,0.82,0.075), 'body','Body',0.06)
cube('Front bumper', (2.33,0,0.95), (0.10,0.92,0.16), 'dark','Body',0.05)
cube('Rear bumper', (-2.36,0,0.96), (0.10,0.92,0.16), 'dark','Body',0.05)
cube('Front grille', (2.34,0,1.43), (0.025,0.55,0.25), 'dark','Body',0.01)
for y in (-0.42,0.42): cube('Headlamp', (2.36,y,1.72), (0.028,0.25,0.13), 'glow','Body',0.025)
for y in (-0.42,0.42): cube('Tail lamp', (-2.38,y,1.48), (0.028,0.12,0.22), 'red','Body',0.02)
# windows and pillars
cube('Windshield', (1.07,0,2.03), (0.03,0.69,0.32), 'glass','Body',0.03,rot=(0,radians(-18),0))
for x in (0.10,0.83):
    for y in (-0.855,0.855): cube('Cab pillar', (x,y,1.75), (0.08,0.04,0.50), 'dark','Body',0.02)
for x in (0.27,0.86):
    cube('Side window L', (x,-0.855,1.95), (0.27,0.028,0.26), 'glass','Body',0.02)
    cube('Side window R', (x,0.855,1.95), (0.27,0.028,0.26), 'glass','Body',0.02)
# doors and handles
for x in (0.25,0.92):
    for y in (-0.88,0.88):
        cube('Door panel', (x,y,1.23), (0.30,0.035,0.44), 'body','Body',0.035)
        cube('Door handle', (x+0.05,y*1.01,1.55), (0.07,0.025,0.018), 'steel','Body',0.01)
# wheels, hubs, brake discs
for x in (-1.62,1.55):
    for y in (-0.91,0.91):
        torus('Tire', (x,y,0.72), 0.37, 0.16, 'rubber','Suspension')
        cyl('Wheel rim', (x,y,0.72), 0.27, 0.12, 'aluminum','Suspension',rot=(radians(90),0,0))
        cyl('Brake disc', (x,y*0.99,0.72), 0.18, 0.025, 'steel','Suspension',rot=(radians(90),0,0))
        cyl('Wheel hub', (x,y*1.01,0.72), 0.065, 0.15, 'iron','Suspension',rot=(radians(90),0,0))
        for a in range(5):
            ang=2*math.pi*a/5; bolt('Wheel lug',(x+0.12*math.cos(ang),y*1.01,0.72+0.12*math.sin(ang)),0.022,0.05)
# engine bay / 4.0L V6 representation
cube('Engine block 4.0L V6', (1.05,0,1.10), (0.55,0.38,0.42), 'iron','Engine',0.08)
for y in (-0.27,0.27):
    cube('Cylinder head', (1.04,y,1.54), (0.48,0.13,0.10), 'aluminum','Engine',0.03)
    cube('Valve cover', (1.04,y,1.68), (0.46,0.12,0.08), 'red','Engine',0.025)
cyl('Crank pulley',(0.44,0,1.1),0.17,0.08,'steel','Engine',rot=(0,radians(90),0))
cyl('Cooling fan',(1.64,0,1.55),0.27,0.05,'dark','Engine',rot=(0,radians(90),0))
cube('Radiator',(1.95,0,1.47),(0.07,0.65,0.42),'aluminum','Engine',0.02)
curve_obj('Upper radiator hose',[(1.18,-0.05,1.82),(1.55,-0.10,1.92),(1.95,-0.12,1.75)],0.045,'rubber','Engine')
curve_obj('Lower radiator hose',[(1.18,0.05,1.18),(1.55,0.10,1.10),(1.95,0.12,1.18)],0.045,'rubber','Engine')
# intake, throttle, fuel rail, ignition
cube('Intake manifold',(1.02,0,1.92),(0.45,0.24,0.13),'aluminum','Engine',0.04)
cyl('Throttle body',(1.48,0,1.98),0.10,0.16,'aluminum','Engine',rot=(0,radians(90),0))
for y in (-0.27,0.27):
    curve_obj('Fuel rail',[(0.70,y,1.78),(1.30,y,1.79)],0.025,'steel','Engine')
    for x in (0.78,0.99,1.20): cyl('Fuel injector',(x,y,1.72),0.025,0.16,'steel','Engine')
# transmission, driveshaft, axle
cube('5R55E automatic transmission',(0.12,0,0.98),(0.55,0.35,0.30),'aluminum','Transmission',0.10)
cyl('Transmission output',( -0.50,0,0.98),0.09,0.30,'steel','Transmission',rot=(0,radians(90),0))
curve_obj('Propeller shaft',[(-0.40,0,0.96),(-1.15,0,0.80),(-1.78,0,0.72)],0.075,'steel','Transmission')
cube('Rear axle housing',(-1.62,0,0.66),(0.20,0.70,0.18),'iron','Suspension',0.08)
for y in (-0.66,0.66): curve_obj('Rear axle shaft',[(-1.62,y*0.35,0.66),(-1.62,y,0.72)],0.055,'steel','Suspension')
# suspension geometry
for x in (-1.62,1.55):
    cube('Suspension control arm',(x,0,0.55),(0.10,0.65,0.07),'steel','Suspension',0.03)
    for y in (-0.62,0.62):
        cyl('Shock absorber',(x,y,0.95),0.055,0.48,'steel','Suspension',rot=(0,radians(12 if x>0 else -12),0))
for y in (-0.72,0.72):
    curve_obj('Front stabilizer bar',[(1.55,y,0.56),(1.20,y*0.65,0.50),(0.85,y*0.55,0.52)],0.035,'steel','Suspension')
# exhaust
curve_obj('Exhaust pipe',[(0.50,-0.34,0.55),(-0.30,-0.34,0.42),(-1.40,-0.34,0.42),(-2.12,-0.34,0.50)],0.06,'steel','Chassis')
cyl('Muffler',(-1.18,-0.34,0.42),0.14,0.52,'steel','Chassis',rot=(0,radians(90),0))
# intake / fuel tank
cube('Fuel tank',(-0.78,0,0.52),(0.50,0.55,0.15),'steel','Chassis',0.06)
curve_obj('Fuel filler neck',[(-1.35,0.58,0.60),(-1.55,0.72,0.94),(-1.75,0.72,1.16)],0.035,'rubber','Chassis')
# interior visible through open-ish top
cube('Instrument panel',(0.88,0,1.98),(0.18,0.72,0.16),'dark','Interior',0.04)
for y in (-0.40,0.40):
    cube('Front seat',(0.05,y,1.30),(0.36,0.30,0.22),'dark','Interior',0.06)
    cube('Front seat back',(0.08,y,1.65),(0.10,0.30,0.34),'dark','Interior',0.05)
cyl('Steering wheel',(0.84,-0.35,1.80),0.19,0.025,'dark','Interior',rot=(radians(90),0,0))
# harnesses and connectors
harnesses=[('Engine harness',[(0.7,-0.42,1.60),(1.05,-0.55,1.78),(1.45,-0.52,1.60),(1.80,-0.45,1.52)],'copper'),('ABS harness',[(1.55,-0.86,0.85),(1.35,-0.72,0.88),(0.75,-0.70,0.72),(-0.2,-0.72,0.62)],'bluewire'),('Body harness',[(0.95,0,1.78),(0.45,0,1.90),(-0.25,0,1.88),(-1.25,0,1.73)],'greenwire'),('Rear lamp harness',[(-0.95,0.60,0.84),(-1.55,0.72,1.08),(-2.15,0.70,1.35)],'orange')]
for name,pts,material in harnesses:
    curve_obj(name,pts,0.018,material,'Wiring')
    for i,p in enumerate((pts[0],pts[-1])):
        cube(name+' connector',p,(0.045,0.035,0.035),'dark','Wiring',0.01)
# Fasteners at representative factory locations
for x in (-1.95,-0.85,0.35,1.55):
    for y in (-0.73,0.73): bolt('Frame mount bolt',(x,y,0.64),0.045,0.08)
for x in (0.58,1.42):
    for y in (-0.52,0.52):
        for z in (1.50,1.70): bolt('Engine fastener',(x,y,z),0.035,0.06)
# annotations
label('2004 FORD EXPLORER SPORT TRAC', (0, -1.55, 3.10), 0.24, 'white')
label('2WD | V6 4.0L | VIN K | FLEX FUEL', (0, -1.55, 2.82), 0.13, 'yellow')
for text,loc in [('4.0L V6 engine',(1.05,-0.95,1.75)),('5R55E automatic transmission',(0.10,-0.95,1.18)),('Rear axle / 2WD',(-1.55,-0.95,0.85)),('Body harness',(0.0,-0.95,1.95)),('Frame rail',(0.0,-0.95,0.55))]: label(text,loc,0.11,'yellow')
# axis/scale marker
cube('Scale bar',(0,-1.72,0.14),(1.0,0.018,0.018),'yellow','Annotations')
label('2 m reference',(0,-1.72,0.20),0.10,'yellow')

# Technical properties on scene and major objects
scene=bpy.context.scene
scene.name=SCENE_NAME
scene['manual_source']='LEMON Manuals bundle: 2004 Ford Explorer Sport Trac 2WD V6-4.0L VIN K Flex Fuel'
scene['manual_pages']=9185
scene['manual_images']=17887
scene['model_scope']='Teaching-grade major-system representation; dimensions inferred/approximated where the manual has no CAD geometry.'
scene['fitment']='2WD, V6 4.0L VIN K Flex Fuel'
for col in collections.values():
    col['system_role']=col.name

# Exploded animation: stagger component collections along Z and X, preserving base transform
exploded = [('Engine',0,2.1),('Transmission',-0.25,1.5),('Suspension',0,-1.0),('Wiring',0.35,1.0),('Interior',0.4,2.8),('Body',0,-2.3),('Fasteners',0.5,3.6)]
for cname,dx,dz in exploded:
    for o in collections[cname].objects:
        if o.type not in {'MESH','CURVE','FONT'}: continue
        base=o.location.copy(); o.keyframe_insert('location',frame=1)
        o.location=base+Vector((dx,0,dz)); o.keyframe_insert('location',frame=80)
        for fc in o.animation_data.action.fcurves if o.animation_data and o.animation_data.action else []:
            for kp in fc.keyframe_points: kp.interpolation='BEZIER'

# Camera and lighting
bpy.ops.object.camera_add(location=(7.4,-8.5,5.2))
cam=bpy.context.object; cam.name='Technical Camera'; scene.camera=cam; move_to(cam,'Presentation')
def track(obj, point): obj.rotation_euler=(Vector(point)-obj.location).to_track_quat('-Z','Y').to_euler()
track(cam,(0,0,1.0)); cam.data.lens=52
for loc,energy,size,color in [((4,-4,7),1800,5,(1.0,0.95,0.88)),((-4,-2,4),1200,4,(0.65,0.78,1.0)),((0,5,3),1400,4,(1.0,0.55,0.30))]:
    bpy.ops.object.light_add(type='AREA', location=loc); l=bpy.context.object; l.data.energy=energy; l.data.shape='DISK'; l.data.size=size; l.data.color=color; track(l,(0,0,1)); move_to(l,'Presentation')
# Ground plane
cube('Technical ground',(0,0,0.05),(3.3,2.2,0.03),'dark','Presentation',0.01)
# Render settings
scene.render.engine='BLENDER_EEVEE'
scene.render.resolution_x=3840; scene.render.resolution_y=2160; scene.render.resolution_percentage=25
scene.render.image_settings.file_format='PNG'
scene.render.film_transparent=False
scene.world.color=(0.015,0.02,0.03)
scene.render.use_file_extension=True
# Freestyle for technical contour; Line Art modifier hook when supported
scene.render.use_freestyle=True
scene.view_layers[0].freestyle_settings.linesets[0].linestyle.color=(0.01,0.015,0.02)
scene.view_layers[0].freestyle_settings.linesets[0].linestyle.thickness=1.2
for o in list(collections['Body'].objects)+list(collections['Engine'].objects):
    try:
        mod=o.modifiers.new('Line Art technical contour','LINEART')
        o['line_art_modifier_applied']=True
    except Exception:
        o['line_art_modifier_requested']=True
# compositor subtle contrast
scene.use_nodes=True
nt=scene.node_tree; nt.nodes.clear(); rl=nt.nodes.new('CompositorNodeRLayers'); glare=nt.nodes.new('CompositorNodeGlare'); glare.glare_type='FOG_GLOW'; glare.quality='HIGH'; glare.threshold=1.2; glare.size=6; comp=nt.nodes.new('CompositorNodeComposite'); nt.links.new(rl.outputs['Image'],glare.inputs['Image']); nt.links.new(glare.outputs['Image'],comp.inputs['Image'])
# Save master
bpy.ops.wm.save_as_mainfile(filepath=os.path.join(OUT,'explorer_sport_trac_teaching_model.blend'))
# Render views
views=[('front_3_4',(7.4,-8.5,5.2),(0,0,1.0)),('side',(0,-10.5,3.0),(0,0,1.1)),('rear_3_4',(-7.0,7.5,4.8),(0,0,1.1))]
for name,loc,target in views:
    cam.location=loc; track(cam,target); scene.render.filepath=os.path.join(OUT,name+'.png'); bpy.ops.render.render(write_still=True)
# export GLB, reset frame to assembled state
scene.frame_set(1)
for o in bpy.context.selected_objects: o.select_set(False)
bpy.ops.object.select_all(action='SELECT')
try:
    bpy.ops.export_scene.gltf(filepath=os.path.join(OUT,'explorer_sport_trac_teaching_model.glb'), export_format='GLB', use_selection=True)
except Exception as e:
    print('GLB export warning:',e)
bpy.ops.wm.save_as_mainfile(filepath=os.path.join(OUT,'explorer_sport_trac_teaching_model.blend'))
print('BUILD_COMPLETE', OUT)
