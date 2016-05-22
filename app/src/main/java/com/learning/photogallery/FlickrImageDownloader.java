package com.learning.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class FlickrImageDownloader<Token> extends HandlerThread {
    private static final String TAG = "FlickrImageDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MAX_CACHE_SIZE = 20;

    Handler mHandler;
    Map<Token, String> requestMap =
            Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;
    LruCache<String, Bitmap> mBitmapCache;

    public interface Listener<Token> {
        void onImageDownloaded(Token token, Bitmap image);

    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }


    public FlickrImageDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mBitmapCache = new LruCache<>(MAX_CACHE_SIZE);
    }

    public void queueImage(Token token, String url) {
        Log.i(TAG, "Got an URL: "+url);

        requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token)
                .sendToTarget();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: "+requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
    }

    private void handleRequest(final Token token) {
        final String url = requestMap.get(token);
        if (url == null) return;

        try {
            final Bitmap bitmap;

            if (mBitmapCache.get(url) == null) {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap =
                        BitmapFactory
                                .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                Log.i(TAG, "Bitmap created.");
                mBitmapCache.put(url, bitmap);
                Log.i(TAG, "Bitmap cached");
            } else {
                bitmap = mBitmapCache.get(url);
                Log.i(TAG, "Bitmap get from cache");
            }

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token) != url) return;

                    requestMap.remove(token);
                    mListener.onImageDownloaded(token, bitmap);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error downloading image.", e);
        }
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

    public void cleareCache() {
        mBitmapCache.evictAll();
    }
}
