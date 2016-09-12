package me.zeeroooo.materialfb;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;

import com.facebook.login.LoginManager;

import java.util.Arrays;
import java.util.List;

class Helpers {
    public static final String LogTag = "FBWrapper";
    static final List<String> FB_PERMISSIONS = Arrays.asList("public_profile", "user_friends");

    // Method to retrieve a single cookie
    public static String getCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(MainActivity.FACEBOOK_URL_BASE);
        if (cookies != null) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains("c_user")) {
                    String[] temp1 = ar1.split("=");
                    return temp1[1];
                }
            }
        }
        // Return null as we found no cookie
        return null;
    }

    // Prompt a login
    public static Snackbar loginPrompt(final View view) {
        final Snackbar snackBar = Snackbar.make(view, R.string.not_logged_in, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.login_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions((Activity) view.getContext(), FB_PERMISSIONS);
            }
        });
        snackBar.show();
        return snackBar;
    }

    // Uncheck all items menu
    public static void uncheckRadioMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).isChecked()) {
                menu.getItem(i).setChecked(false);
                return;
            }
        }
    }

    public static boolean isInteger(String str) {
        return (str.matches("^-?\\d+$"));
    }
}
