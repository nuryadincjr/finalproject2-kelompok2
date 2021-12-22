package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Electronics extends Products implements Parcelable {
    private String brand_name;
    private String electronic_type;

    public Electronics() {
    }

    public Electronics(String id, String name, String descriptions, List<String> photo,
                       String piece, String quantity, String category,
                       String latest_update, String brand_name, String electronic_type) {
        super(id, name, descriptions, photo, piece, quantity, category, latest_update);
        this.brand_name = brand_name;
        this.electronic_type = electronic_type;
    }

    protected Electronics(Parcel in) {
        super(in);
        brand_name = in.readString();
        electronic_type = in.readString();
    }

    public static final Creator<Electronics> CREATOR = new Creator<Electronics>() {
        @Override
        public Electronics createFromParcel(Parcel in) {
            return new Electronics(in);
        }

        @Override
        public Electronics[] newArray(int size) {
            return new Electronics[size];
        }
    };

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getElectronic_type() {
        return electronic_type;
    }

    public void setElectronic_type(String electronic_type) {
        this.electronic_type = electronic_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(brand_name);
        parcel.writeString(electronic_type);

    }
}
