package com.example.runnertest;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.runnertest.maptools.LocPoint;

import java.util.LinkedList;
import java.util.List;

public class RunnerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
