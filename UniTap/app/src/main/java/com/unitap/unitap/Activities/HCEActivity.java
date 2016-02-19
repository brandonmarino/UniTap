package com.unitap.unitap.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.NFCBackend.HCE.HCEAdapter;
import com.unitap.unitap.R;

public class HCEActivity extends NavigationPane {

    private EditText outgoingMessage;
    private TextView hceLog;
    private HCEAdapter hceAdapter;

    /***********************************************************************************************
     *                      App State Functions (Create, Pause, Resume)
     ***********************************************************************************************/

    /**
     * This is what happens when it app is created. all functionality happens once for the lifetime of the app (Or I think that's how it works)
     * @param savedInstanceState this is what makes me think the above statement is not correct
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setNewContentView(R.layout.activity_testing_hce);
        super.onCreate(savedInstanceState);
        setToolbarTitle("HCE Activity");
        //hceAdapter = new HCEAdapter(this);
        hceAdapter.enableReading(); //having this in both the onCreate and onResume could cause issues
        outgoingMessage = (EditText) findViewById(R.id.outgoing);
        hceLog = (TextView) findViewById(R.id.incoming);
        hceLog.setText("H.C.E. Message Log:\n");
    }

    public void update(String message){
        hceLog.append("Term:- " + message+"\n");
    }

    /**
     * What the app does once the phone enters the app (Does this the first time the app is launched, and then every time after that)
     */
    @Override
    public void onResume() {
        hceAdapter.enableReading();
        Log.v("Enabling reading", "ENABLING");
        super.onResume();
    }

    /**
     * What to do when the application pauses
     */
    @Override
    public void onPause() {
        hceAdapter.disableReader();
        Log.v("Disabling reading", "DISABLING");
        super.onPause();
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
        String out = outgoingMessage.getText().toString();
        if (!out.equals("")) {
            hceLog.append("Andr:- " + out + "\n");
            hceAdapter.sendMessage(out);
        } else
            dialogMessage("It's Empty", "You can't send an empty message!");
    }
}
