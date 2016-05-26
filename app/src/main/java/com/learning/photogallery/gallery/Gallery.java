package com.learning.photogallery.gallery;

import com.learning.photogallery.FlickrFetcher;

import java.util.ArrayList;

public abstract class Gallery {
    public static final String TAG = "Gallery";

    public static final int FIRST_PAGE = 1;

    private int mCurrentPage;
    private int mPages;
    private FlickrFetcher mFlickrFetcher;
    private FetchingType mCurrentType;
    private ArrayList<GalleryItem> mCurrentGalleryItems;

    public enum FetchingType {RECENT, SEARCH}

    public Gallery() {
        mFlickrFetcher = new FlickrFetcher();
    }

    public int getGalleryCurrentPage() {
        return mCurrentPage;
    }

    public int getGalleryPages() {
        return mPages;
    }

    public void setFlickrFetchr(FlickrFetcher mFlickrFetcher) {
        this.mFlickrFetcher = mFlickrFetcher;
    }

    public FlickrFetcher getFlickrFetchr() {
        return mFlickrFetcher;
    }

    public void increaseCurrentPageAtOne() {
        mCurrentPage++;
    }

    public void decreaseCurrentPageAtOne() {
        mCurrentPage--;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int mCurrentPage) {
        this.mCurrentPage = mCurrentPage;
    }

    public int getPages() {
        return mPages;
    }

    public void setPages(int mPages) {
        this.mPages = mPages;
    }

    public FetchingType getCurrentType() {
        return mCurrentType;
    }

    public void setCurrentType(FetchingType mCurrentType) {
        this.mCurrentType = mCurrentType;
    }

    public ArrayList<GalleryItem> getCurrentGalleryItems() {
        return mCurrentGalleryItems;
    }

    public void setCurrentGalleryItems(ArrayList<GalleryItem> mCurrentGalleryItems) {
        this.mCurrentGalleryItems = mCurrentGalleryItems;
    }
}
