# Repository Workflow Rules

## Ten-change cadence

After every **10 substantive changes** to this repository, the active agent must update the repository and create a checkpoint before continuing with another batch of substantive work.

A substantive change includes a committed feature, model revision, data-extraction revision, technical-documentation update, asset replacement, or other user-visible project modification. Cosmetic inspection, read-only review, and failed experiments that are not retained do not count.

The agent should maintain a visible change counter in the task notes or commit context, announce when the tenth change is reached, run the relevant validation, commit the accumulated work, push it to the configured branch when the user has authorized repository pushes, and create the corresponding project checkpoint when a managed project checkpoint is available.

This rule is a repository-level workflow convention. It does not alter GitHub branch protection, CI configuration, or the Manus platform’s internal memory and remains subject to the user’s current instructions.
