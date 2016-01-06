package com.unitap.unitap.Activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.unitap.unitap.R;

public class testingHCEActivity extends HCEActivity {

    /**
     * Send a message to another HCE device
     * @param message
     */
    private void sendMessage(String message){
        Intent intent = new Intent("unitap.action.NOTIFY_HCE_DATA");
        intent.putExtra("hcedata", message);
        sendBroadcast(intent);
    }

    /***********************************************************************************************
     *                      Button-Action Handlers
     ***********************************************************************************************/

    /**
     * Main button in testingNDEFActivity.  This will handle messaging on the proof of concept
     *
     * @param v viewable object which has been pressed.  This could be used to differentiate between different buttons
     */
    public void clickableButton(View v) {
        //user has chosen to send a message to another device
        EditText outgoingMessage = (EditText) findViewById(R.id.outgoing);
        TextView incomingMessage = (TextView) findViewById(R.id.incoming);

        String out = outgoingMessage.getText().toString();
        if (!out.equals("")) {
            String toTextBox = "Your last sent Message: \n\n" + outgoingMessage.getText().toString();
            incomingMessage.setText(toTextBox);
            sendMessage(outgoingMessage.getText().toString());
            //perform the sending function
            Log.v("Attempt Change Message", outgoingMessage.getText().toString());
        } else
            dialogMessage("It's Empty", "You can't send an empty message!");
    }
}
