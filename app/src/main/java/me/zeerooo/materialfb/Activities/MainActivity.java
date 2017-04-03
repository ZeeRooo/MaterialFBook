/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks.
 * - Folio for Facebook by creativetrendsapps. Thanks.
 * - Toffed by JakeLane. Thanks.
 */
package me.zeerooo.materialfb.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.clans.fab.FloatingActionMenu;
import org.json.JSONObject;
import android.support.v4.view.GravityCompat;
import android.webkit.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import me.zeerooo.materialfb.Bookmarks.DatabaseHelper;
import me.zeerooo.materialfb.Bookmarks.ListAdapter;
import me.zeerooo.materialfb.Notifications.NotificationsService;
import me.zeerooo.materialfb.Ui.CookingAToast;
import me.zeerooo.materialfb.Ui.Theme;
import me.zeerooo.materialfb.WebView.Helpers;
import me.zeerooo.materialfb.WebView.JavaScriptHelpers;
import me.zeerooo.materialfb.WebView.JavaScriptInterfaces;
import me.zeerooo.materialfb.WebView.MFBWebView;
import me.zeerooo.materialfb.R;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final BadgeStyle BADGE_SIDE_FULL = new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_badge_full, Color.RED, Color.RED, Color.WHITE);
    private ValueCallback<Uri[]> mFilePathCallback;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri> mUploadMessage;
    private static final int FILECHOOSER_RESULTCODE = 2888, INPUT_FILE_REQUEST_CODE = 1;
    private String baseURL, URLBase, mCameraPhotoPath, Url, mUserLink = null;
    private SwipeRefreshLayout swipeView;
    private NavigationView mNavigationView, BookmarksView;
    private FloatingActionMenu mMenuFAB;
    public MFBWebView mWebView;
    private final View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textFAB:
                    if (!mPreferences.getBoolean("save_data", false)) {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + "https%3A%2F%2Fm.facebook.com%2F" + "%3Fpageload%3Dcomposer%22%7D%7D)()");
                    } else {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + "https%3A%2F%2Fmbasic.facebook.com%2F" + "%3Fpageload%3Dcomposer%22%7D%7D)()");
                    }
                    break;
                case R.id.photoFAB:
                    if (!mPreferences.getBoolean("save_data", false)) {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + "https%3A%2F%2Fm.facebook.com%2F" + "%3Fpageload%3Dcomposer_photo%22%7D%7D)()");
                    } else {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + "https%3A%2F%2Fmbasic.facebook.com%2F" + "%3Fpageload%3Dcomposer_photo%22%7D%7D)()");
                    }
                    break;
                case R.id.checkinFAB:
                    if (!mPreferences.getBoolean("save_data", false)) {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + "https%3A%2F%2Fm.facebook.com%2F" + "%3Fpageload%3Dcomposer_checkin%22%7D%7D)()");
                    } else {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + "https%3A%2F%2Fmbasic.facebook.com%2F" + "%3Fpageload%3Dcomposer_checkin%22%7D%7D)()");
                    }
                    break;
                case R.id.topFAB:
                    mWebView.scrollTo(0, 0);
                    break;
                case R.id.shareFAB:
                    String url_to_be_shared = mWebView.getUrl();
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, url_to_be_shared);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.context_share_link)));
                    break;
                default:
                    break;
            }
            mMenuFAB.close(true);
        }
    };

    private MenuItem mNotificationButton, mMessagesButton;
    private CallbackManager callbackManager;
    @SuppressWarnings("FieldCanBeLocal") // Will be garbage collected as a local variable
    public static SharedPreferences mPreferences;
    private DownloadManager mDownloadManager;
    private DatabaseHelper DBHelper;
    private ListAdapter BLAdapter;
    private ListView BookmarksListView;
    private ArrayList<Helpers> bookmarks;
    private Helpers bk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);

        // Preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Setup the toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Setup the DrawLayout
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        BookmarksView = (NavigationView) findViewById(R.id.BookmarksView);

        DBHelper = new DatabaseHelper(this);
        bookmarks = new ArrayList<>();
        Cursor data = DBHelper.getListContents();
        while(data.moveToNext()) {
            bk = new Helpers(data.getString(1), data.getString(2));
            bookmarks.add(bk);
        }
        BLAdapter =  new ListAdapter(this, bookmarks, DBHelper);
        BookmarksListView = (ListView) findViewById(R.id.listView);
        BookmarksListView.setAdapter(BLAdapter);

        BookmarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long arg) {
                Helpers item = (Helpers) BookmarksListView.getAdapter().getItem(position);
                mWebView.loadUrl(item.getUrl());
            }
        });

        ImageButton newbookmark = (ImageButton) findViewById(R.id.add_bookmark);
        newbookmark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bk = new Helpers(mWebView.getTitle(), mWebView.getUrl());
                DBHelper.addData(bk.getTitle(), bk.getUrl());
                bookmarks.add(bk);
                BLAdapter.notifyDataSetChanged();
                CookingAToast.cooking(MainActivity.this, getString(R.string.new_bookmark) + " " + mWebView.getTitle(), Color.WHITE, Color.parseColor("#214594"), R.drawable.ic_bookmarks, false).show();
            }
        });

        // Create the badge for messages
        ActionItemBadge.update(this, mNavigationView.getMenu().findItem(R.id.nav_messages), (Drawable) null, BADGE_SIDE_FULL, Integer.MIN_VALUE);
        ActionItemBadge.update(this, mNavigationView.getMenu().findItem(R.id.nav_friendreq), (Drawable) null, BADGE_SIDE_FULL, Integer.MIN_VALUE);

        // Hide buttons if they are disabled
        if (!mPreferences.getBoolean("messaging_enabled", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_messages).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_groups", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_groups).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_search", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_search).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_mainmenu", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_mainmenu).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_most_recent", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_most_recent).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_fblogout", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_fblogout).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_events", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_events).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_photos", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_photos).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_back", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_back).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_exitapp", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_exitapp).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_top_stories", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_top_stories).setVisible(false);
        }
        if (!mPreferences.getBoolean("nav_friendreq", false)) {
            mNavigationView.getMenu().findItem(R.id.nav_friendreq).setVisible(false);
        }

        // Start the Swipe to reload listener
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeView.setColorSchemeResources(android.R.color.white);
        swipeView.setProgressBackgroundColorSchemeColor(Theme.getColor(this));
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        // Inflate the FAB menu
        mMenuFAB = (FloatingActionMenu) findViewById(R.id.menuFAB);
        // Nasty hack to get the FAB menu button
        mMenuFAB.getChildAt(4).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMenuFAB.hideMenu(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Show your View after 10 seconds
                        mMenuFAB.showMenu(true);
                    }
                }, 10000);
                return false;
            }
        });
        findViewById(R.id.textFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.photoFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.checkinFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.topFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.shareFAB).setOnClickListener(mFABClickListener);

        // Load the WebView
        mWebView = (MFBWebView) findViewById(R.id.webview);
        mWebView.setOnScrollChangedCallback(new MFBWebView.OnScrollChangedCallback() {
            @Override
            public void onScrollChange(WebView view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Make sure the hiding is enabled and the scroll was significant
                if (Math.abs(oldScrollY - scrollY) > getApplication().getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold)) {
                    if (scrollY > oldScrollY) {
                        // User scrolled down, hide the button
                        mMenuFAB.hideMenuButton(true);
                    } else if (scrollY < oldScrollY) {
                        // User scrolled up, show the button
                        mMenuFAB.showMenuButton(true);
                    }
                }
            }
        });

         if (!mPreferences.getBoolean("save_data", false)) {
            URLBase = String.format("https://m.facebook.com/");
            baseURL = String.format("https://m.facebook.com/");
        } else {
            URLBase = String.format("https://mbasic.facebook.com/");
            baseURL = String.format("https://mbasic.facebook.com/");
        }

        switch (mPreferences.getString("start_url", "MostRecent")) {
            case "Most_recent": {
                URLBase = String.format(URLBase + "home.php?sk=h_chr");
            }
            break;
            case "Top_stories": {
                URLBase = String.format(URLBase + "home.php?sk=h_nor");
            }
            break;
            case "Messages": {
                URLBase = String.format(URLBase + "messages/");
            }
            break;
        }

        assert mWebView != null;

        if (checkLoggedInState()) {
            mWebView.loadUrl(URLBase);
        }

        mWebView.getSettings().setGeolocationEnabled(mPreferences.getBoolean("location_enabled", false));
        mWebView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");
        mWebView.addJavascriptInterface(this, "Vid");
        mWebView.getSettings().setBlockNetworkImage(mPreferences.getBoolean("stop_images", false));
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (BB10; Kbd) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.1.0.4633 Mobile Safari/537.10+");
        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint("NewApi")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    // clean an url from facebook redirection before processing (no more blank pages on back)
                    if (url != null)
                        url = Helpers.cleanAndDecodeUrl(url);

                    if (url.contains("mailto:")) {
                        Intent mailto = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(mailto);
                    }

                    if ((Uri.parse(url).getHost().endsWith("facebook.com")
                            || Uri.parse(url).getHost().endsWith("*.facebook.com")
                            || Uri.parse(url).getHost().endsWith("akamaihd.net")
                            || Uri.parse(url).getHost().endsWith("ad.doubleclick.net")
                            || Uri.parse(url).getHost().endsWith("sync.liverail.com")
                            || Uri.parse(url).getHost().endsWith("lookaside.fbsbx.com"))) {
                        return false;
                    }

                    if (url.contains("giphy") || url.contains("gifspace") || url.contains("tumblr") || url.contains("gph") || url.contains("gif") || url.contains("fbcdn.net")) {
                        if (url.contains("giphy") || url.contains("gph")) {
                            if (!url.endsWith(".gif")) {
                                if (url.contains("giphy.com") || url.contains("html5")) {
                                    url = String.format("http://media.giphy.com/media/%s/giphy.gif", url.replace("http://giphy.com/gifs/", ""));
                                } else if (url.contains("gph.is") && !url.contains("html5")) {
                                    view.loadUrl(url);
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
                                }
                            }
                        }

                        if (url.contains("gifspace")) {
                            if (!url.endsWith(".gif")) {
                                url = String.format("http://gifspace.net/image/%s.gif", url.replace("http://gifspace.net/image/", ""));
                            }
                        }

                        Intent Photo = new Intent(MainActivity.this, Photo.class);
                        Photo.putExtra("url", url);
                        Photo.putExtra("title", view.getTitle());
                        startActivity(Photo);
                        return true;
                    }

                    // Open external links in browser
                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browser);

                    return true;
                } catch (NullPointerException npe) {
                    return true;
                }
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("https://mbasic.facebook.com/home.php?s=")) {
                    view.loadUrl(baseURL);
                }
            }

            @Override
            public void onLoadResource(final WebView view, final String url) {
                JavaScriptHelpers.videoView(view);

                // Hide Orange highlight on focus
                String css = "*%7B-webkit-tap-highlight-color%3Atransparent%3Boutline%3A0%7D";

                // Hide the menu bar (but not on the composer or if disabled)
                if (mPreferences.getBoolean("hide_menu_bar", true) && !url.contains("/composer/") && !url.contains("/friends/") && !url.contains("sharer") && !url.contains("events")) {
                    css += "%23page%7Btop%3A-45px%7D";
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

                if (mPreferences.getBoolean("comments_recently", true)) {
                    css += "div._4nmh._2g7c._4u3j%7Bdisplay%3Anone%3B%7D" + "header._5rgs._5sg5%7Bdisplay%3Anone%3B%7D";
                }

                css += "article#u_0_q._d2r%7Bdisplay%3Anone%3B%7D";

                // Web themes
                switch (mPreferences.getString("web_themes", "Material")) {
                    case "FacebookMobile": {
                        css += "i.img {\nborder-radius: 0;\n}";
                    }
                    break;
                    case "Material": {
                        css += getString(R.string.Material);
                    }
                    break;
                    case "MaterialAmoled": {
                        css += getString(R.string.MaterialAmoled);
                    }
                    break;
                    case "MaterialBlack": {
                        css += getString(R.string.MaterialBlack);
                    }
                    break;
                    case "MaterialPink": {
                        css += getString(R.string.MaterialPink);
                    }
                    break;
                    case "MaterialGrey": {
                        css += getString(R.string.MaterialGrey);
                    }
                    break;
                    case "MaterialGreen": {
                        css += getString(R.string.MaterialGreen);
                    }
                    break;
                    case "MaterialRed": {
                        css += getString(R.string.MaterialRed);
                    }
                    break;
                    case "MaterialLime": {
                        css += getString(R.string.MaterialLime);
                    }
                    break;
                    case "MaterialYellow": {
                        css += getString(R.string.MaterialYellow);
                    }
                    break;
                    case "MaterialPurple": {
                        css += getString(R.string.MaterialPurple);
                    }
                    break;
                    case "MaterialLightBlue": {
                        css += getString(R.string.MaterialLightBlue);
                    }
                    break;
                    case "MaterialOrange": {
                        css += getString(R.string.MaterialOrange);
                    }
                    break;
                    case "MaterialGooglePlayGreen": {
                        css += getString(R.string.MaterialGPG);
                    }
                    break;
                }

                if (url.contains("lookaside")) {
                    Url = url;
                    RequestStoragePermission();
                }

                // Inject the css
                JavaScriptHelpers.loadCSS(view, css);

                // Get the currently open tab and check on the navigation menu
                JavaScriptHelpers.updateCurrentTab(view);
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                // Load a certain page if there is a parameter
                JavaScriptHelpers.paramLoader(view, url);

                // Hide Orange highlight on focus
                String css = "*%7B-webkit-tap-highlight-color%3Atransparent%3Boutline%3A0%7D";

                // Stop loading
                setLoading(false);

                // Enable or disable FAB
                if (url.contains("messages/") || !mPreferences.getBoolean("fab_enable", false)) {
                    mMenuFAB.hideMenu(true);
                } else {
                    mMenuFAB.showMenu(true);
                }

                if (url.contains("messages/")) {
                    css += ".touch ._52we, .fcl {text-align: center;}";
                    css += "i.img {border-radius: 200px;}";
                }

                if (url.contains("https://mbasic.facebook.com/composer/?text=")) {
                    UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
                    sanitizer.setAllowUnregisteredParamaters(true);
                    sanitizer.parseUrl(url);
                    String param = sanitizer.getValue("text");
                    view.loadUrl("javascript:(function()%7Bdocument.querySelector('%23composerInput').innerHTML%3D'" + param + "'%7D)()");
                }

                // Inject the css
                JavaScriptHelpers.loadCSS(view, css);

                // Get the currently open tab and check on the navigation menu
                JavaScriptHelpers.updateCurrentTab(view);

                // Get the notification number
                JavaScriptHelpers.updateNumsService(view);
            }
        });

        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);

        // Long press
        mWebView.setWebChromeClient(new WebChromeClient() {

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                // Double check that we don't have any existing callbacks
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                // Set up the take picture intent
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                // Set up the intent to get an existing image
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");

                // Set up the intents for the Intent chooser
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            // openFileChooser for Android 3.0+
            private void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

                // Update message
                mUploadMessage = uploadMsg;
                File imageStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs();
                }
                // Create camera captured image file path and name
                File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                mCapturedImageURI = Uri.fromFile(file);

                // Camera capture image intent
                final Intent captureIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");

                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

                // Set camera intent to file chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                // On select image call onActivityResult method of activity
                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
            }

            private File createImageFile() throws IOException {
                // Create an image file name
                final String imageFileName = "JPEG_" + String.valueOf(System.currentTimeMillis()) + "_";
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                final File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
                return imageFile;
            }

            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (view.getUrl().contains("home.php?sk=h_nor")) {
                    setTitle(R.string.menu_top_stories);
                } else if (title.contains("Facebook")) {
                    setTitle(R.string.menu_most_recent);
                } else {
                    setTitle(title);
                }
            }
        });

        // Add OnClick listener to Profile picture
        ImageView profileImage = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.profile_picture);
        profileImage.setClickable(true);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserLink != null) {
                    drawer.closeDrawers();
                    mWebView.loadUrl(mUserLink);
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> loginResult = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mWebView.loadUrl(URLBase);
                updateUserInfo();
            }

            @Override
            public void onCancel() {
                checkLoggedInState();
            }

            @Override
            public void onError(FacebookException error) {
                CookingAToast.cooking(MainActivity.this, getString(R.string.error_login), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                LoginManager.getInstance().logOut();
                checkLoggedInState();
            }
        };

        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
        LoginManager.getInstance().registerCallback(callbackManager, loginResult);

        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        // Handling the intents
        if (getIntent() != null && getIntent().getExtras() != null) {
            URLBase = getIntent().getExtras().getString("url");
            mWebView.loadUrl(URLBase);
        }

        if (Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getType() != null) {
            if (URLUtil.isValidUrl(getIntent().getStringExtra(Intent.EXTRA_TEXT))) {
                try {
                    mWebView.loadUrl("https://mbasic.facebook.com/composer/?text=" + URLEncoder.encode(getIntent().getStringExtra(Intent.EXTRA_TEXT), "utf-8"));
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                }
            }
        }

        if (getIntent() != null && getIntent().getDataString() != null) {
            URLBase = getIntent().getDataString();
            mWebView.loadUrl(URLBase.replace("fb://profile/", "https://facebook.com/"));
        }

    }

    public void RequestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Url));
                    File downloads_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!downloads_dir.exists()) {
                        if (!downloads_dir.mkdirs()) {
                            return;
                        }
                    }
                    File destinationFile = new File(downloads_dir, Uri.parse(Url).getLastPathSegment());
                    request.setDestinationUri(Uri.fromFile(destinationFile));
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    mDownloadManager.enqueue(request);

                    CookingAToast.cooking(this, getString(R.string.downloaded), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_download, false).show();
                } else {
                    CookingAToast.cooking(this, getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (getIntent().getBooleanExtra("apply", false)) {
            finish();
            Intent apply = new Intent(this, MainActivity.class);
            startActivity(apply);
        }

        // Handling the intents
        if (getIntent() != null && getIntent().getExtras() != null) {
            URLBase = getIntent().getExtras().getString("url");
        }

        if (Intent.ACTION_SEND.equals(getIntent().getAction()) && getIntent().getType() != null) {
            if (URLUtil.isValidUrl(getIntent().getStringExtra(Intent.EXTRA_TEXT))) {
                try {
                    mWebView.loadUrl("https://mbasic.facebook.com/composer/?text=" + URLEncoder.encode(getIntent().getStringExtra(Intent.EXTRA_TEXT), "utf-8"));
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                }
            }
        }

        if (getIntent() != null && getIntent().getDataString() != null) {
            URLBase = getIntent().getDataString();
            URLBase.replace("fb://profile/", "https://facebook.com/");
        }

        if (checkLoggedInState()) {
            mWebView.loadUrl(URLBase);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        DBHelper.close();
        super.onDestroy();
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.removeAllViews();
        mWebView.destroy();

        if (mPreferences.getBoolean("clear_cache", false))
            deleteCache(this);

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Thanks to Koras for the tutorial. http://dev.indywidualni.org/2015/02/an-advanced-webview-with-some-cool-features
        if (Build.VERSION.SDK_INT >= 21) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                if(requestCode==FILECHOOSER_RESULTCODE) {
                    if (null == this.mUploadMessage) {
                        return;
                    }

                    Uri result;
                     if (resultCode != RESULT_OK) {
                         result = null;
                     } else {
                         // retrieve from the private variable if the intent is null
                         result = data == null ? mCapturedImageURI : data.getData();
                     }

                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mPreferences.getBoolean("save_data", false)) {
            updateUserInfo();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private static void deleteCache(Context context) {
        try {
            final File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            //
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            final String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mNotificationButton = menu.findItem(R.id.action_notifications);
        ActionItemBadge.update(this, mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
        mMessagesButton = menu.findItem(R.id.nav_messages);
        ActionItemBadge.update(this, mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_message, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            mWebView.loadUrl(baseURL + "notifications.php");
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
            NotificationsService.ClearNotif(this);
        }
        if (id == R.id.nav_messages) {
            mWebView.loadUrl(baseURL + "messages/");
            NotificationsService.ClearMessages(this);
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
        }
        // Update the notifications
        if (!mPreferences.getBoolean("save_data", false)) {
            JavaScriptHelpers.updateNums(mWebView);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_top_stories:
                mWebView.loadUrl(baseURL + "home.php?sk=h_nor");
                item.setChecked(true);
                break;
            case R.id.nav_most_recent:
                mWebView.loadUrl(baseURL + "home.php?sk=h_chr");
                item.setChecked(true);
                break;
            case R.id.nav_friendreq:
                setTitle(R.string.menu_friendreq);
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23requests_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + "https%3A%2F%2Fm.facebook.com%2F" + "friends%2Fcenter%2Frequests%2F'%7D%7D)()");
                } else {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23requests_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + "https%3A%2F%2Fmbasic.facebook.com%2F" + "friends%2Fcenter%2Frequests%2F'%7D%7D)()");
                }
                item.setChecked(true);
                Helpers.uncheckRadioMenu(mNavigationView.getMenu());
                break;
            case R.id.nav_messages:
                mWebView.loadUrl(baseURL + "messages/");
                NotificationsService.ClearMessages(this);
                Helpers.uncheckRadioMenu(mNavigationView.getMenu());
                break;
            case R.id.nav_search:
                setTitle(R.string.menu_search);
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23search_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + "https%3A%2F%2Fm.facebook.com%2F" + "search%2F'%7D%7D)()");
                } else {
                    mWebView.loadUrl("https://mbasic.facebook.com/" + "/search/");
                }
                item.setChecked(true);
                break;
            case R.id.nav_groups:
                mWebView.loadUrl(baseURL + "groups/?category=membership");
                item.setChecked(true);
                break;
            case R.id.nav_mainmenu:
                setTitle(R.string.menu_mainmenu);
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23bookmarks_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + "https%3A%2F%2Fm.facebook.com%2F" + "home.php'%7D%7D)()");
                } else {
                    mWebView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/?ref_component=mbasic_home_header&ref_page=%2Fwap%2Fhome.php&refid=8");
                }
                item.setChecked(true);
                break;
            case R.id.nav_events:
                mWebView.loadUrl(baseURL + "events");
                item.setChecked(true);
                break;
            case R.id.nav_photos:
                mWebView.loadUrl(baseURL + "photos/");
                item.setChecked(true);
                break;
            case R.id.nav_fblogout:
                LoginManager.getInstance().logOut();
                mWebView.reload();
                break;
            case R.id.nav_settings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                break;
            case R.id.nav_back:
                if (mWebView.canGoBack())
                    mWebView.goBack();
                break;
            case R.id.nav_exitapp:
                finishAffinity();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setLoading(boolean loading) {
        // Toggle the WebView and Spinner visibility
        mWebView.setVisibility(loading ? View.GONE : View.VISIBLE);
        swipeView.setRefreshing(loading);
    }

    public boolean checkLoggedInState() {
        if (AccessToken.getCurrentAccessToken() != null && Helpers.getCookie() != null) {
            mWebView.setVisibility(View.VISIBLE);
            return true;
        } else {
            setLoading(false);
            mWebView.setVisibility(View.GONE);
            LoginManager.getInstance().logInWithReadPermissions(this, Helpers.FB_PERMISSIONS);
            return false;
        }
    }

    @JavascriptInterface
    public void LoadVideo(final String video_url) {
        Intent Video = new Intent(this, Video.class);
        Video.putExtra("video_url", video_url);
        startActivity(Video);
    }

    private void updateUserInfo() {
        if (!mPreferences.getBoolean("save_data", false)) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    // Update header
                    try {
                        mUserLink = object.getString("link");

                        // Set the user's name under the header
                        ((TextView) findViewById(R.id.profile_name)).setText(object.getString("name"));

                        // Set the cover photo with resizing
                        Glide.with(MainActivity.this).load("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large").diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) findViewById(R.id.profile_picture));

                        Glide.with(MainActivity.this).load(object.getJSONObject("cover").getString("source")).diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) findViewById(R.id.cover));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,cover,link");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }
    public void setNotificationNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications, null), num);
        } else {
            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications, null), Integer.MIN_VALUE);
        }
    }

    public void setMessagesNum(int num) {
        // Only update message count if enabled
        if (mPreferences.getBoolean("messaging_enabled", false)) {
            if (num > 0) {
                ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_messages), num);
            } else {
                // Hide the badge and show the washed-out button
                ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_messages), Integer.MIN_VALUE);
            }
        }
        if (num > 0) {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_message, null), num);
        } else {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_message, null), Integer.MIN_VALUE);
        }
    }

    public void setRequestsNum(int num) {
        if (mPreferences.getBoolean("nav_friendreq", false)) {
            if (num > 0) {
                ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_friendreq), num);
            } else {
                // Hide the badge and show the washed-out button
                ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_friendreq), Integer.MIN_VALUE);
            }
        }
    }
}
