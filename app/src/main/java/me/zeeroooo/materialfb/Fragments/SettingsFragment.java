/**
 * Code taken from FaceSlim by indywidualny. Thanks.
 **/
package me.zeeroooo.materialfb.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.Activities.More;
import me.zeeroooo.materialfb.Notifications.NotificationsJS;
import me.zeeroooo.materialfb.Ui.CookingAToast;
import me.zeeroooo.materialfb.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private MainActivity mActivity;
    private SharedPreferences mPreferences;
    private FirebaseJobDispatcher NotificationDispatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        // set onPreferenceClickListener for a few preferences
        Preference Notif, Nav, More;
        Notif = findPreference("notifications_settings");
        Nav = findPreference("navigation_menu_settings");
        More = findPreference("moreandcredits");
        Notif.setOnPreferenceClickListener(this);
        Nav.setOnPreferenceClickListener(this);
        More.setOnPreferenceClickListener(this);

        NotificationDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                switch (key) {
                    case "stop_images":
                        mActivity.mWebView.getSettings().setBlockNetworkImage(prefs.getBoolean(key, false));
                        break;
                    case "location_enabled":
                        System.out.print("jgfn");
                        Log.i("tag", "fgfd");
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        break;
                    case "notif":
                            setScheduler();
                        break;
                    case "notif_interval":
                        NotificationDispatcher.cancelAll();
                        setScheduler();
                        break;
                    default:
                        break;
                }
            }
        };

        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private void setScheduler() {
        if (mPreferences.getBoolean("notif", false)) {
            int time = Integer.parseInt(mPreferences.getString("notif_interval", "60"));
            Job myJob = NotificationDispatcher.newJobBuilder()
                    .setService(NotificationsJS.class)
                    .setTag("MFBNotif")
                    .setLifetime(Lifetime.FOREVER)
                    .setReplaceCurrent(true)
                    .setRecurring(true)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setTrigger(Trigger.executionWindow(time, time))
                    .build();
            NotificationDispatcher.mustSchedule(myJob);
        } else
            NotificationDispatcher.cancelAll();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mActivity.mWebView.getSettings().setGeolocationEnabled(true);
                else
                    CookingAToast.cooking(getActivity(), getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
        }
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
        }

        return false;
    }
}