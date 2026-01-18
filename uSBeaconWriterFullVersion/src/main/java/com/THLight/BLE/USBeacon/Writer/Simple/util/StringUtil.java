package com.THLight.BLE.USBeacon.Writer.Simple.util;

import android.net.Uri;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by allen on 2020/2/24.
 */
public class StringUtil {

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isAllEmpty(String... strings) {
        for (String string : strings) {
            if (!isEmpty(string)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllNotEmpty(String... strings) {
        for (String string : strings) {
            if (isEmpty(string)) {
                return false;
            }
        }
        return strings.length != 0;
    }

    public static boolean isEquals(String firstString, String secondString) {
        return !isEmpty(firstString) && !isEmpty(secondString) && firstString.equalsIgnoreCase(secondString);
    }

    public static boolean isEmail(String email) {
        Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String generateNumberUnitString(long number) {
        if (number >= 1000000) {
            return String.format(Locale.US, "%.1f", (number / 1000000f)) + "m";
        } else if (number >= 1000) {
            return String.format(Locale.US, "%.1f", (number / 1000f)) + "k";
        } else {
            return String.valueOf(number);
        }
    }

    public static int getIndexFromStringArray(String data, String[] stringArray) {
        if (StringUtil.isEmpty(data)) {
            return 0;
        }
        for (int i = 0; i < stringArray.length; i++) {
            if (data.equalsIgnoreCase(stringArray[i])) {
                return i;
            }
        }
        return 0;
    }

    public static String generateUtf8UrlEncode(String string) {
        return Uri.encode(string, "utf-8");
    }

    public static boolean isPositiveInteger(String number) {
        return number.matches("([1-9][0-9]*)|([0]$)");
    }
}
