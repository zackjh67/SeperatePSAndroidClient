package com.example.phil.phonesim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
* This is a broadcast receiver that listens only for a signal
* sent from HomeService telling the receiver that HomeService has been killed/stopped.
* When it receives this broadcast, it restarts the service, so HomeService can be
* always running.
* */
public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //logging "info" tag, just for debugging and such
        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(),
                "Service has been stopped.... restarting");
        //command to restart HomeService
        context.startService(new Intent(context, HomeService.class));;
    }
}