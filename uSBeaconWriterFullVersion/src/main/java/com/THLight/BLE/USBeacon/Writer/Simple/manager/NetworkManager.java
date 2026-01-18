package com.THLight.BLE.USBeacon.Writer.Simple.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;

import com.THLight.BLE.USBeacon.Writer.Simple.helper.SendBroadcastHelper;

/**
 * Created by allen on 2020/3/26.
 */

public class NetworkManager extends ConnectivityManager.NetworkCallback {
    public static NetworkManager instance;
    private ConnectivityManager connectivityManager;
    private Context context;

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void registerNetworkChange(final Context context) { // 註冊網路狀態變化
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            this.connectivityManager.registerNetworkCallback(generateNetworkRequest(), this);
        }
    }

    private NetworkRequest generateNetworkRequest() {
        return new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    private int getNetworkType() { // 取得目前網路狀態
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < 23) {
            if (isNetWorkNormal()) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return ConnectivityManager.TYPE_WIFI;
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return ConnectivityManager.TYPE_MOBILE;
                }
            }
        } else {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return NetworkCapabilities.TRANSPORT_WIFI;
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return NetworkCapabilities.TRANSPORT_CELLULAR;
                }
            }
        }
        return -1;
    }

    public boolean isNetWorkNormal() {  // 目前網路是否可用
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    public boolean isMobileNetworkNormal() { // 目前網路是否為行動網路
        return getNetworkType() == NetworkCapabilities.TRANSPORT_CELLULAR;
    }

    public boolean isWifiNetworkNormal() { // 目前網路是否為wifi
        return getNetworkType() == NetworkCapabilities.TRANSPORT_WIFI;
    }

    @Override
    public void onAvailable(Network network) {//Called when the framework connects and has declared a new network ready for use.
        super.onAvailable(network);
        System.out.println("NetWorkManager onAvailable :");
        new Handler().postDelayed(() -> SendBroadcastHelper.sendNetworkChangeAction(context), 500);
    }

    @Override
    public void onLost(Network network) { //Called when a network disconnects or otherwise no longer satisfies this request or callback.
        super.onLost(network);
        System.out.println("NetWorkManager onLost : ");
        new Handler().postDelayed(() -> SendBroadcastHelper.sendNetworkChangeAction(context), 500);
    }
}
