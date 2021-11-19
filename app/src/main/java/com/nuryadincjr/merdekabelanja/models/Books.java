package com.nuryadincjr.merdekabelanja.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Books extends Products implements Parcelable {
    private String title;
    private String author;
    private String publisher;
    private String publisher_year;
    private List<String> book_type;
    private int number_of_page;

    public Books() {
    }

    public Books(String id, String name, String descriptions, List<String> photo,
                 String piece, String quantity, String title, String author, String publisher,
                 String publisher_year, List<String> book_type, int number_of_page) {
        super(id, name, descriptions, photo, piece, quantity);
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publisher_year = publisher_year;
        this.book_type = book_type;
        this.number_of_page = number_of_page;
    }

    protected Books(Parcel in) {
        super(in);
        title = in.readString();
        author = in.readString();
        publisher = in.readString();
        publisher_year = in.readString();
        in.readStringList(book_type);
        number_of_page = in.readInt();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public List<String> getBook_type() {
        return book_type;
    }

    public void setBook_type(List<String> book_type) {
        this.book_type = book_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(author);
        parcel.writeString(publisher);
        parcel.writeString(publisher_year);
        parcel.writeList(book_type);
        parcel.writeInt(number_of_page);
    }
}
