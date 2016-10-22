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
    public String setTheme(){ return mPreferences.getString(APP_THEME, "MaterialFBook"); }
}
