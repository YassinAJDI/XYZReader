package com.ajdi.xyzreader.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class Article implements Parcelable {

    @SerializedName("id")
    private long id;

    @SerializedName("body")
    private String body;

    @SerializedName("photo")
    private String photo_url;

    @SerializedName("thumb")
    private String thumb_url;

    @SerializedName("author")
    private String author;

    @SerializedName("title")
    private String title;

    @SerializedName("aspect_ratio")
    private float aspect_ratio;

    @SerializedName("published_date")
    private String published_date;

    protected Article(Parcel in) {
        id = in.readLong();
        body = in.readString();
        photo_url = in.readString();
        thumb_url = in.readString();
        author = in.readString();
        title = in.readString();
        aspect_ratio = in.readFloat();
        published_date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(body);
        dest.writeString(photo_url);
        dest.writeString(thumb_url);
        dest.writeString(author);
        dest.writeString(title);
        dest.writeFloat(aspect_ratio);
        dest.writeString(published_date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getAspect_ratio() {
        return aspect_ratio;
    }

    public void setAspect_ratio(float aspect_ratio) {
        this.aspect_ratio = aspect_ratio;
    }

    public String getPublished_date() {
        return published_date;
    }

    public void setPublished_date(String published_date) {
        this.published_date = published_date;
    }
}
