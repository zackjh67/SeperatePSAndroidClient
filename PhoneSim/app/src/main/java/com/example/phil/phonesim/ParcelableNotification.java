package com.example.phil.phonesim;

import org.parceler.Parcel;

/**
 * Created by Zack on 1/29/2018.
 */

//annotiation to use Parceler, an easy way of created Android parcelables
//parcelables are used to transfer Java objects via intents
@Parcel
public class ParcelableNotification {
    public String getNotificationPackage() {
        return nPackage;
    }

    public void setNotificationPackage(String nPackage) {
        this.nPackage = nPackage;
    }

    public String getNotificationTicker() {
        return nTicker;
    }

    public void setNotificationTicker(String nTicker) {
        this.nTicker = nTicker;
    }

    public String getNotificationTitle() {
        return nTitle;
    }

    public void setNotificationTitle(String nTitle) {
        this.nTitle = nTitle;
    }

    public String getNotificationText() {
        return nText;
    }

    public void setNotificationText(String nText) {
        this.nText = nText;
    }

    //attributes of notifications
    String nPackage;
    String nTicker;
    String nTitle;
    String nText;

    public ParcelableNotification(){

    }

    public ParcelableNotification(String nPackage, String nTicker, String nTitle, String nText){
        this.nPackage = nPackage;
        this.nTicker = nTicker;
        this.nTitle = nTitle;
        this.nText = nText;
    }


}

