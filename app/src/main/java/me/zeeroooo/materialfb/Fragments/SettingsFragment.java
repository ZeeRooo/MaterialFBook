/**
  * Code taken from FaceSlim by indywidualny. Thanks.
  **/
package me.zeeroooo.materialfb.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.greysonparrelli.permiso.Permiso;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.Activities.More;
import me.zeeroooo.materialfb.Notifications.Receiver;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.CookingAToast;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private MainActivity mActivity;
    Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

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
                        if (prefs.getBoolean(key, false)) {
                            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                                @Override
                                public void onPermissionResult(Permiso.ResultSet resultSet) {
                                    if (resultSet.areAllPermissionsGranted()) {
                                        mActivity.mWebView.getSettings().setGeolocationEnabled(true);
                                    } else {
                                        CookingAToast.cooking(mContext, R.string.permission_denied, Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                                    }
                                }
                                @Override
                                public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                                    // TODO Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                                    callback.onRationaleProvided();
                                }
                            }, Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                        break;
                    case "notif":
                        Receiver.ScheduleNotif(mContext, false);
                        break;
                    case "notif_interval":
                        Receiver.ScheduleNotif(mContext, false);
                        break;
                    default:
                        break;
                }
            }
        };

        mPreferences.registerOnSharedPreferenceChangeListener(listener);
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