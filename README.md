USBeacon Writer (Android) - DEV Version
======================================

中文
----
這是一個 Android App，用於掃描、連線與設定 USBeacon 裝置，並透過伺服器 API 進行登入、註冊、資料同步與燒錄狀態回報。專案使用 Gradle 建置，主要模組為 `uSBeaconWriterFullVersion`。

帳密規則 / UUID 規則 / 本地儲存
-----------------------------
中文
- 帳號與密碼格式：必須為 4 碼英數（`^[A-Za-z0-9]{4}$`）。
- Access UUID 規則：`USERID + PASSWD` 以 UTF-8 串接後重複填滿 16 bytes，轉為 UUID 字串格式（8-4-4-4-12）。
- Beacon UUID 規則：仍為 SHA-256 方式產生（用途標記為 `beacon`）。
- 本地儲存位置：
  - 註冊帳密（明文）在 `local_auth_store.json`：
    - `/data/data/com.THLight.BLE.USBeacon.Writer.Simple.dev/files/local_auth_store.json`
    - 欄位：`account` / `passwordPlain` / `passwordHash` / `salt`
  - 最近一次登入資訊與 UUID 在 SharedPreferences：
    - `/data/data/com.THLight.BLE.USBeacon.Writer.Simple.dev/shared_prefs/SHARED_PREFERENCES_FILE_LOGIN.xml`
    - 欄位：`SHARED_PREFERENCES_STRING_LAST_USER_ID` / `SHARED_PREFERENCES_STRING_LAST_PASSWORD` /
      `SHARED_PREFERENCES_STRING_LAST_ACCOUNT_UUID` / `SHARED_PREFERENCES_STRING_LAST_BEACON_UUID` /
      `SHARED_PREFERENCES_STRING_LAST_QUERY_UUID`

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
- 建議使用 Android Studio，JDK 17。
- 專案包含單一模組：`uSBeaconWriterFullVersion`。
- 可用 Gradle 指令建置：
  - `./gradlew :uSBeaconWriterFullVersion:assembleDebug`

English
- Use Android Studio with JDK 17.
- Single module: `uSBeaconWriterFullVersion`.
- Build with Gradle:
  - `./gradlew :uSBeaconWriterFullVersion:assembleDebug`

Notes
-----
中文
- 網路請求多為 HTTP（非 HTTPS），詳見 `API_SERVER.md`。

English
- Most network requests use HTTP (not HTTPS). See `API_SERVER.md`.
