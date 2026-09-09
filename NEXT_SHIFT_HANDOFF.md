# Ford Sport Trac Mentor — Next-Shift Handoff

**Prepared:** 2026-09-09  
**Repository:** https://github.com/Gtownrter77/Ford-  
**Target:** 2004 Ford Explorer Sport Trac, 4.0L SOHC V6, 4WD, VIN K Flex Fuel  
**Current Android HEAD at last pull:** `134b651` (`refactor: optimize UI defaults and project structure`, 2026-09-08)  
**This checkpoint:** CHARM web bay + HVAC Testing and Inspection + CB1 Linux notes  
**Pushed:** `9a7d080` (docs) · `7036912` (mentor-web source + 598 labor JSON)  
**Cadence:** 5 substantive changes → push (WORKFLOW_RULES.md)

---

## Two products, one truck

| Product | Where | Status |
|---|---|---|
| Android Mentor (Kotlin / Compose / GLB) | `app/` on `main` | Last ship: Google sign-in gate + OBD/FORScan. Physical-device Google sign-in still open. |
| Web Mentor bay (React / Three / CHARM book) | `mentor-web/` (this checkpoint) | Playable 1:1 bay. CHARM 4WD VIN K labor catalog (598 leaves). HVAC diagnostics desk from printed Testing and Inspection leaves. |

Do not treat the web bay as a replacement for the Android app. It is the CHARM book + meter-true hull for rehearsal while CB1 / Pixel 8 / Android catch up.

Do not treat CHARM as Ford. It is a third-party copy. VECI and the under-hood label override a table if they differ.

---

## What landed 2026-09-07 → 2026-09-09 (web)

1. Meter-true 2004 Sport Trac hull (Owner Guide dimensions). Oxford White / Shadow Grey / Redfire.
2. Shop bay + WebXR enter (Quest browser). Orbit / walk the bay.
3. CHARM 4WD VIN K tree only. 9,562 HTML leaves in the pack. **598 labor-times tables** parsed into `mentor-web/src/lib/mentor/charm-labor.json`.
4. HVAC labor cards with printed hours: compressor 1.1, clutch 1.8, shaft seal 1.8, condenser 1.1, evaporator 2.1/2.7, heater core 8.8/2.7, recover 0.4, evac/charge 1.4, system diagnosis 1.0, pinpoint 0.5.
5. Compressor **Service and Repair** (leaf 1705) + **External Leak Test** (leaf 1703) as printed steps.
6. HVAC **Testing and Inspection** desk (leaves 1759–1779, pinpoint A–K 1762–1772).
7. Search + section chips over the 598 labor leaves. Hours only as CHARM printed them. No guessed torque, charge, or oil.

### CHARM HVAC diagnostics (printed)

