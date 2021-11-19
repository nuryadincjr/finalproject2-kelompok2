package com.nuryadincjr.merdekabelanja.pojo;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import android.webkit.MimeTypeMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constaint {
    public static final String DATE_FORMAT = "dd/MM/yyy hh:mm:ss a";
    public static final String SHARED_PREF_NAME = "samplePref";
    public static final String PREF_IS_LOGIN = "isLogin";

    public static String time() {
        return DateFormat.format(Constaint.DATE_FORMAT, new Date()).toString();
    }

    public static String getFileExtension(Uri imageUri, Context context) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context
                .getContentResolver().getType(imageUri));
    }

    public static String getImagPath(String imageUrl) {
        Pattern patternImg = Pattern.compile("%2F([A-Za-z0-9.-]+)");
        Matcher mat = patternImg.matcher(imageUrl);
        List<String> listImg = new ArrayList<>();
        while (mat.find()) {
            listImg.add(mat.group(1));
        }
        return listImg.get(1);
    }
}
