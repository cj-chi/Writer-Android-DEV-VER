package com.THLight.BLE.USBeacon.Writer.Simple.entity.scan;

import java.io.Serializable;

public class BluetoothDeviceItemEntity implements Serializable {
    private int deviceType;
    private String targetId;
    private String deviceName;
    private String uuid;
    private String major;
    private String minor;
    private String rssi;
    private String meterRssi;
    private String macAddress;
    private int batteryPower;
    private int versionCharacteristicInt;
    private String firmWare;
    private int broadcastFrequency;
    private int txPowerIndex;
    private int restTime;
    private int restBroadcastFrequency;
    private int connectWindowDelay;
    private boolean isDisableAlarm;
    private int sensorSensitive;
    private int sensorSampling;
    private int sensorStatus;
    private int trackerStatus;
    private String hwId = "";
    private String userId = "";
    private String askKey;
    private int lowBatteryLedStatus;

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getMeterRssi() {
        return meterRssi;
    }

    public void setMeterRssi(String meterRssi) {
        this.meterRssi = meterRssi;
    }

    public int getBatteryPower() {
        return batteryPower;
    }

    public void setBatteryPower(int batteryPower) {
        this.batteryPower = batteryPower;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getFirmWare() {
        return firmWare;
    }

    public void setFirmWare(String firmWare) {
        this.firmWare = firmWare;
    }

    public int getVersionCharacteristicInt() {
        return versionCharacteristicInt;
    }

    public void setVersionCharacteristicInt(int versionCharacteristicInt) {
        this.versionCharacteristicInt = versionCharacteristicInt;
    }

    public int getBroadcastFrequency() {
        return broadcastFrequency;
    }

    public void setBroadcastFrequency(int broadcastFrequency) {
        this.broadcastFrequency = broadcastFrequency;
    }

    public int getTxPowerIndex() {
        return txPowerIndex;
    }

    public void setTxPowerIndex(int txPowerIndex) {
        this.txPowerIndex = txPowerIndex;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getRestBroadcastFrequency() {
        return restBroadcastFrequency;
    }

    public void setRestBroadcastFrequency(int restBroadcastFrequency) {
        this.restBroadcastFrequency = restBroadcastFrequency;
    }

    public int getConnectWindowDelay() {
        return connectWindowDelay;
    }

    public void setConnectWindowDelay(int connectWindowDelay) {
        this.connectWindowDelay = connectWindowDelay;
    }

    public boolean isDisableAlarm() {
        return isDisableAlarm;
    }

    public void setDisableAlarm(boolean disableAlarm) {
        isDisableAlarm = disableAlarm;
    }

    public int getSensorSensitive() {
        return sensorSensitive;
    }

    public void setSensorSensitive(int sensorSensitive) {
        this.sensorSensitive = sensorSensitive;
    }

    public int getSensorSampling() {
        return sensorSampling;
    }

    public void setSensorSampling(int sensorSampling) {
        this.sensorSampling = sensorSampling;
    }

    public int getSensorStatus() {
        return sensorStatus;
    }

    public void setSensorStatus(int sensorStatus) {
        this.sensorStatus = sensorStatus;
    }

    public int getTrackerStatus() {
        return trackerStatus;
    }

    public void setTrackerStatus(int trackerStatus) {
        this.trackerStatus = trackerStatus;
    }

    public String getHwId() {return String.valueOf(hwId);}

    public void setHwId(String hwId) {this.hwId = hwId;}

    public String getUserId() {return String.valueOf(userId);}

    public void setUserId(String userId) {this.userId = userId;}

    public String getAskKey() {return askKey;}

    public void setAskKey(String askKey) {this.askKey = askKey;}

    public int getLowBatteryLedStatus() {
        System.out.println("getLowBatteryLedStatus: " + lowBatteryLedStatus);
        return lowBatteryLedStatus;}

    public void setLowBatteryLedStatus(int ledStatus) { this.lowBatteryLedStatus = ledStatus;}
}