package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Electronics extends Products implements Parcelable {
    private String category;
    private String brand_name;
    private String product_type;

    public Electronics() {
    }

    public Electronics(String id, String name, String descriptions,
                       List<String> photo, String piece, String quantity,
                       String category, String brand_name, String product_type) {
        super(id, name, descriptions, photo, piece, quantity);
        this.category = category;
        this.brand_name = brand_name;
        this.product_type = product_type;
    }

    protected Electronics(Parcel in) {
        super(in);
        category = in.readString();
        brand_name = in.readString();
        product_type = in.readString();
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(category);
        parcel.writeString(brand_name);
        parcel.writeString(product_type);
    }
}
