package com.sid.android.roommanager.common;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class Util {
    public static String getRGBColorCommand(int rgbColor) {
        return getRGBColorCommand(Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor));
    }

    public static String getRGBColorCommand(int red, int green, int blue) {
        return String.format("1:%d-%d-%d;", red, green, blue);
    }

    public static String getPreDefRGBCommand(int type, long intervalTime, long transitionTime) {
        return String.format("2:%d-%d-%d;", type, intervalTime, transitionTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getSetAlarmCommand(long alarmTime) {
        return String.format("3:%d;", alarmTime);
    }

    public static String getResetAlarmCommand() {
        return String.format("4:;");
    }

    public static String getCurrentAlarmConfig() {
        return String.format("5:;");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getTimeSyncTimeCommand() {
        long curEpochSecondTimeStamp = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
        return String.format("6:%s;", curEpochSecondTimeStamp);
    }

    public static String getDeviceWifiSSIDSetCommand(String ssid) {
        return String.format("7:%s;", ssid);
    }

    public static String getDeviceWifiPasswordSetCommand(String password) {
        return String.format("8:%s;", password);
    }

    public static String getDeviceRebootCommand() {
        return String.format("9:;");
    }

    public static String getDeviceCurrentWifiSettingsCommand() {
        return String.format("10:;");
    }

    public static String getGetDeviceCurrentWifiStatusCommand() {
        return String.format("11:;");
    }

    public static String getSetScheduleOnOffCommand(String relay, String targetState, long scheduleAt) {
        return String.format("12:%s-%d-%d;", relay, scheduleAt, targetState.equalsIgnoreCase("Off") ? 0 : (targetState.equalsIgnoreCase("On") ? 1 : 2));
    }

    public static String getResetScheduleOnOffCommand(String relay) {
        return String.format("13:%s;", relay);
    }

    public static String getCurrentScheduleCommand(String relay) {
        return String.format("14:%s;", relay);
    }
}
