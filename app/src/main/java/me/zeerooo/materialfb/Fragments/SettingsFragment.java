/**
  * Code taken from FaceSlim by indywidualny. Thanks.
  **/
package me.zeerooo.materialfb.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import me.zeerooo.materialfb.Activities.MainActivity;
import me.zeerooo.materialfb.Activities.More;
import me.zeerooo.materialfb.Notifications.Receiver;
import me.zeerooo.materialfb.Ui.CookingAToast;
import me.zeerooo.materialfb.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    MainActivity mActivity;
    Context mContext;
    SharedPreferences mPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        addPreferencesFromResource(R.xml.settings);

        // set onPreferenceClickListener for a few preferences
        Preference notificationsSettingsPref = findPreference("notifications_settings");
        Preference navigationmenuSettingsPref = findPreference("navigation_menu_settings");
        Preference More = findPreference("moreandcredits");
        notificationsSettingsPref.setOnPreferenceClickListener(this);
        navigationmenuSettingsPref.setOnPreferenceClickListener(this);
        More.setOnPreferenceClickListener(this);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                switch (key) {
                    case "stop_images":
                        mActivity.mWebView.getSettings().setBlockNetworkImage(prefs.getBoolean(key, false));
                        break;
                    case "location_enabled":
                        RequestLocationPermission();
                        break;
                    case "notif":
                        Receiver.ScheduleNotif(mContext);
                        break;
                    case "notif_interval":
                        Receiver.ScheduleNotif(mContext);
                        break;
                    default:
                        break;
                }
            }
        };

        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private void RequestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mActivity.mWebView.getSettings().setGeolocationEnabled(true);
                } else {
                    CookingAToast.cooking(getActivity(), getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                }
            }
        }
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
}