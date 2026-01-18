package com.THLight.BLE.USBeacon.Writer.Simple.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.THLight.BLE.USBeacon.Writer.Simple.entity.command.UsBeaconCommand;
import com.THLight.BLE.USBeacon.Writer.Simple.util.BytesUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BluetoothConnectDeviceManager extends BluetoothGattCallback {
    private static final UUID UUID_CUSTOM_SERVICE = UUID.fromString("0000f000-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_DEVICE_INFO_SERVICE = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_VERSION_CHARACTERISTIC = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb");
    private static BluetoothConnectDeviceManager instance;
    private ReadVersionCharacteristicListener readVersionCharacteristicListener;
    private CharacteristicChangeListener characteristicChangeListener;
    private DiscoverServiceListener discoverServiceListener;
    private ConnectDeviceStateListener connectDeviceStateListener;
    private ReadRemoteRSSIListener readRemoteRSSIListener;
    private MessageAckListener messageAckListener;
    private BluetoothGattCharacteristic versionCharacteristic;
    private BluetoothGattCharacteristic commandCharacteristic;
    private BluetoothGattCharacteristic ackCharacteristic;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private Timer countDownTimer;
    private Context context;
    private int connectState;
    private byte SECURITY_KEY = (byte) 0x5A;
    private int commandCode; // 被執行到的指令代號
    private List<byte[]> gattWriteCommandList = new ArrayList<>();
    private CommandType commandType = CommandType.UnKnown;

    public static BluetoothConnectDeviceManager getInstance() {
        if (instance == null) {
            instance = new BluetoothConnectDeviceManager(ApplicationManager.getInstance().getContext());
        }
        return instance;
    }

    private BluetoothConnectDeviceManager(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            this.bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public void startConnectDeviceTask(String addressString) { // 與裝置連線
        disConnectDevice();
        if (bluetoothAdapter == null || StringUtil.isEmpty(addressString) || bluetoothAdapter.getRemoteDevice(addressString) == null) {
            this.connectDeviceStateListener.onConnectDeviceStateError();
            return;
        }
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(addressString);
        startCountDownTimer();
        this.bluetoothGatt = bluetoothDevice.connectGatt(context, false, this);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) { // 裝置連線變化Callback
        System.out.println("ConnectBLE onConnectionStateChange BluetoothGatt : " + status + " , " + newState);
        if ((gatt != null) && (BluetoothGatt.GATT_SUCCESS == status) && (newState == BluetoothProfile.STATE_CONNECTED)) {
            this.connectState = newState;
            this.connectDeviceStateListener.onConnectDeviceStateSuccess();
        } else if (status == 133) {
            disConnectDevice();
            this.connectDeviceStateListener.onConnectDeviceStateError();
        } else {
            disConnectDevice();
            this.connectDeviceStateListener.onDisconnectDeviceState();
        }
    }

    /* Called after the connection is successful,
     it is recommended on the Internet that after the connection is successful,
     wait 2 seconds and then find the service is more stable */
    public void discoverService() { // 尋找 裝置的 service
        try {
            Thread.sleep(2500);
            bluetoothGatt.discoverServices();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) { // 尋找 裝置的 service Callback
        System.out.println("ConnectBLE onServicesDiscovered");
        if (gatt != null && status == BluetoothGatt.GATT_SUCCESS) {
            System.out.println("ConnectBLE onServicesDiscovered GATT_SUCCESS");
            getDeviceDataService(gatt);
            getCustomService(gatt);
        }
    }

    private void getDeviceDataService(BluetoothGatt gatt) { // 取得 官方 device info service
        System.out.println("ConnectBLE generateDeviceDataService  ");
        BluetoothGattService deviceDataService = gatt.getService(UUID_DEVICE_INFO_SERVICE);
        if (deviceDataService != null) {
            this.versionCharacteristic = deviceDataService.getCharacteristic(UUID_VERSION_CHARACTERISTIC);
            System.out.println("ConnectBLE generateDeviceDataService versionCharacteristic " + versionCharacteristic.getUuid());
        }
    }

    private void getCustomService(BluetoothGatt gatt) { // 取得 自定 service
        BluetoothGattService uuidService = gatt.getService(UUID_CUSTOM_SERVICE);
        if (uuidService != null) {
            getCommandCharacteristic(uuidService);
            getAckCharacteristic(uuidService);
            getNotifyCharacteristic(uuidService);
            stopCountDownTimer();
            this.discoverServiceListener.onDiscoverServiceSuccess();
            System.out.println("ConnectBLE generateUuidService step 1 ");
        } else {
            System.out.println("ConnectBLE generateUuidService step 2 ");
            disConnectDevice();
            this.discoverServiceListener.onDiscoverServiceFailed();
        }
    }

    private void getCommandCharacteristic(BluetoothGattService uuidService) { // 取得 characteristic (write)
        this.commandCharacteristic = getCustomCharacteristic(uuidService, BluetoothGattCharacteristic.PROPERTY_WRITE);
        if (commandCharacteristic != null) {
            this.commandCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            System.out.println("ConnectBLE generateCommandCharacteristic : " + commandCharacteristic.getUuid());
        }
    }

    private void getAckCharacteristic(BluetoothGattService uuidService) { // 取得 characteristic (read)
        this.ackCharacteristic = getCustomCharacteristic(uuidService, BluetoothGattCharacteristic.PROPERTY_READ);
        System.out.println("ConnectBLE generateAckCharacteristic : " + ackCharacteristic.getUuid());
    }

    private void getNotifyCharacteristic(BluetoothGattService uuidService) { // 取得 characteristic (notify)
        BluetoothGattCharacteristic notifyCharacteristic = getCustomCharacteristic(uuidService, BluetoothGattCharacteristic.PROPERTY_NOTIFY);
        if (notifyCharacteristic != null) {
            this.bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, false);
            System.out.println("ConnectBLE generateNotifyCharacteristic : " + notifyCharacteristic.getUuid());
            List<BluetoothGattDescriptor> bluetoothGattDescriptorList = notifyCharacteristic.getDescriptors();

            for (BluetoothGattDescriptor descriptor : bluetoothGattDescriptorList) { // 開啟數據變化通知
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                this.bluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) { // Ble Characteristic 數據改變
        byte[] data = characteristic.getValue();
        if (data != null && characteristicChangeListener != null) {
            this.characteristicChangeListener.onCharacteristicResponse(data);
        }
    }

    public void addCommandToQueue(final byte[]... data) { // 新增命令至列表
        for (byte[] command : data) {
            this.gattWriteCommandList.add(generateEncryptData(command));
        }
    }

    public void setCommandType(CommandType commandType) { // 設定目前所在列表中的命令,目的為何
        this.commandType = commandType;
    }

    public void executeCommandTask() { // 執行列表中的命令 , 依序跑完直至為空
        System.out.println("ConnectBLE Command executeCommandTask gattWriteCommandList  : " + gattWriteCommandList.size() + this.commandType);
        if (isBlueToothConnected() && gattWriteCommandList.size() > 0) {
            commandWriteData(gattWriteCommandList.get(0));
        } else {
            this.messageAckListener.onMessageAckResponseDone(commandType);
            this.commandType = CommandType.UnKnown;
        }
    }

    private void commandWriteData(byte[] data) { // 將命令寫入 beacon 中
        if (commandCharacteristic != null && data != null) {
            System.out.println("ConnectBLE commandWriteData -- >  " + BytesUtil.getHexString(data));
            this.commandCharacteristic.setValue(data);
            this.bluetoothGatt.writeCharacteristic(commandCharacteristic);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) { // 將命令寫入 beacon 中 Callback
        byte[] bytes = decryptData(characteristic.getValue());  //only for log.
        this.commandCode = bytes[1];
        System.out.println("ConnectBLE onCharacteristicWrite  : " + BytesUtil.getHexString(commandCode) + " , " + BytesUtil.getHexString(bytes));
        if (messageAckListener != null) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (bluetoothGatt != null && ackCharacteristic != null) {
                    this.bluetoothGatt.readCharacteristic(ackCharacteristic);
                }
            } else {
                this.messageAckListener.onMessageAckResponseFailed(commandCode);
            }
        }
    }


    private boolean isDeviceBusy(){
        boolean state = false;
        try {
            state = (boolean)readField(bluetoothGatt,"mDeviceBusy");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return state;
    }
    public  Object readField(Object object, String name) throws IllegalAccessException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    public void readVersionCharacteristic(ReadVersionCharacteristicListener listener) { // 讀取 beacon 的回應

        if (isBlueToothConnected() && versionCharacteristic != null && !isDeviceBusy()) {
            this.readVersionCharacteristicListener = listener;
            boolean rtn = bluetoothGatt.readCharacteristic(versionCharacteristic);
            System.out.println("ConnectBLE readVersionCharacteristic : " + versionCharacteristic.getUuid() + " " + rtn);
        }else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ConnectBLE readVersionCharacteristic : device is busy");
            readVersionCharacteristic(listener);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) { //  讀取 beacon 的回應 Callback
        System.out.println("ConnectBLE onCharacteristicRead " + characteristic.getUuid() + " status: " + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (versionCharacteristic == characteristic) {
                onVersionCharacteristicRead(characteristic);
            } else if (ackCharacteristic == characteristic) {
                gattWriteCommandList.remove(0);
                onAckCharacteristicRead(characteristic);
                executeCommandTask();
            }
        }
    }

    private void onVersionCharacteristicRead(BluetoothGattCharacteristic characteristic) { // 讀取 beacon 的版本
        String characteristicValue = new String(characteristic.getValue());
        System.out.println("ConnectManager onVersionCharacteristicRead originValue: " + characteristicValue);
        String[] versionKey = characteristicValue.split("-");
        String versionCharacteristicString;
        if (versionKey.length == 2) {
            versionCharacteristicString = versionKey[0];
            System.out.println("ConnectManager onVersionCharacteristicRead versionKey[0] : " + versionKey[0]);
            byte[] b = new BigInteger(versionKey[1], 16).toByteArray();
            b[1] = (byte) (b[1] ^ 0xFF);
            String key = BytesUtil.getHexString(b[1]);
            char char0 = key.charAt(0);
            char char1 = key.charAt(1);
            key = "" + char1 + char0;
            byte[] b2 = new BigInteger(key, 16).toByteArray();
            SECURITY_KEY = b2[1];
        } else {
            versionCharacteristicString = characteristicValue;
            SECURITY_KEY = 0x5A;
            System.out.println("ConnectManager onVersionCharacteristicRead characteristicValue " + versionCharacteristicString);
        }
        int versionCharacteristicInt = getVersionCharacteristicInt(versionCharacteristicString);
        System.out.println("ConnectBLE onVersionCharacteristicRead versionCharacteristicInt : " + versionCharacteristicInt);
        this.readVersionCharacteristicListener.onReadVersionCharacteristicResponse(versionCharacteristicInt);
    }

    private void onAckCharacteristicRead(BluetoothGattCharacteristic characteristic) { // 將 beacon 的回應交給監聽者
        if (characteristic.getValue() != null) {
            byte[] data = decryptData(characteristic.getValue());
            byte[] ackData = Arrays.copyOfRange(data, 2, data.length);
            System.out.println("ConnectBLE onAckCharacteristicRead Ack : " + BytesUtil.getHexString(data[1]) + " , " + BytesUtil.getHexString(ackData));
            this.messageAckListener.onMessageAckResponseSuccess(commandCode, data[1], ackData);
        }
    }

    public void readRemoteRSSI(ReadRemoteRSSIListener readRemoteRSSIListener) { // 連線讀取RSSI
        if (isBlueToothConnected()) {
            this.readRemoteRSSIListener = readRemoteRSSIListener;
            this.bluetoothGatt.readRemoteRssi();
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) { // 連線讀取RSSI的Callback
        this.readRemoteRSSIListener.onReadRemoteRSSIResponse(rssi);
    }

    public void destroyConnectDeviceManager() { // 銷毀所有東西
        disConnectDevice();
        this.characteristicChangeListener = null;
        this.discoverServiceListener = null;
        this.connectDeviceStateListener = null;
        this.readRemoteRSSIListener = null;
        this.messageAckListener = null;
        this.bluetoothAdapter = null;
        this.gattWriteCommandList = null;
        instance = null;
    }

    public void disConnectDevice() { // 中斷目前的連線
        if (bluetoothGatt != null) {
            this.gattWriteCommandList.clear();
            this.bluetoothGatt.disconnect();
            this.bluetoothGatt.close();
            this.bluetoothGatt = null;
            this.commandType = CommandType.UnKnown;
            this.connectState = BluetoothProfile.STATE_DISCONNECTED;
        }
        stopCountDownTimer();
    }

    private byte[] decryptData(byte[] data) { // 解密命令
        byte[] resultData = data.clone();
        int key = SECURITY_KEY < 0 ? 255 + SECURITY_KEY + 1 : SECURITY_KEY;
        for (int i = data.length - 1; 0 < i; i--) {
            switch (resultData[0] & 3) {
                case 0:
                    resultData[i] = (key >= 0x80 || key == 0x5A) ? (byte) (data[i] ^ SECURITY_KEY) : (byte) (~(data[i] ^ SECURITY_KEY));
                    break;
                case 1:
                    resultData[i] = (key >= 0x80 || key == 0x5A) ? (byte) (~(data[i] ^ SECURITY_KEY)) : (byte) (data[i] ^ SECURITY_KEY);
                    break;
                case 2:
                    resultData[i] = (byte) (((data[i] << 6) & 0xC0) | ((data[i] >>> 2) & 0x3F));
                    break;
                case 3:
                    resultData[i] = (byte) (((data[i] << 2) & 0xFD) | ((data[i] >>> 6) & 0x03));
                    break;
            }
        }
        return resultData;
    }

    private byte[] encryptData(byte[] data, int length) { // 加密命令
        byte[] resultData = data.clone();
        Random random = new Random();
        resultData[0] = (byte) (random.nextInt() & 0xFF);
        int key = SECURITY_KEY < 0 ? 255 + SECURITY_KEY + 1 : SECURITY_KEY;
        for (int i = length - 1; 0 < i; i--) {
            switch ((resultData[0] & 3)) {
                case 0:
                    resultData[i] = (key >= 0x80 || key == 0x5A) ? (byte) ((data[i] ^ SECURITY_KEY) & 0xFF) : (byte) ((~(data[i] ^ SECURITY_KEY)) & 0xFF);
                    break;
                case 1:
                    resultData[i] = (key >= 0x80 || key == 0x5A) ? (byte) ((~(data[i] ^ SECURITY_KEY)) & 0xFF) : (byte) ((data[i] ^ SECURITY_KEY) & 0xFF);
                    break;
                case 2:
                    resultData[i] = (byte) ((((data[i] << 2) & 0xFD) | ((data[i] >>> 6) & 0x03)) & 0xFF);
                    break;
                case 3:
                    resultData[i] = (byte) ((((data[i] << 6) & 0xC0) | ((data[i] >>> 2) & 0x3F)) & 0xFF);
                    break;
            }
        }
        if (length < UsBeaconCommand.BUF_LEN) {
            for (int i = UsBeaconCommand.BUF_LEN - 1; i >= length; i--) {
                resultData[i] = (byte) (random.nextInt() & 0xFF);
            }
        }
        return resultData;
    }

    private void startCountDownTimer() { // 設定 連線 -> 成功找到服務 的時間限制 , 超過會中斷連線
        this.countDownTimer = new Timer(true);
        this.countDownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("ConnectBLE startCountDownTimer onDisconnectDeviceState");
                if (discoverServiceListener != null) {
                    discoverServiceListener.onDiscoverServiceFailed();
                }
                disConnectDevice();
            }
        }, 8000);
    }

    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            this.countDownTimer.cancel();
        }
    }

    private boolean isBlueToothConnected() { // 是否目前已連線
        return bluetoothGatt != null && connectState == BluetoothProfile.STATE_CONNECTED;
    }

    public boolean isNeedAuth() {
        return SECURITY_KEY == 0x5A;
    }

    public void setConnectDeviceStateListener(ConnectDeviceStateListener connectDeviceStateListener) {
        this.connectDeviceStateListener = connectDeviceStateListener;
    }

    public void setDiscoverServiceListener(DiscoverServiceListener discoverServiceListener) {
        this.discoverServiceListener = discoverServiceListener;
    }

    public void setCharacteristicChangeListener(CharacteristicChangeListener characteristicChangeListener) {
        this.characteristicChangeListener = characteristicChangeListener;
    }

    public void setMessageAckListener(MessageAckListener messageAckListener) {
        this.messageAckListener = messageAckListener;
    }

    private int getVersionCharacteristicInt(String versionCharacteristicString) { // 取得版本號
        int result = 0;
        if (!StringUtil.isEmpty(versionCharacteristicString)) {
            Pattern compile = Pattern.compile("[.a-zA-Z]");
            Matcher matcher = compile.matcher(versionCharacteristicString);
            // If versionCharacteristicString isn't positive, set it 0.
            if (StringUtil.isPositiveInteger(matcher.replaceAll("")))
                result = Integer.parseInt(matcher.replaceAll(""));
            else
                result = 0;
        }
        System.out.println("getVersionCharacteristicString - > " + result);
        return result;
    }

    private byte[] generateEncryptData(byte[] data) { // 根據命令不同 , 加密的長度不同
        System.out.println("ConnectBLE generateEncryptData : " + BytesUtil.getHexString(data));
        byte[] encryptBytes = null;
        if (isBlueToothConnected()) {
            switch (data[1]) {
                case UsBeaconCommand.CMD_FACTORY_RESET:                /** 0x01, factory reset and reboot usbeacon, cmd(rule + 0x01) */
                case UsBeaconCommand.CMD_GET_NAME:                    /** 0x07**/
                case UsBeaconCommand.CMD_GET_MAC_ADDR:                /** 0x05, cmd(rule + 0x05), response 8 bytes, data(rule + 0x05 + mac_addr) */
                case UsBeaconCommand.CMD_R_STEPS:
                case UsBeaconCommand.CMD_R_REMOTE_ID:
                case UsBeaconCommand.CMD_R_SIGNIN_USERID:
                case UsBeaconCommand.CMD_R_SIGNIN_HWID:
                case UsBeaconCommand.CMD_R_SIGNIN_AESKEY:
                    /** advertise data. */
                case UsBeaconCommand.CMD_R_BEACON_UUID:                /** 0x10, response 18 bytes, data(rule + 0x10 + beacon_uuid). */
                case UsBeaconCommand.CMD_R_BEACON_MAJOR:            /** 0x11, response 4 bytes, data(rule + 0x11 + MAJOR). */
                case UsBeaconCommand.CMD_R_BEACON_MINOR:            /** 0x12, response 4 bytes, data(rule + 0x12 + MINOR). */
                case UsBeaconCommand.CMD_R_BEACON_RSSI:                /** 0x13, response 3 bytes, data(rule + 0x13 + RSSI). */
                case UsBeaconCommand.CMD_R_SYNC_WORK_ADJUST_TIME:
                    /** request information, command only. */
                case UsBeaconCommand.CMD_R_ADVERTISE_PER_SECOND:
                case UsBeaconCommand.CMD_R_TX_POWER:                    /** 0x16, response 3 bytes, data(rule + 0x16 + tx_power). */
                case UsBeaconCommand.CMD_R_MOVE_STATUS:
                case UsBeaconCommand.CMD_R_SECONDS_2_REST:            /** 0x2C**/
                case UsBeaconCommand.CMD_GET_BEACON_TYPE:             /** 0x2E, Get beacon type by command.*/
                case UsBeaconCommand.CMD_R_REST_FREQ:
                case UsBeaconCommand.CMD_R_ENABLE_SENSOR_DATA:
                case UsBeaconCommand.CMD_R_ENABLE_ADV_STEPS:
                case UsBeaconCommand.CMD_R_ENABLE_ALARM:
                case UsBeaconCommand.CMD_R_DISABLE_ALARM_SIGNALS:
                case UsBeaconCommand.CMD_R_ADV_INFO:
                case UsBeaconCommand.CMD_R_ENABLE_LOW_BATTERY_LED:     // 0x70, Read low battery led status.
                    encryptBytes = encryptData(data, 2);
                    break;
                case UsBeaconCommand.CMD_CHECK_ACC_UUID:            /** 0x03, authorization, cmd(rule + 0x03 + acc_uuid) */
                case UsBeaconCommand.CMD_SET_ACC_UUID:                /** 0x04, 18 bytes, cmd(rule + 0x04 + acc_uuid) */
                case UsBeaconCommand.CMD_SET_NAME:                    /** 0x08**/
                case UsBeaconCommand.CMD_W_SIGNIN_AESKEY:
                case UsBeaconCommand.CMD_W_BEACON_UUID:
                case UsBeaconCommand.CMD_ADMIN_FACTORY_RESET:            /** 0x30, 18 bytes, cmd(rule + 0x30 + admin_key) */
                    /** 0x20, 18 bytes, cmd(rule + 0x20 + beacon_uuid) */
                    encryptBytes = encryptData(data, 18);
                    break;
                case UsBeaconCommand.CMD_SET_MAC_ADDR:                /** 0x09**/
                case UsBeaconCommand.CMD_GET_INFO:                    /** 0x06, cmd(rule + 0x06), response 4 bytes, data(rule + 0x06 + FW_VER --> Big-Endian) */
                    encryptBytes = encryptData(data, 7);
                    break;
                /** advertise data. */
                case UsBeaconCommand.CMD_W_BEACON_RSSI:
                case UsBeaconCommand.CMD_W_TX_POWER:                    /** 0x26, 3 bytes, cmd(rule + 0x26 + tx_power) */
                case UsBeaconCommand.CMD_W_ENABLE_SENSOR_DATA:
                case UsBeaconCommand.CMD_W_ENABLE_ADV_STEPS:
                case UsBeaconCommand.CMD_W_ENABLE_ALARM:
                case UsBeaconCommand.CMD_W_DISABLE_ALARM_SIGNALS:
                case UsBeaconCommand.CMD_W_ENABLE_LOW_BATTERY_LED:      //0x71, 3bytes, cmd(rule + 0x71 + On(1)/Off(0)
                    /** 0x23, 3 bytes, cmd(rule + 0x23 + Rssi) */
                    encryptBytes = encryptData(data, 3);
                    break;
                case UsBeaconCommand.CMD_W_BEACON_MAJOR:            /** 0x21, 4 bytes, cmd(rule + 0x21 + Major --> Big-Endian) */
                case UsBeaconCommand.CMD_W_BEACON_MINOR:            /** 0x22, 4 bytes, cmd(rule + 0x22 + Minor --> Big-Endian) */
                case UsBeaconCommand.CMD_W_REMOTE_ID:
                case UsBeaconCommand.CMD_W_SECONDS_2_REST:            /** 0x2B**/
                case UsBeaconCommand.CMD_W_REST_FREQ:
                    encryptBytes = encryptData(data, 4);
                    break;
                /** write information. */
                case UsBeaconCommand.CMD_W_ADVERTISE_PER_SECOND:                /** 0x24, 3 bytes, cmd(rule + 0x24 + frequency(1)+delay_time(2)) */
                case UsBeaconCommand.CMD_W_GSENSOR_SETTING:
                case UsBeaconCommand.CMD_R_GSENSOR_SETTING:
                    encryptBytes = encryptData(data, 5);
                    break;
                case UsBeaconCommand.CMD_W_SIGNIN_USERID:
                case UsBeaconCommand.CMD_W_SIGNIN_HWID:
                    encryptBytes = encryptData(data, 6);
                    break;
                case UsBeaconCommand.CMD_W_SYNC_WORK_ADJUST_TIME:
                case UsBeaconCommand.CMD_R_LINE_INFO:
                case UsBeaconCommand.CMD_W_LINE_INFO:
                    encryptBytes = encryptData(data, 15);
                    break;
                case UsBeaconCommand.CMD_W_BUZZER:
                    encryptBytes = encryptData(data, 9);
                    break;
                case UsBeaconCommand.CMD_W_ADV_INFO:
                    encryptBytes = encryptData(data, 12);
                    break;
            }
        }
        return encryptBytes;
    }

    private BluetoothGattCharacteristic getCustomCharacteristic(BluetoothGattService bluetoothGattService, int characteristicType) { // 找出 service 中 某種 type 的 Characteristic
        for (BluetoothGattCharacteristic characteristic : bluetoothGattService.getCharacteristics()) {
            int result = characteristic.getProperties() & characteristicType;
            if (result != 0) {
                return characteristic;
            }
        }
        return null;
    }

    public enum CommandType {
        Write, Read, UnKnown
    }

    public interface ConnectDeviceStateListener {
        void onConnectDeviceStateSuccess();

        void onDisconnectDeviceState();

        void onConnectDeviceStateError();
    }

    public interface DiscoverServiceListener {
        void onDiscoverServiceSuccess();

        void onDiscoverServiceFailed();
    }

    public interface ReadVersionCharacteristicListener {
        void onReadVersionCharacteristicResponse(int characteristicValue);
    }

    public interface CharacteristicChangeListener {
        void onCharacteristicResponse(byte[] data);
    }

    public interface ReadRemoteRSSIListener {
        void onReadRemoteRSSIResponse(int rssi);
    }

    public interface MessageAckListener {
        void onMessageAckResponseSuccess(int code, int responseCode, byte[] data);

        void onMessageAckResponseDone(CommandType commandType);

        void onMessageAckResponseFailed(int flag);
    }
}
