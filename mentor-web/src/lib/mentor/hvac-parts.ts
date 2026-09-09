import type { MentorJob } from "./book";
import { LABOR } from "./charm-catalog";

const HVAC = "Heating and Air Conditioning";

const HOT: Record<string, [number, number, number]> = {
  "Compressor HVAC": [0.28, 0.72, 1.72],
  "Compressor Clutch": [0.28, 0.72, 1.82],
  "Compressor Clutch Bearing": [0.28, 0.72, 1.82],
  "Compressor Clutch Hub": [0.28, 0.72, 1.82],
  "Compressor Shaft Seal": [0.28, 0.72, 1.82],
  "Condenser HVAC": [0.0, 0.72, 2.35],
  "Receiver Dryer": [0.32, 0.78, 1.35],
  "Expansion Block/Orifice Tube": [0.18, 0.7, 1.45],
  "Expansion Valve": [0.0, 0.78, 1.12],
  "Evaporator Core": [0.0, 0.78, 1.12],
  "Heater Core": [0.0, 0.78, 1.05],
  "Heater Hose": [-0.12, 0.82, 1.12],
  "Hose/Line HVAC": [0.12, 0.7, 1.55],
  "Blower Motor": [0.22, 0.72, 1.08],
  "Blower Motor Resistor": [0.22, 0.68, 1.02],
  "Blower Motor Switch": [0.0, 0.98, 0.92],
  "Control Assembly": [0.0, 0.98, 0.92],
  "Air Door Actuator / Motor": [0.0, 0.92, 1.0],
  "Air Door": [0.0, 0.9, 1.02],
  "Housing Assembly HVAC": [0.0, 0.82, 1.08],
  "A/C Coupler O-ring": [0.18, 0.7, 1.5],
  "Refrigerant Pressure Sensor / Switch": [0.22, 0.68, 1.48],
  "Vacuum Sensor / Switch HVAC": [0.0, 0.95, 0.95],
  "Heating and Air Conditioning": [0.28, 0.72, 1.72],
};

/** Labor leaves already given service-and-repair cards in book.ts JOBS. */
const RICH = new Set(["8361", "9331", "9336", "9338", "8383", "9343", "9347"]);

export const HVAC_PARTS: MentorJob[] = LABOR.filter(
  (l) => l.s === HVAC && !RICH.has(l.id),
).map((l) => ({
  id: `leaf-${l.id}`,
  title: l.t,
  system: `HVAC · CHARM labor leaf ${l.id}`,
  pages: [{ id: l.id, kind: "Labor Times" }],
  labor: l.r.map((r) => ({
    op: r.o,
    item: r.i ?? undefined,
    std: r.h,
    warr: r.w,
    skill: r.k,
    notes: r.n,
  })),
  cautions: [
    "Printed CHARM labor only on this card. Recover / evacuate / charge are add operations on leaf 8383.",
  ],
  hotspot: HOT[l.t] ?? [0.0, 0.82, 1.2],
}));
