import bpy, math
from mathutils import Vector
from math import radians
SRC='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_next5_hardware_detail.blend'
OUT='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_hvac_oil_timing_release.blend'
GLB='/home/ubuntu/ford-sport-trac/app/src/main/assets/models/ford_explorer_sport_trac_2004_hvac_oil_timing_release.glb'
PNG='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/renders/hvac_oil_timing_release_overview.png'
bpy.ops.wm.open_mainfile(filepath=SRC)
COLS=['HVAC_L1_FullSystem','HVAC_L2_ServiceDetail','HVAC_L3_Diagnostic','OIL_L1_FullSystem','OIL_L2_ServiceDetail','OIL_L3_Diagnostic','TIMING_L1_FullSystem','TIMING_L2_ServiceDetail','TIMING_L3_Diagnostic']
for n in COLS:
 c=bpy.data.collections.get(n)
 if c: bpy.data.collections.remove(c)
C={n:bpy.data.collections.new(n) for n in COLS}
for c in C.values(): bpy.context.scene.collection.children.link(c)
def mat(n,col,metal=0,rough=.45):
 m=bpy.data.materials.get(n) or bpy.data.materials.new(n); m.diffuse_color=(*col,1); m.use_nodes=True
 b=m.node_tree.nodes.get('Principled BSDF'); b.inputs['Base Color'].default_value=(*col,1); b.inputs['Metallic'].default_value=metal; b.inputs['Roughness'].default_value=rough
 return m
M={'black':mat('Release black',(.01,.012,.015),0,.7),'steel':mat('Release steel',(.3,.33,.35),.8,.3),'rubber':mat('Release rubber',(.008,.01,.012),0,.78),'al':mat('Release aluminum',(.5,.52,.54),.8,.26),'copper':mat('Release copper',(.55,.12,.025),.7,.3),'blue':mat('AC blue',(.01,.13,.7),0,.3),'red':mat('Oil red',(.45,.01,.005),0,.4),'green':mat('Oil green',(.02,.28,.03),0,.4),'yellow':mat('Diagnostic yellow',(.9,.55,.01),0,.3),'glass':mat('Sight glass',(.04,.16,.2),.2,.15)}
def link(o,c,ma):
 for x in list(o.users_collection): x.objects.unlink(o)
 c.objects.link(o); o.data.materials.append(M[ma]); return o
def cube(n,p,s,c,ma,b=.015):
 bpy.ops.mesh.primitive_cube_add(location=p); o=bpy.context.object; o.name=n; o.scale=s; bpy.ops.object.transform_apply(location=False,rotation=False,scale=True); q=o.modifiers.new('edge radius','BEVEL'); q.width=b; q.segments=2; return link(o,c,ma)
def cyl(n,p,r,d,c,ma,rot=(0,0,0),v=24):
 bpy.ops.mesh.primitive_cylinder_add(vertices=v,radius=r,depth=d,location=p,rotation=rot); o=bpy.context.object; o.name=n; return link(o,c,ma)
def tor(n,p,R,r,c,ma,rot=(0,0,0)):
 bpy.ops.mesh.primitive_torus_add(major_radius=R,minor_radius=r,major_segments=32,minor_segments=10,location=p,rotation=rot); o=bpy.context.object; o.name=n; return link(o,c,ma)
def pipe(n,a,b,r,c,ma):
 d=Vector(b)-Vector(a); o=cyl(n,(Vector(a)+Vector(b))/2,r,d.length,c,ma); o.rotation_euler=d.to_track_quat('Z','Y').to_euler(); return o
def bolt(n,p,c,thread='M6 x 1.00'):
 cyl(n+' shank',p,.018,.09,c,'steel',rot=(0,radians(90),0)); tor(n+' washer',p,.033,.008,c,'al',rot=(0,radians(90),0)); cyl(n+' head',p,.028,.025,c,'steel',rot=(0,radians(90),0),v=6); bpy.context.object['thread_spec']=thread
