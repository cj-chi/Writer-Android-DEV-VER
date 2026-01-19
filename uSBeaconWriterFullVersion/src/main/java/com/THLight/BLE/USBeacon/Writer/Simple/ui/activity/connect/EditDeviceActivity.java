package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.NetworkManager;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseFragment;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.pager.BasePagerAdapter;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.beacon.BeaconProcessEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.command.UsBeaconCommand;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.login.AccountDataEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.SendBroadcastHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager.CommandType;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager.MessageAckListener;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager.ReadVersionCharacteristicListener;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.pager.CustomViewPager;
import com.THLight.BLE.USBeacon.Writer.Simple.util.BytesUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.GsonUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.BurnBeaconTask;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.UpdateBeaconTask;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.UpdateBeaconTask.UpdateBeaconTaskListener;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.INTENT_STRING_JSON_DEVICE_ENTITY;

public class EditDeviceActivity extends BaseActivity implements MessageAckListener, OnClickListener,
        ReadVersionCharacteristicListener, OnPageChangeListener, UpdateBeaconTaskListener {
    //-----------------------fragment page-------------------------------
    public static final int INFORMATION_BEACON_PAGE = 0;
    public static final int POWER_BEACON_PAGE = 1;
    public static final int DISTANCE_BEACON_PAGE = 2;
    public static final int WEARABLE_BEACON_PAGE = 3;
    //-----------------------beacon type---------------------------------
    protected static final int ORIGINAL_TYPE_BEACON = 0;
    protected static final int REMOTE_TYPE_BEACON = 1;
    protected static final int WEARABLE_TYPE_BEACON = 2;
    protected static final int DAGGER_TYPE_BEACON = 3;
    protected static final int PROX_TYPE_BEACON = 4;
    protected static final int B3339B_BEACON = 5;
    //-------------------------------------------------------------------
    public static final String OUTPUT_ENTITY_DEVICE_INFO = "OUTPUT_ENTITY_DEVICE_INFO";
    protected BluetoothDeviceItemEntity entity;
    private InformationBeaconFragment informationBeaconFragment;
    private PowerBeaconFragment powerBeaconFragment;
    private MeterRSSIFragment meterRSSIFragment;
    private WearableBeaconFragment wearableBeaconFragment;
    private CustomViewPager viewPager;
    private boolean isUpdate;
    private final Handler writeTimeoutHandler = new Handler(Looper.getMainLooper());
    private boolean isWriting;
    private boolean isResetting;
    private final Runnable writeTimeoutRunnable = () -> {
        if (isWriting) {
            isWriting = false;
            hideLoadingDialog();
            toastMessageView(this, "寫入逾時，請確認裝置連線狀態");
        }
    };
    private final Runnable resetTimeoutRunnable = () -> {
        if (isResetting) {
            isResetting = false;
            hideLoadingDialog();
            toastMessageView(this, "重置逾時，請確認裝置連線狀態");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);
        initValue();
        registerListener();
        bindContentView();
        BluetoothConnectDeviceManager.getInstance().readVersionCharacteristic(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.viewPager.removeOnPageChangeListener(this);
        BluetoothConnectDeviceManager.getInstance().disConnectDevice();
        setUpFastSetting();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) { // viewpager 換頁 callback
        hideKeyboardView();
        setStyleBackground(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    // Get beacon type by command before other commands.
    @Override
    public void onReadVersionCharacteristicResponse(int characteristicValue) { // 取得版本號 callback
        entity.setVersionCharacteristicInt(characteristicValue);
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_GET_BEACON_TYPE),
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_CHECK_ACC_UUID, LoginManager.getInstance().getAccountDataEntity().generateAccessUuid()));
        BluetoothConnectDeviceManager.getInstance().executeCommandTask();
    }

    // Because the command response is slow so need to broadcast the notification component update
    @Override
    public void onMessageAckResponseSuccess(int code, int responseCode, byte[] data) {
        System.out.println("EditDeviceActivity OnMessageAckResponse flag : " + BytesUtil.getHexString(code) +
                " , responseCode : " + BytesUtil.getHexString(responseCode) +
                " , data : " + BytesUtil.getHexString(data));
        switch (responseCode) {
            case UsBeaconCommand.CMD_GET_BEACON_TYPE:
                readBeaconType(data);
                break;
            case UsBeaconCommand.CMD_R_REMOTE_ID:
                readRemoteId(data);
                break;
            case UsBeaconCommand.CMD_R_BEACON_UUID:
                readBeaconUuid(data);
                break;
            case UsBeaconCommand.CMD_GET_INFO:
                readDeviceInformation(data);
                break;
            case UsBeaconCommand.CMD_R_TX_POWER:
                readTxPower(data);
                break;
            case UsBeaconCommand.CMD_R_ADV_INFO:
                readAdvertiseInformation(data);
                break;
            case UsBeaconCommand.CMD_R_ADVERTISE_PER_SECOND:
                readBroadcastFrequency(data);
                break;
            case UsBeaconCommand.CMD_R_DISABLE_ALARM_SIGNALS:
                readDisableAlarmSignals(data);
                break;
            case UsBeaconCommand.CMD_R_GSENSOR_SETTING:
                readGSensorSetting(data);
                break;
            case UsBeaconCommand.CMD_R_SECONDS_2_REST:
                readRestTime(data);
                break;
            case UsBeaconCommand.CMD_R_REST_FREQ:
                readRestBroadcastFrequency(data);
                break;
            case UsBeaconCommand.CMD_R_ENABLE_SENSOR_DATA:
                readEnableSensorData(data);
                break;
            case UsBeaconCommand.CMD_R_ENABLE_ADV_STEPS:
                readEnableAdvertisingTracker(data);
                break;
            case UsBeaconCommand.CMD_R_STEPS:
                readSteps();
                break;
            case UsBeaconCommand.CMD_R_LINE_INFO:
                break;
            case UsBeaconCommand.CMD_R_SIGNIN_HWID:
                readHwId(data);
                break;
            case UsBeaconCommand.CMD_R_SIGNIN_USERID:
                readUserId(data);
                break;
            case UsBeaconCommand.CMD_R_SIGNIN_AESKEY:
                readAskKey(data);
                break;
            case UsBeaconCommand.CMD_R_ENABLE_LOW_BATTERY_LED:
                readEnableLowBatteryLed(data);
                break;
            case UsBeaconCommand.ERR_CMD_NOT_FOUND:
                errorCommandNotFound(code);
                break;
            case UsBeaconCommand.ACK_SUCCESS:
            case UsBeaconCommand.ERR_ACC_FREE:
                ackSuccess(code);
                break;
            case UsBeaconCommand.ERR_ACC_NOT_MATCH:
                errorAccessNotMatch();
                break;
        }
        if (code == UsBeaconCommand.CMD_FACTORY_RESET && responseCode == UsBeaconCommand.ACK_SUCCESS) {
            isResetting = false;
            writeTimeoutHandler.removeCallbacks(resetTimeoutRunnable);
            hideLoadingDialog();
            toastMessageView(this, "重置完成");
            finish();
        }
    }

    @Override
    public void onMessageAckResponseDone(CommandType type) { // 列表內的命令全部執行完畢
        switch (type) {
            case Write:
                System.out.println("onMessageAckResponseDone ->  write");
                isWriting = false;
                writeTimeoutHandler.removeCallbacks(writeTimeoutRunnable);
                this.isUpdate = true;
                setUpModifiedDate();
                startUpdateBeaconTask();
                SendBroadcastHelper.sendRefreshAllDataAction(this);
                break;
            case Read:
                System.out.println("onMessageAckResponseDone ->  Read");
                SendBroadcastHelper.sendRefreshAllDataAction(this);
                break;
            case UnKnown:
                System.out.println("onMessageAckResponseDone ->  UnKnown");
                break;
        }
    }

    @Override
    public void onMessageAckResponseFailed(int flag) {
        isWriting = false;
        isResetting = false;
        writeTimeoutHandler.removeCallbacks(writeTimeoutRunnable);
        writeTimeoutHandler.removeCallbacks(resetTimeoutRunnable);
        hideLoadingDialog();
        toastMessageView(this, "(" + flag + ")" + " 寫入失敗 ...");
    }

    private void errorCommandNotFound(int code) {
//        String text = getString(R.string.read_data_failed) + "( 0x" + BytesUtil.getHexString(flag) + ")";
//        toastMessageView(this, text);
    }

    private void ackSuccess(int code) {
        if (code == UsBeaconCommand.CMD_CHECK_ACC_UUID) { // 檢查目前的裝置是否自己能夠使用
            addCommandToQueue();
        }
    }

    private void errorAccessNotMatch() {
        toastMessageView(this, getString(R.string.connect_no_permission));
        finish();
    }

    private void readBeaconType(byte[] data) {
        entity.setDeviceType(BeaconProcessEntity.getBeaconType(data));
        System.out.println("readBeaconType : " + entity.getDeviceType());
    }

    private void readRemoteId(byte[] data) { //  命令 (CMD_R_REMOTE_ID) Callback , 得到 beacon remoteId
        System.out.println("onMessageReadRemoteId : " + BeaconProcessEntity.getRemoteID(data));
        entity.setMajor(String.valueOf(BeaconProcessEntity.getRemoteID(data)));
        entity.setMinor("0");
    }

    private void readBeaconUuid(byte[] data) { // 命令 (CMD_R_BEACON_UUID) Callback , 得到 beacon UUID
        entity.setUuid(BeaconProcessEntity.getBeaconUuid(data));
    }

    private void readDeviceInformation(byte[] data) { //  命令 (CMD_GET_INFO) Callback , 得到 beacon 韌體版本和電量
        entity.setFirmWare(BeaconProcessEntity.getFirmwareVersion(data));
        entity.setBatteryPower(BeaconProcessEntity.getBatteryPower(data));
        System.out.println("readDeviceInformation : " + entity.getFirmWare() + " " + entity.getBatteryPower());

    }

    private void readTxPower(byte[] data) { //  命令 (CMD_R_TX_POWER) Callback , 得到 beacon TxPower
        entity.setTxPowerIndex(BeaconProcessEntity.getTxPower(data));
    }

    private void readAdvertiseInformation(byte[] data) { //  命令 (CMD_R_ADV_INFO) Callback , 得到 beacon 廣播頻率 & 多久進入睡眠的時間 & 睡眠時廣播頻率
        entity.setBroadcastFrequency(BeaconProcessEntity.getAdvInfoFrequency(data) / 60);
        entity.setRestTime(BeaconProcessEntity.getAdvInfoSecondToRest(data));
        entity.setRestBroadcastFrequency(BeaconProcessEntity.getAdvInfoRestFrequency(data));
    }

    private void readBroadcastFrequency(byte[] data) { //  命令 (CMD_R_ADVERTISE_PER_SECOND) Callback , 得到 beacon 廣播頻率
        entity.setBroadcastFrequency(BeaconProcessEntity.getAdvertiseFrequency(data));
    }

    private void readDisableAlarmSignals(byte[] data) { //  命令 (CMD_R_DISABLE_ALARM_SIGNALS) Callback , 是否關閉提示聲
        entity.setDisableAlarm((BeaconProcessEntity.getDisableAlarmSignal(data) == 1));
    }

    private void readGSensorSetting(byte[] data) { //  命令 (CMD_R_GSENSOR_SETTING) Callback , 得到 beacon 靈敏度與取樣數
        entity.setSensorSensitive(BeaconProcessEntity.getGsensorSensitive(data));
        entity.setSensorSampling(BeaconProcessEntity.getGsensorSampling(data));
    }

    private void readRestTime(byte[] data) { //  命令 (CMD_R_SECONDS_2_REST) Callback , 得到 beacon 多久進入睡眠的時間
        entity.setRestTime(BeaconProcessEntity.getRestTime(data));
    }

    private void readRestBroadcastFrequency(byte[] data) { //  命令 (CMD_R_REST_FREQ) Callback , 得到 beacon 睡眠時廣播頻率
        entity.setRestBroadcastFrequency(BeaconProcessEntity.getRestTime(data));
    }

    private void readEnableSensorData(byte[] data) { //  命令 (CMD_R_ENABLE_SENSOR_DATA) Callback , 是否開啟sensor
        entity.setSensorStatus(BeaconProcessEntity.getSensorStatus(data));
    }

    private void readEnableAdvertisingTracker(byte[] data) { //  命令 (CMD_R_ENABLE_ADV_STEPS) Callback , 是否開啟計部器
        entity.setTrackerStatus(BeaconProcessEntity.getAdvSteps(data));
    }

    private void readSteps() { // 命令 (CMD_R_STEPS) Callback , 清除計步器
        toastMessageView(this, getString(R.string.step_cleared));
    }

    private void readHwId (byte[] data) {
        entity.setHwId(String.valueOf(BeaconProcessEntity.getHwId(data)));
    }

    private void readUserId (byte[] data) {
        entity.setUserId(String.valueOf(BeaconProcessEntity.getUserID(data)));
    }

    private void readAskKey(byte[] data)
    {
        try {
            entity.setAskKey(BeaconProcessEntity.getAskKey(data));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void readEnableLowBatteryLed(byte[] data) {  // 命令 (CMD_R_ENABLE_LOW_BATTERY_LED) Callback, 是否開啟低電量燈號顯示
        entity.setLowBatteryLedStatus(BeaconProcessEntity.getLedStatus(data));
    }

    private void startUpdateBeaconTask() {
        hideLoadingDialog();
        toastMessageView(this, getString(R.string.successfully_modified));
    }

    @Override
    public void onUpdateBeaconTaskSuccess(boolean isAdd, String response) { // 成功修改在 server 上的裝置
        hideLoadingDialog();
        response = isAdd ? response : entity.getTargetId();
        startWebServiceTask(new BurnBeaconTask(response, entity.getMacAddress(), "burn"));
        toastMessageView(this, getString(R.string.successfully_modified));
    }

    private void initValue() {
        String stringExtra = getIntent().getStringExtra(INTENT_STRING_JSON_DEVICE_ENTITY);
        if (!StringUtil.isEmpty(stringExtra)) {
            this.entity = GsonUtil.generateGenericData(stringExtra, BluetoothDeviceItemEntity.class);
        }
    }

    private void registerListener() { // 註冊命令回應的監聽
        BluetoothConnectDeviceManager.getInstance().setMessageAckListener(this);
    }

    private void addCommandToQueue() { // 新增命令至列表
        switch (entity.getDeviceType()) {
            case REMOTE_TYPE_BEACON:
                BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_REMOTE_ID),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_BEACON_UUID));
            case PROX_TYPE_BEACON:
                BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_ADV_INFO),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_BEACON_UUID),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_GET_INFO),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_TX_POWER));
                break;
            default:
                BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_ADVERTISE_PER_SECOND),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_BEACON_UUID),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_GET_INFO),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_TX_POWER));
                if (entity.getDeviceType() == ORIGINAL_TYPE_BEACON) {
                    break;
                }
                System.out.println("EditDeviceActivity addCommandToQueue step 2 ");
                BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_DISABLE_ALARM_SIGNALS),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_GSENSOR_SETTING),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_SECONDS_2_REST),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_REST_FREQ),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_ENABLE_SENSOR_DATA),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_ENABLE_ADV_STEPS),
                        UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_ENABLE_LOW_BATTERY_LED));
        }

        if (LoginManager.getInstance().isAesSetting())
            BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                    UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_SIGNIN_HWID),
                    UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_SIGNIN_USERID),
                    UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_SIGNIN_AESKEY));

        BluetoothConnectDeviceManager.getInstance().setCommandType(CommandType.Read);
        BluetoothConnectDeviceManager.getInstance().executeCommandTask();
    }

    private void bindContentView() {
        bindBackButton();
        bindViewPager();
        bindResetButton();
        bindSaveButton();
    }

    private void bindBackButton() {
        findViewById(R.id.activityEditDevice_backTextView).setOnClickListener(this);
    }

    private void bindViewPager() {
        List<BaseFragment> fragmentList = generateFragmentList();
        this.viewPager = findViewById(R.id.activityEditDevice_viewPager);
        this.viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        this.viewPager.setAdapter(new BasePagerAdapter(getSupportFragmentManager(), fragmentList));
        this.viewPager.addOnPageChangeListener(this);
        setCurrentPage(INFORMATION_BEACON_PAGE);
    }

    private void bindResetButton() {
        View view = findViewById(R.id.activityEditDevice_resetButton);
        view.setOnClickListener(this);
    }

    private void bindSaveButton() {
        View view = findViewById(R.id.activityEditDevice_saveButton);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activityEditDevice_backTextView:
                onResponseCallBack();
                break;
            case R.id.activityEditDevice_resetButton:
                onResetButtonClick();
                break;
            case R.id.activityEditDevice_saveButton:
                onSaveButtonClick();
                break;
        }
    }

    private void onResetButtonClick() { // reset
        showLoadingDialog(null, "請稍後...");
        isResetting = true;
        writeTimeoutHandler.removeCallbacks(resetTimeoutRunnable);
        writeTimeoutHandler.postDelayed(resetTimeoutRunnable, 20000);
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_FACTORY_RESET));
        BluetoothConnectDeviceManager.getInstance().setCommandType(CommandType.Write);
        BluetoothConnectDeviceManager.getInstance().executeCommandTask();
    }

    private void onSaveButtonClick() { // 確定修改資料至beacon
        String uuidError = validateBeaconUuidInput();
        if (!StringUtil.isEmpty(uuidError)) {
            toastMessageView(this, uuidError);
            return;
        }
        showCustomDialog(R.drawable.ic_usbeacon, "USBeacon Writer",
                getString(R.string.save_warning), false,
                (dialog, which) -> startUpdateDataToBeacon());
    }

    private String validateBeaconUuidInput() {
        if (getInformationBeaconFragment() == null) {
            return null;
        }
        String uuid = getInformationBeaconFragment().getBeaconUuidValue();
        if (StringUtil.isEmpty(uuid)) {
            return null;
        }
        if (uuid.length() != 36) {
            return "UUID 長度需為 36（含 4 個 '-'）";
        }
        if (!uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            return "UUID 格式錯誤，需為 8-4-4-4-12 的 16 進位";
        }
        return null;
    }

    /*
    When the device is of the original beacon type , the commands in the fourth page cannot be added.
     */
    private void startUpdateDataToBeacon() {
        showLoadingDialog(null, "請稍後...");
        isWriting = true;
        writeTimeoutHandler.removeCallbacks(writeTimeoutRunnable);
        writeTimeoutHandler.postDelayed(writeTimeoutRunnable, 20000);
        updateAccessUuidCommand();
        switch (entity.getDeviceType()) {
            case REMOTE_TYPE_BEACON:
                updateRemoteIdCommand();
            case PROX_TYPE_BEACON:
                updateTwPowerCommand();
                updateBeaconUuidCommand();
                updateAdvertisingInformation();
                break;
            case ORIGINAL_TYPE_BEACON:
                updateBeaconUuidCommand();
                updateMajorCommand();
                updateMinorCommand();
                updateTwPowerCommand();
                updateBroadcastFrequencyCommand();
                updateHwIdCommand();
                updateUserIdCommand();
                break;
            default: // B3339, wearable
                updateBeaconUuidCommand();
                updateMajorCommand();
                updateMinorCommand();
                updateTwPowerCommand();
                updateBroadcastFrequencyCommand();
                updateDelayRestTimeCommand();
                updateRestBroadcastFrequencyCommand();
                updateBuzzerCommand();
                updateGSensorSettingCommand();
                updateHwIdCommand();
                updateUserIdCommand();
                break;
        }
        BluetoothConnectDeviceManager.getInstance().setCommandType(CommandType.Write);
        BluetoothConnectDeviceManager.getInstance().executeCommandTask();
    }

    private void updateAccessUuidCommand() { // 將 自己的 access uuid 複寫到裝置上
        byte[] accessUuidBytes = LoginManager.getInstance().getAccountDataEntity().generateAccessUuid();
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_SET_ACC_UUID, accessUuidBytes));
    }

    private void updateBeaconUuidCommand() {  // 將 自己的 beacon uuid 複寫到裝置上
        String customUuid = getInformationBeaconFragment().getBeaconUuidValue();
        byte[] beaconUuidBytes = null;
        if (!StringUtil.isEmpty(customUuid)) {
            beaconUuidBytes = AccountDataEntity.parseUuidBytes(customUuid);
        }
        if (beaconUuidBytes == null) {
            beaconUuidBytes = LoginManager.getInstance().getAccountDataEntity().generateBeaconUuid();
        }
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_BEACON_UUID, beaconUuidBytes));
    }

    private void updateMajorCommand() { // 修改 major
        int majorValue = getInformationBeaconFragment().getMajorValue();
        byte[] majorBytes = BytesUtil.getIntegerToBytes(majorValue);
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_BEACON_MAJOR, majorBytes));
        entity.setMajor(String.valueOf(majorValue));
    }

    private void updateMinorCommand() {  // 修改 minor
        int minorValue = getInformationBeaconFragment().getMinorValue();
        byte[] minorBytes = BytesUtil.getIntegerToBytes(minorValue);
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_BEACON_MINOR, minorBytes));
        entity.setMinor(String.valueOf(minorValue));
    }

    private void updateTwPowerCommand() { // 修改 txPower
        int spinnerItemPosition = getPowerBeaconFragment().getSpinnerItemPosition();
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_TX_POWER, (byte) spinnerItemPosition));
        entity.setTxPowerIndex(spinnerItemPosition);
    }

    private void updateBroadcastFrequencyCommand() { // 修改 廣播頻率
        byte[] broadcastFrequencyBytes = new byte[3];
        int broadcastFrequencyValue = getPowerBeaconFragment().getBroadcastFrequencyValue();
        broadcastFrequencyBytes[0] = (byte) broadcastFrequencyValue;
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_ADVERTISE_PER_SECOND, broadcastFrequencyBytes));
        entity.setBroadcastFrequency(broadcastFrequencyValue);
    }

    private void updateBuzzerCommand() { // 修改蜂鳴器設定
        byte[] buzzerBytes = getWearableBeaconFragment().getBuzzerBytes();
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_BUZZER, buzzerBytes));
    }

    private void updateDelayRestTimeCommand() { // 修改多久進入睡眠的時間
        int delayRestTimeValue = getWearableBeaconFragment().getDelayRestTimeValue();
        byte[] delayRestTimeBytes = BytesUtil.getIntegerToBytes2(delayRestTimeValue);
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_SECONDS_2_REST, delayRestTimeBytes));
        entity.setRestTime(delayRestTimeValue);
    }

    private void updateRestBroadcastFrequencyCommand() { // 修改睡眠時廣播的頻率
        int restBroadcastFrequencyValue = getWearableBeaconFragment().getRestBroadcastFrequencyValue();
        byte[] restBroadcastFrequencyBytes = BytesUtil.getIntegerToBytes2(restBroadcastFrequencyValue);
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_REST_FREQ, restBroadcastFrequencyBytes));
        entity.setRestBroadcastFrequency(restBroadcastFrequencyValue);
    }

    private void updateGSensorSettingCommand() { // 修改 Sensor 的設定
        if (BluetoothConnectDeviceManager.getInstance().isNeedAuth()) {
            byte[] gSensorBytes = new byte[3];
            int gSensorSampling = getWearableBeaconFragment().getGSensorSampling();
            int gSensorSensitive = getWearableBeaconFragment().getGSensorSensitive();
            gSensorBytes[0] = (byte) gSensorSensitive;
            gSensorBytes[1] = (byte) (gSensorSampling & 0x00FF);
            gSensorBytes[2] = (byte) ((gSensorSampling >>> 8) & 0x00FF);
            BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                    UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_GSENSOR_SETTING, gSensorBytes));
        }
    }

    private void updateRemoteIdCommand() { // 修改 remoteId
        byte[] remoteIdBytes = new byte[2];
        int majorValue = getInformationBeaconFragment().getMajorValue();
        remoteIdBytes[0] = (byte) (majorValue & 0x00FF);
        remoteIdBytes[1] = (byte) ((majorValue >>> 8) & 0x00FF);
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_REMOTE_ID, remoteIdBytes));
        entity.setMajor(String.valueOf(majorValue));
        entity.setMinor("0");
    }

    private void updateAdvertisingInformation() { // 修改廣播的資訊
        byte[] bytes = new byte[10];
        int broadcastFrequencyValue = getPowerBeaconFragment().getBroadcastFrequencyValue();
        int delayRestTimeValue = getWearableBeaconFragment().getDelayRestTimeValue();
        int restBroadcastFrequencyValue = getWearableBeaconFragment().getRestBroadcastFrequencyValue();
        bytes[0] = (byte) ((broadcastFrequencyValue * 60) & 0x00FF);
        bytes[1] = (byte) (((broadcastFrequencyValue * 60) >>> 8) & 0x00FF);
        bytes[2] = 1;
        bytes[3] = (byte) (delayRestTimeValue & 0x00FF);
        bytes[4] = (byte) ((delayRestTimeValue >>> 8) & 0x00FF);
        bytes[5] = (byte) (delayRestTimeValue & 0x00FF);
        bytes[6] = (byte) ((delayRestTimeValue >>> 8) & 0x00FF);
        bytes[7] = (byte) (restBroadcastFrequencyValue & 0x00FF);
        bytes[8] = (byte) ((restBroadcastFrequencyValue >>> 8) & 0x00FF);
        bytes[9] = 45;
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_ADV_INFO, bytes));
        entity.setBroadcastFrequency(broadcastFrequencyValue);
        entity.setRestTime(delayRestTimeValue);
        entity.setRestBroadcastFrequency(restBroadcastFrequencyValue);
    }

    private void updateHwIdCommand() {  //Update Hardware ID
        String hwIdValue = getInformationBeaconFragment().getHwIdValue();

        if (!StringUtil.isEmpty(hwIdValue))
        {
            byte[] hwIdBytes = BytesUtil.hexStringToByteArray(new BigInteger(hwIdValue).toString(16));
            byte[] hwIdFourBytes = new byte[4];  //HW ID is 4 bytes.

            System.arraycopy(hwIdBytes, 0, hwIdFourBytes, 4 - hwIdBytes.length, hwIdBytes.length);
            BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                    UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_SIGNIN_HWID, hwIdFourBytes));

            System.out.println("updateHwIdCommand: " + BytesUtil.getHexString(hwIdFourBytes));
            entity.setHwId(hwIdValue);
        }
    }

    private void updateUserIdCommand() {
        String userIdValue = getInformationBeaconFragment().getUserIdValue();

        if (!StringUtil.isEmpty(userIdValue)) {
            byte[] userIdBytes = BytesUtil.hexStringToByteArray(new BigInteger(userIdValue).toString(16));
            byte[] userIdFourBytes = new byte[4];   //User ID is 4 bytes.

            System.arraycopy(userIdBytes, 0, userIdFourBytes, 4 - userIdBytes.length, userIdBytes.length);
            BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                    UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_SIGNIN_USERID, userIdFourBytes));

            System.out.println("updateUserIdCommand: " + BytesUtil.getHexString(userIdFourBytes));
            entity.setUserId(userIdValue);
        }
    }

    private List<BaseFragment> generateFragmentList() { // 取得四頁至列表
        List<BaseFragment> baseFragmentList = new ArrayList<>();
        baseFragmentList.add(getInformationBeaconFragment());
        baseFragmentList.add(getPowerBeaconFragment());
        baseFragmentList.add(getMeterRSSIFragment());
        baseFragmentList.add(getWearableBeaconFragment());
        return baseFragmentList;
    }

    private InformationBeaconFragment getInformationBeaconFragment() {
        if (informationBeaconFragment == null) {
            informationBeaconFragment = new InformationBeaconFragment();
        }
        return informationBeaconFragment;
    }

    private PowerBeaconFragment getPowerBeaconFragment() {
        if (powerBeaconFragment == null) {
            powerBeaconFragment = new PowerBeaconFragment();
        }
        return powerBeaconFragment;
    }

    private MeterRSSIFragment getMeterRSSIFragment() {
        if (meterRSSIFragment == null) {
            meterRSSIFragment = new MeterRSSIFragment();
        }
        return meterRSSIFragment;
    }

    private WearableBeaconFragment getWearableBeaconFragment() {
        if (wearableBeaconFragment == null) {
            wearableBeaconFragment = new WearableBeaconFragment();
        }
        return wearableBeaconFragment;
    }

    private void setStyleBackground(int page) { // 換頁時按鈕樣式改變
        int saveDrawable;
        int toolbarDrawable;
        switch (page) {
            case INFORMATION_BEACON_PAGE:
                saveDrawable = R.drawable.btn_info_save;
                toolbarDrawable = R.drawable.beacon_config_head;
                break;
            case POWER_BEACON_PAGE:
                saveDrawable = R.drawable.btn_pm_save;
                toolbarDrawable = R.drawable.beacon_config_head;
                break;
            case DISTANCE_BEACON_PAGE:
                saveDrawable = R.drawable.btn_ca_save;
                toolbarDrawable = R.drawable.calibrate_head;
                break;
            default:
                saveDrawable = R.drawable.btn_wearable_save;
                toolbarDrawable = R.drawable.beacon_config_head;
                break;
        }
        findViewById(R.id.activityEditDevice_saveButton).setBackgroundResource(saveDrawable);
        findViewById(R.id.activityEditDevice_contentFrameLayout).setBackgroundResource(toolbarDrawable);
    }

    private void setUpModifiedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        Date curDate = new Date(System.currentTimeMillis());
        LoginManager.getInstance().setLastModifiedDate(formatter.format(curDate));
    }

    private void setUpFastSetting() {
        if (isUpdate && LoginManager.getInstance().isFastSetting()) {
            LoginManager.getInstance().setMajor(String.valueOf(getInformationBeaconFragment().getMajorValue()));
            LoginManager.getInstance().setMinor(String.valueOf(getInformationBeaconFragment().getMinorValue() + 1));
            LoginManager.getInstance().setBroadcastFrequency(String.valueOf(getPowerBeaconFragment().getBroadcastFrequencyValue()));
            LoginManager.getInstance().setTxPowerIndex(getPowerBeaconFragment().getSpinnerItemPosition());
            if (entity.getDeviceType() != ORIGINAL_TYPE_BEACON) {
                LoginManager.getInstance().setRestTime(String.valueOf(getWearableBeaconFragment().getDelayRestTimeValue()));
                LoginManager.getInstance().setRestBroadcastFrequency(String.valueOf(getWearableBeaconFragment().getRestBroadcastFrequencyValue()));
            }
        }
    }

    protected void setCurrentPage(int currentPage) { // 換成指定的頁數
        if ((entity.getDeviceType() == ORIGINAL_TYPE_BEACON )//|| entity.getDeviceType() == B3339B_BEACON)
                && currentPage == WEARABLE_BEACON_PAGE) {
            toastMessageView(this, "這顆 Beacon 不支持此功能....");
        } else if (viewPager != null) {
            this.viewPager.setCurrentItem(currentPage);
        }
    }

    private void onResponseCallBack() { // 將修改完的 entity 回傳
        Bundle bundle = new Bundle();
        bundle.putSerializable(OUTPUT_ENTITY_DEVICE_INFO, entity);
        setResult(RESULT_OK, new Intent().putExtras(bundle));
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) { // 監聽 back 鍵
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onResponseCallBack();
        }
        return false;
    }
}