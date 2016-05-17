package com.learning.photogallery.gallery;

public class GalleryItem {
    public String mId;
    public String mUrl;
    public String mCaption;

    public GalleryItem(String mId, String mUrl, String mCaption) {
        this.mId = mId;
        this.mUrl = mUrl;
        this.mCaption = mCaption;
    }

    @Override
    public String toString() {
        return mCaption;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmCaption() {
        return mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }
}
