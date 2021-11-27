package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Books extends Products implements Parcelable {
    private String author;
    private String book_type;
    private int number_of_page;
    private String publisher;
    private String publisher_year;


    public Books() {
    }

    public Books(String id, String name, String descriptions, List<String> photo, String piece,
                 String quantity, String category, String author, String latest_update, String publisher,
                 String publisher_year, String book_type, int number_of_page) {
        super(id, name, descriptions, photo, piece, quantity, category, latest_update);
        this.author = author;
        this.publisher = publisher;
        this.publisher_year = publisher_year;
        this.book_type = book_type;
        this.number_of_page = number_of_page;
    }

    protected Books(Parcel in) {
        super(in);
        this.author = in.readString();
        this.publisher = in.readString();
        this.publisher_year = in.readString();
        this.book_type = in.readString();
        this.number_of_page = in.readInt();
    }

    public static final Creator<Books> CREATOR = new Creator<Books>() {
        @Override
        public Books createFromParcel(Parcel in) {
            return new Books(in);
        }

        @Override
        public Books[] newArray(int size) {
            return new Books[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBook_type() {
        return book_type;
    }

    public void setBook_type(String book_type) {
        this.book_type = book_type;
    }

    public int getNumber_of_page() {
        return number_of_page;
    }

    public void setNumber_of_page(int number_of_page) {
        this.number_of_page = number_of_page;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher_year() {
        return publisher_year;
    }

    public void setPublisher_year(String publisher_year) {
        this.publisher_year = publisher_year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(this.author);
        parcel.writeString(this.publisher);
        parcel.writeString(this.publisher_year);
        parcel.writeString(this.book_type);
        parcel.writeInt(this.number_of_page);
    }
}
