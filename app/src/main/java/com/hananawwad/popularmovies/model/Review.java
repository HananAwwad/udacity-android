package com.hananawwad.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 1/3/2016.
 */
public class Review implements Parcelable {

    public String author;
    public String content;
    public String url;

    protected Review(Parcel in) {
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    @Override
    public String toString() {
        return "Review{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

}
