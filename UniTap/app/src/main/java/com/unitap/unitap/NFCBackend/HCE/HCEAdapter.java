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
public class HCEAdapter implements NfcAdapter.ReaderCallback, IsoDepTransceiver.OnMessageReceived {

    private NfcAdapter nfcAdapter;
    private IsoDepAdapter isoDepAdapter;
    private Intent APDUService;
    private HCEActivity activity;

    public HCEAdapter (HCEActivity activity) {
        this.activity = activity;

        isoDepAdapter = new IsoDepAdapter(activity.getLayoutInflater());
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        APDUService = new Intent(activity, UnitapApduService.class);
        APDUService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    }
    public void sendMessage(String message){
        Intent intent = new Intent("unitap.action.NOTIFY_HCE_DATA");
        intent.putExtra("hcedata", message);
        activity.sendBroadcast(intent);
    }
    public void enableReading(){
        nfcAdapter.enableReaderMode(activity, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        activity.startService(APDUService);
    }
    public void disableReader() {
        activity.stopService(APDUService);
        nfcAdapter.disableReaderMode(activity);
    }
    public void isoDepAddMessage(String message){
        isoDepAdapter.addMessage(message);
    }
    /***********************************************************************************************
     *                      HCE Tag Handlers
     ***********************************************************************************************/

    /**
     * What to do when a tag is discovered
     * @param tag
     */
    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        IsoDepTransceiver transceiver = new IsoDepTransceiver(isoDep, this);
        Thread thread = new Thread(transceiver);
        thread.start();
    }

    /**
     *
     * @param message
     */
    @Override
    public void onMessage(final byte[] message) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                isoDepAddMessage(new String(message));
            }
        });
    }

    /**
     * The HCE implementation has errored at some point
     * @param exception
     */
    @Override
    public void onError(Exception exception) {
        //onMessage(exception.getMessage().getBytes());
        //activity.dialogMessage("HCE Error", exception.getMessage());
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
            //send this data out to the user
            //notify(hcedata);
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
}
