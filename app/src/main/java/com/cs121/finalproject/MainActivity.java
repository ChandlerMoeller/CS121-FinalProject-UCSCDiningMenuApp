package com.cs121.finalproject;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.SearchManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

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
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    Retrofit retrofit;
    private List<MenuItem> listmenu;
    private ArrayList<List<MenuItem>> listdaydiningmenu = new ArrayList<List<MenuItem>>(3);
    public ArrayList<ArrayList<List<MenuItem>>> listdayalldiningmenu = new ArrayList<ArrayList<List<MenuItem>>>(5);
    public ArrayList<ArrayList<List<MenuItem>>> favmenus;
    private int[][] retrofitcheck = new int[5][3];
    private pickedDate passDateToSearchActivity = pickedDate.getPickedDate();

    private String name;
    private String url;
    private List<String> tags;
    private String ingredients;
    private String allergens;
    private int numCachedMenus;

    String pickedday;
    String pickedmonth;
    String pickedyear;

    MiddleFragment middlefragment = MiddleFragment.newInstance("regular");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitclear();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor e = settings.edit();
        numCachedMenus = settings.getInt("numCachedMenus", -1);
        if (numCachedMenus == -1) {
            numCachedMenus = 0;
            e.putInt("numCachedMenus", numCachedMenus);
            e.apply();
        }

        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        pickedday = singleinttodoublestring(c.get(Calendar.DAY_OF_MONTH));
        pickedmonth = singleinttodoublestring(c.get(Calendar.MONTH) + 1);
        pickedyear = Integer.toString(c.get(Calendar.YEAR));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mainframe, middlefragment);
        transaction.commit();


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
        GetDaysMenusFromServer(pickedday, pickedmonth, pickedyear, 1);

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
        pickedday = singleinttodoublestring(dayOfMonth);
        pickedmonth = singleinttodoublestring(monthOfYear+1);
        pickedyear = Integer.toString(year);

        Log.d("DATABASE", "ONDATESET");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("bydining", true)) {
            middlefragment.Menusview(0, null, null, null);
            GetDaysMenusFromServer(pickedday, pickedmonth, pickedyear, 0);
        } else if(settings.getBoolean("bymeal", false)) {
            middlefragment.Menusview(1, null, null, null);
            GetDaysMenusFromServer(pickedday, pickedmonth, pickedyear, 0);
        } else if(settings.getBoolean("byfavorites", false)) {
            //
            int[] intarray = new int[15];
            for (int i = 0; i < intarray.length; i++) {
                intarray[i] = 1;
            }
            GetDaysMenusFromServer(pickedday, pickedmonth, pickedyear, 0);
            middlefragment.Menusview(3, intarray, "search", getfavmenus());
        }


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
        int id = item.getItemId();

        if (id == R.id.action_sort_bydining) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = settings.edit();
            e.putBoolean("bymeal", false);
            e.putBoolean("bydining", true);
            e.putBoolean("byfavorites", false);
            e.commit();
            middlefragment.Menusview(0, null, null, null);
            return true;
        }
        if (id == R.id.action_sort_bymeal) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = settings.edit();
            e.putBoolean("bymeal", true);
            e.putBoolean("bydining", false);
            e.putBoolean("byfavorites", false);
            e.commit();
            middlefragment.Menusview(1, null, null, null);
            return true;
        }
        if (id == R.id.action_sort_byfavorites) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor e = settings.edit();
            e.putBoolean("bymeal", false);
            e.putBoolean("bydining", false);
            e.putBoolean("byfavorites", true);
            e.commit();
            int[] intarray = new int[15];
            for (int i = 0; i < intarray.length; i++) {
                intarray[i] = 1;
            }
            middlefragment.Menusview(3, intarray, "search", getfavmenus());
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


    public void GetDaysMenusFromServer(String day, String month, String year, int firstopen) {
        String[] ca = {"CM", "CS", "EO", "NT", "PK"};
        String[] meal = {"BR", "LU", "DI"};

        retrofitclear();

        //
        // if days menu is not on cache
        //
        DBHandler db = new DBHandler(getApplicationContext());
        String dayMonthYear = day + "-" + month + "-" + year;
        if (db.searchCacheForDate(dayMonthYear) == null) {
            Log.d("DATABASE", "CACHE NOT hit");
            int j = 0;
            for (String a : ca) {
                int i = 0;
                for (String b : meal) {
                    String url = "jmenu_" + month + "_" + day + "_" + year + "_" + a + "_" + b + ".json";
                    getjsonfromurl(retrofit, url, i, j, dayMonthYear);
                    i++;
                }
                j++;
            }
        } else {
            listdayalldiningmenu = db.searchCacheForDate(dayMonthYear);
            Log.d("DATABASE", "CACHE hit");
            if(firstopen==1) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (settings.getBoolean("byfavorites", false)) {
                    Log.d("DATABASE", "FAV");
                    int[] intarray = new int[15];
                    for (int i = 0; i < intarray.length; i++) {
                        intarray[i] = 1;
                    }
                    middlefragment.Menusview(3, intarray, "search", getfavmenus());
                }
            }
        }
        //
        //else if it is then get it from the SQLite data base and set listdayalldiningmenu as it
        //
    }


    //This function is called when refreshing
    public void getjsonfromurl(Retrofit retrofit, String url, final int i, final int j, String dayMonthYear2) {
        GetJson service = retrofit.create(GetJson.class);
        final String dayMonthYear = dayMonthYear2;

        //Retrofit stuff
        Call<List<MenuItem>> queryResponseCall =
                service.getMenus(url);


        //Call retrofit asynchronously
        queryResponseCall.enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Response<List<MenuItem>> response) {

                //Update variables with new data
                listdayalldiningmenu.get(j).set(i, response.body());
                retrofitcheck[j][i] = 1;

                if (isretrofitdone()) {
                    Log.d("Retrofit", "Called");
                    //
                    //ADD TO DATABASE HERE
                    //
                    //add listdayalldiningmenu to database
                    DBHandler db = new DBHandler(getApplicationContext());
                    if (numCachedMenus < 2) {
                        passDateToSearchActivity.setDate(dayMonthYear);
                        db.insertCacheOneItem(listdayalldiningmenu, dayMonthYear);
                        Log.e("lmaoooo", "lmaoooooooo");
                        numCachedMenus++;
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor e = settings.edit();
                        e.putInt("numCachedMenus", numCachedMenus);
                        e.apply();
                    } else {
                        passDateToSearchActivity.setDate(dayMonthYear);
                        db.insertCacheOneItem(listdayalldiningmenu, dayMonthYear);
                        Log.e("ayyyy","atttttyyy");
                        db.deleteCacheOneItem();
                    }
                    favmenus = getfavmenus();

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (settings.getBoolean("byfavorites", false)) {
                        String str = "fav";
                        int[] intarray = new int[15];
                        for (int i = 0; i < intarray.length; i++) {
                            intarray[i] = 1;
                        }
                        middlefragment.Menusview(3, intarray, str, getfavmenus());
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("FAILURE", "NOT ON SERVER");
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

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
// Commit the transaction
        int[] pass = whattodisplay(v.getTag().toString());
        middlefragment.Menusview(3, pass, null, listdayalldiningmenu);

        String str = "fav";
        int[] intarray = new int[15];
        for (int i = 0; i < intarray.length; i++) {
            intarray[i] = 1;
        }
    }

    ArrayList<ArrayList<List<MenuItem>>> getmenus() {
        return listdayalldiningmenu;
    }

    ArrayList<ArrayList<List<MenuItem>>> getfavmenus() {
        DBHandler dba = new DBHandler(this);

        ArrayList<ArrayList<List<MenuItem>>> favitems;
        ArrayList<ArrayList<List<MenuItem>>> favhititems = new ArrayList<>(5);
        favhititems.add(new ArrayList<List<MenuItem>>(3));
        favhititems.add(new ArrayList<List<MenuItem>>(3));
        favhititems.add(new ArrayList<List<MenuItem>>(3));
        favhititems.add(new ArrayList<List<MenuItem>>(3));
        favhititems.add(new ArrayList<List<MenuItem>>(3));
        for (ArrayList<List<MenuItem>> v : favhititems) {
            v.add(new ArrayList<MenuItem>());
            v.add(new ArrayList<MenuItem>());
            v.add(new ArrayList<MenuItem>());
        }
        if (dba.searchCacheForDate(passDateToSearchActivity.getDate()) != null) {
            favitems = dba.searchCacheForDate(passDateToSearchActivity.getDate());
        } else {
            favitems = dba.getCacheOneItems().get(0);
        }
        // get favourites to be displayed
        for (MenuItem a : dba.getFavouritesItems()) {
            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 2; j++) {
                        for (int k = 0; k <= favitems.get(i).get(j).size() - 1; k++) {
                            if (favitems.get(i).get(j).get(k).name.toLowerCase().equals(a.name.toLowerCase())) {
                                favhititems.get(i).get(j).add(favitems.get(i).get(j).get(k));
                                Log.d("logging", "" + favhititems.get(i).get(j).get(0).name);
                            }
                        }
                }
            }
        }
        return favhititems;
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
