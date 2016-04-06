/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

class SensorTag {
    SensorTag(BluetoothManager bluetoothManager) {
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    }
}
