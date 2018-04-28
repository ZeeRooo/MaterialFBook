/**
 * Code taken from FaceSlim by indywidualny. Thanks.
 **/
package me.zeeroooo.materialfb.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import java.util.Locale;

import me.zeeroooo.materialfb.Activities.More;
import me.zeeroooo.materialfb.BuildConfig;
import me.zeeroooo.materialfb.Notifications.Scheduler;
import me.zeeroooo.materialfb.Ui.CookingAToast;
import me.zeeroooo.materialfb.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences mPreferences;
    private Scheduler mScheduler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mScheduler = new Scheduler(getActivity());

        // set onPreferenceClickListener for a few preferences
        findPreference("notifications_settings").setOnPreferenceClickListener(this);
        findPreference("navigation_menu_settings").setOnPreferenceClickListener(this);
        findPreference("moreandcredits").setOnPreferenceClickListener(this);
        findPreference("location_enabled").setOnPreferenceClickListener(this);
        findPreference("save_data").setOnPreferenceClickListener(this);
        findPreference("notif").setOnPreferenceClickListener(this);

        findPreference("localeSwitcher").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Locale locale = new Locale(o.toString());
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                return true;
            }
        });
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "notifications_settings":
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
                startActivity(new Intent(getActivity(), More.class));
                return true;
            case "location_enabled":
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return true;
            case "save_data":
                setScheduler();
                return true;
            case "notif":
                setScheduler();
                return true;
        }
        return false;
    }

    private void setScheduler() {
        if (mPreferences.getBoolean("notif", false) && !mPreferences.getBoolean("save_data", false))
            mScheduler.schedule(Integer.parseInt(mPreferences.getString("notif_interval", "60000")), true);
        else
            mScheduler.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    CookingAToast.cooking(getActivity(), getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
        }
    }
}