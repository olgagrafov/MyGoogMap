package com.example.olgag.mygoogmap.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by olgag on 06/10/2017.
 */

public class PowerReceiver extends BroadcastReceiver {
    private OnPowerConnectedListener listener;

    public PowerReceiver(OnPowerConnectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String powerMessage = "";
        String powerAction = intent.getAction();

        if(powerAction.equals(Intent.ACTION_POWER_CONNECTED)) {
            powerMessage="Your device is connected to power";
        }
        else if(powerAction.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            powerMessage="Your device is disconnected from power";
        }

        listener.isPowerConeected(powerMessage );
    }

    public interface OnPowerConnectedListener{
        void isPowerConeected(String powerInfo);
    }
}
