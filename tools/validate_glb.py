import bpy
import sys
from pathlib import Path

path = Path(sys.argv[-1])
bpy.ops.wm.read_factory_settings(use_empty=True)
result = bpy.ops.import_scene.gltf(filepath=str(path))
if result != {'FINISHED'}:
    raise RuntimeError(f"GLB import failed: {result}")
objects = [obj for obj in bpy.context.scene.objects if obj.type == 'MESH']
assert objects, "GLB contains no mesh objects"
assert all(len(obj.data.vertices) > 0 and len(obj.data.polygons) > 0 for obj in objects)
assert all(all(abs(v.co.x) < 1e6 and abs(v.co.y) < 1e6 and abs(v.co.z) < 1e6 for v in obj.data.vertices) for obj in objects)
materials = {mat.name for obj in objects for mat in obj.data.materials if mat}
print(f"GLB validation: PASS; mesh_objects={len(objects)}; materials={len(materials)}; file_bytes={path.stat().st_size}")
