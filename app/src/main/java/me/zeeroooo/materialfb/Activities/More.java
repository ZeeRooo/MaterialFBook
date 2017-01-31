package me.zeeroooo.materialfb.Activities;

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

import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.Theme;

public class More extends AppCompatActivity {
    AppCompatActivity MaterialFBookAct;

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
            setContentView(R.layout.activity_more);

            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
    }
    static class MoreAndCredits extends PreferenceFragment implements Preference.OnPreferenceClickListener {
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
            View view  = inflater.inflate(R.layout.dialog_custom_titile, null);
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
