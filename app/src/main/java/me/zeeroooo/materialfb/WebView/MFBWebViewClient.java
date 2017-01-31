package me.zeeroooo.materialfb.WebView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MFBWebViewClient extends WebViewClient {

    @SuppressLint("NewApi")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // clean an url from facebook redirection before processing (no more blank pages on back)
        if (url != null)
            url = cleanAndDecodeUrl(url);
            view.getSettings().setUseWideViewPort(true);

            if (Uri.parse(url).getHost().endsWith("facebook.com")
                    || Uri.parse(url).getHost().endsWith("*.facebook.com")
                    || Uri.parse(url).getHost().endsWith("fbcdn.net")
                    || Uri.parse(url).getHost().endsWith("akamaihd.net")
                    || Uri.parse(url).getHost().endsWith("ad.doubleclick.net")
                    || Uri.parse(url).getHost().endsWith("sync.liverail.com")
                    || Uri.parse(url).getHost().endsWith("fb.me")) {
                return false;
            }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
            return true;
        }

    // "clean" and decode an url, all in one
    private static String cleanAndDecodeUrl(String url) {
        return decodeUrl(cleanUrl(url));
    }

    // "clean" an url and remove Facebook tracking redirection
    private static String cleanUrl(String url) {
        return url.replace("http://lm.facebook.com/l.php?u=", "")
                .replace("https://m.facebook.com/l.php?u=", "")
                .replace("http://0.facebook.com/l.php?u=", "")
                .replace("https://lm.facebook.com/l.php?u=", "")
                .replaceAll("&h=.*", "").replaceAll("\\?acontext=.*", "");
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
}