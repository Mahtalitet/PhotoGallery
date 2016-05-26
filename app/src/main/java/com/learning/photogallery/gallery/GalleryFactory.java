package com.learning.photogallery.gallery;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class GalleryFactory {
    private static final String TAG ="GalleryFactory";

    private static GalleryFactory mGalleryFactory;
    private Gallery mGallery;
    private static Gallery.FetchingType mCurrentRequestType;
    private Context mContext;

    private GalleryFactory(Gallery gallery, Context context) {
        mGallery = gallery;
        mContext = context;
    }

    public static GalleryFactory get(Gallery.FetchingType requestType, boolean isFirst, Context context) {

        if ((mCurrentRequestType == null || isFirst) || (mCurrentRequestType != requestType)) {
            mCurrentRequestType = requestType;
            if (mCurrentRequestType == Gallery.FetchingType.RECENT) {
                mGalleryFactory = new GalleryFactory(new GalleryRecent(), context);
                Log.i(TAG, "Created new instance of "+mGalleryFactory.getClass().toString());
            } else {
                mGalleryFactory = new GalleryFactory(new GallerySearch(), context);
                Log.i(TAG, "Created new instance of "+mGalleryFactory.getClass().toString());
            }
            Log.i(TAG, "Current request type: "+mCurrentRequestType+". Is first: "+isFirst);
        }

        return mGalleryFactory;
    }

    public static GalleryFactory get() throws FactoryNotCreatedException{
         if (mGalleryFactory == null) {
             throw new FactoryNotCreatedException();
         } else {
             return mGalleryFactory;
         }
    }

    public ArrayList<GalleryItem> returnItemsBy(String query) {
        ArrayList<GalleryItem> items = new ArrayList<>();

        switch (mCurrentRequestType) {
            case RECENT:
                items = ((GalleryRecent) mGallery).getRecentPhotos();
                break;
            case SEARCH:
                items = ((GallerySearch) mGallery).getSearchedPhotos(query);
                break;
        }

        return items;
    }

    public void makeToastAboutPagesCount() {

        if ((mCurrentRequestType == Gallery.FetchingType.RECENT) && (mGallery.getCurrentPage() == 1)) {
            Toast.makeText(mContext, "All pages with recent photos: "+mGallery.getPages(), Toast.LENGTH_SHORT).show();
        } else if ((mCurrentRequestType == Gallery.FetchingType.RECENT) && (mGallery.getCurrentPage() > 1) && (mGallery.getCurrentPage() <= mGallery.getPages())) {
            Toast.makeText(mContext, "Current gallery page: "+mGallery.getCurrentPage(), Toast.LENGTH_SHORT).show();
        } else if ((mCurrentRequestType == Gallery.FetchingType.SEARCH) && (mGallery.getCurrentPage() == 1)) {
            Toast.makeText(mContext, "Pages with searched photos: "+mGallery.getPages(), Toast.LENGTH_SHORT).show();
        } else if ((mCurrentRequestType == Gallery.FetchingType.SEARCH) && (mGallery.getCurrentPage() > 1) && (mGallery.getCurrentPage() <= mGallery.getPages())) {
            Toast.makeText(mContext, "Current request's page: "+mGallery.getCurrentPage(), Toast.LENGTH_SHORT).show();
        }
    }

    public int getGalleryPages() throws FactoryNotCreatedException {
        if (mGalleryFactory == null) {
            throw new FactoryNotCreatedException();
        } else {
            return mGallery.getPages();
        }
    }


    public static class FactoryNotCreatedException extends Exception {}
}
