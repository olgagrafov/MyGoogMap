package com.example.olgag.mygoogmap.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.olgag.mygoogmap.R;
import com.example.olgag.mygoogmap.db.DbProvider;
import com.example.olgag.mygoogmap.db.PlaceDbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    double userLat,userLon;
    private GoogleMap mMap;
    private String strForSearch;
    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_map, container, false);

        Bundle bundle = getArguments();
        userLat = bundle.getDouble("userLat",0);
        userLon = bundle.getDouble("userLon",0);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //If user uses Tablet i put his place on the map
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)
            setOnlyUserOnTheMap(userLat,userLon);
    }

    public void setOnlyUserOnTheMap(double userLat, double userLon){
        LatLng userPlace = new LatLng(userLat, userLon);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(userPlace).title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPlace));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLon), 15));
    }

    public void setDataOnMap(double lat, double lon, String namePlace,double userLat, double userLon){
      // Toast.makeText(getContext()," " + lat +" " + lon+ " " + userLat +" " + userLon,Toast.LENGTH_SHORT).show();

        LatLng newPlace = new LatLng(lat, lon);
        LatLng userPlace = new LatLng(userLat, userLon);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(newPlace).title(namePlace));
        mMap.addMarker(new MarkerOptions().position(userPlace).title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPlace));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLon), 15));
    }
   public void setAllFavoresOnMap(double userLat, double userLon){
        Cursor cursor = getContext().getContentResolver().query(DbProvider.CONTENT_FAVOR_URI, null, null, null, null);

        mMap.clear();
        while (cursor.moveToNext()) {
            String favorName = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.FAVOR_NAME));
            double favorhLat = cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.FAVOR_LAT));
            double favorLng = cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.FAVOR_LNG));
            LatLng favorPlace = new LatLng(favorhLat, favorLng);
            mMap.addMarker(new MarkerOptions().position(favorPlace).title(favorName));

        }

        LatLng userPlace = new LatLng(userLat, userLon);
        mMap.addMarker(new MarkerOptions().position(userPlace).title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPlace));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLon), 15));


    }


    public void setFavorPlaceOnTheMap(double userLat, double userLon,String strSearch){
        strForSearch=strSearch;
        Cursor cursor;
        if(strForSearch.isEmpty())
            cursor = getContext().getContentResolver().query(DbProvider.CONTENT_FAVOR_URI, null, null, null, null);
        else
            cursor = getContext().getContentResolver().query(DbProvider.CONTENT_FAVOR_URI, null, "name_favor like ? OR type_favor like ?  OR city_favor like ?", new String[]{strForSearch, strForSearch, strForSearch}, null);
        mMap.clear();
        while (cursor.moveToNext()) {
            String favorName = cursor.getString(cursor.getColumnIndex(PlaceDbHelper.FAVOR_NAME));
            double favorhLat = cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.FAVOR_LAT));
            double favorLng = cursor.getDouble(cursor.getColumnIndex(PlaceDbHelper.FAVOR_LNG));
            LatLng favorPlace = new LatLng(favorhLat, favorLng);
            mMap.addMarker(new MarkerOptions().position(favorPlace).title(favorName));

        }

        LatLng userPlace = new LatLng(userLat, userLon);
        mMap.addMarker(new MarkerOptions().position(userPlace).title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userPlace));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLon), 15));


    }
   }

