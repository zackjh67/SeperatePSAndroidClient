package com.example.phil.phonesim;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;


import com.google.gson.Gson;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;

public class NotificationService extends NotificationListenerService {

    private SparseArray<NotificationHolder> mapOfNotifications;
    private int index = 0;
    private List<String> myList = Arrays.asList(
            "com.google.android.talk",
            "com.android.mms",
            "com.google.android.apps.messaging",
            "com.facebook.orca"
    );

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
//        mapOfNotifications = new SparseArray<NotificationHolder>();
//
//        BroadcastReceiver statusReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(ConnService.BROADCAST_TEST)) {
//                    String str = intent.getStringExtra("TEST");
//
//                    //Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
//                    Log.d("test", "in notif svc yo");
//
//                    ParcelableNotification toWrap = new ParcelableNotification(
//                            "test", "tester", "testest", str);
//                    Parcelable n = Parcels.wrap(toWrap);
//                    ConnService.sendNotification(getApplicationContext(), n);
//                }
//            }
//        };
//        //the certain intent to listen for, ignores all others
//        IntentFilter connectedFilter;
//        //broadcast status code sent through an intent from ConnService
//        connectedFilter = new IntentFilter(ConnService.BROADCAST_TEST);
//        LocalBroadcastManager.getInstance(this).registerReceiver
//                (statusReceiver, connectedFilter);
    }


    /*
     * Overrides default behavior for when user receives a notification and pushes that
     * notification to a different device based on if the user is at their home location or not.
     * */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i("NOTI_FOUND", sbn.getPackageName());
        if (checkValidNotification(sbn.getPackageName())){
            //found this shit here:
            //http://www.androiddevelopersolutions.com/2015/05/android-read-status-bar-notification.html
            /* No Longer NEeded

            Notification.WearableExtender myExtender = new Notification.WearableExtender(sbn.getNotification());
            List<Notification.Action> actions = myExtender.getActions();
            Bundle extras = sbn.getNotification().extras;
            String myArray = "";
            for(Notification.Action act : actions){
                if (act != null && act.getRemoteInputs() != null){
                    NotificationHolder myHolder = new NotificationHolder();
                    myHolder.remInput = act.getRemoteInputs();
                    RemoteInput[] remoteInputs = myHolder.remInput;
                    myHolder.myBundle = sbn.getNotification().extras;
                    myHolder.myIntent = sbn.getNotification().contentIntent;
                    myJSONClass toPass = new myJSONClass(remoteInputs,index,sbn.getTag());
                    mapOfNotifications.append(index,myHolder);
                    index++;
                    myArray = new Gson().toJson(toPass);
                }
            }

            */
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
            /////////////////////////////////////////////////////////
            //this creates a new ParcelableNotification from the given notification components
            //and then wraps it to become a Parcelable object in order for the data to be passed
            //using Intents.
            ParcelableNotification toWrap = new ParcelableNotification(pack, ticker, title, text);
            //toWrap.addActions(myArray);
            Parcelable n = Parcels.wrap(toWrap);

            //call method from ConnService to send notification to server
            ConnService.sendNotification(this, n);

            //Testing FOr Now
            //ReplyToNotification(index - 1, "");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ConnService.disconnect(getApplicationContext());
    }

    private void ReplyToNotification(int indexToRespond, String Message){
//        NotificationHolder respondTo = mapOfNotifications.get(indexToRespond);
//        RemoteInput[] remoteInputs = new RemoteInput[respondTo.remInput.length];
//
//        Intent localIntent = new Intent();
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Bundle localBundle = respondTo.myBundle;
//        int i = 0;
//        for(RemoteInput remoteIn : respondTo.remInput){
//            remoteInputs[i] = remoteIn;
//            localBundle.putCharSequence(remoteInputs[i].getResultKey(), "On Vive right now");//This work, apart from Hangouts as probably they need additional parameter (notification_tag?)
//            i++;
//        }
//        RemoteInput.addResultsToIntent(remoteInputs, localIntent, localBundle);
//        //try {
//            //respondTo.myIntent.send(getApplicationContext(), 0, localIntent);
//        //} catch (PendingIntent.CanceledException e) {
//        //    Log.e("RESPONSE", "replyToLastNotification error: " + e.getLocalizedMessage());
//        //}
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "(616) 808-6005"));
//        intent.putExtra("sms_body", "On Vive right now");
//        startActivity(intent);
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

    private class myJSONClass {
        public List<RemoteInput> myRemotes;
        public int PrimaryKey;
        public String OtherStuff;
        public myJSONClass(RemoteInput[] pArray, int pKey, String pString){
            myRemotes = Arrays.asList(pArray);
            PrimaryKey = pKey;
            OtherStuff = pString;
        }
    }

    private class NotificationHolder {
        public RemoteInput[] remInput;
        public PendingIntent myIntent;
        public Bundle myBundle;
    }
}
