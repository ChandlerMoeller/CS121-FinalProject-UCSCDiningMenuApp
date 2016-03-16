package com.cs121.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class DisplayMenuItemDetail extends AppCompatActivity {

    String name;
    String url;
    List<String> tags;
    String ingredients;
    String allergens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final MenuItem thisitem = (MenuItem)intent.getSerializableExtra("menuitem");

        name = thisitem.name;
        if (thisitem.name.contains("&apos;")) {
            name = name.replace("&apos;", "'");
        }
        if (thisitem.name.contains("&amp;")) {
            name = name.replace("&amp;", "&");
        }
        if (thisitem.name.contains("&quot;")) {
            name = name.replace("&quot;", "\"");
        }

        url = thisitem.url;
        tags = thisitem.tags;
        ingredients = thisitem.ingredients;
        allergens = thisitem.allergens;

        if(thisitem.name!=null) {
            setTitle(thisitem.name);
        }


        final CheckBox chk = (CheckBox) findViewById(R.id.menuitem_checkBox);
        DBHandler db = new DBHandler(DisplayMenuItemDetail.this);
        if (db.checkIfFavourite(thisitem.name)) {
            if (chk != null) {
                chk.setChecked(true);
            }
        }
        if (chk != null) {
            chk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Reacts to a button press.
                    // Gets the integer tag of the button.
                    DBHandler db = new DBHandler(DisplayMenuItemDetail.this);
                    if (chk.isChecked()) {
                        //
                        //add to database
                        //
                        db.insertFavouritesItem(thisitem);
                    } else {
                        //
                        //Delete from database
                        //
                        db.deleteFavouritesItem(thisitem);
                    }
                }


            });
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                tags);
        ListView scrollview = (ListView) findViewById(R.id.menuitem_tags);
        if(scrollview!=null) {
            scrollview.setAdapter(adapter);
        }

        TextView textallergen = (TextView)findViewById(R.id.menuitem_allergens);
        if(allergens!=null && textallergen!=null) {
            textallergen.setText(allergens);
        }
        TextView textingredients = (TextView)findViewById(R.id.menuitem_ingredients);
        if(allergens!=null && textingredients!=null) {
            textingredients.setText(ingredients);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void MenuItemButton(View v) {
        Intent browserIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
        }
    }
}
