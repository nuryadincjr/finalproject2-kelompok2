package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Users extends Admins implements Parcelable {
    private String address2;

    public Users() {
    }

    public Users(String uid, String name, String phone, String email, String photo,
                 String address, String username, String password, String address2) {
        super(uid, name, phone, email, photo, address, username, password);
        this.address2 = address2;
    }

    protected Users(Parcel in) {
        super(in);
        address2 = in.readString();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(address2);
    }
}
