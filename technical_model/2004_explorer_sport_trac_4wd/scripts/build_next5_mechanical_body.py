import bpy, math
from mathutils import Vector
from math import radians
SRC='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_next5_systems.blend'
OUT='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_next5_mechanical_body.blend'
GLB='/home/ubuntu/ford-sport-trac/app/src/main/assets/models/ford_explorer_sport_trac_2004_next5_mechanical_body.glb'
PNG='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/renders/next5_mechanical_body_overview.png'
bpy.ops.wm.open_mainfile(filepath=SRC)
N=['Next5_Cooling','Next5_FuelDelivery','Next5_TransmissionService','Next5_BodyBed','Next5_GlassSeals']
for n in N:
 c=bpy.data.collections.get(n)
 if c: bpy.data.collections.remove(c)
C={n:bpy.data.collections.new(n) for n in N}
for c in C.values(): bpy.context.scene.collection.children.link(c)
def mat(n,col,metal=0,rough=.45):
 m=bpy.data.materials.get(n) or bpy.data.materials.new(n); m.diffuse_color=(*col,1); m.use_nodes=True; b=m.node_tree.nodes.get('Principled BSDF'); b.inputs['Base Color'].default_value=(*col,1); b.inputs['Metallic'].default_value=metal; b.inputs['Roughness'].default_value=rough; return m
M={'black':mat('Mechanical black',(.015,.02,.025),0,.6),'metal':mat('Mechanical steel',(.26,.29,.31),.75,.3),'rubber':mat('Mechanical rubber',(.008,.01,.012),0,.72),'red':mat('Coolant red',(.55,.01,.008),0,.3),'blue':mat('Coolant blue',(.01,.12,.55),0,.3),'green':mat('Fuel green',(.03,.25,.04),0,.4),'silver':mat('Body brightwork',(.5,.53,.55),.8,.25),'glass':mat('Vehicle glass',(.02,.08,.12),.15,.12),'seal':mat('Weather seal',(.01,.012,.014),0,.55),'white':mat('Body white',(.68,.7,.68),0,.35),'amber':mat('Amber lens',(.85,.24,.01),0,.25)}
def link(o,c,ma):
 for x in list(o.users_collection): x.objects.unlink(o)
 c.objects.link(o); o.data.materials.append(M[ma]); return o
def cube(n,p,s,c,ma,bev=.02,rot=(0,0,0)):
 bpy.ops.mesh.primitive_cube_add(location=p,rotation=rot); o=bpy.context.object; o.name=n; o.scale=s; bpy.ops.object.transform_apply(location=False,rotation=False,scale=True); q=o.modifiers.new('edge radius','BEVEL'); q.width=bev; q.segments=2; return link(o,c,ma)
def cyl(n,p,r,d,c,ma,rot=(0,0,0),v=28):
 bpy.ops.mesh.primitive_cylinder_add(vertices=v,radius=r,depth=d,location=p,rotation=rot); o=bpy.context.object; o.name=n; return link(o,c,ma)
def tor(n,p,R,r,c,ma,rot=(0,0,0)):
 bpy.ops.mesh.primitive_torus_add(major_radius=R,minor_radius=r,major_segments=40,minor_segments=10,location=p,rotation=rot); o=bpy.context.object; o.name=n; return link(o,c,ma)
def pipe(n,a,b,r,c,ma):
 d=Vector(b)-Vector(a); o=cyl(n,(Vector(a)+Vector(b))/2,r,d.length,c,ma); o.rotation_euler=d.to_track_quat('Z','Y').to_euler(); return o
