package com.example.pubsub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context mainContext, Intent intent) {
        context = mainContext;

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Intent i = new Intent(context, MainActivity.class);  //MyActivity can be anything which you want to start on bootup...
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }, 5000);
    }
}