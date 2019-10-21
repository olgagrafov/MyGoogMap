package com.example.olgag.mygoogmap.controller;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.olgag.mygoogmap.R;
import com.example.olgag.mygoogmap.db.DbProvider;
import com.example.olgag.mygoogmap.db.PlaceDbHelper;
import com.example.olgag.mygoogmap.model.Place;

import java.util.List;

/**
 * Created by Sergey on 16-Sep-17.
 */

public class SearchAdapter extends ArrayAdapter<Place> {
    private OnPlaceClickListener listener;

    public SearchAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);

        listener = (OnPlaceClickListener) context;

    }

    public SearchAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Place> objects) {
        super(context, resource, objects);
    }

  
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.place_search_item, parent, false);


        ImageView imgPlaceSer= (ImageView) convertView.findViewById(R.id.imgPlaceSer);
        TextView namePlaceSer= (TextView) convertView.findViewById(R.id.namePlaceSer);
        final TextView txtStreetSer= (TextView) convertView.findViewById(R.id.txtStreetSer);
        TextView txtDistanceSer= (TextView) convertView.findViewById(R.id.txtDistanceSer);
        ImageButton btnAddSer= (ImageButton) convertView.findViewById(R.id.btnAddSer);
        ImageButton btnShareSer= (ImageButton) convertView.findViewById(R.id.btnShareSer);
        ImageButton btnHref= (ImageButton) convertView.findViewById(R.id.btnHref);

        final Place currentPlace = getItem(position);

        //if I get information from GOOGLE i make googleButton
        namePlaceSer.setText(currentPlace.getPlacesName());
        txtStreetSer.setText(currentPlace.getStreet());
        txtDistanceSer.setText("" + currentPlace.getDistance());
        imgPlaceSer.setImageResource(currentPlace.getIcon());
        if(!currentPlace.getHref().isEmpty()) {
            btnHref.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = currentPlace.getHref(); //"https://maps.google.com/maps/contrib/116432149663877434486/photos";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    getContext().startActivity(i);
                }
            });
        }
        else{
            btnHref.setVisibility(View.INVISIBLE);
        }
        //I must use anonymous function to get the currentPlace if it is not anonymous position will not work right
        btnShareSer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentPlace.toString());
                sendIntent.setType("text/plain");
               getContext().startActivity(sendIntent);

            }
        });
        btnAddSer.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             ContentValues values = new ContentValues();
                                             values.put(PlaceDbHelper.FAVOR_ICON, currentPlace.getIcon());
                                             values.put(PlaceDbHelper.FAVOR_TYPE, currentPlace.getSearchType());
                                             values.put(PlaceDbHelper.FAVOR_NAME, currentPlace.getPlacesName());
                                             values.put(PlaceDbHelper.FAVOR_CITY, currentPlace.getStreet());
                                             values.put(PlaceDbHelper.FAVOR_LAT, currentPlace.getLat());
                                             values.put(PlaceDbHelper.FAVOR_LNG, currentPlace.getLng() );
                                             getContext().getContentResolver().insert(DbProvider.CONTENT_FAVOR_URI, values);

                                             //I did new row, i must take the new ID  put into plase for FavoriteFragment
                                             Cursor cursor = getContext().getContentResolver().query(DbProvider.CONTENT_FAVOR_URI, null, null, null, null);
                                             cursor.moveToLast();
                                           //  long searchId=cursor.getLong(cursor.getColumnIndex(PlaceDbHelper.FAVOR_ID));
                                            // currentPlace.setId(searchId);

                                             listener.addToFavor(currentPlace);
                                         }
                                     });

        txtStreetSer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.openMap(currentPlace.getLat(), currentPlace.getLng(), currentPlace.getPlacesName(),0.0,0.0);
            }
        });
        namePlaceSer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openMap(currentPlace.getLat(), currentPlace.getLng(), currentPlace.getPlacesName(),0.0,0.0);
            }
        });
        return convertView;

    }


    public interface OnPlaceClickListener{
        //interface for send data to main and main will send data to Favor frame
        void addToFavor(Place placer);
        //interface for send data to main and main will open Map frame
        void openMap(double lat, double lng, String namePlace,double userLat, double userLlng);

    }

}