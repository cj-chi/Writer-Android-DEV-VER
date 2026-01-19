package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.scan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.recycler.RecyclerViewAdapter;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.scan.adapter.ScanDeviceViewType;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.scan.adapter.ScanDeviceViewType.ScanDeviceItemListener;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.system.SystemActivity;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BluetoothScanDeviceHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.BluetoothScanDeviceHelper.ScanDeviceDataListener;
import com.THLight.BLE.USBeacon.Writer.Simple.helper.RequestPermissionHelper;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.BluetoothConnectDeviceManager.ConnectDeviceStateListener;
import com.THLight.BLE.USBeacon.Writer.Simple.manager.LoginManager;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.recycler.CustomRecyclerView;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.recycler.CustomRecyclerView.CustomRecyclerViewScrollListener;
import com.THLight.BLE.USBeacon.Writer.Simple.util.FileUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.GsonUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.LogUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.ScreenUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;
import com.THLight.BLE.USBeacon.Writer.Simple.util.XmlParser;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.DownloadBeaconFileTask;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.DownloadBeaconFileTask.DownloadBeaconFileListener;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.DownloadBeaconUrlTask;
import com.THLight.BLE.USBeacon.Writer.Simple.webservice.task.DownloadBeaconUrlTask.DownloadBeaconUrlTaskResponseListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.connect.EditDeviceActivity.OUTPUT_ENTITY_DEVICE_INFO;
import static com.THLight.BLE.USBeacon.Writer.Simple.application.ConstantConfig.INTENT_STRING_JSON_DEVICE_ENTITY;


