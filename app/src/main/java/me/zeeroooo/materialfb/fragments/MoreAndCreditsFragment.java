package me.zeeroooo.materialfb.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;

import me.zeeroooo.materialfb.BuildConfig;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.ui.MFBDialog;

public class MoreAndCreditsFragment extends MFBPreferenceFragment implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.more);
        findPreference("changelog").setOnPreferenceClickListener(this);
        findPreference("mfb_version").setSummary(getString(R.string.updates_summary, BuildConfig.VERSION_NAME));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "changelog":
                final AlertDialog changelog = new MFBDialog(getActivity()).create();
                changelog.setTitle(getResources().getString(R.string.changelog));
                changelog.setMessage(Html.fromHtml(getResources().getString(R.string.changelog_list)));
                changelog.setCancelable(false);
                changelog.setButton(DialogInterface.BUTTON_POSITIVE, getText(android.R.string.ok), (dialogInterface, i) -> changelog.dismiss());
                changelog.show();
                return true;
        }
        return false;
    }
}