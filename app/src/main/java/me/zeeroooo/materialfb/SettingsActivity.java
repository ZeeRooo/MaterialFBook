/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks.
 * - Toffed by JakeLane. Thanks.
 */
package me.zeeroooo.materialfb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import me.zeeroooo.materialfb.Fragments.SettingsFragment;
import me.zeeroooo.materialfb.Ui.Theme;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_STOP_IMAGES = "stop_images";
    public static final String KEY_PREF_FAB_SCROLL = "hide_fab_on_scroll";
    public static final String KEY_PREF_MESSAGING = "messaging_enabled";
    public static final String KEY_PREF_LOCATION = "location_enabled";
    public static final String KEY_PREF_HIDE_MENU_BAR = "hide_menu_bar";
    public static final String KEY_PREF_HIDE_EDITOR = "hide_editor_newsfeed";
    public static final String KEY_PREF_HIDE_SPONSORED = "hide_sponsored";
    public static final String KEY_PREF_HIDE_BIRTHDAYS = "hide_birthdays";
    public static final String KEY_PREF_NAV_GROUPS = "nav_groups";
    public static final String KEY_PREF_NAV_SEARCH = "nav_search";
    public static final String KEY_PREF_NAV_MAINMENU = "nav_mainmenu";
    public static final String KEY_PREF_NAV_MOST_RECENT = "nav_most_recent";
    public static final String KEY_PREF_NAV_FBLOGOUT = "nav_fblogout";
    public static final String KEY_PREF_NAV_EVENTS = "nav_events";
    public static final String KEY_PREF_NAV_BACK = "nav_back";
    public static final String KEY_PREF_NAV_PHOTOS = "nav_photos";
    public static final String KEY_PREF_NAV_TOP_STORIES = "nav_top_stories";
    public static final String KEY_PREF_NAV_NEWS = "nav_news";
    public static final String KEY_PREF_NAV_FRIENDREQ = "nav_friendreq";
    public static final String KEY_PREF_NAV_EXIT = "nav_exitapp";
    public static final String KEY_PREF_SAVE_DATA = "save_data";
    public static final String KEY_PREF_NOTIF_NOTIF = "notifications_activated";
    public static final String KEY_PREF_NOTIF_MESSAGE = "message_notifications";
    public static final String KEY_PREF_HIDE_NEWS_FEED = "hide_newsfeed";
    public static final String KEY_PREF_BNV_SHIFTING = "shifting";
    public static final String KEY_PREF_BNV_ONLYICONS = "icons_only";
    public static final String KEY_PREF_BNV_ITEM_SHIFTING = "item_shifting";
    public static final String KEY_PREF_BNV_ONLYTEXT = "only_text";

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private AppCompatActivity MaterialFBookAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MaterialFBookAct = this;
        boolean MFB = Theme.getInstance(this).setTheme().equals("MFB");
        final boolean Pink = Theme.getInstance(this).setTheme().equals("Pink");
        final boolean Grey = Theme.getInstance(this).setTheme().equals("Grey");
        final boolean Green = Theme.getInstance(this).setTheme().equals("Green");
        final boolean Red = Theme.getInstance(this).setTheme().equals("Red");
        final boolean Lime = Theme.getInstance(this).setTheme().equals("Lime");
        final boolean Yellow = Theme.getInstance(this).setTheme().equals("Yellow");
        final boolean Purple = Theme.getInstance(this).setTheme().equals("Purple");
        final boolean LightBlue = Theme.getInstance(this).setTheme().equals("LightBlue");
        final boolean Black = Theme.getInstance(this).setTheme().equals("Black");
        final boolean Orange = Theme.getInstance(this).setTheme().equals("Orange");
        final boolean GooglePlayGreen = Theme.getInstance(this).setTheme().equals("GooglePlayGreen");
        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (MFB)
                setTheme(R.style.MFB);
        } else {
            if (Pink)
                setTheme(R.style.Pink);
            if (Grey)
                setTheme(R.style.Grey);
            if (Green)
                setTheme(R.style.Green);
            if (Red)
                setTheme(R.style.Red);
            if (Lime)
                setTheme(R.style.Lime);
            if (Yellow)
                setTheme(R.style.Yellow);
            if (Purple)
                setTheme(R.style.Purple);
            if (LightBlue)
                setTheme(R.style.LightBlue);
            if (Black)
                setTheme(R.style.Black);
            if (Orange)
                setTheme(R.style.Orange);
            if (GooglePlayGreen)
                setTheme(R.style.GooglePlayGreen);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            getFragmentManager().beginTransaction().replace(R.id.content_frame,
                    new SettingsFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else
            getFragmentManager().popBackStack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

}