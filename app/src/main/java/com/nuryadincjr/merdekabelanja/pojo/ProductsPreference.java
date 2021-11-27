package com.nuryadincjr.merdekabelanja.pojo;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.models.Products;

public class ProductsPreference {
    private static ProductsPreference instance;
    private ProgressDialog dialog;
    private Context context;

    public ProductsPreference(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    public static ProductsPreference getInstance(Context context) {
        if(instance == null) {
            instance = new ProductsPreference(context);
        }
        return instance;
    }

    public void onCreateData(Products products, Activity activity) {
        dialog.setMessage("Setuping data..");

        new ProductsRepository().insertProducts(products).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(context,"Success.", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
            activity.finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(context, "Error adding document.", Toast.LENGTH_SHORT).show();
        });
    }

    public void onUpdateData(Products products, ProgressDialog dialog) {
        dialog.setMessage("Setuping data..");

        new ProductsRepository().updateProducts(products).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(context,"Success.", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(context, "Error adding document.", Toast.LENGTH_SHORT).show();
        });
    }
}