# HVAC L1 full circuit
A=C['HVAC_L1_FullSystem']; cube('HVAC case',(0,-.35,1.48),(.42,.25,.35),A,'black',.04); cube('Evaporator core',(0,-.62,1.55),(.25,.06,.27),A,'al',.02); cube('Heater core',(0,-.05,1.55),(.25,.06,.27),A,'al',.02); cyl('Blower motor',(-.32,-.35,1.48),.16,.16,A,'black',rot=(radians(90),0,0)); tor('Blower wheel',(-.32,-.35,1.48),.13,.025,A,'al',rot=(radians(90),0,0)); cyl('AC compressor',(.52,.72,.86),.17,.25,A,'al',rot=(0,radians(90),0)); cyl('Condenser',(0,.95,1.45),.42,.06,A,'al',rot=(radians(90),0,0)); pipe('High-side AC line',(.52,.72,.98),(.25,.9,1.45),.028,A,'blue'); pipe('Low-side AC line',(0,-.6,1.55),(.52,.72,.78),.032,A,'blue'); cube('Orifice tube',(0,-.25,1.46),(.025,.025,.04),A,'steel',.005); cube('Accumulator',(.30,-.55,1.50),(.09,.09,.22),A,'al',.02); cube('AC pressure switch',(.30,-.40,1.70),(.035,.035,.05),A,'yellow',.005); pipe('Heater supply',(-.25,.1,1.52),(-.45,1.2,1.30),.035,A,'red'); pipe('Heater return',(-.25,.0,1.42),(-.38,1.2,1.20),.035,A,'red')
# HVAC L2 service detail
D=C['HVAC_L2_ServiceDetail'];
for i,x in enumerate((-.36,-.18,0,.18,.36)): bolt('HVAC case fastener %02d'%i,(x,-.60,1.82),D)
for p in [(-.32,-.62,1.30),(.30,-.55,1.30),(.52,.72,.68)]: tor('HVAC hose clamp',p,.07,.012,D,'steel',rot=(radians(90),0,0))
for p in [(.52,.72,.86),(0,.95,1.45)]: bolt('HVAC mount bolt',p,D,thread='M8 x 1.25')
cube('Cabin air filter',(-.05,-.35,1.12),(.18,.03,.12),D,'rubber',.005); cube('Blend door actuator',(.25,-.05,1.72),(.08,.05,.06),D,'black',.01); cube('Mode door actuator',(-.25,-.05,1.72),(.08,.05,.06),D,'black',.01)
# HVAC L3 diagnostics and repair data
Q=C['HVAC_L3_Diagnostic'];
for n,p,col in [('Low-side service port',(.42,.74,.80),'blue'),('High-side service port',(.42,.85,1.02),'red'),('Blower resistor',(-.38,-.20,1.40),'yellow'),('A/C clutch connector',(.68,.72,.86),'yellow')]: cube(n,p,(.045,.035,.035),Q,col,.005)
for n,p in [('Low-side pressure test',(.42,.74,.84)),('High-side pressure test',(.42,.85,1.06)),('Evacuation/vacuum test',(.30,-.55,1.74))]: cube(n,p,(.07,.02,.02),Q,'yellow',.003)
Q['diagnostic_sequence']=['Visual belt/clutch inspection','Fuse/relay and blower-power check','Static and running pressure test','Leak test with approved method','Evacuate/recover/recharge by label specification','Verify vent temperature and compressor cycling']
Q['safety_boundary']='Refrigerant recovery/charging requires certified equipment and the under-hood label; do not vent refrigerant'
# oil L1 full system
O=C['OIL_L1_FullSystem']; cube('Oil pan',(0,.35,.38),(.40,.28,.12),O,'red',.03); cube('Oil pump housing',(0,.45,.58),(.16,.12,.18),O,'steel',.025); tor('Oil pump rotor',(0,.58,.58),.09,.025,O,'steel',rot=(radians(90),0,0)); cube('Pickup tube',(0,.42,.55),(.08,.22,.04),O,'steel',.01); cube('Pickup screen',(0,.18,.52),(.10,.04,.06),O,'steel',.01); pipe('Oil gallery main',(0,.72,.80),(0,1.20,1.15),.035,O,'green'); pipe('Oil filter feed',(0,.70,.65),(.42,.85,.78),.032,O,'green'); cyl('Oil filter',(.48,.85,.78),.10,.22,O,'red',rot=(0,radians(90),0)); pipe('Oil return LH',(-.25,1.05,1.12),(-.25,.70,.55),.025,O,'green'); pipe('Oil return RH',(.25,1.05,1.12),(.25,.70,.55),.025,O,'green')
# oil L2 detail
OD=C['OIL_L2_ServiceDetail'];
for i,x in enumerate((-.28,-.14,0,.14,.28)): bolt('Oil pan fastener %02d'%i,(x,.12,.42),OD,thread='M6 x 1.00')
for p in [(0,.35,.24),(0,.70,.65),(.42,.85,.78)]: tor('Oil sealing washer',p,.05,.012,OD,'al',rot=(radians(90),0,0))
cyl('Oil pressure sender',(.34,.90,1.08),.05,.12,OD,'steel',rot=(0,radians(90),0)); cube('Oil level dipstick handle',(.62,.62,1.25),(.06,.03,.07),OD,'yellow',.01); pipe('Dipstick tube',(.62,.62,1.20),(.62,.62,.62),.018,OD,'steel')
# oil L3 diagnostics
OQ=C['OIL_L3_Diagnostic'];
for n,p in [('Oil pressure test port',(.34,.90,1.08)),('Oil level inspection',( .62,.62,1.25)),('Drain plug inspection',(0,.35,.24))]: cube(n,p,(.07,.02,.02),OQ,'yellow',.003)
OQ['diagnostic_sequence']=['Check oil level and condition','Inspect filter, drain plug, pan, and leaks','Measure hot-idle and elevated-rpm oil pressure with mechanical gauge','Confirm correct viscosity and filter','Verify pickup/pump condition if pressure remains low']
OQ['safety_boundary']='Do not run an engine with low oil pressure; pressure values must come from the exact engine/service-manual specification'
# timing L1 full system
T=C['TIMING_L1_FullSystem']; cube('Front timing cover',(0,1.12,1.12),(.52,.08,.62),T,'al',.03); tor('Crank sprocket',(0,1.02,.72),.16,.04,T,'steel',rot=(radians(90),0,0)); tor('Cam sprocket LH',(-.28,1.02,1.48),.14,.035,T,'steel',rot=(radians(90),0,0)); tor('Cam sprocket RH',(.28,1.02,1.48),.14,.035,T,'steel',rot=(radians(90),0,0)); pipe('Timing chain left',(0,1.03,.72),(-.28,1.03,1.48),.025,T,'steel'); pipe('Timing chain right',(0,1.04,.72),(.28,1.04,1.48),.025,T,'steel'); cube('Primary chain guide LH',(-.18,1.08,1.10),(.035,.025,.36),T,'rubber',.01); cube('Primary chain guide RH',(.18,1.08,1.10),(.035,.025,.36),T,'rubber',.01); cube('Chain tensioner LH',(-.40,1.05,1.14),(.08,.04,.10),T,'steel',.01); cube('Chain tensioner RH',(.40,1.05,1.14),(.08,.04,.10),T,'steel',.01)
# timing L2 detail
TD=C['TIMING_L2_ServiceDetail'];
for p in [(-.42,1.14,.65),(-.42,1.14,1.55),(.42,1.14,.65),(.42,1.14,1.55),(0,1.14,.55)]: bolt('Timing cover fastener',p,TD,thread='M8 x 1.25')
for p in [(-.28,1.03,1.48),(.28,1.03,1.48),(0,1.02,.72)]: tor('Timing sprocket washer',p,.17,.012,TD,'al',rot=(radians(90),0,0))
for p in [(-.40,1.08,1.14),(.40,1.08,1.14)]: cube('Tensioner shoe',p,(.06,.02,.12),TD,'rubber',.008)
# timing L3 diagnostics
TQ=C['TIMING_L3_Diagnostic'];
for n,p in [('Timing cover inspection point',(0,1.22,1.12)),('Chain slack check LH',(-.28,1.22,1.15)),('Chain slack check RH',(.28,1.22,1.15)),('Crank pulley reference',(0,1.22,.72))]: cube(n,p,(.07,.02,.02),TQ,'yellow',.003)
TQ['diagnostic_sequence']=['Confirm oil level and pressure first','Listen cold start and warm idle for chain rattle','Inspect scan data for cam/crank correlation faults','Use stethoscope only as a localization aid','Verify chain guides/tensioners and timing marks with cover removed','Replace damaged guides/tensioners/chains using exact service procedure']
TQ['safety_boundary']='A rattle sound alone does not identify a timing-chain failure; do not operate if oil pressure is low, chain slap is severe, or timing has jumped'
# common metadata and release checks
for c in C.values():
 c['world_scale']='meters'; c['release_level']='Three-level: assembly, service detail, diagnostic workflow'; c['verification_status']='Reference model; exact VIN-specific dimensions and service values require factory manual/physical measurement'
 for o in c.objects:
  o['scale_unit']='meter'; o['verification_status']='reference geometry; reconcile to exact VIN manual'
