/**
 * Code taken from FaceSlim by indywidualny. Thanks.
 **/
package me.zeeroooo.materialfb.Fragments;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // set onPreferenceClickListener for a few preferences
        Preference Notif, Nav, More, location, stop_img, notif;
        Notif = findPreference("notifications_settings");
        Nav = findPreference("navigation_menu_settings");
        More = findPreference("moreandcredits");
        location = findPreference("location_enabled");
        stop_img = findPreference("stop_images");
        notif = findPreference("notif");
        Notif.setOnPreferenceClickListener(this);
        Nav.setOnPreferenceClickListener(this);
        More.setOnPreferenceClickListener(this);
        location.setOnPreferenceClickListener(this);
        stop_img.setOnPreferenceClickListener(this);
        notif.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
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
            case "stop_images":
                mActivity.mWebView.getSettings().setBlockNetworkImage(mPreferences.getBoolean(key, false));
                return true;
            case "location_enabled":
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return true;
            case "notif":
                if (mPreferences.getBoolean("notif", false))
                    setScheduler(getActivity(), true, mPreferences);
                else
                    setScheduler(getActivity(), false, mPreferences);
                return true;
        }

        return false;
    }

    public static void setScheduler(Context c, boolean enable, SharedPreferences mPreferences) {
        FirebaseJobDispatcher NotificationDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(c));
        if (enable) {
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
}