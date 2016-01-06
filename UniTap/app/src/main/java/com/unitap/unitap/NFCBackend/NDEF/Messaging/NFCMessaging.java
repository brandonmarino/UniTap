package com.unitap.unitap.NFCBackend.NDEF.Messaging;

import android.app.Activity;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;

/**
 * Created by Brandon Marino on 10/1/2015.
 */
public class NFCMessaging {
    /**
     * Send a message to another device via NFC
     * @param activity the main activity
     * @param message a string message to send
     */
    public static void send(Activity activity, final String message){
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        nfcAdapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback()
        {
            /*
             * (non-Javadoc)
             * @see android.nfc.NfcAdapter.CreateNdefMessageCallback#createNdefMessage(android.nfc.NfcEvent)
             */
            @Override
            public NdefMessage createNdefMessage(NfcEvent event)
            {
                NdefRecord uriRecord = NdefRecord.createUri(Uri.encode(message));
                return new NdefMessage(new NdefRecord[] { uriRecord });
            }

        }, activity, activity);
       // new NdefRecord();
       // new NdefMessage();
        return;
    }
}
