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

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }
}
