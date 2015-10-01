package com.unitap.unitap.NFCBackend;

import android.app.Activity;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;

import com.unitap.unitap.Exceptions.NFCException;

/**
 * Created by Brandon Marino on 10/1/2015.
 */
public class NFCPreparation{
    public static NfcAdapter prepare(Activity activity) throws NFCException {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (nfcAdapter == null)
            throw new NFCException("NFC is not available on this device");
        if (!nfcAdapter.isEnabled())
            throw new NFCException("NFC hardware is not enabled on this device");
        if (!nfcAdapter.isNdefPushEnabled())
            throw new NFCException("NDEF Messaging is disabled on this device");

        /*
        This code should allow a message to be pushed from one phone to another. I don't really know how it works, just that it should
         */
        return nfcAdapter;
    }
}
