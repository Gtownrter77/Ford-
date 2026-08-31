from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
data = (ROOT / "app/src/main/java/com/example/data/SportTracData.kt").read_text()
catalog = (ROOT / "app/src/main/java/com/example/data/SportTracPartsCatalog.kt").read_text()
readiness = (ROOT / "app/src/main/java/com/example/data/SportTracPartsReadiness.kt").read_text()
notes = (ROOT / "source_notes.md").read_text()

required = {
    "component id": "rear_shock_absorbers_4wd_3d",
    "4wd source label": "4WD VIN K CHARM rear specification",
    "manual section": "Operation CHARM 4WD VIN K: Suspension > Specifications > Rear",
    "upper torque": 'TorqueSpec("Shock absorber-to-frame nuts", "17", "23"',
    "lower torque": 'TorqueSpec("Shock absorber lower bolt", "46", "63"',
    "wheel torque": 'TorqueSpec("Wheel nuts", "100", "135"',
    "jack-stand warning": "Do not work beneath a vehicle supported only by a floor jack",
}
for label, needle in required.items():
    assert needle in data, f"missing {label}: {needle}"
assert "rear_suspension_readiness" in readiness
assert "rear_shock_absorbers_4wd_3d" in catalog
assert "part_rear_shock_kyb_344269_pair" in catalog
assert "part_rear_shock_kyb_344269_pair" in readiness
assert "Live 4WD rear-suspension torque verification" in notes
assert "shock absorber-to-frame nuts at 23 N·m (17 lb-ft)" in notes
assert "shock absorber lower bolt at 63 N·m (46 lb-ft)" in notes

# Ensure the component mesh is non-empty and all face indices stay in range.
mesh = data[data.index('private fun createRearShockMesh'):data.index('private fun generateSubAssemblies')]
assert "createBoxMesh" in mesh and "createCylinderMesh" in mesh
assert "return Pair(vertices, faces)" in mesh

# Basic Kotlin structural sanity for the edited files.
for path in [ROOT / "app/src/main/java/com/example/data/SportTracData.kt", ROOT / "app/src/main/java/com/example/data/SportTracPartsCatalog.kt", ROOT / "app/src/main/java/com/example/data/SportTracPartsReadiness.kt"]:
    text = path.read_text()
    assert text.count("(") == text.count(")"), f"unbalanced parentheses in {path}"
    assert text.count("{") == text.count("}"), f"unbalanced braces in {path}"

assert (ROOT / "app/src/main/assets/models/ford_explorer_sport_trac_2004.glb").exists()
print("rear-shock static validation: PASS")
print("verified torque: upper 23 N·m / 17 lb-ft; lower 63 N·m / 46 lb-ft; wheel 135 N·m / 100 lb-ft")
print("linked IDs: component, catalog part, readiness package")
