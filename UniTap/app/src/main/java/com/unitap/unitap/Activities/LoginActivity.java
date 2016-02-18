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
import com.unitap.unitap.R;

/**
 * Created by Danny on 27/01/2016.
 */
public class LoginActivity extends Activity{

    LoginActivity activity;
    Button login, register;
    EditText username, password;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static boolean permissionNFC = false;
    public static boolean permissionReadPhoneState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login_activity);
        super.onCreate(savedInstanceState);

        activity = this;

        login = (Button) findViewById(R.id.parse_login_button);
        register = (Button) findViewById(R.id.parse_signup_button);
        username = (EditText) findViewById(R.id.login_username_input);
        password = (EditText) findViewById(R.id.login_password_input);

        checkPermissions();

        if(ParseUser.getCurrentUser() != null) {
            loadWalletActivity();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            // Login success, load next activity4
                            loadWalletActivity();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //OPEN NEW FRAGMENT

                ParseUser newUser = new ParseUser();
                newUser.setUsername(username.getText().toString());
                newUser.setPassword(password.getText().toString());
                //newUser.setEmail();
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            /*
                             * Register success, take user back to login activity
                             * done by using finish() to kill current activity
                             * pop toast instructing them to confirm email (turn on email conf.)
                             * change login activity to be first activity to open
                             * user ParseUser.currentUser() != null
                             */
                            ParseUser.logOutInBackground();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void loadWalletActivity() {
        Intent i = new Intent(activity, WalletActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void checkPermissions(){

        permissionNFC = (ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_GRANTED);
        permissionReadPhoneState = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);

        if (!permissionNFC) {
            requestPermission(Manifest.permission.NFC,
                    "UniTap needs access to the NFC capabilities of your device to function properly.\n" +
                            "It is necessary for the application to operate.\n" +
                            "On the following screen, please provide UniTap this permission.");
        }
        if (!permissionReadPhoneState)
            requestPermission(Manifest.permission.READ_PHONE_STATE,
                    "UniTap needs access to the phone's state. This information will be used for encryption purposes.\n" +
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
