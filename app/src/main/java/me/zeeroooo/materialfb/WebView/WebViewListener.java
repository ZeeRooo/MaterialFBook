package me.zeeroooo.materialfb.WebView;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.webkit.WebView;
import com.github.clans.fab.FloatingActionMenu;
import im.delight.android.webview.AdvancedWebView;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Activities.SettingsActivity;

public class WebViewListener implements AdvancedWebView.Listener {
    private static final int ID_SHARE_LINK = 3;
    private static final int ID_COPY_LINK = 4;

    // *{-webkit-tap-highlight-color: rgba(0,0,0, 0.0);outline: none;}
    private static final String HIDE_ORANGE_FOCUS = "*%7B-webkit-tap-highlight-color%3Atransparent%3Boutline%3A0%7D";
    // #page{top:-45px;}
    private static final String HIDE_MENU_BAR_CSS = "%23page%7Btop%3A-45px%7D";
    // #mbasic_inline_feed_composer{display:none}
    private static final String HIDE_COMPOSER_CSS = "%23mbasic_inline_feed_composer%7Bdisplay%3Anone%7D";
    // article[data-ft*=ei]{display:none;}
    private static final String HIDE_SPONSORED = "article%5Bdata-ft*%3Dei%5D%7Bdisplay%3Anone%7D";
    // article#u_1j_4{display:none;}
    private static final String HIDE_BIRTHDAYS = "article%23u_1j_4%7Bdisplay%3Anone%3B%7D" + "article._55wm._5e4e._5fjt%7Bdisplay:none%3B%7D";
    private static final String messages_only = "div%23m_newsfeed_stream.storyStream%7Bdisplay%3Anone%7D";

    private final MainActivity mActivity;
    private final SharedPreferences mPreferences;
    private final AdvancedWebView mWebView;
    private final FloatingActionMenu mMenuFAB;
    public static DownloadManager mDownloadManager;
    private final int mScrollThreshold;

    public WebViewListener(MainActivity activity, WebView view) {
        mActivity = activity;
        mWebView = (AdvancedWebView) view;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mScrollThreshold = activity.getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);
        mMenuFAB = (FloatingActionMenu) activity.findViewById(R.id.menuFAB);
        mDownloadManager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        // Show the spinner and hide the WebView
        mActivity.setLoading(true);
    }

    @Override
    public void onPageFinished(String url) {
        // Only do things if logged in
        if (mActivity.checkLoggedInState()) {
            // Load a certain page if there is a parameter
            JavaScriptHelpers.paramLoader(mWebView, url);

            // Hide Orange highlight on focus
            String css = HIDE_ORANGE_FOCUS;

            // Hide the menu bar (but not on the composer or if disabled)
            if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_HIDE_MENU_BAR, true) && !url.contains("/composer/") && !url.contains("/friends/") && !url.contains("sharer") && !url.contains("events")) {
                mActivity.swipeView.setEnabled(true);
                css += HIDE_MENU_BAR_CSS;
            } else {
                mActivity.swipeView.setEnabled(false);
            }

            if (url.contains("https://mbasic.facebook.com/composer/?text=")) {
                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
                sanitizer.setAllowUnregisteredParamaters(true);
                sanitizer.parseUrl(url);
                String param = sanitizer.getValue("text");
                mWebView.loadUrl("javascript:(function()%7Bdocument.querySelector('%23composerInput').innerHTML%3D'" + param + "'%7D)()");
            }

            // Enable or disable FAB
            if (url.contains("messages/") || !mPreferences.getBoolean("fab_enable", false)) {
                mMenuFAB.hideMenu(true);
            } else {
                mMenuFAB.showMenu(true);
            }

            // Hide the status editor on the News Feed if setting is enabled
            if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_HIDE_EDITOR, true)) {
                css += HIDE_COMPOSER_CSS;
            }

            // Hide 'Sponsored' content (ads)
            if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_HIDE_SPONSORED, true)) {
                css += HIDE_SPONSORED;
            }

            // Hide birthday content from News Feed
            if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_HIDE_BIRTHDAYS, true)) {
                css += HIDE_BIRTHDAYS;
            }

            // Messages only
            if (mPreferences.getBoolean("messages_only", true)) {
                css += messages_only;
            }

			// Web themes
		    switch (mPreferences.getString("web_themes", "default")) {
			    case "DarkTheme": {
                   css += mActivity.getString(R.string.DarkTheme);
            }
				break;
			    case "MaterialDarkTheme": {
                   css += mActivity.getString(R.string.MaterialDarkTheme);
            }
				break;
			    case "MaterialTheme": {
                   css += mActivity.getString(R.string.MaterialTheme);
            }
		}

            // Inject the css
            JavaScriptHelpers.loadCSS(mWebView, css);

            // Get the currently open tab and check on the navigation menu
            JavaScriptHelpers.updateCurrentTab(mWebView);

            // Get the notification number
            JavaScriptHelpers.updateNumsService(mWebView);

            // Stop loading
            mActivity.setLoading(false);

        }
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        mActivity.setLoading(false);
    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) { }

    @Override
    public void onExternalPageRequest(String url) { }

    @Override
    public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        // Make sure the hiding is enabled and the scroll was significant
        if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_FAB_SCROLL, false) && Math.abs(oldScrollY - scrollY) > mScrollThreshold) {
            if (scrollY > oldScrollY) {
                // User scrolled down, hide the button
                mMenuFAB.hideMenuButton(true);
            } else if (scrollY < oldScrollY) {
                // User scrolled up, show the button
                mMenuFAB.showMenuButton(true);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu) {
        final WebView.HitTestResult result = mWebView.getHitTestResult();

        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == ID_COPY_LINK) {
                    ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newUri(mActivity.getContentResolver(), "URI", Uri.parse(result.getExtra()));
                    clipboard.setPrimaryClip(clip);
                    Snackbar.make(MainActivity.mCoordinatorLayoutView, R.string.content_copy_link_done, Snackbar.LENGTH_LONG).show();
                    return true;
                } else if (i == ID_SHARE_LINK) {
                    // Share the link
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
                    mActivity.startActivity(Intent.createChooser(shareIntent, mActivity.getString(R.string.context_share_link)));
                    return true;
                } else {
                    return false;
                }
            }
        };

        // Long pressed link
        if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            contextMenu.add(0, ID_SHARE_LINK, 0, R.string.context_share_link).setOnMenuItemClickListener(handler);
            contextMenu.add(0, ID_COPY_LINK, 0, R.string.context_copy_link).setOnMenuItemClickListener(handler);
        }
    }
}
