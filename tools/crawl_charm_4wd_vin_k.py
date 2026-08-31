from pathlib import Path
from urllib.parse import urljoin
from bs4 import BeautifulSoup
import json, re, requests, time
BASE='https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/'
TREE=urljoin(BASE,'Repair%20and%20Diagnosis/')
OUT=Path('/home/ubuntu/ford_explorer_sport_trac_2004_charm_tree'); OUT.mkdir(parents=True,exist_ok=True)
s=requests.Session(); s.headers['User-Agent']='Mozilla/5.0 (compatible; charm-tree-index/1.0)'
r=s.get(TREE,timeout=45); r.raise_for_status(); soup=BeautifulSoup(r.text,'html.parser')
links=[]
for a in soup.find_all('a',href=True):
    href=a['href']
    if 'Diagrams/Electrical%20Diagrams/' in href:
        u=urljoin(TREE,href)
        if u not in [x['url'] for x in links]: links.append({'label':a.get_text(' ',strip=True),'url':u})
all_meta=[]
for item in links:
    safe=re.sub(r'[^A-Za-z0-9._-]+','_',item['label'] or 'diagram').strip('_').lower() or 'diagram'
    d=OUT/safe; d.mkdir(exist_ok=True)
    rr=s.get(item['url'],timeout=45)
    meta={'label':item['label'],'url':item['url'],'status':rr.status_code,'images':[]}
    if rr.ok:
        (d/'source.html').write_text(rr.text)
        sp=BeautifulSoup(rr.text,'html.parser')
        for img in sp.find_all('img'):
            src=img.get('src')
            if not src or '/images/' not in src: continue
            iu=urljoin(item['url'],src); ident=iu.rstrip('/').split('/')[-1]
            fig=img.find_previous(string=re.compile(r'\d+[-–]\d+'))
            label=str(fig).strip() if fig else f'plate-{len(meta["images"])+1}'
            fn=re.sub(r'[^A-Za-z0-9._-]+','_',label.strip('_'))+'_'+ident+'.png'
            try:
                ir=s.get(iu,timeout=45); ir.raise_for_status(); (d/fn).write_bytes(ir.content)
                meta['images'].append({'label':label,'url':iu,'file':str((d/fn).relative_to(OUT)),'bytes':len(ir.content)})
            except Exception as e:
                meta.setdefault('errors',[]).append({'url':iu,'error':str(e)})
            time.sleep(.2)
    else:
        (d/'ERROR.txt').write_text(f'{item["url"]}\nHTTP {rr.status_code}\n')
    (d/'metadata.json').write_text(json.dumps(meta,indent=2)); all_meta.append(meta); time.sleep(.4)
(OUT/'index.json').write_text(json.dumps({'tree_url':TREE,'categories':all_meta},indent=2))
print(json.dumps({'categories':len(all_meta),'plates':sum(len(x['images']) for x in all_meta),'errors':[x for x in all_meta if x['status']!=200]},indent=2))
