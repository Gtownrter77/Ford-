from pathlib import Path
from html import unescape
import re
import csv

ROOT = Path('/home/ubuntu/ford_explorer_sport_trac_2004/manual/2004 Ford Explorer Sport Trac 2WD V6-4.0L VIN K Flex Fuel')
OUT = Path('/home/ubuntu/ford_sport_trac_repo/manual_archive_full_audit.tsv')
PATTERNS = {
    'shock': r'\bshock\b|shock absorber',
    'rear_suspension': r'rear suspension|rear spring|stabilizer bar|suspension noise',
    'noise': r'\bclick\b|\bclunk\b|knock|noise|popping|creak',
    'turning': r'turn(?:ing|s)|cornering|left turn|right turn',
    'wheel_axle': r'wheel|axle|hub|bearing|differential|driveshaft',
    'exhaust': r'exhaust|muffler|tailpipe|catalytic converter|resonator|dual exhaust',
    'torque': r'torque specification|torque|tighten',
}
files = sorted(ROOT.rglob('*.html'))
rows = []
for path in files:
    raw = path.read_text(errors='ignore')
    title_match = re.search(r'<title[^>]*>(.*?)</title>', raw, re.I | re.S)
    title = unescape(re.sub(r'<[^>]+>', ' ', title_match.group(1))).strip() if title_match else ''
    text = unescape(re.sub(r'<script.*?</script>|<style.*?</style>|<[^>]+>', ' ', raw, flags=re.I | re.S))
    text = re.sub(r'\s+', ' ', text).strip()
    hits = [name for name, pattern in PATTERNS.items() if re.search(pattern, text, re.I)]
    if hits:
        rows.append({
            'page': str(path.relative_to(ROOT)),
            'title': title,
            'categories': ','.join(hits),
            'matched_excerpt': text[:700],
        })
with OUT.open('w', newline='') as fh:
    writer = csv.DictWriter(fh, fieldnames=['page', 'title', 'categories', 'matched_excerpt'], delimiter='\t')
    writer.writeheader()
    writer.writerows(rows)
print(f'total_html_pages={len(files)}')
print(f'relevant_pages={len(rows)}')
for name in PATTERNS:
    print(f'{name}_pages={sum(name in row["categories"].split(",") for row in rows)}')
print(f'output={OUT}')
