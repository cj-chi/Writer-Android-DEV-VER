USBeacon Writer (Android) - DEV Version
======================================

中文
----
這是一個 Android App，用於掃描、連線與設定 USBeacon 裝置，並透過伺服器 API 進行登入、註冊、資料同步與燒錄狀態回報。專案使用 Gradle 建置，主要模組為 `uSBeaconWriterFullVersion`。

快速入口
--------
- 使用說明（中文/English）：`USAGE.md`
- 架構說明（中文/English）：`ARCHITECTURE.md`
- API/Server 說明（中文/English）：`API_SERVER.md`

English
-------
This is an Android app for scanning, connecting, and configuring USBeacon devices. It also talks to server APIs for login, registration, data sync, and burn status reporting. The project is built with Gradle, and the main module is `uSBeaconWriterFullVersion`.

Quick Links
-----------
- Usage (中文/English): `USAGE.md`
- Architecture (中文/English): `ARCHITECTURE.md`
- API/Server (中文/English): `API_SERVER.md`

Build / Run
-----------
中文
- 建議使用 Android Studio，JDK 8+。
- 專案包含單一模組：`uSBeaconWriterFullVersion`。
- 可用 Gradle 指令建置：
  - `./gradlew :uSBeaconWriterFullVersion:assembleDebug`

English
- Use Android Studio with JDK 8+.
- Single module: `uSBeaconWriterFullVersion`.
- Build with Gradle:
  - `./gradlew :uSBeaconWriterFullVersion:assembleDebug`

Notes
-----
中文
- 網路請求多為 HTTP（非 HTTPS），詳見 `API_SERVER.md`。

English
- Most network requests use HTTP (not HTTPS). See `API_SERVER.md`.
