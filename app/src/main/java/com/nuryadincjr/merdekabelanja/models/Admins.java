package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Admins implements Parcelable {
    private String uid;
    private String name;
    private String phone;
    private String email;
    private String photo;
    private String address;
    private String username;
    private String password;

    public Admins() {
    }

    public Admins(String uid, String name, String phone, String email,
                  String photo, String address, String username, String password) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.photo = photo;
        this.address = address;
        this.username = username;
        this.password = password;
    }

    protected Admins(Parcel in) {
        uid = in.readString();
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        photo = in.readString();
        address = in.readString();
        username = in.readString();
        password = in.readString();
    }

    public static final Creator<Admins> CREATOR = new Creator<Admins>() {
        @Override
        public Admins createFromParcel(Parcel in) {
            return new Admins(in);
        }

        @Override
        public Admins[] newArray(int size) {
            return new Admins[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(photo);
        parcel.writeString(address);
        parcel.writeString(username);
        parcel.writeString(password);
    }
}
