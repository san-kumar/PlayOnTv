package com.example.pubsub;

import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                RemoteMessage.Notification notice = remoteMessage.getNotification();
                Map<String, String> data = remoteMessage.getData();
                String msg = data.get("body");

                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                if (msg.equals("identify")) {
                    BroadcastMsg.broadcastMessage("pubsub", "NAME:" + Delegate.MyName);
                } else if (msg.startsWith("NAME:")) {
                    String name = msg.substring(5);

                    if (Delegate.players.indexOf(name) == -1) {
                        //Toast.makeText(getApplicationContext(), msg + "zzzzzzzz", Toast.LENGTH_LONG).show();
                        Delegate.players.add(name);
                    }

                    Delegate.MainActivity.refreshPlayers();
                } else if (msg.startsWith("PLAY:")) {
                    String qs = msg.substring(5);
                    UrlQuerySanitizer sanitizer = new UrlQuerySanitizer("https://www.example.com/?" + qs);
                    String app = sanitizer.getValue("app");
                    String player = sanitizer.getValue("player");
                    String theUrl = sanitizer.getValue("url");


                    if (player.equals(Delegate.MyName)) {
                        if (app.equals("default")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(theUrl)));
                        } else {//if (cls.contains("youtube")) {
                            //Toast.makeText(getApplicationContext(), "APP: " + app + ", PLAYER:" + player, Toast.LENGTH_LONG).show();

                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(theUrl));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setPackage(app);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage() + "[ APP: " + app + ", PLAYER:" + player + " ]", Toast.LENGTH_LONG).show();
                            }
                            /*String videoId = "cxLG2wtE7TM";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                            intent.putExtra("VIDEO_ID", videoId);
                            startActivity(intent);*/
                        }
                    }

                }
            }
        });
    }
}
