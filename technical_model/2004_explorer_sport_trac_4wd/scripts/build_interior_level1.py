import bpy, os, math
from mathutils import Vector
from math import radians

SRC='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_hvac_complete_reference.blend'
OUT='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_interior_level1.blend'
GLB='/home/ubuntu/ford-sport-trac/app/src/main/assets/models/ford_explorer_sport_trac_2004_interior_level1.glb'
RENDER='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/renders/interior_level1_exploded.png'

bpy.ops.wm.open_mainfile(filepath=SRC)
old=bpy.data.collections.get('Interior_Level1_Reference')
if old: bpy.data.collections.remove(old)
interior=bpy.data.collections.new('Interior_Level1_Reference'); bpy.context.scene.collection.children.link(interior)

def mat(name,color,metal=0.0,rough=.45):
    m=bpy.data.materials.get(name) or bpy.data.materials.new(name); m.diffuse_color=(*color,1); m.use_nodes=True
    bs=m.node_tree.nodes.get('Principled BSDF'); bs.inputs['Base Color'].default_value=(*color,1); bs.inputs['Metallic'].default_value=metal; bs.inputs['Roughness'].default_value=rough
    return m
M={'dash':mat('Interior dark charcoal',(0.025,.03,.035),0,.46),'black':mat('Interior dark charcoal',(0.025,.03,.035),0,.46),'trim':mat('Interior soft trim',(.07,.08,.085),0,.6),'seat':mat('Interior seat fabric',(.11,.12,.12),0,.82),'vinyl':mat('Interior vinyl',(.055,.06,.062),0,.56),'silver':mat('Interior brushed trim',(.3,.33,.35),.7,.3),'steel':mat('Interior brushed trim',(.3,.33,.35),.7,.3),'glass':mat('Instrument lens',(.02,.06,.08),.1,.1),'white':mat('Instrument markings',(.75,.82,.82),0,.3),'red':mat('Warning indicators',(.8,.02,.01),0,.25),'amber':mat('Indicator amber',(.95,.35,.01),0,.25),'blue':mat('HVAC indicator blue',(.02,.2,.8),.1,.3),'rubber':mat('Pedal rubber',(.01,.012,.013),0,.7)}
def link(o):
    for c in list(o.users_collection): c.objects.unlink(o)
    interior.objects.link(o); return o
def cube(n,loc,scale,ma,bev=.0,rot=(0,0,0)):
    bpy.ops.mesh.primitive_cube_add(location=loc,rotation=rot); o=bpy.context.object; o.name=n; o.scale=scale; bpy.ops.object.transform_apply(location=False,rotation=False,scale=True); o.data.materials.append(M[ma]);
    if bev: mod=o.modifiers.new('Interior edge radius','BEVEL'); mod.width=bev; mod.segments=3
    return link(o)
def cyl(n,loc,r,depth,ma,rot=(0,0,0),verts=32):
    bpy.ops.mesh.primitive_cylinder_add(vertices=verts,radius=r,depth=depth,location=loc,rotation=rot); o=bpy.context.object; o.name=n; o.data.materials.append(M[ma]); return link(o)
def torus(n,loc,major,minor,ma,rot=(radians(90),0,0)):
    bpy.ops.mesh.primitive_torus_add(major_radius=major,minor_radius=minor,major_segments=48,minor_segments=12,location=loc,rotation=rot); o=bpy.context.object; o.name=n; o.data.materials.append(M[ma]); return link(o)
def label(text,loc):
    cu=bpy.data.curves.new('Interior_'+text,'FONT'); cu.body=text; cu.align_x='CENTER'; cu.size=.085; cu.extrude=.002; o=bpy.data.objects.new('Interior label '+text,cu); interior.objects.link(o); o.location=loc; o.rotation_euler=(radians(72),0,0); o.data.materials.append(M['white']); return o
