/*
 * Code taken from FaceSlim by indywidualny. Thanks.
 */
package me.zeeroooo.materialfb.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.misc.DatabaseHelper;
import me.zeeroooo.materialfb.notifications.NotificationsService;
import me.zeeroooo.materialfb.ui.Theme;

public class NotificationsSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences preferences;
    private DatabaseHelper DBHelper;
    private ArrayList<String> blacklist = new ArrayList<>();
    private WorkManager workManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notifications_settings);
        workManager = WorkManager.getInstance();

        if (getActivity() != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            DBHelper = new DatabaseHelper(getActivity());

            findPreference("BlackList").setOnPreferenceClickListener(this);

            preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    switch (key) {
                        case "notif_interval":
                            workManager.cancelAllWork();

                            workManager.enqueue(new PeriodicWorkRequest.Builder(NotificationsService.class, Integer.valueOf(prefs.getString("notif_interval", "60000")), TimeUnit.MILLISECONDS)
                                    .setConstraints(new Constraints.Builder()
                                            .setRequiredNetworkType(NetworkType.CONNECTED)
                                            .build())
                                    .build());
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        DBHelper.close();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "BlackList":
                final AlertDialog blacklistDialog = new AlertDialog.Builder(getActivity()).create();
                blacklistDialog.setTitle(R.string.blacklist_title);

                final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.chip_input, null);

                final Cursor cursor = DBHelper.getReadableDatabase().rawQuery("SELECT BL FROM mfb_table", null);
                while (cursor.moveToNext()) {
                    if (cursor.getString(0) != null)
                        addRemovableChip(cursor.getString(0), rootView);
                }

                final AutoCompleteTextView autoCompleteTextView = rootView.findViewById(R.id.preloadedTags);

                autoCompleteTextView.setAdapter(new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_dropdown_item_1line));

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        addRemovableChip((String) parent.getItemAtPosition(position), rootView);
                        autoCompleteTextView.setText("");
                    }
                });

                autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                            addRemovableChip(v.getText().toString(), rootView);
                            autoCompleteTextView.setText("");
                            return true;
                        } else
                            return false;
                    }
                });

                blacklistDialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int position = 0; position < blacklist.size(); position++) {
                            DBHelper.addData(null, null, blacklist.get(position));
                        }

                        blacklist.clear();

                        if (!cursor.isClosed()) {
                            cursor.close();
                        }
                    }
                });

                blacklistDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        blacklistDialog.dismiss();
                        blacklist.clear();

                        if (!cursor.isClosed()) {
                            cursor.close();
                        }
                    }
                });

                blacklistDialog.setView(rootView);
                blacklistDialog.show();
                return true;
        }

        return false;
    }

    public void addRemovableChip(String text, final View rootView) {
        blacklist.add(text);

        Chip chip = new Chip(rootView.getContext());
        chip.setChipBackgroundColor(ColorStateList.valueOf(Theme.getColor(getActivity())));
        chip.setText(text);
        chip.setTag(text);
        chip.setCloseIconVisible(true);
        ((ChipGroup) rootView.findViewById(R.id.tagsChipGroup)).addView(chip);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChipGroup) rootView.findViewById(R.id.tagsChipGroup)).removeView(v);
                blacklist.remove(v.getTag());
                DBHelper.remove(null, null, v.getTag().toString());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // update notification ringtone preference summary
        String ringtoneString = preferences.getString("ringtone", "content://settings/system/notification_sound");
        Uri ringtoneUri = Uri.parse(ringtoneString);
        String name;

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            name = ringtone.getTitle(getActivity());
        } catch (Exception ex) {
            ex.printStackTrace();
            name = "Default";
        }

        if ("".equals(ringtoneString))
            name = getString(R.string.silent);

        RingtonePreference rpn = (RingtonePreference) findPreference("ringtone");
        rpn.setSummary(getString(R.string.notification_sound_description) + name);

        // update message ringtone preference summary
        ringtoneString = preferences.getString("ringtone_msg", "content://settings/system/notification_sound");
        ringtoneUri = Uri.parse(ringtoneString);

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            name = ringtone.getTitle(getActivity());
        } catch (Exception ex) {
            ex.printStackTrace();
            name = "Default";
        }

        if ("".equals(ringtoneString))
            name = getString(R.string.silent);

        RingtonePreference rpm = (RingtonePreference) findPreference("ringtone_msg");
        rpm.setSummary(getString(R.string.notification_sound_description) + name);
    }
}