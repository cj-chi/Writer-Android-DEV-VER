package com.THLight.BLE.USBeacon.Writer.Simple.application;

import android.app.Application;

import com.THLight.BLE.USBeacon.Writer.Simple.manager.ApplicationManager;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.NetworkManager;

public class ThLightApplication extends Application {

    public ThLightApplication() {
        ApplicationManager.getInstance().setContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkManager.getInstance().registerNetworkChange(this);
    }
}
