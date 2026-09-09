export type RackStep = {
  id: string;
  title: string;
  detail: string;
  command?: string;
};

export const VEHICLE = {
  year: "2004",
  model: "Explorer Sport Trac XLT",
  engine: "4.0L SOHC V6 VIN K Flex Fuel",
  drive: "4WD",
  vinNote: "VIN K engine family — verify every torque and charge on the truck and under-hood label.",
};

export const RACK_STEPS: RackStep[] = [
  {
    id: "tailscale",
    title: "Tailscale on CB1",
    detail: "Bring penguin-1 onto the mesh. Target address 100.78.197.121.",
    command: "curl -fsSL https://tailscale.com/install.sh | sh && sudo tailscale up",
  },
  {
    id: "node",
    title: "Node 22",
    detail: "OmniRoute’s npm path needs a current Node. Confirm with node -v after install.",
    command: "curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash - && sudo apt-get install -y nodejs",
  },
  {
    id: "omniroute",
    title: "OmniRoute gateway",
    detail:
      "Official dashboard and API share port 20128. Do not expect /opt/omniroute or port 8080 unless you built a custom unit.",
    command: "sudo npm install -g omniroute && omniroute",
  },
  {
    id: "omniroute-check",
    title: "OmniRoute health",
    detail: "Doctor plus models list. Point tools at http://127.0.0.1:20128/v1 with the dashboard key.",
    command: "omniroute doctor && curl http://127.0.0.1:20128/v1/models",
  },
  {
    id: "opencode",
    title: "OpenCode agent",
    detail: "Official package is opencode-ai, not npm install -g opencode. Run it on Linux, never on the Pixel as the host.",
    command: "curl -fsSL https://opencode.ai/install | bash",
  },
  {
    id: "caddy",
    title: "Caddy (optional)",
    detail: "Only after OmniRoute is healthy. Reverse-proxy later if you expose the dashboard on Tailscale.",
  },
  {
    id: "cb2",
    title: "Rejoin CB2",
    detail: "penguin at 100.100.214.8 was last seen ~25 days ago. Power it, then tailscale up.",
  },
];

export const CREW = [
  {
    id: "cb1",
    name: "CB1",
    host: "penguin-1",
    ip: "100.78.197.121",
    role: "OmniRoute, OpenCode, Caddy, Tailscale exit",
    status: "reported online",
    tone: "ok" as const,
  },
  {
    id: "cb2",
    name: "CB2",
    host: "penguin",
    ip: "100.100.214.8",
    role: "Build server and staging",
    status: "reported offline",
    tone: "warn" as const,
  },
  {
    id: "hb1",
    name: "HB1",
    host: "Pixel 8",
    ip: "100.68.223.72",
    role: "Physical APK, Bluetooth, OBD later",
    status: "reported online",
    tone: "ok" as const,
  },
];

export const BAY_JOBS = [
  {
    id: "rear-click",
    title: "Rear click / clunk",
    system: "Rear suspension · axle · exhaust",
    stance:
      "Factory archive is 2WD V6 VIN K Flex Fuel. 4WD mismatch is documented. Trace leaf springs, shackles, shocks, U-joints, and exhaust hangers on the truck before buying parts.",
    steps: [
      "Confirm whether the noise is load-sensitive (bump) or RPM-sensitive (exhaust).",
      "Inspect rear shock mounts and bushings — 4WD shock workflow is in the GitHub repo.",
      "Check U-joints and pinion angle play with the truck safely supported.",
      "Do not convert single-to-dual exhaust from app guesses. Manual pages first.",
    ],
  },
  {
    id: "bank2",
    title: "Bank 2 lean / misfire",
    system: "4.0L SOHC intake · PCV · ignition",
    stance:
      "Workshop-sourced kit in the repo: PCV elbow, PCV valve, intake fasteners, plugs/coil. Torque from workshop rows, then VIN-verify.",
    steps: [
      "Read freeze-frame before clearing codes.",
      "PCV elbow on this engine is a common vacuum leak. Inspect, do not assume.",
      "Intake gasket / fastener kit only after smoke or spray confirmation.",
      "Plugs and coil last if the leak path is sealed.",
    ],
  },
  {
    id: "ac",
    title: "A/C workbench",
    system: "Climate · clutch · belt",
    stance:
      "Practice order, access, and safety here. Recovery, evacuate, leak test, and charge stay professional-equipment work. Charge and oil come from the under-hood label.",
    steps: [
      "Rehearse component order on the model before tools hit the truck.",
      "Treat live mic comparison as inspection aid, not a diagnosis.",
      "Do not skip recovery/evacuate/charge equipment because an app ranked a pattern.",
    ],
  },
];

export const READINESS = [
  { name: "Lounge / Manual / Diagnostics / Parts", state: "Source wired", level: "smoke" },
  { name: "Procedural 3D", state: "Gated · device pending", level: "hold" },
  { name: "A/C Workbench", state: "Locally functional", level: "ok" },
  { name: "Mentor voice", state: "Partial", level: "hold" },
  { name: "Part Store live order", state: "Not live", level: "off" },
  { name: "Body Shop", state: "Conceptual", level: "off" },
];

export const PARTS_RULES = [
  "Strongest recorded warranty is the default rank, not a hidden affiliate.",
  "You filter: value, reman, used, lowest delivered, local pickup, American-made.",
  "This app never places an order and never stores retailer logins or cards.",
  "Fitment is 2004 Sport Trac XLT 4WD 4.0L — still confirm on the VIN.",
];
