package me.zeeroooo.materialfb.WebView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.github.clans.fab.FloatingActionMenu;
import com.greysonparrelli.permiso.Permiso;

import java.io.File;

import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.Activities.Photo;
import me.zeeroooo.materialfb.R;

import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;

public class MFBWebViewClient extends WebViewClient {

    private final MainActivity mActivity;
    private final SharedPreferences mPreferences;
    private final MFBWebView mWebView;
    private final FloatingActionMenu mMenuFAB;
    private DownloadManager mDownloadManager;
    private View mCoordinatorLayoutView;

    public MFBWebViewClient(MainActivity activity, WebView view) {
        mActivity = activity;
        mWebView = (MFBWebView) view;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mMenuFAB = (FloatingActionMenu) activity.findViewById(R.id.menuFAB);
        mDownloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        mCoordinatorLayoutView = activity.findViewById(R.id.coordinatorLayout);
    }

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
            url = Helpers.cleanAndDecodeUrl(url);

        if (url.contains("fbcdn.net")) {
            Intent Photo = new Intent(mActivity, me.zeeroooo.materialfb.Activities.Photo.class);
            Photo.putExtra("url", url);
            Photo.putExtra("title", view.getTitle());
            mActivity.startActivity(Photo);
            return true;
        } else if ((Uri.parse(url).getHost().endsWith("facebook.com")
                || Uri.parse(url).getHost().endsWith("*.facebook.com")
                || Uri.parse(url).getHost().endsWith("fbcdn.net")
                || Uri.parse(url).getHost().endsWith("akamaihd.net")
                || Uri.parse(url).getHost().endsWith("ad.doubleclick.net")
                || Uri.parse(url).getHost().endsWith("sync.liverail.com")
                || Uri.parse(url).getHost().endsWith("lookaside.fbsbx.com")
                || Uri.parse(url).getHost().endsWith("fb.me"))) {
            return false;
        }

        if (url.contains("giphy") || url.contains("gifspace") || url.contains("tumblr") || url.contains("gph") || url.contains("gif")) {
            if (url.contains("giphy") || url.contains("gph")) {
                if (!url.endsWith(".gif")) {
                    if (url.contains("giphy.com") || url.contains("html5")) {
                        url = String.format("http://media.giphy.com/media/%s/giphy.gif", new Object[]{url.replace("http://giphy.com/gifs/", "")});
                    } else if (url.contains("gph.is") && !url.contains("html5")) {
                        mWebView.loadUrl(url);
                    }

                    if (url.contains("media.giphy.com/media/") && !url.contains("html5")) {
                        String[] giphy = url.split("-");
                        String giphy_id = giphy[giphy.length - 1];
                        url = String.format("http://media.giphy.com/media/" + giphy_id);
                    }
                    if (url.contains("media.giphy.com/media/http://media")) {
                        String[] gph = url.split("/");
                        String gph_id = gph[gph.length - 2];
                        url = String.format("http://media.giphy.com/media/" + gph_id + "/giphy.gif");
                    }
                    if (url.contains("html5/giphy.gif")) {
                        String[] giphy_html5 = url.split("/");
                        String giphy_html5_id = giphy_html5[giphy_html5.length - 3];
                        url = String.format("http://media.giphy.com/media/" + giphy_html5_id + "/giphy.gif");
                        System.out.println(giphy_html5_id);
                    }
                }
                //    System.out.println(url);
            }

            if (url.contains("gifspace")) {
                if (!url.endsWith(".gif")) {
                    url = String.format("http://gifspace.net/image/%s.gif", new Object[]{url.replace("http://gifspace.net/image/", "")});
                }
            }

            Intent Photo = new Intent(mActivity, Photo.class);
            Photo.putExtra("url", url);
            Photo.putExtra("title", view.getTitle());
            mActivity.startActivity(Photo);
            return true;
        }

        // Open external links in browser
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mActivity.startActivity(browser);

        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // Show the spinner and hide the WebView
        mActivity.setLoading(true);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mActivity.checkLoggedInState()) {
            // Load a certain page if there is a parameter
            JavaScriptHelpers.paramLoader(view, url);

            // Hide Orange highlight on focus
            String css = "*%7B-webkit-tap-highlight-color%3Atransparent%3Boutline%3A0%7D";

            // Hide the menu bar (but not on the composer or if disabled)
            if (mPreferences.getBoolean("hide_menu_bar", true) && !url.contains("/composer/") && !url.contains("/friends/") && !url.contains("sharer") && !url.contains("events")) {
                css += "%23page%7Btop%3A-45px%7D";
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
            if (mPreferences.getBoolean("hide_editor_newsfeed", true)) {
                css += "%23mbasic_inline_feed_composer%7Bdisplay%3Anone%7D";
            }

            // Hide 'Sponsored' content (ads)
            if (mPreferences.getBoolean("hide_sponsored", true)) {
                css += "article%5Bdata-ft*%3Dei%5D%7Bdisplay%3Anone%7D";
            }

            // Hide birthday content from News Feed
            if (mPreferences.getBoolean("hide_birthdays", true)) {
                css += "article%23u_1j_4%7Bdisplay%3Anone%3B%7D" + "article._55wm._5e4e._5fjt%7Bdisplay:none%3B%7D";
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
            JavaScriptHelpers.loadCSS(view, css);

            // Get the currently open tab and check on the navigation menu
            JavaScriptHelpers.updateCurrentTab(view);

            // Get the notification number
            JavaScriptHelpers.updateNumsService(view);

            // Stop loading
            mActivity.setLoading(false);

        }
    }

    @Override
    public void onLoadResource(WebView view, final String url) {
        if (url.contains("lookaside")) {
            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                @Override
                public void onPermissionResult(Permiso.ResultSet resultSet) {
                    if (resultSet.areAllPermissionsGranted()) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        File downloads_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        if (!downloads_dir.exists()) {
                            if (!downloads_dir.mkdirs()) {
                                return;
                            }
                        }
                        File destinationFile = new File(downloads_dir, Uri.parse(url).getLastPathSegment());
                        request.setDestinationUri(Uri.fromFile(destinationFile));
                        request.setVisibleInDownloadsUi(true);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        mDownloadManager.enqueue(request);
                    } else {
                        Snackbar.make(mCoordinatorLayoutView, R.string.permission_denied, Snackbar.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                    // TODO Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                    callback.onRationaleProvided();
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}