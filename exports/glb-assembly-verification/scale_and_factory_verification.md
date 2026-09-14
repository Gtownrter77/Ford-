# GLB Scale and Engine-Fastener Verification

- Source SHA-256: `4577bfc36d3530919761464a2869405c715c10c454c7ad7f317f09629950ff52`
- Declared units: **1.0 = 1 meter**, per `app/src/main/assets/models/README.md`.
- Extracted non-guide envelope (axis order in GLB): `[5.170000102370977, 2.43499992787838, 2.1800000369548798]` m.
- Owner Guide reference envelope: `[5.2298599999999995, 1.8237199999999998, 1.77546]` m (205.9 × 71.8 × 69.9 in converted at 25.4 mm/in).
- Semantic-axis comparison (X length, Y height, Z width): model `[5.170000102370977, 2.43499992787838, 2.1800000369548798]` m vs reference `[length, height, width]` `[5.2298599999999995, 1.77546, 1.8237199999999998]` m; absolute differences `[-0.05986, 0.65954, 0.35628]` m; relative differences `[-1.1, 37.1, 19.5]`%.
- Envelope contributors: X min/max are `Vertical tail lamp` / `Lower dark bumper`; Y min/max are `Sport Trac tire` / `Roof rail.001`; Z min/max are the two outer `Sport Trac tire` nodes.
- Why the width/height differ: the GLB envelope includes tire-to-tire lateral span and tire-ground-to-roof-rail height. The Owner Guide dimensions are vehicle reference dimensions with a different measurement convention/configuration. The repository does not provide a certified datum map proving that these envelope definitions are identical.
- Overall scale status: **length-supported, width/height not fully verified**. This is a measurement-convention/configuration discrepancy to resolve, not a reason to alter the existing model.

## Extracted fastener geometry

| Named GLB nodes | Count | Bounding dimensions (m) | Bounding dimensions (mm) | Interpretation |
|---|---:|---|---|---|
| Engine fastener | 8 | `[0.060621775686740875, 0.07000000029802322, 0.05999999865889549]` | `[60.62, 70.0, 60.0]` | Named visual/teaching geometry; exact fastener identity is not established by name alone. |
| 4WD mount bolt | 2 | `[0.060621775686740875, 0.07000000029802322, 0.07999999821186066]` | `[60.62, 70.0, 80.0]` | Named visual/teaching geometry; exact fastener identity is not established by name alone. |
| Frame mount bolt | 8 | `[0.07794228941202164, 0.09000000357627869, 0.07999999821186066]` | `[77.94, 90.0, 80.0]` | Named visual/teaching geometry; exact fastener identity is not established by name alone. |
| Wheel lug | 20 | `[0.03810511529445648, 0.04399999976158142, 0.05000000074505806]` | `[38.11, 44.0, 50.0]` | Named visual/teaching geometry; exact fastener identity is not established by name alone. |

## Factory cross-checks available in the repository

| Factory claim | Source in repository | Status against the GLB |
|---|---|---|
| Engine mount nuts: 65 lb-ft / 88 Nm | `app/src/main/java/com/example/data/SportTracData.kt`, engine torque specs | **Factory-data claim present; not geometrically linked to the eight `Engine fastener` nodes.** |
| Cylinder-head TTY bolts: M11; 26 lb-ft / 35 Nm plus 90° | `SportTracData.kt`, cylinder-head component/spec entries | **Factory-data claim present; not geometrically linked to the generic `Engine fastener` nodes.** |
| Lower intake manifold bolts: 89 in-lb / 10 Nm | `SportTracData.kt` | **Factory-data claim present; no matching named GLB fastener linkage.** |
| Upper intake plenum bolts: 89 in-lb / 10 Nm | `SportTracData.kt` and `CameraMeasurementDialog.kt` | **Factory-data claim present; no matching named GLB fastener linkage.** |

## Conclusion

The GLB contains 226 named geometry nodes and named fastener instances, and its declared meter convention is supported by the repository. The extracted `Engine fastener` primitives measure approximately 60.62 × 70 × 60 mm as bounding boxes, which should **not** be interpreted as an M11 bolt or any specific factory fastener without a node-level engineering mapping. The repository supplies torque and some hardware descriptions, but it does not currently establish a one-to-one mapping from those factory records to each GLB fastener node. Therefore the engine-fastener verification result is **partially verified for presence and scene measurement; not verified for factory diameter, thread, grade, washer, gasket, or exact geometry**.
