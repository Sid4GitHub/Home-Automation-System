package com.sid.android.roommanager;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Util;


public class DeviceRestartActivity extends AppCompatActivity {
    private AsyncMessageHandler asyncMessageHandler;
    Button restartButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_device_restart);
        this.restartButton = findViewById(R.id.bt_restart);

        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();
        restartButton.setOnClickListener(vew ->
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getDeviceRebootCommand())
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncMessageHandler.shutdown();
    }
}
