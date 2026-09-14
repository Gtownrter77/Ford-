# Ford Digital Twin + Mentor Integration

## Core principle

The Digital Twin is the Mentor's structured mechanical memory and spatial context. It is not another chatbot.

## Runtime flow

User / Camera
→ Component identification or user selection
→ Canonical component ID
→ Digital Twin data
→ assembly hierarchy
→ dependency graph
→ fasteners, tools, and safety
→ service state
→ source confidence
→ Mentor brain
→ selected Mentor character
→ real-time guidance

## Shared contract

Use versioned machine-readable data such as:

- vehicle_manifest.json
- components.json
- assemblies.json
- dependency_graph.json
- fasteners.json
- service_states.json
- mentor_context.json
- sources_and_confidence.json

Stable canonical component IDs are the integration key. Mesh names are implementation details and must not become the permanent identity system.

## Phased plan

1. Stabilize the Twin and configuration boundary.
2. Build canonical component identities and assembly hierarchy.
3. Add dependency and access relationships.
4. Add fasteners and service metadata.
5. Add service-state progression.
6. Connect one representative repair to the Mentor.
7. Validate end-to-end before expanding.
8. Improve visuals, highlighting, exploded views, and camera localization after mechanical data is reliable.

## Synchronization rule

The repository is the source of truth for tracked source files.

A distributable ZIP should be generated from the same project state and commit used for release. Do not manually evolve the ZIP and repository independently.
