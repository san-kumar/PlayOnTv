package com.example.pubsub;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String name;
    private String label;
    private Drawable icon;

    public AppInfo(String name, String label, Drawable icon) {
        this.name = name;
        this.label = label;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }
}
