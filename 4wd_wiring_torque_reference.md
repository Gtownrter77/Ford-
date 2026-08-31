# 2004 Ford Explorer Sport Trac 4WD Wiring and Torque Reference

This working reference is extracted from the Gtownrter77/Ford- repository. It distinguishes explicit repository data from project-authored or externally attributed values. It is not a substitute for the Ford workshop manual.

Parsed 57 data entries from `app/src/main/java/com/example/data/SportTracData.kt` and 16 documentation files.

## 4WD drivetrain and electrical mentions

### `engine_block` — 4.0L SOHC V6 Engine Block & Heads

Cast-iron engine block with aluminum 60-degree V6 cylinder heads and single overhead camshafts.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Cylinder Head Bolts (Step 1) | 26 | 35 | Must replace TTY bolts each time |
| Cylinder Head Bolts (Step 2) | 90 deg | 90 deg | Angle rotation |
| Oil Pan Bolts | 15 | 20 | Criss-cross pattern |
| Engine Mount Nuts | 65 | 88 | To frame crossmember |

### `intake_manifold` — Upper & Lower Intake Manifold

Composite plastic intake manifold with integrated runner controls and fuel rail mountings.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Lower Intake Manifold Bolts | 89 in-lbs | 10 Nm | Tighten in 2 stages |
| Upper Intake Plenum Bolts | 89 in-lbs | 10 Nm | Stage 1: 53 in-lbs, Stage 2: 89 in-lbs |
| Fuel Rail Retaining Bolts | 80 in-lbs | 9 Nm |  |

Technical mentions:

- RepairStep(2, "Disconnect Vacuum Lines & Harness", "Label and remove PCV tube, brake booster vacuum hose, and fuel injector connectors."),

### `throttle_body` — Throttle Body & Mass Air Flow (MAF) Sensor

Aluminum throttle body housing with throttle position sensor (TPS) and Idle Air Control (IAC) valve.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Throttle Body Bolts | 89 in-lbs | 10 Nm | Do not overtighten brass inserts |
| IAC Valve Screws | 71 in-lbs | 8 Nm |  |

### `thermostat_housing` — Coolant Thermostat Housing Assembly

2-piece composite thermostat housing outlet with dual coolant temperature sensors and 192°F thermostat.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Thermostat Housing Bolts | 89 in-lbs | 10 Nm | Plastic housing cracks if over-torqued |
| Coolant Temperature Sensor | 12 | 16 | Use Teflon tape thread sealant |

Technical mentions:

- RepairStep(3, "Unplug Sensors", "Unclip wiring connectors for ECT sensor and gauge sender."),

### `radiator_assembly` — Radiator & Mechanical Fan Clutch

Aluminum cross-flow radiator core with plastic end tanks and viscous thermal fan clutch assembly.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Fan Clutch Nut to Water Pump | 38 | 52 | Left-Hand thread on some models (check arrow) |
| Radiator Mount Brackets | 80 in-lbs | 9 Nm |  |

### `water_pump` — Engine Coolant Water Pump

Cast aluminum mechanical impeller pump driven directly by serpentine belt.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Water Pump Mounting Bolts | 89 in-lbs | 10 Nm | Tighten evenly in star pattern |
| Water Pump Pulley Bolts | 18 | 25 |  |

### `ac_compressor` — A/C Scroll Compressor & Magnetic Clutch

FS10 scroll refrigerant compressor with electromagnetic pulley clutch assembly.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| A/C Compressor Mounting Bolts | 18 | 25 | 3 long bolts through engine bracket |
| Manifold Block Fitting Bolt | 15 | 20 | Use new HNBR green O-rings |

Technical mentions:

- RepairStep(2, "Disconnect Electrical Clutch Connector", "Unplug single-wire magnetic coil lead."),

### `transmission_5r55e` — 5R55E 5-Speed Automatic Transmission

Electronically controlled 5-speed automatic transmission with torque converter lockup clutch and overdrive.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Transmission Fluid Pan Bolts | 120 in-lbs | 14 Nm | Do not over-compress rubber gasket |
| Torque Converter Drive Plate Bolts | 34 | 46 | Access through inspection cover |
| Bellhousing to Engine Bolts | 35 | 47 |  |

Technical mentions:

- RepairStep(2, "Disconnect Driveshafts", "Unbolt front and rear driveshaft flange yokes and support with wire."),

### `transmission_solenoids` — Valve Body Solenoid Pack & Filter

Integrated valve body solenoid block controlling EPC pressure, shift solenoids A/B/C/D, and TCC lockup.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Solenoid Pack Retaining Bolts | 80 in-lbs | 9 Nm | Critical torque to prevent valve body gasket blowouts |
| Filter Retaining Bolt | 80 in-lbs | 9 Nm |  |

Technical mentions:

- RepairStep(3, "Disconnect Wiring Harness", "Unplug main 16-pin harness connector from top of solenoid pack."),

### `alternator_ignition` — 130-Amp Alternator & EDIS Coil Pack

130-Amp heavy duty alternator with internal voltage regulator and 6-tower distributorless coil pack.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Alternator Mounting Bolts | 35 | 47 |  |
| B+ Terminal Nut | 80 in-lbs | 9 Nm | Do not over-torque terminal post |

Technical mentions:

- RepairStep(3, "Unplug Wiring", "Unplug regulator harness and unbolt red B+ power cable."),
- // 10B. WIRING HARNESS, HEADLIGHTS & FUSE BOX 3D

### `wiring_lighting_3d` — Engine Wiring Harness, Headlights & Central Junction Fuse Box

Complete 12V body wiring loom, engine distribution harness, high-intensity dual beam halogen headlight assemblies, fog lamps, and battery junction box fuses.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Battery Terminal Clamp Nuts | 80 in-lbs | 9 Nm | Clean posts with wire brush |
| Headlight Adjuster Assembly Screws | 25 in-lbs | 2.8 Nm | Align light beam height |
| Fuse Junction Box Ground Bolt | 89 in-lbs | 10 Nm | Ensure bare metal contact for body grounds |

Technical mentions:

- id = "wiring_lighting_3d",
- name = "Engine Wiring Harness, Headlights & Central Junction Fuse Box",
- description = "Complete 12V body wiring loom, engine distribution harness, high-intensity dual beam halogen headlight assemblies, fog lamps, and battery junction box fuses.",
- RepairStep(1, "Disconnect Battery Ground", "Remove 10mm negative battery terminal cable before touching any wiring harness or fuse box."),

### `brakes_suspension` — Front Disc Brakes & Torsion Bar Suspension

