package com.nuryadincjr.merdekabelanja.pojo;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_SHARED_PREF;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalPreference {
    private static LocalPreference instance;
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public LocalPreference(Context context) {
        preferences = context.getSharedPreferences(KEY_SHARED_PREF, Context.MODE_PRIVATE);
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
