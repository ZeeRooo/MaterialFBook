package me.zeeroooo.materialfb.webview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;

import java.util.Locale;

public class Helpers {

    // Method to retrieve a single cookie
    public static String getCookie() {
        final CookieManager cookieManager = CookieManager.getInstance();
        final String cookies = cookieManager.getCookie("https://m.facebook.com/");
        if (cookies != null) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains("c_user")) {
                    final String[] temp1 = ar1.split("=");
                    return temp1[1];
                }
            }
        }
        // Return null as we found no cookie
        return null;
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

    static boolean isInteger(String str) {
        return (str.matches("^-?\\d+$"));
    }

    // "clean" and decode an url, all in one
    public static String cleanAndDecodeUrl(String url) {
        return decodeUrl(cleanUrl(url));
    }

    // "clean" an url and remove Facebook tracking redirection
    public static String cleanUrl(String url) {
        return url.replace("http://lm.facebook.com/l.php?u=", "")
                .replace("https://m.facebook.com/l.php?u=", "")
                .replace("http://0.facebook.com/l.php?u=", "")
                .replace("https://lm.facebook.com/l.php?u=", "")
                .replaceAll("&h=.*", "")
                .replaceAll("\\?acontext=.*", "")
                .replace("&SharedWith=", "")
                .replace("www.facebook.com", "m.facebook.com")
                .replace("web.facebook.com", "m.facebook.com");
    }

    // url decoder, recreate all the special characters
    private static String decodeUrl(String url) {
        return url.replace("%3C", "<").replace("%3E", ">").replace("%23", "#").replace("%25", "%")
                .replace("%7B", "{").replace("%7D", "}").replace("%7C", "|").replace("%5C", "\\")
                .replace("%5E", "^").replace("%7E", "~").replace("%5B", "[").replace("%5D", "]")
                .replace("%60", "`").replace("%3B", ";").replace("%2F", "/").replace("%3F", "?")
                .replace("%3A", ":").replace("%40", "@").replace("%3D", "=").replace("%26", "&")
                .replace("%24", "$").replace("%2B", "+").replace("%22", "\"").replace("%2C", ",")
                .replace("%20", " ");
    }

    public static String decodeImg(String img_url) {
        return img_url.replace("\\3a ", ":").replace("efg\\3d ", "oh=").replace("\\3d ", "=").replace("\\26 ", "&").replace("\\", "").replace("&amp;", "&");
    }

    public static void setLocale(Activity activity, int contentViewId) {
        Locale locale = new Locale(PreferenceManager.getDefaultSharedPreferences(activity).getString("defaultLocale", Locale.getDefault().getLanguage()));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        config.setLocale(locale);
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
        activity.setContentView(contentViewId);
    }
}
