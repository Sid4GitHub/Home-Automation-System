package com.sid.android.roommanager.common;

import android.util.Log;

public class Logger {
    static public void debug(String msg) {
        Log.d(Const.LOG_TAG, msg);
    }

    static public void info(String msg) {
        Log.i(Const.LOG_TAG, msg);
    }

    static public void error(String msg) {
        Log.e(Const.LOG_TAG, msg);
    }

    static public void error(String msg, Throwable tr) {
        Log.e(Const.LOG_TAG, msg, tr);
    }
}
