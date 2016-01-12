package com.unitap.unitap.Activities;

import android.app.ActionBar;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.SpinnerAdapter;

import com.unitap.unitap.Activities.Abstracted.AppCompatPreferenceActivity;
import com.unitap.unitap.Activities.Abstracted.NavigationPane;
import com.unitap.unitap.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
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

}


class Bar extends ActionBar {
    @Override
    public void setCustomView(View view) {

    }

    @Override
    public void setCustomView(View view, LayoutParams layoutParams) {

    }

    @Override
    public void setCustomView(int resId) {
    }

    @Override
    public void setIcon(int resId) {

    }

    @Override
    public void setIcon(Drawable icon) {

    }

    @Override
    public void setLogo(int resId) {

    }

    @Override
    public void setLogo(Drawable logo) {

    }

    @Override
    public void setListNavigationCallbacks(SpinnerAdapter adapter, OnNavigationListener callback) {

    }

    @Override
    public void setSelectedNavigationItem(int position) {

    }

    @Override
    public int getSelectedNavigationIndex() {
        return 0;
    }

    @Override
    public int getNavigationItemCount() {
        return 0;
    }

    @Override
    public void setTitle(CharSequence title) {

    }

    @Override
    public void setTitle(int resId) {

    }

    @Override
    public void setSubtitle(CharSequence subtitle) {

    }

    @Override
    public void setSubtitle(int resId) {

    }

    @Override
    public void setDisplayOptions(int options) {
    }

    @Override
    public void setDisplayOptions(int options, int mask) {

    }

    @Override
    public void setDisplayUseLogoEnabled(boolean useLogo) {

    }

    @Override
    public void setDisplayShowHomeEnabled(boolean showHome) {

    }

    @Override
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {

    }

    @Override
    public void setDisplayShowTitleEnabled(boolean showTitle) {

    }

    @Override
    public void setDisplayShowCustomEnabled(boolean showCustom) {

    }

    @Override
    public void setBackgroundDrawable(Drawable d) {

    }

    @Override
    public View getCustomView() {
        return null;
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @Override
    public CharSequence getSubtitle() {
        return null;
    }

    @Override
    public int getNavigationMode() {
        return ActionBar.NAVIGATION_MODE_STANDARD;
    }

    @Override
    public void setNavigationMode(int mode) {

    }

    @Override
    public int getDisplayOptions() {
        return 0;
    }

    @Override
    public Tab newTab() {
        return null;
    }

    @Override
    public void addTab(Tab tab) {

    }

    @Override
    public void addTab(Tab tab, boolean setSelected) {

    }

    @Override
    public void addTab(Tab tab, int position) {

    }

    @Override
    public void addTab(Tab tab, int position, boolean setSelected) {

    }

    @Override
    public void removeTab(Tab tab) {

    }

    @Override
    public void removeTabAt(int position) {

    }

    @Override
    public void removeAllTabs() {

    }

    @Override
    public void selectTab(Tab tab) {

    }

    @Override
    public Tab getSelectedTab() {
        return null;
    }

    @Override
    public Tab getTabAt(int index) {
        return null;
    }

    @Override
    public int getTabCount() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

    }

    @Override
    public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {

    }
}
