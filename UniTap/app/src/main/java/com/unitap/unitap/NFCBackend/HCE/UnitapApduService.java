package com.unitap.unitap.NFCBackend.HCE;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class UnitapApduService extends HostApduService {

    private String newMessage = "";

    /***********************************************************************************************
     *                      App State Functions (Create, Pause, Resume)
     ***********************************************************************************************/

    /**
     * What happens when the service is first run using startService()
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerBroadcastReceiver();
        return START_STICKY;
    }

    /**
     * When the service is destroyed using stopService()
     */
    @Override
    public void onDestroy(){
        Log.v("Deregister Receiver", "HCE Receiver");
        unregisterReceiver(hceNotificationsReceiver);   //remove the broadcastreciever
        super.onDestroy();
    }

    /**
     * Service was deactivated, why?
     * @param reason
     */
    @Override
    public void onDeactivated(int reason) {
        Log.i("HCEDEMO", "Deactivated: " + reason);
    }

    /***********************************************************************************************
     *                      APDU Processing, HCE Messaging
     ***********************************************************************************************/


    /**
     * Receives an APDU (if that APDU was registered to the same AID as the app/service)
     * @param apdu Tag's payload.  Contains a message, some header bytes and the UID
     * @param extras
     * @return some response that we want to give the terminal.  This could be replaced with an ack.
     */
    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (!newMessage.isEmpty()) {
            Log.v("HCEDEMO", "Sending User Message");
            return newMessage.getBytes();
        }

        if (selectAidApdu(apdu)) {

            Log.v("HCEDEMO", "Application selected");
            return "Hello Terminal!".getBytes();
        }
        else {
            //log apdu into logcat
            Log.v("HCEDEMO", "Received " + new String(apdu));
            return "Response From Android".getBytes();
        }
    }

    /**check if this is the very first APDU from the broadcaster
     *
     * @param apdu payload
     * @return if this is a selectApdu
     */
    private boolean selectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
    }

    /***********************************************************************************************
     *                      Receive Messages from another Activity which passed a defined Filter
     ***********************************************************************************************/


    /**
     * Receive some data (Message) from an external Activity.  Used for sending direct messages
     */
    final BroadcastReceiver hceNotificationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (1==1) {
                String hcedata = intent.getStringExtra("hcedata");
                // TODO: do something with the received data
                newMessage = hcedata;
                Log.v("Received HCE-Service", newMessage);
            }
        }
    };

    /**
     * Register a broadcast receiver for this Service
     */
    private void registerBroadcastReceiver(){
        final IntentFilter hceNotificationsFilter = new IntentFilter();
        hceNotificationsFilter.addAction("unitap.action.NOTIFY_HCE_DATA");
        registerReceiver(hceNotificationsReceiver, hceNotificationsFilter);
        Log.v("Registering Receiver", "HCE Receiver");
    }
}