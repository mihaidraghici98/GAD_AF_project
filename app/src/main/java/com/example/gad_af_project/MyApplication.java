package com.example.gad_af_project;

import android.app.Application;
import android.content.res.Resources;

import com.example.gad_af_project.database.AppDatabase;

public class MyApplication extends Application {
    static Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
        AppDatabase.getAppDatabase(this);
    }

    public static Resources getR() {
        return resources;
    }
}
