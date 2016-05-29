package com.learning.photogallery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BackgroundTester {

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //noinspection deprecation
        return cm.getBackgroundDataSetting() &&
                (activeNetwork != null && activeNetwork.isConnected());
    }
}
