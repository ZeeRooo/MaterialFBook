/*
 * Created by ZeeRooo
 * https://github.com/ZeeRooo
 */
package me.zeeroooo.materialfb.Ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.R;

public final class Theme {

    // Thanks to Naman Dwivedi
    public static int getColor(Context context) {
        int Attr;
        Attr = R.attr.colorPrimary;
        final TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(Attr, outValue, true);
        return outValue.data;
    }

    public static void Temas(Context ctxt, SharedPreferences mPreferences) {
        switch (mPreferences.getString("app_theme", "MaterialFBook")) {
            case "MaterialFBook":
                MainActivity.css += ctxt.getString(R.string.Material);
                ctxt.setTheme(R.style.MFB);
                break;
            case "Amoled":
                MainActivity.css += ctxt.getString(R.string.MaterialAmoled);
                MainActivity.css += "::selection {background: #D3D3D3;}";
                ctxt.setTheme(R.style.Black);
                break;
            case "Black":
                MainActivity.css += ctxt.getString(R.string.MaterialBlack);
                ctxt.setTheme(R.style.Black);
                break;
            case "Pink":
                MainActivity.css += ctxt.getString(R.string.MaterialPink);
                ctxt.setTheme(R.style.Pink);
                break;
            case "Grey":
                MainActivity.css += ctxt.getString(R.string.MaterialGrey);
                ctxt.setTheme(R.style.Grey);
                break;
            case "Green":
                MainActivity.css += ctxt.getString(R.string.MaterialGreen);
                ctxt.setTheme(R.style.Green);
                break;
            case "Red":
                MainActivity.css += ctxt.getString(R.string.MaterialRed);
                ctxt.setTheme(R.style.Red);
                break;
            case "Lime":
                MainActivity.css += ctxt.getString(R.string.MaterialLime);
                ctxt.setTheme(R.style.Lime);
                break;
            case "Yellow":
                MainActivity.css += ctxt.getString(R.string.MaterialYellow);
                ctxt.setTheme(R.style.Yellow);
                break;
            case "Purple":
                MainActivity.css += ctxt.getString(R.string.MaterialPurple);
                ctxt.setTheme(R.style.Purple);
                break;
            case "LightBlue":
                MainActivity.css += ctxt.getString(R.string.MaterialLightBlue);
                ctxt.setTheme(R.style.LightBlue);
                break;
            case "Orange":
                MainActivity.css += ctxt.getString(R.string.MaterialOrange);
                ctxt.setTheme(R.style.Orange);
                break;
            case "GooglePlayGreen":
                MainActivity.css += ctxt.getString(R.string.MaterialGPG);
                ctxt.setTheme(R.style.GooglePlayGreen);
                break;
        }
    }
}
