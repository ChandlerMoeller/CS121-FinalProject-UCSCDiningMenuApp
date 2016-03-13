package com.cs121.finalproject;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


public class SearchableActivity extends android.support.v4.app.FragmentActivity {


    private pickedDate passDateToSearchActivity = pickedDate.getPickedDate();
    DBHandler db = new DBHandler(this);

    ArrayList<ArrayList<List<MenuItem>>> cacheHitItems = new ArrayList<>(5);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            TextView searchExplanationText = (TextView) findViewById(R.id.searchTextView);
            searchExplanationText.setText("Dining Halls which are currently serving "+ query.toUpperCase() +":");
            ArrayList<ArrayList<List<MenuItem>>> cacheitems;
            String ayy = "";
            String currentDate = passDateToSearchActivity.getDate();
            //ArrayList<ArrayList<List<MenuItem>>> cacheHitItems = new ArrayList<>(5);
            cacheHitItems.add(new ArrayList<List<MenuItem>>(3));
            cacheHitItems.add(new ArrayList<List<MenuItem>>(3));
            cacheHitItems.add(new ArrayList<List<MenuItem>>(3));
            cacheHitItems.add(new ArrayList<List<MenuItem>>(3));
            cacheHitItems.add(new ArrayList<List<MenuItem>>(3));
            for (ArrayList<List<MenuItem>> v : cacheHitItems) {
                v.add(new ArrayList<MenuItem>());
                v.add(new ArrayList<MenuItem>());
                v.add(new ArrayList<MenuItem>());
            }
            //cacheitems = db.getCacheOneItems().get(1);
            if(db.searchCacheForDate(currentDate) != null) {
                cacheitems = db.searchCacheForDate(currentDate);
            }else{
                cacheitems = db.getCacheOneItems().get(0);
            }
            for(int i = 0; i <= 4; i++){
                for(int j = 0; j <= 2; j++){
                    Log.d("cache", "hit");
                  for(int k = 0; k<= cacheitems.get(i).get(j).size()-1; k++) {
                      Log.d("cache", "hit2");
                      if (cacheitems.get(i).get(j).get(k).name.toLowerCase().equals(query.toLowerCase())) {

                         cacheHitItems.get(i).get(j).add(cacheitems.get(i).get(j).get(k));
                          for(MenuItem x : cacheHitItems.get(i).get(j)){
                            if(i == 0) {
                              x.name = "Crown/Merrill during";
                            }else if(i == 1){
                                x.name = "Cowell/Stevenson during";
                            }else if(i == 2){
                                x.name = "Eight/Oakes during";
                            }else if(i == 3){
                                x.name = "Nine/Ten during";
                            }else if(i == 4){
                                x.name = "Porter/Kresge during";
                            }

                              if(j == 0){
                                  x.name += " Breakfast";
                              }else if(j == 1){
                                  x.name += " Lunch";
                              }else if(j == 2){
                                  x.name += " Dinner";
                              }
                          }
                          Log.d("cache", cacheHitItems.get(i).get(j).get(0).name);
                          ayy +=cacheHitItems.get(i).get(j).get(0).name+"\n";
                      }
                  }
                }
            }

            searchExplanationText.setText(ayy);

            FragmentManager fragmentManager3 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();

            //ListFragment fragment3 = new ListFragment();
            String str = "l";
            int[] intarray = new int[15];
            for (int i = 0; i < intarray.length; i++) {
                intarray[i] = 1;
            }

            ListSearchFragment fragment3 = ListSearchFragment.newInstance(intarray, str);
            fragmentTransaction3.replace(R.id.searchListContainer, fragment3);
            //fragmentTransaction3.addToBackStack("test");
            fragmentTransaction3.commit();



        }
    }

    ArrayList<ArrayList<List<MenuItem>>> getsearchmenus() {
        return cacheHitItems;
    }

}