Dual-piston floating brake calipers, vented rotors, and heavy-duty front torsion bar suspension arms.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Caliper Guide Pin Bolts | 26 | 35 | Lubricate with silicone brake grease |
| Caliper Anchor Bracket Bolts | 85 | 115 | Apply blue Threadlocker |
| Wheel Lug Nuts | 100 | 135 | Torque in star pattern |

### `truck_frame_body` — Sport Trac Frame & Composite Cargo Bed

Full box-section steel frame rails, 4-door cab shell structure, and dent-resistant composite cargo bed with tie-down cleats.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Cargo Bed Mounting Bolts (6 Bolts) | 59 | 80 | Torx T55 head bolts |
| Cab Cushion Body Mount Bolts | 60 | 81 | Check rubber isolator bushings |
| Trailer Hitch Receiver Bolts | 80 | 108 | Grade 8 hardware |

Technical mentions:

- RepairStep(1, "Disconnect Tailgate Wiring", "Unplug rear taillight harness and tailgate lock wire connector before bed removal."),
- // 13. CONTROL TRAC 4WD TRANSFER CASE & DRIVESHAFTS

### `driveshaft_4x4` — Control Trac 4WD Transfer Case & Driveshafts

BorgWarner 44-11 electric shift-on-the-fly transfer case with front and rear aluminum driveshafts and Ford 8.8 rear differential.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Driveshaft Flange Bolts | 83 | 112 | 12-point 12mm bolts with blue threadlocker |
| Transfer Case Drain & Fill Plugs | 22 | 30 | Use MERCON V ATF fluid |
| Rear Differential Cover Bolts | 33 | 45 | Use 75W-140 Synthetic Gear Oil with Friction Modifier |

Technical mentions:

- id = "driveshaft_4x4",
- name = "Control Trac 4WD Transfer Case & Driveshafts",
- description = "BorgWarner 44-11 electric shift-on-the-fly transfer case with front and rear aluminum driveshafts and Ford 8.8 rear differential.",
- locationDescription = "Mounted behind 5R55E transmission with driveshafts running to front and rear axles.",
- TorqueSpec("Driveshaft Flange Bolts", "83", "112", "12-point 12mm bolts with blue threadlocker"),
- TorqueSpec("Transfer Case Drain & Fill Plugs", "22", "30", "Use MERCON V ATF fluid"),
- RepairStep(1, "Drain Transfer Case", "Remove 3/8-inch square drive lower drain plug and drain 1.5 quarts of MERCON V fluid."),
- RepairStep(2, "Unbolt Driveshaft Flanges", "Remove four 12-point 12mm bolts at rear differential pinion flange."),
- RepairStep(4, "Inspect 4WD Shift Motor", "Check 4x4 electric encoder motor mounted on transfer case rear housing if 4x4 High/Low lights flash on dashboard.")
- commonSymptoms = listOf("4x4 HIGH / LOW dashboard lights flashing 6 times", "Clunking sound when engaging 4WD or shifting into Reverse", "High-speed driveline vibration from dry U-joints"),

### `exhaust_system` — Exhaust Manifolds & Catalytic Converter Y-Pipe

Cast iron exhaust manifolds, dual 3-way catalytic converters Y-pipe assembly, four heated oxygen sensors (HO2S), and stainless steel muffler.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Exhaust Manifold Nuts to Cylinder Head | 18 | 25 | Apply high-temp anti-seize |
| Y-Pipe Exhaust Flange Studs | 30 | 41 | Must replace rusted flange nuts |
| Heated O2 Sensors (HO2S) | 30 | 41 | 22mm oxygen sensor socket |

Technical mentions:

- RepairStep(2, "Disconnect Oxygen Sensors", "Unplug electrical connectors for upstream (Bank 1/2 Sensor 1) and downstream O2 sensors."),

### `fuel_tank_pump` — 22.5 Gallon Fuel Tank & High-Pressure Pump Module

Molded 22.5-gallon polyethylene fuel tank, steel tank straps, in-tank electric turbine fuel pump module with fuel level sender, and fuel rail pressure sensor.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Fuel Tank Strap Bolts | 38 | 52 | Inspect straps for rust rot |
| Fuel Pump Retaining Lock Ring | 55 | 75 | Use brass punch or spanner tool |
| Fuel Filter Bracket Bolt | 89 in-lbs | 10 Nm |  |

### `steering_rack` — Power Steering Rack & Pinion Assembly

Hydraulic power-assisted rack and pinion steering gear with inner/outer tie rod ends, power steering pump, and MERCON V fluid reservoir.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Steering Rack Mounting Bolts | 85 | 115 | Re-torque to frame crossmember |
| Outer Tie Rod End Castle Nuts | 41 | 56 | Install new cotter pin |
| Power Steering Line Pressure Fittings | 20 | 27 | Teflon O-ring seal |

Technical mentions:

- locationDescription = "Mounted across front lower engine crossmember behind front differential.",

### `abs_master_cylinder_3d` — Brake Master Cylinder, Power Vacuum Booster & 4-Wheel ABS Pump

Cast aluminum dual-reservoir brake master cylinder, 10-inch vacuum power booster diaphragm, and 4-channel hydraulic anti-lock brake pump control module.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Master Cylinder Mounting Nuts to Booster | 18 | 25 | Replace paper seal gasket |
| Brake Fluid Line Fitting Nuts | 14 | 19 | Use 3/8-in & 7/16-in flare nut wrenches |
| ABS Module Hydraulic Unit Bolts | 89 in-lbs | 10 Nm |  |

### `rear_brakes_3d` — Rear Disc Brakes & Internal Drum Parking Brake Shoes

Solid rear brake rotors with integrated hat drum for parking brake shoes, single-piston calipers, and mechanical emergency brake tension cables.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Rear Caliper Mounting Pins | 26 | 35 | Apply silicone brake lubricant |
| Rear Caliper Bracket Bolts | 70 | 95 | Apply threadlocker |
| Wheel Lug Nuts | 100 | 135 | Torque to 100 lb-ft |

### `catback_exhaust_muffler_3d` — Stainless Steel Cat-Back Exhaust Muffler & Mandrel Tailpipe

Full 2.5-inch aluminized / stainless steel replacement exhaust system including acoustic chamber muffler, tailpipe over rear axle, and heavy-duty rubber isolator hangers.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Exhaust Flange Clamp Bolts | 35 | 47 | Use heavy duty U-bolt clamps |
| Cat-Back Inlet Flange Nuts | 30 | 41 | Apply high temp anti-seize |

### `oxygen_sensors_3d` — Upstream & Downstream Heated Oxygen Sensors (HO2S)

Zirconia 4-wire heated oxygen sensors (Bank 1 & Bank 2 Sensor 1 upstream for fuel trim feedback, and Sensor 2 downstream for catalytic converter monitor).

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| O2 Sensor Thread Torque | 30 | 41 | Apply high-temp nickel anti-seize to threads (avoid sensor tip) |

