import { createServerFn } from "@tanstack/react-start";

export const seedAdmin = createServerFn({ method: "GET" }).handler(async () => {
  const { ensureStudioAdmin } = await import("./seed-admin.server");
  return ensureStudioAdmin();
});
