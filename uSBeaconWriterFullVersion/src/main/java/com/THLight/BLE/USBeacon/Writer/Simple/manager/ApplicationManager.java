package com.THLight.BLE.USBeacon.Writer.Simple.manager;

import android.content.Context;

public class ApplicationManager {
    private static ApplicationManager instance;
    private Context context;

    public static ApplicationManager getInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
