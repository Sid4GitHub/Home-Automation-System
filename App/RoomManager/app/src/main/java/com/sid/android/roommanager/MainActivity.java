package com.sid.android.roommanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    Intent btScanBtn;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btScanBtn = new Intent(MainActivity.this, BluetoothScanAndPairActivity.class);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] listOfRequiredPermission = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA, android.Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
            if (Arrays.stream(listOfRequiredPermission).anyMatch(permission -> checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(listOfRequiredPermission, 0);
            } else {
                init();
            }
        }

    }

    public void init() {
        if (btAdapter.isEnabled()) {
            btScanBtn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(btScanBtn);
        } else {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent authActivityResult) {
        super.onActivityResult(requestCode, resultCode, authActivityResult);
        if (resultCode == RESULT_OK) {
            btScanBtn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(btScanBtn);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(this, Arrays.toString(permissions), Toast.LENGTH_SHORT).show();
        if (requestCode == 0) {
            if (!Arrays.stream(grantResults).anyMatch(r -> r == PackageManager.PERMISSION_DENIED)) {
                init();
            } else {
                Toast.makeText(getBaseContext(), "Permission error, Try later", Toast.LENGTH_LONG).show();
            }
        }
    }
}