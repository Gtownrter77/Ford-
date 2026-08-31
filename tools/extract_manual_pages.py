from pathlib import Path
from html import unescape
import re
import sys

root = Path('/home/ubuntu/ford_explorer_sport_trac_2004/manual/2004 Ford Explorer Sport Trac 2WD V6-4.0L VIN K Flex Fuel/pages')
out = Path('/home/ubuntu/ford_sport_trac_repo/manual_relevant_pages.txt')
page_numbers = sys.argv[1:] or ['528','5312','5313','3363','3367','3373','7204','7205','7220','8747','8750','2267','2268','2269','2957','2999']
with out.open('w') as fh:
    for n in page_numbers:
        path = root / f'{n}.html'
        if not path.exists():
            continue
        raw = path.read_text(errors='ignore')
        title = re.search(r'<title[^>]*>(.*?)</title>', raw, re.I | re.S)
        title = unescape(re.sub(r'<[^>]+>', ' ', title.group(1))).strip() if title else ''
        text = unescape(re.sub(r'<script.*?</script>|<style.*?</style>|<[^>]+>', ' ', raw, flags=re.I | re.S))
        text = re.sub(r'\s+', ' ', text).strip()
        fh.write(f'===== PAGE {n} =====\n{title}\n{text}\n\n')
print(f'written={out}')