# render selected systems
scene=bpy.context.scene
for co in bpy.data.collections: co.hide_render=co.name not in C
bpy.ops.object.camera_add(location=(3.8,-4.6,2.8)); cam=bpy.context.object; scene.camera=cam; cam.data.lens=55; cam.rotation_euler=(Vector((0,.45,.9))-cam.location).to_track_quat('-Z','Y').to_euler()
for p,e,col in [((3,-3,5),1400,(1,.8,.65)),((-3,-2,3),1000,(.4,.6,1)),((0,3,2),900,(1,.3,.15))]:
 bpy.ops.object.light_add(type='AREA',location=p); l=bpy.context.object; l.data.energy=e; l.data.size=3; l.data.color=col; l.rotation_euler=(Vector((0,.45,.9))-l.location).to_track_quat('-Z','Y').to_euler()
scene.render.engine='BLENDER_EEVEE'; scene.render.resolution_x=1800; scene.render.resolution_y=1200; scene.render.resolution_percentage=100; scene.render.filepath=PNG; scene.world.color=(.004,.006,.01); bpy.ops.render.render(write_still=True)
for co in bpy.data.collections: co.hide_render=False
bpy.ops.wm.save_as_mainfile(filepath=OUT); bpy.ops.object.select_all(action='SELECT'); bpy.ops.export_scene.gltf(filepath=GLB,export_format='GLB',use_selection=True); bpy.ops.wm.save_as_mainfile(filepath=OUT); print('HVAC_OIL_TIMING_RELEASE_COMPLETE',OUT,GLB,PNG)
