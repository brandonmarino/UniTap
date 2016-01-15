package com.unitap.unitap.NFCBackend.HCE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;
import com.unitap.unitap.Activities.HCEActivity;


/**The main HCE activity will use this Adapter in order to send data to the other device.  From the external system's view, this will function will simply pass messages and notify the activity when a new message is received
 * Created by Brandon Marino on 1/6/2016.
 */
public class HCEAdapter /*implements NfcAdapter.ReaderCallback, IsoDepTransceiver.OnMessageReceived*/ {

    private HCEActivity activity;
    private static boolean active = false;

    public HCEAdapter (HCEActivity activity) {
        this.activity = activity;
    }

    /**
     * Dorty workaround which I hate.
     * @return if the apdu service can handle connections
     */
    public static boolean isActive(){
        return active;
    }

    public void sendMessage(String message){
        Intent intent = new Intent("unitap.action.NOTIFY_HCE_DATA");
        intent.putExtra("hcedata", message);
        activity.sendBroadcast(intent);
    }

    public void enableReading(){
        active = true;
        registerBroadcastReceiver();
    }
    public void disableReader() {
        active = false;
        deregisterBroadcastReceiver();
    }

    /***********************************************************************************************
     *               Receive Messaging between Activities
     ***********************************************************************************************/

    /**
     * Receive some data (Message) from an external Activity.  Used for sending direct messages
     */
    final BroadcastReceiver hceNotificationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String hcedata = intent.getStringExtra("hcedata");
            activity.update(hcedata);
        }
    };

    /**
     * Register a broadcast receiver for this Service
     */
    private void registerBroadcastReceiver(){
        final IntentFilter hceNotificationsFilter = new IntentFilter();
        hceNotificationsFilter.addAction("unitap.action.NOTIFY_MAIN_DATA");
        activity.registerReceiver(hceNotificationsReceiver, hceNotificationsFilter);
        Log.v("Registering Receiver", "MAIN Receiver");
    }
    private void deregisterBroadcastReceiver(){
        activity.unregisterReceiver(hceNotificationsReceiver);
    }
}
