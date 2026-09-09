# Checkpoint — 2026-09-09

**Repo:** `Gtownrter77/Ford-`  
**Android HEAD before this push:** `134b651`  
**Docs commit:** `9a7d080`  
**mentor-web commit:** `7036912`  
**This checkpoint adds:** web Mentor CHARM bay + HVAC Testing and Inspection + CB1 notes.

## Change counter (5-change cadence)

| # | Change |
|---|---|
| 1 | Meter-true 3D Sport Trac hull + shop bay + WebXR |
| 2 | CHARM 4WD VIN K labor catalog (598 leaves) |
| 3 | HVAC component labor + compressor R&R / leak-test steps |
| 4 | HVAC Testing and Inspection desk (1759–1779, pinpoint A–K) |
| 5 | **This push** — handoffs, checkpoint, `mentor-web/` source |

Cadence reset after this commit.

## Validation

- Web typecheck: `tsc --noEmit` passed.
- Web production build passed in the Grok Build sandbox.
- Browser smoke: 200, canvas present, no page errors, CHARM HVAC desk visible.
- Android `assembleDebug` was **not** re-run this shift. Do not claim a new APK.

## Recover

```
git clone https://github.com/Gtownrter77/Ford-.git
# Web book lives under mentor-web/
# Android app is unchanged under app/
```

CHARM zip is not in git. Re-download:

`https://charm.li/bundle/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/`
