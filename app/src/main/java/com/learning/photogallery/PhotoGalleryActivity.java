package com.learning.photogallery;

import android.support.v4.app.Fragment;

import com.learning.photogallery.gallery.GalleryContent;
import com.learning.photogallery.gallery.GalleryItem;

public class PhotoGalleryActivity extends SingleFragmentActivity implements PhotoGalleryFragment.OnListFragmentInteractionListener {


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

    }
}
