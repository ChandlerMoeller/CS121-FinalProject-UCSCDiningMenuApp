package com.cs121.finalproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Modified code by shobhit on 1/24/16.
 * Which is Copied from Prof. Luca class code
 */
public class ListAdapter extends ArrayAdapter<MenuItem> {

    int resource;
    int resource_meal_header;
    int resource_dining_header;
    Context context;
    String client_userId;

    public ListAdapter(Context _context, int _resource, int _resourcemealheader, List<MenuItem> items, int _resourcediningheader, String _client_userId) {
        super(_context, _resource, _resourcemealheader, items);
        resource_meal_header = _resourcemealheader;
        resource_dining_header = _resourcediningheader;
        resource = _resource;
        context = _context;
        client_userId = _client_userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout newView;

        final MenuItem w = getItem(position);
        String itemname = w.name;

        // Inflate a new view always.
        newView = new LinearLayout(getContext());
        String inflater = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
        //if (!client_userId.equals(itemname)) {
        if (position == 0) {
            vi.inflate(resource_dining_header, newView, true);
        } else {
            vi.inflate(resource, newView, true);
        }

        // Fills in the view.
        TextView b = (TextView) newView.findViewById(R.id.element_name);
        //tv.setText(w.name);
        String fixedname = w.name;


        b.setText(w.name);

        //Fixes the text output
        if (w.name != null) {
            fixedname = w.name;
            if (w.name.contains("&apos;")) {
                fixedname = fixedname.replace("&apos;", "'");
            }
            if (w.name.contains("&amp;")) {
                fixedname = fixedname.replace("&amp;", "&");
            }
            if (w.name.contains("&quot;")) {
                fixedname = fixedname.replace("&quot;", "\"");
            }

            b.setText(fixedname);

            //if (element != null) {
            if (fixedname.equals("Farm Fridays")) {
                b.setTypeface(null, Typeface.BOLD);
                b.setTextColor(Color.RED);
                ////holder.LinearLayoutView.setBackgroundColor(ContextCompat.getColor(context, R.color.dining_greensubbar));
            }
            if (fixedname.equals("Healthy Mondays")) {
                b.setTypeface(null, Typeface.BOLD);
                b.setTextColor(Color.RED);
                ////holder.LinearLayoutView.setBackgroundColor(ContextCompat.getColor(context, R.color.dining_greensubbar));
            }
            //}

        }


        if (position == 0) {
            TextView c = (TextView) newView.findViewById(R.id.element_meal);
            c.setText(w.allergens);
            b.setTypeface(null, Typeface.BOLD);
            c.setTypeface(null, Typeface.BOLD);
        } else {
            final CheckBox chk = (CheckBox) newView.findViewById(R.id.checkBox);

            //
            //Check if item is in database already
            //if it is in it, then set chk.setChecked(true);
            //

            //TODO:UNDO COMMENT
            /*DBHandler db = new DBHandler(context);
            if (db.checkIfFavourite(w.name)) {
                if (chk != null) {
                    chk.setChecked(true);
                }
            }*/

            // Sets a listener for the button, and a tag for the button as well.
            //chk.setTag(new Integer(position));
            if (chk != null) {
                chk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Reacts to a button press.
                        // Gets the integer tag of the button.
                        //String s = v.getTag().toString();
                        //int duration = Toast.LENGTH_SHORT;
                        //Toast toast = Toast.makeText(context, s, duration);
                        //toast.show();
                        DBHandler db = new DBHandler(context);
                        if (chk.isChecked()) {
                            //
                            //add to database
                            //
                            db.insertFavouritesItem(w);
                        } else {
                            //
                            //Delete from database
                            //
                            db.deleteFavouritesItem(w);
                        }
                    }


                });
            }
        }

        // Set a listener for the whole list item.
        newView.setTag(R.string.one, w.name);

        final int position2 = position;

        //newView.setTag(R.string.two, w.nickname);
        newView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position2 != 0) {

                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, w.name, duration);
                    toast.show();
                    Intent intent = new Intent(context, DisplayMenuItemDetail.class);
                    intent.putExtra("menuitem", w);
                    context.startActivity(intent);
                    //TODO; add dialiog

                }

            }
        });

        return newView;
    }

}