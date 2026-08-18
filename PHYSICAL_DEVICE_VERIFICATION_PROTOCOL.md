# Physical-Device Verification Protocol

**Target artifact:** `ford-sport-trac-projected-center-hardening-debug.apk`  
**Expected SHA-256:** `e61a2eb5d10d4870eac9d82dd58e6c9b356e2ca5ac3d944a41747116becf3cae`  
**Application ID:** `com.aistudio.fordexplorer2004.trac3d`  
**Purpose:** Determine whether the latest sandbox-verified APK launches and remains responsive on the physical Android device.

> **Important:** This protocol defines a test. It does not assume success. Record observed results exactly, including failures, delays, crashes, or missing evidence.

## 1. Required equipment and prerequisites

Use the target physical Android phone, a USB data cable, a computer with Android Platform Tools installed, and at least 2 GB of free device storage. Charge the phone to at least 40 percent, disable battery-saver mode for the test, and close unrelated apps. Do not perform the test while the phone is unusually hot.

Enable **Developer options** and **USB debugging** on the phone. If the device asks whether to authorize the computer, accept the authorization only if the computer is trusted. Keep the phone unlocked during installation and the first-launch portion of the test.

Copy the APK to the computer and verify its checksum before installing:

```bash
sha256sum ford-sport-trac-projected-center-hardening-debug.apk
```

The output must be:

```text
e61a2eb5d10d4870eac9d82dd58e6c9b356e2ca5ac3d944a41747116becf3cae
```

If the checksum differs, stop. Do not install that file as the test artifact.

## 2. Establish the device connection

Connect the phone by USB and run:

```bash
adb devices
adb shell getprop ro.product.model
adb shell getprop ro.build.version.release
adb shell getprop ro.build.version.sdk
```

The device must appear as `device`, not `unauthorized` or `offline`. Record the model, Android release, and API level. If `adb devices` does not show a usable device, record that fact and do not call the test complete.

Before installation, clear old evidence from the computer-side capture files:

```bash
adb logcat -c
```

If this is a clean test of the latest build, uninstall any prior copy first:

```bash
adb uninstall com.aistudio.fordexplorer2004.trac3d
```

An uninstall error stating that the package is not installed is acceptable. Record whether an older version was present.

## 3. Install and verify the package

Install the exact APK:

```bash
adb install -r ford-sport-trac-projected-center-hardening-debug.apk
```

The command must return `Success`. Confirm the package is installed:

```bash
adb shell pm path com.aistudio.fordexplorer2004.trac3d
adb shell dumpsys package com.aistudio.fordexplorer2004.trac3d | grep -E 'versionCode|versionName|targetSdk|minSdk'
```

If installation fails, save the complete command output and stop the behavioral test. Installation failure is a packaging or device-compatibility result, not an ANR result.

## 4. Capture a clean cold launch

Start log capture before launching the app:

```bash
adb logcat -v threadtime > sport-trac-cold-launch.log
```

In a second terminal, force-stop and launch the package:

```bash
adb shell am force-stop com.aistudio.fordexplorer2004.trac3d
adb shell monkey -p com.aistudio.fordexplorer2004.trac3d 1
```

Record these timestamps using a stopwatch or shell timestamps:

| Checkpoint | Required observation |
|---|---|
| T0 | Launch command issued. |
| T1 | First visible app window appears. |
| T2 | First frame is visually stable and no loading/blank loop is continuing. |
| T3 | A tap or swipe receives a response. |
| T4 | The app remains responsive for 60 seconds. |

For a practical pass, T1 should occur without an indefinite blank screen, T2 should follow without repeated closing/reopening, and the app should respond by T3. Do not invent a numerical performance pass threshold if the device does not provide measured frame data; record the observed elapsed times instead.

During the first launch, do not tap repeatedly. If the app appears frozen, wait at least 30 seconds while watching the screen and logcat. Repeated taps can make an ordinary slow launch look like an interaction failure.

At the end of the cold-launch observation, stop log capture with Ctrl-C. Save the file unchanged.

## 5. First-screen and navigation checks

The source default is `VIEW_3D`, so the first screen should be the 3D training view rather than a claim of a completed Lounge-first flow. Record the actual screen shown.

Perform each check once, waiting up to 10 seconds for the UI to respond:

| ID | Action | Pass condition |
|---|---|---|
| N1 | Observe the initial screen without touching it for 10 seconds. | The process remains open and the UI does not repeatedly restart. |
| N2 | Use the visible navigation control to open the Lounge or home destination if available. | The destination opens without closing the app. |
| N3 | Open the Part Store. | The Part Store screen appears and remains interactive. |
| N4 | Open Diagnostics or the A/C Workbench. | The selected screen appears without an ANR or crash. |
| N5 | Return to the 3D screen. | The 3D screen returns without a blank or repeated initialization loop. |

