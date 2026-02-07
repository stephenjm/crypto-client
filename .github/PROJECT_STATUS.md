# Project status — Crypto Client / Library

Last updated: 2026-02-07
Author: stephenjm / GitHub Copilot (assisted)

Summary
- Purpose: Add guidance for safe cryptographic library design to avoid unsafe defaults and to require test vectors and API stability.
- Files to be added:
  - .github/instructions/crypto-library.instructions.md
  - .github/PROJECT_STATUS.md

Immediate next steps (priority)
1. Add canonical test vectors and cross-checks with known-good implementations — high
2. Add CI matrix across supported runtimes — medium
3. Add property/fuzz tests for encoders/parsers — medium
