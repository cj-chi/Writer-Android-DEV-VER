package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.register;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.RegisterTask;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.RegisterTask.RegisterResponseListener;

public class RegisterActivity extends BaseActivity implements OnClickListener, RegisterResponseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_alpha_in_duration_300, R.anim.activity_alpha_out_duration_300);
        setContentView(R.layout.activity_register);
        bindContentView();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_alpha_in_duration_300, R.anim.activity_alpha_out_duration_300);
    }

    private void bindContentView() {
        bindMainView();
        bindSettingButton();
    }

    private void bindMainView() {
        findViewById(R.id.activityCreateAccount_mainView).setOnClickListener(this);
    }

    private void bindSettingButton() {
        findViewById(R.id.activityCreateAccount_settingButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activityCreateAccount_mainView:
                finish();
            case R.id.activityCreateAccount_settingButton:
                startRegisterTask();
                break;
        }
    }

    private void startRegisterTask() {
        if (isClickBlock()) {
            return;
        } else if (checkEditTextEmpty()) {
            if (StringUtil.isEmpty(getAccountEditTextString())) {
                getAccountEditText().setError("請輸入E-mail");
            }
            if (StringUtil.isEmpty(getPassWordEditTextString())) {
                getPassWordEditText().setError("請輸入密碼");
            }
            if (StringUtil.isEmpty(getConfirmPassWordEditTextString())) {
                getConfirmPassWordEditText().setError("請確認密碼");
            }
        } else if (!StringUtil.isEmail(getAccountEditTextString())) {
            getAccountEditText().setError("email格式錯誤");
        } else if (getPassWordEditTextString().length() < 6) {
            getPassWordEditText().setError("密碼數量不得低於6位");
        } else if (!checkPassWordEditString()) {
            getConfirmPassWordEditText().setError("請確認是否與密碼輸入相同");
        } else {
            showLoadingDialog(null, "請稍後...");
            startWebServiceTask(new RegisterTask(this, getAccountEditTextString(), getPassWordEditTextString()));
        }
    }

    private boolean checkEditTextEmpty() {
        return StringUtil.isEmpty(getAccountEditTextString()) || StringUtil.isEmpty(getPassWordEditTextString()) || StringUtil.isEmpty(getConfirmPassWordEditTextString());
    }

    private boolean checkPassWordEditString() {
        return StringUtil.isEquals(getPassWordEditTextString(), getConfirmPassWordEditTextString());
    }


    private String getAccountEditTextString() {
        return getAccountEditText().getText().toString();
    }

    private String getPassWordEditTextString() {
        return getPassWordEditText().getText().toString();
    }

    private String getConfirmPassWordEditTextString() {
        return getConfirmPassWordEditText().getText().toString();
    }

    private EditText getAccountEditText() {
        return (EditText) findViewById(R.id.activityCreateAccount_accountEditText);
    }

    private EditText getPassWordEditText() {
        return (EditText) findViewById(R.id.activityCreateAccount_passWordEditText);
    }

    private EditText getConfirmPassWordEditText() {
        return (EditText) findViewById(R.id.activityCreateAccount_confirmPassWordEditText);
    }

    @Override
    public void onRegisterResponseSuccess(String response) {
        hideLoadingDialog();
        toastMessageView(this, getString(R.string.success));
        finish();
    }

    @Override
    public void onRegisterNetworkError() {
        hideLoadingDialog();
        toastMessageView(this, getString(R.string.network_no_connect));
    }

    @Override
    public void onRegisterResponseError() {
        hideLoadingDialog();
        toastMessageView(this, getString(R.string.email_used));
    }
}
