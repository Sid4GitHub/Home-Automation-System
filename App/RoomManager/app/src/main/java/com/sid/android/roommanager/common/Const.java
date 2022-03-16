package com.sid.android.roommanager.common;

public interface Const {
    String LOG_TAG = "Room-Manager: ";
    int EVENT_MESSAGE_READ_CODE = 1;
    int EVENT_SET_UI_FLAG_CODE = 2;
    int EVENT_CLEAR_UI_FLAG_CODE = 3;
    int EVENT_SHOW_SHORT_MESSAGE_CODE = 4;
    int EVENT_SHOW_LONG_MESSAGE_CODE = 5;
    int MESSAGE_QUEUE_MONITOR_INTERVAL = 5;
    String SUCCESS_RESPONSE_CODE = "SUCCESS";
    String FAILURE_RESPONSE_CODE = "FAILURE";
    String SHARED_ALARM_DETAILS_RESPONSE_CODE = "RT0:";
    String SHARED_RELAY_SCHEDULE_RESPONSE_CODE = "RT1:";
    String INTERNET_MODE_MAC = "Internet";
}
