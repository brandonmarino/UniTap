package com.unitap.unitap.NFCBackend.HCE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class UnitapApduService extends HostApduService {

    /***********************************************************************************************
     *                      App State Functions (Create, Pause, Resume)
     ***********************************************************************************************/

    private String lastMessage = "";
    private boolean enabled = false;

    /**
     * What happens when the service is first run using startService()
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * When the service is destroyed using stopService()
     */
    @Override
    public void onDestroy(){
        //Log.v("Deregister Receiver", "HCE Receiver + State-Change Receiver");
        unregisterReceiver(hceNotificationsReceiver);   //remove the broadcast reciever
        unregisterReceiver(stateChangeService);     //remove the state-change receiver
        enabled = false;
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
        registerBroadcastReceivers(); //register broadcasts receivers to handle communication
        if (HCEAdapter.isActive() || apdu == null) {
            String response = new String(apdu);
            if (!response.isEmpty()) {
                Log.v("Resp-Term", new String(apdu));
                if (selectAidApdu(apdu)) {
                    Log.v("Analyzing Response", "Select APDU");
                    //decrypt, etc
                    //if CRC matches
                    return "ACK#Hello".getBytes();
                    //else return ERR#Hello
                } else if (ackApdu(apdu)) {
                    //nothing else needed in the exchange
                    return null;
                } else if (errorApdu(apdu)) {
                    //retry HCE send
                    return lastMessage.getBytes();
                } else if (requestApdu(apdu)) {
                    //send message out
                    sendToBroadcastReceiver(response);
                    return "ACK#Received".getBytes();
                }
            }
            return "ERR#Received".getBytes();
        }
        return "u".getBytes();
    }

    /**
     * Send a response to the terminal
     * @param message
     */
    private void sendToTerminal(String message){
        if (enabled) {
            lastMessage = message;
            //generate CRC and append to message with # separator
            //encrypt message
            sendResponseApdu(message.getBytes());
        }
    }

    /**
     * Check if the apdu is an acknowledgement
     * @param candidate some apdu from a device
     */
    private boolean ackApdu(byte[] candidate){
        String response = new String(candidate);
        return response.regionMatches(0,"ACK#",0,4);
    }

    /**
     * Check if the apdu is an error and the lsat message needs to be resent
     * @param candidate some apdu from a device
     */
    private boolean errorApdu(byte[] candidate){
        String response = new String(candidate);
        return response.regionMatches(0,"ERR#",0,4);
    }

    /**
     * Check if the apdu is an error and the lsat message needs to be resent
     * @param candidate some apdu from a device
     */
    private boolean requestApdu(byte[] candidate){
        String response = new String(candidate);
        return response.regionMatches(0, "REQ#", 0, 4);
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
     *               Receive Messages from another Activity which passed a defined Filter
     ***********************************************************************************************/

    /**
     * Receive some data (Message) from an external Activity.  Used for sending direct messages
     */
    final BroadcastReceiver hceNotificationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String hcedata = intent.getStringExtra("hcedata");
            sendToTerminal(hcedata);
            Log.v("Send-Term", hcedata);
        }
    };

    final BroadcastReceiver stateChangeService =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("ENTERED RECEIVER", "Hello");
            String out = intent.getStringExtra("stateChange");
            if (out == null)
                return;
            else
                Log.v("Message", out);

            if (out.toLowerCase().equals( "TRUE".toLowerCase())) {
                Log.v("Reader State Change:", "ENABLED");
                enabled = true;
            }
            else if (out.toLowerCase().equals("FALSE".toLowerCase())) {
                Log.v("Reader State Change:", "DISABLED");
                enabled = false;
            }
        }
    };

    /**
     * Register a broadcast receiver for this Service
     * This is to receive messages from the application using this service
     */
    private void registerBroadcastReceivers(){
        final IntentFilter hceNotificationsFilter = new IntentFilter();
        hceNotificationsFilter.addAction("unitap.action.NOTIFY_HCE_DATA");
        registerReceiver(hceNotificationsReceiver, hceNotificationsFilter);
        Log.v("Registering Receiver", "HCE Receiver");

        final IntentFilter stateChangeNotificationsFilter = new IntentFilter();
        stateChangeNotificationsFilter.addAction("unitap.action.READER_STATE_CHANGE");
        registerReceiver(stateChangeService, stateChangeNotificationsFilter);
        Log.v("Registering Receiver", "State Changer");
    }

    /**
     * Send a message out to the adapter/activity which is making use of the HCE peer messaging
     * @param message some message to be send up to the other activity
     */
    public void sendToBroadcastReceiver(String message){
        Intent intent = new Intent("unitap.action.NOTIFY_MAIN_DATA");
        intent.putExtra("hcedata", message);
        sendBroadcast(intent);
    }
}