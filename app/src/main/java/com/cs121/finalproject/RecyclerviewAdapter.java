package com.cs121.finalproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//Base setup by Google Developer Example: http://developer.android.com/training/material/lists-cards.html#RecyclerView
public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {
    private ArrayList<ArrayList<List<MenuItem>>> allmenus;
    Context mContext;
    int mResource;
    int[] mintarry;
    String mStringCommand;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ListView mListView;

        public ViewHolder(ListView v) {
            super(v);
            mListView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerviewAdapter(Context _context, int[] intarry, String _string, int _resource, ArrayList<ArrayList<List<MenuItem>>> myDataset) {
        allmenus = myDataset;
        mContext = _context;
        mResource = _resource;
        mintarry = intarry;
        mStringCommand = _string;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_element, parent, false);
        ListView h = (ListView) v.findViewById(R.id.recyclerview_scrollview);
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
        adapt(holder.mListView, position);
        //holder.mListView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int howmanyadded = 0;
        if (mintarry != null) {
            for (int i = 0; i < mintarry.length; i++) {
                if (mintarry[i] == 1) {
                    howmanyadded++;
                }
            }
        }

        //return allmenus.size();
        return howmanyadded;
    }


    public void adapt(View l, int position) {
        //allmenus = ((MainActivity) getActivity()).getmenus();
        String namedininghall = " ";
        String namemeal = " ";

        int howmanyadded = 1;
        if (mintarry != null) {
            for (int i = 0; i < mintarry.length; i++) {
                int j = -1;
                int k = -1;
                if (mintarry[i] == 1) {
                    if (0 <= i && i <= 2) {
                        //Crown/Merrill
                        j = 0;
                        namedininghall = "Crown/Merrill";
                    }
                    if (3 <= i && i <= 5) {
                        //Cowell/Stevenson
                        j = 1;
                        namedininghall = "Cowell/Stevenson";
                    }
                    if (6 <= i && i <= 8) {
                        //Eight/Oakes
                        j = 2;
                        namedininghall = "Eight/Oakes";
                    }
                    if (9 <= i && i <= 11) {
                        //Nine/Ten
                        j = 3;
                        namedininghall = "Nine/Ten";
                    }
                    if (12 <= i && i <= 15) {
                        //PorterKresge
                        j = 4;
                        namedininghall = "Porter/Kresge";
                    }
                    if ((i + 1) % 3 == 1) {
                        //Breakfast
                        k = 0;
                        namemeal = "Breakfast";
                    }
                    if ((i + 1) % 3 == 2) {
                        //Lunch
                        k = 1;
                        namemeal = "Lunch";
                    }
                    if ((i + 1) % 3 == 0) {
                        //Dinner
                        k = 2;
                        namemeal = "Dinner";
                    }

                    //if (position == howmanyadded-1) {

                        //resultlist = allmenus.get(j).get(k);
                        List<MenuItem> resultlist = allmenus.get(j).get(k);
                        int scrollviewid = -1;

                        if (resultlist != null) {
                            if (!resultlist.isEmpty()) {

                                if (!resultlist.get(0).name.equals(namedininghall)) {
                                    MenuItem tmp = new MenuItem();

                                    tmp.name = namedininghall;
                                    tmp.allergens = namemeal;
                                    resultlist.add(0, tmp);
                                }

                                int element = R.layout.list_element;
                                if (mStringCommand != null) {
                                    if (mStringCommand.equals("search") || mStringCommand.equals("fav")) {
                                        element = R.layout.list_element_no_checkbox;
                                    }
                                }
                                ListAdapter adapter2 = new ListAdapter(mContext, element, R.layout.list_meal_header, resultlist, R.layout.list_dining_header, "test");
                                ListView scrollview = (ListView) l.findViewById(R.id.recyclerview_scrollview);
                                scrollview.setAdapter(adapter2);
                                adapter2.notifyDataSetChanged();


                                int howmanyitems = resultlist.size();
                                int howbig = howmanyitems * (int) l.getResources().getDimension(R.dimen.item_height) + (int) l.getResources().getDimension(R.dimen.item_height_addition);

                                Log.d("big", "" + howmanyitems);


                                TextView txt = (TextView) l.findViewById(R.id.listtextview);
                                txt.setVisibility(l.GONE);

                                //Set the heigh programmatically
                                //
                                //Used ideas from http://stackoverflow.com/questions/7441696/how-to-change-list-views-height-progmmatically
                                //
                                ViewGroup.LayoutParams params = scrollview.getLayoutParams();
                                params.height = howbig;
                                scrollview.setLayoutParams(params);
                                scrollview.requestLayout();
                            }
                        }
                    //}






                /*if (resultlist != null) {
                    adapter2 = new ListAdapter(getContext(), R.layout.list_element, R.layout.list_meal_header, allmenus.get(3).get(2), R.layout.list_dining_header, "test");
                    scrollview = (ListView) l.findViewById(R.id.scrollview2);
                    scrollview.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();
                }*/

                    howmanyadded++;
                }
            }
        }


    }


}
