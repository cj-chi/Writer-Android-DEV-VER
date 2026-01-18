package com.THLight.BLE.USBeacon.Writer.Simple.webservice;

import android.os.AsyncTask;

public class ApiAsyncTask extends AsyncTask<String, Void, Object> {
    private ApiAsyncTaskConnectionListener listener;

    ApiAsyncTask(ApiAsyncTaskConnectionListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (listener != null) {
            return listener.onApiAsyncTaskDoInBackground(strings[0]);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        if (listener != null) {
            System.out.println("任務 回傳  :" + (String) object);
            listener.onApiAsyncTaskPostExecute(object);
        }
    }

    public interface ApiAsyncTaskConnectionListener {
        String onApiAsyncTaskDoInBackground(String apiUrl);

        void onApiAsyncTaskPostExecute(Object object);
    }
}
