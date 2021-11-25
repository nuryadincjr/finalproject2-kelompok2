package com.nuryadincjr.merdekabelanja.adapters;

import android.content.Context;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SpinnersAdapter {
    private static SpinnersAdapter instance;
    private final Context context;

    public SpinnersAdapter(Context context) {
        this.context = context;
    }

    public static SpinnersAdapter getInstance(Context context) {
        if(instance == null) {
            instance = new SpinnersAdapter(context);
        }
        return instance;
    }

    public void getSpinnerAdapter(Spinner spinner, int array) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(context, array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener((OnItemSelectedListener) context);
        spinner.setAdapter(adapter);
    }

}
