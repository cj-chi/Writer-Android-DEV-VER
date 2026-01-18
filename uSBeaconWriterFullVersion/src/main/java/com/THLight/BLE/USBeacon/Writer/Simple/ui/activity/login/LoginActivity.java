package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.login.AccountDataEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LocalAuthStore;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.register.RegisterActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.scan.ScanDeviceListActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LocalAuthStore;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.VersionUtil;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {
    private List<String> userAccountList = new ArrayList<>();
    private List<String> userPasswordList = new ArrayList<>();
    private boolean initialization;
    private int lastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initValue();
        bindContentView();
    }

    private void initValue() {
        this.userAccountList = LoginManager.getInstance().getUserAccountList();
        this.userPasswordList = LoginManager.getInstance().getUserPasswordList();
        this.lastPosition = LoginManager.getInstance().getLastPosition();
        this.initialization = true;
    }

    private void bindContentView() {
        bindAccountSpinner();
        bindAccountEditText();
        bindPasswordEditText();
        bindEditorActionListeners();
        bindForgetPasswordTextView();
        bindCreateAccountButton();
        bindLoginButton();
        bindVersionTextView();
    }

    private void bindAccountSpinner() {
        if (userAccountList != null && !userAccountList.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userAccountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = findViewById(R.id.activityLogin_accountSpinner);
            spinner.setAdapter(adapter);
            spinner.setSelection(lastPosition);
            spinner.setOnItemSelectedListener(this);
        }
    }

    private void bindAccountEditText() {
        if (userPasswordList == null || StringUtil.isEmpty(userAccountList.get(lastPosition))) {
            return;
        }
        EditText editText = findViewById(R.id.activityLogin_accountEditText);
        editText.setText(userAccountList.get(lastPosition));
        editText.setOnClickListener(this);
    }

    private void bindPasswordEditText() {
        if (userPasswordList == null || StringUtil.isEmpty(userPasswordList.get(lastPosition))) {
            return;
        }
        EditText editText = findViewById(R.id.activityLogin_passwordEditText);
        editText.setText(userPasswordList.get(lastPosition));
        editText.setOnClickListener(this);
    }

    private void bindForgetPasswordTextView() {
        findViewById(R.id.activityLogin_forgetPasswordTextView).setOnClickListener(this);
    }

    private void bindLoginButton() {
        View view = findViewById(R.id.activityLogin_loginButton);
        view.setOnClickListener(this);
    }

    private void bindCreateAccountButton() {
        findViewById(R.id.activityLogin_createAccountButton).setOnClickListener(this);
    }

    private void bindVersionTextView() {
        String versionString = getString(R.string.version_name) + VersionUtil.getVersionName(this);
        TextView textView = findViewById(R.id.activityLogin_versionTextView);
        textView.setText(versionString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activityLogin_accountEditText:
                findViewById(R.id.activityLogin_accountSpinner).performClick();
                break;
            case R.id.activityLogin_forgetPasswordTextView:
                startForgetPassWordTask();
                break;
            case R.id.activityLogin_loginButton:
                startLoginTask();
                break;
            case R.id.activityLogin_createAccountButton:
                startCreateAccountActivity();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { // 由於 spinner 初始化會觸發一次, 會把之前記得位置洗掉 , 因此這裡將首次擋掉
        this.lastPosition = initialization ? lastPosition : position;
        this.initialization = false;
        bindAccountEditText();
        bindPasswordEditText();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void startForgetPassWordTask() {
        if (StringUtil.isEmpty(getAccountEditTextString())
                || !StringUtil.isFourAlphaNumeric(getAccountEditTextString())) {
            toastMessageView(this, getString(R.string.account_format_error));
            return;
        }
        if (StringUtil.isEmpty(getPassWordEditTextString())) {
            getPassWordEditText().setError(getString(R.string.password_format_error));
            return;
        }
        showLoadingDialog(null, "請稍後...");
        boolean reset = LocalAuthStore.getInstance()
                .resetPassword(getAccountEditTextString(), getPassWordEditTextString());
        hideLoadingDialog();
        if (reset) {
            toastMessageView(this, getString(R.string.password_send));
        } else {
            toastMessageView(this, getString(R.string.account_not_exist));
        }
    }

    private void startLoginTask() {
        if (isClickBlock()) {
            return;
        } else if (checkEditTextEmpty()) {
            toastMessageView(this, getString(R.string.error_login));
        } else if (!StringUtil.isFourAlphaNumeric(getAccountEditTextString())) {
            getAccountEditText().setError(getString(R.string.account_format_error));
        } else if (!StringUtil.isFourAlphaNumeric(getPassWordEditTextString())) {
            getPassWordEditText().setError(getString(R.string.password_format_error));
        } else {
            showLoadingDialog(null, "請稍後...");
            if (tryLocalLogin()) {
                hideLoadingDialog();
                startScanDeviceListActivity();
            } else {
                hideLoadingDialog();
                toastMessageView(this, getString(R.string.login_failed));
            }
        }
    }


    private boolean checkEditTextEmpty() {
        return StringUtil.isEmpty(getAccountEditTextString()) || StringUtil.isEmpty(getPassWordEditTextString());
    }

    private void saveAccountList() {
        if (userAccountList == null || userPasswordList == null) {
            userAccountList = new ArrayList<>();
            userPasswordList = new ArrayList<>();
        }

        for (String accountString : userAccountList) {
            if (StringUtil.isEquals(accountString, getAccountEditTextString())) {
                LoginManager.getInstance().setLastPosition(lastPosition);
                return;
            }
        }
        userAccountList.add(getAccountEditTextString());
        userPasswordList.add(getPassWordEditTextString());
        LoginManager.getInstance().setUserAccountList(userAccountList);
        LoginManager.getInstance().setUserPasswordList(userPasswordList);
        LoginManager.getInstance().setLastPosition(userAccountList.size() - 1); // 從新登入的位置開始
    }

    private boolean tryLocalLogin() {
        String account = getAccountEditTextString();
        String password = getPassWordEditTextString();
        boolean verified = LocalAuthStore.getInstance().verify(account, password);
        if (!verified) {
            return false;
        }
        AccountDataEntity accountDataEntity = AccountDataEntity.fromCredentials(account, password);
        LoginManager.getInstance().setAccountDataEntity(accountDataEntity);
        LoginManager.getInstance().persistPlainTextCredentials(account, password, accountDataEntity);
        saveAccountList();
        return true;
    }

    private String getAccountEditTextString() {
        return getAccountEditText().getText().toString();
    }

    private String getPassWordEditTextString() {
        return getPassWordEditText().getText().toString();
    }

    private EditText getAccountEditText() {
        return (EditText) findViewById(R.id.activityLogin_accountEditText);
    }

    private EditText getPassWordEditText() {
        return (EditText) findViewById(R.id.activityLogin_passwordEditText);
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
    }

    private void validateFourAlphaNumeric(EditText editText) {
        String text = editText.getText().toString();
        if (editText.getId() == R.id.activityLogin_accountEditText) {
            if (!StringUtil.isFourAlphaNumeric(text)) {
                editText.setError(getString(R.string.account_format_error));
            }
        } else if (editText.getId() == R.id.activityLogin_passwordEditText) {
            if (!StringUtil.isFourAlphaNumeric(text)) {
                editText.setError(getString(R.string.password_format_error));
            }
        }
    }

    private void startScanDeviceListActivity() {
        Intent intent = new Intent(this, ScanDeviceListActivity.class);
        startActivity(intent);
    }

    private void startCreateAccountActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}