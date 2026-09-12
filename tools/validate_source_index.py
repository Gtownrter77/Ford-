#!/usr/bin/env python3
"""Report page count, empty pages, duplicate hashes, configuration labels, index version."""
from __future__ import annotations

import gzip
import json
import sys
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SUMMARY = ROOT / "docs" / "source_manual_extraction" / "source_index.summary.json"
JSONL = ROOT / "docs" / "source_manual_extraction" / "source_index.jsonl"


def main() -> int:
    summary = json.loads(SUMMARY.read_text())
    n = 0
    empty = 0
    hashes: Counter[str] = Counter()
    configs: Counter[str] = Counter()
    eligibility: Counter[str] = Counter()
    src = JSONL if JSONL.exists() else ROOT / "app" / "src" / "main" / "assets" / "mentor" / "source_index.jsonl.gz"
    opener = gzip.open if src.suffix == ".gz" else open
    with opener(src, "rt", encoding="utf-8") as fh:
        for line in fh:
            if not line.strip():
                continue
            rec = json.loads(line)
            n += 1
            if len(rec.get("excerpt") or "") < 40:
                empty += 1
            hashes[rec.get("checksum") or ""] += 1
            configs[rec.get("configuration") or ""] += 1
            eligibility[rec.get("eligibility") or ""] += 1
    dupes = sum(1 for k, v in hashes.items() if k and v > 1)
    print(f"indexVersion={summary.get('indexVersion')}")
    print(f"pageCount={n}")
    print(f"emptyPages={empty}")
    print(f"duplicateBodyHashes={dupes}")
    print("configurationLabels=")
    for k, v in configs.most_common():
        print(f"  {v}\t{k}")
    print("eligibility=")
    for k, v in eligibility.most_common():
        print(f"  {v}\t{k}")
    ok = n > 0 and empty == 0 and eligibility.get("preferred", 0) > 0
    print("STATUS=" + ("PASS" if ok else "FAIL"))
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
