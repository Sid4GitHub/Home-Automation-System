package com.sid.android.roommanager.common;

public class CmdSync {
    String cmd;
    long ts;

    public CmdSync(String cmd, long ts) {
        this.cmd = cmd;
        this.ts = ts;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