# dashboard shell and cowl
cube('Instrument panel upper shell',(.0,.1,1.98),(0.78,.20,.14),'dash',.08,rot=(radians(-3),0,0))
cube('Dashboard lower knee bolster',(0,-.12,1.67),(.78,.16,.20),'vinyl',.05)
cube('Dashboard passenger fascia',(-.48,.04,1.83),(.28,.08,.22),'trim',.04)
cube('Dashboard driver fascia',(.48,.04,1.83),(.23,.08,.22),'trim',.04)
# instrument cluster, gauges, warning lights
cube('Instrument cluster bezel',(.50,-.05,2.00),(.22,.035,.13),'black',.025)
cube('Instrument cluster lens',(.50,-.087,2.00),(.18,.012,.095),'glass',.015)
for x in (.43,.57):
    cyl('Analog gauge',(x,-.105,2.01),.062,.018,'white',rot=(radians(90),0,0),verts=32)
    cyl('Gauge needle',(x,-.12,2.02),.008,.02,'red',rot=(radians(90),0,0),verts=12)
for x in (.40,.46,.54,.60): cube('Warning lamp',(x,-.12,1.91),(.012,.008,.012),'red',.004)
# center stack and radio/climate controls
cube('Center stack bezel',(0,-.08,1.82),(.24,.045,.35),'silver',.025)
cube('Mach 500 radio face',(0,-.135,1.98),(.18,.018,.075),'black',.012)
cube('Radio display',(0,-.158,2.00),(.08,.008,.018),'blue',.004)
for x in (-.14,-.07,.07,.14): cyl('Radio button',(x,-.16,1.94),.018,.02,'silver',rot=(radians(90),0,0),verts=24)
cube('HVAC control panel',(0,-.14,1.70),(.18,.018,.12),'black',.012)
for x in (-.11,0,.11): cyl('HVAC rotary knob',(x,-.17,1.70),.032,.024,'silver',rot=(radians(90),0,0),verts=24)
# vents
for x in (-.50,.50):
    cube('Cabin dash vent',(x,-.16,1.98),(.12,.018,.055),'black',.018)
    for z in (1.96,1.98,2.00): cube('Vent blade',(x,-.185,z),(.09,.006,.006),'silver',.002)
# steering column, wheel, horn pad and stalks
cyl('Steering column',(.52,-.05,1.67),.055,.48,'steel',rot=(0,radians(90),0))
torus('Steering wheel',(.52,-.15,1.82),.18,.025,'vinyl',rot=(radians(90),0,0))
cube('Steering wheel spoke',(.52,-.15,1.82),(.025,.02,.14),'vinyl',.01,rot=(0,0,radians(90)))
cube('Steering wheel horn pad',(.52,-.18,1.82),(.07,.015,.055),'dash',.02)
for x in (.30,.74): cyl('Steering control stalk',(x,-.12,1.92),.018,.18,'black',rot=(0,radians(90),0),verts=16)
# center console, shifter, cupholders, 4WD selector
cube('Center console',(0,.02,1.34),(.28,.34,.14),'trim',.06)
cube('Transmission shifter base',(0,.02,1.52),(.12,.11,.04),'black',.02)
cyl('Automatic gear selector',(0,-.02,1.66),.035,.30,'steel',rot=(radians(-20),0,0),verts=24)
cube('Gear selector knob',(0,-.12,1.78),(.06,.05,.07),'vinyl',.025)
cube('4WD selector bezel',(-.16,.02,1.48),(.07,.08,.025),'black',.015)
cyl('4WD selector knob',(-.16,-.01,1.54),.028,.06,'silver',rot=(radians(90),0,0),verts=24)
for x in (-.16,.16): cyl('Cup holder', (x,.18,1.48),.08,.025,'black',rot=(0,0,0),verts=32)
# front bucket seats with bolsters and headrests
for x in (-.40,.40):
    cube('Front seat cushion',(x,.25,1.19),(.30,.34,.12),'seat',.08)
    cube('Front seat back',(x,.25,1.52),(.30,.10,.30),'seat',.08,rot=(radians(-5),0,0))
    cube('Seat outer bolster',(x-.27,.25,1.32),(.055,.34,.19),'seat',.04)
    cube('Seat inner bolster',(x+.27,.25,1.32),(.055,.34,.19),'seat',.04)
    cube('Headrest',(x,.25,1.88),(.18,.08,.12),'seat',.045)
    for y in (.01,.49): cyl('Headrest post',(x,y,1.76),.012,.25,'steel',rot=(radians(90),0,0),verts=12)
