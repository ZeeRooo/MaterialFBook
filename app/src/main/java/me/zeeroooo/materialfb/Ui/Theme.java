/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package me.zeeroooo.materialfb.Ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import me.zeeroooo.materialfb.R;

public final class Theme {
    private static final String APP_THEME = "app_theme";
    private static Theme AppTheme;
    private static SharedPreferences mPreferences;

    public Theme(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Theme getInstance(final Context context) {
        if (AppTheme == null) {
            AppTheme = new Theme(context.getApplicationContext());
        }
        return AppTheme;
    }

    private String setTheme() {
        return mPreferences.getString(APP_THEME, "MaterialFBook");
    }

    public static int getColor(Context context) {
        int Attr;
        Attr = R.attr.colorPrimary;
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(Attr, outValue, true);
        return outValue.data;
    }

    public static void getTheme(Context ctxt) {
        final boolean MFB = Theme.getInstance(ctxt).setTheme().equals("MFB");
        final boolean Pink = Theme.getInstance(ctxt).setTheme().equals("Pink");
        final boolean Grey = Theme.getInstance(ctxt).setTheme().equals("Grey");
        final boolean Green = Theme.getInstance(ctxt).setTheme().equals("Green");
        final boolean Red = Theme.getInstance(ctxt).setTheme().equals("Red");
        final boolean Lime = Theme.getInstance(ctxt).setTheme().equals("Lime");
        final boolean Yellow = Theme.getInstance(ctxt).setTheme().equals("Yellow");
        final boolean Purple = Theme.getInstance(ctxt).setTheme().equals("Purple");
        final boolean LightBlue = Theme.getInstance(ctxt).setTheme().equals("LightBlue");
        final boolean Black = Theme.getInstance(ctxt).setTheme().equals("Black");
        final boolean Orange = Theme.getInstance(ctxt).setTheme().equals("Orange");
        final boolean GooglePlayGreen = Theme.getInstance(ctxt).setTheme().equals("GooglePlayGreen");
        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (MFB)
                ctxt.setTheme(R.style.MFB);
        } else {
            if (Pink)
                ctxt.setTheme(R.style.Pink);
            if (Grey)
                ctxt.setTheme(R.style.Grey);
            if (Green)
                ctxt.setTheme(R.style.Green);
            if (Red)
                ctxt.setTheme(R.style.Red);
            if (Lime)
                ctxt.setTheme(R.style.Lime);
            if (Yellow)
                ctxt.setTheme(R.style.Yellow);
            if (Purple)
                ctxt.setTheme(R.style.Purple);
            if (LightBlue)
                ctxt.setTheme(R.style.LightBlue);
            if (Black)
                ctxt.setTheme(R.style.Black);
            if (Orange)
                ctxt.setTheme(R.style.Orange);
            if (GooglePlayGreen)
                ctxt.setTheme(R.style.GooglePlayGreen);
        }
    }
}
