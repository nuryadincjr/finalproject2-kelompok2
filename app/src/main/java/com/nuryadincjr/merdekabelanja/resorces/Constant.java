package com.nuryadincjr.merdekabelanja.resorces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

public class Constant {

    public static final String KEY_UID = "UID";
    public static final String KEY_ISLOGIN = "ISLOGIN";
    public static final String KEY_SHARED_PREF = "samplePref";
    public static final String KEY_CATEGORY_DIVISION = "CATEGORY DIVISION";
    public static final String KEY_CATEGORY_PRODUCT = "CATEGORY PRODUCT";

    public static final String NAME_DATA = "DATA";
    public static final String NAME_UID = "UID";
    public static final String NAME_ISLOGIN = "ISLOGIN";
    public static final String NAME_ISPRINT = "ISPRINT";
    public static final String NAME_ISEDIT = "ISEDIT";
    public static final String NAME_CATEGORY = "CATEGORY";
    public static final String NAME_PRODUCT = "PRODUCT";
    public static final String NAME_REGISTER = "REGISTER";
    public static final String NAME_EDITED = "EDITED";
    public static final String NAME_ACTION = "ACTION";
    public static final String NAME_LOGIN = "LOGIN";
    public static final String KEY_CATEGORY_CLOTHING = "CATEGORY CLOTHING";
    public static final String NAME_CATEGORY_DIVISION = "CATEGORY DIVISION";

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
    public static final String PATTERN_LABEL = "(http|ftp|https):\\/\\/([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])";


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

    @SuppressLint("SetTextI18n")
    public static void isEmptyMessage(FrameLayout frameLayout, RecyclerView recyclerView, Context context) {
        TextView messages = new TextView(context);
        messages.setText("The Data is not currently available!\nPlease add now!");
        messages.setGravity(Gravity.CENTER);
        frameLayout.addView(messages);
        recyclerView.setVisibility(View.GONE);
    }

    public static <T> void getStartActivity(Context context, Class<T> tClass, Object tData, String key) {
        context.startActivity(new Intent(context, tClass)
                .putExtra(NAME_DATA, (Parcelable) tData)
                .putExtra(key, true));
    }


    @SuppressLint("SetTextI18n")
    public static void isEmptyMessage(LinearLayout linearLayout, RecyclerView recyclerView) {
        TextView messages = new TextView(linearLayout.getContext());
        messages.setText("The product is not currently available!");
        messages.setGravity(Gravity.CENTER);
        linearLayout.addView(messages);
        recyclerView.setVisibility(View.GONE);
    }

}
