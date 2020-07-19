package com.example.huoqu.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String packageName;
    private Drawable icon ;
    private String appName;

    public AppInfo(String packageName, Drawable icon, String appName) {
        this.packageName = packageName;
        this.icon = icon;
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
