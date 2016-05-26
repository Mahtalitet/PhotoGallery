package com.learning.photogallery.gallery;

import android.util.Log;

import java.util.ArrayList;

public class GalleryRecent extends Gallery {

    public ArrayList<GalleryItem> getRecentPhotos() {

        increaseCurrentPageAtOne();
        Log.i(TAG, "Current page for "+GalleryRecent.class.toString()+" is: "+getCurrentPage());
        if (getCurrentPage() == FIRST_PAGE) {
            setCurrentGalleryItems(getFlickrFetchr().fetchItemsLast());
            setPages(getFlickrFetchr().getAllPages());
        } else if (getCurrentPage() <= getPages()) {
            setCurrentGalleryItems(getFlickrFetchr().fetchItemsByPage(getCurrentPage()));
        } else if (getCurrentPage() > getPages()) {
            setCurrentGalleryItems(new ArrayList<GalleryItem>());
        }

        return getCurrentGalleryItems();
    }
}
