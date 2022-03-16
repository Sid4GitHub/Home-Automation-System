package com.sid.android.roommanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.widget.ImageView;
import android.widget.Toast;

import com.sid.android.roommanager.common.AsyncMessageHandler;
import com.sid.android.roommanager.common.BluetoothOrFirebaseReadFromAndWriteToDeviceInstance;
import com.sid.android.roommanager.common.Util;


public class FindRGBLedColorUsingCameraActivity extends AppCompatActivity {

    ImageView imageViewCamera;
    ImageView imageViewForDominantColor;
    ImageView imageViewForVibrantColor;
    ImageView imageViewForDarkVibrantColor;
    ImageView imageViewForLightVibrantColor;
    ImageView imageViewForMutedColor;
    ImageView imageViewForDarkMutedColor;
    ImageView imageViewForLightMutedColor;

    private AsyncMessageHandler asyncMessageHandler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_led_color_using_camera);

        imageViewCamera = (ImageView) findViewById(R.id.camImageView);
        imageViewForDominantColor = (ImageView) findViewById(R.id.imageViewForDominantColor);
        imageViewForVibrantColor = findViewById(R.id.imageViewForVibrantColor);
        imageViewForDarkVibrantColor = findViewById(R.id.imageViewForDarkVibrantColor);
        imageViewForLightVibrantColor = findViewById(R.id.imageViewForLightVibrantColor);
        imageViewForMutedColor = findViewById(R.id.imageViewForMutedColor);
        imageViewForDarkMutedColor = findViewById(R.id.imageViewForDarkMutedColor);
        imageViewForLightMutedColor = findViewById(R.id.imageViewForLightMutedColor);

        asyncMessageHandler = new AsyncMessageHandler(this);
        asyncMessageHandler.startHandler();

        imageViewCamera.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 0);
        });


        imageViewForDominantColor.setOnClickListener(view -> {
                    int color = ((ColorDrawable) view.getBackground()).getColor();
                    Toast.makeText(this.getApplicationContext(), String.format("RGB(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color)), Toast.LENGTH_LONG).show();
                    String command = Util.getRGBColorCommand(Color.red(color), Color.green(color), Color.blue(color));
                    BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
                }
        );

        imageViewForVibrantColor.setOnClickListener(view -> {
                    int color = ((ColorDrawable) view.getBackground()).getColor();
            Toast.makeText(this.getApplicationContext(), String.format("RGB(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color)), Toast.LENGTH_LONG).show();
                    String command = Util.getRGBColorCommand(Color.red(color), Color.green(color), Color.blue(color));
                    BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
                }
        );

        imageViewForDarkVibrantColor.setOnClickListener(view -> {
                    int color = ((ColorDrawable) view.getBackground()).getColor();
            Toast.makeText(this.getApplicationContext(), String.format("RGB(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color)), Toast.LENGTH_LONG).show();
                    String command = Util.getRGBColorCommand(Color.red(color), Color.green(color), Color.blue(color));
                    BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
                }
        );

        imageViewForLightVibrantColor.setOnClickListener(view -> {
                    int color = ((ColorDrawable) view.getBackground()).getColor();
            Toast.makeText(this.getApplicationContext(), String.format("RGB(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color)), Toast.LENGTH_LONG).show();
                    String command = Util.getRGBColorCommand(Color.red(color), Color.green(color), Color.blue(color));
                    BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
                }
        );

        imageViewForMutedColor.setOnClickListener(view -> {
                    int color = ((ColorDrawable) view.getBackground()).getColor();
            Toast.makeText(this.getApplicationContext(), String.format("RGB(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color)), Toast.LENGTH_LONG).show();
                    String command = Util.getRGBColorCommand(Color.red(color), Color.green(color), Color.blue(color));
                    BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
                }
        );

        imageViewForDarkMutedColor.setOnClickListener(view -> {
                    int color = ((ColorDrawable) view.getBackground()).getColor();
            Toast.makeText(this.getApplicationContext(), String.format("RGB(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color)), Toast.LENGTH_LONG).show();
                    String command = Util.getRGBColorCommand(Color.red(color), Color.green(color), Color.blue(color));
                    BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
                }
        );

        imageViewForLightMutedColor.setOnClickListener(view -> {
                    int color = ((ColorDrawable) view.getBackground()).getColor();
            Toast.makeText(this.getApplicationContext(), String.format("RGB(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color)), Toast.LENGTH_LONG).show();
                    String command = Util.getRGBColorCommand(Color.red(color), Color.green(color), Color.blue(color));
                    BluetoothOrFirebaseReadFromAndWriteToDeviceInstance.getBluetoothOrFirebaseReadFromAndWriteToDevice().write(command);
                }
        );

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (data == null || data.getExtras() == null || data.getExtras().get("data") == null) {
            return;
        }

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");


        imageViewCamera.setImageBitmap(bitmap);

        Palette p = Palette.from(bitmap).generate();
        /*imageViewForDominantColor.setBackgroundColor(p.getDominantColor(0));

        imageViewForVibrantColor.setBackgroundColor( p.getVibrantColor(0));
        imageViewForDarkVibrantColor. setBackgroundColor(p.getDarkVibrantColor(0));
        imageViewForLightVibrantColor.setBackgroundColor(p.getLightVibrantColor(0));

        imageViewForMutedColor.setBackgroundColor(p.getMutedColor(0));
        imageViewForDarkMutedColor.setBackgroundColor(p.getDarkMutedColor(0));
        imageViewForLightMutedColor.setBackgroundColor(p.getLightMutedColor(0));*/


        if (p.getDominantSwatch() != null)
            imageViewForDominantColor.setBackgroundColor(p.getDominantSwatch().getRgb());
        else
            imageViewForDominantColor.setBackgroundColor(0);

        if (p.getVibrantSwatch() != null)
            imageViewForVibrantColor.setBackgroundColor(p.getVibrantSwatch().getRgb());
        else
            imageViewForVibrantColor.setBackgroundColor(0);

        if (p.getDarkVibrantSwatch() != null)
            imageViewForDarkVibrantColor.setBackgroundColor(p.getDarkVibrantSwatch().getRgb());
        else
            imageViewForDarkVibrantColor.setBackgroundColor(0);

        if (p.getLightVibrantSwatch() != null)
            imageViewForLightVibrantColor.setBackgroundColor(p.getLightVibrantSwatch().getRgb());
        else
            imageViewForLightVibrantColor.setBackgroundColor(0);

        if (p.getMutedSwatch() != null)
            imageViewForMutedColor.setBackgroundColor(p.getMutedSwatch().getRgb());
        else
            imageViewForMutedColor.setBackgroundColor(0);

        if (p.getDarkMutedSwatch() != null)
            imageViewForDarkMutedColor.setBackgroundColor(p.getDarkMutedSwatch().getRgb());
        else
            imageViewForDarkMutedColor.setBackgroundColor(0);

        if (p.getLightMutedSwatch() != null)
            imageViewForLightMutedColor.setBackgroundColor(p.getLightMutedSwatch().getRgb());
        else
            imageViewForLightMutedColor.setBackgroundColor(0);
    }

    /*public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }*/
/*    public static int getDominantColor(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        p.getDominantColor(0);

        p.getVibrantColor(0);
        p.getDarkVibrantColor(0);
        p.getLightVibrantColor(0);

        p.getMutedColor(0);
        p.getDarkMutedColor(0);
        p.getLightMutedColor(0);


        return Objects.requireNonNull(p.getVibrantSwatch()).getRgb();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncMessageHandler.shutdown();
    }

}
