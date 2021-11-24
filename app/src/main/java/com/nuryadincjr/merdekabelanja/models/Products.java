package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Products implements Parcelable {
    private String id;
    private String name;
    private String descriptions;
    private List<String> photo;
    private String piece;
    private String quantity;
    private String category;

    public Products() {
    }

    public Products(String id, String name, String descriptions,
                    List<String> photo, String piece, String quantity, String category) {
        this.id = id;
        this.name = name;
        this.descriptions = descriptions;
        this.photo = photo;
        this.piece = piece;
        this.quantity = quantity;
        this.category = category;
    }

    protected Products(Parcel in) {
        id = in.readString();
        name = in.readString();
        descriptions = in.readString();
        photo = in.createStringArrayList();
        piece = in.readString();
        quantity = in.readString();
        category = in.readString();
    }

    public static final Creator<Products> CREATOR = new Creator<Products>() {
        @Override
        public Products createFromParcel(Parcel in) {
            return new Products(in);
        }

        @Override
        public Products[] newArray(int size) {
            return new Products[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public List<String> getPhoto() {
        return photo;
    }

    public void setPhoto(List<String> photo) {
        this.photo = photo;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(descriptions);
        parcel.writeStringList(photo);
        parcel.writeString(piece);
        parcel.writeString(quantity);
        parcel.writeString(category);
    }
}
