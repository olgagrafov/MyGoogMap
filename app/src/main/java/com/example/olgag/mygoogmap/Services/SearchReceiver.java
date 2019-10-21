package com.example.olgag.mygoogmap.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olgag.mygoogmap.R;
import com.example.olgag.mygoogmap.controller.SearchAdapter;
import com.example.olgag.mygoogmap.db.DbProvider;
import com.example.olgag.mygoogmap.db.PlaceDbHelper;
import com.example.olgag.mygoogmap.model.Place;

/**
 * Created by jbt on 17/09/2017.
 */

public class SearchReceiver extends BroadcastReceiver {
    private int lenghtOfData;
    private SearchAdapter adapter;
    private Context cont;
    private View vw;


    public SearchReceiver(Context context) {
        this.cont=context;

    }

    public SearchReceiver(Context context, View vw) {
        this.cont=context;
        this.vw=vw;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.lenghtOfData = intent.getIntExtra("lenghtOfData",0);
        String search = intent.getStringExtra("search");
        String photoUri = intent.getStringExtra("photoUri");
        //i check the result if i found for user what he asked, i bild the list of founded places
      if(lenghtOfData>1) {
            Cursor cursor = context.getContentResolver().query(DbProvider.CONTENT_SEARCH_URI, null, null, null, null);
           //I use here the costume adapter :1.for study 2.because the search has not large list
            adapter = new SearchAdapter(cont, R.layout.place_search_item);
           String searchType="";
            while (cursor.moveToNext()) {
                long searchId=cursor.getLong(cursor.getColumnIndex(PlaceDbHelper.SEARCH_ID));
                int searchIcon = cursor.getInt(cursor.getColumnIndex(PlaceDbHelper.SEARCH_ICON));
                searchType = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.SEARCH_TYPE));
                String ahref = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.AHREF));
                String searchName = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.SEARCH_NAME));
                String searchAdress = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.SEARCH_ADRESS));
                String searchDistance=cursor.getString(cursor.getColumnIndex(PlaceDbHelper.SEARCH_DISTANCE));
                double searchLat=cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.SEARCH_LAT));
                double searchLng=cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.SEARCH_LNG));
               // adapter.add(new Place(searchId, "city", searchAdress, searchIcon, searchType, searchName, searchDistance, searchLat,searchLng));
                adapter.add(new Place(searchId, "city", searchAdress, searchIcon, searchType, searchName, searchDistance, searchLat,searchLng,ahref));
                }

            ListView listView = (ListView) vw.findViewById(R.id.SearchList);
            listView.setAdapter(adapter);
            TextView txtLastSearch= (TextView) vw.findViewById(R.id.txtLastSearch);
            txtLastSearch.setText("your last search was: " +searchType);


         // Toast.makeText(context, ":" + searchIcon, Toast.LENGTH_SHORT).show();

          // Toast.makeText(context, ":" + searchIcon, Toast.LENGTH_SHORT).show();
            //  Toast.makeText(context,""+tt,Toast.LENGTH_SHORT).show();
        }
        else {
          AlertDialog.Builder builder = new AlertDialog.Builder(vw.getContext());
          builder.setTitle("Search result")
                  .setMessage("no rusults for: " + search)
                  .setIcon(android.R.drawable.ic_dialog_alert)
                  //I use here anonymous because i don't need to do in function, i need the function  only for CANCEL BUTTON
                  .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                      }
                  })

                  .show();

              }
    }


}
