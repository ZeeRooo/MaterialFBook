/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks.
 * - Folio for Facebook by creativetrendsapps. Thanks.
 * - Toffed by JakeLane. Thanks.
 */
package me.zeeroooo.materialfb.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.clans.fab.FloatingActionMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;

import berlin.volders.badger.BadgeShape;
import berlin.volders.badger.Badger;
import berlin.volders.badger.CountBadge;
import me.zeeroooo.materialfb.Bookmarks.DatabaseHelper;
import me.zeeroooo.materialfb.Bookmarks.ListAdapter;
import me.zeeroooo.materialfb.Notifications.NotificationsService;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.CookingAToast;
import me.zeeroooo.materialfb.Ui.Theme;
import me.zeeroooo.materialfb.WebView.Helpers;
import me.zeeroooo.materialfb.WebView.JavaScriptHelpers;
import me.zeeroooo.materialfb.WebView.JavaScriptInterfaces;
import me.zeeroooo.materialfb.WebView.MFBWebView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ValueCallback<Uri[]> mFilePathCallback;
    private Uri mCapturedImageURI = null, sharedFromGallery;
    private ValueCallback<Uri> mUploadMessage;
    private final int FILECHOOSER_RESULTCODE = 2888, INPUT_FILE_REQUEST_CODE = 1;
    private String baseURL, mCameraPhotoPath, Url, urlIntent, cookies = Helpers.getCookie();
    private SwipeRefreshLayout swipeView;
    private FloatingActionMenu mMenuFAB;
    public MFBWebView mWebView;
    private SharedPreferences mPreferences;
    private DownloadManager mDownloadManager;
    private DatabaseHelper DBHelper;
    private ListAdapter BLAdapter;
    private ListView BookmarksListView;
    private ArrayList<Helpers> bookmarks;
    private Helpers bk;
    private DrawerLayout drawer;
    private Elements elements;
    private Toolbar mToolbar, searchToolbar;
    private BottomNavigationView mBottomNav;
    private Menu menu, search_menu;
    private CountBadge.Factory circleFactory;
    public static String css;
    private Element eName;
    private Document dName;
    private MenuItem searchItem;
    private SearchView searchView;
    AppBarLayout appBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Theme.Temas(this, mPreferences);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleFactory = new CountBadge.Factory(this, BadgeShape.circle(.7f, Gravity.END | Gravity.TOP));

        mWebView = (MFBWebView) findViewById(R.id.webview);
        NavigationView BookmarksView = (NavigationView) findViewById(R.id.BookmarksView);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Setup the toolbar
        appBar = (AppBarLayout) findViewById(R.id.appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        searchToolbar();

        URLs();

        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        menu = mBottomNav.getMenu();

        switch (mPreferences.getString("start_url", "Most_recent")) {
            case "Most_recent":
                mWebView.loadUrl(baseURL + "home.php?sk=h_chr");
                menu.getItem(0).setIcon(R.drawable.ic_menu_most_recent);
                menu.getItem(0).setTitle(R.string.menu_most_recent);
                break;
            case "Top_stories":
                mWebView.loadUrl(baseURL + "home.php?sk=h_nor");
                menu.getItem(0).setIcon(R.drawable.ic_menu_top_stories);
                menu.getItem(0).setTitle(R.string.menu_top_stories);
                break;
            case "Messages":
                mWebView.loadUrl(baseURL + "messages/");
                break;
        }

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        mWebView.loadUrl(baseURL + "home.php?sk=h_chr");
                        Badger.sett(menu.getItem(0), circleFactory).setCount(0);
                        break;
                    case R.id.bottom_message:
                        mWebView.loadUrl(baseURL + "messages/");
                        NotificationsService.ClearMessages(MainActivity.this);
                        Badger.sett(menu.getItem(1), circleFactory).setCount(0);
                        break;
                    case R.id.bottom_notifications:
                        mWebView.loadUrl(baseURL + "notifications.php");
                        setTitle(R.string.nav_notifications);
                        NotificationsService.ClearNotif(MainActivity.this);
                        Badger.sett(menu.getItem(2), circleFactory).setCount(0);
                        break;
                    case R.id.bottom_more:
                        if (!mPreferences.getBoolean("save_data", false)) {
                            mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23bookmarks_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + "https%3A%2F%2Fm.facebook.com%2F" + "home.php'%7D%7D)()");
                        } else {
                            mWebView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/?ref_component=mbasic_home_header&ref_page=%2Fwap%2Fhome.php&refid=8");
                        }
                        setTitle(R.string.menu_mainmenu);
                        break;
                }
                return true;
            }
        });


        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mMenuFAB.setTranslationY((float) (verticalOffset * -4));
                mBottomNav.setTranslationY((float) (verticalOffset * -4));
            }
        });


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
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (BB10; Kbd) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.1.0.4633 Mobile Safari/537.10+");

        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mWebView.getUrl().contains("posts/pcb.")) {
                    WebView.HitTestResult result = mWebView.getHitTestResult();
                    if (result.getType() == WebView.HitTestResult.IMAGE_TYPE) {
                        Message msg = mHandler.obtainMessage();
                        mWebView.requestImageRef(msg);
                    }
                }
                return false;
            }

        });

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
                        Photo.putExtra("link", url);
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
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void[] params) {
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
                if (swipeView.isRefreshing())
                    JavaScriptHelpers.loadCSS(view, css);
                if (url.contains("facebook.com/composer/mbasic/") || url.contains("https://m.facebook.com/sharer.php?sid="))
                    css += "#page{top:0}";
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeView.setRefreshing(false);

                Theme.Temas(MainActivity.this, mPreferences);

                if (cookies != null && !mPreferences.getBoolean("save_data", false))
                    JavaScriptHelpers.updateNumsService(view);

                if (url.contains("lookaside") || url.contains("cdn.fbsbx.com")) {
                    Url = url;
                    RequestStoragePermission();
                }

                if (cookies != null && !mPreferences.getBoolean("save_data", false) && !url.contains("messages"))
                    updateUserInfo();

                if (url.contains("messages")) {
                    mToolbar.setSubtitle(null);
                    mToolbar.setLogo(null);
                }

                if (url.contains("/?stype=lo") || cookies == null) {
                    mToolbar.setLogo(null);
                    mToolbar.setTitle(R.string.app_name);
                    mToolbar.setSubtitle(null);
                }

                // Enable or disable FAB
                if (url.contains("messages") || !mPreferences.getBoolean("fab_enable", false))
                    mMenuFAB.setVisibility(View.GONE);
                else
                    mMenuFAB.setVisibility(View.VISIBLE);

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

                if (mPreferences.getBoolean("hide_menu_bar", true))
                    css += "#page{top:-45px}";

                // Hide the status editor on the News Feed if setting is enabled
                if (mPreferences.getBoolean("hide_editor_newsfeed", true))
                    css += "#mbasic_inline_feed_composer{display:none}";

                // Hide 'Sponsored' content (ads)
                if (mPreferences.getBoolean("hide_sponsored", true))
                    css += "article[data-ft*=ei]{display:none}";

                // Hide birthday content from News Feed
                if (mPreferences.getBoolean("hide_birthdays", true))
                    css += "article#u_1j_4{display:none}" + "article._55wm._5e4e._5fjt{display:none}";

                if (mPreferences.getBoolean("comments_recently", true))
                    css += "._15ks+._4u3j{display:none}";

                if (url.contains("posts/pcb."))
                    css += "._i81:after {display: none;}";

                if (sharedFromGallery != null)
                    view.loadUrl("javascript:(function(){try{document.getElementsByClassName(\"_56bz _54k8 _52jh _5j35 _157e\")[0].click()}catch(_){document.getElementsByClassName(\"_50ux\")[0].click()}})()");

            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                // Double check that we don't have any existing callbacks
                if (sharedFromGallery != null) {
                    filePathCallback.onReceiveValue(new Uri[]{sharedFromGallery});
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
                if (sharedFromGallery == null) {
                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                }
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            // openFileChooser for Android 3.0+
            private void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

                if (sharedFromGallery != null) {
                    uploadMsg.onReceiveValue(sharedFromGallery);
                } else {
                    mUploadMessage = uploadMsg;
                }

                File imageStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs();
                }
                // Create camera captured image file path and name
                File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                mCapturedImageURI = Uri.fromFile(file);

                // Camera capture image intent
                final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");

                // Create file chooser intent
                if (sharedFromGallery == null) {
                    Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                }
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
                if (view.getUrl().contains("home.php?sk=h_nor"))
                    setTitle(R.string.menu_top_stories);
                else if (title.contains("Facebook"))
                    setTitle(R.string.menu_most_recent);
                else
                    setTitle(title);
            }
        });

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
        // super.onActivityResult(requestCode, resultCode, data);
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
        if (searchView.isSelected())
            searchItem.collapseActionView();
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        URLs();
        int id = item.getItemId();
        if (id == R.id.toolbar_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
        }
        if (id == R.id.action_search) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                circleReveal(R.id.searchtoolbar, 1, true, true);
            else
                searchToolbar.setVisibility(View.VISIBLE);

            searchItem.expandActionView();
        }
        return true;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //   URLs();
        // Handle navigation view item clicks here.
       /* switch (item.getItemId()) {
            case R.id.nav_top_stories:
                mWebView.loadUrl(baseURL + "home.php?sk=h_nor");
                setTitle(R.string.menu_top_stories);
                item.setChecked(true);
                break;
            case R.id.nav_most_recent:
                mWebView.loadUrl(baseURL + "home.php?sk=h_chr'");
                setTitle(R.string.menu_most_recent);
                item.setChecked(true);
                break;
            case R.id.nav_friendreq:
                mWebView.loadUrl(baseURL + "friends/center/requests/");
                setTitle(R.string.menu_friendreq);
                item.setChecked(true);
                break;
            case R.id.nav_messages:
                mWebView.loadUrl(baseURL + "messages/");
                NotificationsService.ClearMessages(this);
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
            case R.id.lol:
                mWebView.loadUrl("javascript:document.querySelector('[name*=\"view_photo\"]').click();");
                break;
            default:
                break;
        }

        drawer.closeDrawers();*/
        return true;
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void LoadVideo(final String video_url) {
        Intent Video = new Intent(this, Video.class);
        Video.putExtra("video_url", video_url);
        startActivity(Video);
    }

    private void updateUserInfo() {
        // Set the user name
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void[] params) {
                try {
                    dName = Jsoup.connect("https://mbasic.facebook.com/me").cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).get();
                    eName = dName.select("span > strong").first();
                } catch (Exception e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(String string) {
                if (eName.text() != null)
                    try {
                        mToolbar.setSubtitle(eName.text());
                    }catch (NullPointerException ignored){
                    }catch (Exception i){
                    i.printStackTrace();
                    }
            }
        }.execute();

        // Set the profile picture
        Glide.with(this)
                .load("https://graph.facebook.com/" + cookies + "/picture?type=large")
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>(90, 90) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(mToolbar.getResources(), bitmap);
                        drawable.setCircular(true);
                        mToolbar.setLogo(drawable);
                        mToolbar.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mWebView.loadUrl(baseURL + "me");
                            }
                        });
                    }
                });
    }

    public void setNotificationNum(int num) {
        if (num > 0) {
            Badger.sett(menu.findItem(R.id.bottom_notifications), circleFactory).setCount(num);
        } else {
            Badger.sett(menu.findItem(R.id.bottom_notifications), circleFactory).setCount(0);
        }
    }

    public void setMessagesNum(int num) {
        if (num > 0) {
            Badger.sett(menu.findItem(R.id.bottom_message), circleFactory).setCount(num);
        } else {
            Badger.sett(menu.findItem(R.id.bottom_message), circleFactory).setCount(0);
        }
    }

    public void setMrNum(int num) {
        if (mPreferences.getBoolean("nav_most_recent", false)) {
            if (num > 0) {
                Badger.sett(menu.findItem(R.id.bottom), circleFactory).setCount(num);
            } else {
                Badger.sett(menu.findItem(R.id.bottom_message), circleFactory).setCount(num);
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
            urlIntent = intent.getExtras().getString("url");
        }

        if (intent.getDataString() != null) {
            urlIntent = getIntent().getDataString();
            if (intent.getDataString().contains("profile"))
                urlIntent.replace("fb://profile/", "https://facebook.com/");
        }

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (intent.getType().startsWith("image/") || intent.getType().startsWith("video/") || intent.getType().startsWith("audio/")) {
                sharedFromGallery = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                css += "#mbasic_inline_feed_composer{display:initial}";
                mWebView.loadUrl("https://m.facebook.com");
            }
        }

        mWebView.loadUrl(urlIntent);
    }

    // Thanks to Jaison Fernando for the great tutorial.
    // http://droidmentor.com/searchview-animation-like-whatsapp/
    public void searchToolbar() {
        searchToolbar = (Toolbar) findViewById(R.id.searchtoolbar);
        searchToolbar.inflateMenu(R.menu.menu_search);
        search_menu = searchToolbar.getMenu();

        searchItem = search_menu.findItem(R.id.action_filter_search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleReveal(R.id.searchtoolbar, 1, true, false);
                } else
                    searchToolbar.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        searchView = (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // set hint and the text colors
        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHint(R.string.menu_search);
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(Theme.getColor(this));

        // set the cursor
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.ic_search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                mWebView.loadUrl(baseURL + "/search/?query=" + query);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

        });
    }

    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View v = findViewById(viewID);

        int width = v.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = v.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, (float) width, 0);

        anim.setDuration((long) 220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    v.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            v.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String url = (String) msg.getData().get("url");
            Intent Photo = new Intent(MainActivity.this, Photo.class);
            Photo.putExtra("link", url);
            Photo.putExtra("title", mWebView.getTitle());
            startActivity(Photo);
        }
    };
}
