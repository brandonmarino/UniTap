package com.unitap.unitap.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;

import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.DataControl.ExtensibleMarkupLanguage;
import com.unitap.unitap.DataControl.FileIO;
import com.unitap.unitap.DataControl.AdvancedEncryptionStandard;
import com.unitap.unitap.Exceptions.ProjectExceptions;
import com.unitap.unitap.R;
import com.unitap.unitap.Wallet.Tag;
import com.unitap.unitap.Wallet.Wallet;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import me.drakeet.materialdialog.MaterialDialog;

public class WalletActivity extends NavigationPane {

    protected static final int REQUEST_CODE = 100;
    private Wallet wallet = new Wallet("Some Guy");;  //model
    private ArrayList<Card> cardList = new ArrayList<>();   //view
    private File walletCache;   //file store encrypted xml equivalent of the user's wallet
    private AdvancedEncryptionStandard crypt;
    private Activity wActivity;
    private CardArrayAdapter mCardArrayAdapter;
    private MaterialDialog mMaterialDialog;
    private String nameOfNewCard;
    private String themeChoice;
    private SharedPreferences prefs;
    private PreferenceChangeListener prefListener;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String choice = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("themeType", "3");
        chooseTheme(choice);
        setNewContentView(R.layout.activity_wallet);
        super.onCreate(savedInstanceState);

        //Title in Actionbar
        setToolbarTitle("wallet");

        //location of the walletCache File
        checkPermissions();
        walletCache = new File(this.getFilesDir(),"walletCache.ut");//creates a file which only this app can access
        crypt= new AdvancedEncryptionStandard(getKey()); //store key in encryption object
        wActivity = this;

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
                                Tag tag = new Tag(nameOfNewCard, "payload");
                                addCard(tag);
                                saveWallet();
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
         restoreWallet();

        //assign cardList to cardAdapter
        mCardArrayAdapter = new CardArrayAdapter(this, cardList);

