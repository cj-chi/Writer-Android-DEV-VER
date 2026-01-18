package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity;

import android.app.Activity;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        bindVideoView();
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
