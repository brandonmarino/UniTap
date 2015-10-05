package com.unitap.unitap.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unitap.unitap.DataControl.FileIO;
import com.unitap.unitap.DataControl.ExtensibleMarkupLanguage;
import com.unitap.unitap.Encryption.AdvancedEncryptionStandard;
import com.unitap.unitap.Exceptions.InheritedExceptions.NFCException;
import com.unitap.unitap.NFCBackend.Messaging.NFCSendMessage;
import com.unitap.unitap.NFCBackend.NFCPreparation;
import com.unitap.unitap.R;
import com.unitap.unitap.Wallet.Wallet;

import java.io.File;
import java.util.UUID;


/**
 * In order to run this app, on your computer, you'll need to download the files here:
 * http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
 * and store them here:
 * {jdk}/jre/lib/security
 *
 * This is to allow the app to use 256 bit encryption (because the generated UUID is 32 bits)
 */
public class UniTapActivity extends AppCompatActivity {

    private AlertDialog dialogMessage;
    private Wallet wallet;  //users wallet to store tags
    private File walletCache;   //file store encrypted xml equivalent of the user's wallet
    private AdvancedEncryptionStandard crypt;

    /***********************************************************************************************
     *                      App State Functions (Create, Pause, Resume)
     ***********************************************************************************************/

    /**
     * This is what happens when it app is created. all functionality happens once for the lifetime of the app (Or I think that's how it works)
     * @param savedInstanceState this is what makes me think the above statement is not correct
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcmessaging);
        dialogMessage = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
        //Outgoing and incoming simply refer to the two things on the main screen of the app (your meessage goes here... thingy)

        walletCache = new File(this.getFilesDir(),"walletCache.ut");//creates a file which only this app can access

        //generating keys for decryption (This will need to become more sophisticated in the future)
        //keys need to be 128bits, 16bytes Therefore 16 characters long.  This ensures AES128.
        //Also, its kind of a dumb restriction put forth by the Java compiler
        //IF THIS IS REMOVED, ALSO REMOVE DEVICE_STATUS FROM MANIFEST PERMISSIONS

        //another idea is to encrypt on the UUID and then Encrypt on a use password
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() <<32) | tmSerial.hashCode());
        String key = deviceUuid.toString();
        key = key.replace("-","");  //get rid of padding '-' characters
        dialogMessage(key, "Size was " + key.length() + " bytes");
        //store key in encryption object
        crypt= new AdvancedEncryptionStandard(key);
    }

    /**
     * What to do whenever the app is paused/exited for any reason
     */
    @Override
    public void onPause(){
        super.onPause();
        /*Write the current wallet to storage.  This could be expanded later in order to store multiple wallets for multiple users
        something like; append filename(username) and then decrypt on the user's password */

        String xml = ExtensibleMarkupLanguage.marshal(wallet);
        String encryptedXml = crypt.encrypt(xml);
        FileIO.saveToFile(walletCache, encryptedXml);
    }

    /**
     * What the app does once the phone enters the app (Does this the first time the app is launched, and then every time after that)
     */
    @Override
    public void onResume(){
        super.onResume();

        //ensure NFC is operational on this device
        try{
            NFCPreparation.prepare(this);
        }catch (NFCException nfcException){
            String nfcMessage = nfcException.getMessage();
            if (nfcMessage != null && !nfcMessage.equals(""))
                dialogMessage("NFC Issues!",nfcMessage);
        }

        //get Stored Wallet
        String encryptedXml = "" + FileIO.readFromFile(walletCache);
        if(!encryptedXml.equals("")) {
            String xml = "" + crypt.decrypt(encryptedXml);
            if(xml.equals(""))
                wallet = ExtensibleMarkupLanguage.unMarshal(xml, wallet.getClass());
        }

        //check if stored wallet is legit/uncorrupted
        if (wallet == null)
            wallet = new Wallet();
    }

    /***********************************************************************************************
     *                      Button-Action Handlers
     ***********************************************************************************************/

    /**
     * Main button in UniTapActivity.  This will handle messaging on the proof of concept
     * @param v
     */
    public void clickableButton(View v) {
        //user has chosen to send a message to another device
        EditText outgoingMessage = (EditText) findViewById(R.id.outgoing);
        TextView incomingMessage = (TextView) findViewById(R.id.incoming);

        String out = outgoingMessage.getText().toString();
        if (!out.equals("")){
            String toTextBox = "Your last sent Message: \n\n" + outgoingMessage.getText().toString();
            incomingMessage.setText(toTextBox);
            //perform the sending function
            NFCSendMessage.send(this, outgoingMessage.getText().toString());
        }else
            dialogMessage("It's Empty", "You can't send an empty message!");
    }

    /***********************************************************************************************
     *                      Non-Critical Functions
     ***********************************************************************************************/

    /**
     * This will just make it easier to send an alert to the user when need be
     * @param message some message to display
     */
    public void dialogMessage(String title, String message){
        dialogMessage.setTitle(title);
        dialogMessage.setMessage(message);
        dialogMessage.show();
    }

    /**
     * Seems pretty useless. We could remove it, but it isn't hurting anything so I left it
     * @param item dont know what this is
     * @return something..
     */
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
}
