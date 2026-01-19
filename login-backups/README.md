Login backup artifacts (test evidence).

Sensitive contents:
- Some logs include account, password, and Access UUID (see SENSITIVE_AUTH).
- Treat this folder as confidential test data.

Current file patterns:
- factory-reset-test_*.logcat.txt: factory reset test sessions (logcat).
- factory-reset-new-account-test_*.logcat.txt: reset + new account login tests.
- full-bug-test_*.logcat.txt: full regression test sessions.
- write-test_*.txt: write flow test excerpts.
- *ble-log*.txt: BLE exchange excerpts.
- login-info_*.xml: local SharedPreferences snapshot after login.

Notes:
- logcat-session.txt in repo root is a working file and is not intended to be committed.
