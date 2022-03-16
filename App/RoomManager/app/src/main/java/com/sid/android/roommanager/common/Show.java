package com.sid.android.roommanager.common;

import androidx.annotation.NonNull;

public class Show {
    String res;
    long ts;


    @Override
    public String toString() {
        return "Show{" +
                "res='" + res + '\'' +
                ", ts=" + ts +
                '}';
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
