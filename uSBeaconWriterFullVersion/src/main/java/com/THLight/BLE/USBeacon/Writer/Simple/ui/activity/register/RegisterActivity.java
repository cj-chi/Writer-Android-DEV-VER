package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.register;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LocalAuthStore;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

public class RegisterActivity extends BaseActivity implements OnClickListener {

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
        bindEditorActionListeners();
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
        } else if (!StringUtil.isFourAlphaNumeric(getAccountEditTextString())) {
            getAccountEditText().setError(getString(R.string.account_format_error));
        } else if (!StringUtil.isFourAlphaNumeric(getPassWordEditTextString())) {
            getPassWordEditText().setError(getString(R.string.password_format_error));
        } else if (!StringUtil.isFourAlphaNumeric(getConfirmPassWordEditTextString())) {
            getConfirmPassWordEditText().setError(getString(R.string.confirm_password_format_error));
        } else if (!checkPassWordEditString()) {
            getConfirmPassWordEditText().setError("請確認是否與密碼輸入相同");
        } else {
            showLoadingDialog(null, "請稍後...");
            boolean registered = LocalAuthStore.getInstance()
                    .register(getAccountEditTextString(), getPassWordEditTextString());
            hideLoadingDialog();
            if (registered) {
                showRegisterSuccessDialog();
            } else {
                toastMessageView(this, getString(R.string.email_used));
            }
        }
    }

    private void showRegisterSuccessDialog() {
        String account = getAccountEditTextString();
        String password = getPassWordEditTextString();
        String message = String.format(getString(R.string.register_success_message), account, password);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.register_success_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.register_success_button), (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false)
                .show();
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

    private void bindEditorActionListeners() {
        OnEditorActionListener listener = (view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateFourAlphaNumeric((EditText) view);
            }
            return false;
        };
        getAccountEditText().setOnEditorActionListener(listener);
        getPassWordEditText().setOnEditorActionListener(listener);
        getConfirmPassWordEditText().setOnEditorActionListener(listener);
    }

    private void validateFourAlphaNumeric(EditText editText) {
        String text = editText.getText().toString();
        if (editText.getId() == R.id.activityCreateAccount_accountEditText) {
            if (!StringUtil.isFourAlphaNumeric(text)) {
                editText.setError(getString(R.string.account_format_error));
            }
        } else if (editText.getId() == R.id.activityCreateAccount_passWordEditText) {
            if (!StringUtil.isFourAlphaNumeric(text)) {
                editText.setError(getString(R.string.password_format_error));
            }
        } else if (editText.getId() == R.id.activityCreateAccount_confirmPassWordEditText) {
            if (!StringUtil.isFourAlphaNumeric(text)) {
                editText.setError(getString(R.string.confirm_password_format_error));
            }
        }
    }

}
