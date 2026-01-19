package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseFragment;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper.ActionRefreshDataListener;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.DISTANCE_BEACON_PAGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.POWER_BEACON_PAGE;
import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.WEARABLE_BEACON_PAGE;

public class InformationBeaconFragment extends BaseFragment implements OnClickListener, ActionRefreshDataListener {
    private BluetoothDeviceItemEntity bluetoothDeviceItemEntity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentView() == null) {
            setContentView(inflater.inflate(R.layout.fragment_information_beacon, container, false));
            initValue();
            bindPowerPageView();
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
        bindContentView();
    }

    private void initValue() {
        EditDeviceActivity activity = (EditDeviceActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            this.bluetoothDeviceItemEntity = activity.entity;
        }
    }

    private void bindContentView() {
        bindNameEditText();
        bindMacAddressEditText();
        bindUuidEditText();
        bindMajorEditText();
        bindMinorEditText();
        bindBatterPowerView();
        bindLastModifiedDateTextView();
        bindHwIdEditText();
        bindUserIdEditText();
        bindAskKeyTextView();
    }

    private void bindPowerPageView() {
        getContentView().findViewById(R.id.fragmentInformationBeacon_powerPageButton).setOnClickListener(this);
    }

    private void bindDistancePageView() {
        getContentView().findViewById(R.id.fragmentInformationBeacon_distancePageButton).setOnClickListener(this);
    }

    private void bindWearablePageView() {
        getContentView().findViewById(R.id.fragmentInformationBeacon_wearablePageButton).setOnClickListener(this);
    }

    private void bindNameEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        TextView editText = getContentView().findViewById(R.id.fragmentInformationBeacon_nameTextView);
        editText.setText(bluetoothDeviceItemEntity.getDeviceName());
    }

    private void bindMacAddressEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        TextView editText = getContentView().findViewById(R.id.fragmentInformationBeacon_macAddressTextView);
        editText.setText(bluetoothDeviceItemEntity.getMacAddress());
    }

    private void bindUuidEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText part1EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart1EditText);
        EditText part2EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart2EditText);
        EditText part3EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart3EditText);
        EditText part4EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart4EditText);
        EditText part5EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart5EditText);
        applyUuidPartFilters(part1EditText, 8);
        applyUuidPartFilters(part2EditText, 4);
        applyUuidPartFilters(part3EditText, 4);
        applyUuidPartFilters(part4EditText, 4);
        applyUuidPartFilters(part5EditText, 12);

        String uuid = bluetoothDeviceItemEntity.getUuid();
        if (StringUtil.isEmpty(uuid)) {
            clearUuidParts(part1EditText, part2EditText, part3EditText, part4EditText, part5EditText);
            return;
        }
        String raw = uuid.replace("-", "").trim();
        if (raw.length() != 32) {
            clearUuidParts(part1EditText, part2EditText, part3EditText, part4EditText, part5EditText);
            return;
        }
        raw = raw.toUpperCase();
        part1EditText.setText(raw.substring(0, 8));
        part2EditText.setText(raw.substring(8, 12));
        part3EditText.setText(raw.substring(12, 16));
        part4EditText.setText(raw.substring(16, 20));
        part5EditText.setText(raw.substring(20, 32));
    }

    private void bindMajorEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentInformationBeacon_majorEditText);
        if (!LoginManager.getInstance().isFastSetting() || StringUtil.isEmpty(LoginManager.getInstance().getMajor())) {
            editText.setText(bluetoothDeviceItemEntity.getMajor());
        } else {
            editText.setText(LoginManager.getInstance().getMajor());
        }
    }

    private void bindMinorEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentInformationBeacon_minorEditText);
        if (!LoginManager.getInstance().isFastSetting() || StringUtil.isEmpty(LoginManager.getInstance().getMinor())) {
            editText.setText(bluetoothDeviceItemEntity.getMinor());
        } else {
            editText.setText(LoginManager.getInstance().getMinor());
        }
    }

    private void bindBatterPowerView() { // 根據電量來顯示格子數
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        System.out.println("bluetoothDeviceItemEntity.getBatteryPower() : " + bluetoothDeviceItemEntity.getBatteryPower());
        if (bluetoothDeviceItemEntity.getBatteryPower() >= 260) {
            getContentView().findViewById(R.id.fragmentInformationBeacon_batteryImageView1).setVisibility(View.VISIBLE);
            getContentView().findViewById(R.id.fragmentInformationBeacon_batteryImageView2).setVisibility(View.VISIBLE);
            getContentView().findViewById(R.id.fragmentInformationBeacon_batteryImageView3).setVisibility(View.VISIBLE);
        } else if (bluetoothDeviceItemEntity.getBatteryPower() > 240) {
            getContentView().findViewById(R.id.fragmentInformationBeacon_batteryImageView1).setVisibility(View.VISIBLE);
            getContentView().findViewById(R.id.fragmentInformationBeacon_batteryImageView2).setVisibility(View.VISIBLE);
        } else {
            getContentView().findViewById(R.id.fragmentInformationBeacon_batteryImageView1).setVisibility(View.VISIBLE);
        }
    }

    private void bindLastModifiedDateTextView() {
        if (getActivity() == null || getContentView() == null) {
            return;
        }
        TextView textView = getContentView().findViewById(R.id.fragmentInformationBeacon_lastModifiedDateTextView);
        textView.setText(LoginManager.getInstance().getLastModifiedDate());
    }

    private void bindHwIdEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentInformationBeacon_hwIdEditText);

        System.out.println("AES setting: " + LoginManager.getInstance().isAesSetting());
        if (LoginManager.getInstance().isAesSetting()) {
            editText.setEnabled(true);
            editText.setText(bluetoothDeviceItemEntity.getHwId());
        }
    }

    private void bindUserIdEditText() {
        if (getActivity() == null || getContentView() == null || bluetoothDeviceItemEntity == null) {
            return;
        }
        EditText editText = getContentView().findViewById(R.id.fragmentInformationBeacon_userIdEditText);
        if (LoginManager.getInstance().isAesSetting()) {
            editText.setEnabled(true);
            editText.setText(bluetoothDeviceItemEntity.getUserId());
        }
    }

    private void bindAskKeyTextView() {
        if (getActivity() == null || getContentView() == null) {
            return;
        }
        TextView textView = getContentView().findViewById(R.id.fragmentInformationBeacon_askKeyTextView);
        textView.setText(bluetoothDeviceItemEntity.getAskKey());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragmentInformationBeacon_powerPageButton:
                setCurrentPage(POWER_BEACON_PAGE);
                break;
            case R.id.fragmentInformationBeacon_distancePageButton:
                setCurrentPage(DISTANCE_BEACON_PAGE);
                break;
            case R.id.fragmentInformationBeacon_wearablePageButton:
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

    protected int getMajorValue() {
        EditText editText = getContentView().findViewById(R.id.fragmentInformationBeacon_majorEditText);
        return checkMajorMinorValue(editText.getText().toString());
    }

    protected int getMinorValue() {
        EditText editText = getContentView().findViewById(R.id.fragmentInformationBeacon_minorEditText);
        return checkMajorMinorValue(editText.getText().toString());
    }

    protected String getBeaconUuidValue() {
        EditText part1EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart1EditText);
        EditText part2EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart2EditText);
        EditText part3EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart3EditText);
        EditText part4EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart4EditText);
        EditText part5EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart5EditText);
        String part1 = getUuidPartValue(part1EditText);
        String part2 = getUuidPartValue(part2EditText);
        String part3 = getUuidPartValue(part3EditText);
        String part4 = getUuidPartValue(part4EditText);
        String part5 = getUuidPartValue(part5EditText);
        if (StringUtil.isEmpty(part1) && StringUtil.isEmpty(part2)
                && StringUtil.isEmpty(part3) && StringUtil.isEmpty(part4)
                && StringUtil.isEmpty(part5)) {
            return "";
        }
        return part1 + "-" + part2 + "-" + part3 + "-" + part4 + "-" + part5;
    }

    protected String[] getBeaconUuidParts() {
        EditText part1EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart1EditText);
        EditText part2EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart2EditText);
        EditText part3EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart3EditText);
        EditText part4EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart4EditText);
        EditText part5EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart5EditText);
        return new String[]{
                getUuidPartValue(part1EditText),
                getUuidPartValue(part2EditText),
                getUuidPartValue(part3EditText),
                getUuidPartValue(part4EditText),
                getUuidPartValue(part5EditText)
        };
    }

    protected void clearUuidPartErrors() {
        EditText part1EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart1EditText);
        EditText part2EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart2EditText);
        EditText part3EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart3EditText);
        EditText part4EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart4EditText);
        EditText part5EditText = getContentView().findViewById(R.id.fragmentInformationBeacon_uuidPart5EditText);
        part1EditText.setError(null);
        part2EditText.setError(null);
        part3EditText.setError(null);
        part4EditText.setError(null);
        part5EditText.setError(null);
    }

    protected void setUuidPartError(int index, String message) {
        int viewId;
        switch (index) {
            case 0:
                viewId = R.id.fragmentInformationBeacon_uuidPart1EditText;
                break;
            case 1:
                viewId = R.id.fragmentInformationBeacon_uuidPart2EditText;
                break;
            case 2:
                viewId = R.id.fragmentInformationBeacon_uuidPart3EditText;
                break;
            case 3:
                viewId = R.id.fragmentInformationBeacon_uuidPart4EditText;
                break;
            default:
                viewId = R.id.fragmentInformationBeacon_uuidPart5EditText;
                break;
        }
        EditText editText = getContentView().findViewById(viewId);
        editText.setError(message);
        editText.requestFocus();
    }

    private String getUuidPartValue(EditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        String value = editText.getText().toString().trim();
        return StringUtil.isEmpty(value) ? "" : value.toUpperCase();
    }

    private void applyUuidPartFilters(EditText editText, int maxLength) {
        InputFilter hexFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                        return "";
                    }
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(maxLength),
                hexFilter
        });
    }

    private void clearUuidParts(EditText part1, EditText part2, EditText part3, EditText part4, EditText part5) {
        part1.setText("");
        part2.setText("");
        part3.setText("");
        part4.setText("");
        part5.setText("");
    }

    private int checkMajorMinorValue(String textString) { // 回傳前先檢查是否超出指定邊界
        int value = StringUtil.isEmpty(textString) ? 0 : Integer.parseInt(textString);
        value = Math.max(value, 0);
        value = Math.min(value, 65535);
        return value;
    }

    protected String getHwIdValue() {
        EditText editText = getContentView().findViewById(R.id.fragmentInformationBeacon_hwIdEditText);
        return editText.getText().toString();
    }

    protected String getUserIdValue() {
        EditText editText = getContentView().findViewById(R.id.fragmentInformationBeacon_userIdEditText);
        return editText.getText().toString();
    }
}
