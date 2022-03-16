package com.sid.android.roommanager;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Logger;
import com.sid.android.roommanager.common.Util;

public class WifiSettingsActivity extends AppCompatActivity {

    Button setWifiSettingButton;
    Button getWifiSettingButton;
    Button wifiStatusButton;
    EditText editTextWifiSSID;
    EditText editTextWifiPassword;

    private AsyncMessageHandler asyncMessageHandler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_wifi_settings);

        this.setWifiSettingButton = findViewById(R.id.bt_set_wifi_settings);
        this.editTextWifiSSID = findViewById(R.id.editText_ssid);
        this.editTextWifiPassword = findViewById(R.id.editText_password);
        this.getWifiSettingButton = findViewById(R.id.bt_get_wifi_settings);
        this.wifiStatusButton = findViewById(R.id.bt_get_wifi_status);

        setWifiSettingButton.setOnClickListener(view -> {
            try {
                String wifiSSID = editTextWifiSSID.getText().toString();
                String wifiPassword = editTextWifiPassword.getText().toString();

                Logger.debug(String.format("wifiSSID: %s : wifiPassword: %s", wifiSSID, wifiPassword));

                String ssidSetCommand = Util.getDeviceWifiSSIDSetCommand(wifiSSID);
                String wifiPasswordSetCommand = Util.getDeviceWifiPasswordSetCommand(wifiPassword);
                String restartCommand = Util.getDeviceRebootCommand();
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(ssidSetCommand);
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(wifiPasswordSetCommand);
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(restartCommand);
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
                Toast.makeText(getBaseContext(), "Exc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        getWifiSettingButton.setOnClickListener(view -> {
            try {
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getDeviceCurrentWifiSettingsCommand());
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
                Toast.makeText(getBaseContext(), "Exc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        wifiStatusButton.setOnClickListener(view -> {
            try {
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getGetDeviceCurrentWifiStatusCommand());
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
                Toast.makeText(getBaseContext(), "Exc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncMessageHandler.shutdown();
    }
}
