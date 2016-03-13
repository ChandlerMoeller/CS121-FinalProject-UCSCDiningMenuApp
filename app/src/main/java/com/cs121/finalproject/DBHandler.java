package com.cs121.finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;

import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DBHandler extends SQLiteOpenHelper {

    // some of this code was modified from code found at
    // http://stackoverflow.com/questions/23577825/android-save-object-as-blob-in-sqlite
    // http://instinctcoder.com/android-studio-sqlite-database-example/
    // http://www.techotopia.com/index.php/An_Android_Studio_SQLite_Database_Tutorial
    // and http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/


    int cachedMenuNum = 0;
    Context context;
    private pickedDate passDateToSearchActivity = pickedDate.getPickedDate();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DiningMenuDB.db";
    private static final String TABLE_FAVOURITES = "Favourites";
    private static final String TABLE_CACHEONE = "Cache1";
    private static final String TABLE_CACHETWO = "Cache2";

    public static final String COLUMN_DININGITEM = "DiningItem";
    public static final String MENUITEM_ID = "MenuItemID";
    public static final String CACHE_ID = "CacheID";
    public static final String CACHE_DMY = "CacheDayMonthYear";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //----------------------------favourites DB-----------------------------------------------------
    public void insertFavouritesItem(MenuItem favourite) {

        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        values.put(COLUMN_DININGITEM, gson.toJson(favourite).getBytes()/*.getDiningItem()*/);
        String nameWithoutSpaces = favourite.name.replaceAll("\\s+","");
        values.put(MENUITEM_ID, nameWithoutSpaces);
        //values.put(COLUMN_DININGITEM, favourite/*.getDiningItem()*/);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_FAVOURITES, null, values);
        db.close();
    }

    public ArrayList<MenuItem> getFavouritesItems(/*String favourite*/) {
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITES;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<MenuItem> favouriteitems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_DININGITEM));
                String json = new String(blob);
                Gson gson = new Gson();
                MenuItem favourite = gson.fromJson(json, new TypeToken<MenuItem>() {}.getType());
                favouriteitems.add(favourite);
                //favouriteitems.add((cursor.getString(cursor.getColumnIndex(COLUMN_DININGITEM))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favouriteitems;
    }

    public ArrayList<ArrayList<List<MenuItem>>> getFavouritesAsAllMenu() {
        DBHandler dba = new DBHandler(context);

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
        if(dba.searchCacheForDate(passDateToSearchActivity.getDate()) != null) {
            favitems = dba.searchCacheForDate(passDateToSearchActivity.getDate());
        }else{
            favitems = dba.getCacheOneItems().get(0);
        }
        for(MenuItem a : dba.getFavouritesItems()) {
            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 2; j++) {
                    for (int k = 0; k <= favitems.get(i).get(j).size() - 1; k++) {
                        //String nameWithoutSpaces = favitems.get(i).get(j).get(k).name.replaceAll("\\s+","");
                        if (/*nameWithoutSpaces*/favitems.get(i).get(j).get(k).name.toLowerCase().equals(a.name.toLowerCase())) {
                            favhititems.get(i).get(j).add(favitems.get(i).get(j).get(k));
                            Log.d("logging", ""+favhititems.get(i).get(j).get(0).name);
                        }
                    }
                }
            }
        }
        return favhititems;
    }

    public void deleteFavouritesItem(MenuItem exFavourite) {
        SQLiteDatabase db = this.getReadableDatabase();
        Gson gson = new Gson();

        //Log.d("-----------------------","-------------: "+String.valueOf(gson.toJson(exFavourite)));
        //String[] x = new String[] {String.valueOf(gson.toJson(exFavourite).getBytes())};
        //gson.toJson(exFavourite).getBytes()
        //db.delete(TABLE_FAVOURITES, COLUMN_DININGITEM + " = ?", new String[] {String.valueOf(exFavourite)});
        //int y = db.delete(TABLE_FAVOURITES, COLUMN_DININGITEM + " = ?", new String[] {String.valueOf(0)});
        String nameWithoutSpaces = exFavourite.name.replaceAll("\\s+", "");
        /*int y = */db.delete(TABLE_FAVOURITES, MENUITEM_ID + " = ?", new String[]{nameWithoutSpaces});
        //Log.d("-----------------------", "-------------: " + y);
        db.close();
    }

    public boolean checkIfFavourite(String item) {
        ArrayList<MenuItem> favouriteitems = getFavouritesItems();
        for(MenuItem menuitem : favouriteitems) {
            if(menuitem.name.equals(item)) {
                return true;
            }
        }
        return false;
    }
    //----------------------------------------------------------------------------------------------

    //-----------------------------cache one DB-----------------------------------------------------
    public void insertCacheOneItem(ArrayList<ArrayList<List<MenuItem>>> allDHAllMeals, String dayMonthYear) {

        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        values.put(COLUMN_DININGITEM, gson.toJson(allDHAllMeals).getBytes());
        cachedMenuNum++;
        String cacheNum = ""+cachedMenuNum;
        values.put(CACHE_ID,cacheNum);
        values.put(CACHE_DMY,dayMonthYear);
        //String nameWithoutSpaces = allDHAllMeals.get(0).get(0).get(0).name.replaceAll("\\s+","");
        //values.put(MENUITEM_ID, nameWithoutSpaces);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CACHEONE, null, values);
        db.close();
    }

    public ArrayList<ArrayList<ArrayList<List<MenuItem>>>> getCacheOneItems() {
        String selectQuery = "SELECT  * FROM " + TABLE_CACHEONE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<ArrayList<ArrayList<List<MenuItem>>>> cacheitems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_DININGITEM));
                String json = new String(blob);
                Gson gson = new Gson();
                ArrayList<ArrayList<List<MenuItem>>> cacheitem = gson.fromJson(json, new TypeToken<ArrayList<ArrayList<List<MenuItem>>>>() {}.getType());
                cacheitems.add(cacheitem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cacheitems;
    }

    public ArrayList<ArrayList<List<MenuItem>>> searchCacheForDate(String dayMonthYear){
        String query = "Select * FROM " + TABLE_CACHEONE + " WHERE " + CACHE_DMY + " =  \"" + dayMonthYear + "\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            byte[] blob = cursor.getBlob(cursor.getColumnIndex(COLUMN_DININGITEM));
            String json = new String(blob);
            Gson gson = new Gson();
            ArrayList<ArrayList<List<MenuItem>>> cacheitem = gson.fromJson(json, new TypeToken<ArrayList<ArrayList<List<MenuItem>>>>() {}.getType());
            cursor.close();
            db.close();
            return cacheitem;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public void deleteCacheOneItem(/*ArrayList<ArrayList<List<MenuItem>>> exAllDHAllMeals*/) {
        SQLiteDatabase db = this.getReadableDatabase();
        Gson gson = new Gson();
        db.delete(TABLE_CACHEONE, CACHE_ID + " = ?", new String[]{""+(cachedMenuNum-2)});
        //String nameWithoutSpaces = exAllDHAllMeals.get(0).get(0).get(0).name.replaceAll("\\s+", "");
        //db.delete(TABLE_CACHEONE, MENUITEM_ID + " = ?", new String[]{nameWithoutSpaces});
        db.close();
    }

    public Cursor getWordMatches(String query, String[] columns) {
        String selection = COLUMN_DININGITEM + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE_CACHEONE);

        Cursor cursor = builder.query(db,
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    //----------------------------------------------------------------------------------------------

    //-----------------------------cache two DB-----------------------------------------------------
    public void insertCacheTwoItem(ArrayList<MenuItem> favourite) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_DININGITEM, favourite.get(0).name);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CACHETWO, null, values);
        db.close();
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVOURITES_TABLE = "CREATE TABLE " +
                TABLE_FAVOURITES + "("
                + COLUMN_DININGITEM
                + " TEXT," + MENUITEM_ID + " TEXT" + ")";

        String CREATE_CACHEONE_TABLE = "CREATE TABLE "
                + TABLE_CACHEONE + "(" + COLUMN_DININGITEM + " TEXT," + CACHE_ID + " TEXT," +
                CACHE_DMY+ " TEXT" +")";

        String CREATE_CACHETWO_TABLE = "CREATE TABLE "
                + TABLE_CACHETWO + "(" + COLUMN_DININGITEM + "TEXT" + ")";

        db.execSQL(CREATE_FAVOURITES_TABLE);
        db.execSQL(CREATE_CACHEONE_TABLE);
        db.execSQL(CREATE_CACHETWO_TABLE);
        //insertFavouritesItem(passedMenuList.getMenuList().get(0).name);
        //Log.d("DB TEST", "ayyyyyyy: "+getFavouritesItems().get(0));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHEONE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHETWO);
        onCreate(db);
    }

}
