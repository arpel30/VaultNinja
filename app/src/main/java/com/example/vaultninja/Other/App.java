package com.example.vaultninja.Other;

import android.app.Application;

import com.example.vaultninja.Utils.MySPV;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MySPV.init(this);
    }
}