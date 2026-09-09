#!/usr/bin/env node
import { cpSync, existsSync, mkdirSync, rmSync, writeFileSync } from "node:fs";
import { spawnSync } from "node:child_process";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = dirname(dirname(fileURLToPath(import.meta.url)));
const www = join(root, "android-www");
const sdk = process.env.ANDROID_HOME || "/opt/android-sdk";
const jdk = process.env.JAVA_HOME || "/opt/jdk/jdk-21.0.12.1+1";
const androidDir = join(root, "android");

function run(cmd, args, cwd = root, env = {}) {
  const r = spawnSync(cmd, args, {
    cwd,
    stdio: "inherit",
    env: { ...process.env, ANDROID_HOME: sdk, ANDROID_SDK_ROOT: sdk, JAVA_HOME: jdk, PATH: `${jdk}/bin:${process.env.PATH ?? ""}`, ...env },
  });
  if (r.status !== 0) process.exit(r.status ?? 1);
}

run(join(root, "node_modules", ".bin", "vite"), ["build"], root, { ANDROID_SPA: "1" });

const candidates = [
  join(root, "dist", "client"),
  join(root, "dist"),
  join(root, ".output", "public"),
  join(root, ".vercel", "output", "static"),
];
const src = candidates.find(
  (p) => existsSync(join(p, "index.html")) || existsSync(join(p, "_shell.html")),
);
if (!src) {
  console.error("SPA build produced no HTML shell. Looked in:", candidates.join(", "));
  process.exit(1);
}

rmSync(www, { recursive: true, force: true });
mkdirSync(www, { recursive: true });
cpSync(src, www, { recursive: true });
if (!existsSync(join(www, "index.html")) && existsSync(join(www, "_shell.html"))) {
  cpSync(join(www, "_shell.html"), join(www, "index.html"));
}
mkdirSync(androidDir, { recursive: true });
writeFileSync(join(androidDir, "local.properties"), `sdk.dir=${sdk}\n`);

if (!existsSync(join(androidDir, "app"))) {
  run("npx", ["cap", "add", "android"]);
}
run("npx", ["cap", "sync", "android"]);
run("./gradlew", ["assembleDebug"], androidDir);
const apk = join(androidDir, "app", "build", "outputs", "apk", "debug", "app-debug.apk");
if (!existsSync(apk)) {
  console.error("gradle did not emit a debug APK");
  process.exit(1);
}
mkdirSync(join(root, "artifacts"), { recursive: true });
mkdirSync(join(root, "public"), { recursive: true });
cpSync(apk, join(root, "artifacts", "SportTracMentor.apk"));
cpSync(apk, join(root, "public", "SportTracMentor.apk"));
console.log("APK ready: artifacts/SportTracMentor.apk");
