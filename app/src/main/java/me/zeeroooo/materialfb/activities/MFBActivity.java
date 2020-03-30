package me.zeeroooo.materialfb.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import java.util.Locale;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.ui.MFBResources;

@SuppressLint("Registered")
public class MFBActivity extends AppCompatActivity {
    protected SharedPreferences sharedPreferences;
    private boolean darkTheme;
    protected byte themeMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        create(savedInstanceState);
    }

    @Override
    public Resources getResources() {
        return new MFBResources(super.getResources());
    }

    protected void create(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        darkTheme = sharedPreferences.getBoolean("darkMode", false);

        if (darkTheme)
            setTheme(R.style.Black);

        super.onCreate(savedInstanceState);
    }

    private byte getThemeMode() {
        if (ColorUtils.calculateLuminance(MFB.colorPrimary) < 0.01 && darkTheme) // dark theme with darker color scheme
            return 1;
        else if (darkTheme) // dark theme with light color scheme
            return 2;
        else if (ColorUtils.calculateLuminance(MFB.colorPrimary) < 0.5) // light theme with dark color scheme
            return 0;
        else if (ColorUtils.calculateLuminance(MFB.colorPrimary) > 0.8) // light theme with bright color scheme
            return 3;
        else
            return 9; // light theme without shinny colors
    }

    @Override
    public void setContentView(int layoutResID) {

        final Locale locale = new Locale(PreferenceManager.getDefaultSharedPreferences(this).getString("defaultLocale", Locale.getDefault().getLanguage()));
        Locale.setDefault(locale);

        final Configuration config = new Configuration();
        config.locale = locale;
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        themeMode = getThemeMode();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && themeMode == 3)
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        super.setContentView(layoutResID);
    }
}
