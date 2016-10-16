/**
 * Code taken from FaceSlim by indywidualny. Thanks.
 */
package me.zeeroooo.materialfb.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;
import me.zeeroooo.materialfb.MaterialFBook;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.MainActivity;
import me.zeeroooo.materialfb.Notifications.NotificationsService;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private Context mContext;
    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mContext = MaterialFBook.getContextOfApplication();
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // set onPreferenceClickListener for a few preferences
        Preference notificationsSettingsPref = findPreference("notifications_settings");
        Preference navigationmenuSettingsPref = findPreference("navigation_menu_settings");
        notificationsSettingsPref.setOnPreferenceClickListener(this);
        navigationmenuSettingsPref.setOnPreferenceClickListener(this);

        // listener for changing preferences (works after the value change)
        prefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // service intent (start, stop)
                final Intent intent = new Intent(mContext, NotificationsService.class);

                switch (key) {
                    case "notifications_activated":
                        if (prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("message_notifications", false)) {
                            mContext.stopService(intent);
                            mContext.startService(intent);
                        } else
                            if (!prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("message_notifications", false)) {
                            } else if (prefs.getBoolean("notifications_activated", false) && !preferences.getBoolean("message_notifications", false)) {
                                Toast.makeText(getActivity(), getString(R.string.applying_changes), Toast.LENGTH_SHORT).show();
                                mContext.startService(intent);
                                Toast.makeText(getActivity(), getString(R.string.applying_changes), Toast.LENGTH_SHORT).show();
                            } else
                                mContext.stopService(intent);
                        break;
                    case "message_notifications":
                        if (prefs.getBoolean("message_notifications", false) && preferences.getBoolean("notifications_activated", false)) {
                            mContext.stopService(intent);
                            mContext.startService(intent);
                        } else
                            if (!prefs.getBoolean("message_notifications", false) && preferences.getBoolean("notifications_activated", false)) {
                                // ignore this case
                            } else if (prefs.getBoolean("message_notifications", false) && !preferences.getBoolean("notifications_activated", false)) {
                                mContext.startService(intent);
                            } else
                                mContext.stopService(intent);
                        relaunch();
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
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new NotificationsSettingsFragment()).commit();
                return true;
            case "navigation_menu_settings":
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0)
                        .addToBackStack(null).replace(R.id.content_frame,
                        new NavigationMenuFragment()).commit();
                return true;
        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister the listener
        preferences.unregisterOnSharedPreferenceChangeListener(prefChangeListener);
    }

    // relaunch the app
    private void relaunch() {
        // notify user about relaunching the app
        Toast.makeText(getActivity(), getString(R.string.applying_changes), Toast.LENGTH_SHORT).show();
        // sending intent to onNewIntent() of MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("core_settings_changed", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}