package com.example.vaultninja.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.vaultninja.Other.Constants;

public class MySPV {
    private static MySPV instance;
    private SharedPreferences prefs;

    private MySPV(Context context) {
        prefs = context.getSharedPreferences(Constants.SP_FILE, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new MySPV(context.getApplicationContext());
        }
    }

    public static MySPV getInstance() {
        return instance;
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String def) {
        return prefs.getString(key, def);
    }

    public void putBool(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBool(String key) {
        return prefs.getBoolean(key, false);
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}

