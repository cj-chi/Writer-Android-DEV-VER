package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseFragment;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper.ActionRefreshDataListener;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.B3339B_BEACON;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.DAGGER_TYPE_BEACON;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.DISTANCE_BEACON_PAGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.INFORMATION_BEACON_PAGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.PROX_TYPE_BEACON;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.REMOTE_TYPE_BEACON;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.WEARABLE_BEACON_PAGE;

public class PowerBeaconFragment extends BaseFragment implements OnClickListener, OnItemSelectedListener, TextWatcher,
        ActionRefreshDataListener {
    private BluetoothDeviceItemEntity bluetoothDeviceItemEntity;
    private int spinnerItemPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            setContentView(inflater.inflate(R.layout.fragment_power_beacon, container, false));
            initValue();
            bindInformationPageView();
            bindDistancePageView();
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
    public void onActionRefreshData() {
        initValue();
        bindTxPowerSpinner();
        bindBroadCastFrequencyEditText();
        bindPowerCapacityEditText();
        bindConnectWindowDelayEditText();
    }

    private void initValue() {
        EditDeviceActivity activity = (EditDeviceActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            this.bluetoothDeviceItemEntity = activity.entity;
        }
    }

    private void bindInformationPageView() {
        getContentView().findViewById(R.id.fragmentPowerBeacon_informationPageButton).setOnClickListener(this);
    }

    private void bindDistancePageView() {
        getContentView().findViewById(R.id.fragmentPowerBeacon_distancePageButton).setOnClickListener(this);
    }

    private void bindWearablePageView() {
        getContentView().findViewById(R.id.fragmentPowerBeacon_wearablePageButton).setOnClickListener(this);
    }

    private void bindTxPowerSpinner() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        Spinner spinner = getContentView().findViewById(R.id.fragmentPowerBeacon_txPowerSpinner);
        ArrayAdapter<?> arrayAdapter;
        int deviceId = bluetoothDeviceItemEntity.getDeviceType();
        if (deviceId == REMOTE_TYPE_BEACON || deviceId == DAGGER_TYPE_BEACON || deviceId == PROX_TYPE_BEACON || getBluetoothDeviceFirmWare() >= 2.0) {
            arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.txpower2640, R.layout.spinner_item_text);
        }else if (deviceId == B3339B_BEACON) {
            arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.txpower2020, R.layout.spinner_item_text);
        }
        else {
            arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.txpower, R.layout.spinner_item_text);
        }
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item_big_text);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        if (!LoginManager.getInstance().isFastSetting() ||
                LoginManager.getInstance().getTxPowerIndex() == -1 ||
                spinner.getCount() < LoginManager.getInstance().getTxPowerIndex()) {
            spinner.setSelection(bluetoothDeviceItemEntity.getTxPowerIndex());
        } else {
            spinner.setSelection(LoginManager.getInstance().getTxPowerIndex());
        }
    }

    private void bindBroadCastFrequencyEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentPowerBeacon_broadcastFrequencyEditText);
        if (!LoginManager.getInstance().isFastSetting() || StringUtil.isEmpty(LoginManager.getInstance().getBroadcastFrequency())) {
            editText.setText(String.valueOf(bluetoothDeviceItemEntity.getBroadcastFrequency()));
        } else {
            editText.setText(LoginManager.getInstance().getBroadcastFrequency());
        }
        editText.addTextChangedListener(this);
    }

    private void bindPowerCapacityEditText() {
        EditText editText = getContentView().findViewById(R.id.fragmentPowerBeacon_powerCapacityEditText);
        editText.addTextChangedListener(this);
    }

    private void bindConnectWindowDelayEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentPowerBeacon_connectWindowDelayEditText);
        editText.setText(String.valueOf(bluetoothDeviceItemEntity.getConnectWindowDelay()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragmentPowerBeacon_informationPageButton:
                setCurrentPage(INFORMATION_BEACON_PAGE);
                break;
            case R.id.fragmentPowerBeacon_distancePageButton:
                setCurrentPage(DISTANCE_BEACON_PAGE);
                break;
            case R.id.fragmentPowerBeacon_wearablePageButton:
                setCurrentPage(WEARABLE_BEACON_PAGE);
                break;
        }
    }

    private void setCurrentPage(int currentPage) {
        EditDeviceActivity activity = (EditDeviceActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            activity.setCurrentPage(currentPage);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.spinnerItemPosition = position;
        calculateLifeTime();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (StringUtil.isEmpty(s.toString())) {
            return;
        }
        calculateLifeTime();
    }

    private void calculateLifeTime() { // 剩餘電量能撐的天數
        EditText broadcastFrequencyEditText = getContentView().findViewById(R.id.fragmentPowerBeacon_broadcastFrequencyEditText);
        EditText remainPowerEditText = getContentView().findViewById(R.id.fragmentPowerBeacon_powerCapacityEditText);
        TextView remainDayTextView = getContentView().findViewById(R.id.fragmentPowerBeacon_remainDayTextView);
        int broadcastFrequencyInt = Integer.parseInt(broadcastFrequencyEditText.getText().toString());
        int remainPowerInt = Integer.parseInt(remainPowerEditText.getText().toString());
        long dayLong = 0;
        if (broadcastFrequencyInt != 0) {
            if (spinnerItemPosition == 3) {
                dayLong = (long) (remainPowerInt * 10.3) / broadcastFrequencyInt / 24;
            } else if (spinnerItemPosition == 2) {
                dayLong = (long) (remainPowerInt * 11.21) / broadcastFrequencyInt / 24;
            } else if (spinnerItemPosition == 1) {
                dayLong = (long) (remainPowerInt * 12.6) / broadcastFrequencyInt / 24;
            } else if (spinnerItemPosition == 0) {
                dayLong = (long) (remainPowerInt * 13.61) / broadcastFrequencyInt / 24;
            }
        }
        remainDayTextView.setText(dayLong + " Days Left");
    }

    private double getBluetoothDeviceFirmWare() {
        return StringUtil.isEmpty(bluetoothDeviceItemEntity.getFirmWare()) ? 0 : Double.parseDouble(bluetoothDeviceItemEntity.getFirmWare());
    }

    protected int getSpinnerItemPosition() {
        return spinnerItemPosition;
    }

    protected int getBroadcastFrequencyValue() { // 回傳前,檢查廣播頻率是否超出指定邊界
        EditText editText = getContentView().findViewById(R.id.fragmentPowerBeacon_broadcastFrequencyEditText);
        String textString = editText.getText().toString();
        int broadcastFrequency = StringUtil.isEmpty(textString) ? 0 : Integer.parseInt(textString);
        if (broadcastFrequency < 1) {
            broadcastFrequency = 1;
        } else if (broadcastFrequency > 30 && getBluetoothDeviceFirmWare() >= 1.5) {
            broadcastFrequency = 30;
        } else if (broadcastFrequency > 25 && getBluetoothDeviceFirmWare() < 1.5) {
            broadcastFrequency = 25;
        }
        return broadcastFrequency;
    }
}
