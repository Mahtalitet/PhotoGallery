package com.learning.photogallery.gallery;

public class GalleryItem {
    private String mId;
    private String mUrl;
    private String mCaption;
    private String mPageUrl;

    public GalleryItem(String mId, String mUrl, String mCaption, String mPageUrl) {
        this.mId = mId;
        this.mUrl = mUrl;
        this.mCaption = mCaption;
        this.mPageUrl = mPageUrl;
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

    public String getPageUrl() {
        return mPageUrl;
    }

    public void setPageUrl(String pageUrl) {
        mPageUrl = pageUrl;
    }
}
