# ADB Real-Time Performance Capture

**Target package:** `com.aistudio.fordexplorer2004.trac3d`  
**Target APK:** `ford-sport-trac-projected-center-hardening-debug.apk`  
**Purpose:** Capture CPU, memory, frame/rendering, battery/thermal, process, and logcat evidence during the physical-device verification test.

> Run these commands from a trusted computer with Android Platform Tools installed. Keep the phone unlocked and connected by USB. Replace the output directory if desired.

## 1. Establish the device and create an evidence directory

```bash
mkdir -p sport-trac-evidence
adb devices -l
adb shell getprop ro.product.model | tee sport-trac-evidence/device-model.txt
adb shell getprop ro.build.version.release | tee sport-trac-evidence/android-release.txt
adb shell getprop ro.build.version.sdk | tee sport-trac-evidence/android-api.txt
adb shell wm size | tee sport-trac-evidence/display-size.txt
adb shell wm density | tee sport-trac-evidence/display-density.txt
```

The device must appear as `device` in `adb devices -l`. Record the complete output. Do not treat `unauthorized` or `offline` as a usable test connection.

## 2. Start clean process and log capture

Before launching the app, clear the device log buffer and force-stop the package:

```bash
adb logcat -c
adb shell am force-stop com.aistudio.fordexplorer2004.trac3d
```

Start a full real-time logcat capture in a separate terminal:

```bash
adb logcat -v threadtime > sport-trac-evidence/logcat-realtime.txt
```

For a filtered second view, use:

```bash
adb logcat -v threadtime | grep -E 'com\.aistudio\.fordexplorer2004\.trac3d|AndroidRuntime|ActivityTaskManager|Choreographer|OpenGLRenderer|RenderThread|ANR|FATAL EXCEPTION'
```

Keep the full unfiltered capture. The filtered view is for quick observation only and must not replace the full file.

## 3. Launch and identify the process

In another terminal, record the launch time and start the app:

```bash
date -Ins | tee sport-trac-evidence/launch-time.txt
adb shell monkey -p com.aistudio.fordexplorer2004.trac3d 1 | tee sport-trac-evidence/launch-command.txt
sleep 3
adb shell pidof com.aistudio.fordexplorer2004.trac3d | tee sport-trac-evidence/pid-after-3s.txt
```

Set the PID in the shell for later commands. On POSIX shells:

```bash
PID="$(adb shell pidof com.aistudio.fordexplorer2004.trac3d | tr -d '\r')"
printf 'PID=%s\n' "$PID" | tee sport-trac-evidence/pid.txt
```

If `PID` is empty, the app is not running. Capture logcat and stop the performance portion; do not substitute another process.

## 4. Real-time CPU and process sampling

Run this sampling loop while reproducing the cold launch, 3D drag, component tap, and navigation checks. It samples once per second and stops with Ctrl-C:

```bash
while true; do
  printf '\n===== %s =====\n' "$(date -Ins)"
  adb shell top -b -n 1 -m 12 | grep -E 'PID|com\.aistudio\.fordexplorer2004\.trac3d'
  sleep 1
done | tee sport-trac-evidence/cpu-samples.txt
```

For a focused process snapshot when the PID is known:

```bash
while true; do
  printf '\n===== %s =====\n' "$(date -Ins)"
  adb shell top -b -n 1 -p "$PID"
  sleep 1
done | tee sport-trac-evidence/cpu-process-samples.txt
```

If the device’s `top` does not accept `-p`, use the package-filtered command instead:

```bash
while true; do
  printf '\n===== %s =====\n' "$(date -Ins)"
  adb shell top -b -n 1 | grep -E 'PID|com\.aistudio\.fordexplorer2004\.trac3d'
  sleep 1
done | tee sport-trac-evidence/cpu-process-samples.txt
```

## 5. Real-time memory sampling

Use `dumpsys meminfo` once before interaction, during the 3D screen, and after returning to the home or Lounge screen:

```bash
adb shell dumpsys meminfo com.aistudio.fordexplorer2004.trac3d | tee sport-trac-evidence/meminfo-start.txt
adb shell dumpsys meminfo com.aistudio.fordexplorer2004.trac3d | tee sport-trac-evidence/meminfo-3d.txt
adb shell dumpsys meminfo com.aistudio.fordexplorer2004.trac3d | tee sport-trac-evidence/meminfo-after-navigation.txt
```

For repeated sampling during the test:

```bash
while true; do
  printf '\n===== %s =====\n' "$(date -Ins)"
  adb shell dumpsys meminfo "$PID" | grep -E 'TOTAL|Dalvik Heap|Native Heap|Graphics|GL mtrack|Unknown|Objects'
  sleep 5
done | tee sport-trac-evidence/memory-samples.txt
```

If the PID disappears, the loop may report an error; preserve that output because process disappearance is relevant evidence.

## 6. Frame and rendering metrics

Reset the package’s graphics statistics before the test:

```bash
adb shell dumpsys gfxinfo com.aistudio.fordexplorer2004.trac3d reset
```

Then perform the test sequence: cold launch, wait 10 seconds, drag the 3D model for approximately 10 seconds, tap a component, open another screen, return to `VIEW_3D`, and wait another 10 seconds.

Capture the summary:

```bash
adb shell dumpsys gfxinfo com.aistudio.fordexplorer2004.trac3d | tee sport-trac-evidence/gfxinfo-summary.txt
```

Capture detailed frame timing data where supported:

```bash
adb shell dumpsys gfxinfo com.aistudio.fordexplorer2004.trac3d framestats | tee sport-trac-evidence/gfxinfo-framestats.txt
```

