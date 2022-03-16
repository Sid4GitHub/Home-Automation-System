package com.sid.android.roommanager.common;

import lombok.AllArgsConstructor;
import lombok.Data;


public class AsynMessage {
    String msg;
    int code;

    public AsynMessage(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
