package com.unitap.unitap.Activities.Abstracted;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.unitap.unitap.Activities.WalletActivity;
import com.unitap.unitap.Activities.testingHCEActivity;
import com.unitap.unitap.Activities.testingNDEFActivity;
import com.unitap.unitap.Exceptions.InheritedExceptions.NFCException;
import com.unitap.unitap.NFCBackend.NFCPreparation;
import com.unitap.unitap.R;

public abstract class NavigationPane extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int contentView;
    private Toolbar toolbar;
    private AlertDialog dialogMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView);
        //initializing a dialog message
        dialogMessage = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     *
     * @param contentView
     */
    protected void setNewContentView(int contentView){
        this.contentView = contentView;
    }

    /**
     *
     * @param title
     */
    protected void setToolbarTitle(String title){
        toolbar.setTitle(title);
    }


    /**
     * This will just make it easier to send an alert to the user when need be
     * @param message some message to display
     */
    protected void dialogMessage(String title, String message){
        dialogMessage.setTitle(title);
        dialogMessage.setMessage(message);
        dialogMessage.show();
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent newIntent;
        if (id == R.id.nav_wallet) {
            newIntent = new Intent(this, WalletActivity.class);
            startActivity(newIntent);
        } else if (id == R.id.nav_testNDEF) {
            newIntent = new Intent(this, testingNDEFActivity.class);
            startActivity(newIntent);
        } else if (id == R.id.nav_testHCE){
            newIntent = new Intent(this, testingHCEActivity.class);
            startActivity(newIntent);
        } else if (id == R.id.nav_loginout) {
            //logout of the current wallet
            if (item.getTitle().equals("Log In")) {
                item.setTitle("Log Out");
            }else{
                item.setTitle("Log In");
            }
        } else if (id == R.id.nav_settings) {
            //go to app settings
        } else if (id == R.id.nav_share) {
            //share the app via facebook and stuff
        } else if (id == R.id.nav_about) {
            //tell them about us... i guess...
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        //ensure NFC is operational on this device
        try{
            NFCPreparation.prepare(this);
        }catch (NFCException nfcException){
            String nfcMessage = nfcException.getMessage();
            if (nfcMessage != null && !nfcMessage.equals(""))
                dialogMessage("NFC Issues!",nfcMessage);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }


    /***********************************************************************************************
     *                      Non-Critical Functions
     ***********************************************************************************************/

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
