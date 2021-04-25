package com.example.runnertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.runnertest.accountmanager.AccountConnector;
import com.example.runnertest.accountmanager.AccountUnit;
import com.example.runnertest.maptools.LocPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static LinkedList<LocPoint> locPoints;
    public static SharedPreferences sharedPref;
    public static String[] dataKeys = {
        "username", "token"
    };

    TextView tv;
    Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv1);
        tf = Typeface.createFromAsset(getAssets(), "fonts/FZYanTi.ttf");

        locPoints = new LinkedList<>();
        sharedPref = getPreferences(MODE_PRIVATE);
        checkBackgroundPermission();

        stepIntoBMP();
        /*
        String username = loadData(dataKeys[0]);
        String token = loadData(dataKeys[1]);
        new Thread(()->{
            if (AccountConnector.isLogin(username, token)) {
                stepIntoGame(username, token);
            }
        }).start();
        */
    }

    public void stepIntoBMP() {
        Intent intent = new Intent(this, BMPActivity.class);
        startActivity(intent);
    }

    public void doLogin(View view) {
        EditText userText = (EditText) findViewById(R.id.ID);
        EditText passwdText = (EditText) findViewById(R.id.password);
        String username = userText.getText().toString();
        String passwd = passwdText.getText().toString();
        new Thread(()-> {
            String token = AccountConnector.login(username, passwd);
            if (token.equals(emptyString)) {
                // 登录失败
                return;
            }
            stepIntoGame(username, token);
        }).start();

    }


    public void doRegister(View view) {
        EditText userText = (EditText) findViewById(R.id.ID);
        EditText passwdText = (EditText) findViewById(R.id.password);
        String username = userText.getText().toString();
        String passwd = passwdText.getText().toString();
        String token = AccountConnector.register(username, "12345", passwd);
        if (token.equals(emptyString)) {
            // 注册失败
            return;
        }
        stepIntoGame(username, token);
    }

    public void stepIntoGame(String username, String token) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void checkBackgroundPermission() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveData(int key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(dataKeys[key], value);
        editor.apply();
    }

    private static final String emptyString = "";
    public static String loadData(String key) {
        return sharedPref.getString(key, emptyString);
    }
}