import { createFileRoute } from "@tanstack/react-router";
import { AdminLock } from "@/components/mentor/admin-lock";
import { seedAdmin } from "@/lib/mentor/seed-admin";

export const Route = createFileRoute("/login")({
  beforeLoad: async () => {
    await seedAdmin();
  },
  component: Login,
});

function Login() {
  return <AdminLock />;
}
