package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseFragment;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper.ActionRSSIValueChangeListener;

import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.INFORMATION_BEACON_PAGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.POWER_BEACON_PAGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.WEARABLE_BEACON_PAGE;

public class MeterRSSIFragment extends BaseFragment implements OnClickListener, ActionRSSIValueChangeListener {
    private BluetoothDeviceItemEntity bluetoothDeviceItemEntity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            setContentView(inflater.inflate(R.layout.fragment_meter_rssi, container, false));
            initValue();
            bindInformationPageView();
            bindPowerPageView();
            bindWearablePageView();
            BroadcastReceiverHelper.registerListener(this);
        }
        return getContentView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BroadcastReceiverHelper.unregisterListener(this);
    }

    @Override
    public void onActionRSSIValueChange(int RSSI) {
        if (bluetoothDeviceItemEntity != null) {
            bluetoothDeviceItemEntity.setMeterRssi(String.valueOf(RSSI));
            bindCurrentRssiTextView();
        }
    }

    private void initValue() {
        EditDeviceActivity activity = (EditDeviceActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            this.bluetoothDeviceItemEntity = activity.entity;
        }
    }

    private void bindContentView() {
        bindCalibrateRSSIButton();
        bindCurrentRssiTextView();
    }

    private void bindInformationPageView() {
        getContentView().findViewById(R.id.fragmentMeterRSSI_informationPageButton).setOnClickListener(this);
    }

    private void bindPowerPageView() {
        getContentView().findViewById(R.id.fragmentMeterRSSI_powerPageButton).setOnClickListener(this);
    }

    private void bindWearablePageView() {
        getContentView().findViewById(R.id.fragmentMeterRSSI_wearablePageButton).setOnClickListener(this);
    }

    private void bindCalibrateRSSIButton() {
        getContentView().findViewById(R.id.fragmentMeterRSSI_calibrateRssiButton).setOnClickListener(this);
    }

    private void bindCurrentRssiTextView() {
        TextView textView = getContentView().findViewById(R.id.fragmentMeterRSSI_currentRssiTextView);
        textView.setText(bluetoothDeviceItemEntity != null ? bluetoothDeviceItemEntity.getMeterRssi() : "0");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragmentMeterRSSI_informationPageButton:
                setCurrentPage(INFORMATION_BEACON_PAGE);
                break;
            case R.id.fragmentMeterRSSI_powerPageButton:
                setCurrentPage(POWER_BEACON_PAGE);
                break;
            case R.id.fragmentMeterRSSI_wearablePageButton:
                setCurrentPage(WEARABLE_BEACON_PAGE);
                break;
            case R.id.fragmentMeterRSSI_calibrateRssiButton:
                startCalibrateRssiDialogActivity();
                break;
        }
    }

    private void setCurrentPage(int currentPage) {
        EditDeviceActivity activity = (EditDeviceActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            activity.setCurrentPage(currentPage);
        }
    }

    private void startCalibrateRssiDialogActivity() {
        if (getActivity() == null || isClickBlock()) {
            return;
        }
        Intent intent = new Intent(getActivity(), ReadRemoteRSSIDialogActivity.class);
        startActivity(intent);
    }
}
