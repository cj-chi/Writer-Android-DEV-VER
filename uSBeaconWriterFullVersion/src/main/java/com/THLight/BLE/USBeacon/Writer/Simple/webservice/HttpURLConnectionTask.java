package com.THLight.BLE.USBeacon.Writer.Simple.webservice;

import com.THLight.BLE.USBeacon.Writer.Simple.manager.ApplicationManager;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.ApiAsyncTask.ApiAsyncTaskConnectionListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public abstract class HttpURLConnectionTask implements ApiAsyncTaskConnectionListener {
    protected static final String webHost = "http://ec2-54-248-224-99.ap-northeast-1.compute.amazonaws.com/";
    private static final int readTimeOut = 10 * 1000;
    private static final int connectTimeOut = 15 * 1000;
    private String pathString;
    private String fileName;
    private EnCodingType enCodingType;

    public enum EnCodingType {
        XML, JSON, FILE
    }

    protected void generatePathString(EnCodingType enCodingType, String pathString, String... extraString) {
        this.enCodingType = enCodingType;
        this.pathString = pathString;
        this.fileName = extraString.length > 0 ? extraString[0] : "";
        System.out.println("任務 Url : " + pathString);
    }

    private HttpURLConnection createConnection() throws Exception {
        URL url = new URL(pathString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setReadTimeout(readTimeOut);
        connection.setConnectTimeout(connectTimeOut);
        return connection;
    }

    private InputStream getInputStream() throws Exception {
        HttpURLConnection connection = createConnection();
        return connection.getInputStream();
    }

    public void start() {
        new ApiAsyncTask(this).execute(pathString);
    }

    @Override
    public String onApiAsyncTaskDoInBackground(String apiUrl) {
        try {
            InputStream inputStream = getInputStream();
            switch (enCodingType) {
                case XML:
                    return processXml(inputStream);
                case JSON:
                    return processJson(inputStream);
                case FILE:
                    processFile(inputStream);
                    break;
            }
        } catch (Exception e) {
            System.out.println("onApiAsyncTaskDoInBackground Exception : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public abstract void onApiAsyncTaskPostExecute(Object object);

    private String processXml(InputStream inputStream) throws Exception { // parse xml to json
        XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
        JSONObject jsonObject = xmlToJson.toJson();
        return jsonObject != null ? jsonObject.get("THLight").toString() : "";
    }

    private String processJson(InputStream inputStream) throws Exception { // return json
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2000];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toString();
    }

    private void processFile(InputStream inputStream) throws Exception { // 下載檔案到 Download 目錄下
        File file = new File(ApplicationManager.getInstance().getContext().getExternalFilesDir("Download"), fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
//        final int bufferLength = 1024;
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, length);
        }
        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
