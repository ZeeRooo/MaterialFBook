package me.zeeroooo.materialfb.fragments;

import android.os.Bundle;

import me.zeeroooo.materialfb.R;

public class NavigationMenuFragment extends MFBPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.navigation_menu_settings);
    }
}