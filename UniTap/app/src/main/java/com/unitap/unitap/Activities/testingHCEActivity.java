package com.unitap.unitap.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.Exceptions.InheritedExceptions.NFCException;
import com.unitap.unitap.NFCBackend.Messaging.NFCSendMessage;
import com.unitap.unitap.NFCBackend.NFCPreparation;
import com.unitap.unitap.R;

/**
 * In order to run this app, on your computer, you'll need to download the files here:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * and store them here:
 * {jdk}/jre/lib/security
 * <p/>
 * This is to allow the app to use 256 bit encryption (because the generated UUID is 32 bits)
 */
public class testingHCEActivity extends NavigationPane {

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

        //
    }


    /**
     * What the app does once the phone enters the app (Does this the first time the app is launched, and then every time after that)
     */
    @Override
    public void onResume() {
        super.onResume();
        //test for hce functionality/ do hce preparation stuff
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
            NFCSendMessage.send(this, outgoingMessage.getText().toString());
        } else
            dialogMessage("It's Empty", "You can't send an empty message!");
    }
}
