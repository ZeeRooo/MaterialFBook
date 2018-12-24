/**
 * Code taken from FaceSlim by indywidualny. Thanks.
 **/
package me.zeeroooo.materialfb.fragments;

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

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.activities.MoreActivity;
import me.zeeroooo.materialfb.notifications.NotificationsService;
import me.zeeroooo.materialfb.ui.CookingAToast;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences mPreferences;
    private WorkManager workManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        workManager = WorkManager.getInstance();

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
                startActivity(new Intent(getActivity(), MoreActivity.class));
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
            default:
                return false;
        }
    }

    private void setScheduler() {
        if (mPreferences.getBoolean("notif", false) && !mPreferences.getBoolean("save_data", false))
            workManager.enqueue(new PeriodicWorkRequest.Builder(NotificationsService.class, Integer.valueOf(mPreferences.getString("notif_interval", "60000")), TimeUnit.MILLISECONDS)
                    .setConstraints(new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build())
                    .build());
        else
            workManager.cancelAllWork();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            CookingAToast.cooking(getActivity(), getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
    }
}