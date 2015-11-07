package com.unitap.unitap.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.DataControl.ExtensibleMarkupLanguage;
import com.unitap.unitap.DataControl.FileIO;
import com.unitap.unitap.Encryption.AdvancedEncryptionStandard;
import com.unitap.unitap.R;
import com.unitap.unitap.Wallet.Tag;
import com.unitap.unitap.Wallet.Wallet;

import java.io.File;
import java.util.UUID;

import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import me.drakeet.materialdialog.MaterialDialog;

public class WalletActivity extends NavigationPane {

    protected static final int REQUEST_CODE = 100;
    private Wallet wallet;
    private File walletCache;   //file store encrypted xml equivalent of the user's wallet
    private AdvancedEncryptionStandard crypt;
    private Activity wActivity;
    CardArrayAdapter mCardArrayAdapter;
    MaterialDialog mMaterialDialog;
    String nameOfNewCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setNewContentView(R.layout.activity_wallet);
        super.onCreate(savedInstanceState);
        //Title in Actionbar
        setToolbarTitle("wallet");

        //location of the walletCache File
        walletCache = new File(this.getFilesDir(),"walletCache.ut");//creates a file which only this app can access
        wActivity = this;

        //find device id for copy protection/encryption purposes
        //gather defining device IDs
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //get UUID from this information (posthashing)
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() <<32) | tmSerial.hashCode());
        String key = deviceUuid.toString();
        key = key.replace("-","");  //get rid of padding '-' characters
        //store key in encryption object
        crypt= new AdvancedEncryptionStandard(key);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText cardName = new EditText(wActivity);
                mMaterialDialog = new MaterialDialog(wActivity)
                        .setTitle("Add Card")
                        .setContentView(cardName)
                        .setPositiveButton("Add", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nameOfNewCard = cardName.getText().toString();
                                Tag tag = new Tag(wActivity, nameOfNewCard, "payload");
                                wallet.addTag(tag);
                                addCard(tag);
                                mMaterialDialog.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nameOfNewCard = "";
                                mMaterialDialog.dismiss();
                            }
                        });

                mMaterialDialog.show();
            }
        });

        //Make test Wallet
        wallet = new Wallet("Dan");

        Tag tag = new Tag(this, "dan", "this is the payload to be send by nfc and stuff");
        wallet.addTag(tag);
        addCard(tag);

        //assign wallet to cardAdapter
        mCardArrayAdapter = new CardArrayAdapter(this, wallet);

        //
        CardListView listView = (CardListView) findViewById(R.id.myList);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }

    }

    /**
     * What to do whenever the app is paused/exited for any reason
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
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

        //ensure that this device supports the H.C.E. NFC feature
        //PackageManager pm = context.getPackageManager();

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

    public void addCard(Tag tag){
        //logo on card
        int image = R.drawable.tagstand_logo_icon;

        //Make card header
        CardHeader header = new CardHeader(this);
        header.setTitle(tag.getName());
        tag.addCardHeader(header);

        //Stuff with card thumbnail
        CardThumbnail thumbNail = new CardThumbnail(this);
        thumbNail.setDrawableResource(image);
        tag.addCardThumbnail(thumbNail);
    }
}
