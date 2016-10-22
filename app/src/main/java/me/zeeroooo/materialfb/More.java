package me.zeeroooo.materialfb;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class More extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle Instance) {
        super.onCreate(Instance);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MoreAndCredits()).commit();
    }
    public static class MoreAndCredits extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle Instance) {
            super.onCreate(Instance);
            addPreferencesFromResource(R.xml.more);
        }
    }
}