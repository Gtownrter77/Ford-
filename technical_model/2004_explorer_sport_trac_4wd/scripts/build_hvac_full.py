import bpy, os, math
from mathutils import Vector
from math import radians

SRC='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_corrected_exterior.blend'
OUT='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_hvac_complete_reference.blend'
GLB='/home/ubuntu/ford-sport-trac/app/src/main/assets/models/ford_explorer_sport_trac_2004_hvac_complete_reference.glb'
RENDER='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/renders/hvac_complete_exploded.png'

bpy.ops.wm.open_mainfile(filepath=SRC)
old=bpy.data.collections.get('HVAC_Complete_Reference')
if old:
    bpy.data.collections.remove(old)
hvac=bpy.data.collections.new('HVAC_Complete_Reference')
bpy.context.scene.collection.children.link(hvac)

# materials

def mat(name,color,metal=0.0,rough=0.4):
    m=bpy.data.materials.get(name) or bpy.data.materials.new(name); m.diffuse_color=(*color,1); m.use_nodes=True
    bs=m.node_tree.nodes.get('Principled BSDF'); bs.inputs['Base Color'].default_value=(*color,1); bs.inputs['Metallic'].default_value=metal; bs.inputs['Roughness'].default_value=rough
    return m
M={
 'black':mat('HVAC black polymer',(0.01,0.015,0.018),0.0,0.42), 'case':mat('HVAC case polymer',(0.08,0.10,0.11),0.0,0.5),
 'al':mat('HVAC cast aluminum',(0.38,0.42,0.44),0.72,0.3), 'steel':mat('HVAC zinc steel',(0.3,0.34,0.36),0.8,0.25),
 'rubber':mat('HVAC hose rubber',(0.008,0.01,0.012),0.0,0.68), 'foam':mat('HVAC seal foam',(0.12,0.18,0.16),0.0,0.9),
 'copper':mat('HVAC copper',(0.55,0.16,0.035),0.7,0.28), 'blue':mat('HVAC cold blue',(0.03,0.16,0.7),0.15,0.3),
 'red':mat('HVAC hot red',(0.8,0.04,0.01),0.12,0.3), 'white':mat('HVAC label white',(0.8,0.86,0.88),0.0,0.35)
}

def link(o):
    for c in list(o.users_collection): c.objects.unlink(o)
    hvac.objects.link(o); return o

def cube(n,loc,scale,ma,bev=.0,rot=(0,0,0)):
    bpy.ops.mesh.primitive_cube_add(location=loc,rotation=rot); o=bpy.context.object; o.name=n; o.scale=scale; bpy.ops.object.transform_apply(location=False,rotation=False,scale=True); o.data.materials.append(M[ma]);
    if bev: mod=o.modifiers.new('HVAC edge radius','BEVEL'); mod.width=bev; mod.segments=3
    return link(o)

def cyl(n,loc,r,depth,ma,rot=(0,0,0),verts=32):
    bpy.ops.mesh.primitive_cylinder_add(vertices=verts,radius=r,depth=depth,location=loc,rotation=rot); o=bpy.context.object; o.name=n; o.data.materials.append(M[ma]); return link(o)

def torus(n,loc,major,minor,ma,rot=(radians(90),0,0)):
    bpy.ops.mesh.primitive_torus_add(major_radius=major,minor_radius=minor,major_segments=48,minor_segments=12,location=loc,rotation=rot); o=bpy.context.object; o.name=n; o.data.materials.append(M[ma]); return link(o)

def curve(n,pts,bevel,ma):
    cu=bpy.data.curves.new(n,'CURVE'); cu.dimensions='3D'; cu.bevel_depth=bevel; cu.bevel_resolution=3; sp=cu.splines.new('BEZIER'); sp.bezier_points.add(len(pts)-1)
    for b,p in zip(sp.bezier_points,pts): b.co=p; b.handle_left_type='AUTO'; b.handle_right_type='AUTO'
    o=bpy.data.objects.new(n,cu); hvac.objects.link(o); o.data.materials.append(M[ma]); return o

def bolt(n,loc): return cyl(n,loc,.028,.055,'steel',rot=(radians(90),0,0),verts=6)

def label(text,loc):
    cu=bpy.data.curves.new('HVAC_'+text,'FONT'); cu.body=text; cu.align_x='CENTER'; cu.size=.09; cu.extrude=.002; o=bpy.data.objects.new('HVAC label '+text,cu); hvac.objects.link(o); o.location=loc; o.rotation_euler=(radians(72),0,0); o.data.materials.append(M['white']); return o

