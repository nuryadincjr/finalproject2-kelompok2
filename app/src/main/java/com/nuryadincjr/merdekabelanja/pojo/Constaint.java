package com.nuryadincjr.merdekabelanja.pojo;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import android.webkit.MimeTypeMap;

import java.util.Date;

public class Constaint {
    public static final String DATE_FORMAT = "dd/MM/yyy hh:mm:ss a";
    public static final String SHARED_PREF_NAME = "samplePref";
    public static final String PREF_IS_LOGIN = "isLogin";
    public static final String TAG = Activity.class.getName();

    public static String time() {
        return DateFormat.format(Constaint.DATE_FORMAT, new Date()).toString();
    }

    public static String getFileExtension(Uri imageUri, Context context) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context
                .getContentResolver().getType(imageUri));
    }
}
