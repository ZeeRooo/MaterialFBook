package me.zeeroooo.materialfb.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import me.zeeroooo.materialfb.BuildConfig;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.ui.Theme;
import me.zeeroooo.materialfb.webview.Helpers;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Theme.Temas(this, mPreferences);
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_more);

        Helpers.setLocale(this, R.layout.activity_more);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    public static class MoreAndCredits extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(final Bundle Instance) {
            super.onCreate(Instance);
            addPreferencesFromResource(R.xml.more);
            findPreference("changelog").setOnPreferenceClickListener(this);
            findPreference("mfb_version").setSummary(getString(R.string.updates_summary, BuildConfig.VERSION_NAME));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case "changelog":
                    AlertDialog.Builder changelog = new AlertDialog.Builder(getActivity());
                    changelog.setTitle(getResources().getString(R.string.changelog));
                    changelog.setMessage(Html.fromHtml(getResources().getString(R.string.changelog_list)));
                    changelog.setCancelable(false);
                    changelog.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface changelog, int id) {
                            // Nothing here :p
                        }
                    });
                    changelog.show();
                    return true;
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