# cooling system
K=C['Next5_Cooling']; cube('Radiator core',(0,.95,1.45),(.52,.06,.42),K,'metal',.025); cube('Radiator support',(0,.88,1.45),(.62,.05,.52),K,'black',.02); cyl('Cooling fan',(0,.78,1.42),.34,.06,K,'black',rot=(radians(90),0,0)); tor('Fan shroud',(0,.78,1.42),.38,.025,K,'metal',rot=(radians(90),0,0)); cube('Expansion tank',(-.62,.98,1.70),(.12,.13,.20),K,'white',.03); pipe('Upper radiator hose',(0,1.0,1.72),(.28,1.0,1.35),.055,K,'rubber'); pipe('Lower radiator hose',(0,1.0,1.20),(-.22,.75,1.05),.055,K,'rubber'); pipe('Heater hose supply',(-.25,1.0,1.2),(-.42,1.4,1.35),.035,K,'red'); pipe('Heater hose return',(-.20,1.0,1.1),(-.38,1.4,1.25),.035,K,'blue'); cyl('Water pump',(0,.78,1.10),.13,.18,K,'metal',rot=(0,radians(90),0))
# fuel delivery
F=C['Next5_FuelDelivery']
cube('Fuel tank service body',(0,.05,-.62),(.55,.18,.48),F,'black',.05)
cyl('Fuel pump module',(0,.28,-.45),.12,.12,F,'metal')
pipe('Fuel supply line',(0,.28,-.38),(.42,.7,.62),.018,F,'green')
pipe('Fuel return line',(.05,.25,-.38),(-.42,.7,.62),.018,F,'green')
cube('Fuel filter',(-.42,.5,.62),(.10,.06,.06),F,'metal',.02)
cube('Fuel rail',(.0,1.05,1.18),(.28,.04,.04),F,'metal',.015)
for x in (-.22,-.07,.07,.22): cyl('Fuel injector',(x,1.0,1.10),.025,.16,F,'metal')
pipe('EVAP vapor line',(.25,.2,-.45),(.45,1.1,1.0),.018,F,'rubber')
cube('EVAP purge valve',(.45,1.1,1.08),(.06,.05,.06),F,'black',.015)
# transmission service hardware
T=C['Next5_TransmissionService']
cube('5R55E transmission case',(0,.46,.20),(.32,.28,.52),T,'metal',.05)
cube('Transmission pan',(0,.18,.02),(.34,.25,.08),T,'black',.04)
cube('Valve body service plate',(0,.08,.02),(.20,.12,.025),T,'metal',.01)
cyl('Transmission filter',(0,.02,.14),.10,.04,T,'black',rot=(radians(90),0,0))
pipe('Transmission cooler line A',(-.25,.48,.35),(-.55,.90,1.20),.025,T,'metal')
pipe('Transmission cooler line B',(-.18,.40,.25),(-.48,.90,1.15),.025,T,'metal')
cube('Shift solenoid pack',(.18,.35,.28),(.12,.10,.16),T,'black',.02)
for x in (-.25,-.12,0,.12,.25): cyl('Transmission pan bolt',(x,.22,.10),.018,.12,T,'metal')
# body and bed hardware
B=C['Next5_BodyBed']
cube('Bed floor panel',(0,-1.25,1.0),(.68,.05,.46),B,'white',.03)
for x in (-.58,.58): cube('Bed side panel',(x,-1.25,1.38),(.05,.42,.38),B,'white',.03)
cube('Tailgate inner panel',(0,-1.72,1.22),(.66,.05,.30),B,'white',.03)
for x in (-.48,.48): cyl('Tailgate hinge',(x,-1.78,1.48),.04,.18,B,'metal',rot=(0,radians(90),0))
for x in (-.50,.50): cube('Bed tie down',(x,-1.25,1.55),(.06,.04,.07),B,'metal',.02)
for x in (-.62,.62): pipe('Bed rail seal',(x,-1.25,1.78),(x,-.75,1.78),.025,B,'rubber')
cube('Hood inner reinforcement',(0,1.46,1.95),(.58,.04,.20),B,'metal',.025)
for x in (-.45,.45): cyl('Hood hinge',(x,1.40,1.85),.035,.16,B,'metal',rot=(0,radians(90),0))
# glass and weather seals
G=C['Next5_GlassSeals']; cube('Windshield glass',(0,1.28,1.90),(.62,.025,.34),G,'glass',.03,rot=(radians(-8),0,0)); tor('Windshield perimeter seal',(0,1.25,1.90),.52,.025,G,'seal',rot=(radians(90),0,0)); cube('Rear window glass',(0,-.95,1.88),(.48,.025,.25),G,'glass',.025,rot=(radians(5),0,0)); tor('Rear window seal',(0,-.98,1.88),.38,.022,G,'seal',rot=(radians(90),0,0));
for x in (-.42,.42):
 cube('Front door glass',(x,.92,1.92),(.22,.02,.22),G,'glass',.02); pipe('Door glass run seal',(x,.88,1.92),(x,.88,1.50),.018,G,'seal'); cube('A pillar seal',(x*1.45,1.18,1.85),(.018,.018,.28),G,'seal',.01)
for c in C.values():
 c['accuracy_boundary']='Reference assembly; verify VIN option, dimensions, material, routing, fasteners, and clearances against factory data or measurement'
 for o in c.objects: o['source_scope']='Next-five Level 1 mechanical/body detail'
scene=bpy.context.scene
for co in bpy.data.collections: co.hide_render=co.name not in C
bpy.ops.object.camera_add(location=(4,-5,3.0)); cam=bpy.context.object; scene.camera=cam; cam.data.lens=52; cam.rotation_euler=(Vector((0,0,.5))-cam.location).to_track_quat('-Z','Y').to_euler()
for p,e,col in [((3,-3,5),1200,(1,.8,.65)),((-3,-2,3),900,(.4,.6,1)),((0,3,2),800,(1,.3,.15))]: bpy.ops.object.light_add(type='AREA',location=p); l=bpy.context.object; l.data.energy=e; l.data.size=3; l.data.color=col; l.rotation_euler=(Vector((0,0,.5))-l.location).to_track_quat('-Z','Y').to_euler()
scene.render.engine='BLENDER_EEVEE'; scene.render.resolution_x=1800; scene.render.resolution_y=1200; scene.render.resolution_percentage=100; scene.render.filepath=PNG; scene.world.color=(.004,.006,.01); bpy.ops.render.render(write_still=True)
for co in bpy.data.collections: co.hide_render=False
bpy.ops.wm.save_as_mainfile(filepath=OUT); bpy.ops.object.select_all(action='SELECT'); bpy.ops.export_scene.gltf(filepath=GLB,export_format='GLB',use_selection=True); bpy.ops.wm.save_as_mainfile(filepath=OUT); print('NEXT5_MECHANICAL_BODY_COMPLETE',OUT,GLB,PNG)
