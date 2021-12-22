package com.nuryadincjr.merdekabelanja.resorces;

import android.annotation.SuppressLint;
import android.content.Context;

import com.nuryadincjr.merdekabelanja.R;

public class Categoryes {
    @SuppressLint("StaticFieldLeak")
    private static Categoryes instance;
    private final Context context;

    public Categoryes(Context context) {
        this.context = context;
    }

    public static Categoryes getInstance(Context context) {
        if(instance == null) {
            instance = new Categoryes(context);
        }
        return instance;
    }

    public String[] people(){
        return context.getResources().getStringArray(R.array.people);
    }

    public String[] bookType(){
        return context.getResources().getStringArray(R.array.book_type);
    }

    public String[] electronicType(){
        return context.getResources().getStringArray(R.array.electronic_type);
    }

    public String[] clothingType(){
        return context.getResources().getStringArray(R.array.clothing_type);
    }

    public String[] division(){
        return context.getResources().getStringArray(R.array.division);
    }

    public String[] productsType(){
        return context.getResources().getStringArray(R.array.products_type);
    }
}
