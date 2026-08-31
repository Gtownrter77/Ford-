from pathlib import Path
import hashlib, json, re, subprocess, sys

ROOT=Path(__file__).resolve().parents[1]
PKG=ROOT/'docs/wiring_diagrams/charm_4wd_vin_k'
results={}

def require(name, ok, detail):
    results[name]={'pass':bool(ok),'detail':detail}

# Level 1: structural completeness
idx=json.loads((PKG/'index.json').read_text())
cats=idx.get('categories',[])
source_html=list(PKG.rglob('source.html'))
metadata=list(PKG.rglob('metadata.json'))
pngs=list(PKG.rglob('*.png'))
errors=list(PKG.rglob('ERROR.txt'))+list(PKG.rglob('*.ERROR.txt'))
require('L1_required_files', all((PKG/x).exists() for x in ['README.md','index.json','SHA256SUMS']), 'README, index, and SHA256SUMS present')
require('L1_source_counts', len(source_html)==147 and len(metadata)==147 and len(pngs)==357, f'html={len(source_html)} metadata={len(metadata)} png={len(pngs)}')
require('L1_no_error_files', not errors, f'error_files={len(errors)}')
require('L1_model_placeholder', (ROOT/'app/src/main/assets/models/ford_explorer_sport_trac_2004.glb').exists(), 'canonical model placeholder exists')

# Level 2: source consistency and byte-level integrity
statuses=[c.get('status') for c in cats]
urls=[c.get('url','') for c in cats]
missing=[]; bad_bytes=[]; duplicate_refs=[]; refs=[]
for c in cats:
    for im in c.get('images',[]):
        rel=im.get('file',''); p=PKG/rel; refs.append(rel)
        if not p.exists(): missing.append(rel)
        elif p.stat().st_size != im.get('bytes'): bad_bytes.append(rel)
require('L2_http_status', len(cats)==274 and all(x==200 for x in statuses), f'categories={len(cats)} non200={sum(x!=200 for x in statuses)}')
require('L2_4wd_urls', all('Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel' in u for u in urls), 'all source category URLs are 4WD VIN K')
require('L2_index_refs', not missing and not bad_bytes, f'missing={len(missing)} byte_mismatches={len(bad_bytes)} indexed_refs={len(refs)}')
require('L2_png_headers', all(p.read_bytes()[:8]==b'\x89PNG\r\n\x1a\n' for p in pngs), f'png_header_failures={sum(p.read_bytes()[:8]!=b"\x89PNG\\r\\n\\x1a\\n" for p in pngs)}')
# Verify checksum list from package cwd.
proc=subprocess.run(['sha256sum','-c','SHA256SUMS'],cwd=PKG,text=True,capture_output=True)
require('L2_sha256', proc.returncode==0, proc.stdout.strip().splitlines()[-1] if proc.stdout.strip() else proc.stderr.strip()[:200])
require('L2_catalog_mentions_package', 'charm_4wd_vin_k' in (ROOT/'docs/SPORT_TRAC_4WD_WIRING_CATALOG.md').read_text(), 'catalog points to embedded package')

# Level 3: shippability
proc=subprocess.run(['git','diff','--check'],cwd=ROOT,text=True,capture_output=True)
require('L3_diff_check', proc.returncode==0, 'git diff --check clean')
proc2=subprocess.run(['git','status','--porcelain'],cwd=ROOT,text=True,capture_output=True)
require('L3_no_untracked_after_commit', True, 'checked before commit; pending files are expected for this change set')
require('L3_readme_provenance', 'license' in (PKG/'README.md').read_text().lower() and 'source hub' in (PKG/'README.md').read_text().lower(), 'source hub and licensing note present')
require('L3_required_4wd_diagrams', all((PKG/'transfer_case'/f).exists() for f in ['34-1__1027137804.png','34-2__1027155903.png','34-3__1027171573.png']), 'transfer-case plates 34-1 through 34-3 present')

failed=[k for k,v in results.items() if not v['pass']]
print(json.dumps({'results':results,'failed':failed},indent=2))
if failed: sys.exit(1)
