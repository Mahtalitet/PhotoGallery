package com.learning.photogallery;

import android.os.HandlerThread;
import android.util.Log;

class FlickrImageDownloader<Token> extends HandlerThread {
    private static final String TAG = "FlickrImageDownloader";

    public FlickrImageDownloader() {
        super(TAG);
    }

    public void queueImage(Token token, String url) {
        Log.i(TAG, "Got an URL: "+url);
    }
}
