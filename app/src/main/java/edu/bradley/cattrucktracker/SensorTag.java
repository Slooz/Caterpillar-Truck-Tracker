/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.UUID;

class SensorTag {
    SensorTag(Context context) {
        BluetoothManager bluetoothManager
                = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice("B0:B4:48:C0:4C:85");
        bluetoothDevice.connectGatt(context, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                gatt.discoverServices();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                UUID serviceUuid = UUID.fromString("f000aa80-0451-4000-b000-000000000000");
                BluetoothGattService bluetoothGattService = gatt.getService(serviceUuid);
                UUID periodUuid = UUID.fromString("f000aa83-0451-4000-b000-000000000000");
                BluetoothGattCharacteristic periodCharacteristic
                        = bluetoothGattService.getCharacteristic(periodUuid);
                periodCharacteristic.setValue(new byte[]{0x0A});
                gatt.writeCharacteristic(periodCharacteristic);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic,
                                              int status) {
            }
        });
    }
}
