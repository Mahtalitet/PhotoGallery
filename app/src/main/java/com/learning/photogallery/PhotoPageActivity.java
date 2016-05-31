package com.learning.photogallery;

import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }

    @Override
    protected int getLayoutResuorceId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragmentContainer;
    }
}
