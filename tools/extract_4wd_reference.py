from pathlib import Path
import json, re

root = Path('/home/ubuntu/ford_sport_trac_repo')
src = root / 'app/src/main/java/com/example/data/SportTracData.kt'
text = src.read_text(errors='ignore')

# Capture each object-ish block from id = through the next id = or end.
blocks = re.split(r'(?=\n\s*id\s*=\s*"[^\"]+")', text)
entries = []
for block in blocks:
    mid = re.search(r'\bid\s*=\s*"([^"]+)"', block)
    if not mid:
        continue
    entry_id = mid.group(1)
    name = (re.search(r'\bname\s*=\s*"([^"]+)"', block) or [None, ''])[1]
    desc = (re.search(r'\bdescription\s*=\s*"([^"]+)"', block) or [None, ''])[1]
    torques = []
    for m in re.finditer(r'TorqueSpec\(\s*"([^"]+)"\s*,\s*"([^"]*)"\s*,\s*"([^"]*)"\s*,\s*"([^"]*)"\s*\)', block):
        torques.append({'component':m.group(1),'imperial':m.group(2),'metric':m.group(3),'notes':m.group(4)})
    mentions = []
    for line in block.splitlines():
        if re.search(r'(?i)wiring|harness|connector|pinout|wheel speed|shift motor|transfer case|4wd|4x4|front differential|front drive|driveshaft', line):
            s=line.strip()
            if s and s not in mentions:
                mentions.append(s)
    if torques or mentions:
        entries.append({'id':entry_id,'name':name,'description':desc,'torque_specs':torques,'technical_mentions':mentions[:80]})

# Repository docs with explicit wiring/torque claims.
doc_hits=[]
for p in sorted(root.glob('docs/*.md')):
    t=p.read_text(errors='ignore')
    lines=[]
    for i,line in enumerate(t.splitlines(),1):
        if re.search(r'(?i)wiring|harness|connector|pinout|schematic|torque|lb-ft|ft-lb|in-lb|Nm|transfer case|front axle|rear axle|driveshaft|4wd|4x4', line):
            lines.append({'line':i,'text':line.strip()})
    if lines: doc_hits.append({'path':str(p.relative_to(root)),'hits':lines})

out={'source_file':str(src.relative_to(root)),'entry_count':len(entries),'entries':entries,'doc_hits':doc_hits}
(root/'tools/4wd_reference.json').write_text(json.dumps(out,indent=2),encoding='utf-8')

with (root/'4wd_wiring_torque_reference.md').open('w',encoding='utf-8') as f:
    f.write('# 2004 Ford Explorer Sport Trac 4WD Wiring and Torque Reference\n\n')
    f.write('This working reference is extracted from the Gtownrter77/Ford- repository. It distinguishes explicit repository data from project-authored or externally attributed values. It is not a substitute for the Ford workshop manual.\n\n')
    f.write(f'Parsed {len(entries)} data entries from `{src.relative_to(root)}` and {len(doc_hits)} documentation files.\n\n')
    f.write('## 4WD drivetrain and electrical mentions\n\n')
    for e in entries:
        if e['torque_specs'] or e['technical_mentions']:
            f.write(f"### `{e['id']}` — {e['name'] or 'Unnamed entry'}\n\n")
            if e['description']: f.write(e['description']+'\n\n')
            if e['torque_specs']:
                f.write('| Component | Imperial | Metric | Notes |\n|---|---:|---:|---|\n')
                for q in e['torque_specs']:
                    f.write(f"| {q['component']} | {q['imperial']} | {q['metric']} | {q['notes']} |\n")
                f.write('\n')
            if e['technical_mentions']:
                f.write('Technical mentions:\n\n')
                for m in e['technical_mentions'][:30]: f.write(f'- {m}\n')
                f.write('\n')
    f.write('## Repository wiring evidence\n\n')
    f.write('The repository does not appear to contain a complete factory wiring-diagram or connector-pinout dump. `docs/SPORT_TRAC_ALL_REPAIRS.md` explicitly states that a wiring-diagram dump is not included. The available wiring evidence is therefore limited to model/data descriptions, component labels, and repair-step text such as ABS harness, dash harness/CJB/GEM, headlight harness, and window/roof motor connectors.\n\n')
    for d in doc_hits:
        wiring=[h for h in d['hits'] if re.search(r'(?i)wiring|harness|connector|pinout|schematic|shift motor|wheel speed',h['text'])]
        if wiring:
            f.write(f"### `{d['path']}`\n\n")
            for h in wiring: f.write(f"- Line {h['line']}: {h['text']}\n")
            f.write('\n')
    f.write('## Explicit torque data found in repository documents\n\n')
    f.write('| Source | Component / use | Value | Evidence status |\n|---|---|---:|---|\n')
    explicit=[
        ('docs/2004_SPORT_TRAC_OWNER_GUIDE_SPECS.md','Wheel lug nuts','84–114 lb-ft (113–153 Nm)','Owner Guide value reproduced in repository; roadside/wheel torque, not a complete workshop torque table.'),
        ('docs/2004_SPORT_TRAC_OWNER_GUIDE_SPECS.md','Transfer case refill','1.3 qt MERCON ATF','Fluid capacity, not fastener torque.'),
        ('docs/2004_SPORT_TRAC_OWNER_GUIDE_SPECS.md','Front axle refill','1.8 qt SAE 80W-90','Fluid capacity, not fastener torque.'),
        ('docs/SPORT_TRAC_BANK2_LEAN_KIT.md','Intake manifold bolts','10 Nm / 89 in-lb','Repository workshop reference; verify against exact Ford workshop page.'),
        ('docs/SPORT_TRAC_BANK2_LEAN_KIT.md','Throttle body bolts','9 Nm / 80 in-lb','Repository reference attributed to CHARM 2005 VIN K.'),
        ('docs/SPORT_TRAC_BANK2_LEAN_KIT.md','Ignition coil bolts','6 Nm / 53 in-lb','Repository reference attributed to Mitchell 2004 engine-performance table.'),
        ('docs/SPORT_TRAC_BANK2_LEAN_KIT.md','Coil bracket bolts','10 Nm / 89 in-lb','Repository reference attributed to Mitchell 2004 engine table.'),
        ('docs/SPORT_TRAC_BANK2_LEAN_KIT.md','Spark plugs','20 Nm / 15 lb-ft','Repository reference attributed to Mitchell 2004 engine-performance table.'),
        ('docs/SPORT_TRAC_BANK2_LEAN_KIT.md','Fuel rail bolts','23 Nm / 17 lb-ft','Repository reference attributed to Mitchell 2004 engine-performance table.'),
        ('docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md','Intake bolts','10 Nm / 89 in-lb','Repository workshop note; verify.'),
        ('docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md','Throttle body','9 Nm / 80 in-lb','Repository workshop note; verify.'),
        ('docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md','Coil bolts / bracket','6 Nm / 53 in-lb; 10 Nm / 89 in-lb','Repository workshop note; verify.'),
        ('docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md','Spark plugs','20 Nm / 15 lb-ft','Repository workshop note; verify.'),
        ('docs/SPORT_TRAC_LEAN_MISFIRE_REPAIRS.md','Fuel rail','23 Nm / 17 lb-ft','Repository workshop note; verify.'),
    ]
    for row in explicit: f.write('| '+' | '.join(row)+' |\n')
print(json.dumps({'entries':len(entries),'doc_files':len(doc_hits),'output':'4wd_wiring_torque_reference.md'},indent=2))
