package com.sid.android.roommanager.common;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BluetoothOrFirebaseReadFromAndWriteToDeviceInstance {

    private static BluetoothDevice bluetoothDevice = null;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static FirebaseReadFromAndWriteToDevice firebaseReadFromAndWriteToDevice = null;

    private static BluetoothReadFromAndWriteToDevice bluetoothReadFromAndWriteToDevice = null;

    public static BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public static void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.bluetoothDevice = bluetoothDevice;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ReadFromWriteToDevice getBluetoothOrFirebaseReadFromAndWriteToDevice() {
        ReadFromWriteToDevice readFromWriteToDevice = null;
        if (bluetoothDevice != null) {
            if (bluetoothReadFromAndWriteToDevice == null) {
                bluetoothReadFromAndWriteToDevice = new BluetoothReadFromAndWriteToDevice(bluetoothDevice);
            }
            Logger.info("Bluetooth Mode");
            readFromWriteToDevice = bluetoothReadFromAndWriteToDevice;
        } else {
            if (firebaseReadFromAndWriteToDevice == null) {
                firebaseReadFromAndWriteToDevice = new FirebaseReadFromAndWriteToDevice(database);
            }
            Logger.info("Internet Mode");
            readFromWriteToDevice = firebaseReadFromAndWriteToDevice;
        }
        return readFromWriteToDevice;
    }

    public static void close() {
        if (bluetoothReadFromAndWriteToDevice != null) {
            bluetoothReadFromAndWriteToDevice.close();
            bluetoothReadFromAndWriteToDevice = null;
        }

        if (firebaseReadFromAndWriteToDevice != null) {
            firebaseReadFromAndWriteToDevice.close();
            firebaseReadFromAndWriteToDevice = null;
        }
    }
}
