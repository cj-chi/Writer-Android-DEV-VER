package com.THLight.BLE.USBeacon.Writer.Simple.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.ACTION_RSSI_VALUE_CHANGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.ACTION_NETWORK_CHANGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.ACTION_REFRESH_ALL_DATA;
import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.INTENT_INT_RSSI_VALUE;

public class BroadcastReceiverHelper extends BroadcastReceiver {
    private static List<BroadcastReceiverHelperListener> listenerList = new CopyOnWriteArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_NETWORK_CHANGE:
                System.out.println("SendBroadcastHelper ACTION_NETWORK_CHANGE");
                onActionNetWorkChange();
                break;
            case ACTION_RSSI_VALUE_CHANGE:
                System.out.println("SendBroadcastHelper ACTION_AVERAGE_VALUE_CHANGE");
                onActionRSSIValueChange(intent.getIntExtra(INTENT_INT_RSSI_VALUE, 0));
                break;
            case ACTION_REFRESH_ALL_DATA:
                onActionRefreshData();
                break;
        }
    }

    public static void registerListener(BroadcastReceiverHelperListener listener) { // 註冊監聽
        if (listenerList.isEmpty() || !listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public static void unregisterListener(BroadcastReceiverHelperListener listener) { // 解除註冊監聽
        if (!listenerList.isEmpty()) {
            listenerList.remove(listener);
        }
    }

    private void onActionNetWorkChange() {
        for (BroadcastReceiverHelperListener listener : listenerList) {
            if (listener instanceof ActionNetWorkChangeListener) {
                ((ActionNetWorkChangeListener) listener).onActionNetWorkChange();
            }
        }
    }

    private void onActionRSSIValueChange(int RSSI) {
        for (BroadcastReceiverHelperListener listener : listenerList) {
            if (listener instanceof ActionRSSIValueChangeListener) {
                ((ActionRSSIValueChangeListener) listener).onActionRSSIValueChange(RSSI);
            }
        }
    }

    private void onActionRefreshData() {
        for (BroadcastReceiverHelperListener listener : listenerList) {
            if (listener instanceof ActionRefreshDataListener) {
                ((ActionRefreshDataListener) listener).onActionRefreshData();
            }
        }
    }

    private interface BroadcastReceiverHelperListener {

    }

    public interface ActionNetWorkChangeListener extends BroadcastReceiverHelperListener { // 網路狀態監聽
        void onActionNetWorkChange();
    }

    public interface ActionRSSIValueChangeListener extends BroadcastReceiverHelperListener { // RSSI 監聽
        void onActionRSSIValueChange(int RSSI);
    }

    public interface ActionRefreshDataListener extends BroadcastReceiverHelperListener { // 需刷新全局的監聽
        void onActionRefreshData();
    }
}
