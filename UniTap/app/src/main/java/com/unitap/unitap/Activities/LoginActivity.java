package com.unitap.unitap.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login_activity);
        super.onCreate(savedInstanceState);

        activity = this;

        login = (Button) findViewById(R.id.parse_login_button);
        register = (Button) findViewById(R.id.parse_signup_button);
        username = (EditText) findViewById(R.id.login_username_input);
        password = (EditText) findViewById(R.id.login_password_input);

        if(ParseUser.getCurrentUser() != null) {
            loadWalletActivity();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e == null) {
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
}
