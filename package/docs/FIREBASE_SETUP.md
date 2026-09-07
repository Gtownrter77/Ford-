# Firebase Google Sign-In Setup

This project requires a Google account before any Mentor, 3D, diagnostics, OBD-II/FORScan, manual, or saved-record feature is available. The app intentionally fails closed when Firebase is not configured.

## 1. Create or select the Firebase project

Open the [Firebase Console](https://console.firebase.google.com/) and create or select the project that will own this app. Use a project controlled by the application owner and restrict project access to the development team.

In **Project settings**, add an Android app with this exact application ID:

```text
com.aistudio.fordexplorer2004.trac3d
```

If the Firebase project uses a restricted OAuth test audience, add each tester’s Google account to the allowed test users.

## 2. Enable Google authentication

Open **Build → Authentication → Sign-in providers**, select **Google**, enable it, select the project support email, and save. Do not enable anonymous authentication for this product requirement.

## 3. Register signing fingerprints

The Firebase Android client must recognize the certificate used to sign the APK. For a debug build, run this on the build computer:

```bash
keytool -list -v \\
  -alias androiddebugkey \\
  -keystore ~/.android/debug.keystore \\
  -storepass android \\
  -keypass android
```

Copy both the SHA-1 and SHA-256 values into **Project settings → Your apps → Android app → SHA certificate fingerprints**. For a release APK, add the fingerprints for the release/upload configuration separately.

## 4. Download and place `google-services.json`

In **Project settings → Your apps**, select the Android app and choose **Download google-services.json**. Place the downloaded file at this exact path in the repository:

```text
app/google-services.json
```

The file must be inside the `app` module, next to the module’s Gradle configuration. Do not rename it. Do not commit it to a public repository if the project’s policy treats Firebase configuration as private.

## 5. Obtain `GOOGLE_WEB_CLIENT_ID`

In Firebase Console, open **Project settings → General → Your apps** and inspect the **Web API key/OAuth client configuration**, or open **Google Cloud Console → APIs & Services → Credentials** for the same project. Find the OAuth 2.0 client whose application type is **Web application**. Copy its client ID, which normally ends with:

```text
.apps.googleusercontent.com
```

Do not use the Android OAuth client ID for `GOOGLE_WEB_CLIENT_ID`. Credential Manager uses the server/web client ID so Firebase can validate the Google ID token.

## 6. Configure the project Secret

Create or update the project’s `.env`/Secrets entry without committing the actual value:

```text
GOOGLE_WEB_CLIENT_ID=your-web-client-id.apps.googleusercontent.com
```

The repository’s `.env.example` contains only a placeholder. Keep the real value in the configured Secrets panel or local untracked `.env` file. Never paste the client secret, private key, refresh token, or Google password into source code. The OAuth client ID itself is not a password, but it must still match the Firebase project used by `google-services.json`.

## 7. Rebuild and verify locally

From the repository root, rebuild the debug APK:

```bash
./gradlew testDebugUnitTest assembleDebug --no-daemon
```

Confirm that:

- `app/google-services.json` exists and is not the wrong project’s file.
- `GOOGLE_WEB_CLIENT_ID` is non-empty and is a Web application client ID.
- The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.
- A fresh install displays only the Google sign-in gate.
- Cancelling sign-in does not expose the app shell.
- A successful sign-in opens the protected shell.

## 8. Physical-device verification

Install the rebuilt APK on the test phone and complete the authentication cases in [the physical-device test plan](docs/PHYSICAL_DEVICE_TEST_PLAN.md). Test first with a dedicated tester account. Redact email addresses, tokens, VINs, and personal information from any shared logs or screenshots.

## Troubleshooting

| Symptom | Likely cause | Corrective action |
|---|---|---|
| `Firebase Auth is not configured` | Missing or mismatched `google-services.json` | Download the Android file from the correct Firebase project and place it under `app/`. |
| `Google web client ID is not configured` | Missing placeholder replacement or Secrets entry | Set `GOOGLE_WEB_CLIENT_ID` to the Web OAuth client ID and rebuild. |
| Google sign-in returns developer/configuration error | Package name or certificate fingerprint mismatch | Verify the application ID and add the debug/release SHA-1 and SHA-256 fingerprints. |
| Account is not allowed | OAuth test audience is restricted | Add the tester account to the project’s permitted test users. |
| Sign-in works, but a different project appears | APK and client ID point to different Firebase projects | Download both configuration values from the same Firebase project. |

## Security boundary

Firebase authentication proves account identity; it does not prove that the truck is safe to drive or that a repair is authorized. The Mentor must continue to require measurements, show source evidence, and apply stop-and-tow rules for safety-critical symptoms.
