package com.unitap.unitap.NFCBackend.NDEF.Messaging;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unitap.unitap.NFCBackend.NDEF.Messaging.NFCMessaging;
import com.unitap.unitap.R;
import com.unitap.unitap.Activities.Abstracted.NavigationPane;

/**
 * In order to run this app, on your computer, you'll need to download the files here:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * and store them here:
 * {jdk}/jre/lib/security
 *
 * This is to allow the app to use 256 bit encryption (because the generated UUID is 32 bits)
 */
public class testingNDEFActivity extends NavigationPane {

    /***********************************************************************************************
     *                      App State Functions (Create, Pause, Resume)
     ***********************************************************************************************/

    /**
     * This is what happens when it app is created. all functionality happens once for the lifetime of the app (Or I think that's how it works)
     * @param savedInstanceState this is what makes me think the above statement is not correct
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setNewContentView(R.layout.activity_testing_ndef);
        super.onCreate(savedInstanceState);
        setToolbarTitle("NDEF Testing");
    }

    @Override
    public void onResume() {
        super.onResume();
        //ensure NDEF is operational on this device
        String ndefFailure = NDEFprepare();
        if( !ndefFailure.isEmpty() )
            dialogMessage("NFC Issues!", ndefFailure);
    }

    private String NDEFprepare() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
            return "NFC is not available on this device";
        if (!nfcAdapter.isEnabled())
            return "NFC hardware is not enabled on this device";
        if (!nfcAdapter.isNdefPushEnabled())
            return "NDEF Messaging is disabled on this device";

        /*
        This code should allow a message to be pushed from one phone to another. I don't really know how it works, just that it should
         */
        return "";
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
            NFCMessaging.send(this, outgoingMessage.getText().toString());
        } else
            dialogMessage("It's Empty", "You can't send an empty message!");
    }
}
