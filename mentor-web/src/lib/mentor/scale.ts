/** Meter-true 2004 Explorer Sport Trac. Owner Guide is the authority. */
export const INCH = 0.0254;

export const LENGTH = 205.9 * INCH;
export const WIDTH = 71.8 * INCH;
export const HEIGHT = 70.6 * INCH;
export const WHEELBASE = 125.9 * INCH;
export const TRACK_F = 58.5 * INCH;
export const TRACK_R = 58.3 * INCH;
export const FRONT_OVERHANG = 34.6 * INCH;
export const REAR_OVERHANG = 45.4 * INCH;
export const BED_LEN = 50.0 * INCH;
export const BED_IN_H = 19.7 * INCH;
export const BED_IN_W = 51.2 * INCH;
export const LOAD_H = 31.8 * INCH;
export const GROUND = 6.7 * INCH;

export const TIRE_SECTION = 0.265;
export const TIRE_ASPECT = 0.7;
export const WHEEL_D = 16 * INCH;
export const TIRE_R = WHEEL_D / 2 + TIRE_SECTION * TIRE_ASPECT;
export const TIRE_W = TIRE_SECTION;
export const WHEEL_R = WHEEL_D / 2;

export const FRONT_AXLE_Z = WHEELBASE / 2;
export const REAR_AXLE_Z = -WHEELBASE / 2;
export const NOSE_Z = FRONT_AXLE_Z + FRONT_OVERHANG;
export const TAIL_Z = REAR_AXLE_Z - REAR_OVERHANG;

export const BODY_W = WIDTH - 0.1;
export const CAB_REAR_Z = TAIL_Z + BED_LEN + 0.05;
export const COWL_Z = FRONT_AXLE_Z - 0.46;

export const PAINTS = {
  oxford: { name: "Oxford White", color: "#e6e2d8" },
  shadow: { name: "Shadow Grey", color: "#3e4246" },
  redfire: { name: "Redfire", color: "#7a2424" },
} as const;

export type PaintId = keyof typeof PAINTS;
