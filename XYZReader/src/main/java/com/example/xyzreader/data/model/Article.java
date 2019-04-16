package com.example.xyzreader.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Yassin Ajdi
 * @since 4/16/2019.
 */
public class Article {

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
