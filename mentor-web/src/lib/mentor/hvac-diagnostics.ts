import type { MentorJob } from "./book";

/** CHARM Heating and Air Conditioning — Testing and Inspection. 4WD VIN K only. */

export const HVAC_DIAGS: MentorJob[] = [
  {
    id: "hvac-inspect",
    title: "Inspect / verify",
    system: "HVAC · Testing and Inspection · leaf 1759",
    pages: [
      { id: "1759", kind: "Initial Inspection and Diagnostic Overview" },
      { id: "8383", kind: "Labor Times" },
      { id: "1751", kind: "Technician Safety Information" },
    ],
    labor: [
      {
        op: "Diagnose/Test",
        item: "System, Diagnosis",
        std: "1.0",
        warr: "0.0",
        skill: "B",
        notes: "Includes: Partial Charge, Pressure and Leak Diagnosis.",
      },
    ],
    cautions: [
      "To avoid accidental deployment and possible injury, the air bag system backup power supply must be depleted before repairing any climate control components. Disconnect the battery ground cable and wait one minute.",
      "Always wear safety goggles when repairing an air conditioning system.",
      "Avoid contact with liquid refrigerant R-134a. R-134a vaporizes at approximately -25°C (-13°F) under atmospheric pressure and it will freeze skin tissue.",
      "Never allow refrigerant R-134a gas to escape in quantity in an occupied space. R-134a is non-toxic, but it will displace the oxygen needed to support life.",
      "An A/C refrigerant analyzer must be used before the recovery of any vehicle's A/C refrigerant.",
    ],
    steps: [
      {
        n: "1",
        text: "Verify the customer's concern by operating the climate control system to duplicate the condition.",
      },
      {
        n: "2",
        text: "Inspect to determine if one of the mechanical or electrical concerns apply. Visual Inspection Chart.",
      },
      {
        n: "3",
        text: "If the inspection reveals obvious concern(s) that can be readily identified, repair as required.",
      },
      {
        n: "4",
        text: "If the concern remains after the inspection, connect the scan tool to the data link connector (DLC) located beneath the instrument panel and select the vehicle to be tested from the scan tool menu.",
        note: "If the vehicle selection cannot be entered: check that the program card is correctly installed; check the connections to the vehicle; check the ignition switch position. If the scan tool still does not allow the vehicle selection to be entered, refer to the scan tool manual.",
      },
      {
        n: "5",
        text: "Perform the DATA LINK DIAGNOSTIC TEST using the scan tool.",
        note: "If CKT 914 and CKT 915 = ALL MODULE NO RESPONSE/NOT EQUIPPED, go to Communication System Diagnostics in Information Bus. If the PCM is not listed for a communication concern, turn the function selector switch to off and execute self-test diagnostics for the PCM.",
      },
      {
        n: "6",
        text: "If any PCM DTCs are retrieved, and are related to the concern, go to the Powertrain Control Module Diagnostic Trouble Code (DTC) Index. CHARM HVAC DTC index on this truck is P1460–P1469 (leaf 1760, factory chart).",
      },
      {
        n: "7",
        text: "If no DTCs related to the concern are retrieved, GO to Symptom Chart to continue diagnostics (leaf 1761).",
      },
    ],
    hotspot: [0.0, 0.85, 1.2],
  },
  {
    id: "hvac-check",
    title: "Retail system check",
    system: "HVAC · Component Tests · leaf 1775",
    pages: [{ id: "1775", kind: "A/C System Check - Retail Procedure" }],
    labor: [
      {
        op: "Diagnose/Test",
        item: "System, Diagnosis",
        std: "1.0",
        warr: "0.0",
        skill: "B",
        notes: "Includes: Partial Charge, Pressure and Leak Diagnosis.",
      },
    ],
    cautions: [
      "This Retail Procedure is not eligible for claims on Ford paid repairs (warranty and ESP).",
      "The engine should be run at idle for 10 minutes with the air conditioning on and set to MAX A/C (if equipped) or FULL COOL and RECIRC (if equipped) before carrying out this retail procedure.",
      "Read and follow all of the Warnings, Cautions and Notes.",
    ],
    steps: [
      {
        n: "1",
        text: "Visual inspection. Open the hood and inspect coolant level; heater hoses; radiator and condenser (fins, mounting); accessory drive belt(s) and cooling fan(s); refrigerant lines and connections; compressor; suction accumulator or receiver/drier; wiring and connectors.",
      },
      {
        n: "2",
        text: "A/C refrigerant analysis. Carry out air conditioning refrigerant analysis. If the refrigerant fails the analysis, discontinue diagnosis and make recommendations for repairs. If it passes, carry out the air conditioning system check.",
      },
      {
        n: "3",
        text: "Connect an R-134a manifold gauge set or refrigerant service center with gauges. Vehicle in park, parking brake set, thermometer in center panel vent, A/C on MAX A/C or FULL COOL and RECIRC. Start the engine.",
      },
      {
        n: "4",
        text: "Record refrigerant system pressures while running the engine at 1,500 rpm and allow the engine to return to idle.",
      },
      {
        n: "5",
        text: "Operate the blower motor in all control positions and check for correct blower speed changes.",
      },
      {
        n: "6",
        text: "With the blower motor on MED HI, operate the air discharge mode selector in all positions and check for correct airflow in each position.",
      },
      {
        n: "7",
        text: "Operate the temperature blend selector in all positions and check for correct change in discharge temperature. Check discharge temperature in the coolest position with A/C on to determine if it is acceptable for the current ambient air temperature. Carry out the ATC self-test (if applicable).",
        note: "If the refrigerant system pressures were low, carry out the refrigerant system leak test.",
      },
      {
        n: "8",
        text: "Refrigerant system leak test. Use either an ultraviolet (UV) or an electronic leak detector to check for leaks at all refrigerant lines, connections, and components.",
        note: "After all tests have been completed, report all findings and recommended repairs to your service advisor before carrying out further diagnostic procedures.",
      },
    ],
    hotspot: [0.0, 0.85, 1.35],
  },
  {
    id: "hvac-pressures",
    title: "Refrigerant pressures",
    system: "HVAC · Refrigerant System Tests · leaf 1779",
    pages: [{ id: "1779", kind: "Refrigerant System Tests" }],
    labor: [
      {
        op: "Diagnose/Test",
        item: "System, Diagnosis",
        std: "1.0",
        warr: "0.0",
        skill: "B",
        notes: "Includes: Partial Charge, Pressure and Leak Diagnosis.",
      },
    ],
    cautions: [
      "The system performance can be evaluated and diagnosed by analysis of the compressor suction and discharge pressures.",
      "If the ambient temperature is 38°C (100°F) or lower, follow procedure 1. If the ambient temperature is over 38°C (100°F), follow procedure 2.",
      "If the A/C compressor cycles at any time during this test, refer to the diagnostic table (factory chart on leaf 1779).",
    ],
    steps: [
      { n: "1", text: "Drive the vehicle or run the engine until it reaches normal operating temperature." },
      {
        n: "2",
        text: "Connect a manifold gauge set or refrigerant service center with high-pressure and low-pressure gauges to the refrigerant system.",
      },
      {
        n: "3",
        text: "Set the climate controls. Manual: A/C-PANEL, full COOL, FRESH, HI blower (procedure 1) or MED LO (procedure 2), A/C ON. ATC: 60°F (15°C), blower high (procedure 1) or MED LO (procedure 2), FRESH, A/C ON. Auxiliary (if equipped): full COOL, PANEL, matching blower.",
      },
      {
        n: "4",
        text: "Open all vehicle windows and leave the hood open for the test. Open the rear hatch and/or rear doors (if equipped).",
      },
      {
        n: "5",
        text: "Confirm the compressor clutch is engaged and the engine cooling fan(s) are operating. Allow the vehicle to idle until suction and discharge pressures are stable or fluctuate in a repeating range.",
      },
      { n: "6", text: "Record the ambient (shop) temperature." },
      {
        n: "7",
        text: "Record the discharge pressure. If fluctuating, record the average value. Compare to the Normal Refrigerant Discharge Pressures chart on leaf 1779.",
      },
      {
        n: "8",
        text: "Record the suction pressure. If fluctuating, record the average value. Compare to the Normal Refrigerant Suction Pressures chart on leaf 1779.",
      },
      {
        n: "9",
        text: "Proceed to the diagnostic table on leaf 1779 if operating pressures are outside normal limits.",
      },
    ],
    hotspot: [0.42, 0.58, 1.52],
  },
  {
    id: "hvac-elec-leak",
    title: "Electronic leak test",
    system: "HVAC · Component Tests · leaf 1776",
    pages: [{ id: "1776", kind: "Electronic Leak Detection" }],
    labor: [
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
      "Good ventilation is necessary in the area where electronic A/C leak testing is to be carried out. If the surrounding air is contaminated with refrigerant gas, the leak detector will indicate this gas all the time.",
      "Odors from other chemicals such as antifreeze, diesel fuel, disc brake cleaner, or other cleaning solvents can cause the same problem. Ventilate with a fan before testing; turn the fan off during actual testing.",
      "The system pressure should be between 413-551 kPa (60-80 psi) at 24°C (75°F) with the engine off.",
    ],
    steps: [
      {
        n: "1",
        text: "Leak test the refrigerant system using the Refrigerant Leak Detector. Follow the instructions included with the leak detector for handling and operation techniques. Special tool: H10PM Refrigerant Leak Detector With Battery.",
      },
      {
        n: "2",
        text: "If a leak is found, recover the refrigerant. Repair the system. Test the system for normal operation.",
      },
    ],
    hotspot: [0.42, 0.58, 1.52],
  },
  {
    id: "hvac-core-leak",
    title: "Evaporator / condenser leak",
    system: "HVAC · On-vehicle leak test · leaf 1774",
    pages: [{ id: "1774", kind: "A/C Evaporator/Condenser Core - On-Vehicle Leak Test" }],
    labor: [
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
      "DO NOT leak test an A/C evaporator core with the suction accumulator/drier attached to the core tubes.",
      "The automatic shut-off valves on some gauge set hoses do not open when connected to the test fittings. The test is not valid if the shut-off valve does not open.",
      "The 45 minute evacuation is necessary to remove any refrigerant from oil left in the core. If the refrigerant is not completely removed from the oil, outgassing will degrade the vacuum and appear as a refrigerant leak.",
    ],
    steps: [
      { n: "1", text: "Discharge and recover the refrigerant." },
      {
        n: "2",
        text: "Disconnect the suspect A/C evaporator core or A/C condenser core from the A/C system.",
      },
      { n: "3", text: "Clean the spring lock couplings." },
      {
        n: "4",
        text: "Connect the appropriate test fittings from the R-12/R-134a Air Conditioning Test Fitting Set to the evaporator or condenser tube connections.",
      },
      {
        n: "5",
        text: "Connect the red and blue hoses from the R-134a Manifold Gauge Set to the test fittings. Connect the yellow hose to a known good vacuum pump.",
      },
      {
        n: "6",
        text: "Open both gauge set valves and start the vacuum pump. Allow the vacuum pump to operate for a minimum of 45 minutes after the low pressure gauge indicates 101 kPa (30 in-Hg).",
      },
      {
        n: "7",
        text: "If the low pressure gauge will not drop to 101 kPa (30 in-Hg), close the valves and observe. If pressure rises rapidly to zero, a large leak is indicated. Recheck fittings before installing a new core.",
      },
      {
        n: "8",
        text: "After evacuating 45 minutes, close the valves and stop the pump. The gauge should remain at 101 kPa (30 in-Hg). If it rises 34 or more kPa (10 or more in-Hg) in 10 minutes, a leak is indicated.",
      },
      {
        n: "9",
        text: "If the A/C evaporator core or A/C condenser core does leak, as verified by the above procedure, install a new A/C evaporator core or A/C condenser core.",
      },
    ],
    hotspot: [0.0, 0.72, 2.28],
  },
  {
    id: "hvac-heater-test",
    title: "Heater core test",
    system: "HVAC · Component Tests · leaf 1778",
    pages: [{ id: "1778", kind: "Heater Core" }],
    labor: [
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
      "Carbon monoxide is colorless, odorless and dangerous. If it is necessary to operate the engine with the vehicle in a closed area such as a garage, always use an exhaust collector to vent the exhaust gases outside the closed area.",
      "Testing of returned heater cores reveals that a large percentage of heater cores are good and did not require installation of a new heater core.",
      "The heater core inlet hose will become too hot to handle if the system is working correctly.",
      "Spring-type clamps are installed as original equipment. Installation and overtightening of non-specification clamps can cause leakage at the heater water hose connection and damage the heater core.",
    ],
    steps: [
      {
        n: "1",
        text: "Inspect for coolant leakage at the heater water hose to heater core attachments. A hose leak can follow the tube and appear as a core leak. Check heater water hose clamps.",
      },
      {
        n: "2",
        text: "Plugged-core check: coolant at correct level, start engine, turn on heater. When at operating temperature, feel the heater core outlet hose. If it is not hot: heater control valve, air pocket, plugged core, or thermostat.",
      },
      {
        n: "3",
        text: "Pressure test: drain cooling system, disconnect heater water hoses, install ~101 mm (4 in) hose on each core tube, fill with water, install Plug BT-7422-B and adapter BT-7422-A, pump 241 kPa (35 psi), observe a minimum of three minutes.",
      },
      {
        n: "4",
        text: "If pressure drops, check hose connections. If hoses do not leak, remove the heater core and carry out the bench test: 241 kPa (35 psi) submerged in water. If a leak is observed, install a new heater core.",
      },
    ],
    hotspot: [0.0, 0.78, 1.05],
  },
  {
    id: "hvac-pt-a",
    title: "Pinpoint A · airflow",
    system: "PINPOINT TEST A · leaf 1762",
    pages: [{ id: "1762", kind: "Pinpoint Test A" }, { id: "1761", kind: "Symptom Chart" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (A1–A18). Cells are not OCR'd on this card — follow leaf 1762.",
    ],
    steps: [
      { n: "A", text: "INCORRECT/ERRATIC DIRECTION OF AIRFLOW FROM OUTLET(S)." },
      { n: "1", text: "Chart groups on leaf 1762: A1–A2, A3–A4, A4–A5, A5–A7, A7–A8, A8–A10, A10–A12, A12–A14, A14–A17, A17–A18." },
    ],
    hotspot: [0.0, 0.95, 0.9],
  },
  {
    id: "hvac-pt-b",
    title: "Pinpoint B · no heat",
    system: "PINPOINT TEST B · leaf 1763",
    pages: [{ id: "1763", kind: "Pinpoint Test B" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (B1–B10). Cells are not OCR'd on this card — follow leaf 1763.",
    ],
    steps: [
      { n: "B", text: "INSUFFICIENT, ERRATIC, OR NO HEAT." },
      { n: "1", text: "Chart groups on leaf 1763: B1, B1–B3, B3–B5, B5–B7, B7–B8, B8–B9, B10." },
    ],
    hotspot: [0.0, 0.78, 1.05],
  },
  {
    id: "hvac-pt-c",
    title: "Pinpoint C · A/C inop",
    system: "PINPOINT TEST C · leaf 1764",
    pages: [{ id: "1764", kind: "Pinpoint Test C" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (C1–C21). Cells are not OCR'd on this card — follow leaf 1764.",
    ],
    steps: [
      { n: "C", text: "THE AIR CONDITIONING (A/C) IS INOPERATIVE/DOES NOT OPERATE CORRECTLY." },
      { n: "1", text: "Chart groups on leaf 1764: C1–C3, C4–C7, C8–C10, C11–C13, C14–C16, C16–C18, C18–C20, C21." },
    ],
    hotspot: [0.42, 0.58, 1.52],
  },
  {
    id: "hvac-pt-d",
    title: "Pinpoint D · always on",
    system: "PINPOINT TEST D · leaf 1765",
    pages: [{ id: "1765", kind: "Pinpoint Test D" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (D1–D6). Cells are not OCR'd on this card — follow leaf 1765.",
    ],
    steps: [
      { n: "D", text: "THE AIR CONDITIONING (A/C) IS ALWAYS ON." },
      { n: "1", text: "Chart groups on leaf 1765: D1–D3, D3–D5, D5–D6." },
    ],
    hotspot: [0.42, 0.58, 1.52],
  },
  {
    id: "hvac-pt-f",
    title: "Pinpoint F · blower inop",
    system: "PINPOINT TEST F · leaf 1767",
    pages: [{ id: "1767", kind: "Pinpoint Test F" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (F1–F9). Cells are not OCR'd on this card — follow leaf 1767.",
    ],
    steps: [
      { n: "F", text: "THE BLOWER MOTOR IS INOPERATIVE." },
      { n: "1", text: "Chart groups on leaf 1767: F1–F3, F3–F4, F5–F7, F8–F9." },
    ],
    hotspot: [0.0, 0.95, 0.85],
  },
  {
    id: "hvac-pt-e",
    title: "Pinpoint E · temp",
    system: "PINPOINT TEST E · leaf 1766",
    pages: [{ id: "1766", kind: "Pinpoint Test E" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (E1–E8). Cells are not OCR'd on this card — follow leaf 1766.",
    ],
    steps: [
      { n: "E", text: "NO OPERATION IN ALL THE TEMPERATURE SETTINGS." },
      { n: "1", text: "Chart groups on leaf 1766: E1, E1–E3, E4–E6, E6–E8." },
    ],
    hotspot: [0.0, 0.95, 0.9],
  },
  {
    id: "hvac-pt-g",
    title: "Pinpoint G · blower odd",
    system: "PINPOINT TEST G · leaf 1768",
    pages: [{ id: "1768", kind: "Pinpoint Test G" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (G1–G5). Cells are not OCR'd on this card — follow leaf 1768.",
    ],
    steps: [
      { n: "G", text: "THE BLOWER MOTOR DOES NOT OPERATE CORRECTLY." },
      { n: "1", text: "Chart groups on leaf 1768: G1, G1–G2, G3–G4, G4–G5." },
    ],
    hotspot: [0.0, 0.95, 0.85],
  },
  {
    id: "hvac-pt-h",
    title: "Pinpoint H · stuck high",
    system: "PINPOINT TEST H · leaf 1769",
    pages: [{ id: "1769", kind: "Pinpoint Test H" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (H1–H4). Cells are not OCR'd on this card — follow leaf 1769.",
    ],
    steps: [
      { n: "H", text: "THE BLOWER MOTOR OPERATES CONTINUOUSLY IN HIGH SPEED." },
      { n: "1", text: "Chart groups on leaf 1769: H1, H1–H3, H4." },
    ],
    hotspot: [0.0, 0.95, 0.85],
  },
  {
    id: "hvac-pt-i",
    title: "Pinpoint I · no high",
    system: "PINPOINT TEST I · leaf 1770",
    pages: [{ id: "1770", kind: "Pinpoint Test I" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (I1–I4). Cells are not OCR'd on this card — follow leaf 1770.",
    ],
    steps: [
      { n: "I", text: "NO OPERATION IN HIGH BLOWER SETTING." },
      { n: "1", text: "Chart groups on leaf 1770: I1–I2, I2–I4." },
    ],
    hotspot: [0.0, 0.95, 0.85],
  },
  {
    id: "hvac-pt-j",
    title: "Pinpoint J · no low",
    system: "PINPOINT TEST J · leaf 1771",
    pages: [{ id: "1771", kind: "Pinpoint Test J" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (J1–J2). Cells are not OCR'd on this card — follow leaf 1771.",
    ],
    steps: [
      { n: "J", text: "NO OPERATION IN LOWER SPEED." },
      { n: "1", text: "Chart groups on leaf 1771: J1–J2." },
    ],
    hotspot: [0.0, 0.95, 0.85],
  },
  {
    id: "hvac-pt-k",
    title: "Pinpoint K · console",
    system: "PINPOINT TEST K · leaf 1772",
    pages: [{ id: "1772", kind: "Pinpoint Test K" }],
    labor: [
      { op: "Diagnose/Test", item: "Pinpoint, Test", std: "0.5", warr: "0.3", skill: "B", notes: "" },
    ],
    cautions: [
      "CHARM prints this pinpoint as factory charts (K1–K9). Cells are not OCR'd on this card — follow leaf 1772.",
    ],
    steps: [
      { n: "K", text: "THE CONSOLE BLOWER MOTOR IS INOPERATIVE." },
      { n: "1", text: "Chart groups on leaf 1772: K1, K1–K2, K3–K5, K5–K6, K7–K9." },
    ],
    hotspot: [0.0, 0.7, 0.2],
  },
];

export const PINPOINT_INDEX = [
  { id: "hvac-pt-a", letter: "A", title: "Airflow direction", page: "1762" },
  { id: "hvac-pt-b", letter: "B", title: "No heat", page: "1763" },
  { id: "hvac-pt-c", letter: "C", title: "A/C inop", page: "1764" },
  { id: "hvac-pt-d", letter: "D", title: "A/C always on", page: "1765" },
  { id: "hvac-pt-e", letter: "E", title: "No temp control", page: "1766" },
  { id: "hvac-pt-f", letter: "F", title: "Blower inop", page: "1767" },
  { id: "hvac-pt-g", letter: "G", title: "Blower incorrect", page: "1768" },
  { id: "hvac-pt-h", letter: "H", title: "Blower stuck high", page: "1769" },
  { id: "hvac-pt-i", letter: "I", title: "No high blower", page: "1770" },
  { id: "hvac-pt-j", letter: "J", title: "No low blower", page: "1771" },
  { id: "hvac-pt-k", letter: "K", title: "Console blower", page: "1772" },
] as const;
