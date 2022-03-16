package com.sid.android.roommanager.common;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public class BluetoothReadFromAndWriteToDevice implements ReadFromWriteToDevice{

    private final BluetoothSocket bluetoothSocket;
    private final OutputStream outStream;
    private final InputStream inputStream;
    private ScheduledExecutorService scheduledExecutorService;
    private Future<?> openSocket;
    private BluetoothAdapter bluetoothAdapter;
    private UUID uuid;

    BlockingQueue<AsynMessage> msgQueue;

    @Override
    public BlockingQueue<AsynMessage> getMsgQueue() {
        return msgQueue;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public BluetoothReadFromAndWriteToDevice(BluetoothDevice device) {
        this(BluetoothAdapter.getDefaultAdapter(), device , UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"), new LinkedBlockingQueue<>());
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public BluetoothReadFromAndWriteToDevice(BluetoothAdapter bluetoothAdapter, BluetoothDevice device, UUID uuid, BlockingQueue<AsynMessage> msgQueue) {
        this.bluetoothAdapter = bluetoothAdapter;
        BluetoothSocket temBluetoothSocket = null;
        OutputStream temOutStream = null;
        InputStream temInStream = null;
        this.uuid = uuid;
        this.msgQueue = msgQueue;
        try {
            temBluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            temOutStream = temBluetoothSocket.getOutputStream();
            temInStream = temBluetoothSocket.getInputStream();
            Logger.debug("bluetoothSocket opened");
        } catch (IOException e) {
            Logger.error(e.getMessage(), e);
        }
        this.bluetoothSocket = temBluetoothSocket;
        this.outStream = temOutStream;
        this.inputStream = temInStream;


        openSocket = Executors.newSingleThreadExecutor().submit(() -> {
            bluetoothAdapter.cancelDiscovery();
            Logger.debug("Connect run method is called");
            try {
                bluetoothSocket.connect();
                Logger.debug("Connection is made");
                msgQueue.add(new AsynMessage(String.valueOf(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE), Const.EVENT_CLEAR_UI_FLAG_CODE));
                msgQueue.add(new AsynMessage("CONNECTED", Const.EVENT_SHOW_SHORT_MESSAGE_CODE));
            } catch (IOException connectException) {
                Logger.error("Unable to connect: " + connectException.getMessage(), connectException);
                //msgQueue.add(new AsynMessage(String.valueOf(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE), Const.EVENT_SET_UI_FLAG_CODE));
                try {
                    bluetoothSocket.close();
                } catch (Exception closeException) {
                    Logger.error(closeException.getMessage(), closeException);
                }
            }
        });


        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                while (openSocket == null) ;
                openSocket.get();
            } catch (ExecutionException | InterruptedException e) {
                Logger.error(e.getMessage(), e);
            }
            try {
                while (inputStream.available() > 0) {

                    StringBuffer received = new StringBuffer("");
                    int tmp = 0;
                    while (tmp != ';') {
                        if (inputStream.available() > 0) {
                            tmp = inputStream.read();
                            received.append((char) tmp);
                        }
                        Thread.sleep(1);
                    }
                    while (inputStream.available() > 0) {
                        Thread.sleep(1);
                        inputStream.read();
                    }
                    String receivedStr = received.toString();
                    Logger.debug(receivedStr);
                    msgQueue.offer(new AsynMessage(receivedStr, Const.EVENT_MESSAGE_READ_CODE));

                }
            } catch (Exception e) {
                //Failed to distinguish if the connection with remote device failed;
                //with this managed to identify unwanted logs
                if (!e.getMessage().contains("socket closed")) Logger.error(e.getMessage(), e);
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    @SneakyThrows
    public void write(String command) {
        try {
            while (openSocket == null) ;
            openSocket.get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.error(e.getMessage(), e);
        }
        Logger.debug("Command sent over bluetooth: " + command);
        if (bluetoothSocket != null) {
            try {
                outStream.write(command.getBytes(StandardCharsets.UTF_8));
                outStream.flush();
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.close();
                msgQueue.add(new AsynMessage("Device Disconnected", Const.EVENT_SHOW_SHORT_MESSAGE_CODE));
            }
        } else {
            Logger.debug("Writing to Null bluetoothSocket");
        }
    }

    @Override
    public void close() {
        try {
            bluetoothSocket.close();
            inputStream.close();
            outStream.close();
            scheduledExecutorService.shutdown();
            Logger.debug("bluetoothSocket closed");
        } catch (IOException e) {
            Logger.error(e.getMessage(), e);
        }
    }
}
