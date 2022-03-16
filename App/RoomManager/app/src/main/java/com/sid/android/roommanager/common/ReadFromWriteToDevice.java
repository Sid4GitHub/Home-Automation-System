package com.sid.android.roommanager.common;

import java.util.concurrent.BlockingQueue;

public interface ReadFromWriteToDevice {
    void write(String command);
    BlockingQueue<AsynMessage> getMsgQueue();
    void close();
}
