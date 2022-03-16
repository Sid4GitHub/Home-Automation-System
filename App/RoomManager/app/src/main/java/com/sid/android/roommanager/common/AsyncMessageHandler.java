package com.sid.android.roommanager.common;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncMessageHandler {
    private ScheduledExecutorService scheduledExecutorService;
    private Activity activity;

    public AsyncMessageHandler(Activity activity) {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ScheduledExecutorService startHandler() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {

                AsynMessage msg = BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().getMsgQueue().take();
                switch (msg.getCode()) {
                    case Const.EVENT_SHOW_SHORT_MESSAGE_CODE:
                        activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), msg.getMsg(), Toast.LENGTH_SHORT).show());
                        break;
                    case Const.EVENT_SHOW_LONG_MESSAGE_CODE:
                        activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), msg.getMsg(), Toast.LENGTH_LONG).show());
                        break;
                    case Const.EVENT_SET_UI_FLAG_CODE:
                        activity.runOnUiThread(() -> activity.getWindow().addFlags(Integer.parseInt(msg.getMsg())));
                        break;
                    case Const.EVENT_CLEAR_UI_FLAG_CODE:
                        activity.runOnUiThread(() -> this.activity.getWindow().clearFlags(Integer.parseInt(msg.getMsg())));
                        break;
                    case Const.EVENT_MESSAGE_READ_CODE:
                        Logger.info("Message Received: " + msg.getMsg());
                        String[] msgs = msg.getMsg().split(";");
                        for (String command : msgs) {
                            if (Const.SUCCESS_RESPONSE_CODE.compareToIgnoreCase(command) == 0) {
                                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), "Command Succeed", Toast.LENGTH_SHORT).show());
                            } else if (Const.FAILURE_RESPONSE_CODE.compareToIgnoreCase(command) == 0) {
                                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), "Command Failed", Toast.LENGTH_SHORT).show());
                            } else if (command.startsWith(Const.SHARED_ALARM_DETAILS_RESPONSE_CODE)) {
                                String formattedAlarmDetails = LocalDateTime.ofEpochSecond(Long.parseLong(command.substring(command.indexOf(":") + 1)), 0, OffsetDateTime.now().getOffset()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy h:m a"));
                                Logger.info(formattedAlarmDetails);
                                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), "At: " + formattedAlarmDetails, Toast.LENGTH_LONG).show());
                            } else if (command.startsWith(Const.SHARED_RELAY_SCHEDULE_RESPONSE_CODE)) {
                                String trimmedCommand = command.substring(command.indexOf(":") + 1);
                                Logger.info("Trimmed Command " + trimmedCommand);
                                Matcher matcher = Pattern.compile("(.*), At: (.*), New State: (.*)").matcher(trimmedCommand);
                                matcher.find();
                                String formattedAlarmDetails = LocalDateTime.ofEpochSecond(Long.parseLong(matcher.group(2)), 0, OffsetDateTime.now().getOffset()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy h:m a"));
                                String displayCommand = matcher.replaceFirst("$1, At: " + formattedAlarmDetails + ", New State: $3");
                                Logger.info("displayCommand: " + displayCommand);
                                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), displayCommand, Toast.LENGTH_LONG).show());
                            } else {
                                activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), command, Toast.LENGTH_LONG).show());
                            }
                        }
                        break;
                    default:
                        Logger.debug("Unhandled message code: " + msg);
                }

            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            }

        }, 0, Const.MESSAGE_QUEUE_MONITOR_INTERVAL, TimeUnit.MILLISECONDS);

        return scheduledExecutorService;
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
    }
}
