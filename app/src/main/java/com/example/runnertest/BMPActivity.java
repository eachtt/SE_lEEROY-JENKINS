package com.example.runnertest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.runnertest.maptools.BitAdapter;
import com.example.runnertest.maptools.LocPoint;
import com.example.runnertest.maptools.MapView;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class BMPActivity extends AppCompatActivity {
    private static final int LOCATION_FINE_PERMISSION = 1;
    private Bitmap mBitmap;
    private MapView mMapView;
    private BitAdapter mAdapter;

    private int getBitmapId(String imageName) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_m_p);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_FINE_PERMISSION);
        }

        mMapView = (MapView) findViewById(R.id.surface);
        mAdapter = new BitAdapter();
        mAdapter.setLocTracker(this);
        mMapView.setAdapter(mAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(200);
                } catch(InterruptedException e) {

                }

                mAdapter.setBitmap(decodeSampledBitmapFromStream(getResources(), getBitmapId("myimage2")));

            }
        }).start();

    }

    // 读取bmp文件
    public static Bitmap decodeSampledBitmapFromStream(Resources res, int resId) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            //options.inSampleSize=1;
            InputStream is = res.openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}