        CardListView listView = (CardListView) findViewById(R.id.myList);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefListener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(prefListener);

    }

    /**
     * What to do whenever the app is paused/exited for any reason
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        /*Write the current wallet to storage.  This could be expanded later in order to store multiple wallets for multiple users
        something like; append filename(username) and then decrypt on the user's password */
        //saveWallet();
    }

    /**
     * What the app does once the phone enters the app (Does this the first time the app is launched, and then every time after that)
     */
    @Override
    public void onResume(){
        super.onResume();
        //restoreWallet();
    }


    /*********************************************************************************
     *                            Persistence and Encryption
     *********************************************************************************/
    private boolean saveWallet(){
        try {
            String xml = ExtensibleMarkupLanguage.marshal(wallet);
            String encryptedXml = crypt.encrypt(xml);
            FileIO.saveToFile(walletCache, encryptedXml);
            return true;

        }catch(ProjectExceptions e){
            dialogMessage("Save Wallet Error", e.getMessage());
            return false;
        }
    }

    private boolean restoreWallet(){
        //get Stored Wallet
        try {
            String encryptedXml = "" + FileIO.readFromFile(walletCache);
            if (!encryptedXml.equals("")) {
                String xml = "" + crypt.decrypt(encryptedXml);
                if (!xml.equals("")) {
                    Wallet newWallet = ExtensibleMarkupLanguage.unMarshal(xml, (new Wallet()).getClass());

                    //map tags to cards
                    for(Tag currentTag: newWallet.getWallet()){
                        addCard(currentTag);
                    }
                    wallet = newWallet;
                }
            }
        }catch(ProjectExceptions e){
            dialogMessage("Restore Wallet Error", e.getMessage());
            return false;
        }
        return true;
    }

    private String getKey(){
        //find device id for copy protection/encryption purposes
        //gather defining device IDs
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() <<32) | tmSerial.hashCode()); //get UUID from this information (posthashing)
        String key = deviceUuid.toString();
        key = key.replace("-", "");  //get rid of padding '-' characters
        return key;
    }

    /*************************************************************************************************
     *                          User Interface and customization
     ************************************************************************************************/
    public void chooseTheme(String choice){
        int themeId = 0;
        if(choice.equals("1")){
            themeId = R.style.AppTheme_Light;
        }
        if(choice.equals("2")){
            themeId = R.style.AppTheme_Dark;
        }
        if(choice.equals("3")){
            themeId = R.style.AppTheme_NoActionBar;
        }
        setTheme(themeId);
    }

    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
            String choice = prefs.getString(key, "themeType");
            chooseTheme(choice);
            recreate(); //Causing minor hiccup in displaying navigation view on return from settings
        }
    }

    private void addCard(final Tag tag){
        //logo on card
        final int image = R.drawable.tagstand_logo_icon;
        Card newCard = new Card(this, R.layout.content_wallet);

        //Make card header
        CardHeader header = new CardHeader(this);
        header.setTitle(tag.getName());
        newCard.addCardHeader(header);

        //Stuff with card thumbnail
        CardThumbnail thumbNail = new CardThumbnail(this);
        thumbNail.setDrawableResource(image);
        newCard.addCardThumbnail(thumbNail);

        //Make card clickable
        newCard.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(wActivity, CardActivity.class);
                intent.putExtra("cardName", tag.getName());
                intent.putExtra("cardImage", image);
                startActivity(intent);
            }
        });

        //When a card is pressed on for a longer time, a dialog will pop up indicating
        //if user wants to delete or edit card
        newCard.setOnLongClickListener(new Card.OnLongCardClickListener() {
            @Override
            public boolean onLongClick(final Card card, View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(wActivity);
                dialog.setItems(R.array.card_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            final EditText cardName = new EditText(wActivity);
                            mMaterialDialog = new MaterialDialog(wActivity)
                                    .setTitle("Edit Card")
                                    .setContentView(cardName)
                                    .setPositiveButton("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            nameOfNewCard = cardName.getText().toString();
                                            tag.setName(nameOfNewCard);
                                            card.getCardHeader().setTitle(nameOfNewCard);
                                            saveWallet();
                                            card.notifyDataSetChanged();
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
                        if(which == 1){
                            new AlertDialog.Builder(wActivity)
                                    .setTitle("Title")
                                    .setMessage("Do you really want to remove this card?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            cardList.remove(card);
                                            wallet.removeTag(tag);
                                            saveWallet();
                                            mCardArrayAdapter.remove(card);
                                            mCardArrayAdapter.notifyDataSetChanged();
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

        //add items to respective places
        cardList.add(newCard);
        wallet.addTag(tag);
    }

    private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        themeChoice = sharedPrefs.getString("themeType", "3");
    }

    /******************************************************************************************************************
     *                      Permissions
     ******************************************************************************************************************/
    private void checkPermissions(){

        boolean permissionNFC = (ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_GRANTED);
        boolean permissionReadPhoneState = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);

        if (!permissionNFC) {
            requestPermission(Manifest.permission.NFC,
                    "UniTap needs access to the NFC capabilities of your device to function properly.\n" +
                            "It is necessary for the application to operate.\n" +
                            "On the following screen, please provide UniTap this permission.");
        }
        if (!permissionReadPhoneState)
            requestPermission(Manifest.permission.READ_PHONE_STATE,
                    "UniTap needs access to the state. This information will be used for encryption purposes.\n" +
                            "It is necessary for the application to operate.\n" +
                            "On the following screen, please provide UniTap this permission.");

    }
    private boolean requestPermission(String specificPermission, String explanation) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, specificPermission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, specificPermission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                dialogMessage("Permission Request", explanation);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{specificPermission},
                        PERMISSION_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialogMessage("Permission Granted", "The permission was granted. Thank you!");
                } else {
                    dialogMessage("Permission Denied", "UniTap cannot function properly without this permission. Please consider enabling it in the future!");
                }
                break;
        }
    }
}
