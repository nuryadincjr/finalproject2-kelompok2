package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Clothing extends Products implements Parcelable {
    private String people;
    private String brand_name;
    private String clothing_type;
    private List<String> color;
    private List<String> size;

    public Clothing() {
    }

    public Clothing(String id, String name, String descriptions, List<String> photo,
                    String piece, String quantity, String category, String latest_update,
                    String people, String brand_name, String clothing_type, List<String> color, List<String> size) {
        super(id, name, descriptions, photo, piece, quantity, category, latest_update);
        this.people = people;
        this.brand_name = brand_name;
        this.clothing_type = clothing_type;
        this.color = color;
        this.size = size;
    }

    protected Clothing(Parcel in) {
        super(in);
        this.people = in.readString();
        this.brand_name = in.readString();
        this.clothing_type = in.readString();
        this.size = in.createStringArrayList();
        this.color = in.createStringArrayList();
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

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getClothing_type() {
        return clothing_type;
    }

    public void setClothing_type(String clothing_type) {
        this.clothing_type = clothing_type;
    }

    public List<String> getColor() {
        return color;
    }

    public void setColor(List<String> color) {
        this.color = color;
    }

    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(people);
        parcel.writeString(brand_name);
        parcel.writeString(clothing_type);
        parcel.writeStringList(size);
        parcel.writeStringList(color);
    }
}
