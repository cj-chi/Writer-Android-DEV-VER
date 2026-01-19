package com.THLight.BLE.USBeacon.Writer.Simple.helper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.NetworkManager;

import java.lang.ref.WeakReference;

public class RequestPermissionHelper {
    private static final int REQUEST_CODE_PERMISSION = 0;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_ENABLE_MOBILE_NETWORK = 2;
    private static final int REQUEST_ENABLE_LOCATION = 3;
    private static final int REQUEST_CODE_BLUETOOTH_PERMISSION = 4;

    public static void requestBluetoothEnable(Activity activity) { // 請求藍芽打開
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        weakReference.get().startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
    }

    public static boolean isBluetoothAdapterEnable() { // 是否藍牙有打開
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    public static boolean isLocationSettingEnable(Context context) { // 使否定位有打開
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void requestLocationSettingEnable(Activity activity) { // 請求使用者打開定位
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        if (NetworkManager.getInstance().isNetWorkNormal()) {
            SettingsClient mSettingsClient = LocationServices.getSettingsClient(weakReference.get());
            mSettingsClient.checkLocationSettings(generateLocationSettingRequest()).addOnFailureListener(e -> {
                try {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    resolvableApiException.startResolutionForResult(weakReference.get(), REQUEST_ENABLE_LOCATION);
                } catch (IntentSender.SendIntentException ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            weakReference.get().startActivityForResult(intent, REQUEST_ENABLE_LOCATION);
        }
    }

    private static LocationSettingsRequest generateLocationSettingRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        return builder.build();
    }

    public static boolean checkSelfPermission(Context context, String... permissions) { // 檢查是否具有這些權限
        if (permissions == null || permissions.length == 0) {
            return true;
        }
        for (String permission : permissions) {
            boolean granted = ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                return false;
            }
        }
        return true;
    }

    public static void requestLocationPermission(Activity activity) { // 請求定位權限
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        ActivityCompat.requestPermissions(weakReference.get(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
    }

    public static boolean hasLocationPermission(Context context) {
        return checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean hasRequiredScanPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT);
        }
        return checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static void requestRequiredScanPermissions(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                    weakReference.get(),
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_CODE_BLUETOOTH_PERMISSION);
        } else {
            requestLocationPermission(activity);
        }
    }

    public static void requestAllScanPermissions(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                    weakReference.get(),
                    new String[]{
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_CODE_BLUETOOTH_PERMISSION);
        } else {
            requestLocationPermission(activity);
        }
    }
}
