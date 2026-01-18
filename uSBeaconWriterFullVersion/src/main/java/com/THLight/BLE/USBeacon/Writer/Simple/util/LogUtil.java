package com.THLight.BLE.USBeacon.Writer.Simple.util;

public class LogUtil {
    private static final int LOG_MAX_LENGTH = 2000;

    public static void log(String TAG, String msg) {
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAX_LENGTH;
        for (int i = 0; i < 100; i++) {
            //大於截斷並繼續
            if (strLength > end) {
                System.out.println(TAG + " : " + msg.substring(start, end));
                start = end;
                end = end + LOG_MAX_LENGTH;
            } else {
                System.out.println(TAG + " : " + msg.substring(start, strLength));
                break;
            }
        }
    }
}
