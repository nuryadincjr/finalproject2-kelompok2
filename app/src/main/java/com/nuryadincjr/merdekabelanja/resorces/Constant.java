package com.nuryadincjr.merdekabelanja.resorces;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.webkit.MimeTypeMap;

import java.util.Date;

public class Constant {

    public static final String KEY_UID = "UID";
    public static final String KEY_ISLOGIN = "ISLOGIN";
    public static final String KEY_SHARED_PREF = "samplePref";
    public static final String KEY_FILTER_DIVISION = "FILTER DIVISION";
    public static final String KEY_FILTER_PRODUCT = "FILTER PRODUCT";

    public static final String NAME_DATA = "DATA";
    public static final String NAME_UID = "UID";
    public static final String NAME_ISLOGIN = "ISLOGIN";
    public static final String NAME_ISPRINT = "ISPRINT";
    public static final String NAME_ISEDIT = "ISEDIT";
    public static final String NAME_CATEGORY = "CATEGORY";
    public static final String NAME_PRODUCT = "PRODUCT";
    public static final String NAME_REGISTER = "REGISTER";
    public static final String NAME_ACTION = "ACTION";
    public static final String NAME_LOGIN = "LOGIN";

    public static final String CHILD_USER = "users";
    public static final String CHILD_ADMIN = "admins";
    public static final String CHILD_STAFF = "staffs";
    public static final String CHILD_PROFILE = "profiles";
    public static final String CHILD_PRODUCT = "products";

    public static final String COLLECTION_USER = "users";
    public static final String COLLECTION_STAFF = "staffs";
    public static final String COLLECTION_PRODUCT = "products";
    public static final String COLLECTION_ADMIN = "admins";

    public static final int SESSION_FIRST = 1;
    public static final int SESSION_SECOND = 2;

    public static final String ARG_CATEGORY = "category";
    public static final String ARG_TAB_INDEX = "tab index";

    public static final String PREF_IS_LOGIN = "isLogin";

    public static String getFileExtension(Uri imageUri, Context context) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context
                .getContentResolver().getType(imageUri));
    }

    public static void getInfo(Context context){
        context.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://nuryadincjr.github.io")));
    }


    public static final String TAG = "LIA";


    public static final String DATE_FORMAT = "dd/MM/yyy hh:mm:ss a";
    public static String time() {
        return DateFormat.format(DATE_FORMAT, new Date()).toString();
    }
}
