package com.THLight.BLE.USBeacon.Writer.Simple.helper;

import android.content.Context;
import android.content.Intent;

import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.ACTION_RSSI_VALUE_CHANGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.ACTION_NETWORK_CHANGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.ACTION_REFRESH_ALL_DATA;
import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.INTENT_INT_RSSI_VALUE;

public class SendBroadcastHelper {

    public static void sendNetworkChangeAction(Context context) {
        Intent intent = new Intent(context, BroadcastReceiverHelper.class);
        intent.setAction(ACTION_NETWORK_CHANGE);
        context.sendBroadcast(intent);
    }

    public static void sendAverageValueChangeAction(Context context, int averageValue) {
        Intent intent = new Intent(context, BroadcastReceiverHelper.class);
        intent.setAction(ACTION_RSSI_VALUE_CHANGE);
        intent.putExtra(INTENT_INT_RSSI_VALUE, averageValue);
        context.sendBroadcast(intent);
    }

    public static void sendRefreshAllDataAction(Context context) { //
        Intent intent = new Intent(context, BroadcastReceiverHelper.class);
        intent.setAction(ACTION_REFRESH_ALL_DATA);
        context.sendBroadcast(intent);
    }
}
