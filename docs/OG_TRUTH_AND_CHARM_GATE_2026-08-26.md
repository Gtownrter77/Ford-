# Owner Guide source truth + CHARM 4WD gate — 2026-08-26 (source-fixed)

What is actually on `main` after the ship pass:

1. `SportTracData` source numbers match the 2004 P207 Owner Guide: coolant 14.0 qt, air FA-1744, transfer case 1.3 qt MERCON ATF, rear axle 75W-90 FE + XL-7.
2. Live ViewModel, hub, maintenance, health, reminder, and offline-cache paths read `PublishedSportTracData`.
3. Local Gemini overheating refill is 14.0 qt. Power-steering service-manual flush cites OG MERCON ATF first.
4. 4WD diagram transfer-case card is 1.3 qt MERCON ATF. Rear-diff chip is 75W-90 FE, not 75W-140.
5. `CharmWorkshopIndex` stays on the 4WD VIN K tree, rejects 2WD pack URLs, and now includes Parts and Labor plus the official CHARM offline zip bundle URL.

Overlay remains a second fence. 5R55E transmission fluid stays MERCON V.
This is source-shippable. It is not a physical-device verification or a licensed GLB release.
