//
//* Code taken from:
//* - FaceSlim by indywidualny. Thanks.
//* - Folio for Facebook by creativetrendsapps. Thanks.
//* - Toffed by JakeLane. Thanks.
//
package me.zeeroooo.materialfb.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.adapters.AdapterBookmarks;
import me.zeeroooo.materialfb.misc.DatabaseHelper;
import me.zeeroooo.materialfb.misc.ModelBookmarks;
import me.zeeroooo.materialfb.misc.UserInfo;
import me.zeeroooo.materialfb.ui.CookingAToast;
import me.zeeroooo.materialfb.ui.MFBFloatingActionButton;
import me.zeeroooo.materialfb.ui.MFBResources;
import me.zeeroooo.materialfb.webview.Helpers;
import me.zeeroooo.materialfb.webview.JavaScriptHelpers;
import me.zeeroooo.materialfb.webview.JavaScriptInterfaces;
import me.zeeroooo.materialfb.webview.MFBWebView;

public class MainActivity extends MFBActivity implements NavigationView.OnNavigationItemSelectedListener, Handler.Callback {
    private ValueCallback<Uri[]> mFilePathCallback;
    private Uri mCapturedImageURI = null, sharedFromGallery;
    private ValueCallback<Uri> mUploadMessage;
    private int lastEvent, screenHeight;
    private boolean showAnimation, showHeader = false, loadCss = true, hideHeaderPref;
    private String baseURL, mCameraPhotoPath, Url;
    private SwipeRefreshLayout swipeView;
    private NavigationView mNavigationView;
    private MFBFloatingActionButton mfbFloatingActionButton;
    private MFBWebView mWebView;
    private DatabaseHelper DBHelper;
    private AdapterBookmarks BLAdapter;
    private ListView BookmarksListView;
    private ArrayList<ModelBookmarks> bookmarks;
    private ModelBookmarks bk;
    private DrawerLayout drawer;
    private Elements elements;
    private Toolbar searchToolbar;
    private MenuItem searchItem;
    private SearchView searchView;
    private Handler badgeUpdate;
    private Runnable badgeTask;
    private TextView mr_badge, fr_badge, notif_badge, msg_badge;
    private Cursor cursor;
    private View circleRevealView;
    private final StringBuilder css = new StringBuilder();
    private final Handler mHandler = new Handler(this);

