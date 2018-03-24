package com.example.phil.phonesim;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.google.gson.Gson;

import org.parceler.Parcels;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnService extends IntentService {
    static Socket controlSocket;
    static DataOutputStream controlOut;
    static DataInputStream controlIn;

    //connection status, persist across all intent service calls
    static boolean connected = false;

    // intent actions
    private static final String ACTION_CONNECT = "com.example.phil.phonesim.action.ACTION_CONNECT";
    private static final String ACTION_DISCONNECT = "com.example.phil.phonesim.action.ACTION_DISCONNECT";
    private static final String ACTION_SEND_NOTIFICATION = "com.example.phil.phonesim.action.ACTION_SEND_NOTIFICATION";


    //broadcast codes passed to activity
    public static final String BROADCAST_CONNECTED = "com.example.phil.phonesim.extra.BROADCAST_CONNECTED";
    public static final String BROADCAST_DISCONNECTED = "com.example.phil.phonesim.extra.BROADCAST_DISCONNECTED";
    public static final String BROADCAST_NOTIFICATION_SENT = "com.example.phil.phonesim.extra.BROADCAST_NOTIFICATION_SENT";


    //extras passed back broadcast reciever
    public static final String EXTRA_NOTIFICATION = "com.example.phil.phonesim.extra.EXTRA_NOTIFICATION";

    public ConnService() {
        super("ConnService");
    }

    /**
     * adds user id to users node
     * calling this method from another activity sends an intent from
     * that activity to this one
     */
    public static void sendNotification(Context context, Parcelable notification) {
        Intent intent = new Intent(context, ConnService.class);
        intent.setAction(ACTION_SEND_NOTIFICATION);
        intent.putExtra(EXTRA_NOTIFICATION, notification);
        context.startService(intent);
    }

    public static void connect(Context context, String ip, String port){
        Intent intent = new Intent(context, ConnService.class);
        intent.setAction(ACTION_CONNECT);
        intent.putExtra("IP",ip);
        intent.putExtra("port",port);
        context.startService(intent);
    }

    public static void disconnect(Context context){
        Intent intent = new Intent(context, ConnService.class);
        intent.setAction(ACTION_DISCONNECT);
        context.startService(intent);
    }

    /**
    * this is the actual logic that the sendNotification intent method invokes
    *
    * sends notification over network to client
    *
    * */
    private void sendNotificationOverNetwork(Parcelable notification) {
        if(connected) {
            try {

                //unwrap parcelable notification to access the data
                ParcelableNotification n = Parcels.unwrap(notification);
                //TODO maybe use JSON format for messages, for now we will just pass the full
                //text of the notification as a String
                String test = n.getNotificationText();
                Gson gson = new Gson();
                test = gson.toJson(n);
                sendMessage(controlOut,test +"\r\n");
                //return intent with result code signalling message passed
                Intent result = new Intent(BROADCAST_NOTIFICATION_SENT);

                //send intent to broadcast receiver
                LocalBroadcastManager.getInstance(this).sendBroadcast(result);

            } catch (Exception e) {
                //If you can't connect its a bad name or port or something
                Log.d("NOT_ERROR", e.toString());
            }
        }
    }

    private void connectToNetwork(String myIP, String myPort) {
        try{
            //Try to Create Control Socket for connection
            controlSocket = new Socket(myIP,Integer.parseInt(myPort));

                    /* data passed to server */
            controlOut =
                    new DataOutputStream(
                            new BufferedOutputStream(
                            controlSocket.getOutputStream()));
            sendMessage(controlOut,"Connect");
                    /* data passed to client */
            controlIn =
                    new DataInputStream(
                            new BufferedInputStream(
                                    controlSocket.getInputStream()));
            String myResponse = getMessage(controlIn);
            if(myResponse.contains("404 OK")) {
                //If you made it this far your connected
                Log.i("connected", "connected to the server");
                connected = true;

                //return intent with result code signalling connection successful
                Intent result = new Intent(BROADCAST_CONNECTED);

                //send intent to broadcast receiver
                LocalBroadcastManager.getInstance(this).sendBroadcast(result);
            }else{
                Log.e("BADSERVER", "bad server response?");
                throw new Exception("Bad Server Response");
            }
        } catch (Exception e){
            //If you can't connect its a bad name or port
            Log.d("Socket connect error", e.toString());
        }
    }

    private String getMessage(DataInputStream myStream)  throws IOException {
        byte[] lenBytes = new byte[4];
        int numberOfBytesRead = 0;
        StringBuilder myCompleteMessage = new StringBuilder();
        //do{
        //   numberOfBytesRead = myStream.read(myReadBuffer,0,myReadBuffer.length);
        //   myCompleteMessage.append(new String(myReadBuffer,"UTF-8"));
        //}while(myStream.available() > 0);
        myStream.read(lenBytes,0,4);
        numberOfBytesRead = bytesToInt(lenBytes);
        byte[] myReadBuffer = new byte[numberOfBytesRead];
        myStream.read(myReadBuffer, 0, numberOfBytesRead);
        myCompleteMessage.append(new String(myReadBuffer,"UTF-8"));
        return myCompleteMessage.toString();
    }
    private int bytesToInt(byte[] byteValue){
        int value = 0;
        for(int i = 0; i < byteValue.length; i++){
            value += ((int) byteValue[i] & 0xffL) << (8 * i);
        }
        return value;
    }
    private void sendMessage(DataOutputStream myStream, String myMessage) throws IOException {
        byte[] myMessageByte = myMessage.getBytes(("UTF-8"));
        int numberOfBytesWrite = myMessageByte.length;
        byte[] toSendLenBytes = new byte[4];
        toSendLenBytes[0] = (byte)(numberOfBytesWrite & 0xff);
        toSendLenBytes[1] = (byte)((numberOfBytesWrite >> 8) & 0xff);
        toSendLenBytes[2] = (byte)((numberOfBytesWrite >> 16) & 0xff);
        toSendLenBytes[3] = (byte)((numberOfBytesWrite >> 24) & 0xff);
        myStream.write(toSendLenBytes);
        myStream.write(myMessageByte);
        myStream.flush();
    }

    private void disconnectNetwork(){
        if(connected){
            try {
                sendMessage(controlOut,"Disconnect\r\n");
                connected  = false;
                controlOut.close();
                controlSocket.close();

                //return intent with result code signalling connection closed
                Intent result = new Intent(BROADCAST_DISCONNECTED);

                //send intent to broadcast receiver
                LocalBroadcastManager.getInstance(this).sendBroadcast(result);
            } catch(Exception e){
                Log.d("Socket close error", e.toString());
            }
        }
        else{
            Log.d("Socket","Not connected??");
        }
    }

    /*
    * this method handles the recursive intents passed to this service
    * */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            //if action is adding user
            if (ACTION_SEND_NOTIFICATION.equals(action)) {
                //get notification object from intent
                final Parcelable notification = intent.getParcelableExtra(EXTRA_NOTIFICATION);
                //call internal method that sends it over network
                sendNotificationOverNetwork(notification);
            }
            if(ACTION_CONNECT.equals(action)){
                //internal method that connects to a network
                String myIP = intent.getStringExtra("IP");
                String port = intent.getStringExtra("port");
                connectToNetwork(myIP, port);
            }
            if(ACTION_DISCONNECT.equals(action)){
                //internal method to disconnect from network
                disconnectNetwork();
            }
        }
    }
}
