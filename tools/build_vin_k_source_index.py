#!/usr/bin/env python3
"""Build a bounded VIN-K 4WD source index from the CHARM zip. Does not extract images."""
from __future__ import annotations

import hashlib
import json
import re
import zipfile
from html.parser import HTMLParser
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ZIP_CANDIDATES = [
    Path("/mnt/sd1/2004 Ford Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel.zip"),
    Path("/mnt/shared/removable/sd1/2004 Ford Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel.zip"),
]
OUT_DIR = ROOT / "docs" / "source_manual_extraction"
ASSET_DIR = ROOT / "app" / "src" / "main" / "assets" / "mentor"
CONFIG = "2004 Sport Trac 4WD V6-4.0L VIN K Flex Fuel"
TITLE_SPLIT = re.compile(r"\s+—\s+2004 Ford Explorer Sport Trac", re.I)
TAG = re.compile(r"<[^>]+>")
WS = re.compile(r"\s+")
TWOWD = re.compile(r"\b2WD\b", re.I)

TYPE_RULES = [
    ("diagnosis", re.compile(r"\b(DTC|diagnos|pinpoint|trouble code|testing and inspection)\b", re.I)),
    ("specification", re.compile(r"\b(spec|torque|capacity|pressure|clearance)\b", re.I)),
    ("wiring", re.compile(r"\b(wiring|diagram|connector|pinout)\b", re.I)),
    ("parts/labor", re.compile(r"\b(parts|labor|times)\b", re.I)),
    ("procedure", re.compile(r"\b(service and repair|removal|install|replace)\b", re.I)),
]


class TextExtractor(HTMLParser):
    def __init__(self) -> None:
        super().__init__()
        self._skip = 0
        self.parts: list[str] = []

    def handle_starttag(self, tag: str, attrs) -> None:
        if tag in {"script", "style"}:
            self._skip += 1

    def handle_endtag(self, tag: str) -> None:
        if tag in {"script", "style"} and self._skip:
            self._skip -= 1

    def handle_data(self, data: str) -> None:
        if self._skip:
            return
        t = data.strip()
        if t:
            self.parts.append(t)


def text_of(html: str) -> str:
    p = TextExtractor()
    try:
        p.feed(html)
    except Exception:
        return WS.sub(" ", TAG.sub(" ", html))
    return WS.sub(" ", " ".join(p.parts)).strip()


def evidence_type(title: str) -> str:
    for name, rx in TYPE_RULES:
        if rx.search(title):
            return name
    return "unknown"


def find_zip() -> Path:
    for p in ZIP_CANDIDATES:
        if p.exists():
            return p
    raise SystemExit("CHARM VIN-K zip not found on /mnt/sd1")


def main() -> None:
    zpath = find_zip()
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    ASSET_DIR.mkdir(parents=True, exist_ok=True)
    jsonl = OUT_DIR / "source_index.jsonl"
    compact = ASSET_DIR / "source_index.jsonl"
    pages = 0
    empty = 0
    blocked = 0
    preferred = 0
    hashes: dict[str, str] = {}
    dupes = 0
    with zipfile.ZipFile(zpath) as z, jsonl.open("w", encoding="utf-8") as out, compact.open(
        "w", encoding="utf-8"
    ) as asset:
        for name in z.namelist():
            if "/pages/" not in name or not name.endswith(".html"):
                continue
            raw = z.read(name).decode("utf-8", "replace")
            tm = re.search(r"<title>(.*?)</title>", raw, re.I | re.S)
            title_full = WS.sub(" ", tm.group(1) if tm else "").strip()
            title = TITLE_SPLIT.split(title_full)[0].strip() or f"page {Path(name).stem}"
            body = text_of(raw)
            # drop chrome
            for junk in (
                "Operation CHARM: Car repair manuals for everyone.",
                "Manuals through 2025 now available!",
            ):
                body = body.replace(junk, " ")
            body = WS.sub(" ", body).strip()
            pages += 1
            if len(body) < 80:
                empty += 1
                continue
            digest = hashlib.sha1(body.encode()).hexdigest()[:16]
            if digest in hashes:
                dupes += 1
            hashes[digest] = name
            is_2wd = bool(TWOWD.search(title) or TWOWD.search(name))
            if is_2wd:
                blocked += 1
                eligible = "blocked"
            else:
                preferred += 1
                eligible = "preferred"
            page_id = Path(name).stem
            rec = {
                "sourceId": f"charm-4wd-vin-k-{page_id}",
                "title": title[:180],
                "section": "CHARM 4WD VIN K Flex Fuel",
                "pageLabel": page_id,
                "configuration": CONFIG,
                "excerpt": body[:700],
                "sourceUrl": f"zip://pages/{page_id}.html",
                "evidenceType": evidence_type(title),
                "eligibility": eligible,
                "checksum": digest,
            }
            line = json.dumps(rec, ensure_ascii=False)
            out.write(line + "\n")
            if eligible == "preferred":
                asset.write(line + "\n")
    summary = {
        "indexVersion": "2.0.0",
        "source": str(zpath.name),
        "targetConfiguration": CONFIG,
        "pageCount": pages,
        "indexedNonEmpty": preferred + blocked - empty,
        "eligibleForTarget": preferred,
        "blockedConfigurationMismatch": blocked,
        "emptyPages": empty,
        "duplicateBodyHashes": dupes,
        "indexPath": str(jsonl.relative_to(ROOT)),
        "assetPath": str(compact.relative_to(ROOT)),
    }
    (OUT_DIR / "source_index.summary.json").write_text(json.dumps(summary, indent=2) + "\n")
    print(json.dumps(summary, indent=2))


if __name__ == "__main__":
    main()
