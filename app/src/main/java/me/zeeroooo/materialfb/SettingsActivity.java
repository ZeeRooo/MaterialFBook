package me.zeeroooo.materialfb;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_BACK_BUTTON = "back_button_enabled";
    public static final String KEY_PREF_STOP_IMAGES = "stop_images";
    public static final String KEY_PREF_FAB_SCROLL = "hide_fab_on_scroll";
    public static final String KEY_PREF_MESSAGING = "messaging_enabled";
    public static final String KEY_PREF_JUMP_TOP_BUTTON = "jump_top_enabled";
    public static final String KEY_PREF_LOCATION = "location_enabled";
    public static final String KEY_PREF_MOST_RECENT_MENU = "most_recent_menu";
    public static final String KEY_PREF_HIDE_MENU_BAR = "hide_menu_bar";
    public static final String KEY_PREF_HIDE_EDITOR = "hide_editor_newsfeed";
    public static final String KEY_PREF_HIDE_SPONSORED = "hide_sponsored";
    public static final String KEY_PREF_HIDE_BIRTHDAYS = "hide_birthdays";
    public static final String KEY_PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String KEY_PREF_NOTIFICATION_INTERVAL = "notification_interval";
    public static final String KEY_PREF_NOTIFICATION_VIBRATE = "notifications_vibration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.menu_settings);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

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
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
        }
    }
}

