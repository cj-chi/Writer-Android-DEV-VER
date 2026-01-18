package com.THLight.BLE.USBeacon.Writer.Simple.entity.beacon;

import java.util.Arrays;

public class IBeaconDataEntity {
    private String macAddress = "";
    private String beaconUuid = "00000000-0000-0000-0000-000000000000";
    private int major = 0;
    private int minor = 0;
    private byte oneMeterRssi = 0;
    private byte rssi = 0;

//		(byte)0x02,	/** Number of bytes that follow in first AD structure */
//		(byte)0x01,	/** Flags AD type */
//		/*	0x1A # Flags value 0x1A = 000011010
//		 *		bit 0 (OFF) LE Limited Discoverable Mode
//		 *		bit 1 (ON) LE General Discoverable Mode
//		 *		bit 2 (OFF) BR/EDR Not Supported
//		 *		bit 3 (ON) Simultaneous LE and BR/EDR to Same Device Capable (controller)
//		 *		bit 4 (ON) Simultaneous LE and BR/EDR to Same Device Capable (Host)
//		(byte)0x06,             /** typical flag . */
//		(byte)0x1A,				/** data length. */
//		(byte)0xFF,				/** data type */
//		(byte)0x4C, (byte)0x00,	/** Apple Company Identifier */
//		(byte)0x02, (byte)0x15,	/** beacon type */

    private final static byte[] iBeaconPostfix = new byte[]{
            (byte) 0x02,                /** Number of bytes that follow in first AD structure */
            (byte) 0x01,                /** Flags AD type */
            (byte) 0x06,                /** typical flag . */
            (byte) 0x1A,                /** data length. */
            (byte) 0xFF,                /** data type */
            (byte) 0x4C, (byte) 0x00,   /** Apple Company Identifier */
            (byte) 0x02, (byte) 0x15,   /** type */
    };

    private static boolean isIBeaconData(byte[] scanRecord) {
        return Arrays.equals(Arrays.copyOf(scanRecord, iBeaconPostfix.length), iBeaconPostfix);
    }

    public static IBeaconDataEntity generateIBeacon(byte[] scanRecord) {
        if (!isIBeaconData(scanRecord)) {
            return null;
        }
        int uuidIndex = 9;
        int majorIndex = 25;
        int minorIndex = 27;
        int rssiIndex = 29;
        IBeaconDataEntity iBeaconDataEntity = new IBeaconDataEntity();
        byte[] uuidArray = Arrays.copyOfRange(scanRecord, uuidIndex, uuidIndex + 16);
        // 去除 16 進位 前面的 0x??
        iBeaconDataEntity.beaconUuid = String.format("%02X%02X%02X%02X-%02X%02X-%02X%02X-%02X%02X-%02X%02X%02X%02X%02X%02X",
                uuidArray[0], uuidArray[1], uuidArray[2], uuidArray[3],
                uuidArray[4], uuidArray[5],
                uuidArray[6], uuidArray[7],
                uuidArray[8], uuidArray[9],
                uuidArray[10], uuidArray[11], uuidArray[12], uuidArray[13], uuidArray[14], uuidArray[15]);

        // 需要左移8位元讓兩者相加, 最高位元為1 表示負數 , 為了不出現負數 , 在最高位元前面補上 1 byte 的 0 , 因此能夠以正數表示
//        iBeaconDataEntity.major = (((int) scanRecord[majorIndex] << 8) & 0x00FF00) + ((int) scanRecord[majorIndex + 1] & 0x00FF);
        iBeaconDataEntity.major = BeaconProcessEntity.getMajor(Arrays.copyOfRange(scanRecord, majorIndex, majorIndex + 2));
        iBeaconDataEntity.minor = BeaconProcessEntity.getMinor(Arrays.copyOfRange(scanRecord, minorIndex, minorIndex + 2));
//        iBeaconDataEntity.minor = (((int) scanRecord[minorIndex] << 8) & 0x00FF00) + ((int) scanRecord[minorIndex + 1] & 0x00FF);
        iBeaconDataEntity.oneMeterRssi = scanRecord[rssiIndex];
        return iBeaconDataEntity;
    }

    /**
     * create a new IBeaconData and clone data from iBeacon.
     *
     * @param iBeacon IBeaconData
     * @return return new IBeaconData.
     */

    public static IBeaconDataEntity copyOf(IBeaconDataEntity iBeacon) {
        IBeaconDataEntity newBeacon = new IBeaconDataEntity();
        newBeacon.macAddress = iBeacon.macAddress;
        newBeacon.beaconUuid = iBeacon.beaconUuid;
        newBeacon.major = iBeacon.major;
        newBeacon.minor = iBeacon.minor;
        newBeacon.oneMeterRssi = iBeacon.oneMeterRssi;
        newBeacon.rssi = iBeacon.rssi;
        return newBeacon;
    }

    public String getBeaconUuid() {
        return beaconUuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public byte getOneMeterRssi() {
        return oneMeterRssi;
    }

    public byte getRssi() {
        return rssi;
    }
}