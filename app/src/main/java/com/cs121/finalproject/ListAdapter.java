package com.cs121.finalproject;

import android.content.Context;
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

        MenuItem w = getItem(position);
        String itemname = w.name;

        // Inflate a new view always.
        newView = new LinearLayout(getContext());
        String inflater = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
        //if (!client_userId.equals(itemname)) {
        if(position == 0) {
            vi.inflate(resource_dining_header, newView, true);
        } else {
            vi.inflate(resource, newView, true);
        }
        //} else {
            //vi.inflate(resource_meal_header, newView, true);
        //}
        //vi.inflate(resource_dining_header, newView, true);

        // Fills in the view.
        //TextView tv = (TextView) newView.findViewById(R.id.message_message);
        TextView b = (TextView) newView.findViewById(R.id.element_name);
        //tv.setText(w.name);
        b.setText(w.name);



        if(position == 0) {
            TextView c = (TextView) newView.findViewById(R.id.element_meal);
            c.setText(w.allergens);
        } else {

            final CheckBox chk = (CheckBox) newView.findViewById(R.id.checkBox);
            // Sets a listener for the button, and a tag for the button as well.
            chk.setTag(new Integer(position));
            //TODO: if favoite add favorite tag
            chk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Reacts to a button press.
                    // Gets the integer tag of the button.
                    String s = v.getTag().toString();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, s, duration);
                    toast.show();
                    if(chk.isChecked()) {
                        //
                        //TODO: Chris, add to database here
                        //
                    } else {
                        //
                        //TODO: Chris, delete from database here
                        //
                    }
                }
            });
        }

        // Set a listener for the whole list item.
        newView.setTag(R.string.one, w.name);
        //TODO: if favoite add favorite tag

        //newView.setTag(R.string.two, w.nickname);
        newView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                if (settings.getBoolean("spoofmode_switch", true)) {
                    SharedPreferences.Editor e = settings.edit();
                    e.putString("spoofed_user_id", v.getTag(R.string.one).toString());
                    e.putString("spoofed_username", v.getTag(R.string.two).toString());
                    e.commit();

                    String s = v.getTag(R.string.one).toString();
                    String s2 = v.getTag(R.string.two).toString();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, s2+" | "+s, duration);
                    toast.show();
                }*/
            }
        });

        return newView;
    }


}