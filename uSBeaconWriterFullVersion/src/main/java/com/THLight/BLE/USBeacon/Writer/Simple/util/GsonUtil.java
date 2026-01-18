package com.THLight.BLE.USBeacon.Writer.Simple.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class GsonUtil {
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtil() {
    }

    public static String toJson(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    public static <T> T generateGenericData(String gsonString, Class<T> className) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, className);
        }
        return t;
    }

    public static <T> List<T> generateDataList(String gsonString, Class<T> className) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, TypeToken.getParameterized(List.class, className).getType());
        }
        return list;
    }

    public static <T> Map<String, T> generateDataMap(String gsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }
}
