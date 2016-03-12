package com.cs121.finalproject;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment;

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
    private ArrayList<ArrayList<List<MenuItem>>> listdayalldiningmenu = new ArrayList<ArrayList<List<MenuItem>>>(5);
    private int[][] retrofitcheck = new int[5][3];

    private String name;
    private String url;
    private List<String> tags;
    private String ingredients;
    private String allergens;
    Fragment frag = new ByDiningHallFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitclear();

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
        GetDaysMenusFromServer("11", "03", "2016");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(MainActivity.this);
                cdp.show(getSupportFragmentManager(), "fragment_date_picker_name");

                GetDaysMenusFromServer("11", "03", "2016");
            }
        });

    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        //mResultTextView.setText(getString(R.string.calendar_date_picker_result_values, year, monthOfYear, dayOfMonth));

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_sort_bydining) {
            return true;
        }
        if (id == R.id.action_sort_bymeal) {

//            Intent intent2 = new Intent(this, MainActivity.class);
//            startActivity(intent2);

            return true;
        }
        if (id == R.id.action_search) {
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
                    return new ByDiningHallFragment();
                case 1:
                    return new ByDiningHallFragment();
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

        ArrayList<List<MenuItem>> locallistdaydiningmenu = new ArrayList<List<MenuItem>>(3);
        ArrayList<ArrayList<List<MenuItem>>> locallistdayalldiningmenu = new ArrayList<ArrayList<List<MenuItem>>>(5);

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
                    Toast toast = Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
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
                        //TODO: CHRIS ADD TO DATABASE HERE
                        //
                        //add listdayalldiningmenu to database

                        //------------------------------------------------------------------------------
                        //passedMenuList.setMenuList(response.body());

                        DBHandler db = new DBHandler(getApplicationContext());
                        //MainActivity.this.deleteDatabase("DiningMenuDB.db");
                        //db.insertFavouritesItem(listmenu.get(0));
                        //db.deleteFavouritesItem(listmenu.get(0));
                        //Toast toast2 = Toast.makeText(MainActivity.this, db.getFavouritesItems().get(0).name, Toast.LENGTH_LONG);
                        //toast2.setGravity(Gravity.TOP, 0, 0);
                        //toast2.show();
                        //------------------------------------------------------------------------------

                        //clear listdayalldiningmenu and retrofitcheck
                        retrofitclear();
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

        for (int i=0; i<5; i++) {
            for (int j=0; j<3; j++) {
                retrofitcheck[i][j] = 0;
            }
        }
    }

    public interface GetJson {
        @GET("menuoutputdetailed/{url}")
        Call<List<MenuItem>> getMenus(@Path("url") String url);
    }
}
