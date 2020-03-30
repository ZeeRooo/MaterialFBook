package me.zeeroooo.materialfb.ui;

import android.content.Context;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.adapters.AdapterListPreference;

public class MFBDialog extends MaterialAlertDialogBuilder {

    private Button button = null;

    public MFBDialog(Context context, int overrideThemeResId) {
        super(context, overrideThemeResId);
    }

    public MFBDialog(Context context) {
        super(context, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
    }

    public MFBDialog(Context context, Preference preference) {
        super(context, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);

        final ListPreference listPreference = (ListPreference) preference;

        final CharSequence[] valuesArray = listPreference.getEntryValues();

        setSingleChoiceItems(listPreference.getEntries(), listPreference.findIndexOfValue(listPreference.getValue()), null);

        setAdapter(new AdapterListPreference(getContext(), listPreference), (dialogInterface, i) -> {
            String value = valuesArray[i].toString();

            if (listPreference.callChangeListener(value))
                listPreference.setValue(value);

            dialogInterface.dismiss();
        });

        setPositiveButton(null, null);
    }

    @Override
    public AlertDialog show() {
        final AlertDialog alertDialog = super.show();

        for (byte a = -3; a < 0; a++) {
            button = alertDialog.getButton(a);
            if (button != null)
                button.setTextColor(MFB.colorPrimary);
        }

        return alertDialog;
    }
}
