package com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.scan.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.THLight.BLE.USBeacon.Writer.Simple.R;
import com.THLight.BLE.USBeacon.Writer.Simple.ui.recycler.RecyclerViewAdapter;
import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;
import com.THLight.BLE.USBeacon.Writer.Simple.util.StringUtil;

import java.util.List;

public class ScanDeviceViewType implements RecyclerViewAdapter.ViewTypeInterface {
    private List<BluetoothDeviceItemEntity> bluetoothDeviceItemEntityList;
    private ScanDeviceItemListener listener;

    public ScanDeviceViewType(ScanDeviceItemListener listener, List<BluetoothDeviceItemEntity> bluetoothDeviceItemEntityList) {
        this.listener = listener;
        this.bluetoothDeviceItemEntityList = bluetoothDeviceItemEntityList;
    }

    @Override
    public int getItemCount() {
        return bluetoothDeviceItemEntityList != null && bluetoothDeviceItemEntityList.size() > 0 ? bluetoothDeviceItemEntityList.size() : 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_scan_device_item;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View itemView) {
        return new ItemViewHolder(itemView);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder viewHolder, int index) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        BluetoothDeviceItemEntity entity = bluetoothDeviceItemEntityList.get(index);
        String majorMinorString = StringUtil.isEmpty(entity.getMajor()) && StringUtil.isEmpty(entity.getMinor()) ? "" : " ( " + entity.getMajor() + " , " + entity.getMinor() + " )";
        String informationString = entity.getDeviceName() +
                majorMinorString +
                "\n" + entity.getMacAddress() +
                "\n" + entity.getRssi() + " dpm";
        itemViewHolder.informationTextView.setText(informationString);
    }

    public void setBluetoothDeviceItemEntityList(List<BluetoothDeviceItemEntity> bluetoothDeviceItemEntityList) {
        this.bluetoothDeviceItemEntityList = bluetoothDeviceItemEntityList;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView informationTextView;

        private ItemViewHolder(View itemView) {
            super(itemView);
            informationTextView = itemView.findViewById(R.id.adapterScanDeviceItem_informationTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onScanDeviceItemClick(getLayoutPosition());
        }
    }

    public interface ScanDeviceItemListener {
        void onScanDeviceItemClick(int position);
    }
}
