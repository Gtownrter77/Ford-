# Owner Guide source truth + CHARM 4WD gate — 2026-08-26

Five shippable fixes after the overlay-only pass.

1. `SportTracData` now carries 2004 P207 Owner Guide numbers at source: coolant 14.0 qt, air FA-1744, transfer case 1.3 qt MERCON ATF, power steering MERCON ATF.
2. Live ViewModel / hub / maintenance / cache paths read `PublishedSportTracData`, not the raw catalog.
3. Local Gemini overheating reply and power-steering service-manual step use the same OG fluids.
4. 4WD diagram transfer-case card is 1.3 qt MERCON ATF, not 1.5 qt MERCON V.
5. `CharmWorkshopIndex` publishes only the 4WD VIN K tree, rejects the 2WD pack URLs, and Mentor/MentorDock can open those leaves.

Overlay remains as a second fence. 5R55E transmission fluid stays MERCON V.
