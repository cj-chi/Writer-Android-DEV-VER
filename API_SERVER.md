API / Server 說明
=================

中文
----
本專案的網路請求主要透過 `HttpURLConnectionTask` 進行，支援三種回傳型態：
- `XML`：會被轉成 JSON（使用 XmlToJson），並取 `THLight` 根節點字串。
- `JSON`：原樣轉成字串。
- `FILE`：下載為檔案（例：`USBeaconList.zip`）。

主要主機：
- `http://ec2-54-248-224-99.ap-northeast-1.compute.amazonaws.com/`（大多數 API）
- `http://usbeacon.com.tw/`（下載清單 URL）

注意：目前多數 API 為 **HTTP**（非 HTTPS）。

English
-------
Requests are handled by `HttpURLConnectionTask` with three response types:
- `XML`: converted to JSON (XmlToJson) and reads the `THLight` root node string.
- `JSON`: raw string.
- `FILE`: downloaded as a file (e.g., `USBeaconList.zip`).

Main hosts:
- `http://ec2-54-248-224-99.ap-northeast-1.compute.amazonaws.com/` (most APIs)
- `http://usbeacon.com.tw/` (download list URL)

Note: Most endpoints are **HTTP** (not HTTPS).

Endpoints / 端點
----------------

1) Login / 登入
中文
- Method: GET
- Path: `/api/func?func=managerLogin&login_type=normal`
- Params: `account`, `password`
- Success check: response contains `"success"`
- Response fields (observed in code): `account_uuid`, `beacon_uuid`, `dataquery_uuid`, `mbr_id`, `status`

English
- Method: GET
- Path: `/api/func?func=managerLogin&login_type=normal`
- Params: `account`, `password`
- Success check: response contains `"success"`
- Response fields (observed in code): `account_uuid`, `beacon_uuid`, `dataquery_uuid`, `mbr_id`, `status`

2) Register / 註冊
中文
- Method: GET
- Path: `/func/register`
- Params: `mail`, `password`
- Success check: response contains `"ok"`

English
- Method: GET
- Path: `/func/register`
- Params: `mail`, `password`
- Success check: response contains `"ok"`

3) Forgot Password / 忘記密碼
中文
- Method: GET
- Path: `/func/pw_get`
- Params: `mail`, `ctrl=1`
- Success check: response contains `"ok"`

English
- Method: GET
- Path: `/func/pw_get`
- Params: `mail`, `ctrl=1`
- Success check: response contains `"ok"`

4) Add/Update Beacon / 新增或更新 Beacon
中文
- Method: GET
- Add Path: `/func/add`
- Update Path: `/func/update`
- Params (common): `dvc_name`, `dvc_version_major`, `dvc_version_minor`, `mbr_id`
- Update-only: `dvc_gid`
- Success check: response contains `"ok"`
- Add response used field: `gid`

English
- Method: GET
- Add Path: `/func/add`
- Update Path: `/func/update`
- Params (common): `dvc_name`, `dvc_version_major`, `dvc_version_minor`, `mbr_id`
- Update-only: `dvc_gid`
- Success check: response contains `"ok"`
- Add response used field: `gid`

5) Burn Status / 燒錄狀態回報
中文
- Method: GET
- Path: `/api/func?func=setBurn`
- Params: `id`, `mac`, `status`
- Response: only logged to console (no parsing in code)

English
- Method: GET
- Path: `/api/func?func=setBurn`
- Params: `id`, `mac`, `status`
- Response: only logged to console (no parsing in code)

6) Download Beacon List URL / 取得清單下載 URL
中文
- Host: `http://usbeacon.com.tw`
- Method: GET
- Path: `/api/func?func=getDataquery`
- Params: `dataquery_uuid`, `time_stamp=0`
- Success check: response contains `"success"`
- Response JSON field: `zip_path`

English
- Host: `http://usbeacon.com.tw`
- Method: GET
- Path: `/api/func?func=getDataquery`
- Params: `dataquery_uuid`, `time_stamp=0`
- Success check: response contains `"success"`
- Response JSON field: `zip_path`

7) Download Beacon List File / 下載清單檔案
中文
- Method: GET
- URL: 由 `zip_path` 指向的檔案網址
- Save: `USBeaconList.zip` (app external files dir, `Download/`)

English
- Method: GET
- URL: file URL from `zip_path`
- Save: `USBeaconList.zip` (app external files dir, `Download/`)

Need Confirmation / 需要你確認的資訊
------------------------------------
中文
- 請提供「登入成功」與「登入失敗」的實際回應範例。
- 註冊、忘記密碼、更新/新增 Beacon 的完整回應格式（是否有錯誤碼、訊息欄位）。
- `setBurn` 的回傳內容格式與可能狀態碼。
- `getDataquery` 的完整回應格式（除 `zip_path` 外是否還有欄位）。

English
-------
- Please provide real response examples for login success/failure.
- Full response format for register, forgot password, add/update beacon (error code/message fields?).
- Response format and status codes for `setBurn`.
- Full response format for `getDataquery` (fields beyond `zip_path`?).
