/*
 * Code taken from FaceSlim by indywidualny. Thanks.
 */
package me.zeeroooo.materialfb.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.misc.DatabaseHelper;
import me.zeeroooo.materialfb.notifications.NotificationsService;
import me.zeeroooo.materialfb.ui.MFBDialog;
import me.zeeroooo.materialfb.ui.MFBRingtoneDialog;

public class NotificationsSettingsFragment extends MFBPreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences sharedPreferences;
    private DatabaseHelper DBHelper;
    private ArrayList<String> blacklist = new ArrayList<>();
    private WorkManager workManager;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.notifications_settings);
        workManager = WorkManager.getInstance();

        if (getActivity() != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            DBHelper = new DatabaseHelper(getActivity());

            findPreference("BlackList").setOnPreferenceClickListener(this);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                findPreference("ringtone").setOnPreferenceClickListener(this);
                findPreference("ringtone_msg").setOnPreferenceClickListener(this);
            } else
                findPreference("notification_channel_shortcut").setOnPreferenceClickListener(this);

            sharedPreferences.registerOnSharedPreferenceChangeListener((prefs, key) -> {
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
            case "notification_channel_shortcut":
                startActivity(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName()));
                break;
            case "ringtone":
            case "ringtone_msg":
                new MFBRingtoneDialog(getActivity(), sharedPreferences, preference.getKey()).show();
                break;
            case "BlackList":
                AlertDialog blacklistDialog = new MFBDialog(getActivity()).create();
                blacklistDialog.setTitle(R.string.blacklist_title);

                final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.chip_input, null);

                final Cursor cursor = DBHelper.getReadableDatabase().rawQuery("SELECT BL FROM mfb_table", null);
                while (cursor.moveToNext()) {
                    if (cursor.getString(0) != null)
                        addRemovableChip(cursor.getString(0), rootView);
                }

                final AutoCompleteTextView autoCompleteTextView = rootView.findViewById(R.id.preloadedTags);

                autoCompleteTextView.setAdapter(new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_dropdown_item_1line));

                autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                    addRemovableChip((String) parent.getItemAtPosition(position), rootView);
                    autoCompleteTextView.setText("");
                });

                autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
                    if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                        addRemovableChip(v.getText().toString(), rootView);
                        autoCompleteTextView.setText("");
                        return true;
                    } else
                        return false;
                });

                blacklistDialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(android.R.string.ok), (dialog, which) -> {
                    for (int position = 0; position < blacklist.size(); position++) {
                        DBHelper.addData(null, null, blacklist.get(position));
                    }

                    blacklist.clear();

                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                });

                blacklistDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(android.R.string.cancel), (dialog, which) -> {
                    blacklistDialog.dismiss();
                    blacklist.clear();

                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                });

                blacklistDialog.setView(rootView);
                blacklistDialog.show();

                return true;
        }

        return false;
    }

    private void addRemovableChip(String text, final View rootView) {
        blacklist.add(text);

        Chip chip = new Chip(rootView.getContext());
        chip.setChipBackgroundColor(ColorStateList.valueOf(MFB.colorAccent));
        // chip.setChipBackgroundColor(ColorStateList.valueOf(Theme.getColor(getActivity())));
        chip.setTextColor(MFB.textColor);
        chip.setText(text);
        chip.setTag(text);
        chip.setCloseIconVisible(true);
        ((ChipGroup) rootView.findViewById(R.id.tagsChipGroup)).addView(chip);
        chip.setOnCloseIconClickListener(v -> {
            ((ChipGroup) rootView.findViewById(R.id.tagsChipGroup)).removeView(v);
            blacklist.remove(v.getTag());
            DBHelper.remove(null, null, v.getTag().toString());
        });
    }
}