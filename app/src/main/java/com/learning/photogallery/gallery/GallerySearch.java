package com.learning.photogallery.gallery;

import android.util.Log;

import java.util.ArrayList;

public class GallerySearch extends Gallery {

    public ArrayList<GalleryItem> getSearchedPhotos(String query) {

        increaseCurrentPageAtOne();
        Log.i(TAG, "Current page for "+GallerySearch.class.toString()+" is: "+getCurrentPage());
        if (getCurrentPage() == FIRST_PAGE) {
            setCurrentGalleryItems(getFlickrFetchr().search(query));
            setPages(getFlickrFetchr().getAllPages());
        } else if (getCurrentPage() <= getPages()) {
            setCurrentGalleryItems(getFlickrFetchr().searchByPage(query, getCurrentPage()));
        } else if (getCurrentPage() > getPages()) {
            setCurrentGalleryItems(new ArrayList<GalleryItem>());
        }

        return getCurrentGalleryItems();
    }
}
