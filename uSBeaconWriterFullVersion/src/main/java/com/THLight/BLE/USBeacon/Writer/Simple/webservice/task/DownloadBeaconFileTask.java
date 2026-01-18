package com.THLight.BLE.USBeacon.Writer.Simple.webservice.task;

import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

public class DownloadBeaconFileTask extends HttpURLConnectionTask {
    private DownloadBeaconFileListener listener;

    public DownloadBeaconFileTask(DownloadBeaconFileListener listener, String pathString) {
        this.listener = listener;
        generatePathString(EnCodingType.FILE, pathString, "USBeaconList.zip");
    }

    @Override
    public void onApiAsyncTaskPostExecute(Object object) {
        listener.onDownloadBeaconFileComplete();
    }

    public interface DownloadBeaconFileListener {
        void onDownloadBeaconFileComplete();
    }
}
