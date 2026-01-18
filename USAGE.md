Usage / 使用說明
================

中文
----
以下為 App 的一般操作流程與常見功能：

1) 啟動與登入
- 進入 App 後，若未登入會導向登入頁。
- 登入成功後，伺服器回傳的帳號資料會保存在本機（SharedPreferences）。

2) 註冊與忘記密碼
- 註冊會呼叫伺服器的註冊 API。
- 忘記密碼會呼叫伺服器的重設密碼 API。

3) 掃描裝置
- 開啟藍牙與定位權限後，掃描附近 USBeacon 裝置。
- 依訊號強度與裝置資訊排序顯示。

4) 連線與設定
- 進入裝置頁後，App 會依裝置類型讀取裝置資訊。
- 可更新裝置廣播參數、電量、感測器等資訊。

5) 與伺服器同步
- 會回報燒錄狀態（Burn status）。
- 可能會下載 Beacon 清單 ZIP 檔。

English
-------
Typical user flow and common features:

1) Launch & Login
- When not logged in, the app shows the login screen.
- On success, account data from server is saved locally (SharedPreferences).

2) Register & Forgot Password
- Registration calls the server registration API.
- Forgot password calls the server reset password API.

3) Scan Devices
- Enable Bluetooth and location permissions to scan nearby devices.
- Devices are shown with RSSI and metadata.

4) Connect & Configure
- The app reads device info based on device type.
- You can update advertising params, power, sensor settings, etc.

5) Server Sync
- Burn status is reported to the server.
- The app may download a beacon list ZIP file.

Notes
-----
中文
- 實際 UI 流程可能依版本不同而略有差異。
- 若需要更精確的流程對照畫面，請提供目標畫面或流程。

English
- UI flow can differ slightly across versions.
- If you need a screen-by-screen walkthrough, please provide the target screen or flow.
