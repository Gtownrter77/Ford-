import bpy
from mathutils import Vector
from math import radians
SRC='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_next5_mechanical_body.blend'
OUT='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_next5_chassis_safety.blend'
GLB='/home/ubuntu/ford-sport-trac/app/src/main/assets/models/ford_explorer_sport_trac_2004_next5_chassis_safety.glb'
PNG='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/renders/next5_chassis_safety_overview.png'
bpy.ops.wm.open_mainfile(filepath=SRC)
N=['Next5_Steering','Next5_BrakeHydraulics','Next5_4WDDriveline','Next5_OccupantSafety','Next5_WheelsTires']
for n in N:
 c=bpy.data.collections.get(n)
 if c:bpy.data.collections.remove(c)
C={n:bpy.data.collections.new(n) for n in N}
for c in C.values():bpy.context.scene.collection.children.link(c)
def mat(n,col,metal=0,rough=.45):
 m=bpy.data.materials.get(n) or bpy.data.materials.new(n);m.diffuse_color=(*col,1);m.use_nodes=True;b=m.node_tree.nodes.get('Principled BSDF');b.inputs['Base Color'].default_value=(*col,1);b.inputs['Metallic'].default_value=metal;b.inputs['Roughness'].default_value=rough;return m
M={'dark':mat('Chassis black',(.015,.02,.025),0,.62),'black':mat('Chassis black',(.015,.02,.025),0,.62),'metal':mat('Chassis steel',(.28,.31,.33),.75,.3),'rubber':mat('Chassis rubber',(.008,.01,.012),0,.75),'red':mat('Safety red',(.65,.01,.008),.2,.3),'yellow':mat('Safety yellow',(.9,.55,.01),0,.3),'glass':mat('Safety glass',(.03,.12,.16),.1,.15),'tire':mat('Tire rubber',(.008,.009,.01),0,.9),'alloy':mat('Wheel alloy',(.42,.45,.47),.85,.25),'blue':mat('Brake fluid blue',(.01,.04,.12),0,.35)}
def link(o,c,ma):
 for x in list(o.users_collection):x.objects.unlink(o)
 c.objects.link(o);o.data.materials.append(M[ma]);return o
def cube(n,p,s,c,ma,b=.02,rot=(0,0,0)):
 bpy.ops.mesh.primitive_cube_add(location=p,rotation=rot);o=bpy.context.object;o.name=n;o.scale=s;bpy.ops.object.transform_apply(location=False,rotation=False,scale=True);q=o.modifiers.new('edge radius','BEVEL');q.width=b;q.segments=2;return link(o,c,ma)
def cyl(n,p,r,d,c,ma,rot=(0,0,0),v=28):
 bpy.ops.mesh.primitive_cylinder_add(vertices=v,radius=r,depth=d,location=p,rotation=rot);o=bpy.context.object;o.name=n;return link(o,c,ma)
def tor(n,p,R,r,c,ma,rot=(0,0,0)):
 bpy.ops.mesh.primitive_torus_add(major_radius=R,minor_radius=r,major_segments=40,minor_segments=10,location=p,rotation=rot);o=bpy.context.object;o.name=n;return link(o,c,ma)
def pipe(n,a,b,r,c,ma):
 d=Vector(b)-Vector(a);o=cyl(n,(Vector(a)+Vector(b))/2,r,d.length,c,ma);o.rotation_euler=d.to_track_quat('Z','Y').to_euler();return o
# steering
S=C['Next5_Steering']
cube('Steering rack',(0,.38,.65),(.42,.12,.10),S,'metal',.03)
pipe('Steering shaft',(0,.55,1.25),(0,.40,.65),.045,S,'metal')
pipe('Tie rod LH',(0,.38,.65),(-.72,.40,.65),.035,S,'metal')
pipe('Tie rod RH',(0,.38,.65),(.72,.40,.65),.035,S,'metal')
for x in (-.72,.72):
 cyl('Outer tie rod',(x,.40,.65),.06,.12,S,'metal',rot=(0,radians(90),0))
 tor('Tie rod boot',(x*.86,.40,.65),.06,.018,S,'rubber',rot=(0,radians(90),0))
