package com.unitap.unitap.Activities;

import android.os.Bundle;
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

public class CardActivity extends AppCompatActivity {
    private HCEAdapter hceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startTransaction();
    }

    private void startTransaction(){
        ImageView cardImageView;
        hceAdapter = new HCEAdapter(this);

        //hceAdapter.enableReading();
        //Log.v("Enabling reading", "ENABLING");

        String cardName = getIntent().getStringExtra("cardName");
        int cardImage = getIntent().getIntExtra("cardImage", 0);


        TextView cardNameView = (TextView) findViewById(R.id.textView2);
        cardNameView.setText(cardName);

        cardImageView = (ImageView) findViewById(R.id.imageView2);
        cardImageView.setImageResource(cardImage);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.animation);
        cardImageView.startAnimation(myFadeInAnimation);

        hceAdapter.sendMessage(cardName);
    }
    public void update(String replyFromServer){
        //do something with the message reply
    }

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
}