Capture the package’s profileable/debug rendering information if available:

```bash
adb shell dumpsys gfxinfo com.aistudio.fordexplorer2004.trac3d reset
adb shell dumpsys gfxinfo com.aistudio.fordexplorer2004.trac3d framestats > sport-trac-evidence/gfxinfo-framestats-after-reset.txt
```

The exact fields vary by Android release. Preserve the raw output rather than reducing it to a single average. Pay particular attention to total frames, janky frames, 90th/95th/99th-percentile frame times if shown, and any indication of missed vsync or slow UI/render work.

On devices that expose SurfaceFlinger latency for the active surface, first list surfaces:

```bash
adb shell dumpsys SurfaceFlinger --list | tee sport-trac-evidence/surface-list.txt
```

Surface names differ by Android version and device vendor. Do not guess a surface name. If the device exposes a usable application surface, capture its latency with the exact reported name:

```bash
adb shell dumpsys SurfaceFlinger --latency '<exact-surface-name>' | tee sport-trac-evidence/surface-latency.txt
```

If `--latency` is unsupported or the surface is not identifiable, record **not available** rather than treating the omission as a failure.

## 7. Battery and thermal sampling

Capture battery state before and after the interaction sequence:

```bash
adb shell dumpsys battery | tee sport-trac-evidence/battery-start.txt
adb shell dumpsys battery | tee sport-trac-evidence/battery-end.txt
```

For repeated battery sampling:

```bash
while true; do
  printf '\n===== %s =====\n' "$(date -Ins)"
  adb shell dumpsys battery | grep -E 'AC powered|USB powered|temperature|level|status|voltage|current'
  sleep 10
done | tee sport-trac-evidence/battery-samples.txt
```

Capture thermal-service information if the device exposes it:

```bash
adb shell dumpsys thermalservice | tee sport-trac-evidence/thermal-start.txt
adb shell dumpsys thermalservice | tee sport-trac-evidence/thermal-end.txt
```

If `thermalservice` is unavailable, record that the device did not expose the service through this command. Do not infer thermal safety from a missing command.

## 8. Responsiveness and ANR evidence

At the exact moment the screen appears frozen or an ANR dialog appears, capture:

```bash
date -Ins | tee -a sport-trac-evidence/failure-times.txt
adb shell pidof com.aistudio.fordexplorer2004.trac3d | tee -a sport-trac-evidence/failure-pids.txt
adb shell dumpsys activity processes | grep -A25 -B8 com.aistudio.fordexplorer2004.trac3d | tee sport-trac-evidence/activity-processes-at-failure.txt
adb shell dumpsys meminfo com.aistudio.fordexplorer2004.trac3d > sport-trac-evidence/meminfo-at-failure.txt
adb logcat -d -v threadtime > sport-trac-evidence/logcat-at-failure.txt
```

If the process is still alive and the device permits a stack dump, capture it without killing the app:

```bash
adb shell kill -3 "$PID"
sleep 2
adb logcat -d -v threadtime > sport-trac-evidence/logcat-after-sigquit.txt
```

Use `kill -3` only for evidence capture. Do not use force-stop or reboot before saving the logs.

For a complete device report after the failure:

```bash
adb bugreport sport-trac-evidence/sport-trac-bugreport.zip
```

A bugreport can be large and may take several minutes. Keep the phone connected and unlocked until it completes.

## 9. Stop the capture cleanly

After the full test sequence, stop each `while` loop with Ctrl-C. Then capture final state:

```bash
adb shell dumpsys gfxinfo com.aistudio.fordexplorer2004.trac3d > sport-trac-evidence/gfxinfo-final.txt
adb shell dumpsys meminfo com.aistudio.fordexplorer2004.trac3d > sport-trac-evidence/meminfo-final.txt
adb shell dumpsys battery > sport-trac-evidence/battery-final.txt
adb shell dumpsys thermalservice > sport-trac-evidence/thermal-final.txt 2>&1 || true
adb shell am force-stop com.aistudio.fordexplorer2004.trac3d
```

Stop the full logcat terminal with Ctrl-C only after the force-stop line has been captured. Preserve the raw files.

## 10. Minimum evidence bundle to return

The minimum useful bundle contains the following files:

| Evidence | Required file |
|---|---|
| Device identity | `device-model.txt`, `android-release.txt`, `android-api.txt` |
| Connection and installation | `adb-devices.txt` if saved, `install-output.txt`, package-path output |
| Launch timeline | `launch-time.txt`, `pid.txt`, first-frame and interaction timestamps |
| CPU/process | `cpu-samples.txt` and, if available, `cpu-process-samples.txt` |
| Memory | `meminfo-start.txt`, `meminfo-3d.txt`, `meminfo-final.txt`, and samples if captured |
| Rendering | `gfxinfo-summary.txt`, `gfxinfo-framestats.txt` |
| Battery/thermal | Battery and thermal start/end files |
| Runtime logs | Full `logcat-realtime.txt`, plus failure-specific logcat if applicable |
| Test result | Completed physical-device verification table from the protocol |

The performance capture commands measure device behavior; they do not by themselves prove model accuracy or repair correctness. Interpret them alongside the exact APK checksum and the step-by-step test record.

## References

[1]: ./PHYSICAL_DEVICE_VERIFICATION_PROTOCOL.md "Physical-Device Verification Protocol"
[2]: ./FINAL_EVIDENCE_LEDGER_2026-08-18.md "Ford Sport Trac Android App — Final Evidence Ledger"
[3]: ./PROJECT_HANDOFF.md "Ford Sport Trac Android Project Handoff"
