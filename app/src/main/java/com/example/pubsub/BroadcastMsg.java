package com.example.pubsub;

import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BroadcastMsg {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static String fTopic;
    private static String fMsg;

    public static void broadcastMessage(String topic, String msg) {
        fTopic = topic;
        fMsg = msg;

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://fcm.googleapis.com/fcm/send";
                String auth = "key=AAAAjOMEixo:APA91bF-5C8lX562lX6FYaMMAzDGAYvDh1tafVH3bcDrO-K7gBT8Lyiwe2kvJvdgroALV8_JsYmnkE4cEHHZSyelhYTXx4Yt_GIxvwutLLEvPk9bswuUB6KbHnDrdHBRjQJ1Ei6Ts2Lw";
                String json = "{\"to\": \"/topics/" + fTopic + "\", \"data\": {\"body\": \"" + fMsg + "\"}}";

                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(json, JSON); // new
                    // RequestBody body = RequestBody.create(JSON, json); // old
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", auth)
                            .post(body)
                            .build();
                    Response response = null;
                    response = client.newCall(request).execute();
                } catch (Exception ex) {
                    Log.d("ERROR", String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage()));
                }
            }
        }).start();
    }
}