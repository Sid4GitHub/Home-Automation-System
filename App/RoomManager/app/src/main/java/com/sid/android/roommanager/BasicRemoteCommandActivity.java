package com.sid.android.roommanager;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BasicRemoteCommandActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> listAdapter;

    List<Pair<String, String>> remoteKeysAndCommands = new ArrayList<>();

    private AsyncMessageHandler asyncMessageHandler;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_bt_basic);

        listView = findViewById(R.id.listview_cmd);

        this.populateListView();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            try {
                Logger.debug(new String((remoteKeysAndCommands.get(i).second + ";").getBytes(), StandardCharsets.UTF_8));
                String command = remoteKeysAndCommands.get(i).second + ";";
                BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
                Toast.makeText(getBaseContext(), "Exc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void populateListView() {
        /*
        power: ed127f80 :: 3977412480
        mode: e51a7f80 :: 3843719040
        mute: e11e7f80 :: 3776872320
        play: fe017f80 :: 4261511040
        pre: fd027f80 :: 4244799360
        next: fc037f80 :: 4228087680
        eq: fb047f80 :: 4211376000
        vol+: f9067f80 :: 4177952640
        vol-: fa057f80 :: 4194664320
        0: f8077f80 :: 4161240960
        rpt: f7087f80 :: 4144529280
        sd: f6097f80 :: 4127817600
        1: f50a7f80 :: 4111105920
        2: e41b7f80 :: 3827007360
        3: e01f7f80 :: 3760160640
        4: f30c7f80 :: 4077682560
        5: f20d7f80 :: 4060970880
        6: f10e7f80 :: 4044259200
        7: ff007f80 :: 4278222720
        8: f00f7f80 :: 4027547520
        9: e6197f80 :: 3860430720
        */

        remoteKeysAndCommands.add(new Pair<>("power", "ED127F80"));
        remoteKeysAndCommands.add(new Pair<>("mode", "E51A7F80"));
        remoteKeysAndCommands.add(new Pair<>("mute", "E11E7F80"));
        remoteKeysAndCommands.add(new Pair<>("play", "FE017F80"));
        remoteKeysAndCommands.add(new Pair<>("pre", "FD027F80"));
        remoteKeysAndCommands.add(new Pair<>("next", "FC037F80"));
        remoteKeysAndCommands.add(new Pair<>("eq", "FB047F80"));
        remoteKeysAndCommands.add(new Pair<>("vol+", "F9067F80"));
        remoteKeysAndCommands.add(new Pair<>("vol-", "FA057F80"));
        remoteKeysAndCommands.add(new Pair<>("0", "F8077F80"));
        remoteKeysAndCommands.add(new Pair<>("rpt", "F7087F80"));
        remoteKeysAndCommands.add(new Pair<>("sd", "F6097F80"));
        remoteKeysAndCommands.add(new Pair<>("1", "F50A7F80"));
        remoteKeysAndCommands.add(new Pair<>("2", "E41B7F80"));
        remoteKeysAndCommands.add(new Pair<>("3", "E01F7F80"));
        remoteKeysAndCommands.add(new Pair<>("4", "F30C7F80"));
        remoteKeysAndCommands.add(new Pair<>("5", "F20D7F80"));
        remoteKeysAndCommands.add(new Pair<>("6", "F10E7F80"));
        remoteKeysAndCommands.add(new Pair<>("7", "FF007F80"));
        remoteKeysAndCommands.add(new Pair<>("8", "F00F7F80"));
        remoteKeysAndCommands.add(new Pair<>("9", "E6197F80"));

        listAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, 0) {

            @SuppressLint("ResourceAsColor")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = view.findViewById(android.R.id.text1);
                if ((getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    textView.setTextColor(Color.WHITE);
                } else {
                    textView.setTextColor(Color.BLACK);
                }

                return view;
            }
        };
        remoteKeysAndCommands.stream().forEach(key -> listAdapter.add(key.first + " :  " + key.second));
        listView.setAdapter(listAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncMessageHandler.shutdown();
    }

}