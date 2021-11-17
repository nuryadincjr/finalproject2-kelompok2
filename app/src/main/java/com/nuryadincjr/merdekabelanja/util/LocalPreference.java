package com.nuryadincjr.merdekabelanja.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.nuryadincjr.merdekabelanja.pojo.Constaint;

public class LocalPreference {
    private static LocalPreference instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public LocalPreference(Context context) {
        preferences = context.getSharedPreferences(Constaint.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static LocalPreference getInstance(Context context) {
        if(instance == null) {
            instance = new LocalPreference(context);
        }
        return instance;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }
}
