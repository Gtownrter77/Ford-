# Ford Sport Trac Mentor — Physical-Device Test Plan

## Current readiness

The debug APK has been generated at `app/build/outputs/apk/debug/app-debug.apk`. The Android project builds successfully with `testDebugUnitTest` and `assembleDebug`. This sandbox has no connected Android device or usable `adb` executable, so USB installation and Bluetooth validation must be performed from a computer with Android platform tools installed.

The repository currently does **not** contain `app/google-services.json`. Google sign-in is intentionally mandatory, so the app must not be treated as physically testable until Firebase is configured.

## 1. Firebase setup

In Firebase Console, create or open the project for the app and add an Android application with this package name:

```text
com.aistudio.fordexplorer2004.trac3d
```

Enable **Authentication → Sign-in method → Google**. Add the tester’s Google account under the Firebase project’s authorized/tester settings if the project uses a restricted test audience.

Register the SHA-1 and SHA-256 fingerprints for the signing key used by the APK. For the debug APK built from this repository, obtain them on the build computer with:

```bash
keytool -list -v \\
  -alias androiddebugkey \\
  -keystore ~/.android/debug.keystore \\
  -storepass android \\
  -keypass android
```

Download `google-services.json` and place it at:

```text
app/google-services.json
```

Copy the Firebase Web OAuth client ID into the project Secrets configuration as:

```text
GOOGLE_WEB_CLIENT_ID=your-firebase-web-client-id.apps.googleusercontent.com
```

Rebuild the APK after these values are configured. Do not commit `google-services.json` or real secrets to Git.

## 2. Install on a physical Android phone

On the test phone:

1. Open **Settings → About phone** and tap **Build number** seven times to enable Developer options.
2. Open **Developer options** and enable **USB debugging**.
3. Connect the phone with a data-capable USB cable.
4. Accept the RSA debugging prompt on the phone.

On the computer used for testing, install Android Platform Tools and verify the connection:

```bash
adb devices
```

The device should appear as `device`, not `unauthorized` or `offline`. Install the APK:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

If an older package conflicts with the test build, remove only this app and reinstall:

