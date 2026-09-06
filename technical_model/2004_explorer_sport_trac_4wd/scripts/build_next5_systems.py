import bpy, math
from mathutils import Vector
from math import radians
SRC='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_interior_level1.blend'
OUT='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_next5_systems.blend'
GLB='/home/ubuntu/ford-sport-trac/app/src/main/assets/models/ford_explorer_sport_trac_2004_next5_systems.glb'
PNG='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/renders/next5_systems_overview.png'
bpy.ops.wm.open_mainfile(filepath=SRC)
for n in ['Next5_EngineBay','Next5_SuspensionBrakes','Next5_UnderbodyExhaust','Next5_ElectricalHarness','Next5_ExteriorTrim']:
 c=bpy.data.collections.get(n)
 if c: bpy.data.collections.remove(c)
C={n:bpy.data.collections.new(n) for n in ['Next5_EngineBay','Next5_SuspensionBrakes','Next5_UnderbodyExhaust','Next5_ElectricalHarness','Next5_ExteriorTrim']}
for c in C.values(): bpy.context.scene.collection.children.link(c)
def material(n,col,metal=0,rough=.45):
 m=bpy.data.materials.get(n) or bpy.data.materials.new(n); m.diffuse_color=(*col,1); m.use_nodes=True; b=m.node_tree.nodes.get('Principled BSDF'); b.inputs['Base Color'].default_value=(*col,1); b.inputs['Metallic'].default_value=metal; b.inputs['Roughness'].default_value=rough; return m
M={'dark':material('System dark',(0.02,.025,.03),0,.55),'metal':material('System metal',(.25,.28,.3),.75,.3),'rubber':material('System rubber',(.008,.01,.012),0,.75),'red':material('System brake red',(.5,.015,.01),.2,.35),'yellow':material('System warning yellow',(.8,.5,.02),0,.3),'wire':material('System wiring',(.01,.015,.02),0,.65),'copper':material('System copper',(.5,.12,.03),.7,.3),'glass':material('System lamp glass',(.05,.15,.18),.15,.2),'white':material('System lamp white',(.8,.8,.7),0,.2)}
def obj(o,c,ma):
 for x in list(o.users_collection): x.objects.unlink(o)
 c.objects.link(o); o.data.materials.append(ma); return o
def cube(n,p,s,c,ma,bev=.02):
 bpy.ops.mesh.primitive_cube_add(location=p); o=obj(bpy.context.object,c,M[ma]); o.name=n; o.scale=s; bpy.ops.object.transform_apply(location=False,rotation=False,scale=True)
 if bev: q=o.modifiers.new('edge radius','BEVEL'); q.width=bev; q.segments=2
 return o
def cyl(n,p,r,d,c,ma,rot=(0,0,0),v=24):
 bpy.ops.mesh.primitive_cylinder_add(vertices=v,radius=r,depth=d,location=p,rotation=rot); o=obj(bpy.context.object,c,M[ma]); o.name=n; return o
def tor(n,p,R,r,c,ma,rot=(0,0,0)):
 bpy.ops.mesh.primitive_torus_add(major_radius=R,minor_radius=r,major_segments=32,minor_segments=10,location=p,rotation=rot); o=obj(bpy.context.object,c,M[ma]); o.name=n; return o
def pipe(n,a,b,r,c,ma):
 mid=(Vector(a)+Vector(b))/2; d=Vector(b)-Vector(a); o=cyl(n,mid,r,d.length,c,ma); o.rotation_euler=d.to_track_quat('Z','Y').to_euler(); return o
# 1 engine bay
E=C['Next5_EngineBay']; cube('4.0L SOHC block',(0,.72,.98),(.38,.35,.52),E,'dark',.08); cube('Intake manifold',(0,1.10,1.22),(.28,.18,.20),E,'metal',.04); cube('Valve cover LH',(-.28,.85,1.25),(.10,.30,.18),E,'metal',.04); cube('Valve cover RH',(.28,.85,1.25),(.10,.30,.18),E,'metal',.04); cyl('Throttle body',(0,1.32,1.22),.10,.18,E,'metal',rot=(radians(90),0,0)); cyl('Alternator',(-.48,.72,1.0),.14,.22,E,'metal',rot=(0,radians(90),0)); tor('Serpentine belt',(-.48,.72,1.0),.18,.025,E,'rubber',rot=(0,radians(90),0)); cube('Air filter box',(.55,1.02,1.02),(.20,.24,.22),E,'dark',.04); pipe('Intake snorkel',(.40,1.12,1.13),(.02,1.27,1.22),.06,E,'rubber'); cube('Battery',(-.62,1.00,.95),(.14,.22,.18),E,'dark',.03); pipe('Positive battery cable',(-.52,1.0,1.1),(-.2,.82,1.3),.015,E,'copper')
# 2 suspension/brakes
S=C['Next5_SuspensionBrakes']
for x in (-.74,.74):
 for z in (1.60,-1.60):
  cyl('Coilover strut',(x,.55,z),.07,.70,S,'metal'); cyl('Brake rotor',(x,.45,z),.22,.05,S,'metal',rot=(0,radians(90),0)); tor('Brake caliper',(x,.45,z),.20,.035,S,'red',rot=(0,radians(90),0)); pipe('Control arm',(x,.38,z),(x*.35,.28,z),.045,S,'metal'); pipe('Brake hose',(x,.50,z),(x*.8,.65,z),.012,S,'rubber')
