package com.example.olgag.mygoogmap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by olgag on 21/09/2017.
 */

public class PlaceDbHelper extends SQLiteOpenHelper {
    // we need two table:
    // first for search fragment:
    public static final String TABLE_SEARCH = "tbl_search";
    public static final String SEARCH_ID = "id_search";
    public static final String SEARCH_ICON = "icon_search";
    public static final String SEARCH_TYPE = "type_search";
    public static final String SEARCH_NAME = "name_search";
    public static final String SEARCH_ADRESS = "adress_search";
    public static final String SEARCH_DISTANCE = "distance_search";
    public static final String SEARCH_LAT = "lat_search";
    public static final String SEARCH_LNG = "lng_search";
    public static final String AHREF = "ahref";
    // second for favorite fragment:
    public static final String TABLE_FAVOR = "tbl_favor";
    public static final String FAVOR_ID = "id_favor";
    public static final String FAVOR_ICON = "icon_favor";
    public static final String FAVOR_TYPE = "type_favor";
    public static final String FAVOR_NAME = "name_favor";
    public static final String FAVOR_CITY = "city_favor";
    public static final String FAVOR_LAT = "lat_favor";
    public static final String FAVOR_LNG = "lng_favor";



    public PlaceDbHelper(Context context) {
        super(context,  "myPlaces.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s ( %s  INTEGER PRIMARY KEY AUTOINCREMENT, %s REAL , %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL, %s TEXT )",
                TABLE_SEARCH, SEARCH_ID, SEARCH_ICON, SEARCH_TYPE, SEARCH_NAME, SEARCH_ADRESS, SEARCH_DISTANCE, SEARCH_LAT, SEARCH_LNG, AHREF));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s ( %s  INTEGER PRIMARY KEY AUTOINCREMENT, %s REAL , %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL )",
                TABLE_FAVOR, FAVOR_ID, FAVOR_ICON, FAVOR_TYPE, FAVOR_NAME, FAVOR_CITY,  FAVOR_LAT, FAVOR_LNG));


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