public class ScanDeviceListActivity extends BaseActivity implements CustomRecyclerViewScrollListener,
        ScanDeviceDataListener, ScanDeviceItemListener, ConnectDeviceStateListener, OnRefreshListener,
        DownloadBeaconUrlTaskResponseListener, DownloadBeaconFileListener, BluetoothConnectDeviceManager.DiscoverServiceListener {
    private static final int REQUEST_SETTING_DEVICE = 2001;
    private BluetoothScanDeviceHelper bluetoothScanDeviceHelper;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<BluetoothDeviceItemEntity> bluetoothDeviceItemEntityList = new ArrayList<>();
    private int selectedPosition;
    private List<Map<String, String>> mapList;
    private AlertDialog pidDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device_list);
        initValue();
        bindContentView();
        requestDownloadBeaconTask();
        checkBtScanRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                startSystemActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
        BluetoothConnectDeviceManager.getInstance().destroyConnectDeviceManager();
    }

    @Override
    public void onRefresh() {
        checkBtScanRequest();
        initSwipeRefresh();
    }

    private void initValue() {
        this.bluetoothScanDeviceHelper = new BluetoothScanDeviceHelper(this, this);
    }

    private void registerListener() {
        BluetoothConnectDeviceManager.getInstance().setConnectDeviceStateListener(this);
        BluetoothConnectDeviceManager.getInstance().setDiscoverServiceListener(this);
    }

    private void requestDownloadBeaconTask() {
        // Local-only mode: skip server sync for beacon list.
    }

    private void checkBtScanRequest() {
        if (!RequestPermissionHelper.isBluetoothAdapterEnable()) { // A request for open Bluetooth
            System.out.println("checkBtScanRequest step 1  ");
            RequestPermissionHelper.requestBluetoothEnable(this);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && !RequestPermissionHelper.isLocationSettingEnable(this)) { // A request for open Location
            System.out.println("checkBtScanRequest step 2  ");
            RequestPermissionHelper.requestLocationSettingEnable(this);
        } else { // check location permission
            if (RequestPermissionHelper.hasRequiredScanPermissions(this) && RequestPermissionHelper.hasLocationPermission(this)) {
                System.out.println("checkBtScanRequest step 3  ");
                requestScanDeviceTask();
            } else {
                System.out.println("checkBtScanRequest step 4  ");
                RequestPermissionHelper.requestAllScanPermissions(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // location permission response
        checkBtScanRequest();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SETTING_DEVICE) {
            if (data != null) {
                BluetoothDeviceItemEntity entity = (BluetoothDeviceItemEntity) data.getSerializableExtra(OUTPUT_ENTITY_DEVICE_INFO);
                LogUtil.log("SettingCallBack", GsonUtil.toJson(entity));
                bluetoothDeviceItemEntityList.set(selectedPosition, entity);
                recyclerViewAdapter.notifyItemChanged(selectedPosition);
            }
        } else { // Bt response
            checkBtScanRequest();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void bindContentView() {
        bindToolbar();
        bindSwipeRefresh();
        bindRecyclerView();
    }

    private void bindToolbar() {
        Toolbar toolbar = findViewById(R.id.activityScanDeviceList_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.grey_1));
        toolbar.setBackgroundColor(getResources().getColor(R.color.white_1));
        setActionBar(toolbar);
    }

    private void bindSwipeRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.activityScanDeviceList_swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.pink_1);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white_1);
        int toolbarHeightSize = ScreenUtil.getPxByDp(this, 80);
        int startPosition = toolbarHeightSize - ScreenUtil.getPxByDp(this, 36);
        int endPosition = toolbarHeightSize + ScreenUtil.getPxByDp(this, 4);
        swipeRefreshLayout.setProgressViewOffset(false, startPosition, endPosition);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initSwipeRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.activityScanDeviceList_swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void bindRecyclerView() {
        CustomRecyclerView recyclerView = findViewById(R.id.activityScanDeviceList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<RecyclerViewAdapter.ViewTypeInterface> itemTypeList = new ArrayList<>();
        ScanDeviceViewType scanDeviceViewType = new ScanDeviceViewType(this, bluetoothDeviceItemEntityList);
        itemTypeList.add(scanDeviceViewType);
        recyclerViewAdapter = new RecyclerViewAdapter(this, itemTypeList);
        recyclerView.setCustomRecyclerViewScrollListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void requestScanDeviceTask() { // 請求掃描附近裝置
        ScrollToFirstPosition();
        bluetoothDeviceItemEntityList.clear();
        showLoadingDialog(null, "請稍後...");
        bluetoothScanDeviceHelper.startScanDeviceTask();
    }

    @Override
    public void onScanDeviceItemClick(int position) { // 點擊某個裝置
        if (isClickBlock()) {
            return;
        }
        this.selectedPosition = position;
        showLoadingDialog(null, "請稍後...");
        checkExistDownloadList();
        LogUtil.log("onScanDeviceItemClick : ", GsonUtil.toJson(mapList));
        BluetoothConnectDeviceManager.getInstance().startConnectDeviceTask(bluetoothDeviceItemEntityList.get(position).getMacAddress());
    }

    @Override
    public void onScanDeviceDataResponse(List<BluetoothDeviceItemEntity> bluetoothDeviceItemEntityList) { // 掃描 Callback
        this.bluetoothDeviceItemEntityList.clear();
        this.bluetoothDeviceItemEntityList.addAll(bluetoothDeviceItemEntityList);
        runOnUiThread(() -> {
            recyclerViewAdapter.notifyDataSetChanged();
            hideLoadingDialog();
        });
    }

    @Override
    public void onScanDeviceDataErrorResponse(int errorCode) { // 掃描失敗
        hideLoadingDialog();
        toastMessageView(this, "errorCode - > " + errorCode);
    }

    @Override
    public void onConnectDeviceStateSuccess() { // 連線成功
        showPidDialog("連線成功");
        BluetoothConnectDeviceManager.getInstance().discoverService();
    }

    @Override
    public void onDisconnectDeviceState() { // 中斷連線
        showPidDialog("連線中斷");
        finishActivity(REQUEST_SETTING_DEVICE);
        toastMessageView(this, getString(R.string.disconnected) + bluetoothDeviceItemEntityList.get(selectedPosition).getMacAddress());
    }

    @Override
    public void onConnectDeviceStateError() { // 連線失敗
        showPidDialog("連線失敗");
        toastMessageView(this, getString(R.string.connect_failed) + bluetoothDeviceItemEntityList.get(selectedPosition).getMacAddress());
    }

    private void showPidDialog(String status) {
        int pid = Process.myPid();
        runOnUiThread(() -> {
            if (pidDialog != null && pidDialog.isShowing()) {
                pidDialog.dismiss();
            }
            pidDialog = new AlertDialog.Builder(this)
                    .setTitle(status)
                    .setMessage("PID: " + pid)
                    .setPositiveButton("OK", null)
                    .create();
            pidDialog.show();
        });
    }

    @Override
    public void onDiscoverServiceSuccess() {
        runOnUiThread(() -> {
            hideLoadingDialog();
            startEditDeviceActivity();
        });
    }

    @Override
    public void onDiscoverServiceFailed() {
        toastMessageView(this, getString(R.string.discover_service_failed));
    }

    @Override
    public void onDownloadBeaconUrlTaskResponseSuccess(String pathString) { // 得到已修改裝置的名單的下載地址
        startWebServiceTask(new DownloadBeaconFileTask(this, pathString)); // 訪問 server 給的 地址 把名單下載下來
    }

    @Override
    public void onDownloadBeaconFileComplete() { // 完成訪問
        try {
            parseDownloadFile();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void parseDownloadFile() throws IOException, XmlPullParserException { // 解析已下載裝置檔案的內容
        File file = new File(getExternalFilesDir("Download"), "USBeaconList.zip");
        byte[] bytes = FileUtil.unZip(file);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        XmlParser xmlParser = new XmlParser();
        xmlParser.parse(inputStream, "id", "mac_address");
        mapList = xmlParser.getMapList();
        inputStream.close();
    }

    @Override
    public void onRecyclerViewItemScroll(CustomRecyclerView recyclerView, boolean isScrollBottom) {
    }

    @Override
    public void onScrollStateIdle() {
    }

    @Override
    public void onScrollStateDragging() {
    }

    private void ScrollToFirstPosition() { // 將 recyclerView 滾回最上面
        CustomRecyclerView customRecyclerView = findViewById(R.id.activityScanDeviceList_recyclerView);
        customRecyclerView.scrollToPosition(0);
    }

    private void checkExistDownloadList() { // 檢查點擊的裝置是否曾經修改過, Yes -> 取得 targetId , No 不動作
        if (mapList == null) return;
        BluetoothDeviceItemEntity entity = bluetoothDeviceItemEntityList.get(selectedPosition);
        for (Map<String, String> map : mapList) {
            if (StringUtil.isEquals(entity.getMacAddress(), map.get("mac_address"))) {
                entity.setTargetId(Objects.requireNonNull(map.get("id")));
                break;
            }
        }
    }

    private void startEditDeviceActivity() {
        Intent intent = new Intent(ScanDeviceListActivity.this, EditDeviceActivity.class);
        intent.putExtra(INTENT_STRING_JSON_DEVICE_ENTITY, GsonUtil.toJson(bluetoothDeviceItemEntityList.get(selectedPosition)));
        startActivityForResult(intent, REQUEST_SETTING_DEVICE);
    }

    private void startSystemActivity() {
        Intent intent = new Intent(this, SystemActivity.class);
        startActivity(intent);
    }
}
