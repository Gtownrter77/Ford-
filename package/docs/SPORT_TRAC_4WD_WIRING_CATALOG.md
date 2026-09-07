# 2004 Ford Explorer Sport Trac 4WD Wiring Evidence Catalog

## Verified source package

The repository now embeds the collected 4WD VIN K electrical diagram source tree under [`docs/wiring_diagrams/charm_4wd_vin_k/`](wiring_diagrams/charm_4wd_vin_k/). The package contains 274 discovered CHARM category entries, 147 saved source HTML pages, 357 downloaded PNG plates on disk, and a root `index.json` with source URLs, HTTP statuses, plate URLs, local paths, and byte sizes. All discovered category requests returned HTTP 200 and all indexed plate references resolve to files in the package. `SHA256SUMS` covers the embedded files.

The package was fetched from the [Operation CHARM 2004 Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel hub](https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/) and its live Repair and Diagnosis diagram tree. The fetcher is retained at [`tools/crawl_charm_4wd_vin_k.py`](../tools/crawl_charm_4wd_vin_k.py).

## 4WD control diagrams

The most relevant verified 4WD source family is the local [`transfer_case/`](wiring_diagrams/charm_4wd_vin_k/transfer_case/) folder. It includes three source plates for the Transfer Case electrical diagram family:

| Plate | Local source file | What is visible on the plate |
|---|---|---|
| 34-1 | [`34-1__1027137804.png`](wiring_diagrams/charm_4wd_vin_k/transfer_case/34-1__1027137804.png) | Four-wheel-drive control module, transfer-case assembly, C281A/C281B, C215/C350, fuse feeds, VREF/VPWR, VSS, grounds, wire color/gauge labels, and transfer-case motor/position-switch circuits. |
| 34-2 | [`34-2__1027155903.png`](wiring_diagrams/charm_4wd_vin_k/transfer_case/34-2__1027155903.png) | 4x4 mode switch with Off/High/Low positions, 4WD Low/High instrument indications, C284, central security module, data-link references, grounds, illumination, and wire color/gauge labels. |
| 34-3 | [`34-3__1027171573.png`](wiring_diagrams/charm_4wd_vin_k/transfer_case/34-3__1027171573.png) | Four-wheel-drive control module reference, brake-pedal-position input, digital transmission-range sensor, C278/C281A/C167/C144, grounds, splice references, and 4x4 Low range enable logic. |

The source page for these plates is recorded in [`transfer_case/metadata.json`](wiring_diagrams/charm_4wd_vin_k/transfer_case/metadata.json), with the raw HTML preserved beside it.

## Other embedded electrical diagram families

The complete package includes the available source pages and plates for air-bag systems, anti-lock brakes/traction control, charging, climate control, cruise control, data-link connector, daytime-running lamps, fog/driving lamps, headlamps, horn, ignition, instrument cluster, instrument illumination, keyless entry, lighting, neutral-safety switch, overhead console, parking/marker/license-plate lamps, power door locks, power lumbar/seats/mirrors, radio/stereo/CD, remote alarm/locks, seat temperature elements, shift interlock, sunroof/moonroof, tail/trailer lamps and connector, transfer case, turn signals, windows, and wiper/washer systems. The source tree also preserves diagram sets, component views, grounds, splices, harness pages, and connector-related pages discovered from the live tree.

## Repository-authored evidence versus factory source

The Kotlin data and earlier extraction documents remain useful as a teaching index for connector descriptions and component relationships, but they are not a substitute for the embedded source plates. Repository-authored statements such as connector pin counts, repair-step wording, and torque strings are retained with their provenance and should not be promoted to factory specifications unless the corresponding source page or plate verifies them.

The local Lemon archive originally downloaded for this project is explicitly labeled **2WD**. It must not be used to represent the 4WD wiring source. The embedded CHARM tree is the source used for the 4WD diagram package. The repository also does not claim that every electrical diagram has a complete terminal-by-terminal pinout; the source package contains only the pages and plates exposed by the live 4WD tree.

## Integrity status

A package validator confirms that all discovered category requests returned HTTP 200, all indexed plate references resolve to local files, no stale error files remain, and the embedded PNG files are recognized as PNG images. The package is ready for repository use as a source archive, subject to the source provider’s redistribution terms.

## References

[1]: https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/ "Operation CHARM: 2004 Ford Explorer Sport Trac 4WD-4.0L VIN K Flex Fuel"
[2]: https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/ "Operation CHARM: Repair and Diagnosis"
[3]: https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/Diagrams/Electrical%20Diagrams/Transfer%20Case/ "Operation CHARM: Transfer Case Electrical Diagrams"
