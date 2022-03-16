package com.sid.android.roommanager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TimePicker;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class SettingAlarmActivity extends AppCompatActivity {

    TimePicker timePickerForAlarm;
    Button setAlarmButton;
    Button resetAlarmButton;
    Button findCurrentAlarm;

    private AsyncMessageHandler asyncMessageHandler;

    @SuppressLint({"NewApi", "LocalSuppress"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_alarm);

        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/

        timePickerForAlarm = (TimePicker) findViewById(R.id.timePickerForAlarm);
        timePickerForAlarm.setIs24HourView(false);

        setAlarmButton = (Button) findViewById(R.id.bt_set_alarm);
        resetAlarmButton = (Button) findViewById(R.id.bt_reset_alarm);
        findCurrentAlarm = (Button) findViewById(R.id.bt_find_current_alarm);

        setAlarmButton.setTransformationMethod(null);
        resetAlarmButton.setTransformationMethod(null);
        findCurrentAlarm.setTransformationMethod(null);

        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();

        setAlarmButton.setOnClickListener(view -> {
            long currentSystemEpochTimeInSecond = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.of("Z")).toEpochSecond(ZoneOffset.of("Z"));
            long timePickerTimeEpochInSecond = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(), timePickerForAlarm.getHour(), timePickerForAlarm.getMinute(), 0).toEpochSecond(OffsetDateTime.now().getOffset());

            if (timePickerTimeEpochInSecond < currentSystemEpochTimeInSecond) {
                timePickerTimeEpochInSecond = LocalDateTime.of(LocalDateTime.now().plusDays(1).getYear(), LocalDateTime.now().plusDays(1).getMonthValue(), LocalDateTime.now().plusDays(1).getDayOfMonth(), timePickerForAlarm.getHour(), timePickerForAlarm.getMinute(), 0).toEpochSecond(OffsetDateTime.now().getOffset());
            }
            BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getSetAlarmCommand(timePickerTimeEpochInSecond));
        });

        resetAlarmButton.setOnClickListener(view -> {
            BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getResetAlarmCommand());
        });

        findCurrentAlarm.setOnClickListener(view -> {
            BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getCurrentAlarmConfig());
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncMessageHandler.shutdown();
    }
}
