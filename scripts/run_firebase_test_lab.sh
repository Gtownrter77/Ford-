#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APP_APK="${APP_APK:-$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk}"
TEST_APK="${TEST_APK:-$ROOT_DIR/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk}"
DEVICE_MODEL="${FIREBASE_TEST_DEVICE_MODEL:-redfin}"
ANDROID_VERSION="${FIREBASE_TEST_ANDROID_VERSION:-30}"
LOCALE="${FIREBASE_TEST_LOCALE:-en}"
ORIENTATION="${FIREBASE_TEST_ORIENTATION:-portrait}"
PROJECT_ID="${FIREBASE_PROJECT_ID:-}"
RESULT_BUCKET="${FIREBASE_TEST_BUCKET:-}"

usage() {
  cat <<'USAGE'
Usage: scripts/run_firebase_test_lab.sh [options]

Runs the Android instrumentation suite in Firebase Test Lab. This script does
not build APKs; it uses the existing debug app and androidTest APK paths.

Options:
  --help                  Show this help message.
  --project PROJECT_ID    Set Firebase/GCP project ID.
  --model MODEL           Test Lab device model (default: redfin).
  --version VERSION       Android API version (default: 30).
  --locale LOCALE         Test locale (default: en).
  --orientation VALUE     portrait or landscape (default: portrait).
  --bucket GS_URI         Optional gs:// results bucket.
  --app PATH              App APK path.
  --test PATH             Instrumentation APK path.

Environment equivalents:
  FIREBASE_PROJECT_ID, FIREBASE_TEST_DEVICE_MODEL,
  FIREBASE_TEST_ANDROID_VERSION, FIREBASE_TEST_LOCALE,
  FIREBASE_TEST_ORIENTATION, FIREBASE_TEST_BUCKET, APP_APK, TEST_APK.

Example:
  FIREBASE_PROJECT_ID=my-project \\
    scripts/run_firebase_test_lab.sh --model redfin --version 30
USAGE
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --help|-h) usage; exit 0 ;;
    --project) PROJECT_ID="$2"; shift 2 ;;
    --model) DEVICE_MODEL="$2"; shift 2 ;;
    --version) ANDROID_VERSION="$2"; shift 2 ;;
    --locale) LOCALE="$2"; shift 2 ;;
    --orientation) ORIENTATION="$2"; shift 2 ;;
    --bucket) RESULT_BUCKET="$2"; shift 2 ;;
    --app) APP_APK="$2"; shift 2 ;;
    --test) TEST_APK="$2"; shift 2 ;;
    *) printf 'Unknown option: %s\n\n' "$1" >&2; usage >&2; exit 2 ;;
  esac
done

if [[ -z "$PROJECT_ID" ]]; then
  printf 'ERROR: set FIREBASE_PROJECT_ID or pass --project.\n' >&2
  exit 2
fi
if [[ ! -s "$APP_APK" ]]; then
  printf 'ERROR: app APK not found: %s\n' "$APP_APK" >&2
  exit 2
fi
if [[ ! -s "$TEST_APK" ]]; then
  printf 'ERROR: instrumentation APK not found: %s\nBuild androidTest first or pass --test PATH.\n' "$TEST_APK" >&2
  exit 2
fi
if ! command -v gcloud >/dev/null 2>&1; then
  printf 'ERROR: gcloud is not installed or not on PATH.\n' >&2
  exit 2
fi

CMD=(gcloud firebase test android run
  --project "$PROJECT_ID"
  --app "$APP_APK"
  --test "$TEST_APK"
  --device "model=$DEVICE_MODEL,version=$ANDROID_VERSION,locale=$LOCALE,orientation=$ORIENTATION"
  --type instrumentation
  --timeout 15m
  --num-flaky-test-attempts 1
)
if [[ -n "$RESULT_BUCKET" ]]; then
  CMD+=(--results-bucket "$RESULT_BUCKET")
fi

printf 'Running Firebase Test Lab instrumentation suite:\n'
printf '  project: %s\n  app: %s\n  test: %s\n  device: %s / Android %s / %s / %s\n' "$PROJECT_ID" "$APP_APK" "$TEST_APK" "$DEVICE_MODEL" "$ANDROID_VERSION" "$LOCALE" "$ORIENTATION"
exec "${CMD[@]}"
