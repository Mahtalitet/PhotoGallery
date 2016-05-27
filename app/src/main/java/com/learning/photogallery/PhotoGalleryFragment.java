package com.learning.photogallery;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learning.photogallery.gallery.Gallery;
import com.learning.photogallery.gallery.GalleryFactory;
import com.learning.photogallery.gallery.GalleryItem;
import java.util.ArrayList;

public class PhotoGalleryFragment extends Fragment {
    public static final String TAG = "PhotoGalleryFragment";
    public static final int PHOTO_GALLERY_GRID_COUNT = 2;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private Gallery.FetchingType currentType;

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
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putString(FlickrFetcher.PREF_SEARCH_QUERY, null)
                .commit();
        getItemsFromFlickr(Gallery.FetchingType.RECENT, true);
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
                    updateItems();
                }
            });
        } else if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    updateItems();
                }
            });
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    updateItems();
                }
            });
        }
    }

    private void updateItems() {
        if (currentType == Gallery.FetchingType.RECENT) {
            getItemsFromFlickr(Gallery.FetchingType.RECENT, false);
        } else {
            getItemsFromFlickr(Gallery.FetchingType.SEARCH, false);
        }
    }

    public void getItemsFromFlickr(Gallery.FetchingType requestType, boolean isFirstGet) {
        new FetchItemsTask(isFirstGet).execute(requestType);
    }

    private void setupRecyclerView(Context context) {
        if (getActivity() == null || mRecyclerView == null) return;

        setLayoutManager(context);
        setAdapter(new ArrayList<GalleryItem>());
        setupRecyclerViewPagination(mRecyclerView.getLayoutManager());
    }

    private void setLayoutManager(Context context) {
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
    }

    private void setAdapter(ArrayList<GalleryItem> items) {
        mRecyclerView.setAdapter(new MyFlickrPhotoRecyclerViewAdapter(items, mListener));
    }

    private void addItemsIntoAdapter(ArrayList<GalleryItem> items) {
        ((MyFlickrPhotoRecyclerViewAdapter) mRecyclerView.getAdapter()).addItems(items);
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

    private class FetchItemsTask extends AsyncTask<Gallery.FetchingType, Void, ArrayList<GalleryItem>> {

        private boolean isFirst;

        public FetchItemsTask(boolean isFirst) {
            this.isFirst = isFirst;
        }

        @Override
        protected ArrayList<GalleryItem> doInBackground(Gallery.FetchingType... params) {
            Activity activity = getActivity();

            if (activity == null) return new ArrayList<GalleryItem>();

            String query = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(FlickrFetcher.PREF_SEARCH_QUERY, null);
            setCurrentTypeFrom(query);

            return GalleryFactory.get(params[0], isFirst, getContext()).returnItemsBy(query);
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            Log.i(TAG, "Returned items: "+galleryItems.size());

            if (galleryItems.size() != 0) {
                updateAdapter(galleryItems, isFirst);
            }
            try {
                GalleryFactory.get().makeToastAboutPagesCount();
            } catch (GalleryFactory.FactoryNotCreatedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAdapter(ArrayList<GalleryItem> items, boolean isFirst) {
        try {
            if (((GalleryFactory.get().getGalleryPages()) == 1) || isFirst) {
                setAdapter(items);
            } else {
                addItemsIntoAdapter(items);
            }
        } catch (GalleryFactory.FactoryNotCreatedException e) {
            e.printStackTrace();
        }
    }

    private void setCurrentTypeFrom(String query) {
        if (query != null) {
            currentType = Gallery.FetchingType.SEARCH;
        } else {
            currentType = Gallery.FetchingType.RECENT;
        }
    }

    public void clearAdapter() {
        ((MyFlickrPhotoRecyclerViewAdapter) mRecyclerView.getAdapter()).clearItems();
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(GalleryItem item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchItem = menu.findItem(R.id.menu_item_search);
            SearchView searchView = (SearchView) searchItem.getActionView();

            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            ComponentName name = getActivity().getComponentName();
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(name);

            searchView.setSearchableInfo(searchableInfo);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (!PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.start_polling);
            toggleItem.setIcon(R.drawable.ic_sync);
        } else {
            toggleItem.setTitle(R.string.stop_polling);
            toggleItem.setIcon(R.drawable.ic_sync_disabled);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_search:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    getActivity().onSearchRequested();
                }
                return true;
            case R.id.menu_item_clear:
                synchronized (this) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putString(FlickrFetcher.PREF_SEARCH_QUERY, null)
                            .commit();
                    clearAdapter();
                    getItemsFromFlickr(Gallery.FetchingType.RECENT, true);
                }
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
