# Sport Trac Destination Concept

**Status:** Design direction, not yet implemented as a new home screen.  
**Purpose:** Turn the app from a group of utility tabs into a connected personal world organized around the owner’s truck, the Mentor, and practice-first repair.

> The app should feel like arriving somewhere. The user does not merely “open a screen”; they enter a familiar place where the truck is always waiting, the Mentor has a workbench, and each room has a reason to exist.

## The four rooms

| Room | Emotional role | Primary purpose | Existing capabilities that belong there |
|---|---|---|---|
| **Lounge** | Personal, welcoming, owner-led | **The front door.** Onboard the owner, set up the truck and preferences, then return to saved progress, upcoming work, and favorite builds. | Vehicle profile, maintenance history, saved Mentor checklist progress, skill settings, voice settings, privacy choices, and parts-ranking preferences. |
| **Shop** | Capable, patient, hands-on | Practice and complete a repair with the to-scale truck at center and Mentor beside it. | `VIEW_3D`, `REPAIR_MANUAL`, Mentor Mode, torque data, procedural hardware, repair checklist persistence. |
| **Diagnostics Bay** | Methodical, evidence-led | Identify a problem before buying a part. This is physically inside the Shop rather than a disconnected tab. | `DIAGNOSTICS`, A/C Workbench, FORScan-style paths, sound comparison, symptom flows. |
| **Part Store** | Organized, choice-respecting planning library | Start with abundant common parts, build a repair list, compare choices, run in-app readiness review, and leave for external checkout only if desired. | `PARTS_CART`, readiness packages, price-watch controls, saved quote evidence, retailer links, customer ranking choices. |
| **The Body Shop** | Imaginative, aspirational, creative | Visualize the Sport Trac’s future appearance and configuration before spending money. | New work: body-kit generation, paint/wheel/stance exploration, saved build cards, component overlays, external part-list handoff. |

## Arrival sequence

The first experience should be the **Lounge**, not five small app tabs. It is the front door: a new owner is welcomed, sets up the truck and personal preferences, and chooses how the Mentor and Part Store should work for them. After onboarding, the Lounge remains the home base. The Sport Trac is visible beyond the Lounge, and richly visual doorway cards lead into the working rooms.

| Doorway | What it says | What it does |
|---|---|---|
| **Start Here** | “Lounge” | Opens vehicle setup, skill level, Mentor voice settings, privacy choices, ranking preferences, then later saved repair progress and maintenance story. |
| **Get to Work** | “Shop” | Opens the 3D truck with Mentor and the component/repair path around it. |
| **Figure It Out** | “Diagnostics Bay” | Opens the A/C Workbench, symptom guidance, sound comparison, and diagnosis tools. |
| **Plan the Parts** | “Part Store” | Opens the readiness dashboard and quality-first parts catalog. |
| **Make It Yours** | “The Body Shop” | Opens body-kit, paint, wheel, trim, interior, lighting, and stance generation when built. |

The Lounge is deliberately not an empty decorative room. It is the first conversation with the owner and the place where the app learns only the information needed to help: vehicle setup, skill/confidence, preferred Mentor voice behavior, privacy boundaries, and parts priorities. After that, it anchors the owner’s connection to the truck: what has been repaired, what is being saved for, what is next, and what the truck may become.

## Lounge onboarding flow

The Lounge should take a new owner through a short, human onboarding conversation. It should feel like pulling up a chair, not filling out a government form. Every answer is editable later in the Lounge settings.

