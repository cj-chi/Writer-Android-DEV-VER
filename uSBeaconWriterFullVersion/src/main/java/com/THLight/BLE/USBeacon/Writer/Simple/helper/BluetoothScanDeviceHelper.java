package com.THLight.BLE.USBeacon.Writer.Simple.helper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.THLight.BLE.USBeacon.Writer.Simple.comparator.SortScanDeviceListItemComparator;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.beacon.IBeaconDataEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BluetoothScanDeviceHelper extends ScanCallback implements Handler.Callback {
    private static final int MESSAGE_START_SCAN_DEVICE = 1000;
    private static final int MESSAGE_STOP_SCAN_DEVICE = 1001;
    //-----------------------beacon type---------------------------------
    private static final int ORIGINAL_TYPE_BEACON = 0;
    private static final int REMOTE_TYPE_BEACON = 1;
    private static final int WEARABLE_TYPE_BEACON = 2;
    private static final int DAGGER_TYPE_BEACON = 3;
    private static final int PROX_TYPE_BEACON = 4;
    //-------------------------------------------------------------------
    private ScanDeviceDataListener listener;
    private BluetoothAdapter bluetoothAdapter = null;
    private ScanSettings scanSettings;
    private Handler handler;
    private List<BluetoothDeviceItemEntity> bluetoothDeviceItemEntityList;

    @Override
    public void onScanResult(int callbackType, final ScanResult result) { // 掃描結果 callBack
        super.onScanResult(callbackType, result);
        addScanDeviceItemList(result);
    }

    @Override
    public void onScanFailed(int errorCode) { // 掃描失敗
        super.onScanFailed(errorCode);
        this.listener.onScanDeviceDataErrorResponse(errorCode);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MESSAGE_START_SCAN_DEVICE:
                onMessageStartScanDevice();
                break;
            case MESSAGE_STOP_SCAN_DEVICE:
                onMessageStopScanDevice();
                break;
        }
        return false;
    }

    public BluetoothScanDeviceHelper(Context context, ScanDeviceDataListener listener) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            this.listener = listener;
            this.handler = new Handler(this);
            this.bluetoothDeviceItemEntityList = new ArrayList<>();
            this.bluetoothAdapter = bluetoothManager.getAdapter();
            this.scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        }
    }

    public void startScanDeviceTask() { // 開始掃描 , 預設 5 秒
        handler.sendEmptyMessage(MESSAGE_START_SCAN_DEVICE);
        handler.sendEmptyMessageDelayed(MESSAGE_STOP_SCAN_DEVICE, 5000);
    }

    private void onMessageStartScanDevice() {
        this.bluetoothDeviceItemEntityList.clear();
        this.bluetoothAdapter.getBluetoothLeScanner().startScan(generateScanFilters(), scanSettings, this);
    }

    private void onMessageStopScanDevice() {
        Collections.sort(bluetoothDeviceItemEntityList, new SortScanDeviceListItemComparator());
        this.listener.onScanDeviceDataResponse(bluetoothDeviceItemEntityList);
        this.bluetoothAdapter.getBluetoothLeScanner().stopScan(this);
    }

    private void addScanDeviceItemList(ScanResult scanResult) { // 將掃描到的裝置放入列表中
        if (scanResult != null) {
            BluetoothDevice device = scanResult.getDevice();

            if (!checkScanDeviceExist(device)) {// && !StringUtil.isEmpty(device.getName())) {
                IBeaconDataEntity iBeaconDataEntity;
                if (scanResult.getScanRecord() != null && (iBeaconDataEntity = IBeaconDataEntity.generateIBeacon(scanResult.getScanRecord().getBytes())) != null) {
                    if (!StringUtil.isEquals(iBeaconDataEntity.getBeaconUuid(), "00112233-4455-6677-8899-AABBCCDDEEFF")) {
                        BluetoothDeviceItemEntity entity = new BluetoothDeviceItemEntity();
                        entity.setDeviceName(device.getName());
                        entity.setRssi(String.valueOf(scanResult.getRssi()));
                        entity.setMacAddress(device.getAddress());
                        entity.setMajor(String.valueOf(iBeaconDataEntity.getMajor()));
                        entity.setMinor(String.valueOf(iBeaconDataEntity.getMinor()));
                        entity.setMeterRssi(String.valueOf(iBeaconDataEntity.getOneMeterRssi()));
                        entity.setUuid(iBeaconDataEntity.getBeaconUuid());
                        entity.setDeviceType(generateDeviceType(device.getName()));
                        this.bluetoothDeviceItemEntityList.add(entity);
                    }
                }
            }
        }
    }

    private int generateDeviceType(String deviceName) { // 根據裝置的名字區分類型
        if (StringUtil.isEmpty(deviceName)) {
            return ORIGINAL_TYPE_BEACON;
        } else if (deviceName.contains("USBeacon-Remote") || deviceName.contains("USBeacon-Mesh")) {
            return REMOTE_TYPE_BEACON;
        } else if (deviceName.contains("Wearable")) {
            return WEARABLE_TYPE_BEACON;
        } else if (deviceName.contains("Dagger")) {
            return DAGGER_TYPE_BEACON;
        } else if (deviceName.contains("USBeacon-Prox")) {
            return PROX_TYPE_BEACON;
        } else {
            return ORIGINAL_TYPE_BEACON;
        }
    }

    private boolean checkScanDeviceExist(BluetoothDevice device) { // 檢查列表中是否已有這個裝置
        for (BluetoothDeviceItemEntity bluetoothDeviceItemEntity : bluetoothDeviceItemEntityList) {
            if (StringUtil.isEquals(bluetoothDeviceItemEntity.getMacAddress(), device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    private List<ScanFilter> generateScanFilters() { // 設定掃描過濾
        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter filter = new ScanFilter.Builder()
                .setManufacturerData(0x004c, new byte[]{})
                .build();
        filters.add(filter);
        return filters;
    }

    public interface ScanDeviceDataListener {
        void onScanDeviceDataResponse(List<BluetoothDeviceItemEntity> bluetoothDeviceItemEntityList);

        void onScanDeviceDataErrorResponse(int errorCode);
    }
}
