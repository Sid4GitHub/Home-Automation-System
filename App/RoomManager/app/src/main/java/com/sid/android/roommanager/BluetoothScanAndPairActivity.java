package com.sid.android.roommanager;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Const;
import com.sid.android.roommanager.common.Logger;

import java.util.ArrayList;
import java.util.List;

public class BluetoothScanAndPairActivity extends AppCompatActivity {

    Button scanButton;
    Button selectInternetButton;
    ListView listView;
    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver broadcastReceiver;
    ArrayAdapter<String> listAdapter;
    List<BluetoothDevice> availableDevices;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bluetooth_scan);

        Log.i(Const.LOG_TAG, "\n--LOG BluetoothScanAndPairActivity--\n");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }

        SharedPreferences prefs = getSharedPreferences("RoomManager", MODE_PRIVATE);

        String preSelectedDevice = prefs.getString("mac", "");
        if (!preSelectedDevice.equals("")) {
            Intent intent = new Intent(getBaseContext(), BluetoothPairedDeviceInternetHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("MAC", preSelectedDevice);
            if(!preSelectedDevice.equals(Const.INTERNET_MODE_MAC)) {
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.setBluetoothDevice(bluetoothAdapter.getRemoteDevice(preSelectedDevice));
            }else {
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.setBluetoothDevice(null);
            }
            startActivity(intent);
        } else {

            scanButton = findViewById(R.id.my_bT_scan);
            scanButton.setTransformationMethod(null);

            selectInternetButton = findViewById(R.id.select_Internet_button);
            selectInternetButton.setTransformationMethod(null);

            listView = (ListView) findViewById(R.id.my_listViewscan);
            selectInternetButton.setOnClickListener(view -> {
                Intent intent = new Intent(getBaseContext(), BluetoothPairedDeviceInternetHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("mac", "Internet");
                editor.commit();
                intent.putExtra("MAC", Const.INTERNET_MODE_MAC);
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.setBluetoothDevice(null);
                startActivity(intent);
            });
            broadcastReceiver = new BroadcastReceiver() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onReceive(Context context, Intent intent) {

                    try {
                        String action = intent.getAction();

                        //Toast.makeText(getBaseContext(), "new br: " + action, Toast.LENGTH_LONG).show();
                        Logger.debug("BroadcastReceiver" + intent);

                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            //Toast.makeText(getBaseContext(), "Dev: " + device.getName(), Toast.LENGTH_LONG).show();

                            Logger.debug(String.format("BroadcastReceiver -> onReceive -> device: [%s, %s]", device.getName(), device.getAddress()));
                            //Logger.debug(String.format("BroadcastReceiver -> onReceive -> devices: " + devices.stream().map(o -> o.getAddress()).collect(Collectors.toList())));

                            if (availableDevices.stream().noneMatch(o -> o.getAddress().equals(device.getAddress()))) {
                                availableDevices.add(device);
                                if (bluetoothAdapter.getBondedDevices().stream().anyMatch(pairedDev -> pairedDev.getAddress().equals(device.getAddress()))) {
                                    listAdapter.add(device.getName() + " (Paired) \n" + device.getAddress());
                                } else {
                                    listAdapter.add(device.getName() + "\n" + device.getAddress());
                                }
                            }

                        } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                            newScan();
                        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action) && bluetoothAdapter.getState() == bluetoothAdapter.STATE_OFF) {
                            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                        }
                    } catch (Exception e) {
                        Logger.error(e.getMessage(), e);
                    }

                }
            };

            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));

            newScan();

            scanButton.setOnClickListener(view -> newScan());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("mac", availableDevices.get(i).getAddress());
                    editor.commit();

                    if (!listAdapter.getItem(i).contains("Paired")) {
                        BluetoothDevice selectedDevice = availableDevices.get(i);
                        pairDevice(selectedDevice);
                    } else {
                        BluetoothDevice selectedDevice = availableDevices.get(i);
                        Intent intent = new Intent(getBaseContext(), BluetoothPairedDeviceInternetHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("MAC", selectedDevice.getAddress());
                        BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.setBluetoothDevice(bluetoothAdapter.getRemoteDevice(selectedDevice.getAddress()));
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void newScan() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Toast.makeText(getBaseContext(), "Old Scan Cancelled", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getBaseContext(), "New Scan Started", Toast.LENGTH_SHORT).show();

        listAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, 0) {

            @SuppressLint("ResourceAsColor")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                if ((getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    textView.setTextColor(Color.WHITE);
                } else {
                    textView.setTextColor(Color.BLACK);
                }

                return view;
            }
        };
        listView.setAdapter(listAdapter);
        availableDevices = new ArrayList<>();
        bluetoothAdapter.startDiscovery();
    }


    @SuppressLint("MissingPermission")
    private void pairDevice(BluetoothDevice device) {
        try {
            device.createBond();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
            Toast.makeText(getBaseContext(), "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }
/*
    @Override
    protected void onPause() {
        super.onPause();
        try {
            btAdapter.cancelDiscovery();
            unregisterReceiver(receiver);
        }
        catch (Exception e){}
    }


    @Override
    public void onResume() {
        super.onResume();
        try {

            init();

            registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
            registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));


            //Toast.makeText(getBaseContext(),"Registration",Toast.LENGTH_SHORT ).show();

        }
        catch (Exception e){}
    }*/
}





