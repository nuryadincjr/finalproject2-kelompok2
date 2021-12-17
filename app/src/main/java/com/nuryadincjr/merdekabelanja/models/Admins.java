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
    private String latest_update;
    private String status_account;

    public Admins() {
    }

    public Admins(String uid, String name, String phone, String email,
                  String photo, String address, String username,
                  String password, String latest_update, String status_account) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.photo = photo;
        this.address = address;
        this.username = username;
        this.password = password;
        this.latest_update = latest_update;
        this.status_account = status_account;
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
        latest_update = in.readString();
        status_account = in.readString();
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

    public String getLatest_update() {
        return latest_update;
    }

    public void setLatest_update(String latest_update) {
        this.latest_update = latest_update;
    }

    public String getStatus_account() {
        return status_account;
    }

    public void setStatus_account(String status_account) {
        this.status_account = status_account;
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
        parcel.writeString(latest_update);
        parcel.writeString(status_account);
    }
}
