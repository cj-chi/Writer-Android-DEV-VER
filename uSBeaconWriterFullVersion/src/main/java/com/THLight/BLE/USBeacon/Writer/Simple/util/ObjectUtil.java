package com.THLight.BLE.USBeacon.Writer.Simple.util;

public class ObjectUtil {

    public static <T> T get(Class<T> tClass, Object object) {
        if (tClass.isInstance(object)) {
            return tClass.cast(object);
        }
        return null;
    }
}
