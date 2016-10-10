/*
 * Code taken from Folio for Facebook by creativetrendsapps. Thanks.
 */
package me.zeeroooo.materialfb.Fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import me.zeeroooo.materialfb.R;

public class NavigationMenuFragment extends PreferenceFragment {
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.navigation_menu_settings);

// id of items if you want
    findPreference("nav_groups").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("nav_search").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("nav_mainmenu").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });

    findPreference("nav_most_recent").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("nav_news").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("nav_fblogout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("nav_exitapp").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    findPreference("nav_events").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });

    findPreference("nav_photos").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        	
            return true;
        }
    });
    
    }

@Override
public void onStart() {
    super.onStart();
                   
 	}

@Override
public void onResume() {
    super.onResume();
                   
 	}

@Override
public void onPause() {
    super.onResume();
                  
 	}
 	}
