package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Staffs extends Admins implements Parcelable {
    private String devision;

    public Staffs() {
    }

    public Staffs(String uid, String name, String phone, String email, String photo,
                  String address, String username, String password, String devision) {
        super(uid, name, phone, email, photo, address, username, password);
        this.devision = devision;
    }

    protected Staffs(Parcel in) {
        super(in);
        devision = in.readString();
    }

    public static final Creator<Staffs> CREATOR = new Creator<Staffs>() {
        @Override
        public Staffs createFromParcel(Parcel in) {
            return new Staffs(in);
        }

        @Override
        public Staffs[] newArray(int size) {
            return new Staffs[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(devision);
    }
}
