package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.command.UsBeaconCommand;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.SendBroadcastHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager.ReadRemoteRSSIListener;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.util.ScreenUtil;

public class ReadRemoteRSSIDialogActivity extends BaseActivity implements OnClickListener, Callback, ReadRemoteRSSIListener {
    private static final int MESSAGE_START_READ_REMOTE_RSSI = 1;
    private static final int MESSAGE_STOP_READ_REMOTE_RSSI = 2;
    private Handler handler = new Handler(this);
    private boolean isCalibrating = false;
    private boolean isCompleted;
    private int elapsedTime;
    private int currentValue;
    private int totalValue;
    private int averageValue;
    private int maxValue;
    private int minValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_alpha_in_duration_300, R.anim.activity_alpha_out_duration_300);
        setContentView(R.layout.activity_dialog_read_remote_rssi);
        bindMainView();
        bindContentView();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_alpha_in_duration_300, R.anim.activity_alpha_out_duration_300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.handler.removeMessages(MESSAGE_START_READ_REMOTE_RSSI);
        this.handler.removeMessages(MESSAGE_STOP_READ_REMOTE_RSSI);
        this.finishAndRemoveTask();
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MESSAGE_START_READ_REMOTE_RSSI:
                onMessageStartReadRemoteRSSI();
                break;
            case MESSAGE_STOP_READ_REMOTE_RSSI:
                onMessageStopReadRemoteRSSI();
                break;
        }
        return false;
    }

    @Override
    public void onReadRemoteRSSIResponse(int currentValue) {
        this.currentValue = currentValue;
        this.totalValue += currentValue;
        this.averageValue = totalValue / elapsedTime;
        this.maxValue = Math.max(currentValue, maxValue);
        this.minValue = Math.min(currentValue, minValue);
        runOnUiThread(() -> {
            bindContentView();
            addShapeView();
        });
    }

    private void bindContentView() {
        bindBackgroundView();
        bindMaxValueTextView();
        bindMinValueTextView();
        bindTimeLeftTextView();
        bindAverageValueTextView();
        bindSetCalibrateButton();
        bindEnableCalibrateButton();
    }

    private void bindMainView() {
        findViewById(R.id.activityDialogReadRemoteRSSI_mainView).setOnClickListener(this);
    }

    private void bindBackgroundView() {
        View view = findViewById(R.id.activityDialogReadRemoteRSSI_contentView);
        view.setBackgroundResource(isCompleted ? R.drawable.ca_calibrate_background_complete : R.drawable.ca_calibrate_background);
    }

    private void bindTimeLeftTextView() {
        String valueString = "0:" + (30 - elapsedTime);
        TextView textView = findViewById(R.id.activityDialogReadRemoteRSSI_timeLeftTextView);
        textView.setText(valueString);
    }

    private void bindMaxValueTextView() {
        TextView textView = findViewById(R.id.activityDialogReadRemoteRSSI_maxValueTextView);
        textView.setText(String.valueOf(maxValue));
    }

    private void bindMinValueTextView() {
        TextView textView = findViewById(R.id.activityDialogReadRemoteRSSI_minValueTextView);
        textView.setText(String.valueOf(minValue));
    }

    private void bindAverageValueTextView() {
        TextView textView = findViewById(R.id.activityDialogReadRemoteRSSI_averageValueTextView);
        textView.setText(String.valueOf(averageValue));
    }

    private void bindEnableCalibrateButton() {
        View view = findViewById(R.id.activityDialogReadRemoteRSSI_enableButton);
        view.setBackgroundResource(isCalibrating ? R.drawable.btn_ca_calibrate_stop : R.drawable.btn_ca_calibrate_startover);
        view.setOnClickListener(this);
    }

    private void bindSetCalibrateButton() { // 確定跑完30秒測量才允許按
        View view = findViewById(R.id.activityDialogReadRemoteRSSI_setButton);
        view.setEnabled(!isCalibrating && isCompleted);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activityDialogReadRemoteRSSI_mainView:
                finish();
                break;
            case R.id.activityDialogReadRemoteRSSI_enableButton:
                onEnableCalibrateButtonClick();
                break;
            case R.id.activityDialogReadRemoteRSSI_setButton:
                onSettingValueButtonClick();
                break;
        }
    }

    private void onEnableCalibrateButtonClick() { // 開始新的讀取RSSI的動作
        initValue();
        removeShapeView();
        switchCalibrateValue();
        bindContentView();
    }

    private void onSettingValueButtonClick() { // 將 最近讀取RSSI 得到的平均值寫入beacon
        BluetoothConnectDeviceManager.getInstance().addCommandToQueue(
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_SET_ACC_UUID, LoginManager.getInstance().getAccountDataEntity().generateAccessUuid()),
                UsBeaconCommand.genCmdData(UsBeaconCommand.CMD_W_BEACON_RSSI, (byte) averageValue));
        BluetoothConnectDeviceManager.getInstance().executeCommandTask();
        toastMessageView(this, getString(R.string.set_up_success));
        SendBroadcastHelper.sendAverageValueChangeAction(this, averageValue);
        finish();
    }

    private void initValue() {
        this.isCompleted = false;
        this.maxValue = -99;
        this.averageValue = 0;
        this.minValue = 0;
        this.elapsedTime = 0;
        this.totalValue = 0;
        this.currentValue = 0;
    }

    private void switchCalibrateValue() { // 開始或暫停
        if (!isCalibrating) {
            this.handler.removeMessages(MESSAGE_STOP_READ_REMOTE_RSSI);
            this.handler.sendEmptyMessage(MESSAGE_START_READ_REMOTE_RSSI);
        } else {
            this.handler.removeMessages(MESSAGE_START_READ_REMOTE_RSSI);
            this.handler.sendEmptyMessage(MESSAGE_STOP_READ_REMOTE_RSSI);
        }
        this.isCalibrating = !isCalibrating;
    }

    private void onMessageStartReadRemoteRSSI() { // 如果讀取RSSI的次數小於30 , 就繼續讀取 , 否則結束
        if (elapsedTime < 30) {
            this.isCalibrating = true;
            this.elapsedTime++;
            BluetoothConnectDeviceManager.getInstance().readRemoteRSSI(this);
            this.handler.sendEmptyMessageDelayed(MESSAGE_START_READ_REMOTE_RSSI, 1000);
        } else {
            this.isCompleted = true;
            this.isCalibrating = false;
            this.handler.sendEmptyMessage(MESSAGE_STOP_READ_REMOTE_RSSI);
        }
    }

    private void onMessageStopReadRemoteRSSI() {
        bindBackgroundView();
        bindSetCalibrateButton();
        bindEnableCalibrateButton();
    }

    private void addShapeView() { // 根據 RSSI 進行公式換算畫出粗略的長條圖
        ViewGroup viewGroup = findViewById(R.id.activityDialogReadRemoteRSSI_shapeViewGroup);
        View view = View.inflate(this, R.layout.view_calibrate_rssi, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutParams.width = (ScreenUtil.getScreenWidth(this) - ScreenUtil.getPxByDp(this, 72)) / 30;
        layoutParams.height = (int) ((0.285 * ScreenUtil.getScreenHeight(this) / 360) * (100 + currentValue) * 3.6);
        view.setLayoutParams(layoutParams);
        viewGroup.addView(view);
    }

    private void removeShapeView() {
        ViewGroup viewGroup = findViewById(R.id.activityDialogReadRemoteRSSI_shapeViewGroup);
        viewGroup.removeAllViews();
    }
}
