package com.THLight.BLE.USBeacon.Writer.Simple.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.THLight.BLE.USBeacon.Writer.Simple.entity.login.AccountDataEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.util.GsonUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.LogUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

import java.util.List;

public class LoginManager {
    private static final String SHARED_PREFERENCES_FILE_LOGIN = "SHARED_PREFERENCES_FILE_LOGIN";
    private static final String SHARED_PREFERENCES_STRING_INT_LAST_POSITION = "SHARED_PREFERENCES_STRING_INT_LAST_POSITION";
    private static final String SHARED_PREFERENCES_STRING_JSON_ACCOUNT_DATA = "SHARED_PREFERENCES_STRING_JSON_ACCOUNT_DATA";
    private static final String SHARED_PREFERENCES_STRING_JSON_USER_ACCOUNT_LIST = "SHARED_PREFERENCES_STRING_JSON_USER_ACCOUNT_LIST";
    private static final String SHARED_PREFERENCES_STRING_JSON_USER_PASSWORD_LIST = "SHARED_PREFERENCES_STRING_JSON_USER_PASSWORD_LIST";
    private static final String SHARED_PREFERENCES_STRING_LAST_MODIFIED_DATE = "SHARED_PREFERENCES_STRING_LAST_MODIFIED_DATE";
    private static final String SHARED_PREFERENCES_STRING_LAST_USER_ID = "SHARED_PREFERENCES_STRING_LAST_USER_ID";
    private static final String SHARED_PREFERENCES_STRING_LAST_PASSWORD = "SHARED_PREFERENCES_STRING_LAST_PASSWORD";
    private static final String SHARED_PREFERENCES_STRING_LAST_ACCOUNT_UUID = "SHARED_PREFERENCES_STRING_LAST_ACCOUNT_UUID";
    private static final String SHARED_PREFERENCES_STRING_LAST_BEACON_UUID = "SHARED_PREFERENCES_STRING_LAST_BEACON_UUID";
    private static final String SHARED_PREFERENCES_STRING_LAST_QUERY_UUID = "SHARED_PREFERENCES_STRING_LAST_QUERY_UUID";

    private static final String SHARED_PREFERENCES_BOOLEAN_FAST_SETTING = "SHARED_PREFERENCES_BOOLEAN_FAST_SETTING";
    private static final String SHARED_PREFERENCES_BOOLEAN_AES_SETTING = "SHARED_PREFERENCES_BOOLEAN_AES_SETTING";
    private static final String SHARED_PREFERENCES_STRING_MAJOR_VALUE = "SHARED_PREFERENCES_STRING_MAJOR_VALUE";
    private static final String SHARED_PREFERENCES_STRING_MINOR_VALUE = "SHARED_PREFERENCES_STRING_MINOR_VALUE";
    private static final String SHARED_PREFERENCES_STRING_BROADCAST_FREQUENCY = "SHARED_PREFERENCES_STRING_BROADCAST_FREQUENCY";
    private static final String SHARED_PREFERENCES_STRING_REST_BROADCAST_FREQUENCY = "SHARED_PREFERENCES_STRING_REST_BROADCAST_FREQUENCY";
    private static final String SHARED_PREFERENCES_STRING_REST_TIME = "SHARED_PREFERENCES_STRING_REST_TIME";
    private static final String SHARED_PREFERENCES_INT_TX_POWER_INDEX = "SHARED_PREFERENCES_INT_TX_POWER_LEVEL";

    private SharedPreferences sharedPreferences;
    private List<String> userAccountList;
    private List<String> userPasswordList;
    private static LoginManager instance;