# Engine-bay HVAC: compressor, clutch, accumulator, condenser, receiver/orifice service paths
cyl('A/C compressor housing',(1.02,-.48,1.24),.19,.38,'al',rot=(0,radians(90),0))
torus('A/C compressor clutch',(1.25,-.48,1.24),.15,.035,'steel',rot=(0,radians(90),0))
cyl('A/C compressor pulley',(1.29,-.48,1.24),.11,.06,'steel',rot=(0,radians(90),0))
for a in range(8):
    ang=2*math.pi*a/8; bolt('Compressor mounting bolt',(1.02+.13*math.cos(ang),-.50,1.24+.13*math.sin(ang)))
# condenser core and header tanks behind grille
cube('A/C condenser core',(2.05,-.02,1.46),(.045,.62,.38),'al',.02)
for y in (-.64,.64): cube('Condenser header tank',(2.05,y,1.46),(.06,.08,.39),'steel',.03)
for z in (1.18,1.34,1.50,1.66): cube('Condenser fin bank',(2.00,0,z),(.018,.58,.012),'steel',.002)
# accumulator and drier
cyl('A/C accumulator',(1.35,-.62,1.36),.10,.35,'steel',rot=(radians(90),0,0)); cube('Accumulator mounting strap',(1.35,-.72,1.36),(.04,.03,.22),'steel',.01)
# refrigerant hard/soft lines and service ports
curve('A/C high pressure liquid line',[(2.06,-.54,1.55),(1.72,-.55,1.48),(1.35,-.53,1.38),(1.20,-.50,1.30)],.022,'steel')
curve('A/C low pressure suction line',[(1.35,-.62,1.31),(1.18,-.55,1.20),(1.02,-.48,1.26),(0.82,-.47,1.28)],.035,'rubber')
curve('A/C discharge line',[(1.18,-.50,1.30),(1.50,-.44,1.72),(1.90,-.38,1.72),(2.05,-.20,1.55)],.024,'rubber')
for n,loc,color in [('Low-side service port',(1.16,-.56,1.22),'blue'),('High-side service port',(1.78,-.54,1.52),'red')]:
    cyl(n,loc,.045,.07,color,rot=(radians(90),0,0),verts=16); cyl(n+' cap', (loc[0],loc[1]-.05,loc[2]),.055,.025,'black',rot=(radians(90),0,0),verts=16)
# belt routing
curve('Serpentine belt',[(.75,-.48,1.10),(1.28,-.48,1.05),(1.64,-.48,1.35),(1.36,-.48,1.70),(.82,-.48,1.55),(.75,-.48,1.10)],.026,'rubber')
# Under-dash HVAC case, firewall seal, heater core, evaporator
cube('HVAC plenum case under dash',(.48,-.18,1.56),(.52,.48,.42),'case',.08)
cube('Firewall HVAC foam seal',(.98,-.18,1.56),(.035,.46,.38),'foam',.02)
cube('Evaporator core',(.32,-.18,1.56),(.22,.36,.28),'al',.025)
for y in (-.48,.12): cube('Evaporator tube header',(.08,y,1.56),(.08,.035,.25),'steel',.015)
# heater core and tubes
cube('Heater core',(0.55,-.18,1.37),(.20,.34,.24),'copper',.025)
for y in (-.45,.10): curve('Heater hose',[(.78,y,1.38),(.98,y,1.20),(1.10,y,1.12)],.035,'rubber')
# blower and motor on passenger side
cyl('Blower motor',(0.18,.38,1.50),.17,.20,'steel',rot=(radians(90),0,0)); cyl('Blower squirrel cage',(0.18,.48,1.50),.21,.12,'black',rot=(radians(90),0,0))
for a in range(12):
    ang=2*math.pi*a/12; cube('Blower cage fin',(0.18+.14*math.cos(ang),.56,1.50+.14*math.sin(ang)),(.018,.04,.08),'case',.006,rot=(0,ang,0))
cube('Blower resistor pack',(.02,.34,1.30),(.06,.03,.12),'steel',.01)
# blend/mode/recirc doors and actuators
for n,loc,rot in [('Blend door',(0.55,-.18,1.62),radians(18)),('Mode door',(0.55,-.18,1.45),radians(-22)),('Recirculation door',(.05,.18,1.70),radians(35))]:
    cube(n,loc,(.25,.34,.018),'foam',.01,rot=(0,rot,0)); cyl(n+' actuator',(loc[0]+.28,loc[1],loc[2]),.045,.10,'steel',rot=(radians(90),0,0),verts=16)
