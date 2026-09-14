# ChatGPT Addendum for the Next Shift

This document is an addendum to the existing Manus handoff. **Do not replace, rewrite, or discard Manus's handoff.** Read it first, then use this file to understand how the Ford Digital Twin work connects to the Android app and Mentor work.

## Shared vision

The project is not merely a 3D Ford model and not merely a chatbot.

The intended system is:

User / Camera
→ component identification or user selection
→ canonical component ID
→ Digital Twin
→ assembly + dependency graph
→ fasteners + tools + safety + service state
→ Mentor brain
→ selected Mentor character
→ calm, real-time guidance.

The Digital Twin provides structured mechanical and spatial truth. The Mentor turns that truth into a human experience.

## Ownership boundary

### Digital Twin / Manus work
Owns:
- vehicle configuration and evidence boundary
- canonical component IDs
- assemblies and hierarchy
- dependency/access relationships
- fasteners, clips, connectors, gaskets, seals
- service metadata
- service states
- 3D spatial mapping
- source and confidence metadata

### App / Mentor work
Owns:
- Mentor Mode
- conversation and guidance flow
- repair-session orchestration
- character presentation
- voice settings and optional user-controlled personalization
- app integration
- presenting Twin data to the user

Do not duplicate mechanical truth inside character prompts or hard-coded chat scripts when it belongs in the Twin.

## Mentor architecture

There are two distinct agents:

1. **Mentor Agent** — in-app guided chatbot. It talks to the user, maintains repair context, explains steps, encourages the user, and adapts delivery to the selected Mentor character.

2. **Research / Web / Parts Agent** — separately researches sources, parts, and supporting evidence. Its findings must be verified before being treated as authoritative mechanical truth.

The Mentor should not be redesigned into a web-search interface.

## Mentor characters

The app currently supports three selectable identities:
- Master Mechanic — rugged, calm, practical, dry humor
- Precision Engineer — precise, analytical, calm, caring
- Gearhead — energetic, approachable, enthusiastic

Character changes presentation and personality, **not mechanical facts**. All characters should consume the same verified Twin context.

Voice choice is separate from character choice. Any future voice cloning/personalization must be explicit opt-in and consent-based.

## Immediate integration priority

Do not attempt the whole vehicle.

The first end-to-end milestone is the existing **Drive Belt / Alternator vertical slice**.

For this slice, the Digital Twin should progressively provide:
- canonical component identity
- assembly context
- verified access dependencies
- safety preconditions
- electrical connector relationships
- fastener records
- verified tools/specifications where available
- source confidence
- valid repair-state transitions

The app/Mentor should then consume that structured context and guide one repair path without guessing the mechanical sequence.

## Rules for the next ChatGPT

- Preserve existing Manus work.
- Preserve existing app work.
- Do not create a competing second contract.
- Use canonical IDs as the bridge between model and app.
- Keep unknowns explicit.
- Never invent torque values, quantities, or removal order.
- Start with one complete vertical slice before expanding.
- Keep GitHub and distributable ZIP synchronized from the same source/commit whenever an actual release archive is produced.
- Make changes additive and traceable.

## Definition of success

The first integration succeeds when the Mentor no longer has to infer the mechanical structure of a repair.

The Twin provides the facts.

The Mentor provides the calm, empathic, real-time human guidance.