| Card | Leaf | What it is |
|---|---|---|
| Inspect / verify | 1759 | Duplicate concern, visual inspect, DLC scan tool, data-link, PCM DTCs, else symptom chart |
| HVAC DTC index | 1760 | **P1460–P1469** (factory chart — image, not OCR'd) |
| Symptom chart | 1761 | Factory chart → pinpoint A–K (image, not OCR'd) |
| Retail system check | 1775 | 10 min MAX A/C idle, gauges at 1500 rpm, blower/mode/temp, leak if low |
| Refrigerant pressures | 1779 | Proc 1 ≤100°F / proc 2 above. Record suction & discharge vs CHARM charts |
| Electronic leak | 1776 | 60–80 psi, engine off, H10PM, ventilate first |
| Evaporator / condenser leak | 1774 | Recover, isolate core, 45 min vacuum, 30 in-Hg hold |
| Heater core test | 1778 | Outlet-hose hot check, then 35 psi / 3 min |
| Pinpoint A–K | 1762–1772 | Named tests + chart groups. Cells are factory images — follow the book |

Safety printed on 1751: airbag backup — battery ground off, wait one minute, before climate-control work. R-134a only. Analyzer before recover.

---

## Android app — unchanged from last `main` (do not downgrade)

Google sign-in is **mandatory** in the Android product. No anonymous fallback.

- Firebase + Credential Manager implemented (`e9eec61` lineage).
- Next shift still owes: `google-services.json`, `GOOGLE_WEB_CLIENT_ID`, physical Pixel 8 sign-in, sign-out UI, revoked-session test.
- OBD: ELM327 SPP + standard PID/DTC. No module programming. No auto-clear.
- Mentor retrieval of the full CHARM corpus is still the largest Android gap. The web bay's 598-leaf labor JSON is a start for that index — do not paste 9,562 pages into Gemini.

---

## CB1 Chromebook Linux (penguin) — infrastructure

**Role:** cracked-screen 4 GB Chromebook, 24/7 house server. Pixel 8 (HB1) is the remote screen via Tailscale.

| Item | Last observed 2026-09-07 |
|---|---|
| Host | Crostini `penguin` / Debian trixie, ~2.7 GiB visible RAM |
| Card 1 | `/dev/sdc1` ext4 label `SD_SRC`, UUID `70040cfb-73e4-4813-ab03-602daf79a901`, mounted `/mnt/sd2` (~469G) |
| fstab | `UUID=… /mnt/sd2 ext4 noatime,nodiratime,commit=120,errors=remount-ro,nofail,x-systemd.device-timeout=8 0 2` |
| Swapfile | `/mnt/sd2/swapfile` 8 GiB, pri 10, UUID `789f7f71-79ae-4b31-98b0-94181a08b40f` |
| zram | `systemd-zram-generator` ram/2 lz4 — **device timed out**. Reboot then `sudo systemctl start /dev/zram0` |
| Second 512 GB card | Not seen in Crostini. Do not bind-mount as fake sd3. |
| Node | v22.23.2 (NodeSource) |
| Grok CLI | 1.0.13, signed in `rlongmbox@gmail.com` via `grok login --device-auth` |
| OmniRoute | `sudo npm install -g omniroute` was **still running** (PID ~10551, swapping). Do not claim installed until `omniroute --version` works. |
| OpenCode | **Not installed this shift.** |
| ChromeOS | Stay signed in. Lid open, charger in, sleep never on AC — or Crostini dies. |

Tailscale IPs from the 2026-09-07 operator note (verify with `tailscale status` before trusting):

- CB1 penguin-1: `100.78.197.121`
- CB2 penguin: `100.100.214.8` (was stale)
- HB1 Pixel 8: `100.68.223.72`

---

## Source of truth

- CHARM tree: `2004 Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel`
- Pack: charm.li bundle for that tree (~129 MB zip, **not** in git)
- Labor JSON: `mentor-web/src/lib/mentor/charm-labor.json` (598 tables)
- HVAC procedures: `mentor-web/src/lib/mentor/hvac-diagnostics.ts` + compressor steps in `book.ts`
- Scale: `mentor-web/src/lib/mentor/scale.ts` — Owner Guide inches

**Never silently use a 2WD leaf.**

---

## Next crew — ordered

1. Confirm OmniRoute finished on CB1 (`which omniroute` / health). If npm died, retry with `NODE_OPTIONS=--max-old-space-size=512`.
2. Install OpenCode: `npm install -g opencode` after Node is stable.
3. Reboot CB1 once for zram. Confirm `swapon --show` has zram prio 100 and sd2 swapfile prio 10.
4. Pixel 8: Tailscale + Android Google sign-in on the Kotlin app.
5. Port the 598-leaf labor JSON + HVAC diagnostic cards into Android `CharmWorkshopIndex` / Room FTS (Phase A–C in the previous handoff).
6. Pinpoint A–K charts are images. OCR or ship the PNGs — do not invent cells.
7. Keep 5-change push cadence.

---

## Do not

- Invent labor, torque, charge, oil, or clutch gap.
- Recover / evacuate / charge without professional equipment.
- Mark physical A/C, oil pressure, or timing “fixed” from this bay.
- Commit the 129 MB CHARM zip or `node_modules`.

## Rights

**© 2026 Guru Studios of ATL. All rights reserved.** See `LICENSE` and `COPYRIGHT.md`. Ford marks remain Ford’s. CHARM pages remain third-party.

## Complete (2026-09-09)

Mentor HVAC desk is closed: Diagnose / Pinpoint A–K / R&R / Tree 598. Symptom chart 1761 and DTC P1460–P1469 on the desk. Clickable FS-10, condenser, orifice, accumulator, evaporator, heater, blower, control. Admin lock is studio-only and is not stored in this public repo.

## Engine desk (2026-09-09)

2004 Sport Trac has one engine: 4.0L SOHC FFV V6 VIN K. Owner Guide 04p27og2e. No 4.6 on this body. VIN E is not a bolt-in. CHARM leaf 7934 for R&R hours.
