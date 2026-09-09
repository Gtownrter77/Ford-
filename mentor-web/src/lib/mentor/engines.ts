import type { MentorJob } from "./book";

/** 2004 Explorer Sport Trac — Owner Guide 04p27og2e is the authority. */
export const ENGINE = {
  thisTruck: "4.0L SOHC FFV V6 · VIN K · Cologne",
  cid: "245",
  fuel: "87 octane or E85 (max)",
  order: "1-4-2-5-3-6",
  ignition: "EDIS",
  compression: "9.7:1",
  oil: "5.0 qt · 5W-30 · FL-820S",
  coolant: "14.0 qt · Motorcraft Premium Gold",
  tank: "22.5 gal",
  trans: "5R55E only (manual dropped for 2004)",
  transFill4wd: "10.3 qt MERCON V",
  tcase: "1.3 qt MERCON",
  air: "FA-1744",
  fuelFilter: "FG-1036",
  battery: "BXT-65-650",
} as const;

export const ENGINE_JOBS: MentorJob[] = [
  {
    id: "engine-vin-k",
    title: "VIN K · 4.0 SOHC FFV",
    system: "Engine family · Owner Guide + CHARM 4WD VIN K",
    pages: [{ id: "7934", kind: "Labor Times" }],
    labor: [
      {
        op: "Diagnose/Test",
        item: "Oil Leak, Diagnosis",
        std: "1.0",
        warr: "0.5",
        skill: "C",
        notes: "",
      },
      {
        op: "Complete Assembly With Transfer Of Parts",
        item: "Auto Trans",
        std: "11.6",
        warr: "9.9",
        skill: "B",
        notes:
          "Includes: Transfer All Fuel & Electrical Units. Does Not Include: Transfer Of Optional Equipment.",
      },
      {
        op: "Long Block",
        item: "Auto Trans",
        std: "15.6",
        warr: "0.0",
        skill: "B",
        notes:
          "Includes: R&I Engine And Transfer All Necessary Components Not Supplied With Long Block.",
      },
      {
        op: "Short Block",
        item: "Auto Trans",
        std: "18.6",
        warr: "11.1",
        skill: "A",
        notes: "Includes: R&I Engine And Replacement Of Necessary Components.",
      },
      {
        op: "Overhaul/Rebuild",
        item: "Auto Trans",
        std: "28.6",
        warr: "0.0",
        skill: "A",
        notes:
          "Includes: Disassemble & Clean Engine, Ridge Ream & Hone Cylinders, Fit Pistons, Rings, Pins, Main & Rod Bearings, Grind Valves & Tune-Up",
      },
    ],
    cautions: [
      "This CHARM tree is 2004 Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel. Eighth VIN digit K is the FFV Cologne. VIN E is a gasoline 4.0 SOHC — PCM, harness, and fuel hardware are not a bolt-in.",
      "2004 Sport Trac has one engine: 4.0L SOHC FFV V6. No 4.6 V8 on this body. The 4.6 3V is 2007+ second-gen Sport Trac and 2004 Explorer optional — different truck.",
      "2004 dropped the manual. Transmission on this tree is 5R55E automatic.",
      "VECI and the under-hood label override a table if they differ.",
    ],
    steps: [
      { n: "1", text: `Engine: ${ENGINE.thisTruck}. ${ENGINE.cid} cid. Compression ${ENGINE.compression}.` },
      { n: "2", text: `Fuel: ${ENGINE.fuel}. Firing order ${ENGINE.order}. Ignition ${ENGINE.ignition}.` },
      { n: "3", text: `Oil ${ENGINE.oil}. Coolant ${ENGINE.coolant}. Tank ${ENGINE.tank}.` },
      { n: "4", text: `Filters: air ${ENGINE.air}, fuel ${ENGINE.fuelFilter}, battery ${ENGINE.battery}.` },
      { n: "5", text: `Trans ${ENGINE.trans}. 4WD fill ${ENGINE.transFill4wd}. Transfer case ${ENGINE.tcase}.` },
      {
        n: "6",
        text: "Do not quote Explorer 4.6 or 2007 Sport Trac V8 hours on this bay. Confirm eighth VIN digit before a long-block or PCM.",
      },
    ],
    hotspot: [-0.18, 0.86, 1.55],
  },
  {
    id: "engine-heads",
    title: "Head gaskets",
    system: "4.0 SOHC · CHARM leaf 8717",
    pages: [{ id: "8717", kind: "Labor Times" }],
    labor: [
      {
        op: "Replace",
        item: "Right Bank",
        std: "7.3",
        warr: "5.2",
        skill: "B",
        notes: "Includes: Remove Carbon And Make All Necessary Adjustments.",
      },
      {
        op: "Replace",
        item: "Left Bank",
        std: "6.6",
        warr: "4.9",
        skill: "B",
        notes: "Includes: Remove Carbon And Make All Necessary Adjustments.",
      },
      {
        op: "Replace",
        item: "Both Banks",
        std: "11.8",
        warr: "8.1",
        skill: "B",
        notes: "Includes: Remove Carbon And Make All Necessary Adjustments.",
      },
    ],
    cautions: [
      "Cologne 4.0 SOHC is a jackshaft / cassette timing engine. Right cassette add on leaf 8773 includes R&I engine.",
    ],
    hotspot: [-0.18, 0.9, 1.5],
  },
  {
    id: "engine-timing",
    title: "Timing chain",
    system: "Jackshaft / cassettes · CHARM leaf 8773",
    pages: [{ id: "8773", kind: "Labor Times" }],
    labor: [
      { op: "Replace", item: "Jackshaft Chain", std: "7.0", warr: "5.9", skill: "B", notes: "" },
      { op: "NOTE", item: "To R&R Left Cassette, Add", std: "0.2", warr: "0.0", skill: "B", notes: "" },
      { op: "NOTE", item: "To R&R Balance Shaft Chain, Add", std: "0.2", warr: "0.0", skill: "B", notes: "" },
      {
        op: "NOTE",
        item: "To R&R Right Cassette, Add",
        std: "11.3",
        warr: "0.0",
        skill: "B",
        notes: "Includes: R&I Engine.",
      },
    ],
    cautions: [
      "Balance-shaft chain tensioner (leaf 8699) is 15.5 and includes front cover and upper oil pan.",
    ],
    hotspot: [-0.05, 0.86, 1.72],
  },
  {
    id: "engine-water-pump",
    title: "Water pump",
    system: "Cooling · CHARM leaf 8793",
    pages: [{ id: "8793", kind: "Labor Times" }],
    labor: [{ op: "Replace", std: "1.8", warr: "1.2", skill: "B", notes: "" }],
    cautions: ["Coolant is Motorcraft Premium Gold, 14.0 qt. Do not mix silicates."],
    hotspot: [0.05, 0.82, 1.85],
  },
];

export const ENGINE_INDEX = [
  { id: "engine-vin-k", title: "VIN K family" },
  { id: "engine-heads", title: "Head gaskets" },
  { id: "engine-timing", title: "Timing chain" },
  { id: "engine-water-pump", title: "Water pump" },
] as const;
