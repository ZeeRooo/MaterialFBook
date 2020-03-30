package me.zeeroooo.materialfb.fragments;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import me.zeeroooo.materialfb.ui.MFBDialog;

public class MFBPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof ListPreference) {
            new MFBDialog(getActivity(), preference).show();
           /* DialogFragment dialogFragment = new MFBPreferenceDialog(preference);// MFBPreferenceDialog.newInstance(preference);
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), null);*/
        } else super.onDisplayPreferenceDialog(preference);
    }
}
