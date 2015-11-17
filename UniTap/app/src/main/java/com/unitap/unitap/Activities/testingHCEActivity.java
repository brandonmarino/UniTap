package com.unitap.unitap.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.cardemulation.HostApduService;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.DataControl.FileIO;
import com.unitap.unitap.NFCBackend.HCE.IsoDepAdapter;
import com.unitap.unitap.NFCBackend.HCE.IsoDepTransceiver;

import com.unitap.unitap.NFCBackend.HCE.IsoDepTransceiver.OnMessageReceived;
import android.nfc.NfcAdapter.ReaderCallback;

import com.unitap.unitap.NFCBackend.HCE.MyHostApduService;
import com.unitap.unitap.R;

import java.io.File;

/**
 * In order to run this app, on your computer, you'll need to download the files here:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * and store them here:
 * {jdk}/jre/lib/security
 * <p/>
 * This is to allow the app to use 256 bit encryption (because the generated UUID is 32 bits)
 */
public class testingHCEActivity extends NavigationPane implements OnMessageReceived, ReaderCallback {

    private NfcAdapter nfcAdapter;
    private IsoDepAdapter isoDepAdapter;
    //private HostApduService myHostApduService = new MyHostApduService();
    Intent serviceIntent;
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
        //listView.setAdapter(isoDepAdapter);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        serviceIntent = new Intent(this, MyHostApduService.class);
    }

    /**
     * What the app does once the phone enters the app (Does this the first time the app is launched, and then every time after that)
     */
    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null);

        //serviceIntent.putExtra("hello world", "android.intent.extra.TEXT");
        startService(serviceIntent);

        /*
        boolean apduRunning = isMyServiceRunning( (new MyHostApduService() ).getClass());
        if (apduRunning)
            dialogMessage("Service Operating", "Service is running in the backgroud");
        else
            dialogMessage("Service Not Operating", "Service is dead");
        */
    }

    /**
     * What to do when the application pauses
     */
    @Override
    public void onPause() {
        super.onPause();
        stopService(serviceIntent);
        nfcAdapter.disableReaderMode(this);
    }

    /**
     * Check if the HostAPDUService is currently running
     * @param serviceClass
     * @return
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
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
    /*NOTE: The above code is probably going to be moved to a superclass, only work with local 'private' methods*/

    /**
     * Send a message to another HCE device
     * @param message
     */
    private void sendMessage(String message){
        onMessage(message.getBytes());
    }

    /***********************************************************************************************
     *                      Button-Action Handlers
     ***********************************************************************************************/

    /**
     * Main button in testingNDEFActivity.  This will handle messaging on the proof of concept
     *
     * @param v
     */
    public void clickableButton(View v) {
        //user has chosen to send a message to another device
        EditText outgoingMessage = (EditText) findViewById(R.id.outgoing);
        TextView incomingMessage = (TextView) findViewById(R.id.incoming);

        String out = outgoingMessage.getText().toString();
        if (!out.equals("")) {
            String toTextBox = "Your last sent Message: \n\n" + outgoingMessage.getText().toString();
            incomingMessage.setText(toTextBox);
            //perform the sending function
            //sendMessage(outgoingMessage.getText().toString());
            //myHostApduService.setMessage("");
            //NFCSendMessage.send(this, outgoingMessage.getText().toString());
        } else
            dialogMessage("It's Empty", "You can't send an empty message!");
    }
}
