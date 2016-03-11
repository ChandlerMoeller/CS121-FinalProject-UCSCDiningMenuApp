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

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

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
        contentRefresh(retrofit);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                contentRefresh(retrofit);
            }
        });

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
                case 0: return new ByDiningHallFragment();
                case 1: return new ByDiningHallFragment();
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


    //This function is called when refreshing
    public void contentRefresh(Retrofit retrofit) {
        GetJson service = retrofit.create(GetJson.class);

        double latitude = 1;
        //Retrofit stuff
        Call<List<MenuItem>> queryResponseCall =
                service.getWeather(latitude);


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
                    Toast toast = Toast.makeText(MainActivity.this, "Content Refreshed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();

                    //Update variables with new data
                    //resultlist = response.body().resultList;
                    listmenu = response.body();

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


    public interface GetJson {
        @GET("menuoutputdetailed/jmenu_01_05_2016_CM_LU.json")
        Call<List<MenuItem>> getWeather(@Query("lat") double latitude);
    }
}
