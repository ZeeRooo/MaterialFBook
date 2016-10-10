package me.zeeroooo.materialfb;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.Context;

import me.zeeroooo.materialfb.Notifications.NotificationsService;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_STOP_IMAGES = "stop_images";
    public static final String KEY_PREF_FAB_SCROLL = "hide_fab_on_scroll";
    public static final String KEY_PREF_MESSAGING = "messaging_enabled";
    public static final String KEY_PREF_LOCATION = "location_enabled";
    public static final String KEY_PREF_HIDE_MENU_BAR = "hide_menu_bar";
    public static final String KEY_PREF_HIDE_EDITOR = "hide_editor_newsfeed";
    public static final String KEY_PREF_HIDE_SPONSORED = "hide_sponsored";
    public static final String KEY_PREF_HIDE_BIRTHDAYS = "hide_birthdays";
	public static final String KEY_PREF_HIDE_MESSENGERDOWN = "messenger_download";
	public static final String KEY_PREF_GROUPS = "nav_groups";
	public static final String KEY_PREF_NAV_SEARCH = "nav_search";
	public static final String KEY_PREF_NAV_MAINMENU = "nav_mainmenu";
	public static final String KEY_PREF_NAV_MOST_RECENT = "nav_most_recent";
	public static final String KEY_PREF_NAV_NEWS = "nav_news";
	public static final String KEY_PREF_NAV_FBLOGOUT = "nav_fblogout";
	public static final String KEY_PREF_NAV_EXITAPP = "nav_exitapp";
	public static final String KEY_PREF_NAV_EVENTS = "nav_events";
	public static final String KEY_PREF_NAV_PHOTOS = "nav_photos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.menu_settings);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
    private static Context context;
    private SharedPreferences preferences;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class SettingsFragment extends PreferenceFragment {
        private SharedPreferences preferences;
        private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);

            // listener for changing preferences (works after the value change)
            prefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    // service intent (start, stop)
                    final Intent intent = new Intent(context, NotificationsService.class);

                    switch (key) {
                        case "notifications_activated":
                            if (prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("message_notifications", false)) {
                                context.stopService(intent);
                                context.startService(intent);
				Toast.makeText(getBaseContext, "Restart needed", Toast.LENGTH_SHORT).show();	    
                            } else //noinspection StatementWithEmptyBody
                                if (!prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("message_notifications", false)) {
                                    // ignore this case
                                } else if (prefs.getBoolean("notifications_activated", false) && !preferences.getBoolean("message_notifications", false)) {
                                    context.startService(intent);
                                } else
                                    context.stopService(intent);
                            break;
                        case "message_notifications":
                            if (prefs.getBoolean("message_notifications", false) && preferences.getBoolean("notifications_activated", false)) {
                                context.stopService(intent);
                                context.startService(intent);
				Toast.makeText(getBaseContext, "Restart needed", Toast.LENGTH_SHORT).show();
                            } else //noinspection StatementWithEmptyBody
                                if (!prefs.getBoolean("message_notifications", false) && preferences.getBoolean("notifications_activated", false)) {
                                    // ignore this case
                                } else if (prefs.getBoolean("message_notifications", false) && !preferences.getBoolean("notifications_activated", false)) {
                                    context.startService(intent);
                                } else
                                    context.stopService(intent);
                            break;
                    }
                }
            };
        }
    }
}

