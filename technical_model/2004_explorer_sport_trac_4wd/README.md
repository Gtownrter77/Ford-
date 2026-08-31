# 2004 Ford Explorer Sport Trac 4WD Technical Model

This folder contains the corrected 4WD teaching-model package for the 2004 Ford Explorer Sport Trac, V6 4.0L VIN K Flex Fuel configuration. The Blender scene includes the transfer case, front propeller shaft, front differential, front halfshafts, labeled drivetrain collections, technical materials, and exploded-view presentation hooks. The canonical Android model asset is now `app/src/main/assets/models/ford_explorer_sport_trac_2004.glb`.

The former package GLB has been moved to the Android placeholder path expected by `VehicleAsset.kt` and `parts_data.json`. The `reference/` folder contains the repository-derived wiring and torque extraction. It is intentionally explicit about evidence boundaries: this repository does not contain a complete factory wiring-schematic or connector-pinout dump, and repository torque strings must be verified against the exact Ford workshop-manual page before field use.
