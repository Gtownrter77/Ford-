# 2004 Ford Explorer Sport Trac Technical Teaching Model

## Project scope

This deliverable is a vocational-teaching visualization for the **2004 Ford Explorer Sport Trac, 2WD, V6 4.0L, VIN K, Flex Fuel**. The supplied LEMON Manuals bundle was downloaded and extracted locally. The resulting model is an organized, annotated, parametric system representation intended to support service-system teaching and presentation. It is not a replacement for Ford’s service procedures, dimensional inspection, or production CAD.

> **Accuracy statement:** The service manual is primarily procedural and illustrated service literature. It does not supply a complete native CAD definition for every body panel, fastener, harness path, or tolerance. Therefore, major system placement follows the vehicle architecture and manual terminology, while many geometry dimensions and detailed fastener locations are modeled as teaching approximations and are marked by the model scope property.

## Manual extraction

The archive contained **16,111 files** and expanded to **9,185 HTML manual pages** with **17,887 referenced images**. A parser generated `manual_pages.csv`, `summary.json`, and `titles_and_systems.md`. Keyword classification found pages relevant to engine, transmission, suspension, body, wiring, and interior systems. Torque-related terms occurred on 546 pages, dimensional-unit candidates occurred on 558 pages, and candidate part-number tokens were present on all 9,185 pages; the latter is intentionally labeled as a candidate extraction because catalog identifiers and alphanumeric service text require component-by-component human verification.

| Extracted item | Result |
|---|---:|
| Archive files | 16,111 |
| HTML manual pages | 9,185 |
| Referenced images | 17,887 |
| Engine-related pages | 2,272 |
| Transmission-related pages | 1,308 |
| Suspension-related pages | 1,489 |
| Body-related pages | 1,781 |
| Wiring-related pages | 2,735 |
| Interior-related pages | 1,553 |
| Pages with torque-related terms | 546 |
| Pages with dimensional-unit candidates | 558 |

The raw extracted manual remains in the `manual/` directory. The parser is retained as `extract_manual.py` so the index can be regenerated or refined for particular subsystem terms.

## Blender model contents

The Blender scene is organized into the requested collections: `Engine`, `Transmission`, `Chassis`, `Suspension`, `Wiring`, `Fasteners`, `Interior`, `Body`, `Annotations`, and `Presentation`. Major modeled assemblies include a 4.0L V6 teaching representation with block, cylinder heads, valve covers, intake, throttle body, fuel rails/injectors, crank pulley, cooling fan, radiator, and hoses; a 5R55E automatic transmission representation; propeller shaft and rear axle; frame rails and crossmembers; suspension arms, stabilizers, shocks, brake discs, hubs, tires and wheels; fuel tank and exhaust; body/cab/bed/doors/hood/windows/lamps/bumpers; an interior panel, seats, and steering wheel; representative electrical harnesses and connectors; and representative fasteners including frame mounts, engine fasteners, and wheel lugs.

The scene contains **157 objects**, including **130 meshes**, **23 curves**, **8 annotation font objects**, one camera, three area lights, and 16 materials. Materials distinguish painted bodywork, rubber, glass, steel, cast aluminum, iron, polymer, wiring colors, lamps, and presentation annotations.

## Technical presentation setup

Three labeled views were rendered: front three-quarter, side, and rear three-quarter. The scene uses a dark technical presentation environment, area lights, Freestyle contour lines, a 2 m reference bar, visible system labels, and a compositing glow pass for lamps and highlights. A Line Art modifier hook is attempted on mesh objects where supported by the installed Blender version; technical contour output is guaranteed through Freestyle, while each relevant object retains a custom property indicating whether the Line Art modifier was applied or requested.

The exploded view is keyed from frame 1 to frame 80. Components in the major system collections move in staggered directions to expose assembly relationships. All inspected animation keyframes use **Bezier interpolation**. The scene’s default timeline remains 1–250 for further instructional additions.

| Validation item | Result |
|---|---:|
| Animated objects | 133 |
| All inspected keyframes Bezier | Yes |
| Collections present | 10 requested organizational collections |
| Render dimensions | 3840 × 2160 |
| GLB export | Completed |
| Blender version used | 4.0.2 |

## Deliverables

The primary files are `explorer_sport_trac_teaching_model.blend`, `explorer_sport_trac_teaching_model.glb`, `front_3_4_4k.png`, `side_4k.png`, and `rear_3_4_4k.png`. Supporting files include `extract_manual.py`, the structured extraction outputs under `extracted/`, `build_vehicle.py`, `render_4k.py`, `validate_blend.py`, and `blend_validation.json`.

## Recommended next refinement

For a final project requiring production-level mechanical accuracy, the next phase should manually reconcile each modeled assembly against the corresponding exploded-view page, convert verified torque values into object-level metadata, import or construct a complete connector/pin database, and replace the teaching primitives with measured or OEM CAD geometry. The current file is intentionally structured to make that refinement practical without reorganizing the scene.
