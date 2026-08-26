# Build increment — 2026-08-26

**Repo:** `Gtownrter77/Ford-`  
**Goal:** leave the all-or-nothing safe shell and restore usable feature rooms without putting the 56-part Canvas on the first frame.

## What changed

| File | Change |
|---|---|
| `app/src/main/java/com/example/navigation/FeatureRoutePolicy.kt` | Per-route enablement and bounded-scene rule. |
| `app/src/main/java/com/example/ui/screens/SafeProcedural3DRoute.kt` | 3D tab starts at `SafeSceneLoadGate`. Canvas is not composed until the user taps Load. |
| `app/src/main/java/com/example/MainActivity.kt` | Removed `SAFE_SHELL_MODE`. Wired Manual, Diagnostics, Schedule, Parts. 3D uses the gated route. Room opens only for Manual/Schedule, or after the 3D gate. |
| `app/src/main/java/com/example/ui/screens/LoungeScreen.kt` | Cold-launch copy updated to match the live routes. |
| `app/src/main/java/com/example/model/FeatureReadiness.kt` | Procedural 3D note updated. |
| `app/src/test/java/com/example/navigation/FeatureRoutePolicyTest.kt` | Bounds and Room-open policy tests. |

## Live vs gated

- Live now: Lounge, Repair Manual, Diagnostics, Service Schedule, Part Store.
- 3D: tab is reachable. First frame is the gate. After Load, the scene receives one component when the filter is ALL, or up to eight when a system is selected.
- Still not live: licensed wreck GLB, OBD2 transport, live retailer order/pay, cloud Gemini unless a real key is injected.

## What this does not claim

This increment is source wiring. It is not a phone ANR result. Install the debug APK and run `PHYSICAL_DEVICE_VERIFICATION_PROTOCOL.md` before calling the 3D path device-verified.

## Phone checks for this increment

1. Cold launch stays on Lounge for 30 seconds.
2. Open Manual, Diagnostics, Schedule, Parts one at a time. Return to Lounge after each.
3. Open 3D. Confirm the gate, not the 56-part layer. Wait 15 seconds.
4. Tap Load safe interactive scene. Confirm PARTS LAYER count is 1 on ALL, not 56.
5. Capture logcat if an ANR returns.
