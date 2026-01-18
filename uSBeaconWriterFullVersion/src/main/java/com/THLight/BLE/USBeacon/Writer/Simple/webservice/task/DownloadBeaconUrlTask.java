package com.THLight.BLE.USBeacon.Writer.Simple.webservice.task;

import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

import org.json.JSONException;
import org.json.JSONObject;

public class DownloadBeaconUrlTask extends HttpURLConnectionTask {
    private DownloadBeaconUrlTaskResponseListener listener;

    public DownloadBeaconUrlTask(DownloadBeaconUrlTaskResponseListener listener, String queryUUID) {
        this.listener = listener;
        String pathString = "http://usbeacon.com.tw/api/func?func=getDataquery" + "&dataquery_uuid=" + queryUUID + "&time_stamp=" + 0;
        System.out.println("DownloadBeaconUrlTask -- > " + pathString);
        generatePathString(EnCodingType.XML, pathString);
    }

    @Override
    public void onApiAsyncTaskPostExecute(Object object) {
        String response = (String) object;
        if (StringUtil.isEmpty(response) || !response.contains("success")) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            String pathString = jsonObject.getString("zip_path");
            this.listener.onDownloadBeaconUrlTaskResponseSuccess(pathString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface DownloadBeaconUrlTaskResponseListener {
        void onDownloadBeaconUrlTaskResponseSuccess(String pathString);
    }
}