# brake hydraulics
B=C['Next5_BrakeHydraulics']
cube('Brake master cylinder',(.35,1.18,1.62),(.16,.08,.10),B,'metal',.025)
cube('Brake fluid reservoir',(.35,1.25,1.78),(.13,.07,.10),B,'blue',.02)
pipe('Front brake hardline',(.35,1.12,1.58),(0,.70,1.05),.015,B,'metal')
pipe('Rear brake hardline',(.35,1.10,1.55),(0,.20,-1.15),.015,B,'metal')
cube('ABS hydraulic unit',(-.35,.65,1.08),(.16,.10,.14),B,'metal',.025)
for x in (-.74,.74): pipe('Flexible brake hose',(x,.55,1.10),(x,.45,1.55),.018,B,'rubber')
# 4WD driveline
D=C['Next5_4WDDriveline']
cube('Transfer case housing',(0,.35,-.52),(.28,.24,.32),D,'metal',.04)
pipe('Front prop shaft',(0,.35,.62),(0,.35,1.55),.065,D,'metal')
pipe('Rear prop shaft',(0,.35,-.52),(0,.35,-1.60),.075,D,'metal')
for z in (1.55,-1.60): cube('Driveshaft flange',(0,.35,z),(.12,.10,.08),D,'metal',.02)
cube('4WD shift motor',(.30,.35,-.52),(.10,.10,.12),D,'black',.02)
cube('Transfer case skid plate',(0,.05,-.62),(.38,.04,.28),D,'metal',.02)
# occupant safety
O=C['Next5_OccupantSafety']
cube('Driver airbag module',(.52,-.18,1.82),(.08,.035,.06),O,'dark',.02)
cube('Passenger airbag module',(-.48,-.16,1.92),(.22,.04,.07),O,'dark',.02)
cube('SRS control module',(0,-.15,1.08),(.12,.08,.06),O,'yellow',.015)
for x in (-.40,.40):
 pipe('Front seat belt webbing',(x,.80,1.82),(x,.30,1.20),.018,O,'red')
 cube('Seat belt retractor',(x,.76,1.25),(.05,.06,.12),O,'black',.02)
for x in (-.40,0,.40):
 cube('Rear seat belt upper anchor',(x,-.80,1.84),(.025,.025,.06),O,'metal',.01)
 cube('Child-seat lower anchor',(x,-.52,1.22),(.06,.02,.02),O,'metal',.01)
# wheels/tires
W=C['Next5_WheelsTires'];
for x in (-.74,.74):
 for z in (1.60,-1.60):
  tor('16 inch tire',(x,.48,z),.32,.10,W,'tire',rot=(0,radians(90),0));cyl('Alloy wheel',(x,.48,z),.24,.08,W,'alloy',rot=(0,radians(90),0));cyl('Wheel hub',(x,.48,z),.07,.10,W,'metal',rot=(0,radians(90),0));
  for i in range(5):
   import math
   a=2*math.pi*i/5;cyl('Lug nut',(x,.54+0.01*0,z),.018,.04,W,'metal',rot=(0,radians(90),0),v=12)
  pipe('Brake hose',(x,.55,z),(x,.70,z),.012,W,'rubber')
for c in C.values():
 c['accuracy_boundary']='Reference assembly; verify VIN option, dimensions, torque, materials, routing, and clearances against factory data or measurement'
 for o in c.objects:o['source_scope']='Next-five Level 1 chassis and safety detail'
scene=bpy.context.scene
for co in bpy.data.collections:co.hide_render=co.name not in C
bpy.ops.object.camera_add(location=(4,-5,3));cam=bpy.context.object;scene.camera=cam;cam.data.lens=52;cam.rotation_euler=(Vector((0,0,.5))-cam.location).to_track_quat('-Z','Y').to_euler()
for p,e,col in [((3,-3,5),1200,(1,.8,.65)),((-3,-2,3),900,(.4,.6,1)),((0,3,2),800,(1,.3,.15))]:bpy.ops.object.light_add(type='AREA',location=p);l=bpy.context.object;l.data.energy=e;l.data.size=3;l.data.color=col;l.rotation_euler=(Vector((0,0,.5))-l.location).to_track_quat('-Z','Y').to_euler()
scene.render.engine='BLENDER_EEVEE';scene.render.resolution_x=1800;scene.render.resolution_y=1200;scene.render.resolution_percentage=100;scene.render.filepath=PNG;scene.world.color=(.004,.006,.01);bpy.ops.render.render(write_still=True)
for co in bpy.data.collections:co.hide_render=False
bpy.ops.wm.save_as_mainfile(filepath=OUT);bpy.ops.object.select_all(action='SELECT');bpy.ops.export_scene.gltf(filepath=GLB,export_format='GLB',use_selection=True);bpy.ops.wm.save_as_mainfile(filepath=OUT);print('NEXT5_CHASSIS_SAFETY_COMPLETE',OUT,GLB,PNG)
