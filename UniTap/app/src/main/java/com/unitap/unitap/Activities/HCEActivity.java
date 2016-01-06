package com.unitap.unitap.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;

import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.NFCBackend.HCE.IsoDepAdapter;
import com.unitap.unitap.NFCBackend.HCE.IsoDepTransceiver;

import com.unitap.unitap.NFCBackend.HCE.IsoDepTransceiver.OnMessageReceived;
import android.nfc.NfcAdapter.ReaderCallback;

import com.unitap.unitap.NFCBackend.HCE.UnitapApduService;
import com.unitap.unitap.R;

/**
 * In order to run this app, on your computer, you'll need to download the files here:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * and store them here:
 * {jdk}/jre/lib/security
 * <p/>
 * This is to allow the app to use 256 bit encryption (because the generated UUID is 32 bits)
 */
public class HCEActivity extends NavigationPane implements OnMessageReceived, ReaderCallback {

    private NfcAdapter nfcAdapter;
    private IsoDepAdapter isoDepAdapter;
    Intent APDUService;
    /***********************************************************************************************
     *                      App State Functions (Create, Pause, Resume)
     ***********************************************************************************************/

    /**
     * This is what happens when it app is created. all functionality happens once for the lifetime of the app (Or I think that's how it works)
     *
     * @param savedInstanceState this is what makes me think the above statement is not correct
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setNewContentView(R.layout.activity_testing_hce);
        super.onCreate(savedInstanceState);
        setToolbarTitle("HCE Testing");
        isoDepAdapter = new IsoDepAdapter(getLayoutInflater());
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        APDUService = new Intent(this, UnitapApduService.class);
        APDUService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    }

    /**
     * What the app does once the phone enters the app (Does this the first time the app is launched, and then every time after that)
     */
    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null);
        startService(APDUService);
    }

    /**
     * What to do when the application pauses
     */
    @Override
    public void onPause() {
        super.onPause();
        stopService(APDUService);
        nfcAdapter.disableReaderMode(this);
    }

    /**
     * Check if the HostAPDUService is currently running
     * @param serviceClass
     * @return
     */
    protected boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                //service.clientPackage
                return true;
            }
        }
        return false;
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
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                isoDepAdapter.addMessage(new String(message));
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
        dialogMessage("HCE Error", exception.getMessage() );
    }
}