for z in (1.60,-1.60): pipe('Sway bar',( -.70,.32,z),(.70,.32,z),.025,S,'metal')
# 3 underbody/exhaust
U=C['Next5_UnderbodyExhaust']; cube('Fuel tank',(0,.20,-.62),(.55,.18,.50),U,'dark',.05); pipe('Exhaust front',(0,.18,.75),(0,.12,-.15),.05,U,'metal'); pipe('Exhaust rear',(0,.12,-.15),(0,.12,-1.55),.05,U,'metal'); cube('Muffler',(0,.12,-.72),(.20,.14,.42),U,'metal',.05); pipe('Tailpipe',(0,.12,-1.05),(.30,.22,-1.80),.05,U,'metal'); cube('Transfer case',(0,.35,-.52),(.25,.22,.32),U,'metal',.04); pipe('Rear driveshaft',(0,.34,-.5),(0,.32,-1.6),.08,U,'metal')
# 4 electrical harness
W=C['Next5_ElectricalHarness']; pipe('Main engine harness',(-.65,.9,.95),(.65,.9,1.0),.025,W,'wire'); pipe('Dash harness',(-.65,1.55,.9),(.65,1.55,.9),.022,W,'wire'); pipe('Left lamp harness',(-.65,1.3,1.0),(-.78,1.3,1.7),.018,W,'wire'); pipe('Right lamp harness',(.65,1.3,1.0),(.78,1.3,1.7),.018,W,'wire'); cube('Junction box',(.45,1.2,1.35),(.10,.08,.12),W,'dark',.02); cube('ABS module',(-.45,.45,.75),(.12,.08,.12),W,'dark',.02)
# 5 exterior lighting/trim
X=C['Next5_ExteriorTrim'];
for x in (-.62,.62):
 cube('Headlamp housing',(x,1.55,1.65),(.18,.08,.14),X,'dark',.03); cube('Headlamp lens',(x,1.64,1.65),(.13,.02,.09),X,'white',.02); cube('Amber marker',(x*1.08,1.63,1.52),(.035,.02,.045),X,'yellow',.01); cube('Rear lamp',(x,-1.78,1.25),(.12,.05,.24),X,'red',.02)
for x in (-.68,.68): cube('Door mirror',(x,1.0,1.85),(.12,.08,.08),X,'dark',.03)
for x in (-.55,0,.55): cube('Tailgate trim',(x,-1.85,1.48),(.18,.025,.025),X,'metal',.01)
for c in C.values():
 c['accuracy_boundary']='Reference assembly; exact VIN option, dimensions, routing, fasteners, and clearances require factory data or measurement'
 for o in c.objects: o['source_scope']='Next-five Level 1 system detail'
# render overview
scene=bpy.context.scene
for co in bpy.data.collections: co.hide_render = co.name not in C or co.name=='Presentation'
bpy.ops.object.camera_add(location=(4,-5,3.1)); cam=bpy.context.object; scene.camera=cam; cam.data.lens=52; cam.rotation_euler=(Vector((0,0,0.4))-cam.location).to_track_quat('-Z','Y').to_euler()
for p,e,col in [((3,-3,5),1200,(1,.8,.65)),((-3,-2,3),900,(.4,.6,1)),((0,3,2),800,(1,.3,.15))]: bpy.ops.object.light_add(type='AREA',location=p); l=bpy.context.object; l.data.energy=e; l.data.size=3; l.data.color=col; l.rotation_euler=(Vector((0,0,.5))-l.location).to_track_quat('-Z','Y').to_euler()
scene.render.engine='BLENDER_EEVEE'; scene.render.resolution_x=1800; scene.render.resolution_y=1200; scene.render.resolution_percentage=100; scene.render.filepath=PNG; scene.world.color=(.004,.006,.01); bpy.ops.render.render(write_still=True)
for co in bpy.data.collections: co.hide_render=False
bpy.ops.wm.save_as_mainfile(filepath=OUT); bpy.ops.object.select_all(action='SELECT'); bpy.ops.export_scene.gltf(filepath=GLB,export_format='GLB',use_selection=True); bpy.ops.wm.save_as_mainfile(filepath=OUT); print('NEXT5_COMPLETE',OUT,GLB,PNG)
