package com.sid.android.roommanager.common;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FirebaseReadFromAndWriteToDevice implements ReadFromWriteToDevice {
    private BlockingQueue<AsynMessage> msgQueue;
    FirebaseDatabase database;
    private DatabaseReference myDevice;
    private DatabaseReference showRef;

    private ValueEventListener valueEventListener;

    public FirebaseReadFromAndWriteToDevice(FirebaseDatabase database) {
        msgQueue = new LinkedBlockingQueue<>();
        this.database = database;

        myDevice = database.getReference("device/device_1/");
        showRef = myDevice.child("show");


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Show value = dataSnapshot.getValue(Show.class);
                Logger.debug("Firebase node show: " + value);
                msgQueue.offer(new AsynMessage(value.res, Const.EVENT_MESSAGE_READ_CODE));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Logger.error("Firebase error: ", error.toException());
            }
        };
        showRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void write(String command) {
        CmdSync cmdSync = new CmdSync(command, ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond());
        myDevice.child("cmdSyn").setValue(cmdSync);
    }

    @Override
    public BlockingQueue<AsynMessage> getMsgQueue() {
        return msgQueue;
    }

    @Override
    public void close() {
        showRef.removeEventListener(valueEventListener);
    }

}
