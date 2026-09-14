---
name: glb-assembly-verification
description: Extract named assemblies, parts lists, per-layer GLBs, node dimensions, and scale evidence from GLB vehicle models; use when auditing a digital twin or comparing modeled components with factory specifications.
---

# GLB Assembly Verification

Use the bundled `scripts/extract_glb_inventory.py` for deterministic GLB inspection. It reports node names, mesh/material counts, world-space bounds in meters, classified systems, and optional per-system GLB exports.

## Workflow

1. Identify the authoritative GLB and its declared unit convention. Treat `1.0 = 1 meter` only when the source documentation or a known reference dimension supports it.
2. Run:

   ```bash
   python scripts/extract_glb_inventory.py vehicle.glb --out exports --split-glb
   ```

3. Review `parts_list.csv`, `parts_list.json`, `assembly_summary.csv`, and `scale_evidence.json`. Use the per-system files under `layers/` for isolated assembly review.
4. Preserve the source GLB. Never rescale, replace, or simplify it during extraction.
5. Classify nodes by names as a navigation aid, not as proof of engineering identity. Keep `source_name`, `source_node_index`, and `source_mesh` in every export record.
6. For factory verification, distinguish:
   - **Geometric evidence:** modeled position and bounding-box dimensions measured from the GLB.
   - **Configuration evidence:** year/engine/drivetrain/transmission and source-manual fitment.
   - **Factory evidence:** an authoritative drawing, parts catalog, service manual, or torque table that states the dimension/specification.
7. Do not infer bolt diameter, thread pitch, grade, washer dimensions, gasket thickness, wire gauge, or torque from a visual GLB alone. Mark these as `not_verified` unless a factory source explicitly supplies them.
8. Report tolerances and mismatches numerically. A model being 1:1 overall does not establish that every subcomponent is 1:1.

## Output contract

Produce a concise report with the source hash, node/mesh/material counts, declared units, per-layer counts and bounds, selected-component measurements, factory references, and a verification status of `verified`, `partially_verified`, or `not_verified` for each claim.

The skill is intentionally additive: extraction must never modify the original model. Use the source repository’s model README and validation documents as provenance, and retain caveats that the model may be a teaching/reference representation rather than OEM CAD.
