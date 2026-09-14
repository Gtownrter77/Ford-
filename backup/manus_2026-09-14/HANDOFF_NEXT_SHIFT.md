# Ford Digital Twin — Next Shift Handoff

## Package purpose

This is the current model-only handoff for the 2004 Ford Explorer Sport Trac target: gray/silver exterior reference, 4WD, 4.0L V6, VIN K Flex Fuel manual configuration.

## Start here

1. Open `updated_model_preview/` as the current browser model project.
2. Read `UPDATED_MODEL_STATUS.md` for the current boundary and limitations.
3. Read `mentor_contract/ROADMAP_FINDINGS.md` for the Twin/Mentor ownership boundary.
4. Read `mentor_contract/vertical_slice_drive_belt_alternator.json` for the first vertical service slice.
5. Use `model_inventory/` for the canonical inventory and hierarchical assembly tree.
6. Use `reference_photos/grey_2004_sport_trac_4wd.jpg` as the exterior appearance reference.

## Current live preview

https://3000-iuvvji7cylbqd5fr4in6f-37119a70.us4.manus.computer/?view=exterior-refresh

The preview now opens with the opaque exterior truck view. Internal systems are enabled through the layer controls; Cutaway and Fly-through remain available.

## Authoritative evidence boundary

The GitHub repository reports an indexed 9,560-page source package for the target configuration:

`2004 Ford Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel`

Use the 4WD VIN K material as the primary mechanical source. Do not use 2WD data as the authority for 4WD-specific relationships. Do not invent torque values, hardware quantities, removal order, or VIN-level certainty.

## Current contract files

- `mentor_contract/vehicle_manifest.json`
- `mentor_contract/components.json`
- `mentor_contract/assemblies.json`
- `mentor_contract/dependency_graph.json`
- `mentor_contract/fasteners.json`
- `mentor_contract/service_states.json`
- `mentor_contract/mentor_context.json`
- `mentor_contract/sources_and_confidence.json`
- `mentor_contract/vertical_slice_drive_belt_alternator.json`

## Recommended next action

Complete the drive-belt/alternator vertical slice from authoritative page-level evidence. Add page references, verified dependencies, access blockers, electrical connector records, fastener records, safety conditions, removal/install order, and state transitions. Then validate the same contract end-to-end through the model preview and Mentor context payload before starting a second repair slice.

## Preserve the foundation

Do not restart the project. Preserve the existing model, GLB, inventory, hierarchy, reference work, and preview. Improve one subsystem at a time. Keep unknowns explicit.
