package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BookTypes implements Parcelable {
    private String book_type;

    protected BookTypes(Parcel in) {
        book_type = in.readString();
    }

    public static final Creator<BookTypes> CREATOR = new Creator<BookTypes>() {
        @Override
        public BookTypes createFromParcel(Parcel in) {
            return new BookTypes(in);
        }

        @Override
        public BookTypes[] newArray(int size) {
            return new BookTypes[size];
        }
    };

    public String getBook_type() {
        return book_type;
    }

    public void setBook_type(String book_type) {
        this.book_type = book_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(book_type);
    }
}
