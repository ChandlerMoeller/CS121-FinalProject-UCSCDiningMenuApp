package com.cs121.finalproject;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import android.util.Log;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DBHandler extends SQLiteOpenHelper {

    //private PassMenuListToDBHandler passedMenuList = PassMenuListToDBHandler.getPassMenuListToDBHandler();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DiningMenuDB.db";
    private static final String TABLE_FAVOURITES = "Favourites";
    private static final String TABLE_CACHEONE = "Cache1";
    private static final String TABLE_CACHETWO = "Cache2";

    public static final String COLUMN_DININGITEM = "DiningItem";
    public static final String MENUITEM_ID = "MenuItemID";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //----------------------------favourites DB-----------------------------------------------------
    // credit goes to http://stackoverflow.com/questions/23577825/android-save-object-as-blob-in-sqlite
    // for the object serialization code
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
    public void insertCacheOneItem(ArrayList<ArrayList<List<MenuItem>>> allDHAllMeals) {

        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        values.put(COLUMN_DININGITEM, gson.toJson(allDHAllMeals).getBytes());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_CACHEONE, null, values);
        db.close();
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
                + TABLE_CACHEONE + "(" + COLUMN_DININGITEM + "TEXT" + ")";

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
