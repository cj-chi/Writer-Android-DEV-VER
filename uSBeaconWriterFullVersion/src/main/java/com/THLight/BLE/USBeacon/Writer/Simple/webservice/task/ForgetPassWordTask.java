package com.THLight.BLE.USBeacon.Writer.Simple.webservice.task;


import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

public class ForgetPassWordTask extends HttpURLConnectionTask {
    private ForgetPassWordTaskResponseListener listener;

    public ForgetPassWordTask(ForgetPassWordTaskResponseListener listener, String emailString) {
        String urlString = webHost + "/func/pw_get?";
        String pathString = urlString + "mail=" + emailString + "&ctrl=1";
        this.listener = listener;
        generatePathString(EnCodingType.XML, pathString);
    }

    @Override
    public void onApiAsyncTaskPostExecute(Object object) {
        String response = (String) object;
        if (StringUtil.isEmpty(response) || !response.contains("ok")) {
            listener.onForgetPassWordResponseError();
        } else {
            listener.onForgetPassWordResponseSuccess();
        }
    }

    public interface ForgetPassWordTaskResponseListener {
        void onForgetPassWordResponseSuccess();

        void onForgetPassWordResponseError();
    }
}
