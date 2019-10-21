package com.example.olgag.mygoogmap.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olgag.mygoogmap.R;
import com.example.olgag.mygoogmap.db.DbProvider;
import com.example.olgag.mygoogmap.db.PlaceDbHelper;
import com.example.olgag.mygoogmap.fragments.MapFragment;
import com.example.olgag.mygoogmap.model.Place;

import java.util.ArrayList;

/**
 * Created by Sergey on 15-Sep-17.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {
    private Context context;
    private ArrayList<Place> places = new ArrayList<>();
    private int position;
    private OnFavorClickListener listener;
    private String strSearch;

    public FavoriteAdapter(Context context) {
        this.context = context;
        listener = (OnFavorClickListener) context;

    }

    public String getStrSearch() {
        return strSearch;
    }

    public void setStrSearch(String strSearch) {
        this.strSearch = strSearch;
    }

    public void add(Place place){
        places.add(place);
        notifyItemInserted(places.size()-1);
    }

    public void del(Place place){
        places.remove(place);
        notifyItemRemoved(position);
    }

    public void delAll(){
        int size = places.size();
        places.clear();
        notifyItemRangeRemoved(0, size);

    }

    @Override
    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new FavoriteHolder(LayoutInflater.from(context).inflate(R.layout.place_favorite_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FavoriteHolder holder, int position) {
        holder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

//**************************HOLDER***********************
    public class FavoriteHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    private ImageView imgPlaceFavor;
    private TextView txtCityFavor,namePlaceFavor,txtStreetFavor,txtDistanceFavor;
    private ImageButton btnDellFavor,btnShare;
    private Place curentPlace;


    public FavoriteHolder(View itemView) {
        super(itemView);

        txtCityFavor = (TextView) itemView.findViewById(R.id.txtCityFavor);
        namePlaceFavor = (TextView) itemView.findViewById(R.id.namePlaceFavor);
     //   txtStreetFavor = (TextView) itemView.findViewById(R.id.txtStreetFavor);
      //  txtDistanceFavor = (TextView) itemView.findViewById(R.id.txtDistanceFavor);
        btnDellFavor= (ImageButton) itemView.findViewById(R.id.btnDellFavor);
        btnShare= (ImageButton) itemView.findViewById(R.id.btnShare);
        btnDellFavor.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        namePlaceFavor.setOnClickListener(this);
        txtCityFavor.setOnClickListener(this);
       // itemView.setOnLongClickListener(this);

    }
    public void bind(Place place){
        curentPlace = place;
        txtCityFavor.setText(place.getStreet());
        namePlaceFavor.setText(place.getPlacesName());
    //    txtStreetFavor.setText(place.getStreet());
      //  txtDistanceFavor.setText("" + place.getDistance());
    }
    @Override
    public boolean onLongClick(View view) {

        return true;
    }

    @Override
    public void onClick(View view) {
        //  places.remove(curentPlace);
        //  notifyItemRemoved(getAdapterPosition());
        switch (view.getId()) {
            case R.id.btnShare:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, curentPlace.toString());
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
                break;
            case R.id.btnDellFavor://Delete one favorite

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete That Favorite")
                        .setMessage("Are you sucre you want delete that  favorite?" )
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                position = getAdapterPosition();
                                context.getContentResolver().delete(DbProvider.CONTENT_FAVOR_URI, PlaceDbHelper.FAVOR_ID + " = " + curentPlace.getId(), null);
                                del(curentPlace);
                                boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
                                //Toast.makeText(context," "+isTablet,Toast.LENGTH_SHORT).show();
                                if (isTablet) listener.setFavorPlaceOnTheMap(0.0, 0.0,strSearch);
                            }
                        })
                        .show();

                break;
            case R.id.namePlaceFavor:
            case R.id.txtCityFavor:
                listener.openMap(curentPlace.getLat(), curentPlace.getLng(), curentPlace.getPlacesName(),0.0,0.0);
                break;
        }
    }

}
    public interface OnFavorClickListener{
        void openMap(double lat, double lng, String namePlace,double userLat, double userLlng);
        void setAllFavoresOnMap(double userLat, double userLon);
        void setFavorPlaceOnTheMap(double userLat, double userLon,String strForSearch);
    }
}
