package com.learning.photogallery;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.learning.photogallery.dummy.DummyContent;

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
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