```bash
adb uninstall com.aistudio.fordexplorer2004.trac3d
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 3. Authentication acceptance tests

Record the phone model, Android version, APK build date, Firebase project, tester account, and result for each case.

| ID | Test | Expected result |
|---|---|---|
| AUTH-01 | Fresh install with no session | Only the Google sign-in screen is visible. Mentor, 3D, Diagnostics, and FORScan are unreachable. |
| AUTH-02 | Cancel Google sign-in | User remains at the sign-in screen; no protected screen opens. |
| AUTH-03 | Successful Google sign-in | Firebase authenticates the account and the protected app shell opens. |
| AUTH-04 | Kill and relaunch app | The valid provider session restores and the app opens for the same account. |
| AUTH-05 | Sign out | The app returns to the sign-in gate and does not show the prior account’s protected content. |
| AUTH-06 | Account switch | A different Google account signs in and cannot see the first account’s records. |
| AUTH-07 | Revoke access or invalidate the session | The app fails closed and returns to the sign-in gate. |
| AUTH-08 | Offline first launch with no cached session | The app does not allow anonymous access and clearly reports the unavailable sign-in state. |

## 4. Core app and safety tests

After successful sign-in, test each protected area without working on the physical truck:

- Open the 3D model and confirm the app remains responsive during model loading.
- Open Mentor and verify safety language, source/citation display, and the absence of autonomous repair authorization.
- Open Diagnostics and confirm measured, simulated, and unavailable states are distinguishable.
- Verify A/C and timing-chain stop/tow guidance is visible for the relevant critical symptoms.
- Confirm no diagnostic screen claims the physical truck is safe to drive without inspection.

## 5. OBD-II/FORScan Bluetooth test

Perform this only with the vehicle safely parked, ignition state controlled according to the adapter manufacturer, and a qualified technician present.

1. Pair the classic-Bluetooth ELM327 adapter in Android Bluetooth settings.
2. Open the app’s FORScan/OBD area.
3. Connect to the paired adapter.
4. Confirm the adapter name and connection state are shown.
5. Read standard PIDs such as RPM and coolant temperature.
6. Read DTCs without clearing them.
7. Export or copy the technician handoff data if available.
8. Compare displayed values with an independent scan tool where possible.

Do **not** test module programming, security access, actuator activation, automatic code clearing, or any command that could alter vehicle state. Do not perform roadside timing-chain, oil-pressure, or A/C repairs based solely on app output.

## 6. Evidence to return after testing

Return:

- Phone model and Android version.
- APK version/build date and Git commit.
- Firebase sign-in result, with the email address redacted if shared publicly.
- Results for AUTH-01 through AUTH-08.
- Screenshots of any failure, excluding private tokens or account details.
- Bluetooth adapter make/model and pairing result.
- OBD PID/DTC results, with VIN and personal information redacted.
- Any crash log captured with:

```bash
adb logcat -d -t 2000 > ford-sport-trac-logcat.txt
```

A successful build is not the same as physical-device acceptance. The release should remain marked **physical validation pending** until the authentication, protected-route, Bluetooth, and safety checks above are completed.

## 3A. Step-by-step authentication procedures

### AUTH-01 — Fresh install with no session

1. Uninstall the app or clear only this app’s data.
2. Install the newly built APK.
3. Launch the app without first opening another screen.
4. Confirm the screen says **Account sign-in required**.
5. Confirm **Continue with Google** is visible.
6. Attempt to use back navigation and any visible app controls.

**Expected result:** The Google gate is the only reachable app state. The protected navigation bar and protected screens are not visible. No anonymous record or Mentor session is created.

### AUTH-02 — Cancelled sign-in

1. From the fresh-install gate, tap **Continue with Google**.
2. When the Google account sheet appears, press cancel or back.
3. Wait for the sign-in operation to finish.
4. Inspect the resulting screen.

**Expected result:** The app remains at the sign-in gate. The failure message may identify cancellation, but the protected app shell does not open and no account is treated as authenticated.

### AUTH-03 — Successful sign-in

1. Tap **Continue with Google**.
2. Select the approved tester account.
3. Complete any Google consent step.
4. Wait until the provider sheet closes.
5. Confirm the protected shell appears.
6. Open 3D Model, Manual, Diagnostics, and another protected section.

**Expected result:** Firebase accepts the Google identity and the protected shell opens. Each protected route is reachable only after the authenticated session is established.

### AUTH-04 — Session restore after cold start

1. Complete AUTH-03.
2. Record the signed-in account label without sharing the full email publicly.
3. Force-stop the app using Android system settings.
4. Relaunch the app while online.
5. Repeat after a normal device restart if available.

**Expected result:** The provider-restored session opens the protected shell for the same account. The app does not expose protected content to an anonymous intermediate state.

### AUTH-05 — Sign-out

1. Complete AUTH-03.
2. Use the app’s sign-out action when available; if the current build has no visible sign-out action, record this as a product gap rather than simulating success.
3. Confirm the app returns to the sign-in gate.
4. Press back and relaunch the app.

**Expected result:** The prior session is removed from the app state. The protected shell is not available until another successful Google sign-in. If no sign-out control is exposed, mark AUTH-05 **blocked**, not passed.

### AUTH-06 — Account switching and data separation

1. Complete AUTH-03 with tester account A.
2. Create or inspect a non-sensitive test record under account A.
3. Sign out.
4. Sign in with tester account B.
5. Inspect the same record location.

**Expected result:** Account B cannot see account A’s records. If records are not yet account-scoped in storage, mark this test **failed** and do not describe the app as multi-user safe.

### AUTH-07 — Revoked or invalid session

1. Complete AUTH-03.
2. Revoke the test account’s app access from the Google account security page or invalidate the Firebase session using the approved test procedure.
3. Force-stop and relaunch the app.
4. Attempt to open a protected route.

**Expected result:** The app fails closed and returns to the sign-in gate or shows a clear reauthentication error. It must not treat stale local UI state as proof of identity.

### AUTH-08 — Offline startup without a valid session

1. Uninstall or clear app data.
2. Disable network connectivity using the phone’s normal controls.
3. Launch the app.
4. Attempt to proceed without signing in.
5. Restore network connectivity after recording the result.

**Expected result:** No anonymous access is granted. The app remains at the gate and explains that Google sign-in cannot complete while offline.

## 3B. Tester sign-off checklist

| Check | Tester initials | Date/time | Result | Notes |
|---|---|---|---|---|
| Firebase project and OAuth client verified |  |  | Pass / Fail |  |
| Fresh install blocks anonymous access |  |  | Pass / Fail |  |
| Cancelled sign-in remains blocked |  |  | Pass / Fail |  |
| Successful Google sign-in opens shell |  |  | Pass / Fail |  |
| Cold-start session restore verified |  |  | Pass / Fail |  |
| Sign-out returns to gate |  |  | Pass / Blocked |  |
| Account switching/data separation verified |  |  | Pass / Fail |  |
| Revoked session fails closed |  |  | Pass / Fail |  |
| Offline startup fails closed |  |  | Pass / Fail |  |
| 3D model remains responsive |  |  | Pass / Fail |  |
| Mentor safety/source behavior verified |  |  | Pass / Fail |  |
| Bluetooth ELM327 connects safely |  |  | Pass / Fail |  |
| Standard PIDs and DTC read verified |  |  | Pass / Fail |  |
| No unsafe write/programming command used |  |  | Confirmed |  |

**Tester name:** ______________________________  
**Phone model / Android version:** ______________________________  
**APK commit:** ______________________________  
**Firebase project:** ______________________________  
**Overall decision:** Pass / Fail / Physical validation pending  
**Tester signature:** ______________________________  
