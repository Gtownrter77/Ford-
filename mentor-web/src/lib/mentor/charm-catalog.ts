import laborJson from "./charm-labor.json";

export type CharmRow = {
  o: string;
  i: string | null;
  h: string;
  w: string;
  k: string;
  n: string;
};

export type CharmLeaf = {
  id: string;
  t: string;
  s: string;
  r: CharmRow[];
};

export const LABOR = laborJson as CharmLeaf[];

export const SECTIONS = [
  "Heating and Air Conditioning",
  "Engine, Cooling and Exhaust",
  "Transmission and Drivetrain",
  "Steering and Suspension",
  "Brakes and Traction Control",
  "Powertrain Management",
  "Starting and Charging",
  "Body and Frame",
  "Lighting and Horns",
  "Sensors and Switches",
  "Maintenance",
  "Windows and Glass",
] as const;

export const SECTION_SHORT: Record<string, string> = {
  "Heating and Air Conditioning": "HVAC",
  "Engine, Cooling and Exhaust": "Engine",
  "Transmission and Drivetrain": "Drivetrain",
  "Steering and Suspension": "Steer / susp",
  "Brakes and Traction Control": "Brakes",
  "Powertrain Management": "PCM",
  "Starting and Charging": "Charge",
  "Body and Frame": "Body",
  "Lighting and Horns": "Lights",
  "Sensors and Switches": "Sensors",
  Maintenance: "Maint",
  "Windows and Glass": "Glass",
};

export function leafById(id: string | null): CharmLeaf | undefined {
  if (!id) return undefined;
  return LABOR.find((l) => l.id === id);
}

export function searchLabor(query: string, section: string | null): CharmLeaf[] {
  const q = query.trim().toLowerCase();
  let pool = section ? LABOR.filter((l) => l.s === section) : LABOR;
  if (q) {
    pool = pool.filter(
      (l) =>
        l.t.toLowerCase().includes(q) ||
        l.s.toLowerCase().includes(q) ||
        l.r.some((r) => (r.i ?? r.o).toLowerCase().includes(q)),
    );
  }
  return pool.slice(0, 8);
}
