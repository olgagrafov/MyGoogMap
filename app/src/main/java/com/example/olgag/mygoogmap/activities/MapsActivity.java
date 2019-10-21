package com.example.olgag.mygoogmap.activities;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.olgag.mygoogmap.Services.PowerReceiver;
import com.example.olgag.mygoogmap.controller.FavoriteAdapter;
import com.example.olgag.mygoogmap.controller.SearchAdapter;
import com.example.olgag.mygoogmap.fragments.FavoriteFragment;
import com.example.olgag.mygoogmap.fragments.MapFragment;
import com.example.olgag.mygoogmap.R;
import com.example.olgag.mygoogmap.fragments.SearchFragment;
import com.example.olgag.mygoogmap.model.Place;
import com.example.olgag.mygoogmap.preferences.PrefsActivity;




public class MapsActivity extends AppCompatActivity implements View.OnClickListener, SearchAdapter.OnPlaceClickListener, LocationListener, FavoriteAdapter.OnFavorClickListener, PowerReceiver.OnPowerConnectedListener {
    private EditText txtSearch;
    private InputMethodManager imm;
    private double distanceKey;
    private double distanceKeyBeforeChange;
    private String measureKeys;
    private String measureKeysBeforeChange;
    private SharedPreferences sp;
    private SearchFragment fragSearch;
    private FavoriteFragment fragFavor;
    private MapFragment fragMmap;
    private LocationManager locationManager;
    private  double lat,lon;
    private  boolean isTablet;
    private Button btnGo;
    private String strHint;
    private PowerReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        //Make Portrait Orientation on a phone
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //And Landscape Orientation on a tablet
        isTablet = getResources().getBoolean(R.bool.isTablet);
       // Toast.makeText(this, " " + isTablet,Toast.LENGTH_SHORT).show();
        if (isTablet){
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
           }

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(" ");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setActionBar(toolbar);
        }


        //Check if device connected to power or not
        receiver=new PowerReceiver(this);
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));

        //check if you connect to Internet
        if(!isOnline())
            Toast.makeText(this,"You don't have Internet connection",Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLocation();


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        //I must put the first data:
        measureKeysBeforeChange = sp.getString("measure_key", "km");
        distanceKeyBeforeChange = Double.parseDouble(sp.getString("distance_key", "500"));
        measureKeys = measureKeysBeforeChange;
        distanceKey = distanceKeyBeforeChange;

        fragSearch = (SearchFragment) getSupportFragmentManager().findFragmentByTag("search");
        fragFavor = (FavoriteFragment) getSupportFragmentManager().findFragmentByTag("favorite");
        fragMmap = (MapFragment) getSupportFragmentManager().findFragmentByTag("map");

        if(savedInstanceState == null){
            fragSearch = new SearchFragment();
            fragFavor = new FavoriteFragment();
            fragMmap = new MapFragment();
            if(isTablet)
            {
                strHint="Enter Text For Search";
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, fragSearch, "search")
                        .show(fragSearch)
                        .add(R.id.fragmentContainer, fragFavor, "favorite")
                        .hide(fragFavor)
                        .add(R.id.mapContainer, fragMmap, "map")
                        .show(fragMmap)
                        .commit();

            }
            else {
                strHint="Search";
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, fragSearch, "search")
                        .show(fragSearch)
                        .add(R.id.fragmentContainer, fragFavor, "favorite")
                        .hide(fragFavor)
                        .add(R.id.fragmentContainer, fragMmap, "map")
                        .hide(fragMmap)
                        .commit();
            }
            //When user first enter I must check the distance in last search
            Bundle bundle = new Bundle();
            bundle.putDouble("userLat", lat);
            bundle.putDouble("userLon", lon);
            fragSearch.setArguments(bundle);
            fragFavor.setArguments(bundle);
            fragMmap.setArguments(bundle);

        }


        txtSearch = (EditText) findViewById(R.id.txtSearch);
        txtSearch.setOnClickListener(this);
        findViewById(R.id.btnFavor).setOnClickListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);
        findViewById(R.id.btnSettings).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
        btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setOnClickListener(this);

        //i made the keyword hide when i don't need to use
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }
    @Override
    protected void onStart() {
        super.onStart();

      //I must change the data in SearchFragment if preferences changed
        measureKeys = sp.getString("measure_key", "km");
        distanceKey = Double.parseDouble(sp.getString("distance_key", "500"));
        if (distanceKey != distanceKeyBeforeChange || measureKeys != measureKeysBeforeChange) {
            if (measureKeys.equals("ml"))
                distanceKey = distanceKey * 1.60934;//I must transfer ml to meters for send the true data

            //Call the method Search when we changed something  in a settings:
            getLocation();
            //   Toast.makeText(this,""+lat,Toast.LENGTH_SHORT).show();
              fragSearch.setDataForSearch(distanceKey,lat,lon);
        }
    }

    //Check if user connects to the Internet
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null){
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        return false;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSearch:
                txtSearch.animate().setStartDelay(100).scaleX(1).scaleY(1);
                btnGo.animate().setStartDelay(100).scaleX(1).scaleY(1);
                //i made the keyword visible i need to use
                imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtSearch, InputMethodManager.SHOW_IMPLICIT);


                if(isTablet){
                    fragMmap.setOnlyUserOnTheMap(lat,lon);
                    getSupportFragmentManager().beginTransaction()
                            .hide(fragFavor)
                            .show(fragSearch)
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    getSupportFragmentManager().beginTransaction()
                            .hide(fragFavor)
                            .hide(fragMmap)
                            .show(fragSearch)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.btnGo:
                    //first of all i delete all data from table, because i need the table contains the latest search
                    // getContentResolver().delete(DbProvider.CONTENT_SEARCH_URI, null, null);
                    getLocation();

                    //I must found the distance for search
                    measureKeys = sp.getString("measure_key", "km");
                    distanceKey = Double.parseDouble(sp.getString("distance_key", "500"));
                    if (measureKeys.equals("ml"))
                        distanceKey = distanceKey * 1.60934;

                    //the function is in SearchFrgment for to search the data from text
                    String str = txtSearch.getText().toString();
                    str.replace(" ", "%20");
                    fragSearch.setDataForSearch(str, distanceKey, lat, lon);

                    //i made the keyword hide i don't need to use
                    imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    //after enter the text for search i close the input text
                    btnGo.animate().setStartDelay(50).scaleX(0).scaleY(0);
                    txtSearch.animate().setStartDelay(50).scaleX(0).scaleY(0);
                    txtSearch.setText("");
                    txtSearch.setHint(strHint);

                    if (isTablet) {
                        getSupportFragmentManager().beginTransaction()
                                .hide(fragFavor)
                                .show(fragSearch)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .hide(fragMmap)
                                .hide(fragFavor)
                                .show(fragSearch)
                                .addToBackStack(null)
                                .commit();
                    }
            break;
            case R.id.btnFavor:
                //i made the keyword hide i don't need to use
                imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //after enter the text for search i close the input text
                btnGo.animate().setStartDelay(50).scaleX(0).scaleY(0);
                txtSearch.animate().setStartDelay(50).scaleX(0).scaleY(0);
                txtSearch.setText("");
                txtSearch.setHint(strHint);
                fragFavor.setAllData();
                if(isTablet){
                    fragMmap.setAllFavoresOnMap(lat,lon);
                    getSupportFragmentManager().beginTransaction()
                            .hide(fragSearch)
                            .show(fragFavor)
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    getSupportFragmentManager().beginTransaction()
                            .hide(fragSearch)
                            .hide(fragMmap)
                            .show(fragFavor)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.btnSettings:
                //I take the data in PreferenceManager before change
                measureKeysBeforeChange = sp.getString("measure_key", "km");
                distanceKeyBeforeChange = Double.parseDouble(sp.getString("distance_key", "500"));
                Intent intent = new Intent(this, PrefsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnClose:
                finish();
                break;
        }

    }


    //realization of interfaces:
    @Override
    public void addToFavor(Place placer) {
        fragFavor.addPlace(placer);
        Toast.makeText(this,"added to favorite successful",Toast.LENGTH_SHORT).show();
       /* getSupportFragmentManager().beginTransaction()
                .hide(fragSearch)
                .show(fragFavor)
                .addToBackStack(null)
                .commit();*/
    }
    @Override
    public void setFavorPlaceOnTheMap(double userLat, double userLon,String strSearch){
       // Toast.makeText(this," isTablet",Toast.LENGTH_SHORT).show();
        fragMmap.setFavorPlaceOnTheMap(lat,lon,strSearch);
    }
    @Override
    public void openMap(double placelat, double placeLon, String namePlace,double userLat, double userLon) {

         fragMmap.setDataOnMap(placelat, placeLon, namePlace, lat, lon);

        if (!isTablet) {
              getSupportFragmentManager().beginTransaction()
                    .hide(fragSearch)
                    .hide(fragFavor)
                    .show(fragMmap)
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public void setAllFavoresOnMap(double userLat, double userLon) {
        fragMmap.setAllFavoresOnMap(lat,lon);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
    //    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, getMainLooper());     //   lat =31.2359582;

       //Found what is the best provider:
        boolean gps_enabled = false;
        boolean network_enabled = false;

        LocationManager lm = (LocationManager)getSystemService(this.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled)
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (network_enabled)
            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gps_loc != null && net_loc != null) {

            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

        }
        else {
            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }

        lat = finalLoc.getLatitude();
        lon=finalLoc.getLongitude();


       // lat = 32.073933;
      //  lon=34.762922;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            } else {
                Toast.makeText(this, "Can't show your location on the map...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat=location.getLatitude();
        lon=location.getLongitude();

        // Toast.makeText(this,""+ lat +" " + lon, Toast.LENGTH_SHORT).show();
       // if(getResources().getConfiguration().orientation==1){
        //    fragMmap.setUserPlaceOnMap(lat,lon);
        //}
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void isPowerConeected(String powerInfo) {
        Toast.makeText(this,powerInfo,Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

    }


}


