import { HVAC_DIAGS } from "./hvac-diagnostics";
import { HVAC_PARTS } from "./hvac-parts";
import { STUDIO } from "./rights";

export const CHARM = {
  tree: "2004 Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel",
  leaves: 9562,
  laborLeaves: 598,
  disclaimer: `${STUDIO.mark} CHARM is a third-party copy of workshop pages. Confirm the 4WD VIN K tree. VECI and the under-hood label override a table if they differ. Recover, evacuate, and charge stay professional-equipment work. Ford marks remain Ford’s.`,
} as const;

export type LaborRow = {
  op: string;
  item?: string;
  std: string;
  warr: string;
  skill: string;
  notes: string;
};

export type CharmStep = {
  n: string;
  text: string;
  note?: string;
};

export type MentorJob = {
  id: string;
  title: string;
  system: string;
  pages: { id: string; kind: string }[];
  labor: LaborRow[];
  cautions: string[];
  steps?: CharmStep[];
  install?: string[];
  hotspot: [number, number, number];
};

export const JOBS: MentorJob[] = [
  {
    id: "ac-compressor",
    title: "A/C compressor",
    system: "FS-10 · Heating and Air Conditioning",
    pages: [
      { id: "8361", kind: "Labor Times" },
      { id: "1705", kind: "Service and Repair" },
      { id: "1701", kind: "Service Precautions" },
      { id: "1702", kind: "Description and Operation" },
    ],
    labor: [
      {
        op: "Replace",
        item: "Complete Assembly, With Transfer Of Parts",
        std: "1.1",
        warr: "0.8",
        skill: "B",
        notes:
          "Does Not Include: Refrigerant Recovery Or Evacuate & Recharge AC System.",
      },
    ],
    cautions: [
      "If installing a new air conditioning compressor due to an internal failure of the old unit, you must carry out the following procedures to remove contamination from the air conditioning system.",
      "If A/C flushing equipment is available, carry out flushing of the air conditioning system prior to installing a new air conditioning compressor.",
      "If A/C flushing equipment is not available, carry out filtering of the air conditioning system after a new air conditioning compressor has been installed.",
      "Install a new evaporator core orifice as directed by the A/C flushing or filtering procedure.",
      "Install a new suction accumulator as directed by the A/C flushing or filtering procedure.",
      "Installation of a new suction accumulator is not required when repairing the air conditioning system except when there is physical evidence of system contamination from a failed A/C compressor or damage to the suction accumulator.",
    ],
    steps: [
      { n: "1", text: "Disconnect the battery ground cable." },
      {
        n: "2",
        text: "If flushing of the refrigerant system has not been performed, recover the refrigerant.",
      },
      { n: "3", text: "Remove the air cleaner outlet pipe." },
      { n: "4", text: "Remove the drive belt from the A/C compressor pulley." },
      { n: "5", text: "Remove the bolts. Detach the power steering fluid reservoir." },
      { n: "6", text: "Remove the bolts and the bracket." },
      { n: "7", text: "Disconnect the A/C clutch field coil electrical connector." },
      {
        n: "8",
        text: "Remove the bolt. Disconnect the A/C manifold and tube. Remove and discard the O-ring seals.",
        note: "During installation, install new O-ring seals lubricated with PAG Refrigerant Compressor Oil (R-134a Systems) F7AZ-19589-DA (Motorcraft YN-12-C) or equivalent meeting Ford specification WSH-M1C231-B.",
      },
      {
        n: "9",
        text: "Remove the retaining pins. Position the inner fender well access panel out of the way.",
      },
      {
        n: "10",
        text: "Remove the lower A/C compressor bolt and stud.",
        note: "1 Remove the bolt. 2 Remove the nut and detach the bracket from the stud. 3 Remove the stud.",
      },
      {
        n: "11",
        text: "Remove the two nuts, two studs, and the A/C compressor.",
        note: "The lower two nuts and two studs are accessible through the inner fender well.",
      },
    ],
    install: [
      "To install, reverse the removal procedure.",
      "If a new A/C compressor is to be installed, the A/C clutch field coil and the A/C clutch must be transferred from the old unit.",
      "If filtering of the refrigerant system is not to be performed, service the replacement A/C compressor with the correct amount of PAG Refrigerant Compressor Oil (R-134a Systems) F7AZ-19589-DA (Motorcraft YN-12-C) or equivalent meeting Ford specification WSH-M1C231-B.",
      "If filtering of the refrigerant system is not to be performed, evacuate, leak test, and charge the A/C system.",
    ],
    hotspot: [0.42, 0.58, 1.52],
  },
  {
    id: "ac-clutch",
    title: "Compressor clutch",
    system: "FS-10 clutch · CHARM labor only",
    pages: [{ id: "9331", kind: "Labor Times" }],
    labor: [
      {
        op: "Replace",
        std: "1.8",
        warr: "1.2",
        skill: "B",
        notes:
          "Does Not Include: Refrigerant Recovery Or Evacuate & Recharge AC System.",
      },
    ],
    cautions: [
      "Internal A/C compressor components are not serviced separately. The FS-10 A/C compressor is serviced only as an assembly. The A/C clutch, A/C clutch pulley, A/C clutch field coil and the shaft seal are serviceable.",
    ],
    hotspot: [0.42, 0.58, 1.52],
  },
  {
    id: "ac-seal",
    title: "Compressor shaft seal",
    system: "FS-10 front seal · CHARM labor only",
    pages: [{ id: "9336", kind: "Labor Times" }, { id: "1703", kind: "External Leak Test" }],
    labor: [
      {
        op: "Replace",
        std: "1.8",
        warr: "1.2",
        skill: "B",
        notes:
          "Does Not Include: Refrigerant Recovery Or Evacuate & Recharge AC System.",
      },
    ],
    cautions: [
      "A one-piece lip-type seal (replaceable from the front of the A/C compressor) is used to seal it at the shaft opening in the assembly.",
    ],
    steps: [
      {
        n: "1",
        text: "Install the A/C pressure test adapter on the rear head of the A/C compressor using the existing manifold retaining bolt.",
      },
      {
        n: "2",
        text: "Connect the high and low pressure lines of a manifold gauge set or a refrigerant recovery/recycling station such as R-134a A/C Service Center to the corresponding fittings on the A/C pressure test adapter.",
      },
      {
        n: "3",
        text: "Attach the center hose of the manifold gauge set to a refrigerant container standing in an upright position.",
      },
      {
        n: "4",
        text: "Hand-rotate the compressor shaft 10 complete revolutions to distribute the oil inside the A/C compressor.",
      },
      {
        n: "5",
        text: "Open the low pressure gauge valve, the high pressure gauge valve and the valve on the refrigerant container to allow the refrigerant vapor to flow into the A/C compressor.",
      },
      {
        n: "6",
        text: "Using the Refrigerant Leak Detector, check for leaks at the compressor shaft seal and the compressor center seal.",
      },
      {
        n: "7",
        text: "If a shaft seal leak is found, install a new shaft seal. If an external leak is found at the center joint of the A/C compressor, install a new A/C compressor.",
      },
      {
        n: "8",
        text: "When the leak test is complete, recover the refrigerant from the compressor.",
      },
    ],
    hotspot: [0.42, 0.58, 1.52],
  },
  {
    id: "ac-condenser",
    title: "Condenser",
    system: "HVAC · CHARM labor only",
    pages: [{ id: "9338", kind: "Labor Times" }],
    labor: [
      {
        op: "Replace",
        std: "1.1",
        warr: "0.8",
        skill: "B",
        notes:
          "Does Not Include: Refrigerant Recovery Or Evacuate & Recharge AC System.",
      },
    ],
    cautions: [],
    hotspot: [0.0, 0.72, 2.28],
  },
  {
    id: "ac-evac",
    title: "Recover / evacuate / charge",
    system: "Heating and Air Conditioning · add to component R&R",
    pages: [{ id: "8383", kind: "Labor Times" }],
    labor: [
      {
        op: "Flush",
        item: "Flush (Complete)",
        std: "0.3",
        warr: "0.0",
        skill: "B",
        notes:
          "To Be Used In Conjunction With Component Replacement Which Could Contaminate System. Does Not Include: Evacuate & Recharge System. R12 Refrigerant MUST NOT Be Used.",
      },
      {
        op: "Evacuate/Recharge",
        item: "Evacuate & Recharge",
        std: "1.4",
        warr: "0.0",
        skill: "B",
        notes:
          "Many Vehicles Are Now Using R134 Refrigerant In The AC System. Extra Care Must Be Observed When Servicing This Type Of System. R12 Refrigerant MUST NOT Be Used.",
      },
      {
        op: "Evacuate/Recharge",
        item: "Partial Refrigerant Charge",
        std: "0.6",
        warr: "0.0",
        skill: "B",
        notes: "Add For Refrigerant Cost.",
      },
      {
        op: "Recover",
        item: "Refrigerant Recover",
        std: "0.4",
        warr: "0.0",
        skill: "B",
        notes: "Does Not Include: Evacuate & Recharge System.",
      },
      {
        op: "Diagnose/Test",
        item: "System, Diagnosis",
        std: "1.0",
        warr: "0.0",
        skill: "B",
        notes: "Includes: Partial Charge, Pressure and Leak Diagnosis.",
      },
      {
        op: "Diagnose/Test",
        item: "Pinpoint, Test",
        std: "0.5",
        warr: "0.3",
        skill: "B",
        notes: "",
      },
    ],
    cautions: [
      "R12 Refrigerant MUST NOT Be Used.",
      "With Any Operation Requiring A Refrigerant Line Disconnect, Add AC Service, I.E. Evacuate, Recharge And Test For Leaks. Add For Refrigerant Cost.",
    ],
    hotspot: [0.42, 0.58, 1.52],
  },
  {
    id: "ac-evaporator",
    title: "Evaporator core",
    system: "HVAC · CHARM labor only",
    pages: [{ id: "9343", kind: "Labor Times" }],
    labor: [
      {
        op: "Replace",
        item: "Auxiliary Air Conditioner",
        std: "2.7",
        warr: "1.8",
        skill: "B",
        notes:
          "Does Not Include: Refrigerant Recovery Or Evacuate & Recharge AC System.",
      },
      {
        op: "Replace",
        item: "Main Air Conditioner",
        std: "2.1",
        warr: "1.0",
        skill: "B",
        notes:
          "Does Not Include: Refrigerant Recovery Or Evacuate & Recharge AC System.",
      },
    ],
    cautions: [],
    hotspot: [0.0, 0.78, 1.15],
  },
  {
    id: "heater-core",
    title: "Heater core",
    system: "HVAC · CHARM labor only",
    pages: [{ id: "9347", kind: "Labor Times" }],
    labor: [
      {
        op: "Replace",
        item: "Main Heater",
        std: "8.8",
        warr: "3.6",
        skill: "B",
        notes: "Includes: Evacuate & Recharge",
      },
      {
        op: "Replace",
        item: "Auxiliary Heater",
        std: "2.7",
        warr: "0.0",
        skill: "B",
        notes: "",
      },
    ],
    cautions: [],
    hotspot: [0.0, 0.78, 1.05],
  },
];

export function jobById(id: string | null): MentorJob | undefined {
  return CARDS.find((j) => j.id === id);
}

export const CARDS = [...HVAC_DIAGS, ...JOBS, ...HVAC_PARTS];