# ducts and dash vents
for n,pts in [('Defrost duct',[(.62,-.28,1.90),(.70,-.28,2.15),(1.00,-.28,2.25)]),('Left dash duct',[(.15,-.18,1.62),(.15,-.55,1.78),(.55,-.72,1.85)]),('Right dash duct',[(.15,.18,1.62),(.15,.55,1.78),(.55,.72,1.85)]),('Floor duct',[(.48,-.18,1.35),(.48,-.18,1.02),(.78,-.18,.92)])]: curve(n,pts,.065,'rubber')
for y in (-.70,.70):
    cube('Dashboard HVAC vent',(0.70,y,1.88),(.13,.035,.08),'black',.02)
    for z in (1.85,1.88,1.91): cube('Vent louvre',(0.70,y*1.02,z),(.10,.018,.006),'steel',.002)
# dash control head, cable/connector and vacuum harness
cube('HVAC control head',(0.82,-.05,1.90),(.18,.30,.08),'black',.03)
for x in (.72,.82,.92): cyl('HVAC control knob',(x,-.36,1.90),.035,.03,'steel',rot=(radians(90),0,0),verts=24)
curve('HVAC actuator electrical harness',[(.42,-.25,1.62),(.28,-.50,1.44),(.02,-.50,1.30)],.012,'blue')
curve('HVAC vacuum control harness',[(.70,.22,1.58),(.42,.45,1.40),(.10,.46,1.26)],.012,'red')
# representative housing screws, washers, and clips
for x in (.12,.42,.82):
    for y in (-.58,.22):
        for z in (1.24,1.84):
            bolt('HVAC housing fastener',(x,y,z)); torus('HVAC housing washer',(x,y-.035,z),.04,.008,'steel',rot=(radians(90),0,0))
for i in range(8): cube('HVAC duct retaining clip',(.15+i*.13,-.62,1.74),(.018,.025,.035),'steel',.008)
# organized metadata
for o in hvac.objects: o['source_scope']='HVAC subsystem reference assembly; verify VIN, dimensions, service manual and exact production option before physical work'
hvac['system']='Complete heat and air reference assembly'
hvac['fitment']='2004 Ford Explorer Sport Trac 4.0L 4WD teaching configuration'
hvac['scale_unit']='metres; approximate packaging coordinates aligned to vehicle teaching scene'
hvac['coverage']='compressor, clutch, pulley, belt, condenser, headers, accumulator, high/low lines, service ports, evaporator, heater core, plenum, blower, resistor, blend/mode/recirc doors, actuators, ducts, vents, controls, harnesses, seals, clips, representative bolts and washers'
scene=bpy.context.scene
scene['hvac_complete_reference']='2026-09-06: full HVAC reference assembly added'
scene['hvac_accuracy_boundary']='Reference assembly, not OEM CAD; dimensions and every fastener require VIN-specific factory documentation or physical measurement.'
# render HVAC exploded view with exterior hidden, then restore
body=bpy.data.collections.get('Body'); prev=body.hide_render if body else False
if body: body.hide_render=True
for c in bpy.data.collections:
    if c.name in {'Engine','Transmission','Chassis','Suspension','Interior','Wiring','Fasteners','Annotations','Presentation'}: c.hide_render=True
bpy.ops.object.camera_add(location=(4.6,-6.8,3.2)); cam=bpy.context.object; cam.name='HVAC exploded camera'; scene.camera=cam; cam.data.lens=58; cam.rotation_euler=(Vector((.7,0,1.45))-cam.location).to_track_quat('-Z','Y').to_euler()
for loc,energy,size,color in [((3,-4,6),1200,4,(1,.9,.8)),((-2,-2,3),800,3,(.4,.65,1)),((1,4,4),900,3,(1,.3,.15))]:
    bpy.ops.object.light_add(type='AREA',location=loc); l=bpy.context.object; l.data.energy=energy; l.data.shape='DISK'; l.data.size=size; l.data.color=color; l.rotation_euler=(Vector((.6,0,1.4))-l.location).to_track_quat('-Z','Y').to_euler()
scene.render.engine='BLENDER_EEVEE'; scene.render.resolution_x=1800; scene.render.resolution_y=1200; scene.render.resolution_percentage=100; scene.render.image_settings.file_format='PNG'; scene.render.filepath=RENDER; scene.world.color=(.004,.006,.01); bpy.ops.render.render(write_still=True)
if body: body.hide_render=prev
for c in bpy.data.collections:
    if c.name in {'Engine','Transmission','Chassis','Suspension','Interior','Wiring','Fasteners','Annotations','Presentation'}: c.hide_render=False
# save and export all visible content
bpy.ops.wm.save_as_mainfile(filepath=OUT)
bpy.ops.object.select_all(action='SELECT'); bpy.ops.export_scene.gltf(filepath=GLB,export_format='GLB',use_selection=True)
bpy.ops.wm.save_as_mainfile(filepath=OUT)
print('HVAC_COMPLETE_REFERENCE',OUT,GLB,RENDER)
