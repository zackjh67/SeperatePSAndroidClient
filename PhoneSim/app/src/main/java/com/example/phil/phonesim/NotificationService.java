package com.example.phil.phonesim;

//import android.app.Notification;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
//import android.os.IBinder;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
//import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

public class NotificationService extends NotificationListenerService {
    private List<String> myList = Arrays.asList(
            "com.google.android.talk",
            "com.android.mms",
            "com.google.android.apps.messaging"
    );

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

        BroadcastReceiver statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConnService.BROADCAST_TEST)) {
                    String str = intent.getStringExtra("TEST");

                    //Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
                    Log.d("test", "in notif svc yo");


                    //doing stuff
                    ParcelableNotification toWrap = new ParcelableNotification("test", "tester", "testest", str);
                    Parcelable n = Parcels.wrap(toWrap);
                    //call method from ConnService to send notification to server
                    ConnService.sendNotification(getApplicationContext(), n);
                }
            }
        };
        //the certain intent to listen for, ignores all others
        IntentFilter connectedFilter;
        //broadcast status code sent through an intent from ConnService
        connectedFilter = new IntentFilter(ConnService.BROADCAST_TEST);
        LocalBroadcastManager.getInstance(this).registerReceiver
                (statusReceiver, connectedFilter);
    }


    /*
     * Overrides default behavior for when user receives a notification and pushes that
     * notification to a different device based on if the user is at their home location or not.
     * */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //TODO figure out which notifications are which and parse them accordingly so the app doesn't crash
        //sometimes
        Log.i("NOTIFPASSED", "notification passed");
        Log.i("PACKAGE", sbn.getPackageName());
        if (checkValidNotification(sbn.getPackageName())){
            //found this shit here:
            //http://www.androiddevelopersolutions.com/2015/05/android-read-status-bar-notification.html
            String pack = sbn.getPackageName();
            String ticker = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Notification.WearableExtender myExtender = new Notification.WearableExtender(sbn.getNotification());
            List<Notification.Action> actions = myExtender.getActions();

            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();
            //just logs in Logcat for debugging
            //package is what type of notification it is
            Log.i("Package", pack);

            //ticker is what it actually says in the status bar
            //for example Zack Hern: Hey whats up broham!?
            Log.i("Ticker", ticker);
            //title in a text message format would be the contact or number of sender
            Log.i("Title", title);
            //text in a text message format would be the actual body of the text
            Log.i("Text", text);
            //////////////////////////////////////////////////////////

            //this creates a new ParcelableNotification from the given notification components
            //and then wraps it to become a Parcelable object in order for the data to be passed
            //using Intents.
            ParcelableNotification toWrap = new ParcelableNotification(pack, ticker, title, text);
            Parcelable n = Parcels.wrap(toWrap);
            //call method from ConnService to send notification to server
            ConnService.sendNotification(this, n);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ConnService.disconnect(getApplicationContext());
    }

    /*
    * Haven't really done anything with this method, but gets signal for when a notification
    * is 'dismissed'
    * we can use the concepts to possibly have the phone dismiss all notifications
    * when connected to the computer automatically, or alternatively
    * give user the option to auto dismiss these or keep them on their phone if they wanted them
    * still in their notifications for some reason
    * */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

    public  boolean checkValidNotification(String packageName){
        for(String str: myList){
            if(str.equals(packageName))
                return true;
        }
        return false;
    }
}
