package com.THLight.BLE.USBeacon.Writer.Simple.entity.beacon;

import com.THLight.BLE.USBeacon.Writer.Simple.util.BytesUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class BeaconProcessEntity {

    public static int getBeaconType(byte[] dataBuf) {
        return (((int) dataBuf[1] << 8) & 0x00FF00) | ((int) dataBuf[0] & 0x00FF);
    }
    /**
     * ================================================
     */
    public static int getMajor(byte[] dataBuf) {
        return (((int) dataBuf[0] << 8) & 0x00FF00) | ((int) dataBuf[1] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getMinor(byte[] dataBuf) {
        return (((int) dataBuf[0] << 8) & 0x00FF00) | ((int) dataBuf[1] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getRemoteID(byte[] dataBuf) {
        return (((int) dataBuf[1] << 8) & 0x00FF00) | ((int) dataBuf[0] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static String getFirmwareVersion(byte[] dataBuf) {
        return ((int) dataBuf[0] & 0x00FF) + "." + (int) ((int) dataBuf[1] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getType(byte[] dataBuf) {
        return (int) ((int) dataBuf[2] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getBatteryPower(byte[] dataBuf) {
        return  (((dataBuf[4] << 8) & 0x00FF00) | (dataBuf[3] & 0x00FF));
    }

    public static String getBeaconUuid(byte[] dataBuf) {
        if (dataBuf == null || dataBuf.length < 16) {
            return "";
        }
        byte[] uuidBytes = Arrays.copyOfRange(dataBuf, 0, 16);
        String hex = BytesUtil.getHexString(uuidBytes);
        return hex.substring(0, 8) + "-"
                + hex.substring(8, 12) + "-"
                + hex.substring(12, 16) + "-"
                + hex.substring(16, 20) + "-"
                + hex.substring(20, 32);
    }

    /**
     * ================================================
     */
    public static int getTxPower(byte[] dataBuf) {
        return (int) dataBuf[0] & 0x00FF;
    }

    /**
     * ================================================
     */
    public static int getOnMeterRSSI(byte[] dataBuf) {
        return (int) dataBuf[0];
    }

    /**
     * ================================================
     */
    public static long getCurrentTime(byte[] dataBuf) {
        return byteArrayToUnsignedLong(Arrays.copyOfRange(dataBuf, 0, 4));
    }

    /**
     * ================================================
     */
    public static long getWorkTime(byte[] dataBuf) {
        return byteArrayToUnsignedLong(Arrays.copyOfRange(dataBuf, 4, 8));
    }

    /**
     * ================================================
     */
    public static int getAdjustTime(byte[] dataBuf) {
        return (int) ((int) dataBuf[8] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static long getAdjustSecond(byte[] dataBuf) {
        return byteArrayToUnsignedLong(Arrays.copyOfRange(dataBuf, 9, 13));
    }

    /**
     * ================================================
     */
    public static int getAdvertiseFrequency(byte[] dataBuf) {
        return (int) dataBuf[0] & 0x00FF;
    }

    /**
     * ================================================
     */
    public static int getAdvertiseDelay(byte[] dataBuf) {
        return (int) (((int) dataBuf[2] << 8) & 0x00FF00) | ((int) dataBuf[1] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getTimerBeforeEnterBeacon(byte[] dataBuf) {
        return (int) dataBuf[0] & 0x00FF;
    }

    /**
     * ================================================
     */
    public static int getTimerBeforeEnterBeaconWearable(byte[] dataBuf) {
        return (int) (((int) dataBuf[1] << 8) & 0x00FF00) | ((int) dataBuf[2] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getRestTime(byte[] dataBuf) {
        return (int) (((int) dataBuf[1] << 8) & 0x00FF00) | ((int) dataBuf[0] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getSensorStatus(byte[] dataBuf) {
        return (int) dataBuf[0];
    }

    /**
     * ================================================
     */
    public static int getAdvSteps(byte[] dataBuf) {
        return (int) dataBuf[0];
    }

    /**
     * ================================================
     */
    public static int getAlarm(byte[] dataBuf) {
        return (int) dataBuf[0];
    }

    /**
     * ================================================
     */
    public static int getGsensorSensitive(byte[] dataBuf) {
        return (int) dataBuf[0];
    }

    /**
     * ================================================
     */
    public static int getGsensorSampling(byte[] dataBuf) {
        return (int) (((int) dataBuf[2] << 8) & 0x00FF00) | ((int) dataBuf[1] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getAdvInfoFrequency(byte[] dataBuf) {
        return (int) (((int) dataBuf[1] << 8) & 0x00FF00) | ((int) dataBuf[0] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getAdvInfoSecondToRest(byte[] dataBuf) {
        return (int) (((int) dataBuf[4] << 8) & 0x00FF00) | ((int) dataBuf[3] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getAdvInfoSecondToNoemal(byte[] dataBuf) {
        return (int) (((int) dataBuf[6] << 8) & 0x00FF00) | ((int) dataBuf[5] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static int getAdvInfoRestFrequency(byte[] dataBuf) {
        return (int) (((int) dataBuf[8] << 8) & 0x00FF00) | ((int) dataBuf[7] & 0x00FF);
    }

    /**
     * ================================================
     */
    public static long getSteps(byte[] dataBuf) {
        return byteArrayToUnsignedLong(Arrays.copyOfRange(dataBuf, 0, 4));
    }

    /**
     * ================================================
     */
    public static int getDisableAlarmSignal(byte[] dataBuf) {
        return (int) dataBuf[0];
    }

    /**
     * ================================================
     */
    public static String[] getLineInfo(byte[] dataBuf) {
        String[] LineInfo = new String[2];

        LineInfo[0] = "";
        LineInfo[1] = "";

        for (int i = 0; i < 5; i++) {
            LineInfo[0] += BytesUtil.getHexString(dataBuf[i]);
        }
        for (int i = 5; i < 13; i++) {
            LineInfo[1] += BytesUtil.getHexString(dataBuf[i]);
        }

        return LineInfo;
    }

    /**
     * ================================================
     */
    public static long getHwId(byte[] dataBuf) {
        return byteArrayToID(Arrays.copyOfRange(dataBuf, 0, 4));
    }

    /**
     * ================================================
     */
    public static long getUserID(byte[] dataBuf) {
        return byteArrayToID(Arrays.copyOfRange(dataBuf, 0, 4));
    }

    /**
     * ================================================
     */

    public static String getAskKey(byte[] dataBuf) throws UnsupportedEncodingException {
        return new String(dataBuf, "UTF-8");
    }

    public static int getLedStatus(byte[] dataBuf) { return dataBuf[0]; }

    private static long byteArrayToUnsignedLong(byte[] b) {
        if (b.length > 8) {  // long only 8 byte
            return 0;
        }
        long l = 0;
        for (int i = b.length - 1; i > 0; i--) {
            l <<= 8;
            l |= b[i] & 0x00FF;
        }
        return l;
    }

    private static long byteArrayToID(byte[] b) {
        if (b.length > 8) {
            return 0;
        }
        long l = 0;
        for (byte value : b) {
            l <<= 8;
            l |= value & 0x00FF;
        }
        return l;
    }
}
