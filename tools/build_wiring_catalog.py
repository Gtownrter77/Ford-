from pathlib import Path
import json, re

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / 'app/src/main/java/com/example/data/SportTracData.kt'
OUT = ROOT / 'tools/wiring_catalog.json'
text = SRC.read_text(errors='ignore')
lines = text.splitlines()
entries = []
current = None
for line_number, line in enumerate(lines, 1):
    m = re.search(r'\bid\s*=\s*"([^"]+)"', line)
    if m:
        current = {'id': m.group(1), 'start_line': line_number, 'mentions': [], 'torques': []}
        entries.append(current)
    if current is None:
        continue
    if 'name' not in current:
        m = re.search(r'\bname\s*=\s*"([^"]+)"', line)
        if m: current['name'] = m.group(1)
    if 'description' not in current:
        m = re.search(r'\bdescription\s*=\s*"([^"]+)"', line)
        if m: current['description'] = m.group(1)
    if re.search(r'(?i)wiring|harness|connector|pinout|schematic|wheel speed|shift motor|4wd|4x4|CJB|GEM|ABS', line):
        current['mentions'].append({'line': line_number, 'text': line.strip()})
    m = re.search(r'TorqueSpec\(\s*"([^"]+)"\s*,\s*"([^"]*)"\s*,\s*"([^"]*)"\s*,\s*"([^"]*)"', line)
    if m:
        current['torques'].append({'component': m.group(1), 'imperial': m.group(2), 'metric': m.group(3), 'notes': m.group(4), 'line': line_number})

wiring_entries = [e for e in entries if e['mentions']]
for e in wiring_entries:
    corpus = ' '.join([e.get('id',''), e.get('name',''), e.get('description','')] + [m['text'] for m in e['mentions']])
    e['classification'] = '4WD control / drivetrain' if re.search(r'(?i)4wd|4x4|shift motor|transfer', corpus) else 'general electrical / harness'

payload = {
    'source': str(SRC.relative_to(ROOT)),
    'diagram_source_package': 'docs/wiring_diagrams/charm_4wd_vin_k/',
    'entries': wiring_entries,
    'note': 'The embedded CHARM package is the source of actual wiring plates; this JSON is only the repository-authored evidence index.'
}
OUT.write_text(json.dumps(payload, indent=2))
print(json.dumps({'wiring_entries': len(wiring_entries), 'output': str(OUT.relative_to(ROOT))}, indent=2))
