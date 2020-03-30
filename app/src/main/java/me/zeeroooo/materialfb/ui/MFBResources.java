package me.zeeroooo.materialfb.ui;

import android.content.SharedPreferences;
import android.content.res.Resources;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;

public class MFBResources extends Resources {

    public MFBResources(Resources r) {
        super(r.getAssets(), r.getDisplayMetrics(), r.getConfiguration());
    }

    @Override
    public int getColor(int id, Theme theme) throws NotFoundException {
        switch (getResourceEntryName(id)) {
            case "colorPrimary":
                return MFB.colorPrimary;
            case "colorPrimaryDark":
                return MFB.colorPrimaryDark;
            case "colorAccent":
                return MFB.colorAccent;
            default:
                return super.getColor(id, theme);
        }
    }

    public void setColors(SharedPreferences sharedPreferences) {
        MFB.colorPrimary = sharedPreferences.getInt("colorPrimary", R.color.colorPrimary);
        MFB.colorPrimaryDark = sharedPreferences.getInt("colorPrimaryDark", R.color.colorPrimaryDark);
        MFB.colorAccent = sharedPreferences.getInt("colorAccent", R.color.colorAccent);
    }
}
