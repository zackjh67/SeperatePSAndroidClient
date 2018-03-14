package com.example.phil.phonesim;

/**
 * Created by phil on 2/19/18.
 */

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

//when the user is home, this service will then establish the http connection with the web server
//via ConnService. Upon connection established, ConnService will send a broadcast to a receiver
//in NotificationListenerExampleService to let it know to start pushing messages to that service
//here is the code to turn off and on the ConnService using the ConnReceiver...
/*
    //to turn it ON
        Intent broadcastIntent = new Intent("com.example.zack.phonesimulator.ConnReceiver");
        broadcastIntent.putExtra("CONN_SVC", 0);
        sendBroadcast(broadcastIntent);

    //to turn it OFF
        Intent broadcastIntent = new Intent("com.example.zack.phonesimulator.ConnReceiver");
        broadcastIntent.putExtra("CONN_SVC", 1);
        sendBroadcast(broadcastIntent);

    //until the geofence/wifi recognition is setup and working, we will be using a connect and
    //disconnect button on the main screen in MainActivity, which will just use this same code
 */

public class HomeService extends Service {
    static final String BROADCAST_CONN_STATUS_CHANGED = "com.example.zack.phonesimulator.BROADCAST_CONN_STATUS_CHANGED";
    //not needed later, just for testing in example
    public int counter=0;
    public HomeService(Context applicationContext) {
        super();
        //just some logging stuff
        Log.i("HERE", "here I am!");
    }
    /*
    * empty constructor yo
    * */
    public HomeService() {
    }

    /*
    * Fires upon service being started with command.
    * START_STICKY flag tells Android OS to try not to kill the service.
    * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        startTimer();
        return START_STICKY;
    }
    /*
    * This method triggers when the user force stops the service or Android OS decides it is low
    * priority and kills it
    * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        //create new intent to broadcast that this service has been stopped
        //will be picked up by a broadcast receiver to restart service
        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * not needed
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed, stops timer for testing purposes
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /*
    * idk whaat this does yet its how the example from online had it
    * */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
