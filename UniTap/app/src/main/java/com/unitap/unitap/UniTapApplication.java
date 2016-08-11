package com.unitap.unitap;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Danny on 27/01/2016.
 */
public class UniTapApplication extends Application{

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

}
