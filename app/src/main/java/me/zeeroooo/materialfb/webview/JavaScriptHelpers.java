package me.zeeroooo.materialfb.webview;

import android.util.Base64;
import android.webkit.WebView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JavaScriptHelpers {

    public static void updateNumsService(WebView view) {
        view.loadUrl("javascript:(function(){function n_s(){android.getNums(document.querySelector(\"#notifications_jewel > a > div > div > span\").innerHTML,document.querySelector(\"#messages_jewel > a > div > div > span\").innerHTML,document.querySelector(\"#requests_jewel > a > div > div > span\").innerHTML,document.querySelector(\"#feed_jewel > a > div > div > span\").innerHTML),setTimeout(n_s,5000)}try{n_s()}catch(_){}})()");
    }

    // Thanks to Simple for Facebook. - https://github.com/creativetrendsapps/SimpleForFacebook/blob/master/app/src/main/java/com/creativetrends/simple/app/helpers/BadgeHelper.java#L36
    public static void videoView(WebView view) {
        view.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'Vid.LoadVideo(\"'+jsonData['src']+'\");');}}})()");
        view.loadUrl("javascript:( window.onload=prepareVideo;)()");
    }

    public static void loadCSS(WebView view, String css) {
        try {
            final InputStream inputStream = new ByteArrayInputStream(css.getBytes(StandardCharsets.UTF_8));
            final byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            view.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + Base64.encodeToString(buffer, Base64.NO_WRAP) + "');parent.appendChild(style)})()");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
