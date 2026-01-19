# 版本測試紀錄與回溯重點

本文件用於 rollback 參考，列出每版上傳前的測試項目、成功/失敗結果與注意事項。
時間以實際測試紀錄為主，若無 log 則標記為未紀錄。

---

## 1.2a.0001

### 測試項目
- 未紀錄

### 成功
- 未紀錄

### 失敗
- 未紀錄

### 備註
- 版本說明只有「修正問題」，缺乏細節。

---

## 1.2a.0002

### 測試項目
- 未紀錄

### 成功
- 未紀錄

### 失敗
- 未紀錄

### 備註
- 版本說明為「修正註冊成功提示顯示帳號密碼」。

---

## 1.2a.0003

### 測試項目
- Android 15 / JDK 17 相容性

### 成功
- 可啟動（未完整測試）

### 失敗
- 未紀錄

### 備註
- 版本說明註記「可執行但尚未測試」。

---

## 1.2a.0004

### 測試項目
- 建立帳號
- 設定值修改
- Factory reset

### 成功
- 建立帳號成功
- 設定值修改成功
- Factory reset 成功

### 失敗
- 未紀錄

### 備註
- 此版本為首次確認完整流程成功的版本。

---

## 1.2a.0005

### 測試項目
- Android 12+ 掃描權限提示與 BLE 掃描

### 成功
- 掃描權限提示可用
- BLE 掃描成功

### 失敗
- Access UUID 可能在更新程式後被清掉，導致連線後出現驗證不匹配而無法寫入

### 備註
- 此版本開始暴露 Access UUID 驗證不一致問題。

---

## 1.2a.0006

### 測試項目
- BLE 連線與服務探索
- 讀取版本資訊
- 寫入多項設定
- Factory reset
- Reset 後 Access UUID 清除驗證
- 切換帳號 oppi/oppi 驗證
- PID 視窗（Debug 專用）

### 成功
- 連線成功、服務探索成功
- 版本讀取成功（常見為 2.7）
- 多項寫入成功（連續 ACK 0x80）
  - CMD_SET_ACC_UUID (0x04)
  - CMD_W_BEACON_UUID (0x20)
  - CMD_W_BEACON_MAJOR (0x21)
  - CMD_W_BEACON_MINOR (0x22)
  - CMD_W_TX_POWER (0x26)
  - CMD_W_ADVERTISE_PER_SECOND (0x24)
- Factory reset 指令回 ACK_SUCCESS
- Reset 後 CMD_CHECK_ACC_UUID 回 ERR_ACC_FREE (0x85)，表示 Access UUID 已被清除
- 切換帳號 oppi/oppi 後 CMD_CHECK_ACC_UUID 回 ACK_SUCCESS (0x80)

### 失敗
- 先前測試曾多次出現 ERR_ACC_NOT_MATCH (0x86) 導致讀寫失敗
- 部分測試 log 抓錯 PID 或時間段，導致看不到 BLE 行為（已用 PID 視窗改善）

### 備註
- 本版新增 PID 對話框協助記錄 PID，後續版本將移除。
- CMD_GET_BEACON_TYPE 不支援的裝置仍會回 ERR_CMD_NOT_FOUND，但已不列為主要測試結果。

---

## 1.2a.0007

### 測試項目
- 啟動 APP
- 連線 beacon
- 讀取並顯示 beacon UUID

### 成功
- 連線成功
- Major/Minor 顯示正常
- beacon UUID 能顯示（不再空白）

### 失敗
- 未紀錄

### 備註
- 回退 UUID 輸入格式化／過濾，避免 UUID 顯示為空白。

---

## 補充：rollback 判斷建議

- 若遇到 Access UUID 驗證問題，回到 1.2a.0004 可驗證基本流程是否仍可完整寫入。
- 若遇到掃描權限問題，1.2a.0005 已包含完整掃描權限提示修正。
- 若需要精準 log，使用 1.2a.0006（含 PID 視窗）。
