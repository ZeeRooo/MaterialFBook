/**
  * Code taken from FaceSlim by indywidualny. Thanks.
  **/
package me.zeeroooo.materialfb.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import me.zeeroooo.materialfb.Activities.More;
import me.zeeroooo.materialfb.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private Context mContext;
    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mContext = getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // set onPreferenceClickListener for a few preferences
        Preference notificationsSettingsPref = findPreference("notifications_settings");
        Preference navigationmenuSettingsPref = findPreference("navigation_menu_settings");
        Preference More = findPreference("moreandcredits");
        notificationsSettingsPref.setOnPreferenceClickListener(this);
        navigationmenuSettingsPref.setOnPreferenceClickListener(this);
        More.setOnPreferenceClickListener(this);


        // listener for changing preferences (works after the value change)
        prefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // service intent (start, stop)

                switch (key) {
                    case "app_theme":
                        break;
                }
            }
        };
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        switch (key) {
            case "notifications_settings":
                //noinspection ResourceType
                getFragmentManager().beginTransaction()
                        .addToBackStack(null).replace(R.id.content_frame,
                        new NotificationsSettingsFragment()).commit();
                return true;
            case "navigation_menu_settings":
                getFragmentManager().beginTransaction()
                        .addToBackStack(null).replace(R.id.content_frame,
                        new NavigationMenuFragment()).commit();
                return true;
            case "moreandcredits":
                Intent moreandcredits = new Intent(getActivity(), More.class);
                startActivity(moreandcredits);
                return true;
        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(prefChangeListener);
    }

}