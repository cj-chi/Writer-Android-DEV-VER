package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.HttpURLConnectionTask;

public class BaseFragment extends Fragment {
    private View contentView;
    private ProgressDialog loadingDialog;
    private long lastClickTime = 0;


    @Override
    public void onDestroyView() {
        hideLoadingDialog();
        super.onDestroyView();
    }

    protected View getContentView() {
        return contentView;
    }

    protected void setContentView(View contentView) {
        this.contentView = contentView;
    }

    protected void showLoadingDialog(String title, String message) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogStyle);
        }
        loadingDialog.setTitle(title);
        loadingDialog.setMessage(message);
        loadingDialog.show();
    }

    protected void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    protected void showErrorDialog(String title, String message, boolean cancelable) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);
        builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    protected void showKeyboardView() {
        if (getContentView() == null || getActivity() == null || getActivity().getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    protected void hideKeyboardView() {
        if (getContentView() == null || getActivity() == null || getActivity().getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    protected void startWebServiceTask(HttpURLConnectionTask task) {
        task.start();
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
