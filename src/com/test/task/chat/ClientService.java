package com.test.task.chat;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

/**
 * Created by Olenka on 18.11.2014.
 */

public class ClientService extends Service {
    public static final String ACTION = "chat.get.new.message";
    public static final String MESSAGE_TEXT = "messageText";
    public static final String DATE = "dateAndTime";
    private BroadcastReceiver mReceiver;
    private PendingIntent mPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("test", "NotificationService created");
        final IntentFilter intentFilter = new IntentFilter(ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null){
                    return;
                }
                intent.putExtra(DATE, new Date().getTime());
                try {
                    mPendingIntent.send(ClientService.this, 1, intent);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(mReceiver, intentFilter);
        startChatService();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPendingIntent = intent.getParcelableExtra(ChatActivity.PARAM_PENDING_INTENT);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("test", "NotificationService destroyed");
        unregisterReceiver(mReceiver);
        stopChatService();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("test", "onBind");
        return null;
    }

    public void startChatService(){
        startService(new Intent(this, SenderService.class));
    }

    public void stopChatService(){
        stopService(new Intent(this, SenderService.class));
    }
}
