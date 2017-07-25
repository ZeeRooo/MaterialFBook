package me.zeeroooo.materialfb.WebView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.Menu;
import android.webkit.CookieManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers {
    public String url, title;

    public Helpers(String title, String url) {
        this.url = url;
        this.title = title;
    }

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

    public static Bitmap Circle(Bitmap circle) {
        final Bitmap bitmap = Bitmap.createBitmap(circle.getWidth(), circle.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, circle.getWidth(), circle.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(circle.getWidth() / 2, circle.getHeight() / 2, circle.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(circle, rect, rect, paint);
        circle.recycle();
        return bitmap;
    }

    /**
     * Download an image as Bitmap object (run always outside the Main Thread)
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            final URL url = new URL(src);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            final InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // "clean" and decode an url, all in one
    public static String cleanAndDecodeUrl(String url) {
        return decodeUrl(cleanUrl(url));
    }

    // "clean" an url and remove Facebook tracking redirection
    private static String cleanUrl(String url) {
        return url.replace("http://lm.facebook.com/l.php?u=", "")
                .replace("https://m.facebook.com/l.php?u=", "")
                .replace("http://0.facebook.com/l.php?u=", "")
                .replace("https://lm.facebook.com/l.php?u=", "")
                .replaceAll("&h=.*", "").replaceAll("\\?acontext=.*", "").replaceAll("&SharedWith=", "");
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

    public static String decodeImg (String img_url) {
        return img_url.replace("\\3a ", ":").replace("efg\\3d ", "oh=").replace("\\3d ", "=").replace("\\26 ", "&").replace("\\", "");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