Technical mentions:

- requiredTools = listOf("22mm (7/8-in) Slotted O2 Sensor Socket", "Penetrating Fluid (PB Blaster)", "Propane Torch (if seized)", "Wire Harness Cleaner"),
- RepairStep(2, "Unplug 4-Pin Harness Connector", "Press locking tab on oxygen sensor pigtail harness connector and pull apart."),
- RepairStep(3, "Unscrew O2 Sensor", "Slip slotted 22mm O2 sensor socket over wire harness and break sensor loose counter-clockwise."),

### `tires_wheels_3d` — P265/70R16 All-Terrain Tires & 16x7-inch Aluminum Wheels

265/70R16 112T All-Terrain tires mounted on 16x7-inch 5-spoke machined cast aluminum wheels with 5x114.3mm (5x4.5) bolt pattern and 1/2-in-20 lug studs.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Wheel Lug Nuts (1/2-in-20 Thread) | 100 | 135 | Tighten in 5-lug star criss-cross pattern |
| Tire Cold Inflation Pressure | 35 PSI Front / 35 PSI Rear | 35 PSI | Check pressure when tires are cold |

### `wheel_bearings_hubs_3d` — Front Wheel Hub & Sealed Bearing Assembly with ABS Sensor

Complete unitized front wheel hub assembly with pre-greased sealed double-row roller bearings, 5 pressed wheel studs, and integrated ABS wheel speed sensor wiring harness.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Front Axle Shaft Nut (4WD Models) | 184 | 250 | Must use 32mm socket & replace cotter pin |
| Hub Bearing Assembly to Knuckle Bolts | 83 | 112 | Torque three 15mm mounting bolts |
| Brake Caliper Anchor Bracket Bolts | 85 | 115 | Apply blue threadlocker |

Technical mentions:

- description = "Complete unitized front wheel hub assembly with pre-greased sealed double-row roller bearings, 5 pressed wheel studs, and integrated ABS wheel speed sensor wiring harness.",
- TorqueSpec("Front Axle Shaft Nut (4WD Models)", "184", "250", "Must use 32mm socket & replace cotter pin"),
- RepairStep(2, "Disconnect ABS Harness Connector", "Unplug ABS wheel speed sensor harness connector pinned behind plastic inner fender liner."),

### `dash_dashboard_cluster_3d` — Instrument Cluster Gauges, White Face Overlay & Dash Bezel

Sport Trac white-face gauge cluster including 120 MPH speedometer, tachometer, fuel level, engine coolant temp, oil pressure, battery voltage gauges, and backlighting bulb circuit board.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Dash Trim Bezel Screws (7mm Head) | 18 in-lbs | 2 Nm | Do not overtighten into plastic clips |
| Instrument Cluster Mounting Screws | 22 in-lbs | 2.5 Nm | Hand tighten securely |

Technical mentions:

- RepairStep(3, "Unscrew Instrument Bezel", "Remove four 7mm screws securing dash cluster surround bezel and unclip headlight switch electrical harness."),
- RepairStep(4, "Pull Cluster & Unplug Wiring Connectors", "Remove four 7mm cluster screws, tilt unit forward, and depress release tabs on two main wire harness connectors."),
- // 24. MAIN DASHBOARD WIRING HARNESS & FUSE BOX (CJB)

### `dash_wiring_harness_3d` — Main Dash Wiring Harness & Central Junction Box (Inside Fuse Panel)

Complete under-dash main wiring harness interfacing Central Junction Box (CJB interior fuse panel), GEM (Generic Electronic Module), ignition switch, HVAC controls, and radio audio connections.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Central Junction Box Mounting Bolts | 89 in-lbs | 10 Nm | Hand start bolts |
| Dash Skeleton Reinforcement Bracket Bolts | 18 | 25 | 10mm socket |

Technical mentions:

- id = "dash_wiring_harness_3d",
- name = "Main Dash Wiring Harness & Central Junction Box (Inside Fuse Panel)",
- description = "Complete under-dash main wiring harness interfacing Central Junction Box (CJB interior fuse panel), GEM (Generic Electronic Module), ignition switch, HVAC controls, and radio audio connections.",
- RepairStep(1, "Disconnect Battery & Disarm Airbags", "Disconnect battery terminals and wait 10 minutes to safely discharge airbag backup capacitors before touching dash wiring."),
- RepairStep(2, "Access Driver Kick Panel Fuse Box", "Remove left lower trim panel to expose Central Junction Box fuse block and GEM module connectors."),

### `sunroof_glass_frame_3d` — Power Moonroof Tempered Glass Panel & Weatherstrip Perimeter Seal

Dark tint solar glass tempered moonroof panel with metal perimeter carrier frame, adjustable height tilt brackets, and rubber perimeter weatherstrip seal.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Glass Panel Corner Fastener Screws (T25 Torx) | 27 in-lbs | 3 Nm | Hand tighten carefully to avoid cracking glass frame |
| Sunroof Frame Assembly Reinforcement Bolts | 89 in-lbs | 10 Nm |  |

### `sunroof_motor_tracks_3d` — Sunroof Electric Drive Motor, Helical Drive Cables & Track Rails

High-torque 12V electric drive motor with worm gear gearbox, twin flexible spiral/helical push-pull cables, aluminum guide track channels, and internal limit switches.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Sunroof Drive Motor Mounting Screws (T20 Torx) | 35 in-lbs | 4 Nm | Apply blue threadlocker |
| Roof Headliner Grab Handle Screw Bolts | 30 in-lbs | 3.5 Nm |  |

Technical mentions:

- RepairStep(2, "Unplug Motor Wire Harness", "Disconnect 3-pin power & ground electrical connector on drive motor attached to front frame crossmember."),

### `sunroof_drain_tubes_shade_3d` — Sunroof Water Drain Hoses, Interior Sunshade & Overhead Switch

Four vinyl corner water drainage tubes (routing through A-pillars and C-pillars), vinyl vinyl-wrapped sliding interior fabric sunshade panel, and overhead rocker control switch.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Overhead Console Mounting Screws | 18 in-lbs | 2 Nm | Snug tight |

### `front_windshield_3d` — Front Acoustic Safety Laminated Windshield, Wipers & Cowl Grille

Solar-control laminated safety glass front windshield with acoustic layer, integrated rearview mirror bracket, dual wiper arms with 22-inch blades, washer jet nozzles, and plastic wiper cowl intake grille.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Wiper Arm Mounting Pivot Nuts | 14 | 19 | Pop plastic nut cover cap off first |
| Wiper Linkage Motor Mounting Bolts | 89 in-lbs | 10 Nm | Apply blue threadlocker |

