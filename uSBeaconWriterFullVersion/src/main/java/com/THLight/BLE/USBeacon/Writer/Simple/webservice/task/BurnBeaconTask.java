package com.THLight.BLE.USBeacon.Writer.Simple.webservice.task;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

public class BurnBeaconTask extends HttpURLConnectionTask {

    public BurnBeaconTask(String... parameters) {
        generatePathString(EnCodingType.XML, webHost + "/api/func?func=setBurn" +
                "&id=" + parameters[0] +
                "&mac=" + parameters[1] +
                "&status=" + parameters[2]);
    }

    @Override
    public void onApiAsyncTaskPostExecute(Object object) {
        System.out.println("BurnBeaconTask api : " + (String) object);
    }
}
