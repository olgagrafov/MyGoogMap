package com.example.olgag.mygoogmap.preferences;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.olgag.mygoogmap.R;

/**
 * Created by olgag on 24/09/2017.
 */

public class MyPrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefer);

        findPreference("measure_key").setOnPreferenceChangeListener(this);
        findPreference("distance_key").setOnPreferenceChangeListener(this);
    }

//we use the function for to see what changes was in preferences
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()){
            case "measure_key":
                preference.setSummary("this is a new kind of measure is a " + newValue); //+ String.valueOf(newValue));
                break;
            case "distance_key":
                double newDistance= Double.parseDouble(newValue.toString())/1000;
                preference.setSummary("this a new distance for search  is "+ newDistance + "  " +  findPreference("measure_key").getSharedPreferences().getString("measure_key"," "));
                break;
        }
        return true;
    }
}

