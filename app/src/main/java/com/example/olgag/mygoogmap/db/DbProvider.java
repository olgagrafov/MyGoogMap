package com.example.olgag.mygoogmap.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DbProvider extends ContentProvider {
    private PlaceDbHelper helper;
    private static final String AUTHORIZATION = "com.example.sergey.secondprojectplaces.controller";
    public static final Uri CONTENT_SEARCH_URI = Uri.parse("content://" + AUTHORIZATION + "/" + PlaceDbHelper.TABLE_SEARCH);
    public static final Uri CONTENT_FAVOR_URI = Uri.parse("content://" + AUTHORIZATION + "/" + PlaceDbHelper.TABLE_FAVOR);




    public DbProvider() {
    }
    @Override
    public boolean onCreate() {
        helper=new PlaceDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        // getLastPathSegment will return the end of the Uri (in our case the name of the table!)
        Cursor c = db.query(uri.getLastPathSegment(), columns, selection, selectionArgs, null, null, sortOrder);
        return c;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
            SQLiteDatabase db = helper.getWritableDatabase();
            int count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
            db.close();
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long rowNum = db.insert(uri.getLastPathSegment(), null, values);
        db.close();
        return Uri.withAppendedPath(uri, rowNum+""); // add "/" at the end and then add rowNum
        //return Uri.parse(uri.toString() + "/" + rowNum);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
        db.close();
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
