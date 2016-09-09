package com.unitap.unitap.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.unitap.unitap.NFCBackend.HCE.HCEAdapter;
import com.unitap.unitap.R;
import com.unitap.unitap.Wallet.Tag;

import java.io.Serializable;
import java.util.UUID;

public class CardActivity extends AppCompatActivity {
    private HCEAdapter hceAdapter;
    private Tag tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startTransaction();
    }

    /**
     * Method to retrieve card data from wallet activity and begin transaction of data
     * when card is tapped to terminal
     */
    private void startTransaction(){
        ImageView cardImageView;
        hceAdapter = new HCEAdapter(this);
        hceAdapter.enableReading();
        //String cardName = getIntent().getStringExtra("cardName");
        //int cardImage = getIntent().getIntExtra("cardImage", 0);\

        Serializable serObj = getIntent().getSerializableExtra("unitap.unitap.serializableObject");
        if(serObj instanceof Tag) {
            tag = (Tag)serObj;
            TextView cardNameView = (TextView) findViewById(R.id.textView2);
            String cardName = tag.getName();
            Bitmap cardImage = tag.getImage(this);
            cardNameView.setText(cardName);

            cardImageView = (ImageView) findViewById(R.id.imageView2);
            cardImageView.setImageBitmap(cardImage);
            //cardImageView.setImageResource(cardImage);
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.animation);
            cardImageView.startAnimation(myFadeInAnimation);
        }else{
            Log.e("Error: ","Extra not found");
        }

    }
    public void update(String replyFromServer){
        //do something with the message reply
    }

    @Override
    public void onResume() {
        hceAdapter.enableReading();

        hceAdapter.provisioService(tag.getPayload(), tag.getCompanyID(), generateKey());
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

    private byte[] generateKey(){
        String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), "hello world".hashCode() /*((long)tmDevice.hashCode() <<32) | tmSerial.hashCode()*/); //get UUID from this information (posthashing)
        String key = deviceUuid.toString();
        key = key.replace("-", "");  //get rid of padding '-' characters
        return key.getBytes();
    }
}
