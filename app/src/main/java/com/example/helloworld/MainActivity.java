package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv1;
    Typeface tf1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 =(TextView)findViewById(R.id.tv1);
        tf1 = Typeface.createFromAsset(getAssets(),"fonts/FZYanTi.ttf");
        tv1.setTypeface(tf1);
    }
}
