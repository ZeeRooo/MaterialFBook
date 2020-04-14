/**
 * Code taken from FaceSlim by indywidualny. Thanks.
 **/
package me.zeeroooo.materialfb.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.ColorUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.concurrent.TimeUnit;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.notifications.NotificationsService;
import me.zeeroooo.materialfb.ui.CookingAToast;
import me.zeeroooo.materialfb.ui.MFBDialog;

public class SettingsFragment extends MFBPreferenceFragment implements Preference.OnPreferenceClickListener {

    private SharedPreferences mPreferences;
    private WorkManager workManager;
    private int red = 1, green = 1, blue = 1, colorPrimary;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        workManager = WorkManager.getInstance();

        // set onPreferenceClickListener for a few preferences
        findPreference("notifications_settings").setOnPreferenceClickListener(this);
        findPreference("navigation_menu_settings").setOnPreferenceClickListener(this);
        findPreference("more_and_credits").setOnPreferenceClickListener(this);
        findPreference("location_enabled").setOnPreferenceClickListener(this);
        findPreference("save_data").setOnPreferenceClickListener(this);
        findPreference("notif").setOnPreferenceClickListener(this);
        findPreference("color_picker").setOnPreferenceClickListener(this);

        findPreference("localeSwitcher").setOnPreferenceChangeListener((preference, o) -> {
            mPreferences.edit().putString("defaultLocale", o.toString()).apply();
            getActivity().recreate();
            return true;
        });
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "notifications_settings":
                getFragmentManager().beginTransaction()
                        .addToBackStack(null).replace(R.id.content_frame,
                        new NotificationsSettingsFragment()).commit();
                return true;
            case "navigation_menu_settings":
                getFragmentManager().beginTransaction()
                        .addToBackStack(null).replace(R.id.content_frame,
                        new NavigationMenuFragment()).commit();
                return true;
            case "more_and_credits":
                getFragmentManager().beginTransaction()
                        .addToBackStack(null).replace(R.id.content_frame,
                        new MoreAndCreditsFragment()).commit();
                return true;
            case "location_enabled":
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return true;
            case "save_data":
            case "notif":
                setScheduler();
                return true;
            case "color_picker":
                final AlertDialog mfbColorPickerDialog = new MFBDialog(getActivity()).create();
                final TextView previewTextView = new TextView(getActivity());
                previewTextView.setTextAppearance(getActivity(), R.style.MaterialAlertDialog_MaterialComponents_Title_Text);
                previewTextView.setTextSize(22.0f);
                previewTextView.setText(R.string.color_picker_title);
                previewTextView.setPadding(24, 21, 24, 21);
                previewTextView.setBackgroundColor(Color.BLACK);
                mfbColorPickerDialog.setCustomTitle(previewTextView);

                final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_color_picker, null);

                final EditText hexColorInput = rootView.findViewById(R.id.color_picker_hex);

                final Slider sliderRed = rootView.findViewById(R.id.color_picker_red_slider), sliderGreen = rootView.findViewById(R.id.color_picker_green_slider), sliderBlue = rootView.findViewById(R.id.color_picker_blue_slider);
                sliderRed.addOnChangeListener((slider, value, fromUser) -> {
                    red = (int) value;
                    setColor(previewTextView, hexColorInput);
                });
                sliderGreen.addOnChangeListener((slider, value, fromUser) -> {
                    green = (int) value;
                    setColor(previewTextView, hexColorInput);
                });
                sliderBlue.addOnChangeListener((slider, value, fromUser) -> {
                    blue = (int) value;
                    setColor(previewTextView, hexColorInput);
                });

                hexColorInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                hexColorInput.setOnEditorActionListener((textView, i, keyEvent) -> {
                    colorPrimary = Color.parseColor("#" + textView.getText().toString().replace("#", ""));

                    previewTextView.setBackgroundColor(colorPrimary);

                    red = Color.red(colorPrimary);
                    green = Color.green(colorPrimary);
                    blue = Color.blue(colorPrimary);

                    sliderRed.setValue(red);
                    sliderGreen.setValue(green);
                    sliderBlue.setValue(blue);
                    return true;
                });

                final SwitchMaterial switchMaterial = rootView.findViewById(R.id.color_picker_dark_mode);
                switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
                    switchMaterial.getThumbDrawable().setColorFilter(b ? MFB.colorAccent : Color.parseColor("#ECECEC"), PorterDuff.Mode.SRC_ATOP);
                    switchMaterial.getTrackDrawable().setColorFilter(b ? MFB.colorPrimaryDark : Color.parseColor("#B9B9B9"), PorterDuff.Mode.SRC_ATOP);
                });

                mfbColorPickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), (dialogInterface, i) -> mfbColorPickerDialog.dismiss());
                mfbColorPickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), (dialogInterface, i) -> {
                    mfbColorPickerDialog.dismiss();

                    colorPrimary = Color.rgb(red, green, blue);
                    int colorAccent;

                    if (ColorUtils.calculateLuminance(colorPrimary) > 0.8 || (ColorUtils.calculateLuminance(MFB.colorPrimary) < 0.5 && switchMaterial.isChecked())) // it's too bright
                        colorAccent = Color.BLACK;
                    else if (ColorUtils.calculateLuminance(colorPrimary) < 0.01 && switchMaterial.isChecked()) // it's too dark
                        colorAccent = Color.WHITE;
                    else
                        colorAccent = colorLighter(colorPrimary);

                    mPreferences.edit().putInt("colorPrimary", colorPrimary).apply();
                    mPreferences.edit().putInt("colorPrimaryDark", colorDarker(colorPrimary)).apply();
                    mPreferences.edit().putInt("colorAccent", colorAccent).apply();
                    mPreferences.edit().putBoolean("darkMode", switchMaterial.isChecked()).apply();

                    getActivity().recreate();
                    //CookingAToast.cooking(getActivity(), getString(R.string.required_restart), Color.WHITE, colorPrimary, R.drawable.ic_error, true).show();
                });

                mfbColorPickerDialog.setView(rootView);
                mfbColorPickerDialog.show();
                return true;
            default:
                return false;
        }
    }

    private void setColor(TextView textView, EditText hexColorInput) {
        colorPrimary = Color.rgb(red, green, blue);

        textView.setBackgroundColor(colorPrimary);
        hexColorInput.setText(Integer.toHexString(colorPrimary).substring(2));
    }

    private float[] hsv = new float[3];

    private int colorDarker(int color) {
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // smaller = darker
        return Color.HSVToColor(hsv);
    }

    private int colorLighter(int color) {
        Color.colorToHSV(color, hsv);
        hsv[2] /= 0.8f;
        return Color.HSVToColor(hsv);
    }

    private void setScheduler() {
        if (mPreferences.getBoolean("notif", false) && !mPreferences.getBoolean("save_data", false))
            workManager.enqueue(new PeriodicWorkRequest.Builder(NotificationsService.class, Integer.valueOf(mPreferences.getString("notif_interval", "60000")), TimeUnit.MILLISECONDS)
                    .setConstraints(new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build())
                    .build());
        else
            workManager.cancelAllWork();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            CookingAToast.cooking(getActivity(), getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
    }
}