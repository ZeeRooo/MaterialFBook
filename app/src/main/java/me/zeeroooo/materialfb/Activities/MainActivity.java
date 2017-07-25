/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks.
 * - Folio for Facebook by creativetrendsapps. Thanks.
 * - Toffed by JakeLane. Thanks.
 */
package me.zeeroooo.materialfb.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.support.v4.view.GravityCompat;
import android.webkit.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zeeroooo.materialfb.Bookmarks.DatabaseHelper;
import me.zeeroooo.materialfb.Bookmarks.ListAdapter;
import me.zeeroooo.materialfb.Notifications.NotificationsService;
import me.zeeroooo.materialfb.Ui.CookingAToast;
import me.zeeroooo.materialfb.Ui.Theme;
import me.zeeroooo.materialfb.WebView.Helpers;
import me.zeeroooo.materialfb.WebView.JavaScriptHelpers;
import me.zeeroooo.materialfb.WebView.JavaScriptInterfaces;
import me.zeeroooo.materialfb.WebView.MFBWebView;
import me.zeeroooo.materialfb.R;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final BadgeStyle BADGE_SIDE_FULL = new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_badge_full, Color.RED, Color.RED, Color.WHITE);
    private ValueCallback<Uri[]> mFilePathCallback;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri> mUploadMessage;
    private final int FILECHOOSER_RESULTCODE = 2888, INPUT_FILE_REQUEST_CODE = 1;
    private String baseURL, mCameraPhotoPath, Url, css;
    private SwipeRefreshLayout swipeView;
    NavigationView mNavigationView, BookmarksView;
    private FloatingActionMenu mMenuFAB;
    public MFBWebView mWebView;
    private MenuItem mNotificationButton, mMessagesButton;
    private CallbackManager callbackManager;
    private SharedPreferences mPreferences;
    private DownloadManager mDownloadManager;
    private DatabaseHelper DBHelper;
    private ListAdapter BLAdapter;
    private ListView BookmarksListView;
    private ArrayList<Helpers> bookmarks;
    private Helpers bk;
    private DrawerLayout drawer;
    private FloatingActionButton UploadFAB;
    Elements elements;
    AsyncTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mWebView = (MFBWebView) findViewById(R.id.webview);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        BookmarksView = (NavigationView) findViewById(R.id.BookmarksView);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Setup the toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Setup the DrawLayout
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        URLs();

        if (checkLoggedInState())
            switch (mPreferences.getString("start_url", "Most_recent")) {
                case "Most_recent":
                    mWebView.loadUrl(baseURL + "home.php?sk=h_chr");
                    break;
                case "Top_stories":
                    mWebView.loadUrl(baseURL + "home.php?sk=h_nor");
                    break;
                case "Messages":
                    mWebView.loadUrl(baseURL + "messages/");
                    break;
                default:
                    break;
            }

        DBHelper = new DatabaseHelper(this);
        bookmarks = new ArrayList<>();
        final Cursor data = DBHelper.getListContents();
        while (data.moveToNext()) {
            bk = new Helpers(data.getString(1), data.getString(2));
            bookmarks.add(bk);
        }
        BLAdapter = new ListAdapter(this, bookmarks, DBHelper);
        BookmarksListView = (ListView) findViewById(R.id.bookmarksListView);
        BookmarksListView.setAdapter(BLAdapter);

        BookmarksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter, View view, int position, long arg) {
                Helpers item = (Helpers) BookmarksListView.getAdapter().getItem(position);
                mWebView.loadUrl(item.getUrl());
                drawer.closeDrawers();
            }
        });

        AppCompatImageButton newbookmark = (AppCompatImageButton) findViewById(R.id.add_bookmark);
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
        ActionItemBadge.update(this, mNavigationView.getMenu().findItem(R.id.nav_most_recent), (Drawable) null, BADGE_SIDE_FULL, Integer.MIN_VALUE);

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

        View.OnClickListener mFABClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.textFAB:
                        mWebView.loadUrl("javascript:(function(){try{document.querySelector('button[name=\"view_overview\"]').click()}catch(_){window.location.href=\"" + baseURL + "?pageload=composer\"}})()");
                        swipeView.setEnabled(false);
                        break;
                    case R.id.photoFAB:
                        mWebView.loadUrl("javascript:(function(){try{document.querySelector('button[name=\"view_photo\"]').click()}catch(_){window.location.href=\"" + baseURL + "?pageload=composer_photo\"}})()");
                        swipeView.setEnabled(false);
                        break;
                    case R.id.checkinFAB:
                        mWebView.loadUrl("javascript:(function(){try{document.querySelector('button[name=\"view_location\"]').click()}catch(_){window.location.href=\"" + baseURL + "?pageload=composer_checkin\"}})()");
                        swipeView.setEnabled(false);
                        break;
                    case R.id.topFAB:
                        mWebView.scrollTo(0, 0);
                        break;
                    case R.id.shareFAB:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.context_share_link)));
                        break;
                    default:
                        break;
                }
                mMenuFAB.close(true);
            }
        };

        findViewById(R.id.textFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.photoFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.checkinFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.topFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.shareFAB).setOnClickListener(mFABClickListener);

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

        mWebView.getSettings().setGeolocationEnabled(mPreferences.getBoolean("location_enabled", false));
        mWebView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");
        mWebView.addJavascriptInterface(this, "Vid");
        mWebView.getSettings().setBlockNetworkImage(mPreferences.getBoolean("stop_images", false));
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);

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
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
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
                            || Uri.parse(url).getHost().endsWith("cdn.fbsbx.com")
                            || Uri.parse(url).getHost().endsWith("lookaside.fbsbx.com"))) {
                        return false;
                    }

                    if (url.contains("giphy") || url.contains("gifspace") || url.contains("tumblr") || url.contains("gph.is") || url.contains("gif") || url.contains("fbcdn.net") || url.contains("imgur")) {
                        if (url.contains("giphy") || url.contains("gph")) {
                            if (!url.endsWith(".gif")) {
                                if (url.contains("giphy.com") || url.contains("html5")) {
                                    url = String.format("http://media.giphy.com/media/%s/giphy.gif", url.replace("http://giphy.com/gifs/", ""));
                                } else if (url.contains("gph.is") && !url.contains("html5")) {
                                    view.loadUrl(url);
                                    url = String.format("http://media.giphy.com/media/%s/giphy.gif", url.replace("http://giphy.com/gifs/", ""));
                                }

                                if (url.contains("media.giphy.com/media/") && !url.contains("html5")) {
                                    String[] giphy = url.split("-");
                                    String giphy_id = giphy[giphy.length - 1];
                                    url = "http://media.giphy.com/media/" + giphy_id;
                                }
                                if (url.contains("media.giphy.com/media/http://media")) {
                                    String[] gph = url.split("/");
                                    String gph_id = gph[gph.length - 2];
                                    url = "http://media.giphy.com/media/" + gph_id + "/giphy.gif";
                                }
                                if (url.contains("html5/giphy.gif")) {
                                    String[] giphy_html5 = url.split("/");
                                    String giphy_html5_id = giphy_html5[giphy_html5.length - 3];
                                    url = "http://media.giphy.com/media/" + giphy_html5_id + "/giphy.gif";
                                }
                            }
                            if (url.contains("?")) {
                                String[] giphy1 = url.split("\\?");
                                String giphy_html5_id = giphy1[0];
                                url = giphy_html5_id + "/giphy.gif";
                                System.out.println(url);
                            }
                        }

                        if (url.contains("gifspace")) {
                            if (!url.endsWith(".gif")) {
                                url = String.format("http://gifspace.net/image/%s.gif", url.replace("http://gifspace.net/image/", ""));
                            }
                        }

                        if (url.contains("phygee")) {
                            if (!url.endsWith(".gif")) {
                                getSrc(url, "span", "img");
                                url = "http://www.phygee.com/" + elements.attr("src");
                            }
                        }

                        if (url.contains("imgur")) {
                            if (!url.endsWith(".gif") && !url.endsWith(".jpg")) {
                                getSrc(url, "div.post-image", "img");
                                url = "https:" + elements.attr("src");
                            }
                        }

                        if (url.contains("media.upgifs.com")) {
                            if (!url.endsWith(".gif")) {
                                getSrc(url, "div.gif-pager-container", "img#main-gif");
                                url = elements.attr("src");
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

            private void getSrc(final String url, final String select, final String select2) {
                 mTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground (Void[] params){
                        try {
                            Document document = Jsoup.connect(url).get();
                            elements = document.select(select).select(select2);
                        } catch (IOException ioex) {
                            ioex.getStackTrace();
                        }
                        return null;
                    }
                }.execute();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                swipeView.setRefreshing(true);
                if (url.contains("https://mbasic.facebook.com/home.php?s=")) {
                    view.loadUrl(baseURL);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                JavaScriptHelpers.videoView(view);
                if (!mPreferences.getBoolean("save_data", false))
                    JavaScriptHelpers.updateNumsService(view);
                if (swipeView.isRefreshing()) {
                    JavaScriptHelpers.loadCSS(view, css);
                }
                if (url.contains("facebook.com/composer/mbasic/") || url.contains("https://m.facebook.com/sharer.php?sid=")) {
                    css += "#page{top:0}";
                    Log.d("Contiene", "mal ahi");
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeView.setRefreshing(false);

                switch (mPreferences.getString("web_themes", "Material")) {
                    case "FacebookMobile":
                    break;
                    case "Material":
                        css += getString(R.string.Material);
                    break;
                    case "MaterialAmoled":
                        css += getString(R.string.MaterialAmoled);
                    break;
                    case "MaterialBlack":
                        css += getString(R.string.MaterialBlack);
                    break;
                    case "MaterialPink":
                        css += getString(R.string.MaterialPink);
                    break;
                    case "MaterialGrey":
                        css += getString(R.string.MaterialGrey);
                    break;
                    case "MaterialGreen":
                        css += getString(R.string.MaterialGreen);
                    break;
                    case "MaterialRed":
                        css += getString(R.string.MaterialRed);
                    break;
                    case "MaterialLime":
                        css += getString(R.string.MaterialLime);
                    break;
                    case "MaterialYellow":
                        css += getString(R.string.MaterialYellow);
                    break;
                    case "MaterialPurple":
                        css += getString(R.string.MaterialPurple);
                    break;
                    case "MaterialLightBlue":
                        css += getString(R.string.MaterialLightBlue);
                    break;
                    case "MaterialOrange":
                        css += getString(R.string.MaterialOrange);
                    break;
                    case "MaterialGooglePlayGreen":
                        css += getString(R.string.MaterialGPG);
                    break;
                    default:
                        break;
                }

                if (url.contains("lookaside") || url.contains("cdn.fbsbx.com")) {
                    Url = url;
                    RequestStoragePermission();
                }

                // Enable or disable FAB
                if (url.contains("messages") || !mPreferences.getBoolean("fab_enable", false)) {
                    mMenuFAB.setVisibility(View.GONE);
                } else {
                    mMenuFAB.setVisibility(View.VISIBLE);
                }

                if (url.contains("https://mbasic.facebook.com/composer/?text=")) {
                    final UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
                    sanitizer.setAllowUnregisteredParamaters(true);
                    sanitizer.parseUrl(url);
                    final String param = sanitizer.getValue("text");
                    view.loadUrl("javascript:(function(){document.querySelector('#composerInput').innerHTML='" + param + "'})()");
                }

                if (url.contains("https://m.facebook.com/public/")) {
                    String[] user = url.split("/");
                    String profile = user[user.length - 1];
                    view.loadUrl("javascript:(function(){document.querySelector('input#u_0_0._5whq.input').value='" + profile + "'})()");
                    view.loadUrl("javascript:(function(){try{document.querySelector('button#u_0_1.btn.btnD.mfss.touchable').disabled = false}catch(_){}})()");
                    view.loadUrl("javascript:(function(){try{document.querySelector('button#u_0_1.btn.btnD.mfss.touchable').click()}catch(_){}})()");
                }

                // Hide Orange highlight on focus
                css += "*{-webkit-tap-highlight-color:transparent;outline:0}";

                if (mPreferences.getBoolean("hide_menu_bar", true)) {
                    css += "#page{top:-45px}";
                }
                // Hide the status editor on the News Feed if setting is enabled
                if (mPreferences.getBoolean("hide_editor_newsfeed", true)) {
                    css += "#mbasic_inline_feed_composer{display:none}";
                }

                // Hide 'Sponsored' content (ads)
                if (mPreferences.getBoolean("hide_sponsored", true)) {
                    css += "article[data-ft*=ei]{display:none}";
                }

                // Hide birthday content from News Feed
                if (mPreferences.getBoolean("hide_birthdays", true)) {
                    css += "article#u_1j_4{display:none}" + "article._55wm._5e4e._5fjt{display:none}";
                }

                if (mPreferences.getBoolean("comments_recently", true)) {
                    css +=  "._15ks+._4u3j{display:none}";
                }

                css += "article#u_0_q._d2r{display:none}";
            }
        });
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
                drawer.closeDrawers();
                mWebView.loadUrl(baseURL + "me");
            }
        });

        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> loginResult = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                updateUserInfo();
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl("https://m.facebook.com/");
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) {
                CookingAToast.cooking(MainActivity.this, getString(R.string.error_login), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                LoginManager.getInstance().logOut();
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager, loginResult);

        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        if (getIntent() != null)
            UrlIntent(getIntent());
    }

    public void RequestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void URLs() {
        if (!mPreferences.getBoolean("save_data", false)) {
            baseURL = "https://m.facebook.com/";
        } else {
            baseURL = "https://mbasic.facebook.com/";
        }
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
        setIntent(intent);

        if (intent.getBooleanExtra("apply", false)) {
            finish();
            Intent apply = new Intent(this, MainActivity.class);
            startActivity(apply);
        }

        UrlIntent(intent);
    }

    private void QuickUpload(final Intent intent) {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.share_dialog, null);
        final AlertDialog QuickUpload = new AlertDialog.Builder(this).create();
        final EditText quick_description = (EditText) view.findViewById(R.id.quick_description);
        UploadFAB = (FloatingActionButton) view.findViewById(R.id.upload_fab);
        final Uri uriToShare = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        final AppCompatImageView preview = (AppCompatImageView) view.findViewById(R.id.preview_img);

        Glide.with(this).load(uriToShare).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).format(PREFER_ARGB_8888).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {

                if (bitmap.getWidth() >= 4096 || bitmap.getHeight() >= 4096) {
                    Bitmap resized = ThumbnailUtils.extractThumbnail(bitmap, 400, 400);
                    preview.setImageBitmap(resized);
                }  else {
                    preview.setImageBitmap(bitmap);
                }

                UploadFAB.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (quick_description.getText().toString().isEmpty()) {
                            quick_description.setText(" ");
                        }
                        if (intent.getType().startsWith("image/")) {
                            SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).setCaption(quick_description.getText().toString()).setUserGenerated(true).build();
                            SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                            ShareApi.share(content, null);
                            CookingAToast.cooking(MainActivity.this, getString(R.string.sharing_info), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_share, false).show();
                        } else {
                            ShareVideo video = new ShareVideo.Builder().setLocalUrl(uriToShare).build();
                            ShareVideoContent contentVid = new ShareVideoContent.Builder().setVideo(video).setContentDescription(quick_description.getText().toString()).build();
                            ShareApi.share(contentVid, null);
                            CookingAToast.cooking(MainActivity.this, getString(R.string.sharing_info), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_share, false).show();
                        }

                        QuickUpload.dismiss();
                        bitmap.recycle();
                    }
                });
            }
        });

        QuickUpload.setView(view);
        QuickUpload.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
        JavaScriptHelpers.updateNumsService(mWebView);
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
        // super.onActivityResult(requestCode, resultCode, data);
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

                if (requestCode == FILECHOOSER_RESULTCODE) {
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
        if (!mPreferences.getBoolean("save_data", false))
            updateUserInfo();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
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
        URLs();
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            mWebView.loadUrl(baseURL + "notifications.php");
            setTitle(R.string.nav_notifications);
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
            NotificationsService.ClearNotif(this);
        }
        if (id == R.id.nav_messages) {
            mWebView.loadUrl(baseURL + "messages/");
            NotificationsService.ClearMessages(this);
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        URLs();
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_top_stories:
                mWebView.loadUrl(baseURL + "home.php?sk=h_nor");
                setTitle(R.string.menu_top_stories);
                item.setChecked(true);
                break;
            case R.id.nav_most_recent:
                mWebView.loadUrl(baseURL + "home.php?sk=h_chr'");
                setTitle(R.string.menu_most_recent);
                item.setChecked(true);
                Helpers.uncheckRadioMenu(mNavigationView.getMenu());
                break;
            case R.id.nav_friendreq:
                mWebView.loadUrl(baseURL + "friends/center/requests/");
                setTitle(R.string.menu_friendreq);
                item.setChecked(true);
                Helpers.uncheckRadioMenu(mNavigationView.getMenu());
                break;
            case R.id.nav_messages:
                mWebView.loadUrl(baseURL + "messages/");
                NotificationsService.ClearMessages(this);
                Helpers.uncheckRadioMenu(mNavigationView.getMenu());
                break;
            case R.id.nav_search:
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function(){try{document.querySelector('#search_jewel > a').click()}catch(_){window.location.href='" + baseURL + "search/?refid=8'}})()");
                } else {
                    mWebView.loadUrl(baseURL + "search/");
                }
                setTitle(R.string.menu_search);
                item.setChecked(true);
                break;
            case R.id.nav_groups:
                mWebView.loadUrl(baseURL + "groups/?category=membership");
                css += "._129- {position:initial}";
                item.setChecked(true);
                break;
            case R.id.nav_mainmenu:
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23bookmarks_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + "https%3A%2F%2Fm.facebook.com%2F" + "home.php'%7D%7D)()");
                } else {
                    mWebView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/?ref_component=mbasic_home_header&ref_page=%2Fwap%2Fhome.php&refid=8");
                }
                setTitle(R.string.menu_mainmenu);
                item.setChecked(true);
                break;
            case R.id.nav_events:
                mWebView.loadUrl(baseURL + "events/upcoming");
                css += "#page{top:0}";
                item.setChecked(true);
                break;
            case R.id.nav_photos:
                mWebView.loadUrl(baseURL + "me/photos");
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

        drawer.closeDrawers();
        return true;
    }

    public boolean checkLoggedInState() {
        if (AccessToken.getCurrentAccessToken() != null && Helpers.getCookie() != null) {
            mWebView.setVisibility(View.VISIBLE);
            return true;
        } else {
            LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
            mWebView.setVisibility(View.GONE);
            return false;
        }
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void LoadVideo(final String video_url) {
        Intent Video = new Intent(this, Video.class);
        Video.putExtra("video_url", video_url);
        startActivity(Video);
    }

    private void updateUserInfo() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // Update header
                try {
                    // Set the user's name under the header
                    ((AppCompatTextView) findViewById(R.id.profile_name)).setText(object.getString("name"));

                    Glide.with(MainActivity.this).load("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large").diskCacheStrategy(DiskCacheStrategy.SOURCE).into((CircleImageView) findViewById(R.id.profile_picture));

                    Glide.with(MainActivity.this).load(object.getJSONObject("cover").getString("source")).diskCacheStrategy(DiskCacheStrategy.SOURCE).into((AppCompatImageView) findViewById(R.id.cover));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,cover");
        request.setParameters(parameters);
        request.executeAsync();
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

    public void setMrNum(int num) {
        if (mPreferences.getBoolean("nav_most_recent", false)) {
            if (num > 0) {
                ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_most_recent), num);
            } else {
                // Hide the badge and show the washed-out button
                ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_most_recent), Integer.MIN_VALUE);
            }
        }
    }

    private void UrlIntent(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (URLUtil.isValidUrl(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                try {
                    mWebView.loadUrl("https://mbasic.facebook.com/composer/?text=" + URLEncoder.encode(intent.getStringExtra(Intent.EXTRA_TEXT), "utf-8"));
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                }
            }
        }

        if (intent.getExtras() != null) {
            baseURL = intent.getExtras().getString("url");
        }

        if (intent.getDataString() != null) {
            baseURL = getIntent().getDataString();
            if (intent.getDataString().contains("profile"))
                baseURL.replace("fb://profile/", "https://facebook.com/");
        }

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (intent.getType().startsWith("image/") || intent.getType().startsWith("video/"))
                QuickUpload(intent);
        }

        mWebView.loadUrl(baseURL);
    }
}
