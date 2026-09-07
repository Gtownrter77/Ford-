#!/usr/bin/env python3
"""Build a bounded, auditable source index for the Mentor's scraping agent.

This is intentionally deterministic: it never invents page text or configuration.
The current repository archive is explicitly a 2WD VIN-K bundle, so entries are
marked blocked for the target 4WD vehicle until a 4WD corpus is supplied.
"""
from __future__ import annotations
import argparse, csv, hashlib, json
from pathlib import Path

TARGET = "2004 Ford Explorer Sport Trac 4WD V6-4.0L VIN K Flex Fuel"

def classify(title: str, systems: str) -> str:
    text = f"{title} {systems}".lower()
    if any(x in text for x in ("torque", "specification", "capacity", "pressure", "dimension")):
        return "specification"
    if any(x in text for x in ("wiring", "connector", "circuit", "diagram")):
        return "wiring"
    if any(x in text for x in ("diagnos", "dtc", "symptom", "test")):
        return "diagnosis"
    if any(x in text for x in ("remove", "install", "service", "repair")):
        return "procedure"
    return "unknown"

def build(source_csv: Path, output: Path) -> dict:
    rows = []
    with source_csv.open(newline="", encoding="utf-8", errors="replace") as handle:
        for row in csv.DictReader(handle):
            page = (row.get("page") or "").strip()
            title = (row.get("title") or "").strip()
            systems = (row.get("systems") or "").strip()
            text = (row.get("text") or "").strip()
            source_id = "manual-2wd-vin-k-" + hashlib.sha256(f"{page}|{title}|{text}".encode()).hexdigest()[:16]
            rows.append({
                "sourceId": source_id,
                "title": title,
                "section": systems,
                "pageLabel": page,
                "configuration": "2004 Sport Trac 2WD 4.0L VIN K Flex Fuel",
                "targetConfiguration": TARGET,
                "eligibility": "blocked",
                "evidenceType": classify(title, systems),
                "excerpt": text[:1200],
                "sourcePath": str(source_csv),
                "contentSha256": hashlib.sha256(text.encode()).hexdigest(),
                "fitWarning": "2WD source: do not silently apply to the target 4WD vehicle."
            })
    output.parent.mkdir(parents=True, exist_ok=True)
    with output.open("w", encoding="utf-8") as handle:
        for row in rows:
            handle.write(json.dumps(row, ensure_ascii=False, separators=(",", ":")) + "\n")
    summary = {
        "indexVersion": "1.0.0",
        "source": str(source_csv),
        "targetConfiguration": TARGET,
        "pageCount": len(rows),
        "eligibleForTarget": sum(row["eligibility"] != "blocked" for row in rows),
        "blockedConfigurationMismatch": sum(row["eligibility"] == "blocked" for row in rows),
        "indexPath": str(output),
    }
    output.with_suffix(".summary.json").write_text(json.dumps(summary, indent=2) + "\n", encoding="utf-8")
    return summary

def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", type=Path, default=Path("docs/source_manual_extraction/2wd_bundle/manual_pages.csv"))
    parser.add_argument("--output", type=Path, default=Path("docs/source_manual_extraction/source_index.jsonl"))
    args = parser.parse_args()
    print(json.dumps(build(args.source, args.output), indent=2))

if __name__ == "__main__":
    main()
