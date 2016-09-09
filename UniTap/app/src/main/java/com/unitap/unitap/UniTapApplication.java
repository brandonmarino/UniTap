package com.unitap.unitap;

import android.app.Activity;
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Danny on 27/01/2016.
 */
public class UniTapApplication extends Application{
    private Activity currentActivity;

    public void onCreate(){
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("r05g6kTnuuIxBdl6J5g1skAcGEuNqsEBsvCC8GC2")
                .clientKey("XKvIT81OpBTnTRrP2Uf42C08NVMEP20ez5U6y6Se")
                .server("https://parseapi.back4app.com")
        .build()
        );
        ParseFacebookUtils.initialize(this);
    }

    /**
     * Gets current activity in foreground
     * @return activity
     */
    public Activity getCurrentActivity(){
        return currentActivity;
    }

    /**
     * Sets activity in foreground
     * @param currentActivity to set
     */
    public void setCurrentActivity(Activity currentActivity){
        this.currentActivity = currentActivity;
    }

}
