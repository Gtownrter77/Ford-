import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { Headphones, RotateCcw } from "lucide-react";
import { MentorBay, xrStore } from "@/components/bay/mentor-bay";
import { JobPanel } from "@/components/mentor/job-panel";
import { PAINTS, type PaintId } from "@/lib/mentor/scale";
import { STUDIO } from "@/lib/mentor/rights";
import { useMentor } from "@/lib/mentor/store";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/")({ component: Home });

function Home() {
  const paint = useMentor((s) => s.paint);
  const setPaint = useMentor((s) => s.setPaint);
  const [xrOk, setXrOk] = useState(false);
  const [vrMsg, setVrMsg] = useState<string | null>(null);

  useEffect(() => {
    let alive = true;
    const xr = navigator as Navigator & {
      xr?: { isSessionSupported: (m: string) => Promise<boolean> };
    };
    xr.xr
      ?.isSessionSupported("immersive-vr")
      .then((ok) => {
        if (alive) setXrOk(ok);
      })
      .catch(() => undefined);
    return () => {
      alive = false;
    };
  }, []);

  async function enterVr() {
    setVrMsg(null);
    if (!xrOk) {
      setVrMsg(
        "No headset on this screen. Open this bay in a Quest browser, or orbit here at 1:1.",
      );
      return;
    }
    try {
      await xrStore.enterVR();
    } catch {
      setVrMsg("Headset refused the session. Allow VR and try again.");
    }
  }

  return (
    <main className="relative h-dvh w-full overflow-hidden bg-bg text-fg">
      <h1 className="sr-only">2004 Sport Trac Mentor — CHARM 4WD VIN K</h1>
      <MentorBay />

      <div className="pointer-events-none absolute inset-0 z-10 flex flex-col justify-between gap-3 p-4 sm:p-6">
        <header className="flex items-start justify-between gap-4">
          <div className="max-w-[22rem]">
            <p className="font-mono text-xs tracking-[0.22em] text-charm uppercase">
              {STUDIO.name}
            </p>
            <p className="font-display mt-1 text-2xl leading-none tracking-tight sm:text-3xl">
              2004 Sport Trac
            </p>
            <p className="mt-2 text-sm text-muted">
              CHARM HVAC · 4WD VIN K · all rights reserved
            </p>
          </div>
        </header>

        <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <JobPanel />

          <div className="flex flex-col gap-3 sm:items-end">
            <div className="pointer-events-auto flex flex-wrap items-center gap-2">
              {(Object.keys(PAINTS) as PaintId[]).map((id) => (
                <button
                  key={id}
                  type="button"
                  onClick={() => setPaint(id)}
                  className={cn(
                    "inline-flex min-h-11 items-center gap-2 rounded-md border px-3 text-sm transition-opacity duration-150",
                    paint === id
                      ? "border-fg bg-raised text-fg"
                      : "border-line bg-surface/80 text-muted hover:opacity-80",
                  )}
                >
                  <span
                    className="size-3 rounded-full border border-line"
                    style={{ background: PAINTS[id].color }}
                    aria-hidden
                  />
                  {PAINTS[id].name}
                </button>
              ))}
            </div>
            <div className="pointer-events-auto flex flex-wrap items-center gap-2">
              <button
                type="button"
                onClick={() => {
                  useMentor.setState({ autoOrbit: true });
                }}
                className="inline-flex min-h-11 items-center gap-2 rounded-md border border-line bg-surface/80 px-4 text-sm text-fg"
              >
                <RotateCcw className="size-4" />
                Orbit
              </button>
              <button
                type="button"
                onClick={() => void enterVr()}
                className="inline-flex min-h-11 items-center gap-2 rounded-md bg-fg px-4 text-sm font-medium text-bg"
              >
                <Headphones className="size-4" />
                Enter VR
              </button>
            </div>
            {vrMsg ? (
              <p className="max-w-xs text-sm text-muted">{vrMsg}</p>
            ) : (
              <p className="hidden max-w-xs text-xs text-faint sm:block">
                {STUDIO.mark}
              </p>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}