Record unavailable controls as **not testable**, not as passes or failures.

## 6. Procedural 3D interaction checks

On the 3D screen, perform the following in order:

1. Wait 10 seconds without interaction. Record whether the model remains visible and whether the process closes.
2. Drag horizontally across the model. Pass condition: the camera view changes or the renderer visibly responds.
3. Drag vertically across the model. Pass condition: the camera pitch or view changes without a crash.
4. Use the visible zoom control or gesture, if present. Record the actual behavior.
5. Tap the center of a visibly identifiable component. Pass condition: a selection, callout, detail panel, or other documented response occurs.
6. Tap an empty area. Record whether the selection clears, remains, or changes.
7. If a mentor, explode, or explicit animation control is visible, activate only one control at a time and wait for its result.

Do not describe a visually simple procedural shape as a complete physical vehicle replica. This test verifies runtime behavior and interaction only.

## 7. A/C, Mentor, audio, and Part Store smoke checks

These are smoke checks, not professional repair validation. Do not run the vehicle, open the refrigerant system, place an order, enter payment information, or treat an app recommendation as a substitute for a qualified diagnosis.

| Area | Test action | Pass condition |
|---|---|---|
| A/C Workbench | Open the A/C workflow and select a visible diagnostic or repair step. | Screen and step content render without crash. |
| Mentor | Open the Mentor UI from a selected component or available entry point. | Dialog or screen opens; controls respond. |
| Audio diagnosis | Open the audio-analysis path only if the device permission flow is shown. | Permission and UI flow behave predictably; no classification accuracy claim is made. |
| Part Store | Open the catalog, change ranking preference, and inspect a part detail. | Local UI updates; no automatic order or payment action occurs. |

If Android asks for microphone permission, record whether it was granted or denied. Do not grant permissions unrelated to the test.

## 8. ANR, crash, and responsiveness evidence

If the app closes, freezes, displays an Application Not Responding dialog, or repeatedly restarts, do not reinstall immediately. First capture evidence:

```bash
adb shell pidof com.aistudio.fordexplorer2004.trac3d
adb shell dumpsys activity processes | grep -A20 -B5 com.aistudio.fordexplorer2004.trac3d
adb shell dumpsys meminfo com.aistudio.fordexplorer2004.trac3d
adb logcat -d -v threadtime > sport-trac-failure.log
adb bugreport sport-trac-bugreport.zip
```

If `pidof` returns nothing because the process already died, record that fact. If the phone displays an ANR dialog, photograph or screenshot the dialog and record its wording. Record the exact last action, elapsed time since launch, screen visible at failure, whether the device was hot, and whether the failure occurred on a clean install or an upgrade install.

For a crash with a stack trace, preserve the logcat lines containing `FATAL EXCEPTION`, the process name, and the first relevant `Caused by` section. Do not trim the evidence to only the final line.

## 9. Success report format

Complete one row for every test. Use only `PASS`, `FAIL`, `NOT TESTABLE`, or `NOT RUN`.

| Field | Value |
|---|---|
| APK filename |  |
| APK SHA-256 |  |
| Device model |  |
| Android release/API |  |
| Clean install or upgrade |  |
| Install result |  |
| T1 first-window time |  |
| T2 stable-frame time |  |
| T3 first responsive interaction |  |
| N1–N5 results |  |
| 3D interaction results |  |
| A/C/Mentor/Audio/Part Store results |  |
| ANR or crash observed |  |
| Logcat/bugreport filenames |  |
| Tester notes |  |

A complete report must include the checksum output, `adb devices` output, the test table, and any failure evidence. A verbal statement that “it opened” is useful but is not enough to establish a reproducible device-verification result.

## 10. Interpretation rules

A **device-verified launch pass** requires the exact checksum-matched APK to install, launch, remain open for the defined observation period, and respond to the basic checks without an ANR or crash. A failure in one optional feature does not automatically invalidate launch, but it must be recorded separately.

A **device-verified ANR resolution** requires the same test to be performed against the prior failing behavior or a documented reproduction condition, with no ANR and with preserved evidence showing the test conditions. Without that comparison, the correct wording is only that the latest APK passed or failed a particular device test.

A successful test does not upgrade the model’s scope claim. The app remains a procedural Compose Canvas training model with generated reference hardware; the device test verifies runtime behavior, not every-fastener physical accuracy, true GPU PBR, or complete repair-database coverage.

## References

[1]: ./FINAL_EVIDENCE_LEDGER_2026-08-18.md "Ford Sport Trac Android App — Final Evidence Ledger"
[2]: ./PROJECT_HANDOFF.md "Ford Sport Trac Android Project Handoff"
[3]: ./PROJECT_TEAM_HARDENING_REVIEW_SUMMARY.md "Project-Team Hardening Review Summary"