| Stop | What the Lounge asks or offers | What is saved / handed off |
|---|---|---|
| **1. Welcome** | “This is your Shop. Let’s get your Sport Trac set up.” | A local profile nickname only if the owner wants one; no account or retailer credentials are required. |
| **2. Truck card** | Confirm year, engine, drivetrain, transmission, trim/cab notes, and optionally VIN later for fitment. | Vehicle profile. VIN is optional and should be entered only when the owner chooses a fitment lookup. |
| **3. Comfort level** | “How do you like to learn?” Choose beginner, learning, capable, or experienced. | Mentor detail level, pace, and safety explanation depth. |
| **4. Mentor presence** | Choose spoken guidance, hands-free controls, voice pace, and whether the Mentor explains the “why” behind a step. | Voice and accessibility settings. |
| **5. Parts style** | Choose a default: Built for life, best warranty, lowest delivered total, local pickup, marketplace/used, American Made, or another saved preference. | Part Store default ranking. The owner can change it at any time. |
| **6. Privacy promise** | Confirm that the app is for planning and learning; no checkout, payment data, retailer account, push message, text, or email is required. | Privacy preference acknowledgment only; no hidden marketing enrollment. |
| **7. Open the door** | The Lounge looks toward the connected Shop and asks, “What do you want to do with the truck today?” | Sends the owner to Shop, Diagnostics Bay, Part Store, or The Body Shop with their saved preferences active. |

The first practical question after onboarding should be simple: **“Are we fixing something, figuring out a problem, getting ready before it fails, or making the truck yours?”** That one answer selects the first doorway without locking the owner into a rigid path.

## Visual language

The space uses midnight navy, charcoal, burnished steel, raw wood, and amber work lights, with teal diagnostic accents and red/blue A/C safety accents. It should feel clean and cared for, but not like a showroom. It is a place where someone has learned, fixed things, and kept the truck alive.

The mentor’s workbench is a recognizable object. It should appear in every room in a subtle way: a small work lamp in the Lounge, an open notebook in the Shop, a consultation counter in the Part Store, and a concept board in The Body Shop. That visual thread makes the Mentor feel present rather than hidden behind a button.

## Mapping from today’s tabs

The app currently uses these main tabs:

| Current tab | Destination home | Migration approach |
|---|---|---|
| `VIEW_3D` | Shop | Become the central vehicle bay. The current 3D model is the centerpiece of the room. |
| `REPAIR_MANUAL` | Shop | Become a contextual repair board and Mentor workbench drawer after a component is selected. |
| `DIAGNOSTICS` | Diagnostics Bay inside Shop | Keep its content and make it a clearly visible physical bay / station. |
| `MAINTENANCE` | Lounge | Present as a service-history desk and “what is next” board. |
| `PARTS_CART` | Part Store | Keep private planning and checkout boundaries; change only the arrival, navigation, and visual environment. |
| No existing tab | The Body Shop | New feature; do not call it complete until a visualizer, saved-build model, and a part-list handoff exist. |

## Phased implementation

| Phase | What changes | Completion evidence |
|---|---|---|
| **1. Entrance shell** | Add a Lobby / destination screen and visual cards that navigate to existing screens. No existing repair or shopping logic is removed. | The user can open the destination and reach all existing screens. |
| **2. Shop skin** | Reframe 3D Model, Repair Manual, and Diagnostics as connected Shop zones; add Mentor workbench presence and contextual links. | A repair can start from the Shop, reach the model, Mentor, and diagnostics without feeling like unrelated tabs. |
| **3. Lounge first-visit flow** | Add the front-door onboarding: vehicle basics, skill/confidence, Mentor voice and hands-free preference, privacy choices, parts ranking preference, then saved repairs and upcoming maintenance. | The Lounge saves only user-approved settings and shows real persisted maintenance/checklist data rather than decorative fake content. |
| **4. Part Store skin** | Reframe Part Store as a planning counter / parts wall while retaining privacy, customer ranking, and no-auto-order rules. | Existing Part Store workflows still work under the new visual shell. |
| **5. The Body Shop** | Add body-kit / paint / wheels / stance / lighting / interior generation, saved build cards, and optional Part Store planning handoff. | Generated concepts, saved builds, and a clearly labeled “concept only / verify fitment” boundary are tested. |

## Non-negotiable boundaries

The destination should never sacrifice the project’s practical rules for aesthetics. The user must retain control of product ranking and shopping choice. No retailer accounts or payment data are stored. No automatic ordering happens. A rendered body kit or generated build concept is a visual idea, not a verified fitment claim. The A/C area retains its heat/refrigerant safety guidance. The Mentor teaches the repair on the model before asking anyone to work on the real truck.
