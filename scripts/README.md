# Script 說明 / Scripts Guide

## 中文說明
本目錄的腳本用於「自動遞增版本號並安裝 App」。
執行腳本時會要求輸入修改目的，並自動更新版本號、編譯、安裝與重啟 App。

### 使用方式
1. 確認手機已連線並可使用 adb。
2. 執行：
   - `./scripts/update_and_install.sh`
3. 依提示輸入「修改目的」。

### 相關檔案
- `version-log.txt`：版本記錄檔（版本號 | 建立時間 | 修改目的）。
- `uSBeaconWriterFullVersion/build.gradle`：腳本會更新 `versionName`。
- `uSBeaconWriterFullVersion/build/outputs/apk/debug/uSBeaconWriterFullVersion-debug.apk`：產出的 APK。

## English
Scripts in this folder automate version bump + build + install.
When you run the script, it prompts for a change reason, then updates the version, builds, installs, and restarts the app.

### Usage
1. Ensure your device is connected and adb is available.
2. Run:
   - `./scripts/update_and_install.sh`
3. Enter the change reason when prompted.

### Related files
- `version-log.txt`: version log (version | createdAt | reason).
- `uSBeaconWriterFullVersion/build.gradle`: `versionName` is updated by the script.
- `uSBeaconWriterFullVersion/build/outputs/apk/debug/uSBeaconWriterFullVersion-debug.apk`: generated APK.