### `rear_window_power_slide_3d` — Power Drop-Down Rear Glass Window, Regulator & Weatherstrip Channel

Signature Sport Trac full-width power drop-down rear window assembly with solar tinted tempered glass, electric cable-driven window regulator motor, defroster heating grid, and lower trough drainage seals.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Rear Window Glass Clamp Bolts | 44 in-lbs | 5 Nm | Tighten carefully into rubber glass channel |
| Rear Regulator Assembly Nut Screws | 89 in-lbs | 10 Nm |  |

Technical mentions:

- RepairStep(3, "Disconnect Power Window Motor Harness", "Unplug 2-pin electrical connector for rear power sliding window motor."),

### `airbag_driver_clockspring_3d` — Driver Steering Wheel Airbag Module & Spiral Clock Spring Harness

Dual-stage pyrotechnic driver frontal airbag inflator module mounted behind steering wheel emblem cover, paired with multi-channel spiral cable clock spring harness supplying continuous electrical contact for horn, cruise control switches, and airbag ignition circuits.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Airbag Steering Wheel Side Screws (8mm Head) | 84 in-lbs | 9.5 Nm | Two side cover screws |
| Steering Wheel Center Hub Bolt | 33 | 45 | Apply blue threadlocker |

Technical mentions:

- name = "Driver Steering Wheel Airbag Module & Spiral Clock Spring Harness",
- description = "Dual-stage pyrotechnic driver frontal airbag inflator module mounted behind steering wheel emblem cover, paired with multi-channel spiral cable clock spring harness supplying continuous electrical contact for horn, cruise control switches, and airbag ignition circuits.",
- RepairStep(3, "Disconnect Yellow SRS Wire Connectors", "Carefully lift driver airbag module off wheel center and depress locking tabs on yellow SRS electrical wire harness connectors."),

### `airbag_rcm_sensors_3d` — Restraint Control Module (RCM) & Front Crash Impact Sensors

Microprocessor-controlled SRS Restraint Control Module (RCM) with internal solid-state accelerometers and rollover sensors, plus dual front radiator core support crash impact sensors.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| RCM Ground Fastener Nuts to Floor Pan | 106 in-lbs | 12 Nm | Must maintain clean metal-to-metal ground seal |
| Front Crash Sensor Core Support Bolts | 89 in-lbs | 10 Nm | Torque to prevent sensor housing vibration |

Technical mentions:

- RepairStep(3, "Unplug RCM Harness Connectors", "Slide red secondary locking wedges back on dual 24-pin yellow airbag connectors."),

### `airbag_seatbelt_pretensioners_3d` — Pyrotechnic Seatbelt Buckle Pretensioners & Buckle Switch Harness

Driver and front passenger pyrotechnic cable-pull seatbelt buckle pretensioner anchors, equipped with integrated buckle latch switches that signal the RCM module whether occupants are buckled in.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Seatbelt Anchor Bolt to Seat Frame | 30 | 40 | T50 Torx head bolt - heavy threadlocker |
| Seat Frame Track Floor Mounting Bolts | 39 | 53 | 15mm socket |

Technical mentions:

- name = "Pyrotechnic Seatbelt Buckle Pretensioners & Buckle Switch Harness",
- locationDescription = "Mounted to inner side of front driver and passenger bucket seat frames.",
- requiredTools = listOf("T50 Torx Socket & Breaker Bar", "15mm Socket", "Wire Harness Contact Cleaner"),
- RepairStep(3, "Unplug Under-Seat Airbag Harness", "Disconnect yellow seatbelt pretensioner wire pigtail located under seat cushion."),

### `ac_compressor_pressure_controls` — A/C Compressor, Electromagnetic Clutch & High/Low Pressure Cut-off Switches

FS10 10-piston aluminum A/C compressor with electromagnetic clutch coil, 6-groove serpentine-belt pulley, air-gap shims, pressure-protection controls, and a sealed R-134a refrigerant circuit. Refrigerant amount and oil balance must be confirmed from the under-hood label and Ford service procedure for the exact vehicle.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| A/C Compressor Mounting Bolts (3 Bolts) | 18 | 25 | Tighten in 2 stages |
| A/C Manifold Hose Block Bolt | 15 | 20 | Must replace green HNBR O-rings |
| Clutch Hub Front Center Nut | Verify | Verify | Measure clutch air gap and use the Ford service specification for this exact compressor/clutch. |

Technical mentions:

- RepairStep(3, "Disconnect A/C Hose Block & Switches", "Remove single 10mm bolt securing manifold hose assembly block to compressor back and unplug 2-pin clutch wire connector."),

### `heater_core_hvac_3d` — Aluminum Heater Core, Vacuum Control Valve & Heater Hoses

Heavy-duty aluminum heat-exchanger core housed inside dash HVAC housing, vacuum-actuated 4-port heater coolant control shut-off valve, and dual 5/8-inch reinforced rubber heater supply/return hoses.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Heater Hose Clamp Tensioners | 22 in-lbs | 2.5 Nm | Inspect rubber hoses for swelling |
| HVAC Case Plenum Mounting Stud Nuts | 89 in-lbs | 10 Nm | Engine firewall studs |

### `hvac_blower_motor_3d` — HVAC Blower Motor Fan Assembly & 4-Speed Resistor Block

High-output 12V permanent magnet blower motor with balanced plastic squirrel cage fan wheel, paired with a ceramic-coated 4-position blower motor resistor block and thermal cutoff fuse.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Blower Motor Mounting Screws (4 Screws) | 25 in-lbs | 2.8 Nm | Hand tighten carefully into plastic housing |
| Blower Resistor Retaining Screws | 18 in-lbs | 2 Nm |  |

Technical mentions:

- requiredTools = listOf("8mm Socket & Nut Driver", "T20 Torx Driver", "Electrical Contact Cleaner", "Wire Strippers / Heat Shrink Terminal Kit (if connector melted)"),
- RepairStep(1, "Unplug Blower Motor Wire Harness", "Press release tab on 2-pin electrical connector at blower motor on engine firewall."),
- RepairStep(4, "Replace Blower Motor Resistor Pack", "Unbolt two 8mm screws on passenger firewall HVAC case to replace burnt resistor block and inspect wire harness connector for melted pin terminals.")

### `hvac_blend_door_actuator_3d` — HVAC Electric Temperature Blend Door Actuator Motor

Micro-stepper electric motor actuator with internal nylon gear reduction drive and feedback potentiometer, controlling the HVAC plenum chamber blend door flap to blend hot heater core air with cold A/C evaporator air.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Blend Door Actuator Screws (8mm Head) | 18 in-lbs | 2 Nm | Do not overtighten screws into plastic plenum |

