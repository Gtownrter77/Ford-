from collections import Counter, defaultdict
from pathlib import Path
import re

source = Path("app/src/main/java/com/example/data/SportTracData.kt").read_text()
blocks = re.findall(r"Component3DModel\((.*?)(?=\n\s*\)\n\s*}\s*(?:,|$))", source, flags=re.S)
components = []
for block in blocks:
    component_id = re.search(r'\bid\s*=\s*"([^"]+)"', block)
    name = re.search(r'\bname\s*=\s*"([^"]+)"', block)
    system = re.search(r'\bsystem\s*=\s*VehicleSystem\.([A-Z_]+)', block)
    if component_id and name and system:
        components.append({
            "id": component_id.group(1),
            "name": name.group(1),
            "system": system.group(1),
        })

by_system = defaultdict(list)
for component in components:
    by_system[component["system"]].append(component)

out = [f"TOTAL_COMPONENTS={len(components)}"]
for system, entries in sorted(by_system.items()):
    out.append(f"{system}={len(entries)}")
    for entry in entries:
        out.append(f"  - {entry['id']} | {entry['name']}")
Path("/tmp/sport_trac_model_inventory.md").write_text("\n".join(out) + "\n")
print("\n".join(out))
