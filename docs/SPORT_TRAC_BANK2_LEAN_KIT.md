# Bank 2 lean / misfire kit

Live diagnostic path on the 2004 Sport Trac 4.0L SOHC VIN K.

## Bank map
Cologne SOHC Explorer, longitudinal:
- Bank 1 passenger / +X, cylinders 1-2-3 front to rear
- Bank 2 driver / -X, cylinders 4-5-6 front to rear
- OG firing order 1-4-2-5-3-6

## Workshop torque used (not Owner Guide)

| Joint | Qty | Torque | Source |
|---|---|---|---|
| Intake manifold bolts | 8 (4 per bank) | 10 Nm / 89 in-lb | 2004 VIN K workshop specs + CHARM 2005 VIN K intake table |
| Throttle body bolts | 4 | 9 Nm / 80 in-lb | CHARM 2005 VIN K |
| Ignition coil bolts | 4 | 6 Nm / 53 in-lb | Mitchell 2004 4.0L SOHC engine-performance table |
| Coil bracket bolts | 2 | 10 Nm / 89 in-lb | Mitchell 2004 engine table |
| Spark plugs | 6 | 20 Nm / 15 lb-ft | Mitchell 2004 engine table |
| Plug gap | 6 | 1.3-1.4 mm | 2004 Owner Guide |
| Fuel rail bolts | 4 | 23 Nm / 17 lb-ft | Mitchell 2004 engine-performance table |
| PCV elbow | 1 | snug | Community / prior Mentor lean path |

Plastic intake inserts crack if you use a foot-pound wrench on the 89 in-lb bolts.

Kotlin: SportTracBank2LeanKit.kt. Attached onto scaled_engine_40l, intake_manifold, spark_plugs_coils.