    private LoginManager(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_LOGIN, Context.MODE_PRIVATE);
        }
    }

    public static LoginManager getInstance() {
        if (instance == null) {
            instance = new LoginManager(ApplicationManager.getInstance().getContext());
        }
        return instance;
    }

    public AccountDataEntity getAccountDataEntity() { // 取得帳號資訊
        String jsonString = sharedPreferences.getString(SHARED_PREFERENCES_STRING_JSON_ACCOUNT_DATA, "");
        if (!StringUtil.isEmpty(jsonString)) {
            return GsonUtil.generateGenericData(jsonString, AccountDataEntity.class);
        }
        return null;
    }

    public void setAccountDataString(String accountDataString) { // 登入成功後, 將 server 給的 json 儲存在本地
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_JSON_ACCOUNT_DATA, accountDataString);
        editor.commit();
    }

    public void setAccountDataEntity(AccountDataEntity accountDataEntity) {
        if (accountDataEntity == null) {
            return;
        }
        setAccountDataString(GsonUtil.toJson(accountDataEntity));
    }

    public void setAccountDataStringWithCredentials(String accountDataString, String account, String password) {
        AccountDataEntity baseEntity = AccountDataEntity.fromCredentials(account, password);
        AccountDataEntity responseEntity = null;
        if (!StringUtil.isEmpty(accountDataString)) {
            responseEntity = GsonUtil.generateGenericData(accountDataString, AccountDataEntity.class);
        }
        if (responseEntity == null) {
            LogUtil.log("SENSITIVE_AUTH", "account=" + account
                    + " password=" + password
                    + " accessUuid=" + (baseEntity == null ? "" : baseEntity.getAccessUUID()));
            setAccountDataEntity(baseEntity);
            return;
        }
        responseEntity.setAccessUUID(baseEntity.getAccessUUID());
        responseEntity.setBeaconUUID(baseEntity.getBeaconUUID());
        if (StringUtil.isEmpty(responseEntity.getQueryUUID())) {
            responseEntity.setQueryUUID(baseEntity.getQueryUUID());
        }
        LogUtil.log("SENSITIVE_AUTH", "account=" + account
                + " password=" + password
                + " accessUuid=" + responseEntity.getAccessUUID());
        setAccountDataEntity(responseEntity);
        persistPlainTextCredentials(account, password, responseEntity);
    }

    public void persistPlainTextCredentials(String account, String password, AccountDataEntity accountDataEntity) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_LAST_USER_ID, account == null ? "" : account);
        editor.putString(SHARED_PREFERENCES_STRING_LAST_PASSWORD, password == null ? "" : password);
        if (accountDataEntity != null) {
            editor.putString(SHARED_PREFERENCES_STRING_LAST_ACCOUNT_UUID,
                    accountDataEntity.getAccessUUID() == null ? "" : accountDataEntity.getAccessUUID());
            editor.putString(SHARED_PREFERENCES_STRING_LAST_BEACON_UUID,
                    accountDataEntity.getBeaconUUID() == null ? "" : accountDataEntity.getBeaconUUID());
            editor.putString(SHARED_PREFERENCES_STRING_LAST_QUERY_UUID,
                    accountDataEntity.getQueryUUID() == null ? "" : accountDataEntity.getQueryUUID());
        } else {
            editor.putString(SHARED_PREFERENCES_STRING_LAST_ACCOUNT_UUID, "");
            editor.putString(SHARED_PREFERENCES_STRING_LAST_BEACON_UUID, "");
            editor.putString(SHARED_PREFERENCES_STRING_LAST_QUERY_UUID, "");
        }
        editor.commit();
    }

    public List<String> getUserAccountList() {
        String jsonString = sharedPreferences.getString(SHARED_PREFERENCES_STRING_JSON_USER_ACCOUNT_LIST, "");
        if (userAccountList == null && !StringUtil.isEmpty(jsonString)) {
            userAccountList = GsonUtil.generateDataList(jsonString, String.class);
        }
        return userAccountList;
    }

    public void setUserAccountList(List<String> userAccountList) { // 設定已登入過的帳號
        this.userAccountList = userAccountList;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_JSON_USER_ACCOUNT_LIST, GsonUtil.toJson(userAccountList));
        editor.commit();
    }

    public List<String> getUserPasswordList() {
        String jsonString = sharedPreferences.getString(SHARED_PREFERENCES_STRING_JSON_USER_PASSWORD_LIST, "");
        if (userPasswordList == null && !StringUtil.isEmpty(jsonString)) {
            userPasswordList = GsonUtil.generateDataList(jsonString, String.class);
        }
        return userPasswordList;
    }

    public void setUserPasswordList(List<String> userPasswordList) { // 設置已登入過的密碼
        this.userPasswordList = userPasswordList;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_JSON_USER_PASSWORD_LIST, GsonUtil.toJson(userPasswordList));
        editor.commit();
    }

    public int getLastPosition() {
        return sharedPreferences.getInt(SHARED_PREFERENCES_STRING_INT_LAST_POSITION, 0);
    }

    public void setLastPosition(int lastPosition) { // 設置目前所選的帳號為帳號列表中的哪個位置
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFERENCES_STRING_INT_LAST_POSITION, lastPosition);
        editor.commit();
    }

    public String getLastModifiedDate() {
        return sharedPreferences.getString(SHARED_PREFERENCES_STRING_LAST_MODIFIED_DATE, "");
    }

    public void setLastModifiedDate(String lastModifiedDate) { // 設置修改的日期
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_LAST_MODIFIED_DATE, lastModifiedDate);
        editor.commit();
    }

    public void setFastSettingBoolean(boolean isEnable) { // 設置是否開啟快速設定
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARED_PREFERENCES_BOOLEAN_FAST_SETTING, isEnable);
        editor.commit();
    }

    public boolean isFastSetting() {
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_BOOLEAN_FAST_SETTING, false);
    }

    public void setAesSettingBoolean(boolean isEnable) { // 設置是否開啟 AES 設定
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARED_PREFERENCES_BOOLEAN_AES_SETTING, isEnable);
        editor.commit();
    }

    public boolean isAesSetting() {
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_BOOLEAN_AES_SETTING, false);
    }

    public void setMajor(String major) { // 設定上次寫入的Major
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_MAJOR_VALUE, major);
        editor.commit();
    }

    public String getMajor() {
        return sharedPreferences.getString(SHARED_PREFERENCES_STRING_MAJOR_VALUE, "");
    }

    public void setMinor(String minor) { // 設定上次寫入的Minor + 1
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_MINOR_VALUE, minor);
        editor.commit();
    }

    public String getMinor() {
        return sharedPreferences.getString(SHARED_PREFERENCES_STRING_MINOR_VALUE, "");
    }

    public void setBroadcastFrequency(String frequency) { // 設定上次寫入的廣播頻率
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_BROADCAST_FREQUENCY, frequency);
        editor.commit();
    }

    public String getBroadcastFrequency() {
        return sharedPreferences.getString(SHARED_PREFERENCES_STRING_BROADCAST_FREQUENCY, "");
    }

    public void setRestBroadcastFrequency(String frequency) { // 設定上次寫入的休眠時廣播頻率
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_REST_BROADCAST_FREQUENCY, frequency);
        editor.commit();
    }

    public String getRestBroadcastFrequency() {
        return sharedPreferences.getString(SHARED_PREFERENCES_STRING_REST_BROADCAST_FREQUENCY, "");
    }

    public void setTxPowerIndex(int index) { //設定上次寫入的Tx power index
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARED_PREFERENCES_INT_TX_POWER_INDEX, index);
        editor.commit();
    }

    public int getTxPowerIndex() {
        return sharedPreferences.getInt(SHARED_PREFERENCES_INT_TX_POWER_INDEX, -1);
    }

    public void setRestTime(String time) { //設定上次寫入的多久進入睡眠
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_STRING_REST_TIME, time);
        editor.commit();
    }

    public String getRestTime() {
        return sharedPreferences.getString(SHARED_PREFERENCES_STRING_REST_TIME, "");
    }

    public void logout() {
        setAccountDataString(null);
        setFastSettingBoolean(false);
        setAesSettingBoolean(false);
        setMajor("");
        setMinor("");
        setTxPowerIndex(-1);
        setBroadcastFrequency("");
        setRestTime("");
        setRestBroadcastFrequency("");
        persistPlainTextCredentials("", "", null);
    }
}
