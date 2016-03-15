package com.cs121.finalproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Chandler on 3/15/2016.
 */
//Base setup by Google Developer Example: http://developer.android.com/training/material/lists-cards.html#RecyclerView
public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {
    private List<MenuItem> mDataset;
    Context mContext;
    int mResource;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ListView mListView;
        public ViewHolder(ListView v) {
            super(v);
            mListView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerviewAdapter(Context _context, int _resource, List<MenuItem> myDataset) {
        mDataset = myDataset;
        mContext = _context;
        mResource = _resource;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_element, parent, false);
        ListView h = (ListView)v.findViewById(R.id.recyclerview_scrollview);
        // set the view's size, margins, paddings and layout parameters
        //...
        ViewHolder vh = new ViewHolder(h);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        /*ListAdapter adapter2 = new ListAdapter(mContext, mResource, R.layout.list_meal_header, mDataset, R.layout.list_dining_header, "test");
        ListView scrollview = (ListView) holder.mListView.findViewById(R.id.recyclerview_scrollview);
        scrollview.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();*/
        //holder.mListView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
