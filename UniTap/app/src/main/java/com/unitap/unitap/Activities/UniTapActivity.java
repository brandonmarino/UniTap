package com.unitap.unitap.Activities;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unitap.unitap.Exceptions.NFCException;
import com.unitap.unitap.NFCBackend.Messaging.SendMessage;
import com.unitap.unitap.NFCBackend.NFCPreparation;
import com.unitap.unitap.R;
import com.unitap.unitap.Wallet.Storage.RestoreTags;
import com.unitap.unitap.Wallet.Storage.SaveTags;
import com.unitap.unitap.Wallet.VirtualWallet;

import java.io.File;

public class UniTapActivity extends AppCompatActivity {

    private EditText outgoingMessage;   //this is where the message from the other user will be stored before it's sent
    private TextView incomingMessage;
    private AlertDialog dialogMessage;
    private VirtualWallet wallet;
    private File walletCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcmessaging);
        dialogMessage = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
        //Outgoing and incoming simply refer to the two things on the main screen of the app (your meessage goes here... thingy)
        outgoingMessage = (EditText) findViewById(R.id.outgoing);
        incomingMessage = (TextView) findViewById(R.id.incoming);
        walletCache = new File(this.getFilesDir(),"walletCache");//creates a file which only this app can access
    }

    @Override
    public void onPause(){
        super.onPause();
        SaveTags.saveTags(walletCache, wallet);
    }

    @Override
    public void onResume(){
        super.onResume();
        wallet = RestoreTags.restoreTags(walletCache);
        try{
            NFCPreparation.prepare(this);
        }catch (NFCException nfcException){
            String nfcMessage = nfcException.getMessage();
            if (nfcMessage != null && !nfcMessage.equals(""))
                dialogMessage("NFC Issues!",nfcMessage);
            return;
        }
        //this dialog will let you know that NFC is ready to use with your device
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
            SendMessage.send(this, outgoingMessage.getText().toString());
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
