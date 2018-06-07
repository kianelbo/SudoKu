package com.kian.pitproject.sudoku;

import android.os.Handler;
import android.widget.TextView;

public class TimeHandler implements Runnable {

    private TextView timeView;
    private long startTime = 0, millis, savedMillis;
    private Handler timeHandler = new Handler();

    public TimeHandler(TextView timeView) {
        this.timeView = timeView;
        startTime = System.currentTimeMillis();
        timeHandler.postDelayed(this, 0);
        savedMillis = 0;
    }

    @Override
    public void run() {
        millis = savedMillis + System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        timeView.setText(String.format("%d:%02d", minutes, seconds));

        timeHandler.postDelayed(this, 500);
    }

    public long getElapsedTime() {
        return millis;
    }

    public void setSavedMillis(long savedMillis) {
        this.savedMillis = savedMillis;
    }
}
