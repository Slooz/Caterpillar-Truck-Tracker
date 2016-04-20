/*
 * Copyright 2016 Nathan Clark, Krzysztof Czelusniak, Michael Holwey, Dakota Leonard
 */

package edu.bradley.cattrucktracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
                BluetoothGattService movementService = getMovementService(gatt);
                UUID periodUuid = getPeriodUuid();
                BluetoothGattCharacteristic periodCharacteristic
                        = movementService.getCharacteristic(periodUuid);
                periodCharacteristic.setValue(new byte[]{0x0A});
                gatt.writeCharacteristic(periodCharacteristic);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic,
                                              int status) {
                UUID characteristicWrittenTo = characteristic.getUuid();
                UUID periodUuid = getPeriodUuid();
                if (characteristicWrittenTo.equals(periodUuid)) {
                    BluetoothGattService movementService = getMovementService(gatt);
                    UUID dataUuid = UUID.fromString("f000aa81-0451-4000-b000-000000000000");
                    BluetoothGattCharacteristic dataCharacteristic
                            = movementService.getCharacteristic(dataUuid);
                    gatt.setCharacteristicNotification(dataCharacteristic, true);
                    UUID notificationUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
                    BluetoothGattDescriptor notificationDescriptor
                            = dataCharacteristic.getDescriptor(notificationUuid);
                    notificationDescriptor
                            .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(notificationDescriptor);
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                          int status) {
                BluetoothGattService movementService = getMovementService(gatt);
                UUID configurationUuid = UUID.fromString("f000aa82-0451-4000-b000-000000000000");
                BluetoothGattCharacteristic configurationCharacteristic
                        = movementService.getCharacteristic(configurationUuid);
                configurationCharacteristic.setValue(new byte[]{0x7F, 0x00});
                gatt.writeCharacteristic(configurationCharacteristic);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {
                byte[] movementData = characteristic.getValue();

                int gyroscopeRange = 250;
                byte gyroscopeXFirstByte = movementData[0];
                byte gyroscopeXSecondByte = movementData[1];
                double gyroscopeX = convertRawDatum
                        (gyroscopeXFirstByte, gyroscopeXSecondByte, gyroscopeRange);

                byte gyroscopeYFirstByte = movementData[2];
                byte gyroscopeYSecondByte = movementData[3];
                double gyroscopeY = convertRawDatum
                        (gyroscopeYFirstByte, gyroscopeYSecondByte, gyroscopeRange);

                byte gyroscopeZFirstByte = movementData[4];
                byte gyroscopeZSecondByte = movementData[5];
                double gyroscopeZ = convertRawDatum
                        (gyroscopeZFirstByte, gyroscopeZSecondByte, gyroscopeRange);
            }

            private BluetoothGattService getMovementService(BluetoothGatt bluetoothGatt) {
                UUID serviceUuid = UUID.fromString("f000aa80-0451-4000-b000-000000000000");
                return bluetoothGatt.getService(serviceUuid);
            }

            private UUID getPeriodUuid() {
                return UUID.fromString("f000aa83-0451-4000-b000-000000000000");
            }

            private double convertRawDatum(byte firstRawByte, byte secondRawByte, int range) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                        .put(firstRawByte).put(secondRawByte);
                short rawDatum = byteBuffer.getShort(0);

                int signedShortValueCount = Short.MAX_VALUE + 1;
                double rawDatumValueCount = signedShortValueCount / range;

                return rawDatum / rawDatumValueCount;
            }
        });
    }
}
