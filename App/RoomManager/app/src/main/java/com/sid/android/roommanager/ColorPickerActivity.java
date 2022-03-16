package com.sid.android.roommanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Logger;
import com.sid.android.roommanager.common.Util;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ColorPickerActivity extends AppCompatActivity {

    private static int COLOR_PICKER_MONITOR_INTERVAL = 150;


    Button sendValuesFromTextBox;
    EditText redTextBok;
    EditText greenTextBox;
    EditText blueTextBox;

    ColorPicker picker;
    SVBar svBar;
    int previousColor;
    int currentColor;


    private AsyncMessageHandler asyncMessageHandler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.picker = findViewById(R.id.picker);
        this.picker.setShowOldCenterColor(false);
        this.svBar = findViewById(R.id.svbar);
        this.picker.addSVBar(svBar);

        this.sendValuesFromTextBox = (Button) findViewById(R.id.bt_led_manu);
        this.sendValuesFromTextBox.setTransformationMethod(null);

        this.redTextBok = findViewById(R.id.ev_r);
        this.greenTextBox = findViewById(R.id.ev_g);
        this.blueTextBox = findViewById(R.id.ev_b);

        redTextBok.setOnFocusChangeListener((view, b) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });


        SharedPreferences prefs = getSharedPreferences("RGB-Code", MODE_PRIVATE);
        try {
            int rgb = prefs.getInt("rgb", 0);
            int red = prefs.getInt("red", 0);
            int green = prefs.getInt("green", 0);
            int blue = prefs.getInt("blue", 0);
            picker.setColor(rgb);
            redTextBok.setText(String.valueOf(red));
            greenTextBox.setText(String.valueOf(green));
            blueTextBox.setText(String.valueOf(blue));
            picker.setOldCenterColor(rgb);
            Logger.debug(String.format("SharedPreferences: RGB: $d [$d, $d, $d]", rgb, red, green, blue));
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

        picker.setShowOldCenterColor(false);

        picker.setOnColorChangedListener(color -> {
            currentColor = color;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("rgb", color);
            editor.putInt("red", Color.red(color));
            editor.putInt("green", Color.green(color));
            editor.putInt("blue", Color.blue(color));
            editor.commit();
        });

        sendValuesFromTextBox.setOnClickListener(view -> {
            try {
                int red = Integer.parseInt(redTextBok.getText().toString());
                int green = Integer.parseInt(greenTextBox.getText().toString());
                int blue = Integer.parseInt(blueTextBox.getText().toString());

                String command = Util.getRGBColorCommand(red, green, blue);
                picker.setColor(Color.rgb(red, green, blue));
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);

            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            }
        });

        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (previousColor != currentColor) {
                Logger.debug(String.format("Color changed: prv= %d, new= %d", previousColor, currentColor));
                int red = Color.red(currentColor);
                int green = Color.green(currentColor);
                int blue = Color.blue(currentColor);

                String command = Util.getRGBColorCommand(red, green, blue);
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
                previousColor = currentColor;

                this.runOnUiThread(() -> {
                    redTextBok.setText(String.valueOf(red));
                    greenTextBox.setText(String.valueOf(green));
                    blueTextBox.setText(String.valueOf(blue));
                });
            }
        }, 1, COLOR_PICKER_MONITOR_INTERVAL, TimeUnit.MILLISECONDS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncMessageHandler.shutdown();
    }
}
