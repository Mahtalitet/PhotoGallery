package com.learning.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.learning.photogallery.gallery.Gallery;
import com.learning.photogallery.gallery.GalleryItem;

public class PhotoGalleryActivity extends SingleFragmentActivity implements PhotoGalleryFragment.OnListFragmentInteractionListener {

    private static final String TAG = "PhotoGalleryActivity";

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance(PhotoGalleryFragment.PHOTO_GALLERY_GRID_COUNT);
    }

    @Override
    protected int getLayoutResuorceId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragmentContainer;
    }

    @Override
    public void onListFragmentInteraction(GalleryItem item) {
        Log.i(TAG, "Clicked "+item.getmId());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment fragment = (PhotoGalleryFragment) getSupportFragmentManager().findFragmentById(getFragmentContainerId());

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(FlickrFetcher.PREF_SEARCH_QUERY, query)
                    .commit();
            Log.i(TAG, "Recived a new search query:" );
        }

        fragment.getItemsFromFlickr(Gallery.FetchingType.SEARCH, true);
    }

}
