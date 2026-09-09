export const ADMIN_USER = "gtownrter77";
export const ADMIN_EMAIL = "gtownrter77@guru-studios.atl";

export function adminEmailFromLogin(raw: string): string {
  const v = raw.trim().toLowerCase();
  if (!v) return "";
  if (v.includes("@")) return v;
  return `${v}@guru-studios.atl`;
}
