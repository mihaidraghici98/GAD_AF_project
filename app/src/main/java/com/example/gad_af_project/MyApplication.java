package com.example.gad_af_project;

import android.app.Application;

import com.example.gad_af_project.database.AppDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppDatabase.getAppDatabase(this);
    }

}
