/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;

class SensorTag {
    SensorTag(Context context) {
        BluetoothManager bluetoothManager
                = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice("B0:B4:48:BE:67:82");
        bluetoothDevice.connectGatt(context, true, new BluetoothGattCallback() {
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                gatt.discoverServices();
            }
        });
    }
}
