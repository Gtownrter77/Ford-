#!/usr/bin/env python3
"""Extract a named-node inventory and optional classified GLB layers."""
from __future__ import annotations
import argparse, csv, hashlib, json, re
from pathlib import Path
import numpy as np
import trimesh

SYSTEM_RULES = {
    "fuel": ("fuel", "tank", "injector", "pump"),
    "transmission": ("transmission", "transfer case", "gear"),
    "suspension": ("spring", "shock", "control arm", "hub", "brake", "axle"),
    "wiring": ("wire", "harness", "fuse", "connector", "battery", "alternator"),
    "steering": ("steer", "column", "rack", "tie rod"),
    "climate": ("heater", "hvac", "evaporator", "condenser", "blower"),
    "radio": ("radio", "speaker", "head unit"),
    "drivetrain": ("engine", "cylinder", "intake", "radiator", "manifold", "exhaust", "differential", "propeller", "driveshaft", "fastener", "bolt"),
}
GUIDE = re.compile(r"label_|scale bar|technical ground", re.I)

def classify(name: str) -> str:
    n = name.lower()
    for system, terms in SYSTEM_RULES.items():
        if any(t in n for t in terms): return system
    return "body"

def bounds_for(mesh: trimesh.Trimesh, transform: np.ndarray):
    if len(mesh.vertices) == 0: return None
    v = trimesh.transform_points(mesh.vertices, transform)
    lo, hi = v.min(axis=0), v.max(axis=0)
    return lo.tolist(), hi.tolist(), (hi-lo).tolist()

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("glb", type=Path)
    ap.add_argument("--out", type=Path, required=True)
    ap.add_argument("--split-glb", action="store_true")
    args = ap.parse_args()
    args.out.mkdir(parents=True, exist_ok=True)
    if not args.glb.exists(): raise SystemExit(f"missing GLB: {args.glb}")
    digest = hashlib.sha256(args.glb.read_bytes()).hexdigest()
    scene = trimesh.load(args.glb, force="scene", process=False)
    records, grouped, group_bounds = [], {}, {}
    for idx, node in enumerate(scene.graph.nodes_geometry):
        transform, geoms = scene.graph[node]
        geom = scene.geometry.get(geoms)
        if geom is None or not hasattr(geom, "vertices"): continue
        name = str(node); system = classify(name)
        b = bounds_for(geom, transform)
        if b is None: continue
        lo, hi, dims = b
        rec = {"source_name": name, "source_node_index": idx, "source_mesh": geoms,
               "system": system, "is_guide": bool(GUIDE.search(name)),
               "bounds_min_m": lo, "bounds_max_m": hi, "dimensions_m": dims,
               "vertex_count": int(len(geom.vertices)), "face_count": int(len(geom.faces))}
        records.append(rec); grouped.setdefault(system, []).append((name, geom, transform))
        gb = group_bounds.setdefault(system, {"min": np.array([np.inf]*3), "max": np.array([-np.inf]*3), "count": 0})
        gb["min"] = np.minimum(gb["min"], lo); gb["max"] = np.maximum(gb["max"], hi); gb["count"] += 1
    records.sort(key=lambda x: (x["system"], x["source_name"]))
    (args.out / "parts_list.json").write_text(json.dumps({"source": str(args.glb), "sha256": digest, "declared_units": "meters (per repository model README)", "parts": records}, indent=2))
    with (args.out / "parts_list.csv").open("w", newline="") as f:
        fields = ["source_name","source_node_index","source_mesh","system","is_guide","dimensions_m","bounds_min_m","bounds_max_m","vertex_count","face_count"]
        w=csv.DictWriter(f, fieldnames=fields); w.writeheader(); w.writerows(records)
    summary=[]
    for system, items in sorted(grouped.items()):
        gb=group_bounds[system]
        summary.append({"system":system,"node_count":len(items),"bounds_min_m":gb["min"].tolist(),"bounds_max_m":gb["max"].tolist(),"dimensions_m":(gb["max"]-gb["min"]).tolist()})
        if args.split_glb:
            layer=trimesh.Scene()
            for name, geom, transform in items:
                layer.add_geometry(geom.copy(), node_name=name, geom_name=name, transform=transform)
            layer.export(args.out / "layers" / f"{system}.glb") if (args.out / "layers").mkdir(exist_ok=True) is None else None
    (args.out / "assembly_summary.csv").write_text("system,node_count,bounds_min_m,bounds_max_m,dimensions_m\n" + "\n".join(f'{x["system"]},{x["node_count"]},"{x["bounds_min_m"]}","{x["bounds_max_m"]}","{x["dimensions_m"]}"' for x in summary) + "\n")
    evidence={"source_sha256":digest,"declared_units":"meters","overall_scale_status":"supported by repository declaration; component engineering accuracy requires independent factory dimensions","claims":[{"claim":"Overall model uses meter units","status":"supported","basis":"repository app/src/main/assets/models/README.md"},{"claim":"Individual fastener diameter/thread/grade/torque","status":"not_verified","basis":"GLB geometry and node names alone do not establish these specifications"}]}
    (args.out / "scale_evidence.json").write_text(json.dumps(evidence, indent=2))
    print(json.dumps({"nodes_with_geometry":len(records),"systems":len(summary),"sha256":digest,"out":str(args.out)}, indent=2))
if __name__ == "__main__": main()
