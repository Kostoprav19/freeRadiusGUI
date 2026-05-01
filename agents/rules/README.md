# IDE rules (canonical copy)

Cursor rule files (`*.mdc`) live **here** so the repo stays vendor-neutral.

Cursor resolves rules from `.cursor/rules/` in the workspace. This repository
tracks **symlinks** under `.cursor/rules/` that point to `agents/rules/*.mdc`.
Edit rules in this directory; keep symlinks in sync when adding new files.
