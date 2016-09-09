package com.unitap.unitap.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ui.ParseLoginBuilder;
import com.parse.ui.ParseLoginDispatchActivity;
import com.unitap.unitap.R;

/**
 * Created by Danny on 27/01/2016.
 */
public class LoginActivity extends ParseLoginDispatchActivity{

    private static final int PERMISSION_REQUEST_CODE = 1;
    public static boolean permissionNFC = false;
    //public static boolean permissionReadPhoneState = false;

    /**
     * Using Parse API, a login screen is generated automatically if a user is not currently
     * logged in, otherwise the user is redirected to the personal wallet immediately
     * @return WalletActivity or ParseLoginActivity
     */
    @Override
    protected Class<?> getTargetClass() {
        checkPermissions();
        return WalletActivity.class;
    }

    private void checkPermissions(){

        permissionNFC = (ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_GRANTED);
        //permissionReadPhoneState = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);

        if (!permissionNFC) {
            requestPermission(Manifest.permission.NFC,
                    "UniTap needs access to the NFC capabilities of your device to function properly.\n" +
                            "It is necessary for the application to operate.\n" +
                            "On the following screen, please provide UniTap this permission.");
        }
        //if (!permissionReadPhoneState)
          //  requestPermission(Manifest.permission.READ_PHONE_STATE,
            //        "UniTap needs access to the phone's state. This information will be used for encryption purposes.\n" +
              //              "It is necessary for the application to operate.\n" +
                //            "On the following screen, please provide UniTap this permission.");

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
                ActivityCompat.requestPermissions(this,
                        new String[]{specificPermission},
                        PERMISSION_REQUEST_CODE);
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

    private void dialogMessage(String title, String message){
        AlertDialog dialogMessage = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
        dialogMessage.setTitle(title);
        dialogMessage.setMessage(message);
        dialogMessage.show();
    }


}
