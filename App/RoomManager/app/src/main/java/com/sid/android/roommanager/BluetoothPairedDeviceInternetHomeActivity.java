package com.sid.android.roommanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;

public class BluetoothPairedDeviceInternetHomeActivity extends AppCompatActivity {

    Button btRemoteCode;
    Button timeSyncButton;
    Button btCamera;
    Button btAlarm;
    Button btColorPicker;
    Button btNewScan;
    Button btLEDAnimation;
    //Button btVoiceCommand;
    Button btWifiSettings;
    Button scheduleOnOffActivityButton;
    Button deviceRestart;
    private AsyncMessageHandler asyncMessageHandler;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_device_home_activity);


        btRemoteCode = (Button) findViewById(R.id.bt_basic);
        timeSyncButton = (Button) findViewById(R.id.bt_time_sync);
        btCamera = (Button) findViewById(R.id.bt_cam);
        btAlarm = (Button) findViewById(R.id.bt_set_alarm);
        btColorPicker = (Button) findViewById(R.id.bt_col_ch);
        btNewScan = (Button) findViewById(R.id.bt_new_scan);
        btLEDAnimation = (Button) findViewById(R.id.bt_ran_col);
        //btVoiceCommand = (Button) findViewById(R.id.bt_voi);
        btWifiSettings = (Button) findViewById(R.id.bt_wifi_settings);
        scheduleOnOffActivityButton = findViewById(R.id.bt_schedule_on_off);
        deviceRestart = findViewById(R.id.bt_device_restart);

        btRemoteCode.setTransformationMethod(null);
        timeSyncButton.setTransformationMethod(null);
        btCamera.setTransformationMethod(null);
        btAlarm.setTransformationMethod(null);
        btColorPicker.setTransformationMethod(null);
        btNewScan.setTransformationMethod(null);
        btLEDAnimation.setTransformationMethod(null);
        //btVoiceCommand.setTransformationMethod(null);
        btWifiSettings.setTransformationMethod(null);
        scheduleOnOffActivityButton.setTransformationMethod(null);
        deviceRestart.setTransformationMethod(null);

        //Issue if device is not connected and need to select new device
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        final String macId = getIntent().getExtras().getString("MAC");
        //Toast.makeText(getBaseContext(),macId,Toast.LENGTH_SHORT).show();

        BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice();

        btRemoteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), BasicRemoteCommandActivity.class);
                i.putExtra("MAC", macId);
                startActivity(i);
            }
        });

        timeSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), DeviceRTCTimeSyncActivity.class);
                i.putExtra("MAC", macId);
                startActivity(i);
            }
        });
        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), FindRGBLedColorUsingCameraActivity.class);
                i.putExtra("MAC", macId);
                startActivity(i);
            }
        });
        btAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), SettingAlarmActivity.class);
                i.putExtra("MAC", macId);
                startActivity(i);
            }
        });

        btColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), ColorPickerActivity.class);
                i.putExtra("MAC", macId);
                startActivity(i);
            }
        });

        btNewScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("RoomManager", MODE_PRIVATE).edit();
                editor.putString("mac", "");
                editor.commit();

                Intent i = new Intent(getBaseContext(), BluetoothScanAndPairActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        btLEDAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), RGBAnimationActivity.class);
                i.putExtra("MAC", macId);
                startActivity(i);
            }
        });

       /* btVoiceCommand.setOnClickListener(view -> {
            Intent i = new Intent(getBaseContext(), BtVoice.class);
            i.putExtra("MAC", macId);
            startActivity(i);
        });*/

        btWifiSettings.setOnClickListener(view -> {
            Intent i = new Intent(getBaseContext(), WifiSettingsActivity.class);
            startActivity(i);
        });

        scheduleOnOffActivityButton.setOnClickListener(view -> {
            Intent i = new Intent(getBaseContext(), ScheduleOnOffActivity.class);
            startActivity(i);
        });

        deviceRestart.setOnClickListener(view -> {
            Intent i = new Intent(getBaseContext(), DeviceRestartActivity.class);
            startActivity(i);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        asyncMessageHandler.shutdown();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();
    }
}
