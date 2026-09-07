# Morning Action List

- [ ] **[DECISION] Choose the repair-realm asset route:** **(A)** use a licensed 2004 Ford Explorer Sport Trac wreck GLB, **(B)** commission a custom wreck GLB, or **(C)** keep the repair realm in its current asset-required state.

- [ ] **[PURCHASE] Obtain the selected 2004 Ford Explorer Sport Trac wreck GLB license and the downloadable GLB asset.**

- [ ] **[FILE] Place the licensed wreck GLB from the provider download folder at `/home/ubuntu/Downloads/<licensed-sport-trac-wreck>.glb` into `/home/ubuntu/ford-sport-trac-github/app/src/main/assets/models/ford_explorer_sport_trac_2004_wreck.glb`.**

- [ ] **[DECISION] Choose the repair-intelligence mode:** **(A)** local fallback diagnostics only, **(B)** enable the current `gemini-3.5-flash` path, or **(C)** keep the Gemini feature disabled until a different authorized model is selected.

- [ ] **[SETUP] If option B is selected, configure a non-placeholder `GEMINI_API_KEY` through the project’s Secrets panel so the existing Gemini client can make its conditional cloud request.**

- [ ] **[BUILD] Install `/home/ubuntu/ford-sport-trac-github/app/build/outputs/apk/debug/app-debug.apk` on the Commander’s Android phone, then run the documented cold-launch, tab-transition, and lifecycle checks in `/home/ubuntu/ford-sport-trac-github/DEVICE_VERIFICATION_PROTOCOL.md`.**

- [ ] **[PLACEHOLDER] Define the required live OBD2/FORScan outcome as either “not included” or “real adapter integration required”; Poindexter will build the transport only after that product decision is supplied.**
