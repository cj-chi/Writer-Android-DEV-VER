package com.THLight.BLE.USBeacon.Writer.Simple.comparator;

import com.THLight.BLE.USBeacon.Writer.Simple.entity.scan.BluetoothDeviceItemEntity;

import java.util.Comparator;

public class SortScanDeviceListItemComparator implements Comparator<BluetoothDeviceItemEntity> {

    @Override
    public int compare(BluetoothDeviceItemEntity firstEntity, BluetoothDeviceItemEntity secondEntity) {
        if (firstEntity == null || secondEntity == null) {
            return 0;
        }
        return Integer.compare(Integer.parseInt(secondEntity.getRssi()), Integer.parseInt(firstEntity.getRssi()));
    }
}
