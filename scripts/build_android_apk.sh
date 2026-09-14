#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SDK_ROOT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/android-sdk}}"
BUILD_MODE="termux"
ACCEPT_LICENSES="auto"
RUN_TESTS=1

usage() {
  cat <<'USAGE'
Usage: scripts/build_android_apk.sh [options]

Build the Ford Sport Trac Mentor APK with the correct toolchain checks.

Options:
  --termux       Build the phone-compatible APK (default); skips native llama C++.
  --full         Build the complete APK; requires a compatible desktop/CI NDK.
  --no-tests     Skip unit tests and assemble only.
  --accept       Accept available SDK licenses non-interactively when sdkmanager exists.
  --no-accept    Never invoke sdkmanager license handling.
  -h, --help     Show this help.

Examples:
  scripts/build_android_apk.sh
  scripts/build_android_apk.sh --termux
  scripts/build_android_apk.sh --full --accept
USAGE
}

while (($#)); do
  case "$1" in
    --termux) BUILD_MODE="termux" ;;
    --full) BUILD_MODE="full" ;;
    --no-tests) RUN_TESTS=0 ;;
    --accept) ACCEPT_LICENSES="yes" ;;
    --no-accept) ACCEPT_LICENSES="no" ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown option: $1" >&2; usage >&2; exit 2 ;;
  esac
  shift
done

fail() { echo "FAIL: $*" >&2; exit 1; }
pass() { echo "PASS: $*"; }

cd "$ROOT_DIR"
[[ -x ./gradlew ]] || fail "gradlew is missing or not executable. Run: chmod +x gradlew"
command -v java >/dev/null 2>&1 || fail "Java is missing. Install OpenJDK 17."
[[ -d "$SDK_ROOT" ]] || fail "Android SDK not found at $SDK_ROOT. Set ANDROID_HOME or ANDROID_SDK_ROOT."
[[ -f "$SDK_ROOT/platforms/android-36/android.jar" ]] || fail "Android API 36 is missing: $SDK_ROOT/platforms/android-36/android.jar"

export ANDROID_HOME="$SDK_ROOT"
export ANDROID_SDK_ROOT="$SDK_ROOT"

SDKMANAGER=""
for candidate in \
  "${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager" \
  "${ANDROID_SDK_ROOT}/cmdline-tools/bin/sdkmanager" \
  "${ANDROID_SDK_ROOT}/tools/bin/sdkmanager"; do
  if [[ -x "$candidate" ]]; then SDKMANAGER="$candidate"; break; fi
done

if [[ "$ACCEPT_LICENSES" == "yes" || ( "$ACCEPT_LICENSES" == "auto" && -n "$SDKMANAGER" ) ]]; then
  [[ -n "$SDKMANAGER" ]] || fail "--accept was requested, but sdkmanager was not found. Install Android command-line tools or use --no-accept."
  echo "Accepting licenses exposed by sdkmanager..."
  yes | "$SDKMANAGER" --licenses >/tmp/ford_sport_trac_sdk_licenses.log || {
    cat /tmp/ford_sport_trac_sdk_licenses.log >&2
    fail "sdkmanager license acceptance failed."
  }
  pass "sdkmanager licenses accepted"
elif [[ "$ACCEPT_LICENSES" == "auto" ]]; then
  echo "INFO: sdkmanager not found; no licenses were fabricated or changed."
fi

if [[ "$BUILD_MODE" == "full" ]]; then
  [[ -d "$SDK_ROOT/ndk/28.2.13676358" ]] || fail "Full build requires NDK 28.2.13676358. Use a desktop/CI SDK Manager, or run the default --termux profile."
  GRADLE_ARGS=(testDebugUnitTest assembleDebug)
  pass "complete build selected"
else
  GRADLE_ARGS=(testDebugUnitTest assembleDebug -PtermuxBuild=true)
  pass "Termux profile selected; native offline llama engine is excluded"
fi

if (( RUN_TESTS == 0 )); then
  GRADLE_ARGS=(assembleDebug "${GRADLE_ARGS[@]:2}")
fi

./gradlew "${GRADLE_ARGS[@]}" --no-daemon
APK="$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk"
[[ -s "$APK" ]] || fail "Gradle completed but APK was not found at $APK"

printf '\nBUILD COMPLETE\nAPK: %s\nSIZE: ' "$APK"
stat -c '%s bytes' "$APK" 2>/dev/null || wc -c < "$APK"
