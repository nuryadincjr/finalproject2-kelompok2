package com.nuryadincjr.merdekabelanja.pojo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.nuryadincjr.merdekabelanja.R;

public class PermissionsAccess {
    private static final int PERMISSION_REQUEST_STORAGE = 0;

    public static void requestStoragePermission(Context context, View rootView) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Snackbar.make(rootView, R.string.storage_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, view ->
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, PERMISSION_REQUEST_STORAGE)).show();

        } else {
            Snackbar.make(rootView, R.string.storage_unavailable, Snackbar.LENGTH_LONG).show();
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_STORAGE);
        }
    }
}
