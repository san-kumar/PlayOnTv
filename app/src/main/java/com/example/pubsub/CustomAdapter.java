package com.example.pubsub;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private ArrayList<AppInfo> apps;
    private Context mContext;
    private String selectedApp = "";

    public CustomAdapter(Context cContext, ArrayList<AppInfo> apps) {
        this.apps = apps;
        this.mContext = cContext;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return apps.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.row, parent, false);
        TextView title  = (TextView) row.findViewById(R.id.txtTitle);
        ImageView i1 = (ImageView) row.findViewById(R.id.imgIcon);
        AppInfo app = apps.get(position);
        title.setText(app.getLabel());
        i1.setImageDrawable(app.getIcon());

        if(app.getName().equals(this.getSelectedApp()))
            row.setBackgroundColor(Color.CYAN);

        return (row);
    }

    public String getSelectedApp() {
        return selectedApp;
    }

    public void setSelectedApp(String selectedApp) {
        this.selectedApp = selectedApp;
        this.notifyDataSetChanged();
    }
}