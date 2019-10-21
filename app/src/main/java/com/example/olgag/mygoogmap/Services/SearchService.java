package com.example.olgag.mygoogmap.Services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.olgag.mygoogmap.R;
import com.example.olgag.mygoogmap.db.DbProvider;
import com.example.olgag.mygoogmap.db.PlaceDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jbt on 17/09/2017.
 */

public class SearchService extends IntentService {

    public static final String SEARCH_ACTION = "com.example.sergey.secondprojectplaces.Services.SEARCH_TITLE";

    public SearchService() {
        super("SearchService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        String search = intent.getStringExtra("search");
        double userLat = intent.getDoubleExtra("userLat",0);
        double userLng = intent.getDoubleExtra("userLng",0);
        double distance = intent.getDoubleExtra("distance", 500);
        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + userLat + "," + userLng + "&radius=" + distance + "&keyword=" + search + "&key=AIzaSyBGPTt9U5ILeZS1JESASK1ua-wYHt83eHo";

      //  https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=31.2359582,34.7796028&radius=500&keyword=hotel&key=AIzaSyBGPTt9U5ILeZS1JESASK1ua-wYHt83eHo

        //http://maps.googleapis.com/maps/api/place/photo?photo_reference=CmRaAAAAL_tYmOXptYg7THXTSIw2yzElJUz7CsPquEuGpoXjbgLakcjXqdgO3TONhUP7R_ZPNk-KGLVYbeRqRq0upiPrH0nzxlnFHFxsxiagcCLPphnO_zcErlArtZ-JllBz7h6IEhAN4VDTX9s5ZWLT43P19Rw_GhSioe2JezeE5Sez8IJFB88IrBv51A&key=AIzaSyBGPTt9U5ILeZS1JESASK1ua-wYHt83eHo

      //  <a href=\"https://maps.google.com/maps/contrib/116432149663877434486/photos\">Maxim Hotel Tel Aviv</a>




        HttpURLConnection connection = null;
        BufferedReader reader;
        StringBuilder builder = new StringBuilder();
        URL url = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            int lengResoult=0;
            if(!builder.toString().isEmpty()) {
                JSONObject root = new JSONObject(builder.toString());
                JSONArray results = root.getJSONArray("results");
                String status = root.getString("status");
                if(status.equals("OK")) {
                    //i use the search server not only for go on JSON but also for write to the SEARCH_TABLE the result of searching
                    ContentValues values = new ContentValues();
                    //first of all i delete all data from table, because i need the table contains the latest search
                    getContentResolver().delete(DbProvider.CONTENT_SEARCH_URI, null, null);
                    double placerLat, placerLng;
                    String distanceToPlaces;
                    String photoUri="";
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject item = results.getJSONObject(i);
                        placerLat = Double.parseDouble(item.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                        placerLng = Double.parseDouble(item.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                        distanceToPlaces = CalculatDistance(userLat, userLng, placerLat, placerLng);
                       if(!item.isNull("photos")) {
                           JSONArray getPhoto=item.getJSONArray("photos");
                           photoUri =getPhoto.getJSONObject(0).getString("html_attributions");
                           photoUri=photoUri.substring(12);
                           int j = photoUri.indexOf(">")  - 2 ;
                           photoUri=photoUri.substring(0,j);
                           photoUri=photoUri.replaceAll("\\\\","");
                          // <a href=\"https://maps.google.com/maps/contrib/110931577521131565018/photos\">Николай Князев</a>
                       }
                        values.put(PlaceDbHelper.AHREF, photoUri);
                        values.put(PlaceDbHelper.SEARCH_ICON, CheckIcon(item.getString("icon")));
                        values.put(PlaceDbHelper.SEARCH_TYPE, search);  //item.getJSONArray("types").get(0).toString());
                        values.put(PlaceDbHelper.SEARCH_NAME, item.getString("name"));
                        values.put(PlaceDbHelper.SEARCH_ADRESS, item.getString("vicinity"));
                        values.put(PlaceDbHelper.SEARCH_DISTANCE, distanceToPlaces);
                        values.put(PlaceDbHelper.SEARCH_LAT, placerLat);
                        values.put(PlaceDbHelper.SEARCH_LNG, placerLng);
                        getContentResolver().insert(DbProvider.CONTENT_SEARCH_URI, values);

                    }
                    lengResoult = results.length();
                }
            }
            Intent broadIntent = new Intent(SEARCH_ACTION);
            broadIntent.putExtra("lenghtOfData",lengResoult );
            broadIntent.putExtra("search", search);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadIntent);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
//the function check what a kind of icon i put in my adapter from drawable
    public int CheckIcon(String pathIcon){
        //pathIcon contains the path of icon include the name, so i must delete:https://maps.gstatic.com/mapfiles/place_api/icons/    &  -71.png
        //after then i will get the name of icon i want to put in my adapter
        String strTemp=new String(pathIcon.substring(50));
        String strCheck=new String(strTemp.replaceAll("-71.png",""));
        if(strCheck.compareTo("bar")==0)
            return R.drawable.bar;
        else if(strCheck.compareTo("bus")==0)
            return R.drawable.bus;
        else if(strCheck.compareTo("cafe")==0)
            return R.drawable.cafe;
        else if(strCheck.compareTo("doctor")==0)
            return R.drawable.doctor;
        else if(strCheck.compareTo("generic_business")==0)
            return R.drawable.generic_business;
        else if(strCheck.compareTo("library")==0)
            return R.drawable.library;
        else if(strCheck.compareTo("lodging")==0)
            return R.drawable.lodging;
        else if(strCheck.compareTo("restaurant")==0)
            return R.drawable.restaurant;
        else if(strCheck.compareTo("school")==0)
            return R.drawable.school;
        else if(strCheck.compareTo("shopping")==0)
            return R.drawable.shopping;
        else if(strCheck.compareTo("museum")==0)
            return R.drawable.museum;
        else
            return R.drawable.geocode;

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
        String measureKeys = PreferenceManager.getDefaultSharedPreferences(this).getString("measure_key", "km");
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
}