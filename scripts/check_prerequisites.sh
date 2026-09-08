#!/usr/bin/env bash
set -u

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PASS_COUNT=0
FAIL_COUNT=0
WARN_COUNT=0

pass() { printf 'PASS: %s\n' "$1"; PASS_COUNT=$((PASS_COUNT + 1)); }
fail() { printf 'FAIL: %s\n' "$1"; FAIL_COUNT=$((FAIL_COUNT + 1)); }
warn() { printf 'WARN: %s\n' "$1"; WARN_COUNT=$((WARN_COUNT + 1)); }

printf 'Ford Sport Trac Mentor prerequisite check\n'
printf 'Repository: %s\n\n' "$ROOT_DIR"

pass "Firebase is not required"

if command -v adb >/dev/null 2>&1; then
  pass "adb is installed ($(command -v adb))"
  DEVICE_LINES="$(adb devices 2>/dev/null | awk 'NR > 1 && $2 == \"device\" { count++ } END { print count + 0 }')"
  if [[ "$DEVICE_LINES" -gt 0 ]]; then
    pass "$DEVICE_LINES Android device(s) connected and authorized"
  else
    warn "adb is installed but no authorized Android device is connected"
  fi
else
  warn "adb is not installed or is not on PATH"
fi

if [[ -s "$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk" ]]; then
  pass "debug APK exists"
else
  warn "debug APK is missing; run ./gradlew assembleDebug or ./gradlew assembleDebug -PtermuxBuild=true"
fi

printf '\nSummary: %d PASS, %d WARN, %d FAIL\n' "$PASS_COUNT" "$WARN_COUNT" "$FAIL_COUNT"
if [[ "$FAIL_COUNT" -gt 0 ]]; then
  printf 'Overall result: FAIL \n'
  exit 1
fi
printf 'Overall result: PASS\n'
exit 0
