package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Clothing extends Products implements Parcelable {
    private String category;
    private String brand_name;
    private List<String> size;
    private List<String> color;

    public Clothing(){
    }

    public Clothing(String id, String name, String descriptions,
                    List<String> photo, String piece, String quantity,
                    String category, String brand_name, List<String> size, List<String> color) {
        super(id, name, descriptions, photo, piece, quantity);
        this.category = category;
        this.brand_name = brand_name;
        this.size = size;
        this.color = color;
    }

    protected Clothing(Parcel in) {
        super(in);
        category = in.readString();
        brand_name = in.readString();
        size = in.createStringArrayList();
        color = in.createStringArrayList();
    }

    public static final Creator<Clothing> CREATOR = new Creator<Clothing>() {
        @Override
        public Clothing createFromParcel(Parcel in) {
            return new Clothing(in);
        }

        @Override
        public Clothing[] newArray(int size) {
            return new Clothing[size];
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

    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
    }

    public List<String> getColor() {
        return color;
    }

    public void setColor(List<String> color) {
        this.color = color;
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
        parcel.writeStringList(size);
        parcel.writeStringList(color);
    }
}
