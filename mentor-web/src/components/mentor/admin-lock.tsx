import { useState, type FormEvent } from "react";
import { useNavigate } from "@tanstack/react-router";
import { authClient } from "@/lib/auth/client";
import { ADMIN_USER, adminEmailFromLogin } from "@/lib/mentor/admin";
import { seedAdmin } from "@/lib/mentor/seed-admin";
import { STUDIO } from "@/lib/mentor/rights";

export function AdminLock({ pending = false }: { pending?: boolean }) {
  const navigate = useNavigate();
  const [user, setUser] = useState(ADMIN_USER);
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    if (pending) return;
    setErr(null);
    setBusy(true);
    await seedAdmin();
    const { error } = await authClient.signIn.email({
      email: adminEmailFromLogin(user),
      password,
      callbackURL: "/",
    });
    if (error) {
      setBusy(false);
      setErr("That login is not the studio admin.");
      return;
    }
    await navigate({ to: "/" });
  }

  return (
    <main className="grid min-h-dvh place-items-center bg-bg p-6 text-fg">
      <form
        onSubmit={(e) => void onSubmit(e)}
        className="w-full max-w-sm space-y-5 rounded-lg border border-line bg-surface p-6"
      >
        <div>
          <p className="font-mono text-xs tracking-[0.22em] text-charm uppercase">
            {STUDIO.name}
          </p>
          <h1 className="font-display mt-2 text-2xl leading-none tracking-tight">
            Sport Trac Mentor
          </h1>
          <p className="mt-2 text-sm text-muted">
            {pending ? "Checking the lock…" : "Admin lock. Studio only."}
          </p>
        </div>
        <label className="block space-y-1.5">
          <span className="font-mono text-[11px] uppercase tracking-[0.2em] text-faint">
            Username
          </span>
          <input
            name="username"
            autoComplete="username"
            value={user}
            onChange={(e) => setUser(e.target.value)}
            disabled={pending}
            className="min-h-11 w-full rounded-md border border-line bg-raised px-3 text-sm text-fg outline-none focus:border-charm disabled:opacity-60"
          />
        </label>
        <label className="block space-y-1.5">
          <span className="font-mono text-[11px] uppercase tracking-[0.2em] text-faint">
            Password
          </span>
          <input
            name="password"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={pending}
            className="min-h-11 w-full rounded-md border border-line bg-raised px-3 text-sm text-fg outline-none focus:border-charm disabled:opacity-60"
          />
        </label>
        {err ? <p className="text-sm text-charm">{err}</p> : null}
        <button
          type="submit"
          disabled={busy || pending}
          className="inline-flex min-h-11 w-full items-center justify-center rounded-md bg-fg px-4 text-sm font-medium text-bg disabled:opacity-60"
        >
          {busy ? "Signing in…" : "Enter bay"}
        </button>
        <p className="text-xs text-faint">{STUDIO.mark}</p>
      </form>
    </main>
  );
}
