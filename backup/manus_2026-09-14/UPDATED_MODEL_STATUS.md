# Ford Sport Trac Digital Twin — Model Update

## Update date

2026-09-14

## Scope

This package contains the model-only work for the 2004 Ford Explorer Sport Trac digital twin. The current target is the gray/silver 4WD truck with the 4.0L V6, using the repository's 4WD VIN K Flex Fuel manual index as the authoritative service-data source.

## Included in this update

- Corrected browser preview source with an opaque exterior truck view as the default.
- Cutaway, exploded, and fly-through controls retained for model inspection.
- Canonical chassis and powertrain inventory.
- Hierarchical assembly tree with explicit service-scope fastener, gasket, and seal records.
- Gray 2004 Sport Trac 4WD real-photo reference.
- Verification boundary and next reconstruction gates.

## Current inventory baseline

- 71 canonical component records.
- 56 repository source component records.
- 15 meter-scale reference envelope records.
- 173 hierarchical inventory nodes.
- 19 explicit service-scope hardware stack records.

## Authoritative source boundary

The GitHub repository's indexed manual package reports 9,560 non-empty pages for the target configuration: 2004 Ford Explorer Sport Trac, 4WD, 4.0L V6, VIN K Flex Fuel. Those pages are the intended authority for service relationships, fasteners, seals, gaskets, torque values, and service sequence.

The current GLB and procedural geometry remain a baseline. They are not yet a VIN-complete OEM CAD assembly. Unverified geometry, quantities, and hardware specifications remain labeled as reference or pending verification in the inventory.

## Next reconstruction gates

1. Lock the exact VIN/trim/axle/transmission configuration.
2. Reconcile each chassis and powertrain assembly to its manual evidence pages.
3. Expand the joint ledger to one-to-one bolts, studs, nuts, washers, gaskets, seals, clips, and retainers.
4. Replace reference envelopes with measured or OEM CAD geometry where evidence permits.
5. Add assembly/disassembly sequence, motion constraints, and time-state records.
6. Re-export and validate the 4D model package.
