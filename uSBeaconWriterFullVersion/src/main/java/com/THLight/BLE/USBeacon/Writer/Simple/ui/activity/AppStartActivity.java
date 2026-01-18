package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.login.LoginActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.scan.ScanDeviceListActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.util.ScreenUtil;

/**
 * Created by Allen on 2020/3/4.
 */
public class AppStartActivity extends Activity implements OnCompletionListener {
    private static final String SHARED_PREFERENCES_FILE_APP_START = "SHARED_PREFERENCES_FILE_APP_START";
    private static final String SHARED_PREFERENCES_BOOLEAN_SPECIAL_DIALOG_SHOWN = "SHARED_PREFERENCES_BOOLEAN_SPECIAL_DIALOG_SHOWN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        showSpecialVersionDialogIfNeeded();
    }

    private void showSpecialVersionDialogIfNeeded() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE_APP_START, MODE_PRIVATE);
        boolean shown = sharedPreferences.getBoolean(SHARED_PREFERENCES_BOOLEAN_SPECIAL_DIALOG_SHOWN, false);
        if (shown) {
            bindVideoView();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("特殊版本提示")
                .setMessage("這是特殊版本，與原版 App 可共存。請記得依需求記錄資訊後再繼續使用。")
                .setPositiveButton("我知道了", (dialog, which) -> {
                    sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_BOOLEAN_SPECIAL_DIALOG_SHOWN, true).apply();
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> bindVideoView())
                .setCancelable(true)
                .show();
    }

    private void bindVideoView() { // 開場動畫
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.opening);
        VideoView videoView = findViewById(R.id.activityAppStart_videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setVisibility(View.GONE);
        videoView.setLayoutParams(new FrameLayout.LayoutParams(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this)));
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.start();
        videoView.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) { // 播放結束
        startSpecifyActivity();
    }

    private void startSpecifyActivity() {
        if (LoginManager.getInstance().getAccountDataEntity() == null) { // 若無登入過需先登入
            startLoginActivity();
        } else {  // 有登入過可直接使用
            startScanDeviceActivity();
        }
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startScanDeviceActivity() {
        Intent intent = new Intent(this, ScanDeviceListActivity.class);
        startActivity(intent);
    }
}
