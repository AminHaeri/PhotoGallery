package com.example.photogallery.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

public class GalleryItem {

    @SerializedName("id")
    private String mId;

    @SerializedName("title")
    private String mCaption;

    @SerializedName("url_s")
    private String mUrl;

    @SerializedName("owner")
    private String mOwner;

    public String getId() {
        return mId;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public GalleryItem(String id, String caption, String url) {
        mId = id;
        mCaption = caption;
        mUrl = url;
    }

    public Uri getPhotoUri() {
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }
}
