from pathlib import Path
from bs4 import BeautifulSoup
import csv
import re
import json

ROOT = Path('/home/ubuntu/ford_explorer_sport_trac_2004/manual/2004 Ford Explorer Sport Trac 2WD V6-4.0L VIN K Flex Fuel')
OUT = Path('/home/ubuntu/ford_explorer_sport_trac_2004/extracted')
OUT.mkdir(parents=True, exist_ok=True)

patterns = {
    'torque': re.compile(r'(?i)\b(?:torque|tighten|tightening)[^\n]{0,160}'),
    'part_number': re.compile(r'\b[A-Z0-9]{2,5}[- ]?[A-Z0-9]{2,8}[- ]?[A-Z0-9]{0,5}\b'),
    'dimension': re.compile(r'(?i)\b\d+(?:\.\d+)?\s*(?:mm|cm|in\.?|inch(?:es)?|ft\.?[- ]?lb|lb\.?[- ]?ft|N[· ]?m|degrees?)\b'),
}
keywords = {
    'Engine': ['engine', 'fuel injection', 'cooling', 'ignition', 'emission', 'cylinder head', 'valvetrain', 'oil pan'],
    'Transmission': ['transmission', 'automatic transmission', 'driveshaft', 'transfer case', 'clutch'],
    'Suspension': ['suspension', 'steering', 'wheel hub', 'spring', 'shock absorber', 'ball joint', 'brake'],
    'Body': ['body', 'door', 'hood', 'tailgate', 'bumper', 'fender', 'glass', 'seat belt'],
    'Wiring': ['wiring', 'connector', 'pinout', 'circuit', 'electrical', 'fuse', 'relay', 'ground'],
    'Interior': ['instrument panel', 'interior', 'seat', 'console', 'carpet', 'air conditioning', 'heater'],
}

rows = []
for page in sorted((ROOT / 'pages').glob('*.html'), key=lambda p: int(p.stem) if p.stem.isdigit() else p.stem):
    raw = page.read_text(errors='ignore')
    soup = BeautifulSoup(raw, 'html.parser')
    title = soup.title.get_text(' ', strip=True) if soup.title else ''
    text = soup.get_text(' ', strip=True)
    text = re.sub(r'\s+', ' ', text)
    lower = text.lower()
    systems = [name for name, terms in keywords.items() if any(term in lower for term in terms)]
    imgs = [img.get('src') for img in soup.find_all('img') if img.get('src')]
    torques = sorted(set(m.group(0).strip() for m in patterns['torque'].finditer(text)))
    dims = sorted(set(m.group(0).strip() for m in patterns['dimension'].finditer(text)))
    parts = sorted(set(m.group(0).strip() for m in patterns['part_number'].finditer(text)))
    rows.append({'page': page.name, 'title': title, 'systems': ';'.join(systems), 'images': len(imgs), 'torque_hits': ' || '.join(torques[:20]), 'dimension_hits': ';'.join(dims[:30]), 'part_candidates': ';'.join(parts[:50]), 'text': text})

with (OUT / 'manual_pages.csv').open('w', newline='', encoding='utf-8') as f:
    writer = csv.DictWriter(f, fieldnames=rows[0].keys())
    writer.writeheader(); writer.writerows(rows)

summary = {
    'root': str(ROOT),
    'page_count': len(rows),
    'image_count': sum(r['images'] for r in rows),
    'pages_by_system': {k: sum(k in r['systems'].split(';') for r in rows) for k in keywords},
    'pages_with_torque_terms': sum(bool(r['torque_hits']) for r in rows),
    'pages_with_dimensions': sum(bool(r['dimension_hits']) for r in rows),
    'pages_with_part_candidates': sum(bool(r['part_candidates']) for r in rows),
}
(OUT / 'summary.json').write_text(json.dumps(summary, indent=2), encoding='utf-8')

with (OUT / 'titles_and_systems.md').open('w', encoding='utf-8') as f:
    f.write('# Manual Page Index\n\n')
    for r in rows:
        f.write(f"- **{r['page']}** — {r['title']} — systems: {r['systems'] or 'Unclassified'} — images: {r['images']}\n")

print(json.dumps(summary, indent=2))
print('Wrote', OUT / 'manual_pages.csv')
print('Wrote', OUT / 'titles_and_systems.md')
