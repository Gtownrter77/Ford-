import { Search, X } from "lucide-react";
import { CHARM, JOBS, jobById } from "@/lib/mentor/book";
import { HVAC_DIAGS, PINPOINT_INDEX } from "@/lib/mentor/hvac-diagnostics";
import {
  LABOR,
  SECTIONS,
  SECTION_SHORT,
  leafById,
  searchLabor,
  type CharmRow,
} from "@/lib/mentor/charm-catalog";
import { useMentor } from "@/lib/mentor/store";
import { cn } from "@/lib/utils";

function Hours({ row }: { row: CharmRow }) {
  return (
    <tr className="border-t border-line">
      <td className="py-2 pr-3 align-top">
        <div className="text-sm text-fg">{row.o}</div>
        {row.i ? <div className="text-xs text-muted">{row.i}</div> : null}
      </td>
      <td className="py-2 pr-3 font-mono text-sm tabular-nums text-fg">{row.h}</td>
      <td className="py-2 pr-3 font-mono text-sm tabular-nums text-muted">{row.w}</td>
      <td className="py-2 font-mono text-sm text-muted">{row.k}</td>
    </tr>
  );
}

export function JobPanel() {
  const jobId = useMentor((s) => s.jobId);
  const leafId = useMentor((s) => s.leafId);
  const query = useMentor((s) => s.query);
  const section = useMentor((s) => s.section);
  const setJob = useMentor((s) => s.setJob);
  const setLeaf = useMentor((s) => s.setLeaf);
  const setQuery = useMentor((s) => s.setQuery);
  const setSection = useMentor((s) => s.setSection);

  const job = jobById(jobId);
  const leaf = leafById(leafId);
  const hits = searchLabor(query, section);
  const rows: CharmRow[] =
    leaf?.r ??
    job?.labor.map((r) => ({
      o: r.op,
      i: r.item ?? null,
      h: r.std,
      w: r.warr,
      k: r.skill,
      n: r.notes,
    })) ??
    [];
  const pageId = job?.pages[0]?.id ?? leaf?.id;
  const heading = job?.title ?? leaf?.t;
  const system = job?.system ?? leaf?.s;

  return (
    <div className="pointer-events-auto flex max-h-[42dvh] w-full max-w-lg flex-col overflow-hidden rounded-md border border-line bg-surface/95 sm:max-h-[68dvh]">
      <div className="border-b border-line px-4 py-3">
        <p className="font-mono text-[11px] uppercase tracking-[0.28em] text-charm">
          Operation CHARM
        </p>
        <p className="mt-1 font-display text-lg leading-none tracking-tight">
          HVAC diagnostics
        </p>
        <p className="mt-1 text-xs text-muted">
          CHARM Testing and Inspection · 4WD VIN K · printed leaves only
        </p>
        <label className="mt-3 flex min-h-11 items-center gap-2 rounded-sm border border-line bg-raised px-3">
          <Search className="size-4 text-faint" />
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search the CHARM tree"
            className="min-h-11 w-full bg-transparent text-sm text-fg outline-none placeholder:text-faint"
          />
        </label>
      </div>

      <div className="flex gap-1 overflow-x-auto border-b border-line p-2">
        <button
          type="button"
          onClick={() => setSection(null)}
          className={cn(
            "inline-flex min-h-11 shrink-0 items-center rounded-sm px-3 text-sm",
            section === null ? "bg-charm text-bg" : "bg-raised text-muted hover:text-fg",
          )}
        >
          All {LABOR.length}
        </button>
        {SECTIONS.map((s) => (
          <button
            key={s}
            type="button"
            onClick={() => setSection(s)}
            className={cn(
              "inline-flex min-h-11 shrink-0 items-center rounded-sm px-3 text-sm",
              section === s ? "bg-charm text-bg" : "bg-raised text-muted hover:text-fg",
            )}
          >
            {SECTION_SHORT[s] ?? s}
          </button>
        ))}
      </div>

      <div className="flex gap-1 overflow-x-auto border-b border-line p-2">
        {HVAC_DIAGS.filter((j) => !j.id.startsWith("hvac-pt-")).map((j) => (
          <button
            key={j.id}
            type="button"
            onClick={() => setJob(j.id)}
            className={cn(
              "inline-flex min-h-11 shrink-0 items-center rounded-sm px-3 text-sm",
              jobId === j.id ? "bg-charm text-bg" : "bg-raised text-muted hover:text-fg",
            )}
          >
            {j.title}
          </button>
        ))}
      </div>

      <div className="flex gap-1 overflow-x-auto border-b border-line p-2">
        {PINPOINT_INDEX.map((p) => (
          <button
            key={p.id}
            type="button"
            onClick={() => setJob(p.id)}
            className={cn(
              "inline-flex size-11 shrink-0 items-center justify-center rounded-sm font-mono text-sm",
              jobId === p.id ? "bg-fg text-bg" : "bg-raised text-muted hover:text-fg",
            )}
            title={`${p.letter} · ${p.title} · leaf ${p.page}`}
          >
            {p.letter}
          </button>
        ))}
      </div>

      <div className="flex gap-1 overflow-x-auto border-b border-line p-2">
        {JOBS.map((j) => (
          <button
            key={j.id}
            type="button"
            onClick={() => setJob(j.id)}
            className={cn(
              "inline-flex min-h-11 shrink-0 items-center rounded-sm px-3 text-sm",
              jobId === j.id ? "bg-fg text-bg" : "bg-raised text-muted hover:text-fg",
            )}
          >
            {j.title}
          </button>
        ))}
      </div>

      <div className="min-h-0 flex-1 overflow-y-auto">
        {query.trim() || !(job || leaf) ? (
        <ul className="divide-y divide-line border-b border-line">
          {hits.map((h) => (
            <li key={h.id}>
              <button
                type="button"
                onClick={() => setLeaf(h.id)}
                className={cn(
                  "flex min-h-11 w-full items-center justify-between gap-3 px-4 py-1.5 text-left",
                  leafId === h.id ? "bg-raised text-fg" : "text-muted hover:text-fg",
                )}
              >
                <span className="min-w-0">
                  <span className="block truncate text-sm text-fg">{h.t}</span>
                  <span className="block truncate text-xs text-faint">
                    {SECTION_SHORT[h.s] ?? h.s} · leaf {h.id}
                  </span>
                </span>
                <span className="shrink-0 font-mono text-sm tabular-nums text-charm">
                  {h.r[0]?.h}
                </span>
              </button>
            </li>
          ))}
        </ul>
        ) : null}

        {job || leaf ? (
          <div className="p-4">
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="font-mono text-xs uppercase tracking-[0.18em] text-charm">
                  CHARM · leaf {pageId}
                </p>
                <h2 className="font-display mt-1 text-xl tracking-tight">{heading}</h2>
                <p className="mt-1 text-sm text-muted">{system}</p>
              </div>
              <button
                type="button"
                onClick={() => {
                  setLeaf(null);
                  setJob(null);
                }}
                className="inline-flex size-11 items-center justify-center rounded-sm text-muted hover:text-fg"
                aria-label="Close leaf"
              >
                <X className="size-4" />
              </button>
            </div>

            <table className="mt-4 w-full text-left">
              <thead>
                <tr className="text-xs uppercase tracking-wide text-faint">
                  <th className="pb-1 font-medium">Operation</th>
                  <th className="pb-1 font-medium">Std</th>
                  <th className="pb-1 font-medium">Warr</th>
                  <th className="pb-1 font-medium">Skill</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((row, i) => (
                  <Hours key={`${row.o}-${row.i ?? i}`} row={row} />
                ))}
              </tbody>
            </table>
            {rows.find((r) => r.n) ? (
              <p className="mt-3 text-xs leading-snug text-muted">{rows.find((r) => r.n)?.n}</p>
            ) : null}

            {job?.cautions.length ? (
              <div className="mt-4 border-t border-line pt-3">
                <p className="font-mono text-xs uppercase tracking-wide text-charm">Caution</p>
                <ul className="mt-2 space-y-2 text-sm leading-snug text-muted">
                  {job.cautions.map((c) => (
                    <li key={c.slice(0, 48)}>{c}</li>
                  ))}
                </ul>
              </div>
            ) : null}

            {job?.steps?.length ? (
              <ol className="mt-4 space-y-3 border-t border-line pt-3">
                {job.steps.map((s) => (
                  <li key={s.n} className="grid grid-cols-[2rem_1fr] gap-2 text-sm">
                    <span className="font-mono text-charm">{s.n}</span>
                    <span>
                      <span className="text-fg">{s.text}</span>
                      {s.note ? <span className="mt-1 block text-xs text-muted">{s.note}</span> : null}
                    </span>
                  </li>
                ))}
              </ol>
            ) : (
              <p className="mt-4 border-t border-line pt-3 text-sm text-muted">
                Labor leaf. Service-and-repair steps print only when that CHARM page is loaded on this card.
              </p>
            )}

            {job?.install?.length ? (
              <div className="mt-4 border-t border-line pt-3">
                <p className="font-mono text-xs uppercase tracking-wide text-charm">Installation</p>
                <ul className="mt-2 space-y-2 text-sm leading-snug text-fg">
                  {job.install.map((s) => (
                    <li key={s.slice(0, 40)}>{s}</li>
                  ))}
                </ul>
              </div>
            ) : null}

            <p className="mt-5 text-xs leading-snug text-faint">
              {CHARM.tree}. {CHARM.disclaimer}
            </p>
          </div>
        ) : null}
      </div>
    </div>
  );
}
