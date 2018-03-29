/*
 * Created by ZeeRooo
 * https://github.com/ZeeRooo
 */
package me.zeeroooo.materialfb.Ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;

import me.zeeroooo.materialfb.R;

public final class Theme {

    // Thanks to Naman Dwivedi
    public static int getColor(final Context context) {
        int Attr;
        Attr = R.attr.colorPrimary;
        final TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(Attr, outValue, true);
        return outValue.data;
    }

    public static void Temas(Context ctxt, SharedPreferences mPreferences) {
        switch (mPreferences.getString("app_theme", "MaterialFBook")) {
            case "MaterialFBook":
                ctxt.setTheme(R.style.MFB);
                break;
            case "Amoled":
                ctxt.setTheme(R.style.Black);
                break;
            case "Black":
                ctxt.setTheme(R.style.Black);
                break;
            case "Pink":
                ctxt.setTheme(R.style.Pink);
                break;
            case "Grey":
                ctxt.setTheme(R.style.Grey);
                break;
            case "Green":
                ctxt.setTheme(R.style.Green);
                break;
            case "Red":
                ctxt.setTheme(R.style.Red);
                break;
            case "Lime":
                ctxt.setTheme(R.style.Lime);
                break;
            case "Yellow":
                ctxt.setTheme(R.style.Yellow);
                break;
            case "Purple":
                ctxt.setTheme(R.style.Purple);
                break;
            case "LightBlue":
                ctxt.setTheme(R.style.LightBlue);
                break;
            case "Orange":
                ctxt.setTheme(R.style.Orange);
                break;
            case "GooglePlayGreen":
                ctxt.setTheme(R.style.GooglePlayGreen);
                break;
        }
    }
}
