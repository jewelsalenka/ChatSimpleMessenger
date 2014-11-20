package com.test.task.chat;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Olenka on 18.11.2014.
 */
public class SenderService extends Service {
    private Timer mTimer;
    private TimerTask mTimerTask;
    long interval = 20000;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("test", "SENDER SERVICE created");
        mTimer = new Timer();
        doTask();
    }

    private void doTask() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                String message = generateMessage();
                Intent intent = new Intent(ClientService.ACTION);
                intent.putExtra(ClientService.MESSAGE_TEXT, message);
                sendBroadcast(intent);
            }
        };
        mTimer.schedule(mTimerTask, 1000, interval);
    }

    private String generateMessage(){
        Resources res = getResources();
        String[] planets = res.getStringArray(R.array.stub_messages);
        Random r = new Random();
        return planets[r.nextInt(planets.length)];
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("test", "SENDER SERVICE destroyed");
        super.onDestroy();
    }
}
