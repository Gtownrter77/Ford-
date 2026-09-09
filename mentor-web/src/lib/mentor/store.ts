import { create } from "zustand";
import { persist } from "zustand/middleware";
import { CARDS } from "./book";
import type { PaintId } from "./scale";

type MentorState = {
  paint: PaintId;
  autoOrbit: boolean;
  jobId: string | null;
  leafId: string | null;
  query: string;
  section: string | null;
  setPaint: (paint: PaintId) => void;
  stopOrbit: () => void;
  setJob: (jobId: string | null) => void;
  setLeaf: (leafId: string | null) => void;
  setQuery: (query: string) => void;
  setSection: (section: string | null) => void;
};

export const useMentor = create<MentorState>()(
  persist(
    (set) => ({
      paint: "oxford",
      autoOrbit: false,
      jobId: "hvac-inspect",
      leafId: "1759",
      query: "",
      section: "Heating and Air Conditioning",
      setPaint: (paint) => set({ paint }),
      stopOrbit: () => set({ autoOrbit: false }),
      setJob: (jobId) => {
        const job = CARDS.find((j) => j.id === jobId);
        set({
          jobId,
          leafId: job?.pages[0]?.id ?? null,
          autoOrbit: false,
        });
      },
      setLeaf: (leafId) => {
        const job = CARDS.find((j) => j.pages.some((p) => p.id === leafId));
        set({ leafId, jobId: job?.id ?? null, autoOrbit: false });
      },
      setQuery: (query) => set({ query }),
      setSection: (section) => set({ section }),
    }),
    {
      name: "trac-mentor-vr-v4",
      partialize: (s) => ({
        paint: s.paint,
        jobId: s.jobId,
        leafId: s.leafId,
        section: s.section,
      }),
    },
  ),
);
