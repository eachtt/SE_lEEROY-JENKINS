package com.example.runnertest.gamekeeper;

import android.view.MotionEvent;

import com.example.runnertest.GameActivity;

import java.util.LinkedList;

public class GameAdapter {
    private final LinkedList<GameUnit> gameUnitList;

    public GameAdapter() {
        gameUnitList = new LinkedList<>();
    }

    public void clickMap(MotionEvent event) {
        for (GameUnit gameUnit : gameUnitList) {
            if (gameUnit.isCanTouch() &&
                    gameUnit.contains((int)event.getX(), (int)event.getY())) {
                // ..
                gameUnit.onClick();
                break;
            }
        }
    }

    public void addGameUnit(GameUnit gameUnit) {
        this.gameUnitList.add(gameUnit);
    }

    public LinkedList<GameUnit> getGameUnitList() {
        return this.gameUnitList;
    }
}
