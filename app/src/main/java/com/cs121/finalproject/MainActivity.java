package com.cs121.finalproject;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.SearchManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;

import android.widget.Toast;

import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

public class MainActivity extends AppCompatActivity implements
        CalendarDatePickerDialogFragment.OnDateSetListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    Retrofit retrofit;
    private List<MenuItem> listmenu;
    private ArrayList<List<MenuItem>> listdaydiningmenu = new ArrayList<List<MenuItem>>(3);
    public ArrayList<ArrayList<List<MenuItem>>> listdayalldiningmenu = new ArrayList<ArrayList<List<MenuItem>>>(5);
    private int[][] retrofitcheck = new int[5][3];
    private pickedDate passDateToSearchActivity = pickedDate.getPickedDate();

    private String name;
    private String url;
    private List<String> tags;
    private String ingredients;
    private String allergens;
    //----------------------------------------------------------------------------------------------
    private int numCachedMenus = 0;
    //----------------------------------------------------------------------------------------------

    String pickedday;
    String pickedmonth;
    String pickedyear;

    MiddleFragment testfrag = new MiddleFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitclear();

        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        pickedday = singleinttodoublestring(c.get(Calendar.DAY_OF_MONTH));
        pickedmonth = singleinttodoublestring(c.get(Calendar.MONTH)+1);
        pickedyear = Integer.toString(c.get(Calendar.YEAR));
        Log.d("lol", pickedmonth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        //Retrofit Stuff
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://chandlermoeller.me/")
                .addConverterFactory(GsonConverterFactory.create())    //parse Gson string
                .client(httpClient)    //add logging
                .build();
        //End of Retrofit stuff

        //Get data on startup
        GetDaysMenusFromServer(pickedday, pickedmonth, pickedyear);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(MainActivity.this);
                cdp.show(getSupportFragmentManager(), "fragment_date_picker_name");

            }
        });

    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        //mResultTextView.setText(getString(R.string.calendar_date_picker_result_values, year, monthOfYear, dayOfMonth));
        pickedday = singleinttodoublestring(dayOfMonth);
        pickedmonth = singleinttodoublestring(monthOfYear);
        pickedyear = Integer.toString(year);
        Log.d("DateSet", "day: " + pickedday+" month: "+pickedmonth);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("bydining", true)) {
            testfrag.Menusview(0, null, null);
        } else {
            testfrag.Menusview(1, null, null);
        }

        GetDaysMenusFromServer(pickedday, pickedmonth, pickedyear);
        //Log.d("lol", listdayalldiningmenu.get(3).get(2).get(0).name);
    }

    String singleinttodoublestring(int integer) {
        if (integer < 10) {
            return ("0" + Integer.toString(integer));
        }
        return Integer.toString(integer);
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag("fragment_date_picker_name");
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }
    }

    //credit goes to http://stackoverflow.com/questions/12022715/unable-to-show-keyboard-automatically-in-the-searchview
    //for showInputMethod() and the below searchview listener
    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view.findFocus());
                }
            }
        });

        //credit goes to http://stackoverflow.com/questions/25930380/android-search-widgethow-to-hide-the-close-button-in-search-view-by-default
        //for code which gets rid of searchview close button
        ImageView searchCloseButton = null;
        try {
            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            searchCloseButton = (ImageView) searchField.get(searchView);
        } catch (Exception e) {
            Log.e("search error", "Error finding close button", e);
        }

        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        if (searchCloseButton != null) {
            searchCloseButton.setEnabled(false);
            searchCloseButton.setImageDrawable(transparentDrawable);
        }

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}
        if (id == R.id.action_sort_bydining) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = settings.edit();
            e.putBoolean("bydining", true);
            e.commit();
            testfrag.Menusview(0, null, null);
            return true;
        }
        if (id == R.id.action_sort_bymeal) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = settings.edit();
            e.putBoolean("bydining", false);
            e.commit();
            testfrag.Menusview(1, null, null);
            return true;
        }
        if (id == R.id.search) {
            SearchView searchView =
                    (SearchView) item.getActionView();
            searchView.setIconified(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return testfrag;
                case 1:
                    return new ListFragment();
            }
            return new ByDiningHallFragment();
        }


        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Menus";
                case 1:
                    return "Favorites";
            }
            return null;
        }
    }

    public void GetDaysMenusFromServer(String day, String month, String year) {
        String[] ca = {"CM", "CS", "EO", "NT", "PK"};
        String[] meal = {"BR", "LU", "DI"};

        retrofitclear();

        //
        // if days menu is not on cache
        //
        DBHandler db = new DBHandler(getApplicationContext());
        String dayMonthYear = day + "-" + month + "-" + year;
        if(db.searchCacheForDate(dayMonthYear) == null) {
            int j = 0;
            for (String a : ca) {
                int i = 0;
                for (String b : meal) {
                    String url = "jmenu_" + month + "_" + day + "_" + year + "_" + a + "_" + b + ".json";
                    getjsonfromurl(retrofit, url, i, j);
                    i++;
                }
                j++;
            }
        }else{
            listdayalldiningmenu = db.searchCacheForDate(dayMonthYear);
        }
        //
        //else if it is then get it from the SQLite data base and set listdayalldiningmenu as it
        //
    }


    //This function is called when refreshing
    public void getjsonfromurl(Retrofit retrofit, String url, final int i, final int j) {
        GetJson service = retrofit.create(GetJson.class);

        //Retrofit stuff
        Call<List<MenuItem>> queryResponseCall =
                service.getMenus(url);


        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Response<List<MenuItem>> response) {
                //View parentView = findViewById(R.id.mainrelativelayout);
                if (response.body().isEmpty()) {
                    //Snackbar for Server Error
                    /*Toast toast = Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();*/
                    return;
                }
                if (!response.body().isEmpty()) {
                    //Toast for on success
                    /*Toast toast = Toast.makeText(MainActivity.this, "Content Refreshed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();*/

                    //Update variables with new data
                    //resultlist = response.body().resultList;
                    //listdayalldiningmenu.set(j, locallistdaydiningmenu);
                    listdayalldiningmenu.get(j).set(i, response.body());
                    retrofitcheck[j][i] = 1;

                    if (isretrofitdone()) {
                        //
                        //ADD TO DATABASE HERE
                        //
                        //add listdayalldiningmenu to database

                        //------------------------------------------------------------------------------
                        //passedMenuList.setMenuList(response.body());

                        String dayMonthYear = pickedday + "-" + pickedmonth + "-" + pickedyear;
                        DBHandler db = new DBHandler(getApplicationContext());
                        if(numCachedMenus < 2) {
                            db.insertCacheOneItem(listdayalldiningmenu, dayMonthYear);
                            passDateToSearchActivity.setDate(dayMonthYear);
                            numCachedMenus++;
                        }else {
                            db.insertCacheOneItem(listdayalldiningmenu, dayMonthYear);
                            passDateToSearchActivity.setDate(dayMonthYear);
                            db.deleteCacheOneItem();
                        }
                        //Toast toast2 = Toast.makeText(MainActivity.this, db.getCacheOneItems().get(1).get(2).get(0).name, Toast.LENGTH_LONG);
                        //toast2.setGravity(Gravity.TOP, 0, 0);
                        //toast2.show();
                        //MainActivity.this.deleteDatabase("DiningMenuDB.db");
                        //db.insertFavouritesItem(listmenu.get(0));
                        //db.deleteFavouritesItem(listmenu.get(0));
                        //Toast toast2 = Toast.makeText(MainActivity.this, db.getFavouritesItems().get(0).name, Toast.LENGTH_LONG);
                        //toast2.setGravity(Gravity.TOP, 0, 0);
                        //toast2.show();
                        //------------------------------------------------------------------------------

                        //clear listdayalldiningmenu and retrofitcheck

                        //retrofitclear();
                    }


                    //Adapter stuff for the listview
                    /*adapter2 = new MyAdapter(ChatActivity.this, R.layout.list_element_yourmessage, R.layout.list_element_mymessage, resultlist, client_userId);
                    scrollview = (ListView) findViewById(R.id.scrollview);
                    scrollview.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();*/

                } else {
                    //Snackbar for Other Error
                    Toast toast = Toast.makeText(MainActivity.this, "Other", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                    return;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Log error here since request failed
                String p = Log.getStackTraceString(t);
                Log.e("Error", p);
                Log.e("Error", "Failure");
            }

        });
    }


    public boolean isretrofitdone() {
        for (int[] aRetrofitcheck : retrofitcheck) {
            for (int bRetrofitcheck : aRetrofitcheck) {
                if (bRetrofitcheck != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public void retrofitclear() {
        listdayalldiningmenu.clear();

        listdayalldiningmenu.add(new ArrayList<List<MenuItem>>(3));
        listdayalldiningmenu.add(new ArrayList<List<MenuItem>>(3));
        listdayalldiningmenu.add(new ArrayList<List<MenuItem>>(3));
        listdayalldiningmenu.add(new ArrayList<List<MenuItem>>(3));
        listdayalldiningmenu.add(new ArrayList<List<MenuItem>>(3));
        for (ArrayList<List<MenuItem>> v : listdayalldiningmenu) {
            v.add(listmenu);
            v.add(listmenu);
            v.add(listmenu);
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                retrofitcheck[i][j] = 0;
            }
        }
    }

    public interface GetJson {
        @GET("menuoutputdetailed/{url}")
        Call<List<MenuItem>> getMenus(@Path("url") String url);
    }

    public void test(View v) {
        Log.e("test", "test");
        //mSectionsPagerAdapter.getItem(0);
        //Fragment newFragment = new ListFragment();
        //android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
        //transaction.replace(R.id.container, newFragment);
        //transaction.addToBackStack(null);
// Commit the transaction
        //transaction.commit();
        int[] pass = whattodisplay(v.getTag().toString());
        testfrag.Menusview(3, pass, null);


        //testfrag.Menusview();
        //mSectionsPagerAdapter.notifyDataSetChanged();
    }

    ArrayList<ArrayList<List<MenuItem>>> getmenus() {
        return listdayalldiningmenu;
    }



    public int[] whattodisplay(String tag) {
        int[] whattodisplay = new int[15];
        switch (tag) {
            case "1":
                //Crown/Merrill
                whattodisplay[0] = 1;
                whattodisplay[1] = 1;
                whattodisplay[2] = 1;
                break;
            case "2":
                //Nine/Ten
                whattodisplay[9] = 1;
                whattodisplay[10] = 1;
                whattodisplay[11] = 1;
                break;
            case "3":
                //Eight/Oakes
                whattodisplay[6] = 1;
                whattodisplay[7] = 1;
                whattodisplay[8] = 1;
                break;
            case "4":
                //Cowell/Stevenson
                whattodisplay[3] = 1;
                whattodisplay[4] = 1;
                whattodisplay[5] = 1;
                break;
            case "5":
                //Porter/Kresge
                whattodisplay[12] = 1;
                whattodisplay[13] = 1;
                whattodisplay[14] = 1;
                break;
            case "6":
                //Breakfast
                whattodisplay[0] = 1;
                whattodisplay[3] = 1;
                whattodisplay[6] = 1;
                whattodisplay[9] = 1;
                whattodisplay[12] = 1;
                break;
            case "7":
                //Lunch
                whattodisplay[1] = 1;
                whattodisplay[4] = 1;
                whattodisplay[7] = 1;
                whattodisplay[10] = 1;
                whattodisplay[13] = 1;
                break;
            case "8":
                //Dinner
                whattodisplay[2] = 1;
                whattodisplay[5] = 1;
                whattodisplay[8] = 1;
                whattodisplay[11] = 1;
                whattodisplay[14] = 1;
                break;
        }

        return whattodisplay;
    }


}