Technical mentions:

- RepairStep(3, "Unscrew Mounting Screws & Unplug Wire Harness", "Remove three 8mm screws using mini ratchet or flex driver and unplug 5-pin wire connector."),

### `ac_evaporator_accumulator_3d` — A/C Evaporator Core, Accumulator / Drier Bottle & Fixed Orifice Tube

High-efficiency aluminum evaporator core, accumulator/drier bottle with internal desiccant and pressure-switch port, and fixed-orifice metering device. The exact replacement configuration must match the VIN-specific parts catalogue and service procedure.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| A/C Accumulator Nut Coupling Spring Lock | 15 | 20 | Use spring lock tool #4 & #5 |
| Low Pressure Cycling Switch to Accumulator Port | 89 in-lbs | 10 Nm | Includes Schrader valve seal |

### `ac_condenser_lines_3d` — A/C Condenser Parallel-Flow Radiator Core & High-Pressure Hose Assembly

Heavy-duty aluminum parallel-flow A/C condenser heat exchanger with integrated sub-cooler, high-pressure aluminum discharge lines, flexible barrier rubber hoses, and dual high/low Schrader service valve ports.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| A/C Condenser Bracket Retaining Bolts | 89 in-lbs | 10 Nm |  |
| High Pressure Hose Fitting Block Bolt | 15 | 20 | Replace green HNBR O-ring seals |

### `ac_service_ports_controls_3d` — A/C Service Ports, Pressure-Protection Controls & Clutch Command Path

Diagnostic-reference assembly representing the high- and low-side service ports, pressure-protection controls, A/C relay/fuse command path, compressor clutch connector, and associated harness routing. It is intended to guide safe observation and electrical inspection without bypassing safety controls or opening the sealed refrigerant circuit.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Service-port cap and valve handling | N/A | N/A | Do not remove Schrader valves or loosen fittings for a basic diagnostic inspection. |
| Pressure-control and clutch-circuit verification | N/A | N/A | Use the Ford wiring information and approved test methods; do not jumper protective switches. |

Technical mentions:

- description = "Diagnostic-reference assembly representing the high- and low-side service ports, pressure-protection controls, A/C relay/fuse command path, compressor clutch connector, and associated harness routing. It is intended to guide safe observation and electrical inspection without bypassing safety controls or opening the sealed refrigerant circuit.",
- locationDescription = "Service ports and pressure-control fittings are located along the refrigerant lines; relay/fuse locations must be confirmed from the exact owner-manual diagram; compressor clutch connector is at the lower passenger-side compressor.",
- TorqueSpec("Pressure-control and clutch-circuit verification", "N/A", "N/A", "Use the Ford wiring information and approved test methods; do not jumper protective switches.")
- RepairStep(3, "Inspect without opening the system", "With the engine OFF and cool, inspect service-port caps, visible line routing, the compressor-clutch connector, and nearby harnesses for damage, oil-stained dirt, or corrosion."),
- RepairStep(4, "Check protected electrical path", "Use the exact owner-manual diagram to inspect the specified fuse and relay. Do not bypass the relay, pressure switches, or clutch connector to force compressor operation.", warning = "Pressure switches protect the system. Bypassing them can damage components or create a hazardous condition."),

### `dash_instrument_cluster_3d` — Dashboard Instrument Cluster & White-Face Gauge Pack

Sport Trac factory white-faced gauge instrument cluster featuring analog speedometer, tachometer, engine temp, fuel gauge, oil pressure, battery voltage gauge, illuminated gear position display, and dual 16-pin micro-lock electrical connectors.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Instrument Cluster Retaining Screws (4 Screws) | 22 in-lbs | 2.5 Nm | Hand tighten into plastic dash framing |
| Dash Bezel Screws (7mm Head) | 18 in-lbs | 2 Nm | Two screws located above gauge lens hood |

Technical mentions:

- description = "Sport Trac factory white-faced gauge instrument cluster featuring analog speedometer, tachometer, engine temp, fuel gauge, oil pressure, battery voltage gauge, illuminated gear position display, and dual 16-pin micro-lock electrical connectors.",
- RepairStep(3, "Disconnect Rear Electrical Plugs & Speedo Cable", "Tilt cluster top forward, press release tabs on black and grey 16-pin wiring harness plugs, and disconnect PRNDL gear selector string clip."),
- RepairStep(4, "Replace Backlight Bulbs / Install Cluster", "Twist ¼-turn bulb sockets on cluster rear circuit board to replace dead gauge backlights, reconnect wiring, and test all gauge needles.")
- commonSymptoms = listOf("Speedometer needle bounces erratically, stays stuck at zero, or digital odometer display goes completely blank", "Backlight bulbs burned out causing dark spots on tachometer or fuel gauge at night", "ABS, Battery, or 4x4 High indicator lights stay continuously illuminated on gauge face"),
- // 40. DASH CENTER STACK CLIMATE CONTROLS, RADIO & 4WD AUTO SWITCH

### `dash_hvac_radio_center_stack_3d` — Dash Center Stack Radio, Electronic HVAC Panel & 4WD Switch

Center dash control console housing factory Mach 500 double-DIN radio unit, rotary HVAC vacuum selector switch module, temperature control potentiometer, rear power window toggle switch, and 3-position rotary 4WD Auto / 4x4 High / 4x4 Low transfer case selector switch.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Center Radio Bezel Screws (7mm Head) | 18 in-lbs | 2 Nm | Two screws above HVAC panel |
| HVAC Control Module Screws | 15 in-lbs | 1.7 Nm | Four mini screws into plastic housing |

Technical mentions:

- name = "Dash Center Stack Radio, Electronic HVAC Panel & 4WD Switch",
- description = "Center dash control console housing factory Mach 500 double-DIN radio unit, rotary HVAC vacuum selector switch module, temperature control potentiometer, rear power window toggle switch, and 3-position rotary 4WD Auto / 4x4 High / 4x4 Low transfer case selector switch.",
- RepairStep(3, "Disconnect Switch Wire Connectors & Vacuum Harness", "Unplug 4WD switch connector, rear power window switch plug, cigarette lighter socket, and 5-tube HVAC vacuum line harness block."),
- RepairStep(4, "Replace Control Switch / Module", "Unbolt four 7mm screws on rear of bezel to replace damaged 4WD rotary switch or climate control module.")
- commonSymptoms = listOf("Turning 4WD selector knob does not engage 4x4 or cause transfer case shift motor to click", "Air blows ONLY out of defrost vents on windshield regardless of HVAC knob position (vacuum leak)", "Rear power window toggle switch fails to lower back glass window"),

