package com.THLight.BLE.USBeacon.Writer.Simple.webservice.task;

import com.THLight.BLE.USBeacon.Writer.Simple.manager.NetworkManager;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

public class RegisterTask extends HttpURLConnectionTask {
    private RegisterResponseListener listener;

    public RegisterTask(RegisterResponseListener listener, String accountString, String passWordString) {
        String urlString = webHost + "/func/register?";
        String pathString = urlString + "mail=" + accountString + "&password=" + passWordString;
        this.listener = listener;
        generatePathString(EnCodingType.XML, pathString);
    }

    @Override
    public void onApiAsyncTaskPostExecute(Object object) {
        String response = (String) object;
        if (!NetworkManager.getInstance().isNetWorkNormal()) {
            listener.onRegisterNetworkError();
        } else if (StringUtil.isEmpty(response) || !response.contains("ok")) {
            listener.onRegisterResponseError();
        } else {
            listener.onRegisterResponseSuccess(response);
        }
    }

    public interface RegisterResponseListener {
        void onRegisterResponseSuccess(String response);

        void onRegisterNetworkError();

        void onRegisterResponseError();
    }
}
