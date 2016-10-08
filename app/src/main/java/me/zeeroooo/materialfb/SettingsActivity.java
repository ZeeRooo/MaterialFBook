package me.zeeroooo.materialfb;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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

