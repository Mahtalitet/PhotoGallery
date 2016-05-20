package com.learning.photogallery;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.learning.photogallery.gallery.GalleryItem;

import java.util.ArrayList;

public class PhotoGalleryFragment extends Fragment {
    public static final String TAG = "PhotoGalleryFragment";
    public static final int PHOTO_GALLERY_GRID_COUNT = 2;


    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ArrayList<GalleryItem> mItems;
    private FlickrFetchr mFlickrFetchr;
    private FlickrImageDownloader mFlickrImageDownloader;

    public PhotoGalleryFragment() {
    }

    public static PhotoGalleryFragment newInstance(int columnCount) {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mItems = new ArrayList<>();
        mFlickrFetchr = new FlickrFetchr();
        getItemsFromFlickr();

        mFlickrImageDownloader = new FlickrImageDownloader<ImageView>();
        mFlickrImageDownloader.start();
        mFlickrImageDownloader.getLooper();
        Log.i(TAG, "Background thread started.");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flickrphoto_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            setupRecyclerView(context);
        }

        return view;
    }


    private void setupRecyclerViewPagination(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    getItemsFromFlickr();
                }
            });
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    getItemsFromFlickr();
                }
            });
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    getItemsFromFlickr();
                }
            });
        }
    }

    private void getItemsFromFlickr() {
        new FetchItemsTask().execute();
    }

    private void setupRecyclerView(Context context) {
        if (getActivity() == null || mRecyclerView == null) return;

        setLayoutManager(context);
        setAdapter();
        setupRecyclerViewPagination(mRecyclerView.getLayoutManager());
    }

    private void setLayoutManager(Context context) {
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
    }

    private void setAdapter() {
        mRecyclerView.setAdapter(new MyFlickrPhotoRecyclerViewAdapter(mFlickrImageDownloader, mItems, mListener));
    }

    private void addItemsIntoAdapter() {
        ((MyFlickrPhotoRecyclerViewAdapter) mRecyclerView.getAdapter()).addItems(mItems);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFlickrImageDownloader.quit();
        Log.i(TAG, "Background thread destroyed.");
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            return mFlickrFetchr.fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            Log.i(TAG, "Returned items: "+galleryItems.size());
            mItems = galleryItems;
            if (mItems.size() != 0) {
                updateAdapter();
                mFlickrFetchr.updateCurrentPage();
            }
        }
    }

    private void updateAdapter() {
        if (mFlickrFetchr.getCurrentPage() == 1) {
            setAdapter();
        } else {
            addItemsIntoAdapter();
        }
        mItems = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(GalleryItem item);
    }
}
