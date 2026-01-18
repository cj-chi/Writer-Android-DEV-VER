package com.THLight.BLE.USBeacon.Writer.Simple.webservice.task;

import com.THLight.BLE.USBeacon.Writer.Simple.util.GsonUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

import java.util.Map;

public class UpdateBeaconTask extends HttpURLConnectionTask {
    private UpdateBeaconTaskListener listener;
    private int type;

    public UpdateBeaconTask(UpdateBeaconTaskListener listener, int type, String... parameters) {
        this.listener = listener;
        this.type = type;
        String pathString = webHost + (type == 0 ? "/func/add?" : "/func/update?");
        generatePathString(EnCodingType.XML, pathString +
                "dvc_name=" + parameters[0] +
                "&dvc_version_major=" + parameters[1] +
                "&dvc_version_minor=" + parameters[2] +
                "&mbr_id=" + parameters[3] +
                (type == 0 ? "" : "&dvc_gid=" + parameters[4])
        );
    }

    @Override
    public void onApiAsyncTaskPostExecute(Object object) {
        String response = (String) object;
        if (StringUtil.isEmpty(response) || !response.contains("ok")) {
            return;
        }
        if (type == 0) {
            Map<String, String> map = GsonUtil.generateDataMap(response);
            listener.onUpdateBeaconTaskSuccess(true, map.get("gid"));
        } else {
            listener.onUpdateBeaconTaskSuccess(false, "");
        }

    }

    public interface UpdateBeaconTaskListener {
        void onUpdateBeaconTaskSuccess(boolean isAdd, String response);
    }
}
