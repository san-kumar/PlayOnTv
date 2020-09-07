package com.example.pubsub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String topic = "pubsub";

    private ListView appList;
    private ArrayList<AppInfo> allApps;
    private CustomAdapter appAdapter;

    private ListView playerList;
    private ArrayList<AppInfo> allPlayers;
    private CustomAdapter playerAdapter;

    private String theUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Delegate.MainActivity = this;

        appList = (ListView) findViewById(R.id.apps);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                AppInfo app = allApps.get(position);
                appAdapter.setSelectedApp(app.getName());
            }
        });

        playerList = (ListView) findViewById(R.id.players);
        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                AppInfo app = allPlayers.get(position);
                playerAdapter.setSelectedApp(app.getName());
            }
        });

        populateApps();

        SharedPreferences settings = getApplicationContext().getSharedPreferences("Default", 0);
        Delegate.MyName = settings.getString("MyName", "None");

        if (Delegate.MyName.equals("None"))
            showNameDialog();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            theUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
            //Toast.makeText(getApplicationContext(), theUrl, Toast.LENGTH_LONG).show();
            //identify();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.connect(null);
    }

    public void connect(View v) {
        FirebaseMessaging firebase = FirebaseMessaging.getInstance();
        firebase.subscribeToTopic(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "Connected (" + Delegate.MyName + ")";

                if (!task.isSuccessful()) {
                    msg = "Connection failed";
                    finish();
                }

                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                BroadcastMsg.broadcastMessage(topic, "identify");
            }
        });
    }

    public void refreshPlayers() {
        allPlayers = new ArrayList<>();

        for (String player : Delegate.players) {
            allPlayers.add(new AppInfo(player, player, null));
        }

        playerAdapter = new CustomAdapter(this, allPlayers);
        playerList.setAdapter(playerAdapter);
    }


    private void populateApps() {
        allApps = new ArrayList<AppInfo>();

        PackageManager pm = getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);

        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            Intent intentOfStartActivity = pm.getLaunchIntentForPackage(p.packageName);
            if (intentOfStartActivity == null) continue;
            if (p.versionName == null) continue;
            //if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;

            String label = p.applicationInfo.loadLabel(pm).toString();
            Drawable icon = p.applicationInfo.loadIcon(pm);
            String name = p.packageName;
            AppInfo app = new AppInfo(name, label, icon);
            allApps.add(app);
            /*String result = p.applicationInfo.loadLabel(getPackageManager()).toString() + ", " + p.packageName + ", " +
                    p.versionName + ", " + p.versionCode + ", " + p.applicationInfo.loadIcon(getPackageManager());*/
        }

        appAdapter = new CustomAdapter(this, allApps);
        appList.setAdapter(appAdapter);
    }

    private void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type a name for this device");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);// | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                SharedPreferences settings = getApplicationContext().getSharedPreferences("Default", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("MyName", m_Text);
                editor.apply();

                Delegate.MyName = m_Text;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    public void launch(View v) {
        String player = playerAdapter.getSelectedApp();
        String app = appAdapter.getSelectedApp();

        if (player.isEmpty() || app.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select both player and app", Toast.LENGTH_LONG).show();
            return;
        }

        if (theUrl.isEmpty()) {
            Toast.makeText(getApplicationContext(), "The URL cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        String msg = "PLAY:" + "player=" + URLEncoder.encode(player) + "&app=" + URLEncoder.encode(app) + "&url=" + URLEncoder.encode(theUrl);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        BroadcastMsg.broadcastMessage(topic, msg);
        finish();
        //theUrl = "https://www.youtube.com/watch?v=bzSTpdcs-EI";
    }
}