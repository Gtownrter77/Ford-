# Complete 2004 Sport Trac 3D assembly

Single entry: `SportTracCompleteAssembly.components`

Includes:
- Existing Mentor catalog parts (`SportTracData.components`)
- Meter-true hull (frame, cab, hood, bed, 4.0L, 5R55E, BW4411, axles, radiator, four P265/70R16 wheels)
- Remaining service envelopes (exhaust, 22.5 gal tank, battery, alternator, steering box, both front calipers, both driveshafts, FA-1744 air box, condenser, lamps, spare)
- Bolt + washer + nut stacks on those joints

Copy these local files into `app/src/main/java/com/example/data/` if they are not already on main:
- SportTracScaledHull.kt
- SportTracFastenerLayer.kt
- SportTracHullExtras.kt
- SportTracCompleteAssembly.kt

Then in ExplorerViewModel replace `SportTracData.components` with `SportTracCompleteAssembly.components` in `filteredComponents` and `selectComponentById`.

This is still not every clip Ford installed. TTY head bolts, interior screws, body clips, and harness retainers stay workshop-manual items.
