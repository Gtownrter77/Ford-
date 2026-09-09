# Web Mentor CHARM bay

**Date:** 2026-09-09  
**Path:** `mentor-web/`  
**Vehicle:** 2004 Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel

## What it is

A 1:1 rehearsal bay. The hull is meter-true from the Owner Guide. The book is CHARM 4WD VIN K only. Hours, cautions, and procedures are printed from those leaves. Nothing else.

## Layout

```
mentor-web/src/
  components/bay/       Three.js hull, shop, WebXR canvas
  components/mentor/    CHARM overlay: search, sections, HVAC desk, labor table
  lib/mentor/
    scale.ts            Owner Guide inches
    book.ts             HVAC labor + compressor procedure cards
    hvac-diagnostics.ts Testing and Inspection cards (1759–1779, A–K)
    charm-labor.json    598 labor-times tables
    charm-catalog.ts    search / section helpers
    store.ts            zustand persist key trac-mentor-vr-v4
```

## Evidence rules

- 4WD VIN K tree only. 2WD leaves are blocked.
- Labor numbers come from `labor-times-table` cells. If a column shifted in CHARM HTML, the parser required a numeric standard hour.
- Pinpoint A–K and the HVAC DTC / symptom charts are **images** in CHARM. The bay names the test and the chart groups. It does not OCR the cells.
- Recover, evacuate, leak, and charge stay professional-equipment work.
- VECI / under-hood label override a table if they differ.

## HVAC diagnostic flow (as CHARM printed)

1. Leaf 1759 — inspect / verify / scan.
2. Leaf 1760 — PCM HVAC DTCs P1460–P1469 if related.
3. Leaf 1761 — symptom chart if no related DTC.
4. Pinpoint A–K (1762–1772) as the chart directs.
5. Component tests 1774–1779 as needed (leak, pressures, heater core, retail check).

System diagnosis labor: 1.0. Pinpoint: 0.5. Both from leaf 8383.

## Not in this tree

- The 129 MB CHARM zip
- Android APK / Gradle
- OmniRoute / OpenCode runtime
- Live OBD
