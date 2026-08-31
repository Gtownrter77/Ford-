import bpy, json, os
p=bpy.data.filepath
collections={}
for c in bpy.data.collections:
    if c.name in {'Engine','Transmission','Chassis','Suspension','Wiring','Fasteners','Interior','Body','Annotations','Presentation'}:
        collections[c.name]={'objects':len(c.objects),'types':{},'animated':0}
        for o in c.objects:
            collections[c.name]['types'][o.type]=collections[c.name]['types'].get(o.type,0)+1
            if o.animation_data and o.animation_data.action:
                collections[c.name]['animated']+=1
anim=[]; bezier=True
for o in bpy.data.objects:
    if o.animation_data and o.animation_data.action:
        frames=[]
        for fc in o.animation_data.action.fcurves:
            for kp in fc.keyframe_points:
                frames.append(kp.co[0]); bezier = bezier and kp.interpolation=='BEZIER'
        anim.append({'object':o.name,'min_frame':min(frames),'max_frame':max(frames)})
report={'blend_path':p,'object_count':len(bpy.data.objects),'mesh_count':len(bpy.data.meshes),'curve_count':len(bpy.data.curves),'materials':len(bpy.data.materials),'collections':collections,'animated_object_count':len(anim),'all_keyframes_bezier':bezier,'animation_frame_range':(bpy.context.scene.frame_start,bpy.context.scene.frame_end),'scene_properties':{k:bpy.context.scene[k] for k in bpy.context.scene.keys()}}
open('/home/ubuntu/ford_explorer_sport_trac_2004/blend_validation.json','w').write(json.dumps(report,indent=2,default=str))
print(json.dumps(report,indent=2,default=str))
