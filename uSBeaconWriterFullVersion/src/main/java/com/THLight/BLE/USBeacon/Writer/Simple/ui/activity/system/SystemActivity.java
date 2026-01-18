package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toolbar;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.login.LoginActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;

public class SystemActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.activity_translate_right_in, R.anim.activity_scale_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        bindContentView();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_scale_in, R.anim.activity_translate_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindContentView() {
        bindToolBar();
        enableFastSetting(LoginManager.getInstance().isFastSetting());
        enableAesSetting(LoginManager.getInstance().isAesSetting());
        bindLogoutView();
    }

    private void bindToolBar() {
        Toolbar toolbar = findViewById(R.id.activitySystem_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.grey_1));
        toolbar.setBackgroundColor(getResources().getColor(R.color.white_1));
        toolbar.setTitle(R.string.system);
        setActionBar(toolbar);
    }

    private void enableFastSetting(boolean isEnable) {
        LoginManager.getInstance().setFastSettingBoolean(isEnable);
        Switch switchView = findViewById(R.id.activitySystem_fastSettingSwitch);
        switchView.setChecked(isEnable);
    }

    private void enableAesSetting(boolean isEnable) {
        LoginManager.getInstance().setAesSettingBoolean(isEnable);
        Switch switchView = findViewById(R.id.activitySystem_aesSettingSwitch);
        switchView.setChecked(isEnable);
    }

    private void bindLogoutView() {
        findViewById(R.id.activitySystem_logoutTextView).setOnClickListener(this);
        findViewById(R.id.activitySystem_fastSettingTextView).setOnClickListener(this);
        findViewById(R.id.activitySystem_aesSettingTextView).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activitySystem_logoutTextView:
                LoginManager.getInstance().logout();
                startLoginActivity();
                break;
            case R.id.activitySystem_fastSettingTextView:
                enableFastSetting(!LoginManager.getInstance().isFastSetting());
                break;
            case R.id.activitySystem_aesSettingTextView:
                enableAesSetting(!LoginManager.getInstance().isAesSetting());
                break;
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
