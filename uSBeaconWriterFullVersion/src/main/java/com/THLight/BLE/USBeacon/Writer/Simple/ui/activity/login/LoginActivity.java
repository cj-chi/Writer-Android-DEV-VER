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
import android.widget.Toast;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.register.RegisterActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.scan.ScanDeviceListActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.VersionUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.LoginTask;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.ForgetPassWordTask;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.ForgetPassWordTask.ForgetPassWordTaskResponseListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener,
        ForgetPassWordTaskResponseListener, LoginTask.LoginResponseListener {
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
                || !StringUtil.isEmail(getAccountEditTextString())) {
            toastMessageView(this, getString(R.string.login_failed));
        } else {
            showLoadingDialog(null, "請稍後...");
            startWebServiceTask(new ForgetPassWordTask(this, getAccountEditTextString()));
        }
    }

    @Override
    public void onForgetPassWordResponseSuccess() {
        hideLoadingDialog();
        toastMessageView(this, getString(R.string.password_send));
    }

    @Override
    public void onForgetPassWordResponseError() {
        hideLoadingDialog();
        toastMessageView(this, getString(R.string.account_not_exist));
    }

    private void startLoginTask() {
        if (isClickBlock()) {
            return;
        } else if (checkEditTextEmpty()) {
            toastMessageView(this, getString(R.string.error_login));
        } else {
            showLoadingDialog(null, "請稍後...");
            startWebServiceTask(new LoginTask(this, getAccountEditTextString(), getPassWordEditTextString()));
        }
    }

    @Override
    public void onLoginResponseSuccess(String response) {
        hideLoadingDialog();
        LoginManager.getInstance().setAccountDataString(response);
        saveAccountList();
        startScanDeviceListActivity();
    }

    @Override
    public void onLoginNetworkError() {
        hideLoadingDialog();
        toastMessageView(this, getString(R.string.network_no_connect));
    }

    @Override
    public void onLoginResponseError() {
        hideLoadingDialog();
        toastMessageView(this, getString(R.string.login_failed));
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

    private String getAccountEditTextString() {
        return ((EditText) findViewById(R.id.activityLogin_accountEditText)).getText().toString();
    }

    private String getPassWordEditTextString() {
        return ((EditText) findViewById(R.id.activityLogin_passwordEditText)).getText().toString();
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