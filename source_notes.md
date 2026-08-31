# Repository Source Notes

Repository: https://github.com/Gtownrter77/Ford-

Repository metadata: owner `Gtownrter77`, repository `Ford-`, default branch `main`, public, latest inspected commit `0f663a0` with message `Add 100-job 3-level check ledger for 2004 Sport Trac 4WD VIN K.` The repository is an Android project with project-authored documentation and Kotlin data, not a raw factory service-manual archive.

Primary extracted source: `app/src/main/java/com/example/data/SportTracData.kt`. The repository includes 4WD entries such as `driveshaft_4x4` (Control Trac 4WD transfer case & driveshafts), `wheel_bearings_hubs_3d` (front axle shaft nut / hub bolts / ABS harness), and many wiring-related entries such as `wiring_lighting_3d`, `transmission_solenoids`, `dash_wiring_harness_3d`, and connector repair steps.

4WD entry details found in the repository: BorgWarner 44-11 transfer-case description; front and rear driveshafts; Ford 8.8 rear differential; stated repository torque strings of driveshaft flange bolts 83 (no unit in the Kotlin field), transfer-case drain/fill plugs 22 (no unit in the Kotlin field), rear differential cover bolts 33 (no unit in the Kotlin field). The same entry states a 12-point 12 mm flange-bolt note, blue threadlocker, MERCON V fluid language, and a 4WD shift-motor inspection step. These values are repository-authored data and must be verified against the exact Ford workshop page before field use.

Front 4WD hub entry: front axle shaft nut 184 / 250, hub bearing assembly to knuckle bolts 83 / 112, brake caliper anchor bracket bolts 85 / 115, plus an ABS wheel-speed sensor harness connector repair step. The repository explicitly labels the axle nut as `4WD Models`.

Wiring evidence: `docs/SPORT_TRAC_ALL_REPAIRS.md` explicitly says `Not included: ... wiring-diagram dump.` Therefore the repository does not contain a complete factory wiring schematic or connector-pinout dump. Available wiring evidence is limited to project data descriptions and repair-step text, including engine harness/fuel-injector connectors, main transmission solenoid 16-pin harness, engine/headlight/CJB wiring loom, dash CJB/GEM/ignition/HVAC/radio interfaces, ABS wheel-speed sensor harness connector, oxygen-sensor 4-pin harness connector, tailgate wiring, and window/sunroof motor connectors.

Owner Guide source document in repository: `docs/2004_SPORT_TRAC_OWNER_GUIDE_SPECS.md`, which links to Ford-hosted PDFs at `https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og1e.pdf`, `04p27og2e.pdf`, `04p27og3e.pdf`, and quick reference `04p27qg1e.pdf`. It states that the Owner Guide is not a workshop manual. It lists 4x4 transmission dry-fill approx. 9.8 L / 10.3 qt, transfer case 1.2 L / 1.3 qt MERCON ATF, front axle 1.7 L / 1.8 qt SAE 80W-90, wheel-lug torque 113–153 Nm / 84–114 lb-ft, and cautions that workshop torque sequences and diagnostic pinpoint tests remain outside that document.

Repository documentation caveats: `docs/SPORT_TRAC_FASTENERS.md` says it is not a VIN-complete Ford BOM and that packaged torque strings should be confirmed against the workshop manual. `docs/SPORT_TRAC_BANK2_LEAN_KIT.md` attributes intake, throttle body, coil, spark-plug, and fuel-rail values to workshop/CHARM/Mitchell references rather than a single Ford factory manual page.
