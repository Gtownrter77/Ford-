# Share with a family tester

Repo is public: https://github.com/Gtownrter77/Ford-

There is no Play Store build yet. The phone install is a debug APK.

## Fastest path for you

1. On the machine that already builds this app:
   `./gradlew :app:assembleDebug`
2. Send `app/build/outputs/apk/debug/app-debug.apk` by text, Drive, or email.
3. On the phone: Settings → Security → allow install from that app (Files / Drive / Gmail).
4. Open the APK and install. Package id: `com.aistudio.fordexplorer2004.trac3d`.

## If you want GitHub to build it

1. Open https://github.com/Gtownrter77/Ford-/actions/workflows/tester-apk.yml
2. Run workflow → main.
3. Download the `mentor-sport-trac-debug` artifact and forward the APK.

GitHub artifact downloads need a GitHub login. A Drive link does not.

## What to tell him to try

- Open 3D view, spin the truck, tap a wheel and a lug nut.
- Ask Mentor: coolant capacity, transfer case fluid, rear axle 75W-90.
- Confirm Bank 2 lean / misfire notes stay inside the Sport Trac catalog.
- Screenshot anything that crashes or looks the wrong size.

Feedback issue: https://github.com/Gtownrter77/Ford-/issues (create one titled Family tester notes).
