# crypto-client — Scaffold

This creates a local branch with a small scaffold (no network or GitHub operations).
Files added:
 - .gitignore
 - src/main/java/... (DataKeyStore, OffHeapKeyStore, DataKey, KmsProvider, EnvelopeEncryption, auto-config)
 - src/test/java/... (OffHeapKeyStoreTest)
 - README_SCaffold.md

This branch is created locally. To push it later, run:
  git push -u origin scaffold/off-heap

If you wish to push without an HTTPS password prompt, push via SSH or use gh auth login.
