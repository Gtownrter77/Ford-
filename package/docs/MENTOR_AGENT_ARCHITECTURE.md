# Mentor Two-Agent Architecture

The Mentor is organized as two different agents with different responsibilities.

## Agent 1 — Mentor

**Provider:** Gemini 2.5 Flash when a valid Gemini API key is configured.

**Fallback:** the app’s offline safety/rules engine when Gemini is unavailable. The fallback is explicitly labeled as a rules engine; it is not represented as an open-source general-purpose LLM.

The Mentor receives the user question, recent conversation context, vehicle configuration, OBD/FORScan evidence, and bounded source-retrieval context. Its job is to reason about the diagnosis and produce a safe outcome:

- distinguish observations, measurements, evidence, inferences, and unknowns;
- cite source IDs when making manual-backed claims;
- identify stop/tow and do-not-run conditions;
- recommend the next discriminating test;
- connect the issue to the 3D component model;
- create a technician-ready handoff.

The Mentor must not invent manual specifications or claim that a physical vehicle is safe to drive without physical verification.

## Agent 2 — Source Scraper / Indexer

**Provider:** deterministic repository scraper and index builder. It is intentionally not a generative model: source ingestion must not invent, paraphrase, or hallucinate manual text.

The scraper reads the available source archive, extracts page metadata/text, computes content hashes, labels evidence type, records configuration, and marks incompatible pages as blocked. It produces a versioned JSONL index for the Mentor retrieval layer.

Current source reality:

- The available extracted bundle is a **2WD VIN-K** bundle with 9,185 pages.
- The target vehicle is **4WD VIN-K**.
- Therefore all current bundle entries are marked `blocked` for target use.
- The scraper must never silently promote these pages to 4WD evidence.
- A genuine 4WD VIN-K source bundle must be supplied/indexed before these pages can be used for exact 4WD procedures.

## Current implementation

- Gemini endpoint corrected to `gemini-2.5-flash`.
- Mentor skill contract is injected into the system prompt.
- Mentor source retrieval supplies bounded 4WD VIN-K workshop pointers and refuses 2WD URLs.
- Offline fallback preserves safety and source-warning behavior.
- `tools/build_source_scrape_index.py` is the dedicated scraper/indexer.
- Its current output is `docs/source_manual_extraction/source_index.jsonl` with an adjacent summary file.

## Provider policy

Do not describe the offline rules engine as a full open-source AI agent. If a real free/open-source local model is added later, it must be a separate provider implementing the same Mentor contract, with its model name, quantization, license, context limit, and offline status displayed in the app. It must receive the same source citations and safety rules and pass the same evaluation suite.

## Required next steps

1. Acquire or add the correct 4WD VIN-K page corpus.
2. Run the scraper against that corpus and validate configuration labels.
3. Add page-level Room/SQLite FTS retrieval and citations.
4. Add persisted technician sessions for DTCs, PIDs, measurements, and source IDs.
5. Add provider selection only after a real local model is bundled and benchmarked.