# rear bench seat
cube('Rear bench cushion',(0,-.62,1.20),(.62,.30,.12),'seat',.08)
cube('Rear bench back',(0,-.62,1.54),(.62,.10,.30),'seat',.08,rot=(radians(5),0,0))
for x in (-.40,0,.40): cube('Rear headrest',(x,-.62,1.86),(.15,.07,.11),'seat',.04)
# doors and trim panels
for x in (-.40,.40):
    for side in (-1,1):
        cube('Front door interior panel',(x,side*.78,1.30),(.34,.035,.40),'trim',.035)
        cube('Door armrest',(x,side*.83,1.53),(.20,.035,.05),'vinyl',.025)
        cube('Interior door handle',(x+.10,side*.85,1.66),(.06,.018,.018),'silver',.01)
        cube('Door speaker grille',(x-.10,side*.85,1.02),(.10,.018,.10),'black',.03)
# pedals and footwell
cube('Brake pedal',(.25,-.40,1.05),(.045,.03,.11),'rubber',.015,rot=(radians(-12),0,0))
cube('Accelerator pedal',(.48,-.40,1.03),(.035,.025,.13),'rubber',.012,rot=(radians(-12),0,0))
cube('Parking brake pedal',(.02,-.40,1.01),(.035,.025,.11),'rubber',.012,rot=(radians(-12),0,0))
cube('Driver footwell',(.38,-.28,.94),(.40,.035,.18),'rubber',.04)
# seat belts, anchors, and trim fasteners
for x in (-.40,.40):
    for side in (-1,1):
        cube('Seat belt upper anchor',(x,side*.80,1.82),(.025,.02,.045),'steel',.008)
        cube('Seat belt buckle',(x+.15,side*.30,1.17),(.025,.025,.10),'red',.012)
for x in (-.68,-.34,0,.34,.68):
    for side in (-1,1): cyl('Interior trim screw',(x,side*.86,1.34),.014,.025,'steel',rot=(radians(90),0,0),verts=12)
# overhead console and dome light
cube('Overhead console',(0,.30,2.55),(.18,.28,.035),'trim',.02)
cube('Dome light',(0,.25,2.51),(.07,.10,.018),'white',.015)
# metadata
for o in interior.objects: o['source_scope']='Interior reference assembly; verify trim, option content, dimensions, and fasteners against VIN and physical vehicle'
interior['level']='Level 1 cabin interior detail'
interior['coverage']='dashboard, cluster, radio, HVAC controls, vents, steering wheel, column, console, shifters, cupholders, front seats, rear bench, doors, speakers, pedals, belts, overhead console, trim fasteners'
interior['accuracy_boundary']='Reference geometry, not OEM CAD; exact trim options and dimensions require VIN-specific documentation or measurement'
scene=bpy.context.scene; scene['interior_level1']='Cabin interior detail added 2026-09-06'
# isolated interior render
for c in bpy.data.collections:
    if c.name not in {'Interior_Level1_Reference','Presentation'}: c.hide_render=True
bpy.ops.object.camera_add(location=(3.5,-4.5,2.6)); cam=bpy.context.object; cam.name='Interior level camera'; scene.camera=cam; cam.data.lens=52; cam.rotation_euler=(Vector((0,.15,1.55))-cam.location).to_track_quat('-Z','Y').to_euler()
for loc,energy,size,color in [((3,-3,5),1100,3,(1,.85,.72)),((-2,-2,3),700,3,(.4,.6,1)),((0,3,3),800,3,(1,.3,.15))]:
    bpy.ops.object.light_add(type='AREA',location=loc); l=bpy.context.object; l.data.energy=energy; l.data.shape='DISK'; l.data.size=size; l.data.color=color; l.rotation_euler=(Vector((0,.15,1.5))-l.location).to_track_quat('-Z','Y').to_euler()
scene.render.engine='BLENDER_EEVEE'; scene.render.resolution_x=1800; scene.render.resolution_y=1200; scene.render.resolution_percentage=100; scene.render.image_settings.file_format='PNG'; scene.render.filepath=RENDER; scene.world.color=(.004,.006,.01); bpy.ops.render.render(write_still=True)
# restore and export all
for c in bpy.data.collections: c.hide_render=False
bpy.ops.wm.save_as_mainfile(filepath=OUT); bpy.ops.object.select_all(action='SELECT'); bpy.ops.export_scene.gltf(filepath=GLB,export_format='GLB',use_selection=True); bpy.ops.wm.save_as_mainfile(filepath=OUT)
print('INTERIOR_LEVEL1_COMPLETE',OUT,GLB,RENDER)