### `steering_wheel_airbag_column_3d` — Steering Column, Cruise Control Switches & Driver SRS Airbag Module

Collapsible tilt steering column assembly complete with leather-wrapped steering wheel, steering column clockspring wiring coil, multifunction turn signal wiper lever, cruise control ON/OFF/SET thumb switches, ignition lock cylinder, and driver SRS airbag module.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Steering Wheel Center Hub Retaining Bolt | 33 | 45 | Apply blue threadlocker to M12 bolt |
| Driver Airbag Module Retaining Screws (8mm) | 89 in-lbs | 10 Nm | Two screws behind steering wheel spokes |
| Steering Column Pinch Bolt to Shaft | 26 | 35 |  |

Technical mentions:

- description = "Collapsible tilt steering column assembly complete with leather-wrapped steering wheel, steering column clockspring wiring coil, multifunction turn signal wiper lever, cruise control ON/OFF/SET thumb switches, ignition lock cylinder, and driver SRS airbag module.",
- RepairStep(2, "Remove Airbag Module", "Remove two 8mm plastic access covers on back of steering wheel, unscrew 8mm bolts, and unplug yellow SRS connector."),

### `overhead_console_compass_3d` — Overhead Roof Console with Digital Compass, Temp Display & Sunroof Switch

Factory overhead roof console containing green VFD digital display for exterior temperature and heading compass, twin dome map lights, garage door opener storage compartment, sunglass holder bay, and power sunroof toggle control switch.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Overhead Console Front Screw (Ph2) | 15 in-lbs | 1.7 Nm | Single Phillips screw inside sunglass compartment |

Technical mentions:

- RepairStep(3, "Unplug Electrical Connector", "Disconnect 8-pin wiring harness plug powering map lights, compass circuit board, and sunroof switch."),

### `power_sunroof_assembly_3d` — Factory Power Glass Sunroof Assembly, Dual-Track Rails & Water Drain Hoses

Complete power glass sunroof assembly featuring tinted tempered glass panel with perimeter rubber weatherseal gasket, dual extruded aluminum guide track rails, tilt/slide mechanism lifter arms, sliding interior fabric sunshade, and 4-corner rubber water drain hoses routed down A/C roof pillars.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Glass Panel Side Screws (Torx T25 - 4 Screws) | 35 in-lbs | 4 Nm | Adjust glass panel flush with outer roof line |
| Sunroof Frame Retaining Bolts (10mm) | 89 in-lbs | 10 Nm | Eight bolts to roof internal crossmembers |

Technical mentions:

- locationDescription = "Integrated into roof panel structure above front driver and passenger seats.",

### `sunroof_motor_drive_gear_3d` — Sunroof Electric Drive Motor, Helical Drive Cables & Limit Module

High-torque 12V reversible electric gear motor with internal brass pinion drive gear, meshing with dual flexible steel helical cables to slide and tilt the sunroof glass, equipped with electronic overload limit sensing and manual hex key emergency closure socket.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Sunroof Motor Mounting Screws (T20 - 3 Screws) | 40 in-lbs | 4.5 Nm | Do not cross-thread into metal bracket |

Technical mentions:

- RepairStep(3, "Unplug Motor Connector & Unbolt Screws", "Disconnect 6-pin electrical plug and unscrew three T20 Torx screws securing motor to roof frame."),

### `power_rear_window_assembly_3d` — Sport Trac Power Drop-Down Back Glass Window & Cable Regulator Motor

Signature Sport Trac full-width power drop-down rear window assembly featuring heated tempered back glass with defrost grid, dual-cable scissor regulator track, high-torque electric motor, bottom drain trough, and rubber weatherstripping seal.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Rear Window Regulator Bracket Bolts (10mm) | 89 in-lbs | 10 Nm |  |
| Glass Mounting Channel Clamp Screws | 35 in-lbs | 4 Nm | Ensure rubber clamp isolator pads in place |

### `headlight_foglight_assemblies_3d` — Dual-Beam Halogen Headlight Assemblies, Corner Markers & Fog Lamps

Clear poly-carbonate front lighting pack consisting of dual composite headlight housings with 9007 HB5 dual-filament halogen bulbs, amber turn signal corner marker lenses with 3157NA bulbs, and lower bumper round fog lamp housings with H10 42W halogen bulbs.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Headlight Retaining Retraction Pins | Hand Latch | N/A | Pull two vertical steel slide retainer pins straight up |
| Fog Lamp Mounting Bracket Bolts (8mm) | 45 in-lbs | 5 Nm | Aiming adjustment bolt on rear bracket |

Technical mentions:

- RepairStep(4, "Twist Bulb Collar & Replace Bulb", "Rotate 9007 bulb retaining ring ¼-turn counter-clockwise, unplug electrical harness connector, and insert fresh 9007 halogen bulb.")
- // 47. REAR TAIL LIGHT LENSES, BRAKE LIGHT SOCKETS & REVERSE HARNESS

### `tail_light_reverse_assemblies_3d` — Rear Tail Light Lenses, Brake Light Sockets & Reverse Wire Harness

Rear bed corner tail lamp assemblies featuring red brake/tail light section (3157 dual-filament bulb), clear reverse light section (3156 bulb), amber turn signal section, rubber weatherproof bulb socket seals, and bed wiring harness plug.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Tail Light Housing Screws (Ph2 - 2 Screws) | 18 in-lbs | 2 Nm | Tighten into plastic bed grommets |

Technical mentions:

- name = "Rear Tail Light Lenses, Brake Light Sockets & Reverse Wire Harness",
- description = "Rear bed corner tail lamp assemblies featuring red brake/tail light section (3157 dual-filament bulb), clear reverse light section (3156 bulb), amber turn signal section, rubber weatherproof bulb socket seals, and bed wiring harness plug.",
- // 48. BORGWARNER 4411 TRANSFER CASE & ELECTRONIC 4WD SHIFT MOTOR

### `transfer_case_shift_motor_3d` — BorgWarner 4411 Transfer Case & Electronic 4WD Shift Control Motor

Cast aluminum BorgWarner 4411 electronic shift-on-the-fly transfer case complete with planetary gear reduction set, electromagnetic clutch assembly for 4x4 Auto torque splitting, internal oil pump, drive chain, and rear-mounted 12V DC electric shift encoder motor.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Transfer Case to Transmission Adapter Bolts (13mm) | 30 | 41 |  |
| 4WD Shift Motor Mounting Bolts (T30 / 10mm) | 89 in-lbs | 10 Nm | Three bolts attaching motor to shift shaft |
| Transfer Case Fill & Drain Plugs (3/8 In Drive) | 22 | 30 | Uses 1.5 quarts MERCON V ATF |

