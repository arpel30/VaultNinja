package com.example.vaultninja;

import android.app.Application;

import com.example.vaultninja.MySPV;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MySPV.init(this);
    }
}