    @Override
    protected void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);

        ((MFBResources) getResources()).setColors(sharedPreferences);

        setContentView(R.layout.activity_main);

        final AppBarLayout appBarLayout = findViewById(R.id.appbarlayout);
        appBarLayout.setBackgroundColor(MFB.colorPrimary);

        final CoordinatorLayout coordinatorLayoutRootView = findViewById(R.id.app_bar_main_root_view);
        coordinatorLayoutRootView.setBackgroundColor(MFB.colorPrimary);

        final CoordinatorLayout coordinatorLayout = findViewById(R.id.app_bar_main_coordinator_layout);
        ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout, (view, insets) ->
                ViewCompat.onApplyWindowInsets(coordinatorLayout, insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom()))
        );

        mWebView = findViewById(R.id.webview);

        mNavigationView = findViewById(R.id.nav_view);

        drawer = findViewById(R.id.drawer_layout);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Setup the DrawLayout
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        if (themeMode == 0 | themeMode == 1) {
            toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
            mToolbar.setTitleTextColor(Color.WHITE);
        } else if (themeMode == 2) {
            toggle.getDrawerArrowDrawable().setColor(Color.BLACK);
            mToolbar.setTitleTextColor(Color.BLACK);
        }

        MFB.textColor = toggle.getDrawerArrowDrawable().getColor();

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        URLs();

        switch (sharedPreferences.getString("start_url", "Most_recent")) {
            case "Most_recent":
                baseURL += "home.php?sk=h_chr";
                break;
            case "Top_stories":
                baseURL += "home.php?sk=h_nor";
                break;
            case "Messages":
                baseURL += "messages/";
                break;
            default:
                break;
        }

        UrlIntent(getIntent());

        swipeView = findViewById(R.id.swipeLayout);
        if (themeMode == 0 | themeMode == 1)
            swipeView.setColorSchemeResources(android.R.color.white);
        else
            swipeView.setColorSchemeResources(android.R.color.black);
        swipeView.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.colorPrimary));
        swipeView.setOnRefreshListener(() -> mWebView.reload());

        final Rect rect = new Rect();
        coordinatorLayoutRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            coordinatorLayoutRootView.getWindowVisibleDisplayFrame(rect);

            screenHeight = coordinatorLayoutRootView.getHeight();

            swipeView.setEnabled((screenHeight - rect.bottom) < screenHeight * 0.15);
        });

        // Inflate the FAB menu
        mfbFloatingActionButton = findViewById(R.id.menuFAB);

        View.OnClickListener mFABClickListener = (View v) -> {
            switch (v.getId()) {
                case 1:
                    mWebView.loadUrl("javascript:(function(){document.getElementsByClassName('_3-99')[1].click()})()");
                    swipeView.setEnabled(false);
                    break;
                case 2:
                    mWebView.loadUrl("javascript:(function(){document.getElementsByClassName('_3-99')[0].click()})()");
                    swipeView.setEnabled(false);
                    break;
                case 3:
                    mWebView.loadUrl("javascript:(function(){document.getElementsByClassName('_3-99')[3].click()})()");
                    swipeView.setEnabled(false);
                    break;
                case 4:
                    mWebView.scrollTo(0, 0);
                    break;
                case 5:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.context_share_link)));
                    break;
            }
        };

        mfbFloatingActionButton.addButton(getResources().getDrawable(R.drawable.ic_fab_checkin), getString(R.string.fab_checkin), FloatingActionButton.SIZE_MINI, mFABClickListener);
        mfbFloatingActionButton.addButton(getResources().getDrawable(R.drawable.ic_fab_photo), getString(R.string.fab_photo), FloatingActionButton.SIZE_MINI, mFABClickListener);
        mfbFloatingActionButton.addButton(getResources().getDrawable(R.drawable.ic_fab_text), getString(R.string.fab_text), FloatingActionButton.SIZE_MINI, mFABClickListener);
        mfbFloatingActionButton.addButton(getResources().getDrawable(R.drawable.ic_fab_jump_top), getString(R.string.fab_jump_top), FloatingActionButton.SIZE_MINI, mFABClickListener);
        mfbFloatingActionButton.addButton(getResources().getDrawable(R.drawable.ic_share), getString(R.string.context_share_link), FloatingActionButton.SIZE_MINI, mFABClickListener);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        searchToolbar();

        final int defaultColor = mNavigationView.getItemTextColor().getDefaultColor();
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{android.R.attr.state_enabled}, new int[]{android.R.attr.state_pressed}, new int[]{android.R.attr.state_focused}, new int[]{android.R.attr.state_pressed}},
                new int[]{MFB.colorPrimary, defaultColor, defaultColor, defaultColor, defaultColor}
        );

        mNavigationView.setItemTextColor(colorStateList);
        mNavigationView.setItemIconTintList(colorStateList);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_most_recent);

        DBHelper = new DatabaseHelper(this);
        bookmarks = new ArrayList<>();

        cursor = DBHelper.getReadableDatabase().rawQuery("SELECT TITLE, URL FROM mfb_table", null);
        while (cursor.moveToNext()) {
            if (cursor.getString(0) != null && cursor.getString(1) != null) {
                bk = new ModelBookmarks(cursor.getString(0), cursor.getString(1));
                bookmarks.add(bk);
            }
        }

        BLAdapter = new AdapterBookmarks(this, bookmarks, DBHelper);
        BookmarksListView = findViewById(R.id.bookmarksListView);
        BookmarksListView.setAdapter(BLAdapter);

        BookmarksListView.setOnItemClickListener((adapter, view, position, arg) -> {
            mWebView.loadUrl(((ModelBookmarks) BookmarksListView.getAdapter().getItem(position)).getUrl());
            drawer.closeDrawers();
        });

        final MaterialButton newbookmark = findViewById(R.id.add_bookmark);
        newbookmark.setBackgroundColor(MFB.colorPrimary);
        newbookmark.setTextColor(MFB.textColor);
        newbookmark.getCompoundDrawablesRelative()[2].setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(MFB.textColor, BlendModeCompat.SRC_ATOP));
        newbookmark.setOnClickListener((View v) -> {
            bk = new ModelBookmarks(mWebView.getTitle(), mWebView.getUrl());
            DBHelper.addData(bk.getTitle(), bk.getUrl(), null);
            bookmarks.add(bk);
            BLAdapter.notifyDataSetChanged();
            CookingAToast.cooking(MainActivity.this, getString(R.string.new_bookmark) + " " + mWebView.getTitle(), Color.WHITE, Color.parseColor("#214594"), R.drawable.ic_bookmarks, false).show();
        });

        mr_badge = (TextView) mNavigationView.getMenu().findItem(R.id.nav_most_recent).getActionView();
        fr_badge = (TextView) mNavigationView.getMenu().findItem(R.id.nav_friendreq).getActionView();

        // Hide buttons if they are disabled
        mNavigationView.getMenu().findItem(R.id.nav_groups).setVisible(sharedPreferences.getBoolean("nav_groups", true));
        mNavigationView.getMenu().findItem(R.id.nav_search).setVisible(sharedPreferences.getBoolean("nav_search", true));
        mNavigationView.getMenu().findItem(R.id.nav_mainmenu).setVisible(sharedPreferences.getBoolean("nav_mainmenu", true));
        mNavigationView.getMenu().findItem(R.id.nav_most_recent).setVisible(sharedPreferences.getBoolean("nav_most_recent", true));
        mNavigationView.getMenu().findItem(R.id.nav_events).setVisible(sharedPreferences.getBoolean("nav_events", true));
        mNavigationView.getMenu().findItem(R.id.nav_photos).setVisible(sharedPreferences.getBoolean("nav_photos", true));
        mNavigationView.getMenu().findItem(R.id.nav_top_stories).setVisible(sharedPreferences.getBoolean("nav_top_stories", true));
        mNavigationView.getMenu().findItem(R.id.nav_friendreq).setVisible(sharedPreferences.getBoolean("nav_friendreq", true));


        mWebView.setOnScrollChangedCallback((WebView view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
            // Make sure the hiding is enabled and the scroll was significant
            if (Math.abs(oldScrollY - scrollY) > 4) {
                if (scrollY > oldScrollY) {
                    // User scrolled down, hide the button
                    mfbFloatingActionButton.hide();
                } else if (scrollY < oldScrollY) {
                    // User scrolled up, show the button
                    mfbFloatingActionButton.show();
                }
            }
        });

        hideHeaderPref = sharedPreferences.getBoolean("hide_menu_bar", true);

        mWebView.getSettings().setGeolocationEnabled(sharedPreferences.getBoolean("location_enabled", false));
        mWebView.getSettings().setMinimumFontSize(Integer.parseInt(sharedPreferences.getString("textScale", "1")));
        mWebView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");
        mWebView.addJavascriptInterface(this, "Vid");
        mWebView.getSettings().setBlockNetworkImage(sharedPreferences.getBoolean("stop_images", false));
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        if (Build.VERSION.SDK_INT >= 19)
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint("NewApi")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // clean an url from facebook redirection before processing (no more blank pages on back)
                url = Helpers.cleanAndDecodeUrl(url);

                if (url.contains("mailto:"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                if (Uri.parse(url).getHost().endsWith("facebook.com")
                        || Uri.parse(url).getHost().endsWith("*.facebook.com")
                        || Uri.parse(url).getHost().endsWith("akamaihd.net")
                        || Uri.parse(url).getHost().endsWith("ad.doubleclick.net")
                        || Uri.parse(url).getHost().endsWith("sync.liverail.com")
                        || Uri.parse(url).getHost().endsWith("cdn.fbsbx.com")
                        || Uri.parse(url).getHost().endsWith("lookaside.fbsbx.com")) {
                    return false;
                }

                if (url.contains("giphy") || url.contains("gifspace") || url.contains("tumblr") || url.contains("gph.is") || url.contains("gif") || url.contains("fbcdn.net") || url.contains("imgur")) {
                    if (url.contains("giphy") || url.contains("gph")) {
                        if (!url.endsWith(".gif")) {
                            if (url.contains("giphy.com") || url.contains("html5"))
                                url = String.format("https://media.giphy.com/media/%s/giphy.gif", url.replace("http://giphy.com/gifs/", ""));
                            else if (url.contains("gph.is") && !url.contains("html5")) {
                                view.loadUrl(url);
                                url = String.format("https://media.giphy.com/media/%s/giphy.gif", url.replace("http://giphy.com/gifs/", ""));
                            }

                            if (url.contains("media.giphy.com/media/") && !url.contains("html5")) {
                                String[] giphy = url.split("-");
                                String giphy_id = giphy[giphy.length - 1];
                                url = "https://media.giphy.com/media/" + giphy_id;
                            }
                            if (url.contains("media.giphy.com/media/http://media")) {
                                String[] gph = url.split("/");
                                String gph_id = gph[gph.length - 2];
                                url = "https://media.giphy.com/media/" + gph_id + "/giphy.gif";
                            }
                            if (url.contains("html5/giphy.gif")) {
                                String[] giphy_html5 = url.split("/");
                                String giphy_html5_id = giphy_html5[giphy_html5.length - 3];
                                url = "https://media.giphy.com/media/" + giphy_html5_id + "/giphy.gif";
                            }
                        }
                        if (url.contains("?")) {
                            String[] giphy1 = url.split("\\?");
                            String giphy_html5_id = giphy1[0];
                            url = giphy_html5_id + "/giphy.gif";
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

                    imageLoader(url);
                    return true;
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
            }

            private void getSrc(final String url, final String select, final String select2) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void[] params) {
                        try {
                            elements = Jsoup.connect(url).get().select(select).select(select2);
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
                if (url.contains("https://mbasic.facebook.com/home.php?s="))
                    view.loadUrl(baseURL);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                JavaScriptHelpers.videoView(view);

                if (url.contains("facebook.com/composer/mbasic/") || url.contains("https://m.facebook.com/sharer.php?sid="))
                    showHeader = true;

                if (loadCss) {
                    if (showHeader || !hideHeaderPref) {
                        css.append("#header { display: inherit; }");
                    } else
                        css.append("#header { display: none; }");

                    css.append(Helpers.cssThemeEngine(themeMode));
                    // Hide the status editor on the News Feed if setting is enabled
                    if (sharedPreferences.getBoolean("hide_editor_newsfeed", true))
                        css.append("#MComposer { display:none }");

                    // Hide 'Sponsored' content (ads)
                    if (sharedPreferences.getBoolean("hide_sponsored", true))
                        css.append("article[data-ft*=ei] { display:none }");

                    // Hide birthday content from News Feed
                    if (sharedPreferences.getBoolean("hide_birthdays", true))
                        css.append("article#u_1j_4 { display:none } article._55wm._5e4e._5fjt { display:none }");

                    if (sharedPreferences.getBoolean("comments_recently", true))
                        css.append("._5gh8 ._15ks:last-child, ._5gh8 ._15ks+._4u3j { display:none }");

                    // css.append("article#u_0_q._d2r{display:none}* { -webkit-tap-highlight-color:transparent; outline:0 }");

                    css.append("._i81:after { display: none; }");

                    JavaScriptHelpers.loadCSS(view, css.toString());

                    loadCss = false;
                }

                if (url.contains("/photo/view_full_size/?fbid="))
                    imageLoader(url.split("&ref_component")[0]);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeView.setRefreshing(false);

                css.delete(0, css.length());

                if (url.contains("lookaside") || url.contains("cdn.fbsbx.com")) {
                    Url = url;
                    RequestStoragePermission();
                }

                // Enable or disable FAB
                if (url.contains("messages") || !sharedPreferences.getBoolean("fab_enable", false))
                    mfbFloatingActionButton.setVisibility(View.GONE);
                else
                    mfbFloatingActionButton.setVisibility(View.VISIBLE);

                if (url.contains("https://mbasic.facebook.com/composer/?text=")) {
                    final UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
                    sanitizer.setAllowUnregisteredParamaters(true);
                    sanitizer.parseUrl(url);
                    final String param = sanitizer.getValue("text");
                    view.loadUrl("javascript:(function(){document.querySelector('#composerInput').innerHTML='" + param + "'})()");
                }

                if (url.contains("https://m.facebook.com/public/")) {
                    final String[] user = url.split("/");
                    final String profile = user[user.length - 1];
                    view.loadUrl("javascript:(function(){document.querySelector('input#u_0_0._5whq.input').value='" + profile + "'})()");
                    view.loadUrl("javascript:(function(){try{document.querySelector('button#u_0_1.btn.btnD.mfss.touchable').disabled = false}catch(_){}})()");
                    view.loadUrl("javascript:(function(){try{document.querySelector('button#u_0_1.btn.btnD.mfss.touchable').click()}catch(_){}})()");
                }

                if (sharedFromGallery != null)
                    view.loadUrl("javascript:(function(){try{document.getElementsByName('view_photo')[0].click()}catch(_){document.getElementsByClassName('bb bc')[0].click()}})()");

                if (showHeader)
                    showHeader = false;

                loadCss = true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && showAnimation && progress == 100) {
                    circleReveal();
                    showAnimation = false;
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                // Double check that we don't have any existing callbacks
                if (sharedFromGallery != null)
                    filePathCallback.onReceiveValue(new Uri[]{sharedFromGallery});

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
                    } else
                        takePictureIntent = null;
                }
                // Set up the intent to get an existing image
                final Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");

                // Set up the intents for the Intent chooser
                Intent[] intentArray;
                if (takePictureIntent != null)
                    intentArray = new Intent[]{takePictureIntent};
                else
                    intentArray = new Intent[0];

                if (sharedFromGallery == null) {
                    final Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, 1);
                }
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            // openFileChooser for Android 3.0+
            private void openFileChooser(ValueCallback uploadMsg, String acceptType) {

                // Update message
                if (sharedFromGallery != null)
                    uploadMsg.onReceiveValue(sharedFromGallery);
                else
                    mUploadMessage = uploadMsg;

                final File imageStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!imageStorageDir.exists())
                    imageStorageDir.mkdirs();

                // Create camera captured image file path and name
                final File file = new File(imageStorageDir + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");

                mCapturedImageURI = Uri.fromFile(file);

                // Camera capture image intent
                final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                final Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");

                if (sharedFromGallery == null) {
                    final Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
                    startActivityForResult(chooserIntent, 2888);
                }
            }

            private File createImageFile() throws IOException {
                // Create an image file name
                final File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                return File.createTempFile("IMG_" + System.currentTimeMillis(), ".jpg", storageDir);
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

        mWebView.setOnTouchListener((View v, MotionEvent event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && lastEvent == MotionEvent.ACTION_DOWN && mWebView.getHitTestResult().getType() == 5) {
                mWebView.requestFocusNodeHref(mHandler.obtainMessage());

                mHandler.sendMessage(mHandler.obtainMessage());
            }
            lastEvent = event.getAction();
            return v.performClick();
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        imageLoader(msg.getData().getString("src"));
        return true;
    }

    private void imageLoader(String url) {
        startActivity(new Intent(this, PhotoActivity.class).putExtra("link", url).putExtra("title", mWebView.getTitle()));
    }

    public void RequestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void URLs() {
        if (!sharedPreferences.getBoolean("save_data", false))
            baseURL = "https://m.facebook.com/";
        else
            baseURL = "https://mbasic.facebook.com/";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Url));
            final File downloads_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloads_dir.exists())
                if (!downloads_dir.mkdirs())
                    return;

            final File destinationFile = new File(downloads_dir, Uri.parse(Url).getLastPathSegment());
            request.setDestinationUri(Uri.fromFile(destinationFile));
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
            mWebView.goBack();
            CookingAToast.cooking(this, getString(R.string.downloaded), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_download, false).show();
        } else
            CookingAToast.cooking(this, getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent.getBooleanExtra("apply", false))
            recreate();

        URLs();
        UrlIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new UserInfo().execute(this);

        URLs();
        mWebView.onResume();
        mWebView.resumeTimers();

        if (Helpers.getCookie() != null && !sharedPreferences.getBoolean("save_data", false)) {
            badgeUpdate = new Handler();
            badgeTask = () -> {
                JavaScriptHelpers.updateNumsService(mWebView);
                badgeUpdate.postDelayed(badgeTask, 15000);
            };
            badgeTask.run();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        if (badgeTask != null && badgeUpdate != null)
            badgeUpdate.removeCallbacks(badgeTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!cursor.isClosed()) {
            DBHelper.close();
            cursor.close();
        }
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.removeAllViews();
        mWebView.destroy();
        if (badgeTask != null && badgeUpdate != null)
            badgeUpdate.removeCallbacks(badgeTask);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // Thanks to Koras for the tutorial. http://dev.indywidualni.org/2015/02/an-advanced-webview-with-some-cool-features
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= 21) {
            if (requestCode != 1 || mFilePathCallback == null)
                return;

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    final String dataString = data.getDataString();
                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else {
            if (requestCode == 2888) {
                if (null == this.mUploadMessage)
                    return;

                Uri result;
                if (resultCode != RESULT_OK)
                    result = null;
                else {
                    // retrieve from the private variable if the intent is null
                    result = data == null ? mCapturedImageURI : data.getData();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    private Point getPointOfView(View view) {
        final int[] location = new int[2];
        view.getLocationInWindow(location);
        return new Point(location[0], location[1]);
    }

    @SuppressWarnings("NewApi")
    private void circleReveal() {
        final int radius = (int) Math.hypot(mWebView.getWidth(), mWebView.getHeight());
        int x, y;
        if (circleRevealView != null) {
            Point point = getPointOfView(circleRevealView);
            x = point.x;
            y = point.y;
        } else {
            x = 0;
            y = mWebView.getHeight() / 2;
        }
        final Animator anim = ViewAnimationUtils.createCircularReveal(mWebView, x, y, 0, radius);
        anim.setDuration(300);
        anim.start();
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawers();
        if (searchToolbar.hasExpandedActionView())
            searchItem.collapseActionView();
        else if (mWebView.canGoBack())
            mWebView.goBack();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final View action_notif = menu.findItem(R.id.action_notifications).getActionView();
        final View action_msg = menu.findItem(R.id.action_messages).getActionView();

        notif_badge = action_notif.findViewById(R.id.badge_count);
        msg_badge = action_msg.findViewById(R.id.badge_count);

        final ImageView notif = action_notif.findViewById(R.id.badge_icon);
        setBackground(notif);
        notif.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications));
        notif.setColorFilter(MFB.textColor);
        notif.setOnClickListener((View v) -> {
            mWebView.setVisibility(View.INVISIBLE);
            showAnimation = true;
            circleRevealView = v;

            mWebView.stopLoading();
            mWebView.loadUrl(baseURL + "notifications.php");
            setTitle(R.string.nav_notifications);
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
            //NotificationsService.clearbyId(MainActivity.this, 12);
        });
        final ImageView msg = action_msg.findViewById(R.id.badge_icon);
        setBackground(msg);
        msg.setImageDrawable(getResources().getDrawable(R.drawable.ic_message));
        msg.setColorFilter(MFB.textColor);
        msg.setOnClickListener((View v) -> {
            mWebView.setVisibility(View.INVISIBLE);
            showAnimation = true;
            circleRevealView = v;

            mWebView.stopLoading();
            mWebView.loadUrl(baseURL + "messages/");
            setTitle(R.string.menu_messages);
            //NotificationsService.clearbyId(MainActivity.this, 969);
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawers();

        showAnimation = true;
        circleRevealView = null;
        mWebView.stopLoading();
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_top_stories:
                mWebView.loadUrl(baseURL + "home.php?sk=h_nor");
                setTitle(R.string.menu_top_stories);
                return true;
            case R.id.nav_most_recent:
                mWebView.setVisibility(View.INVISIBLE);

                mWebView.loadUrl(baseURL + "home.php?sk=h_chr");
                setTitle(R.string.menu_most_recent);
                Helpers.uncheckRadioMenu(mNavigationView.getMenu());
                return true;
            case R.id.nav_friendreq:
                mWebView.setVisibility(View.INVISIBLE);

                mWebView.loadUrl(baseURL + "friends/center/requests/");
                setTitle(R.string.menu_friendreq);
                return true;
            case R.id.nav_search:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.searchtoolbar, true);
                else
                    searchToolbar.setVisibility(View.VISIBLE);

                searchItem.expandActionView();
                return true;
            case R.id.nav_groups:
                loadCss = true;
                showHeader = true;

                mWebView.setVisibility(View.INVISIBLE);

                mWebView.loadUrl(baseURL + "groups/?category=membership");
                return true;
            case R.id.nav_mainmenu:
                loadCss = true;
                showHeader = true;
                mWebView.setVisibility(View.INVISIBLE);

                if (!sharedPreferences.getBoolean("save_data", false))
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23bookmarks_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'https%3A%2F%2Fm.facebook.com%2Fhome.php'%7D%7D)()");
                else
                    mWebView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/?ref_component=mbasic_home_header&ref_page=%2Fwap%2Fhome.php&refid=8");
                setTitle(R.string.menu_mainmenu);
                return true;
            case R.id.nav_events:
                mWebView.setVisibility(View.INVISIBLE);

                mWebView.loadUrl(baseURL + "events/");
                return true;
            case R.id.nav_photos:
                mWebView.setVisibility(View.INVISIBLE);

                mWebView.loadUrl(baseURL + "photos/");
                return true;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            default:
                return true;
        }
    }

    @JavascriptInterface
    @SuppressWarnings("unused")
    public void LoadVideo(final String video_url) {
        startActivity(new Intent(this, VideoActivity.class).putExtra("video_url", video_url));
    }

    public void setNotificationNum(int num) {
        txtFormat(notif_badge, num, Color.WHITE, false);
    }

    public void setMessagesNum(int num) {
        txtFormat(msg_badge, num, Color.WHITE, false);
    }

    public void setRequestsNum(int num) {
        txtFormat(fr_badge, num, Color.RED, true);
    }

    public void setMrNum(int num) {
        txtFormat(mr_badge, num, Color.RED, true);
    }

    private void txtFormat(TextView t, int i, int color, boolean bold) {
        t.setText(String.format("%s", i));
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        if (bold)
            t.setTypeface(null, Typeface.BOLD);
        if (i > 0)
            t.setVisibility(View.VISIBLE);
        else
            t.setVisibility(View.INVISIBLE);
    }

    private void UrlIntent(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (URLUtil.isValidUrl(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                try {
                    baseURL = "https://mbasic.facebook.com/composer/?text=" + URLEncoder.encode(intent.getStringExtra(Intent.EXTRA_TEXT), "utf-8");
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                }
            }
        }

        if (intent.getExtras() != null && intent.getExtras().containsKey("notifUrl")) {
            baseURL = intent.getExtras().getString("notifUrl");
        }

        if (intent.getDataString() != null) {
            baseURL = getIntent().getDataString();
            if (intent.getDataString().contains("profile"))
                baseURL = baseURL.replace("fb://profile/", "https://facebook.com/");
        }

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (intent.getType().startsWith("image/") || intent.getType().startsWith("video/") || intent.getType().startsWith("audio/")) {
                sharedFromGallery = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                // css.append("#MComposer{display:initial}");
                baseURL = "https://mbasic.facebook.com";
            }
        }

        mWebView.loadUrl(Helpers.cleanUrl(baseURL));
    }

    // Thanks to Jaison Fernando for the great tutorial.
    // http://droidmentor.com/searchview-animation-like-whatsapp/
    private void searchToolbar() {
        searchToolbar = findViewById(R.id.searchtoolbar);
        searchToolbar.inflateMenu(R.menu.menu_search);
        final Menu search_menu = searchToolbar.getMenu();

        searchItem = search_menu.findItem(R.id.action_filter_search);

        searchView = (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                mWebView.loadUrl(baseURL + "search/top/?q=" + query);
                searchItem.collapseActionView();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.searchtoolbar, false);
                else
                    searchToolbar.setVisibility(View.INVISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                return false;
            }

        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    circleReveal(R.id.searchtoolbar, false);
                } else
                    searchToolbar.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });
    }

    @SuppressWarnings("NewApi")
    private void circleReveal(int viewID, final boolean show) {
        final View v = findViewById(viewID);

        final int cy = v.getHeight() / 2;

        Animator anim;
        if (show)
            anim = ViewAnimationUtils.createCircularReveal(v, v.getWidth(), cy, 0, v.getWidth());
        else
            anim = ViewAnimationUtils.createCircularReveal(v, v.getWidth(), cy, v.getWidth(), 0);

        anim.setDuration(220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) {
                    super.onAnimationEnd(animation);
                    v.setVisibility(View.GONE);
                }
            }
        });

        // make the view visible and start the animation
        if (show)
            v.setVisibility(View.VISIBLE);

        anim.start();
    }

    private void setBackground(View btn) {
        final TypedValue typedValue = new TypedValue();
        int bg;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            bg = android.R.attr.selectableItemBackgroundBorderless;
        else
            bg = android.R.attr.selectableItemBackground;
        getTheme().resolveAttribute(bg, typedValue, true);
        btn.setBackgroundResource(typedValue.resourceId);
    }
}