Technical mentions:

- name = "BorgWarner 4411 Transfer Case & Electronic 4WD Shift Control Motor",
- system = VehicleSystem.DRIVETRAIN_4WD,
- description = "Cast aluminum BorgWarner 4411 electronic shift-on-the-fly transfer case complete with planetary gear reduction set, electromagnetic clutch assembly for 4x4 Auto torque splitting, internal oil pump, drive chain, and rear-mounted 12V DC electric shift encoder motor.",
- TorqueSpec("Transfer Case to Transmission Adapter Bolts (13mm)", "30", "41", ""),
- TorqueSpec("4WD Shift Motor Mounting Bolts (T30 / 10mm)", "89 in-lbs", "10 Nm", "Three bolts attaching motor to shift shaft"),
- TorqueSpec("Transfer Case Fill & Drain Plugs (3/8 In Drive)", "22", "30", "Uses 1.5 quarts MERCON V ATF")
- RepairStep(1, "Unplug 4WD Shift Motor Wire Connector", "Press locking tab on 7-pin round wiring harness plug attached to rear of transfer case."),
- RepairStep(2, "Unbolt 4WD Shift Motor", "Remove three 10mm bolts securing shift motor assembly to transfer case aluminum rear housing."),
- RepairStep(3, "Inspect Triangular Shift Shaft Rotary Pin", "Use pliers to verify manual rotation of triangular shift shaft pin on transfer case."),
- RepairStep(4, "Install New Shift Motor / Change ATF Fluid", "Align new motor socket onto triangular shaft pin, torque 10mm bolts, and drain/refill transfer case with 1.5 qts fresh MERCON V ATF.")
- commonSymptoms = listOf("4x4 High and 4x4 Low dash lights flash 6 times periodically while driving", "Turning 4WD dash switch produces no sound or engagement under truck", "Grinding noise when 4WD auto engages during rear wheel slip"),
- // 49. REAR ALUMINUM DRIVESHAFT, SLIP YOKE & HEAVY-DUTY U-JOINTS

### `rear_driveshaft_slip_yoke_3d` — Rear Aluminum Driveshaft Assembly, Slip Yoke & Heavy-Duty U-Joints

Balanced 4-inch diameter lightweight aluminum rear driveshaft tube featuring splined transmission slip yoke, rear pinion companion flange, and dual greaseable Spicer 1330 series universal joints.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Rear Pinion Flange 12-Point Bolts (12mm Head) | 83 | 112 | Apply red threadlocker to 4 flange bolts |
| U-Joint Strap Bolts (8mm) | 15 | 20 |  |

Technical mentions:

- id = "rear_driveshaft_slip_yoke_3d",
- name = "Rear Aluminum Driveshaft Assembly, Slip Yoke & Heavy-Duty U-Joints",
- system = VehicleSystem.DRIVETRAIN_4WD,
- description = "Balanced 4-inch diameter lightweight aluminum rear driveshaft tube featuring splined transmission slip yoke, rear pinion companion flange, and dual greaseable Spicer 1330 series universal joints.",
- locationDescription = "Extends from transfer case / transmission rear slip seal to rear differential pinion flange.",
- RepairStep(1, "Mark Pinion Flange Alignment", "Use paint pen to mark indexing alignment mark on driveshaft flange and differential pinion flange."),
- RepairStep(2, "Remove Four 12-Point Flange Bolts", "Unscrew four 12mm 12-point bolts securing driveshaft flange to rear differential."),
- RepairStep(3, "Slide Slip Yoke Out of Transfer Case", "Lower rear of driveshaft and slide front slip yoke out of transfer case rear extension housing seal."),

### `rear_differential_88_3d` — Ford 8.8-Inch Rear Differential Axle with Limited Slip Traction-Lok

Cast iron Ford 8.8-inch rear axle housing with 3.73 or 4.10 ring and pinion gear set, Traction-Lok multi-disc clutch pack limited-slip differential carrier, 31-spline axle shafts, steel rear cover pan, and ABS wheel speed sensor port.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Differential Cover Pan Bolts (10 Bolts - 1/2 In Head) | 33 | 45 | Tighten in star pattern with RTV sealant |
| Pinion Nut | 160 | 217 | Sets pinion bearing preload |
| Differential Fill Plug (3/8 In Drive) | 22 | 30 | Use 80W-90 / 75W-140 Synthetic + 4oz Friction Modifier |

Technical mentions:

- system = VehicleSystem.DRIVETRAIN_4WD,
- description = "Cast iron Ford 8.8-inch rear axle housing with 3.73 or 4.10 ring and pinion gear set, Traction-Lok multi-disc clutch pack limited-slip differential carrier, 31-spline axle shafts, steel rear cover pan, and ABS wheel speed sensor port.",

### `cylinder_heads_valvetrain_3d` — Aluminum Cylinder Heads, Valves, Hydraulic Roller Followers & Head Bolts

Cast aluminum cylinder heads for Cologne 4.0L SOHC V6 featuring 12 overhead valves (6 intake / 6 exhaust), single overhead camshaft per bank, hydraulic roller rocker arm followers, beehive valve springs, MLS multi-layer steel head gaskets, and torque-to-yield head bolts.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Head Bolts - Pass 1 | 26 | 35 | Use NEW TTY Bolts |
| Head Bolts - Pass 2 | 59 | 80 | Tighten in specified sequence |
| Head Bolts - Pass 3 | Rotate +90° | Angle Torque | Final angle torque turn |

### `front_control_arms_balljoints_3d` — Front Upper & Lower Control Arms with Heavy-Duty Press-In Ball Joints

Forged steel upper and lower front A-arm control suspension arms complete with natural rubber frame pivot bushings, greaseable heavy-duty press-in upper/lower ball joints, torsion bar mounting socket, and sway bar end link mounts.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Lower Ball Joint Pinch Nut | 83 | 112 | Install fresh cotter pin |
| Upper Control Arm Camber Pinch Bolts (2 Bolts) | 98 | 133 | Requires 4-wheel alignment after replacement |
| Lower Control Arm Frame Pivot Bolts | 111 | 150 | Torque while vehicle weight is resting on suspension |

### `radio_mach500_head_unit_3d` — Mach 500 / Pioneer Premium Double-DIN AM/FM Radio & CD Changer Head Unit

Factory premium double-DIN stereo receiver head unit featuring integrated 6-disc in-dash CD changer mechanism, RDS digital radio tuner module, dot-matrix green vacuum fluorescent display screen, dual volume/tuner rotary encoder dials, speed-compensated volume control processor, rear subwoofer preamp output plug, and dual 16-pin Ford factory wiring harness sockets.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Center Dash Bezel Screws (7mm Head) | 18 in-lbs | 2 Nm | Two 7mm screws located above climate control knobs |
| Radio Side Mounting Bracket Screws | 15 in-lbs | 1.7 Nm | Four mini T15 Torx screws into side chassis |

