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
