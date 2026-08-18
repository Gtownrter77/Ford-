# Recommendation Integrity Release Standard

## Release invariants

The Part Store is allowed to display a retailer comparison only when the following invariants are enforced in code and covered by automated tests.

| Invariant | Enforcement |
| --- | --- |
| **No commission-driven ranking** | `RecommendationIntegrity` accepts only quote fitment evidence, quote verification status, delivered total, and retailer name. It contains no affiliate, commission, referral, account, payment, or customer data field. |
| **Fitment before price** | A quote with better fitment evidence must rank ahead of a lower-cost listing that has only seller/fitment review status. |
| **Verified records before manual links** | For equal fitment evidence, an app-saved or authorized record ranks ahead of a manual-link-only result. The user can still open every available source. |
| **Untracked choice remains available** | The `OTHER_ONLINE` retailer source remains in the app’s retailer list, preventing an affiliate-only route. |
| **No commerce execution** | The app has no checkout callback, payment-method selector, account balance, credit-line, or order-confirmation method. A retailer purchase occurs only after the customer opens the retailer’s own page. |
| **Clear funding disclosure before activation** | A future eligible affiliate handoff requires an adjacent disclosure and a plainly accessible untracked option. The current build contains no active affiliate tracking. |

## Automated release tests

The `RecommendationIntegrityTest` suite is a release gate. It must show that fitment evidence outranks a cheaper unverified marketplace listing, that a saved quote outranks an equally matched manual-only record, and that the non-affiliate “other retailer” choice remains present. Any future code change that reverses those outcomes is a failed build and cannot release.

## Review protocol

Before activating any retailer-funding link, run the test suite with tracking disabled and enabled using identical quote evidence. The displayed ranking must be identical. A reviewer must then verify the disclosure, open the plain non-tracked alternative, and confirm that no payment, retailer-login, or personal-account data is stored by the app.

## Transparency reference

The FTC explains that financial connections that could affect how people evaluate recommendations should be disclosed clearly and conspicuously. The future funding layer is therefore blocked until the product can disclose it directly at the handoff without affecting part ranking. [1]

## References

[1]: https://www.ftc.gov/business-guidance/resources/ftcs-endorsement-guides-what-people-are-asking "FTC Endorsement Guides"
