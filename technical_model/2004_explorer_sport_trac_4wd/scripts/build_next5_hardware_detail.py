import bpy, math
from mathutils import Vector
from math import radians
SRC='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_next5_chassis_safety.blend'
OUT='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/model/explorer_sport_trac_next5_hardware_detail.blend'
GLB='/home/ubuntu/ford-sport-trac/app/src/main/assets/models/ford_explorer_sport_trac_2004_next5_hardware_detail.glb'
PNG='/home/ubuntu/ford-sport-trac/technical_model/2004_explorer_sport_trac_4wd/renders/next5_hardware_detail_overview.png'
bpy.ops.wm.open_mainfile(filepath=SRC)
COLS=['Next5_EngineFasteners','Next5_HVACBodyFasteners','Next5_SuspensionFasteners','Next5_ServicePoints','Next5_ElectricalConnectors']
for n in COLS:
 c=bpy.data.collections.get(n)
 if c:bpy.data.collections.remove(c)
C={n:bpy.data.collections.new(n) for n in COLS}
for c in C.values():bpy.context.scene.collection.children.link(c)
def mat(n,col,metal=0,rough=.45):
 m=bpy.data.materials.get(n) or bpy.data.materials.new(n);m.diffuse_color=(*col,1);m.use_nodes=True;b=m.node_tree.nodes.get('Principled BSDF');b.inputs['Base Color'].default_value=(*col,1);b.inputs['Metallic'].default_value=metal;b.inputs['Roughness'].default_value=rough;return m
M={'steel':mat('Detail steel',(.3,.33,.35),.8,.3),'zinc':mat('Detail zinc',(.55,.57,.58),.75,.25),'rubber':mat('Detail rubber',(.008,.01,.012),0,.78),'plastic':mat('Detail connector plastic',(.02,.04,.05),0,.55),'red':mat('Detail red',(.65,.01,.008),.15,.3),'yellow':mat('Detail yellow',(.9,.55,.01),0,.3),'blue':mat('Detail blue',(.01,.1,.55),0,.3),'copper':mat('Detail copper',(.5,.12,.03),.75,.3),'black':mat('Detail black',(.01,.012,.015),0,.7)}
def link(o,c,ma):
 for x in list(o.users_collection):x.objects.unlink(o)
 c.objects.link(o);o.data.materials.append(M[ma]);return o
def cyl(n,p,r,d,c,ma,rot=(0,0,0),v=16):
 bpy.ops.mesh.primitive_cylinder_add(vertices=v,radius=r,depth=d,location=p,rotation=rot);o=bpy.context.object;o.name=n;return link(o,c,ma)
def tor(n,p,R,r,c,ma,rot=(0,0,0)):
 bpy.ops.mesh.primitive_torus_add(major_radius=R,minor_radius=r,major_segments=24,minor_segments=8,location=p,rotation=rot);o=bpy.context.object;o.name=n;return link(o,c,ma)
def cube(n,p,s,c,ma,b=.01):
 bpy.ops.mesh.primitive_cube_add(location=p);o=bpy.context.object;o.name=n;o.scale=s;bpy.ops.object.transform_apply(location=False,rotation=False,scale=True);q=o.modifiers.new('edge radius','BEVEL');q.width=b;q.segments=2;return link(o,c,ma)
def pipe(n,a,b,r,c,ma):
 d=Vector(b)-Vector(a);o=cyl(n,(Vector(a)+Vector(b))/2,r,d.length,c,ma);o.rotation_euler=d.to_track_quat('Z','Y').to_euler();return o
def bolt_stack(n,p,axis,c,thread='M8 x 1.25',torque='VIN-specific'):
 rot=(0,radians(90),0) if axis=='X' else ((radians(90),0,0) if axis=='Y' else (0,0,0))
 cyl(n+' shank',p,.018,.10,c,'steel',rot); tor(n+' washer',p,.035,.009,c,'zinc',rot); cyl(n+' hex head',p,.032,.028,c,'steel',rot,v=6); bpy.context.object['thread_spec']=thread; bpy.context.object['torque_boundary']=torque
# 1 engine fasteners
E=C['Next5_EngineFasteners']
for i,x in enumerate((-.30,-.10,.10,.30)):
 for z in (1.12,1.30): bolt_stack('Engine manifold bolt %02d'%(i+1),(x,1.30,z),'Y',E)
for x in (-.48,.48):
 for z in (.88,1.12): bolt_stack('Accessory bracket bolt',(x,.70,z),'X',E,thread='M10 x 1.50')
for x in (-.62,.62): tor('Engine hose clamp',(x,1.0,1.55),.075,.012,E,'steel',rot=(radians(90),0,0))
# 2 HVAC/body fasteners, washers, seals
H=C['Next5_HVACBodyFasteners']
for i,x in enumerate((-.45,-.25,0,.25,.45)):
 bolt_stack('HVAC case bolt',(x,-.30,1.45),'Y',H,thread='M6 x 1.00')
