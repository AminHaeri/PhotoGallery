package com.example.photogallery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Gallery {

    @SerializedName("page")
    private int mPage;

    @SerializedName("pages")
    private int mPages;

    @SerializedName("total")
    private int mTotal;

    @SerializedName("photo")
    private List<GalleryItem> mGalleryItems;

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public int getPages() {
        return mPages;
    }

    public void setPages(int pages) {
        mPages = pages;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public List<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }

    public void setGalleryItems(List<GalleryItem> galleryItems) {
        mGalleryItems = galleryItems;
    }
}
