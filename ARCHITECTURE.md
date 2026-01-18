Architecture / 架構說明
=======================

中文
----
本專案為單一 Android Application 模組：`uSBeaconWriterFullVersion`。主要結構如下：

1) app 入口
- Application：`ThLightApplication` 註冊網路變化監聽。
- Launcher Activity：`AppStartActivity`。

2) 主要套件分層
- `application/`：Application 類別與全域初始化。
- `manager/`：App 狀態與裝置/網路/登入等管理。
- `webservice/`：HTTP 請求與非同步任務封裝。
- `webservice/task/`：各 API 的具體任務（登入、註冊、更新、下載等）。
- `ui/`：Activity/Fragment/UI Adapter。
- `entity/`：資料結構與資料模型。
- `util/`：通用工具類。
- `helper/`：Broadcast 與權限等輔助邏輯。

3) 網路層流程
- `HttpURLConnectionTask` 負責建立連線與解析回傳（XML/JSON/FILE）。
- `ApiAsyncTask` 以 `AsyncTask` 執行網路請求並回呼結果。
- 各任務類（如 `LoginTask`、`UpdateBeaconTask`）負責組合 URL 與處理回應。

4) 儲存機制
- 登入資訊與使用者偏好設定透過 `SharedPreferences` 保存。

English
-------
This project is a single Android application module: `uSBeaconWriterFullVersion`. Main structure:

1) App entry
- Application: `ThLightApplication` registers network change callbacks.
- Launcher Activity: `AppStartActivity`.

2) Package layers
- `application/`: Application class and app-wide initialization.
- `manager/`: app state and device/network/login managers.
- `webservice/`: HTTP request base and async task wrapper.
- `webservice/task/`: concrete API tasks (login, register, update, download).
- `ui/`: Activities, Fragments, adapters.
- `entity/`: data models and entities.
- `util/`: common utilities.
- `helper/`: broadcasts, permissions, and helpers.

3) Network flow
- `HttpURLConnectionTask` builds connections and parses responses (XML/JSON/FILE).
- `ApiAsyncTask` runs network calls via `AsyncTask` and callbacks.
- Each task (e.g., `LoginTask`, `UpdateBeaconTask`) builds URLs and handles responses.

4) Storage
- Login info and user preferences are stored via `SharedPreferences`.
