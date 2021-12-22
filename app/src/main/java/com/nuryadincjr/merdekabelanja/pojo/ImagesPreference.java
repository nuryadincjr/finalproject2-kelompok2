package com.nuryadincjr.merdekabelanja.pojo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class ImagesPreference {
    @SuppressLint("StaticFieldLeak")
    private static ImagesPreference instance;

    public ImagesPreference(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
    }

    public static ImagesPreference getInstance(Context context) {
        if(instance == null) {
            instance = new ImagesPreference(context);
        }
        return instance;
    }

    public void getMultipleImage(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(
                Intent.createChooser(intent, "Select Picture"), 25);
    }

    public void getSinggleImage(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, 25);
    }

    public List<Uri> getUriImageList(int requestCode, int resultCode, Intent data, ToggleButton toggleButton){
        List<Uri> uriImageList = new ArrayList<>();
        if (requestCode != 25 || resultCode != -1) {
            toggleButton.setChecked(false);
        } else if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                uriImageList.add(i, data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uriImageList.add(0, data.getData());
        } else toggleButton.setChecked(false);

        return uriImageList;
    }

    public List<String> getList(String str) {
        String[] myStrings = str.split(",");
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < myStrings.length; i++) {
            String item = myStrings[i].replace(" ", "");
            if(item.equals("OtherProducts")) item = "Other Products";

            stringList.add(i ,item);
        }
        return stringList;
    }

    public static String getStringReplace(Object value){
        return String.valueOf(value)
                .replace("]", "")
                .replace("[", "");
    }
}
