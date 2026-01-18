package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BroadcastReceiverHelper.ActionNetWorkChangeListener;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.NetworkManager;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

/**
 * Created by Allen on 2020/3/4.
 */
public class BaseActivity extends FragmentActivity implements ActionNetWorkChangeListener {
    private ProgressDialog loadingDialog;
    private long lastClickTime = 0;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BroadcastReceiverHelper.registerListener(this);
    }

    @Override
    protected void onDestroy() {
        hideLoadingDialog();
        BroadcastReceiverHelper.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onActionNetWorkChange() { // 網路狀態改變會彈出或收起 SnackBar
        if (NetworkManager.getInstance().isNetWorkNormal()) {
            hideMessageWithSnackBar();
        } else {
            showMessageWithSnackBar("請檢查網路連線");
        }
    }

    protected void showLoadingDialog(String title, String message, boolean isCanCancel) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        }
        loadingDialog.setTitle(title);
        loadingDialog.setMessage(message);
        loadingDialog.setCancelable(isCanCancel);
        loadingDialog.setCanceledOnTouchOutside(isCanCancel);
        if (!isFinishing()) {
            loadingDialog.show();
        }
    }

    protected void showLoadingDialog(String title, String message) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        }
        loadingDialog.setTitle(title);
        loadingDialog.setMessage(message);
        loadingDialog.setCancelable(false);
        if (!isFinishing()) {
            loadingDialog.show();
        }
    }

    protected void hideLoadingDialog() {
        runOnUiThread(() -> {
            if (loadingDialog != null && loadingDialog.isShowing() && !isFinishing()) {
                loadingDialog.dismiss();
            }
        });
    }

    protected void showCustomDialog(int iconId, String title, String message, boolean cancelable, DialogInterface.OnClickListener listener) {
        if (isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(iconId);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    protected View getContentView() {
        return findViewById(android.R.id.content);
    }

    public void showMessageWithSnackBar(String message) {
        runOnUiThread(() -> {
            hideLoadingDialog();
            snackbar = Snackbar.make(getContentView(), message, Snackbar.LENGTH_INDEFINITE);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.transparent1));
            snackBarView.setOnClickListener(view -> {
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
            });
            TextView textView = snackBarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.white_1));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(16);
            snackbar.show();
        });
    }

    private void hideMessageWithSnackBar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    protected void showKeyboardView() {
        if (getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    protected void hideKeyboardView() {
        if (getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    protected void toastMessageView(final Context context, final String message) {
        runOnUiThread(() -> {
            hideLoadingDialog();
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_message, findViewById(R.id.toastMessage_frameLayout));
            TextView messageView = layout.findViewById(R.id.toastMessage_messageTextView);
            messageView.setText(message);
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        });
    }

    protected void startWebServiceTask(HttpURLConnectionTask task) {
        task.start();
    }

    public boolean isActivityDestroy() {
        return isFinishing();
    }

    protected boolean isClickBlock() {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > 500L) {
            lastClickTime = currentClickTime;
            return false;
        }
        return true;
    }
}