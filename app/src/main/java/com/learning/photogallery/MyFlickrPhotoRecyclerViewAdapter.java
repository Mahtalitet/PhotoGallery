package com.learning.photogallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.learning.photogallery.gallery.GalleryItem;

import java.util.List;

public class MyFlickrPhotoRecyclerViewAdapter extends RecyclerView.Adapter<MyFlickrPhotoRecyclerViewAdapter.ViewHolder> {

    private final List<GalleryItem> mValues;
    private final PhotoGalleryFragment.OnListFragmentInteractionListener mListener;

    public MyFlickrPhotoRecyclerViewAdapter(List<GalleryItem> items, PhotoGalleryFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_flickrphoto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Glide.with(holder.mImageView.getContext())
                .load(holder.mItem.getmUrl())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.ic_collections_black_24dp)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addItems(List<GalleryItem> items) {
        mValues.addAll(items);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public GalleryItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.image);
        }
    }
}
