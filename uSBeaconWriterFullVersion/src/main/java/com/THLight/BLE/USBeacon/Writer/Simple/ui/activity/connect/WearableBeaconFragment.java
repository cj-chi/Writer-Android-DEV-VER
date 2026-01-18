package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseFragment;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.command.UsBeaconCommand;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper.ActionRefreshDataListener;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager.CharacteristicChangeListener;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.DISTANCE_BEACON_PAGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.INFORMATION_BEACON_PAGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.POWER_BEACON_PAGE;

public class WearableBeaconFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener,
        CharacteristicChangeListener, ActionRefreshDataListener {
    private BluetoothDeviceItemEntity bluetoothDeviceItemEntity;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            setContentView(inflater.inflate(R.layout.fragment_wearable_beacon, container, false));
            registerListener();
            bindInformationPageView();
            bindPowerPageView();
            bindDistancePageView();
        }
        return getContentView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("BeaconFragment onDestroyView");
        BroadcastReceiverHelper.unregisterListener(this);
    }

    @Override
    public void onCharacteristicResponse(byte[] data) {
        TextView sensorXView = getContentView().findViewById(R.id.fragmentWearableBeacon_sensorXTextView);
        TextView sensorYView = getContentView().findViewById(R.id.fragmentWearableBeacon_sensorYTextView);
        TextView sensorZView = getContentView().findViewById(R.id.fragmentWearableBeacon_sensorZTextView);
        sensorXView.setText(String.valueOf(data[1]));
        sensorYView.setText(String.valueOf(data[3]));
        sensorZView.setText(String.valueOf(data[5]));
    }

    @Override
    public void onActionRefreshData() {
        initValue();
        bindContentView();
    }

    private void registerListener() {
        BluetoothConnectDeviceManager.getInstance().setCharacteristicChangeListener(this);
        BroadcastReceiverHelper.registerListener(this);
    }

    private void initValue() {
        EditDeviceActivity activity = (EditDeviceActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            this.bluetoothDeviceItemEntity = activity.entity;
        }
    }

    private void bindContentView() {
        bindSensorSwitch();
        bindTrackerSwitch();
        bindResetTrackerButton();
        bindBuzzerTestButton();
        bindDisableAlarmSwitch();
        bindSensorSamplingEditText();
        bindSensorSensitiveEditText();
        bindDelayRestTimeEditText();
        bindBroadcastRestFrequencyEditText();
        bindLowBatteryLedSwitch();
    }

    private void bindInformationPageView() {
        getContentView().findViewById(R.id.fragmentWearableBeacon_informationPageButton).setOnClickListener(this);
    }

    private void bindPowerPageView() {
        getContentView().findViewById(R.id.fragmentWearableBeacon_powerPageButton).setOnClickListener(this);
    }

    private void bindDistancePageView() {
        getContentView().findViewById(R.id.fragmentWearableBeacon_distancePageButton).setOnClickListener(this);
    }

    private void bindDelayRestTimeEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentWearableBeacon_delayRestTimeEditText);
        if (!LoginManager.getInstance().isFastSetting() || StringUtil.isEmpty(LoginManager.getInstance().getRestTime())) {
            editText.setText(String.valueOf(bluetoothDeviceItemEntity.getRestTime()));
        } else {
            editText.setText(LoginManager.getInstance().getRestTime());
        }
    }

    private void bindBroadcastRestFrequencyEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentWearableBeacon_restBroadcastFrequencyEditText);
        if (!LoginManager.getInstance().isFastSetting() || StringUtil.isEmpty(LoginManager.getInstance().getRestBroadcastFrequency())) {
            editText.setText(String.valueOf(bluetoothDeviceItemEntity.getRestBroadcastFrequency()));
        } else {
            editText.setText(LoginManager.getInstance().getRestBroadcastFrequency());
        }
    }

    private void bindDisableAlarmSwitch() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        Switch disableAlarmSwitch = getContentView().findViewById(R.id.fragmentWearableBeacon_disableAlarmSwitch);
        disableAlarmSwitch.setOnCheckedChangeListener(this);
        disableAlarmSwitch.setChecked(bluetoothDeviceItemEntity.isDisableAlarm());
    }

    private void bindSensorSwitch() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        Switch sensorSwitch = getContentView().findViewById(R.id.fragmentWearableBeacon_sensorSwitch);
        sensorSwitch.setChecked(bluetoothDeviceItemEntity.getSensorStatus() == 1);
        sensorSwitch.setOnCheckedChangeListener(this);
    }

    private void bindTrackerSwitch() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        Switch trackerSwitch = getContentView().findViewById(R.id.fragmentWearableBeacon_trackerSwitch);
        trackerSwitch.setChecked(bluetoothDeviceItemEntity.getTrackerStatus() == 1);
        trackerSwitch.setOnCheckedChangeListener(this);
    }

    private void bindResetTrackerButton() {
        getContentView().findViewById(R.id.fragmentWearableBeacon_resetTrackerButton).setOnClickListener(this);
    }

    private void bindSensorSensitiveEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentWearableBeacon_sensorSensitiveEditText);
        editText.setText(String.valueOf(bluetoothDeviceItemEntity.getSensorSensitive()));
    }

    private void bindSensorSamplingEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentWearableBeacon_sensorSamplingEditText);
        editText.setText(String.valueOf(bluetoothDeviceItemEntity.getSensorSampling()));
    }

    private void bindBuzzerTestButton() {
        getContentView().findViewById(R.id.fragmentWearableBeacon_buzzerTestButton).setOnClickListener(this);
    }

    private void bindLowBatteryLedSwitch() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        Switch lowBatteryLedSwitch = getContentView().findViewById(R.id.fragmentWearableBeacon_batteryLedSwitch);
        lowBatteryLedSwitch.setChecked(bluetoothDeviceItemEntity.getLowBatteryLedStatus() == 1);
        lowBatteryLedSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragmentWearableBeacon_informationPageButton:
                setCurrentPage(INFORMATION_BEACON_PAGE);
                break;
            case R.id.fragmentWearableBeacon_powerPageButton:
                setCurrentPage(POWER_BEACON_PAGE);
                break;
            case R.id.fragmentWearableBeacon_distancePageButton:
                setCurrentPage(DISTANCE_BEACON_PAGE);
                break;
            case R.id.fragmentWearableBeacon_resetTrackerButton:
                onResetTrackerButtonClick();
                break;
            case R.id.fragmentWearableBeacon_buzzerTestButton:
                onBuzzerTestButtonClick();
                break;
        }
    }

    private void setCurrentPage(int currentPage) {
        EditDeviceActivity activity = (EditDeviceActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            activity.setCurrentPage(currentPage);
        }
    }

    private void onResetTrackerButtonClick() {
        if (isClickBlock()) {
            return;
        }
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_R_STEPS));
        BluetoothConnectDeviceManager.getInstance().executeCommandTask();
    }

    private void onBuzzerTestButtonClick() {
        if (isClickBlock()) {
            return;
        }
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_SET_ACC_UUID, LoginManager.getInstance().getAccountDataEntity().generateAccessUuid()),
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_BUZZER, getBuzzerBytes()));
        BluetoothConnectDeviceManager.getInstance().executeCommandTask();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.fragmentWearableBeacon_sensorSwitch:
                onSwitchCheckedChanged(UsBeaconCommand.CMD_W_ENABLE_SENSOR_DATA, isChecked);
                break;
            case R.id.fragmentWearableBeacon_trackerSwitch:
                onSwitchCheckedChanged(UsBeaconCommand.CMD_W_ENABLE_ADV_STEPS, isChecked);
                break;
            case R.id.fragmentWearableBeacon_disableAlarmSwitch:
                onSwitchCheckedChanged(UsBeaconCommand.CMD_W_DISABLE_ALARM_SIGNALS, isChecked);
                break;
            case R.id.fragmentWearableBeacon_batteryLedSwitch:
                onSwitchCheckedChanged(UsBeaconCommand.CMD_W_ENABLE_LOW_BATTERY_LED, isChecked);
                //Change value for avoiding to show error when press saving button.
                bluetoothDeviceItemEntity.setLowBatteryLedStatus(isChecked ? 1: 0);
                break;
        }
    }

    private void onSwitchCheckedChanged(byte command, boolean isChecked) {
        int enable = isChecked ? 1 : 0;
        byte[] enableSensorBytes = new byte[1];
        enableSensorBytes[0] = (byte) (enable & 0x00FF);
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_SET_ACC_UUID, LoginManager.getInstance().getAccountDataEntity().generateAccessUuid()),
                UsBeaconCommand.genCmdData(command, enableSensorBytes));
        BluetoothConnectDeviceManager.getInstance().executeCommandTask();
    }

    protected byte[] getBuzzerBytes() {
        byte[] buzzerBytes = new byte[7];
        int frequency = 2700;
        buzzerBytes[0] = (byte) (frequency & 0x00FF);
        buzzerBytes[1] = (byte) ((frequency >>> 8) & 0x00FF);
        buzzerBytes[2] = (byte) (getBuzzerOnValue() & 0x00FF);
        buzzerBytes[3] = (byte) ((getBuzzerOnValue() >>> 8) & 0x00FF);
        buzzerBytes[4] = (byte) (getBuzzerOffValue() & 0x00FF);
        buzzerBytes[5] = (byte) ((getBuzzerOffValue() >>> 8) & 0x00FF);
        buzzerBytes[6] = (byte) (getBuzzerCountValue() & 0x00FF);
        return buzzerBytes;
    }

    protected int getGSensorSensitive() {
        EditText editText = getContentView().findViewById(R.id.fragmentWearableBeacon_sensorSensitiveEditText);
        String textString = editText.getText().toString();
        int gSensorSensitive = StringUtil.isEmpty(textString) ? 0 : Integer.parseInt(textString);
        gSensorSensitive = gSensorSensitive < 1 ? 1 : Math.min(gSensorSensitive, 10);
        return gSensorSensitive;
    }

    protected int getGSensorSampling() {
        EditText editText = getContentView().findViewById(R.id.fragmentWearableBeacon_sensorSamplingEditText);
        String textString = editText.getText().toString();
        int gSensorSampling = StringUtil.isEmpty(textString) ? 0 : Integer.parseInt(textString);
        gSensorSampling = gSensorSampling < 60 ? 60 : Math.min(gSensorSampling, 600);
        return gSensorSampling;
    }

    protected int getDelayRestTimeValue() {
        EditText editText = getContentView().findViewById(R.id.fragmentWearableBeacon_delayRestTimeEditText);
        String textString = editText.getText().toString();
        return StringUtil.isEmpty(textString) ? 1 : Integer.parseInt(textString);
    }

    protected int getRestBroadcastFrequencyValue() {
        EditText editText = getContentView().findViewById(R.id.fragmentWearableBeacon_restBroadcastFrequencyEditText);
        String textString = editText.getText().toString();
        return StringUtil.isEmpty(textString) ? 1 : Integer.parseInt(textString);
    }

    private int getBuzzerOnValue() {
        EditText buzzerOnEditText = getContentView().findViewById(R.id.fragmentWearableBeacon_buzzerOnEditText);
        String textString = buzzerOnEditText.getText().toString();
        return StringUtil.isEmpty(textString) ? 500 : Integer.parseInt(textString);
    }

    private int getBuzzerOffValue() {
        EditText buzzerOffEditText = getContentView().findViewById(R.id.fragmentWearableBeacon_buzzerOffEditText);
        String textString = buzzerOffEditText.getText().toString();
        return StringUtil.isEmpty(textString) ? 50 : Integer.parseInt(textString);
    }

    private int getBuzzerCountValue() {
        EditText buzzerCountEditText = getContentView().findViewById(R.id.fragmentWearableBeacon_buzzerCountEditText);
        String textString = buzzerCountEditText.getText().toString();
        return StringUtil.isEmpty(textString) ? 1 : Integer.parseInt(textString);
    }

}
