import { create } from "zustand";
import { persist } from "zustand/middleware";
import { CARDS } from "./book";
import type { PaintId } from "./scale";

export type Desk = "diag" | "pin" | "parts" | "engine" | "tree";

function deskFor(jobId: string | null): Desk {
  if (!jobId) return "tree";
  if (jobId.startsWith("engine-")) return "engine";
  if (jobId.startsWith("hvac-pt-")) return "pin";
  if (jobId.startsWith("hvac-")) return "diag";
  return "parts";
}

type MentorState = {
  paint: PaintId;
  autoOrbit: boolean;
  jobId: string | null;
  leafId: string | null;
  query: string;
  section: string | null;
  desk: Desk;
  setPaint: (paint: PaintId) => void;
  stopOrbit: () => void;
  setJob: (jobId: string | null) => void;
  setLeaf: (leafId: string | null) => void;
  setQuery: (query: string) => void;
  setSection: (section: string | null) => void;
  setDesk: (desk: Desk) => void;
};

export const useMentor = create<MentorState>()(
  persist(
    (set, get) => ({
      paint: "oxford",
      autoOrbit: false,
      jobId: "hvac-inspect",
      leafId: "1759",
      query: "",
      section: "Heating and Air Conditioning",
      desk: "diag",
      setPaint: (paint) => set({ paint }),
      stopOrbit: () => set({ autoOrbit: false }),
      setJob: (jobId) => {
        const job = CARDS.find((j) => j.id === jobId);
        set({
          jobId,
          leafId: job?.pages[0]?.id ?? null,
          autoOrbit: false,
          desk: deskFor(jobId),
        });
      },
      setLeaf: (leafId) => {
        const job = CARDS.find((j) => j.pages.some((p) => p.id === leafId));
        set({
          leafId,
          jobId: job?.id ?? null,
          autoOrbit: false,
          desk: job ? deskFor(job.id) : "tree",
        });
      },
      setQuery: (query) => set({ query, desk: "tree" }),
      setSection: (section) => set({ section, desk: "tree" }),
      setDesk: (desk) => {
        if (desk === "engine" && !get().jobId?.startsWith("engine-")) {
          const job = CARDS.find((j) => j.id === "engine-vin-k");
          set({
            desk,
            jobId: "engine-vin-k",
            leafId: job?.pages[0]?.id ?? "7934",
            section: "Engine, Cooling and Exhaust",
            autoOrbit: false,
          });
          return;
        }
        set({ desk });
      },
    }),
    {
      name: "trac-mentor-vr-v6",
      partialize: (s) => ({
        paint: s.paint,
        jobId: s.jobId,
        leafId: s.leafId,
        section: s.section,
        desk: s.desk,
      }),
    },
  ),
);
