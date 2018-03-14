package com.example.phil.phonesim;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.parceler.Parcels;

/**
 * MIT License
 *
 *  Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


public class NotificationService extends NotificationListenerService {
//    public boolean connected = false;
//    Context context;
    /*
    * when service is created, shouldn't necessarily be destroyed according to what
    * I have read.
    * */
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        context.getApplicationContext();
//        //this may need to go into onStart method, onResume method, both, all, or just onCreate.
//        //I'm actually just not sure yet.
//
//        //the certain intent to listen for, ignores all others
//        IntentFilter isConnectedFilter;
//        //broadcast status code sent through an intent from HomeService
//        isConnectedFilter = new IntentFilter(ConnService.BROADCAST_CONNECTED);
//        //registering receiver to start listening for this intent
//        LocalBroadcastManager.getInstance(this).registerReceiver
//                (statusReceiver, isConnectedFilter);
//    }

//intent filter may need to be initialized here instead to ensure it is always registered
    //unsure tho
//   @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
    /*
    * still not totally sure what this does lol
    * */
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    /*
    * Overrides default behavior for when user receives a notification and pushes that
    * notification to a different device based on if the user is at their home location or not.
    * */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        Log.i("NOTIFPASSED", "notification passed");
        if(true) {
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
            //Parcelable n = Parcels.wrap(new ParcelableNotification(pack, ticker, title, text));

            //call method from ConnService to send notification to server
            //ConnService.sendNotification(this, n);
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
    public void onNotificationRemoved(StatusBarNotification sbn){
        Log.i("Msg","Notification Removed");
    }


//    //broadcast receiver for listening for signal to start sending notifications over connection
//    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(ConnService.BROADCAST_CONNECTED)){
//                connected = true;
//            }
//            if(intent.getAction().equals(ConnService.BROADCAST_DISCONNECTED)){
//                connected = false;
//            }
//        }
//    };
}
