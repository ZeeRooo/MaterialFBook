package me.zeeroooo.materialfb.WebView;

import android.net.UrlQuerySanitizer;
import android.webkit.WebView;

public class JavaScriptHelpers {
    private static final int BADGE_UPDATE_INTERVAL = 5000;

    static void updateCurrentTab(WebView view) {
        // Get the currently open tab and check on the navigation menu
        view.loadUrl("javascript:(function()%7Btry%7Bvar%20jewel%3Ddocument.querySelector(%22.popoverOpen%22).id%3B%22feed_jewel%22%3D%3Djewel%3Fdocument.querySelector('a%5Bhref*%3D%22%2Fhome.php%3Fsk%3Dh_nor%22%5D')%3Fandroid.getCurrent(%22most_recent%22)%3Aandroid.getCurrent(%22top_stories%22)%3Aandroid.getCurrent(jewel)%7Dcatch(_)%7Bandroid.getCurrent(%22null%22)%7D%7D)()");
    }

    public static void updateNums(WebView view) {
        view.loadUrl("javascript:(function()%7Bandroid.getNums(document.querySelector(%22%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23requests_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%7D)()");
    }

    static void updateNumsService(WebView view) {
        view.loadUrl("javascript:(function()%7Bfunction%20n_s()%7Bandroid.getNums(document.querySelector(%22%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23requests_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%2CsetTimeout(n_s%2C" + BADGE_UPDATE_INTERVAL + ")%7Dtry%7Bn_s()%7Dcatch(_)%7B%7D%7D)()");
    }

    static void paramLoader(WebView view, String url) {
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
        sanitizer.setAllowUnregisteredParamaters(true);
        sanitizer.parseUrl(url);
        String param = sanitizer.getValue("pageload");
        if (param != null) {
            switch (param) {
                case "composer":
                    view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7B%7D%7D)()");
                    break;
                case "composer_photo":
                    view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7B%7D%7D)()");
                    break;
                case "composer_checkin":
                    view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7B%7D%7D)()");
                    break;
                default:
                    break;
            }
        }

    }

    static void loadCSS(WebView view, String css) {
        // Inject CSS string to the HEAD of the webpage
        view.loadUrl("javascript:(function()%7Bvar%20styles%3Ddocument.createElement('style')%3Bstyles.innerHTML%3D'" + css + "'%2Cdocument.getElementsByTagName('head')%5B0%5D.appendChild(styles)%7D)()");
    }
}
