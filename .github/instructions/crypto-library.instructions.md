---
description: 'Copilot instructions for cryptographic library development: API stability, correctness, tests, and auditability.'
applyTo: '**/*'
---

# Crypto Client / Library — Copilot Instructions

## Purpose
Guide Copilot to recommend safe cryptographic patterns, robust APIs, and test-driven development for the crypto client library.

## Do
- Use standard, reviewed cryptographic primitives from trusted libraries; do not invent new algorithms.
- Offer clear, high-level APIs that reduce the chance of misuse (e.g., secure defaults, builders that prevent insecure params).
- Provide deterministic unit tests and canonical test vectors for each primitive.
- Document supported algorithms, parameter choices, and compatibility constraints (FIPS mode, key sizes).

## Don't
- Don't expose raw primitives or low-level operations as public APIs unless necessary.
- Don't generate examples with real keys; use marked placeholders or test keys.
- Don't default to insecure parameters (small key sizes, deprecated algorithms).

## Testing & Validation
- Include fixed test vectors and cross-check results with known-good implementations.
- Add fuzzing or property-based tests for parser/encoder logic (e.g., DER/PEM).
- Add benchmarks and validate performance regressions in CI where relevant.

## CI / Tooling Recommendations
- Dependency pinning and vulnerability scanning for cryptographic dependencies.
- Run tests across supported runtime versions and target platforms.
- Add coverage checks for crypto logic and serialization code.

## PR checklist
- [ ] Test vectors present for any crypto change
- [ ] No real secrets or keys in diffs
- [ ] API changes documented and backward-compatibility considered
- [ ] Security reviewer included for substantive crypto changes

## Notes for developers
- Prefer composition over exposing low-level hooks; give higher-level helpers that enforce secure defaults.
- Include examples demonstrating secure usage patterns and common pitfalls to avoid.
