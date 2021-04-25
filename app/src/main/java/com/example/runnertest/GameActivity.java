package com.example.runnertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.runnertest.gamekeeper.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView mGameView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mGameView = findViewById(R.id.game_surface);
    }

    public void stepIntoBMP(View view) {
        Intent intent = new Intent(this, BMPActivity.class);
        startActivity(intent);
    }

}