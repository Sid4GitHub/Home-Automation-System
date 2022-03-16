package com.sid.android.roommanager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.IntStream;

public class ScheduleOnOffActivity extends AppCompatActivity {
    private AsyncMessageHandler asyncMessageHandler;

    Button scheduleButton;
    Button resetScheduleButton;
    Button getCurrentSchedule;

    Spinner relaySelectionSpinner;
    Spinner relayState;

    TimePicker timePickerForScheduleOnOff;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_on_off);
        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();

        scheduleButton = findViewById(R.id.bt_make_schedule);
        resetScheduleButton = findViewById(R.id.bt_reset_schedule);
        getCurrentSchedule = findViewById(R.id.bt_get_current_schedule);
        timePickerForScheduleOnOff = findViewById(R.id.timePickerForScheduleOnOff);
        timePickerForScheduleOnOff.setIs24HourView(false);

        relaySelectionSpinner = findViewById(R.id.relaySelectSpinner);
        ArrayAdapter arrayAdapterForRelays = new ArrayAdapter(this, android.R.layout.simple_spinner_item, IntStream.rangeClosed(1, 8).mapToObj(i -> String.valueOf(i)).toArray(String[]::new));
        arrayAdapterForRelays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relaySelectionSpinner.setAdapter(arrayAdapterForRelays);

        relayState = findViewById(R.id.relayState);
        ArrayAdapter arrayAdapterForRelayState = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new String[]{"On", "Off", "Toggle"});
        arrayAdapterForRelayState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relayState.setAdapter(arrayAdapterForRelayState);

        scheduleButton.setOnClickListener(view -> {
            String selectedRelay = relaySelectionSpinner.getSelectedItem().toString();
            String targetState = relayState.getSelectedItem().toString();
            long currentSystemEpochTimeInSecond = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.of("Z")).toEpochSecond(ZoneOffset.of("Z"));
            long timePickerTimeEpochInSecond = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue(), LocalDateTime.now().getDayOfMonth(), timePickerForScheduleOnOff.getHour(), timePickerForScheduleOnOff.getMinute(), 0).toEpochSecond(OffsetDateTime.now().getOffset());

            if (timePickerTimeEpochInSecond < currentSystemEpochTimeInSecond) {
                timePickerTimeEpochInSecond = LocalDateTime.of(LocalDateTime.now().plusDays(1).getYear(), LocalDateTime.now().plusDays(1).getMonthValue(), LocalDateTime.now().plusDays(1).getDayOfMonth(), timePickerForScheduleOnOff.getHour(), timePickerForScheduleOnOff.getMinute(), 0).toEpochSecond(OffsetDateTime.now().getOffset());
            }
            String cmd = Util.getSetScheduleOnOffCommand(selectedRelay, targetState, timePickerTimeEpochInSecond);

            BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(cmd);
        });

        resetScheduleButton.setOnClickListener(view -> {
            String selectedRelay = relaySelectionSpinner.getSelectedItem().toString();
            BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getResetScheduleOnOffCommand(selectedRelay));
        });

        getCurrentSchedule.setOnClickListener(view -> {
            String selectedRelay = relaySelectionSpinner.getSelectedItem().toString();
            BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(Util.getCurrentScheduleCommand(selectedRelay));
        });

    }
}
