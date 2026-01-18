package com.THLight.BLE.USBeacon.Writer.Simple.webservice.task;

import com.THLight.BLE.USBeacon.Writer.Simple.manager.NetworkManager;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

public class LoginTask extends HttpURLConnectionTask {
    private LoginResponseListener listener;

    public LoginTask(LoginResponseListener listener, String accountString, String passWordString) {
        String urlString = webHost + "/api/func?func=managerLogin&login_type=normal&";
        String pathString = urlString + "account=" + accountString + "&password=" + passWordString;
        this.listener = listener;
        generatePathString(EnCodingType.XML, pathString);
    }

    @Override
    public void onApiAsyncTaskPostExecute(Object object) {
        String response = (String) object;
        if (!NetworkManager.getInstance().isNetWorkNormal()) {
            listener.onLoginNetworkError();
        } else if (StringUtil.isEmpty(response) || !response.contains("success")) {
            listener.onLoginResponseError();
        } else {
            listener.onLoginResponseSuccess(response);
        }
    }


    public interface LoginResponseListener {
        void onLoginResponseSuccess(String response);

        void onLoginNetworkError();

        void onLoginResponseError();
    }
}
