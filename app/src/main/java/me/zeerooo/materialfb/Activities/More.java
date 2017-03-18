package me.zeerooo.materialfb.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import me.zeerooo.materialfb.Ui.Theme;
import me.zeerooo.materialfb.R;

public class More extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    public static class MoreAndCredits extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(final Bundle Instance) {
            super.onCreate(Instance);
            addPreferencesFromResource(R.xml.more);
            Preference changelog = findPreference("changelog");
            changelog.setOnPreferenceClickListener(this);
        }
        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            switch (key) {
                case "changelog":
                    changelog();
                    return true;
            }
            return false;
        }
        public void changelog() {
            AlertDialog.Builder changelog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view  = inflater.inflate(R.layout.dialog_custom_title, null);
            TextView changelog_title = (TextView)view.findViewById(R.id.title);
            changelog.setCustomTitle(view);
            changelog.setMessage(Html.fromHtml(getResources().getString(R.string.changelog_list)));
            changelog.setCancelable(false);
            changelog.setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface changelog, int id) {
                    // Nothing here :p
                }
            });
            changelog.show();
        }
    }

    @Override
    public void onBackPressed() {
       finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
