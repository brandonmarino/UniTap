package com.unitap.unitap.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.unitap.unitap.Activities.Abstracted.AppCompatPreferenceActivity;
import com.unitap.unitap.R;

public class SettingsActivity extends AppCompatPreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String choice = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("themeType", "3");
        chooseTheme(choice);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        String choice = sharedPreferences.getString(key, "themeType");
        chooseTheme(choice);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

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
        setContentView(R.layout.activity_settings);

    }

}

