package com.learning.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.learning.photogallery.gallery.Gallery;
import com.learning.photogallery.gallery.GalleryFactory;
import com.learning.photogallery.gallery.GalleryItem;

import java.util.ArrayList;

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 60 * 5;

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (!BackgroundTester.isNetworkAvailable(this)) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String query = prefs.getString(FlickrFetcher.PREF_SEARCH_QUERY, null);
        String lastResultId = prefs.getString(FlickrFetcher.PREF_LAST_RESULT_ID, null);

        ArrayList<GalleryItem> items;

        if (query != null) {
            items = GalleryFactory
                    .get(Gallery.FetchingType.SEARCH, true, this).returnItemsBy(query);
        } else {
            items = GalleryFactory
                    .get(Gallery.FetchingType.RECENT, true, this).returnItemsBy(null);
        }

        if (items.size() == 0) return;

        String resultId = items.get(0).getId();

        if (!resultId.equals(lastResultId)) {
            Log.i(TAG, "Get a new result: "+resultId);

            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, PhotoGalleryActivity.class), 0);

            NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.new_pictures_title))
                    .setContentText(getResources().getString(R.string.new_pictures_text))
                    .setSmallIcon(R.drawable.ic_photo)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setContentIntent(pi);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationCompatBuilder.build());


        } else {
            Log.i(TAG, "Get an old result: "+resultId);
        }

        prefs.edit()
                .putString(FlickrFetcher.PREF_LAST_RESULT_ID, resultId)
                .apply();

        Log.i(TAG, "Received an intent "+intent);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), POLL_INTERVAL, pi);
            Toast.makeText(context, R.string.toast_polling_start, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Polling started.");
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
            Toast.makeText(context, R.string.toast_polling_stop, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Polling stopped.");
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);

        return pi != null;
    }

}
