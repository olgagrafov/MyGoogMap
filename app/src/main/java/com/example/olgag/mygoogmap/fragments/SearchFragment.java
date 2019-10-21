package com.example.olgag.mygoogmap.fragments;


import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.olgag.mygoogmap.R;
import com.example.olgag.mygoogmap.Services.SearchReceiver;
import com.example.olgag.mygoogmap.Services.SearchService;
import com.example.olgag.mygoogmap.controller.SearchAdapter;
import com.example.olgag.mygoogmap.db.DbProvider;
import com.example.olgag.mygoogmap.db.PlaceDbHelper;
import com.example.olgag.mygoogmap.model.Place;

//import static com.example.sergey.secondprojectplaces.R.id.txtSearch;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
   // private SearchAdapter adapter;
    private SearchReceiver receiver;
    private View vw;
    private String searchType;
    SearchAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      vw=inflater.inflate(R.layout.fragment_search, container, false);

        Bundle bundle = getArguments();
        double userLat = bundle.getDouble("userLat",0);
        double userLon = bundle.getDouble("userLon",0);

//Toast.makeText(getContext(),""+ userLon, Toast.LENGTH_LONG).show();
//when i enter the first time i fill my search adapter from table with the last search data
       Cursor cursor = getContext().getContentResolver().query(DbProvider.CONTENT_SEARCH_URI, null, null, null, null);

        adapter = new SearchAdapter(getContext(), R.layout.place_search_item);
        searchType="";
        while (cursor.moveToNext()) {
            long searchId=cursor.getLong(cursor.getColumnIndex(PlaceDbHelper.SEARCH_ID));
            int searchIcon = cursor.getInt(cursor.getColumnIndex(PlaceDbHelper.SEARCH_ICON));
            searchType = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.SEARCH_TYPE));
            String searchName = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.SEARCH_NAME));
            String ahref = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.AHREF));
            String searchAdress = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.SEARCH_ADRESS));
            double searchLat=cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.SEARCH_LAT));
            double searchLng=cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.SEARCH_LNG));
            String searchDistance= CalculatDistance(userLat, userLon, searchLat, searchLng);
            // adapter.add(new Place(searchId, "city", searchAdress, searchIcon, searchType, searchName, searchDistance, searchLat,searchLng));
            adapter.add(new Place(searchId, "city", searchAdress, searchIcon, searchType, searchName, searchDistance, searchLat,searchLng,ahref));
        }

        ListView listView = (ListView) vw.findViewById(R.id.SearchList);
        listView.setAdapter(adapter);
       if(!searchType.isEmpty()) {
            TextView txtLastSearch = (TextView) vw.findViewById(R.id.txtLastSearch);
            txtLastSearch.setText("your last search was: " + searchType);
        }
        return vw;
    }

    //the function for calculates the distance from user to all founded places
    public String CalculatDistance(double userLat, double userLng, double placerLat, double placerLng) {
        int Radius = 6371;// radius of earth in Km
        double dLat = Math.toRadians(placerLat - userLat);
        double dLon = Math.toRadians(placerLng - userLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(userLat))
                * Math.cos(Math.toRadians(placerLat)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        String measureKeys = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("measure_key", "km");
        if(measureKeys.equals("ml")){
            valueResult/=1.60934;
        }
        String str="" + valueResult;
        DecimalFormat precision = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            precision = new DecimalFormat("0.00");
            str=precision.format( valueResult);
        }
        return (str + " " + measureKeys);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
             LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        }


//I put data from Main activity in frame Search for all searches I need
      public void setDataForSearch(String txtForSearch, double distance, double userLat, double userLng)  {
      //  Toast.makeText(vw.getContext(),""+ userLat + " " +userLng ,Toast.LENGTH_SHORT).show();

              receiver = new SearchReceiver(getContext(), vw);
              LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SearchService.SEARCH_ACTION));
              Intent intent = new Intent(getContext(), SearchService.class);
              intent.putExtra("search", txtForSearch);
              intent.putExtra("distance", distance);
              intent.putExtra("userLat", userLat);
              intent.putExtra("userLng",  userLng);
              getContext().startService(intent);

      }
//if i only changed something  in a settings I call the method with new distance:
    public void setDataForSearch(double distance, double userLat, double userLng) {
            Cursor cursor = getContext().getContentResolver().query(DbProvider.CONTENT_SEARCH_URI, null, null, null, null);
            cursor.moveToFirst();
            String strSearch=cursor.getString(cursor.getColumnIndex(PlaceDbHelper.SEARCH_TYPE));

            receiver = new SearchReceiver(getContext(), vw);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SearchService.SEARCH_ACTION));
            Intent intent = new Intent(getContext(), SearchService.class);
            intent.putExtra("search", strSearch);
            intent.putExtra("distance", distance);
            intent.putExtra("userLat", userLat);
            intent.putExtra("userLng", userLng);
          //  Toast.makeText(vw.getContext(),""+ distance + " " + strSearch,Toast.LENGTH_SHORT).show();
            //I del all data from search_table becouse I will put the new data with new distance
            getContext().getContentResolver().delete(DbProvider.CONTENT_SEARCH_URI, null, null);
            getContext().startService(intent);
    }

}
