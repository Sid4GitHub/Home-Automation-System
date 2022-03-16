package com.sid.android.roommanager;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Logger;
import com.sid.android.roommanager.common.Util;

public class RGBAnimationActivity extends AppCompatActivity {

    Button type1Button;
    Button type2Button;
    EditText inputIntervalTimeTextBox;
    EditText inputTransitionTimeTextBox;

    private AsyncMessageHandler asyncMessageHandler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_led_animation);

        inputIntervalTimeTextBox = findViewById(R.id.editText_interval_milli);
        inputTransitionTimeTextBox = findViewById(R.id.editText_transition_milli);

        type1Button = findViewById(R.id.bt_led1);
        type2Button = findViewById(R.id.bt_led2);

        type1Button.setTransformationMethod(null);
        type2Button.setTransformationMethod(null);
        inputTransitionTimeTextBox.setTransformationMethod(null);

        type1Button.setOnClickListener(view -> {
            try {
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getPreDefRGBCommand(1, Long.parseLong(inputIntervalTimeTextBox.getText().toString()), 0));
            } catch (NumberFormatException e) {
                Logger.error(e.getMessage(), e);
            }
        });

        type2Button.setOnClickListener(view -> {
            try {
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getPreDefRGBCommand(2, Long.parseLong(inputIntervalTimeTextBox.getText().toString()), Long.parseLong(inputTransitionTimeTextBox.getText().toString())));
            } catch (NumberFormatException e) {
                Logger.error(e.getMessage(), e);
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