for x in (-.52,.52):
 tor('HVAC foam seal',(x,-.28,1.52),.10,.018,H,'rubber',rot=(radians(90),0,0)); bolt_stack('HVAC bracket bolt',(x,-.32,1.52),'Y',H,thread='M6 x 1.00')
for x in (-.68,.68):
 bolt_stack('Body mount bolt',(x,.38,.48),'Y',H,thread='M12 x 1.75')
 tor('Body mount isolator',(x,.38,.48),.09,.025,H,'rubber',rot=(radians(90),0,0))
# 3 suspension fasteners
S=C['Next5_SuspensionFasteners']
for x in (-.74,.74):
 for z in (1.60,-1.60):
  for j in range(3): bolt_stack('Suspension control-arm bolt',(x+(j-1)*.06,.38,z),'X',S,thread='M12 x 1.75',torque='Confirm workshop manual')
  for j in range(2): bolt_stack('Brake caliper bolt',(x,.50,z+(j-.5)*.12),'X',S,thread='M12 x 1.75',torque='Confirm workshop manual')
  tor('Wheel hub seal',(x,.54,z),.12,.018,S,'rubber',rot=(0,radians(90),0))
# 4 service points and plugs
P=C['Next5_ServicePoints']
for n,p,col in [('Engine oil drain',(0,.35,.62),'black'),('Transmission fill',(.35,.36,.22),'steel'),('Transfer case fill',(.28,.35,-.42),'steel'),('Rear axle fill',(0,.36,-1.60),'steel'),('Coolant drain',(0,.88,1.10),'blue')]:
 cyl(n+' plug',p,.045,.06,P,col,rot=(radians(90),0,0),v=6); tor(n+' sealing washer',p,.055,.012,P,'zinc',rot=(radians(90),0,0))
for p in ((-.30,.35,.62),(.30,.36,.22),(.25,.35,-.42),(0,.36,-1.60)):
 pipe('Service-point safety wire',p,(p[0]+.08,p[1]+.08,p[2]+.06),.008,P,'copper')
# 5 electrical connectors and terminals
X=C['Next5_ElectricalConnectors']
for i,(p,sz) in enumerate([((-.65,.9,1.0),(.10,.06,.06)),((.65,.9,1.0),(.10,.06,.06)),((-.45,.65,1.08),(.08,.06,.07)),((.45,1.2,1.35),(.08,.06,.08)),((0,-.15,1.08),(.10,.06,.06))]):
 cube('Weather-sealed connector %02d'%(i+1),p,sz,X,'plastic',.01)
 for j in range(4): cube('Connector terminal %02d_%02d'%(i+1,j+1),(p[0]+(j-1.5)*.025,p[1]-.065,p[2]),(.006,.008,.012),X,'copper',.002)
 tor('Connector seal %02d'%(i+1),(p[0],p[1]-.07,p[2]),.06,.012,X,'rubber',rot=(radians(90),0,0))
for a,b in [((-.65,.9,1.0),(-.45,.65,1.08)),((.65,.9,1.0),(.45,1.2,1.35)),((-.45,.65,1.08),(0,-.15,1.08))]:pipe('Harness branch',a,b,.012,X,'rubber')
for c in C.values():
 c['scale_contract']='All added geometry uses the project meter world scale; dimensions are reference values and must be reconciled to VIN-specific OEM data before claiming production accuracy'
 c['detail_level']='washer, nut, seal, clamp, plug, terminal, connector level'
 for o in c.objects:o['verification_status']='reference detail; verify against factory exploded view or measured part'
scene=bpy.context.scene
for co in bpy.data.collections:co.hide_render=co.name not in C
bpy.ops.object.camera_add(location=(3.5,-4.5,2.6));cam=bpy.context.object;scene.camera=cam;cam.data.lens=55;cam.rotation_euler=(Vector((0,0,.65))-cam.location).to_track_quat('-Z','Y').to_euler()
for p,e,col in [((3,-3,5),1300,(1,.8,.65)),((-3,-2,3),950,(.4,.6,1)),((0,3,2),850,(1,.3,.15))]:bpy.ops.object.light_add(type='AREA',location=p);l=bpy.context.object;l.data.energy=e;l.data.size=3;l.data.color=col;l.rotation_euler=(Vector((0,0,.65))-l.location).to_track_quat('-Z','Y').to_euler()
scene.render.engine='BLENDER_EEVEE';scene.render.resolution_x=1800;scene.render.resolution_y=1200;scene.render.resolution_percentage=100;scene.render.filepath=PNG;scene.world.color=(.004,.006,.01);bpy.ops.render.render(write_still=True)
for co in bpy.data.collections:co.hide_render=False
bpy.ops.wm.save_as_mainfile(filepath=OUT);bpy.ops.object.select_all(action='SELECT');bpy.ops.export_scene.gltf(filepath=GLB,export_format='GLB',use_selection=True);bpy.ops.wm.save_as_mainfile(filepath=OUT);print('NEXT5_HARDWARE_DETAIL_COMPLETE',OUT,GLB,PNG)
