package com.example.olgag.mygoogmap.fragments;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.olgag.mygoogmap.R;
import com.example.olgag.mygoogmap.controller.FavoriteAdapter;
import com.example.olgag.mygoogmap.db.DbProvider;
import com.example.olgag.mygoogmap.db.PlaceDbHelper;
import com.example.olgag.mygoogmap.model.Place;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements View.OnClickListener {
    private FavoriteAdapter adapter;
    private double userLat,userLon;
    private RecyclerView favoriteList;
    private  Cursor cursor;
    private EditText txtForSearchFavorite;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vw= inflater.inflate(R.layout.fragment_favorite, container, false);

        Bundle bundle = getArguments();
        userLat = bundle.getDouble("userLat",0);
        userLon = bundle.getDouble("userLon",0);

        txtForSearchFavorite=vw.findViewById(R.id.txtForSearchFavorite);
        favoriteList = (RecyclerView) vw.findViewById(R.id.FavoriteList);
        favoriteList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoriteAdapter(getContext());
        favoriteList.setAdapter(adapter);
        setAllData();



        vw.findViewById(R.id.delAllFavorite).setOnClickListener(this);
        vw.findViewById(R.id.btnSearchInFavorite).setOnClickListener(this);

        return vw;
    }
public void setAllData(){

    adapter.delAll();
    adapter.setStrSearch("");
    cursor = getContext().getContentResolver().query(DbProvider.CONTENT_FAVOR_URI, null, null, null, null);


    while (cursor.moveToNext()) {
        long favorId=cursor.getLong(cursor.getColumnIndex(PlaceDbHelper.FAVOR_ID));
        String favorAdress = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.FAVOR_CITY));
        int favorIcon = cursor.getInt(cursor.getColumnIndex(PlaceDbHelper.FAVOR_ICON));
        String favorType = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.FAVOR_TYPE));
        String favorName = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.FAVOR_NAME));
        double favorhLat=cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.FAVOR_LAT));
        double favorLng=cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.FAVOR_LNG));

        adapter.add(new Place(favorId, "city", favorAdress, favorIcon, favorType, favorName, "500", favorhLat,favorLng,""));
    }
}

    public void addPlace(Place place) {
        adapter.add(place);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSearchInFavorite:
              //  Toast.makeText(getContext(),"swdasdsa",Toast.LENGTH_SHORT).show(); Cursor cursor = getContext().getContentResolver().query(DbProvider.CONTENT_FAVOR_URI, null, null, null, null);
                adapter.delAll();
                boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                //Close Keyboard:
                InputMethodManager imm;
                imm = (InputMethodManager)  getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //Take the text for search:
                String txtSerch =txtForSearchFavorite.getText().toString();
                txtForSearchFavorite.setText("");
                //Make new select with the text search
                cursor = getContext().getContentResolver().query(DbProvider.CONTENT_FAVOR_URI, null, "name_favor like ? OR type_favor like ?  OR city_favor like ?", new String[]{txtSerch, txtSerch, txtSerch}, null);
                 FragmentManager fm = getFragmentManager();
                MapFragment fragm = (MapFragment) fm.findFragmentByTag("map");
                while (cursor.moveToNext()) {
                    long favorId = cursor.getLong(cursor.getColumnIndex(PlaceDbHelper.FAVOR_ID));
                    String favorAdress = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.FAVOR_CITY));
                    int favorIcon = cursor.getInt(cursor.getColumnIndex(PlaceDbHelper.FAVOR_ICON));
                    String favorType = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.FAVOR_TYPE));
                    String favorName = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.FAVOR_NAME));
                    double favorhLat = cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.FAVOR_LAT));
                    double favorLng = cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.FAVOR_LNG));

                    adapter.add(new Place(favorId, "city", favorAdress, favorIcon, favorType, favorName, "500", favorhLat, favorLng,""));
                }
                if (isTablet) {
                       fragm.setFavorPlaceOnTheMap( userLat, userLon,txtSerch);
                   }
                     adapter.setStrSearch(txtSerch);
                     txtForSearchFavorite.setText("");


                break;
            case R.id.delAllFavorite: //Delete all favorites
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Delete All Favorites")
                    .setMessage("Are you sucre you want delete all your favorites?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //dell all data in Favorite table
                            getContext().getContentResolver().delete(DbProvider.CONTENT_FAVOR_URI, null, null);
                            adapter.delAll();
                            boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                            if (isTablet) {
                                // I don't only delete all data in table and adapter, also I clean the map
                                FragmentManager fm = getFragmentManager();
                                MapFragment fragm = (MapFragment) fm.findFragmentByTag("map");
                                fragm.setAllFavoresOnMap(userLat, userLon);
                            }
                        }
                    })
                    .show();
                break;
        }
    }
}
