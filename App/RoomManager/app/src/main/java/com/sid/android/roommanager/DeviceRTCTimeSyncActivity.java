package com.sid.android.roommanager;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Util;


public class DeviceRTCTimeSyncActivity extends AppCompatActivity {
    private AsyncMessageHandler asyncMessageHandler;
    Button syncTimeButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_bt_time_sync);
        this.syncTimeButton = findViewById(R.id.bt_time_sync);

        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();
        syncTimeButton.setOnClickListener(vew ->
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getTimeSyncTimeCommand())
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncMessageHandler.shutdown();
    }
}
