package com.internship.nkuskov.socialmap;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * Created by nkuskov on 5/22/2017.
 */

public class StopWatchService extends Service {

    TextView mTextView;
    Handler mHandler;
    Stopwatch timer;


    final int MSG_START_TIMER = 0;
    final int MSG_UPDATE_TIMER = 1;
    final int MSG_STOP_TIMER = 2;
    final int MSG_CANCEL_TIMER = 3;
    final int REFRESH_RATE = 100;

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Stopwatch();
        createHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.sendEmptyMessage(MSG_STOP_TIMER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.sendEmptyMessage(MSG_START_TIMER);
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_START_TIMER:
                        timer.start();
                        mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                        break;

                    case MSG_UPDATE_TIMER:
                        MapsActivity.currentTime.setText(getStopWatchText(timer.getElapsedTime()));
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                        break;

                    case MSG_STOP_TIMER:
                        mHandler.removeMessages(MSG_UPDATE_TIMER);
                        timer.stop();
                        MapsActivity.currentTime.setText(getStopWatchText(timer.getElapsedTime()));
                        break;

                    case MSG_CANCEL_TIMER:
                        mHandler.removeMessages(MSG_UPDATE_TIMER);
                        MapsActivity.currentTime.clearComposingText();
                        break;

                    default:
                        break;
                }
            }
        };
    }

    public static String getStopWatchText(long timeInMs) {
        long hours = timeInMs / (1000 * 60 * 60);
        long minutes = (timeInMs / (1000 * 60)) % 60;
        long seconds = (timeInMs / (1000)) % 60;

        String minutesSeconds;
        if (seconds < 10) {
            minutesSeconds = "" + minutes + ":0" + seconds;
        } else {
            minutesSeconds = "" + minutes + ":" + seconds;
        }

        if (hours != 0) {
            return "" + hours + ":" + minutesSeconds;
        } else {
            return minutesSeconds;
        }
    }

    private class Stopwatch {
        private long startTime = 0;
        private long stopTime = 0;
        private boolean running = false;

        public void start() {
            this.startTime = System.currentTimeMillis();
            this.running = true;
        }

        public void stop() {
            this.stopTime = System.currentTimeMillis();
            this.running = false;
        }

        public long getElapsedTime() {
            if (running) {
                return System.currentTimeMillis() - startTime;
            }
            return stopTime - startTime;
        }


    }

}