Technical mentions:

- description = "Factory premium double-DIN stereo receiver head unit featuring integrated 6-disc in-dash CD changer mechanism, RDS digital radio tuner module, dot-matrix green vacuum fluorescent display screen, dual volume/tuner rotary encoder dials, speed-compensated volume control processor, rear subwoofer preamp output plug, and dual 16-pin Ford factory wiring harness sockets.",
- RepairStep(4, "Slide Radio Chassis Out & Unplug Harnesses", "Spread DIN keys outward slightly and pull head unit straight forward. Unplug main 16-pin wiring harness, subwoofer RCA/remote plug, and motorola antenna coax lead."),
- RepairStep(5, "Install Replacement Head Unit / Aftermarket Adapter", "Connect Ford wiring harness adapter plug, snap antenna coax lead in place, and slide chassis into center stack frame until latches lock.")

### `audio_door_speakers_subwoofer_3d` — Factory 6x8 Custom Door Speakers, Tweeters & Pioneer 8\

High-fidelity vehicle audio transducer system comprising four 6x8-inch polypropylene full-range coaxial door speakers with treated cloth surrounds, A-pillar silk dome tweeters, and the factory Pioneer rear cabin trim-integrated 8-inch powered subwoofer enclosure with dedicated 290-watt peak audio amplifier module.

| Component | Imperial | Metric | Notes |
|---|---:|---:|---|
| Door Speaker Mounting Screws (4 Screws per Door) | 18 in-lbs | 2 Nm | 7mm screws into plastic door sheet metal inserts |
| Door Interior Handle Screws (7mm Head) | 25 in-lbs | 2.8 Nm | Two screws behind armrest access cover |
| Pioneer Subwoofer Enclosure Bracket Bolts | 89 in-lbs | 10 Nm | Three 10mm bolts to rear cab wall |

Technical mentions:

- requiredTools = listOf("7mm & 8mm Sockets & Driver", "T20 Torx Bit", "Plastic Interior Door Trim Removal Pry Tool", "Wire Stripper & Crimp Connectors"),
- RepairStep(4, "Install 6x8 Speaker & Harness Adapter", "Snap plug-and-play Ford speaker wire harness adapter onto speaker terminals, secure frame with 7mm screws, and reattach door trim panel."),

### `q_audio_symptom` — Unnamed entry

Technical mentions:

- probableCause = "Dry rotted speaker surround or broken door hinge wire harness flex leads.",

## Repository wiring evidence

The repository does not appear to contain a complete factory wiring-diagram or connector-pinout dump. `docs/SPORT_TRAC_ALL_REPAIRS.md` explicitly states that a wiring-diagram dump is not included. The available wiring evidence is therefore limited to model/data descriptions, component labels, and repair-step text such as ABS harness, dash harness/CJB/GEM, headlight harness, and window/roof motor connectors.

### `docs/BLENDER_GRAPHICS_CONTRACT.md`

- Line 11: | Compose Canvas procedural parts | Training schematic | Live behind the safe-scene gate |

### `docs/NEXT_100_TASKS_2026-08-26.md`

- Line 105: ## G. Overlay / cache / wiring (81–86)
- Line 132: 100. Local dirty OG wiring in `SportTracData` / maintenance consumers is part of this pass.

### `docs/SPORT_TRAC_ALL_REPAIRS.md`

- Line 15: 5. BW4411: fill to hole bottom, 1.3 qt. Shift motor CHARM Transfer case.
- Line 23: Not included: TTY head bolts, 5R55E overhaul, wiring-diagram dump.

### `docs/SPORT_TRAC_EVERYTHING.md`

- Line 19: This is still not every clip Ford installed. TTY head bolts, interior screws, body clips, and harness retainers stay workshop-manual items.

### `docs/SPORT_TRAC_FASTENERS.md`

- Line 4: Not a VIN-complete Ford BOM. Hidden TTY head bolts, interior screws, body clips, and harness retainers are not claimed.

## Explicit torque data found in repository documents

| Source | Component / use | Value | Evidence status |
|---|---|---:|---|
| docs/2004_SPORT_TRAC_OWNER_GUIDE_SPECS.md | Wheel lug nuts | 84–114 lb-ft (113–153 Nm) | Owner Guide value reproduced in repository; roadside/wheel torque, not a complete workshop torque table. |
| docs/2004_SPORT_TRAC_OWNER_GUIDE_SPECS.md | Transfer case refill | 1.3 qt MERCON ATF | Fluid capacity, not fastener torque. |
| docs/2004_SPORT_TRAC_OWNER_GUIDE_SPECS.md | Front axle refill | 1.8 qt SAE 80W-90 | Fluid capacity, not fastener torque. |
| docs/SPORT_TRAC_BANK2_LEAN_KIT.md | Intake manifold bolts | 10 Nm / 89 in-lb | Repository workshop reference; verify against exact Ford workshop page. |
| docs/SPORT_TRAC_BANK2_LEAN_KIT.md | Throttle body bolts | 9 Nm / 80 in-lb | Repository reference attributed to CHARM 2005 VIN K. |
| docs/SPORT_TRAC_BANK2_LEAN_KIT.md | Ignition coil bolts | 6 Nm / 53 in-lb | Repository reference attributed to Mitchell 2004 engine-performance table. |
| docs/SPORT_TRAC_BANK2_LEAN_KIT.md | Coil bracket bolts | 10 Nm / 89 in-lb | Repository reference attributed to Mitchell 2004 engine table. |
| docs/SPORT_TRAC_BANK2_LEAN_KIT.md | Spark plugs | 20 Nm / 15 lb-ft | Repository reference attributed to Mitchell 2004 engine-performance table. |
| docs/SPORT_TRAC_BANK2_LEAN_KIT.md | Fuel rail bolts | 23 Nm / 17 lb-ft | Repository reference attributed to Mitchell 2004 engine-performance table. |
| docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md | Intake bolts | 10 Nm / 89 in-lb | Repository workshop note; verify. |
| docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md | Throttle body | 9 Nm / 80 in-lb | Repository workshop note; verify. |
| docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md | Coil bolts / bracket | 6 Nm / 53 in-lb; 10 Nm / 89 in-lb | Repository workshop note; verify. |
| docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md | Spark plugs | 20 Nm / 15 lb-ft | Repository workshop note; verify. |
| docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md | Fuel rail | 23 Nm / 17 lb-ft | Repository workshop note; verify. |
