package com.example.phil.phonesim;

//import android.app.Notification;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
import android.os.Bundle;
//import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
//import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.parceler.Parcels;

public class NotificationService extends NotificationListenerService {

    //@Override
    //public IBinder onBind(Intent intent) {
    //    return super.onBind(intent);
    //}

    /*
     * Overrides default behavior for when user receives a notification and pushes that
     * notification to a different device based on if the user is at their home location or not.
     * */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //TODO figure out which notifications are which and parse them accordingly so the app doesn't crash
        //sometimes
        Log.i("NOTIFPASSED", "notification passed");
        if (true) {
            //found this shit here:
            //http://www.androiddevelopersolutions.com/2015/05/android-read-status-bar-notification.html
            String pack = sbn.getPackageName();
            String ticker = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
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
            Parcelable n = Parcels.wrap(new ParcelableNotification(pack, ticker, title, text));

            //call method from ConnService to send notification to server
            ConnService.sendNotification(this, n);
        }

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
}
