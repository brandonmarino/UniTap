package com.unitap.unitap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NFCMessagingActivity extends AppCompatActivity {

    private EditText outgoingMessage;   //this is where the message from the other user will be stored before it's sent
    private TextView incomingMessage;
    private AlertDialog dialogMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcmessaging);
        outgoingMessage = (EditText) findViewById(R.id.outgoing);
        incomingMessage = (TextView) findViewById(R.id.incoming);
        dialogMessage = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nfcmessaging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void clickableButton(View v){
        //user has chosen to send a message to another device
        String out = outgoingMessage.getText().toString();
        if (out != null && !out.equals("")){
            incomingMessage.setText("Your last sent Message: \n\n" + outgoingMessage.getText().toString());
            //perform the sending function
        }else{
            dialogMessage("It's Empty", "You can't send an empty message!");
        }
    }

    /**
     * This will just make it easier to send an alert to the user when need be
     * @param message some message to display
     */
    public void dialogMessage(String title, String message){
        dialogMessage.setTitle(title);
        dialogMessage.setMessage(message);
        dialogMessage.show();
    }
}
