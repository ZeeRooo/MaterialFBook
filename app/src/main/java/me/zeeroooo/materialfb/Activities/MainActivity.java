/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks.
 * - Folio for Facebook by creativetrendsapps. Thanks.
 * - Toffed by JakeLane. Thanks.
 */
package me.zeeroooo.materialfb.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
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
import com.greysonparrelli.permiso.Permiso;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v4.view.GravityCompat;
import android.webkit.URLUtil;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import me.zeeroooo.materialfb.Notifications.NotificationsService;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.CookingAToast;
import me.zeeroooo.materialfb.Ui.Theme;
import me.zeeroooo.materialfb.WebView.Helpers;
import me.zeeroooo.materialfb.WebView.CustomWebChromeClient;
import me.zeeroooo.materialfb.WebView.JavaScriptHelpers;
import me.zeeroooo.materialfb.WebView.JavaScriptInterfaces;
import me.zeeroooo.materialfb.WebView.MFBWebView;
import me.zeeroooo.materialfb.WebView.MFBWebViewClient;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String FACEBOOK_URL_BASE = "https://m.facebook.com/";
    public static final String FACEBOOK_URL_BASE_BASIC = "https://mbasic.facebook.com/";
    private final String FACEBOOK_URL_BASE_ENCODED = "https%3A%2F%2Fm.facebook.com%2F";
    private final String FACEBOOK_URL_BASE_ENCODED_BASIC = "https%3A%2F%2Fmbasic.facebook.com%2F";
    private final BadgeStyle BADGE_SIDE_FULL = new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_badge_full, R.color.MFBPrimaryDark, R.color.MFBPrimaryDark, Color.WHITE);
    public static String UserAgent = "Mozilla/5.0 (BB10; Kbd) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.1.0.4633 Mobile Safari/537.10+";
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public ValueCallback<Uri[]> mFilePathCallback;
    public String mCameraPhotoPath;
    public Uri mCapturedImageURI = null;
    public ValueCallback<Uri> mUploadMessage;
    public static final int FILECHOOSER_RESULTCODE = 2888;

    // Members
    public SwipeRefreshLayout swipeView;
    public NavigationView mNavigationView;
    public FloatingActionMenu mMenuFAB;
    public MFBWebView mWebView;
    private final View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textFAB:
                    if (!mPreferences.getBoolean("save_data", false)) {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "%3Fpageload%3Dcomposer%22%7D%7D)()");
                    } else {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED_BASIC + "%3Fpageload%3Dcomposer%22%7D%7D)()");
                    }
                    break;
                case R.id.photoFAB:
                    if (!mPreferences.getBoolean("save_data", false)) {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "%3Fpageload%3Dcomposer_photo%22%7D%7D)()");
                    } else {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED_BASIC + "%3Fpageload%3Dcomposer_photo%22%7D%7D)()");
                    }
                    break;
                case R.id.checkinFAB:
                    if (!mPreferences.getBoolean("save_data", false)) {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "%3Fpageload%3Dcomposer_checkin%22%7D%7D)()");
                    } else {
                        mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED_BASIC + "%3Fpageload%3Dcomposer_checkin%22%7D%7D)()");
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

    private MenuItem mNotificationButton;
    private MenuItem mMessagesButton;
    private CallbackManager callbackManager;
    @SuppressWarnings("FieldCanBeLocal") // Will be garbage collected as a local variable
    private String mUserLink = null;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.getTheme(this);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);
        Permiso.getInstance().setActivity(this);

        // Preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
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
        assert mWebView != null;
        mWebView.getSettings().setGeolocationEnabled(mPreferences.getBoolean("location_enabled", false));
        mWebView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");
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
        mWebView.getSettings().setUserAgentString(UserAgent);
        mWebView.setWebViewClient(new MFBWebViewClient(MainActivity.this, mWebView));

        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);

        // Long press
        mWebView.setWebChromeClient(new CustomWebChromeClient(this, mWebView));

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
                mWebView.loadUrl(chooseUrl());
                updateUserInfo();
            }

            @Override
            public void onCancel() {
                checkLoggedInState();
            }

            @Override
            public void onError(FacebookException error) {
                CookingAToast.cooking(MainActivity.this, R.string.error_login, Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                LoginManager.getInstance().logOut();
                checkLoggedInState();
            }
        };

        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
        LoginManager.getInstance().registerCallback(callbackManager, loginResult);

        if (checkLoggedInState()) {
            mWebView.loadUrl(chooseUrl());
        }
        chooseUrl();
    }

    @Override
    protected void onResume() {
        mWebView.onResume();
        super.onResume();
        Runtime.getRuntime().gc();
        Permiso.getInstance().setActivity(this);
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.removeAllViews();
        mWebView.destroy();
        super.onDestroy();
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

                    Uri result=null;

                    try{
                        if (resultCode != RESULT_OK) {
                            result = null;
                        } else {
                            // retrieve from the private variable if the intent is null
                            result = data == null ? mCapturedImageURI : data.getData();
                        }
                    }
                    catch(Exception e) {
                       // Toast.makeText(getApplicationContext(), "activity :"+e, Toast.LENGTH_LONG).show();
                    }

                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPreferences.getBoolean("start_messages", false)) {
            if (!mPreferences.getBoolean("save_data", false)) {
                mWebView.loadUrl(FACEBOOK_URL_BASE + "messages/");
            } else {
                mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC + "messages/");
            }
        }
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

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
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
            if (!mPreferences.getBoolean("save_data", false)) {
                mWebView.loadUrl(FACEBOOK_URL_BASE + "notifications.php");
            } else {
                mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC + "notifications.php");
            }
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
            NotificationsService.ClearNotif(this);
        }
        if (id == R.id.nav_messages) {
            if (!mPreferences.getBoolean("save_data", false)) {
                mWebView.loadUrl(FACEBOOK_URL_BASE + "messages/");
            } else {
                mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC + "messages/");
            }
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_top_stories:
                MainActivity.this.setTitle(R.string.menu_top_stories);
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('a%5Bhref*%3D%22%2Fhome.php%3Fsk%3Dh_nor%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "home.php%3Fsk%3Dh_nor%22%7D%7D)()");
                } else {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('a%5Bhref*%3D%22%2Fhome.php%3Fsk%3Dh_nor%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED_BASIC + "home.php%3Fsk%3Dh_nor%22%7D%7D)()");
                }
                item.setChecked(true);
                break;
            case R.id.nav_most_recent:
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl(FACEBOOK_URL_BASE);
                } else {
                    mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC);
                }
                item.setChecked(true);
                break;
            case R.id.nav_friendreq:
                MainActivity.this.setTitle(R.string.menu_friendreq);
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23requests_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "friends%2Fcenter%2Frequests%2F'%7D%7D)()");
                } else {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23requests_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED_BASIC + "friends%2Fcenter%2Frequests%2F'%7D%7D)()");
                }
                item.setChecked(true);
                break;
            case R.id.nav_messages:
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl(FACEBOOK_URL_BASE + "messages/");
                } else {
                    mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC + "messages/");
                }
                NotificationsService.ClearMessages(this);
                Helpers.uncheckRadioMenu(mNavigationView.getMenu());
                break;
            case R.id.nav_search:
                MainActivity.this.setTitle(R.string.menu_search);
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23search_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "search%2F'%7D%7D)()");
                } else {
                    mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC + "/search/");
                }
                item.setChecked(true);
                break;
            case R.id.nav_groups:
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl(FACEBOOK_URL_BASE + "groups/?category=membership");
                } else {
                    mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC + "groups/?category=membership");
                }
                item.setChecked(true);
                break;
            case R.id.nav_mainmenu:
                MainActivity.this.setTitle(R.string.menu_mainmenu);
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23bookmarks_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "home.php'%7D%7D)()");
                } else {
                    mWebView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/?ref_component=mbasic_home_header&ref_page=%2Fwap%2Fhome.php&refid=8");
                }
                item.setChecked(true);
                break;
            case R.id.nav_events:
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl(FACEBOOK_URL_BASE + "events");
                } else {
                    mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC + "events");
                }
                item.setChecked(true);
                break;
            case R.id.nav_photos:
                if (!mPreferences.getBoolean("save_data", false)) {
                    mWebView.loadUrl(FACEBOOK_URL_BASE + "photos/");
                } else {
                    mWebView.loadUrl(FACEBOOK_URL_BASE_BASIC + "photos/");
                }
                item.setChecked(true);
                break;
            case R.id.nav_fblogout:
                LoginManager.getInstance().logOut();
                mWebView.reload();
                break;
            case R.id.nav_settings:
                Intent settingsActivity = new Intent(getApplication(), SettingsActivity.class);
                startActivity(settingsActivity);
                break;
            case R.id.nav_back:
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

    private void updateUserInfo() {
        if (!mPreferences.getBoolean("save_data", false)) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    // Update header
                    try {
                        String userID = object.getString("id");
                        mUserLink = object.getString("link");

                        // Set the user's name under the header
                        ((TextView) findViewById(R.id.profile_name)).setText(object.getString("name"));

                        // Set the cover photo with resizing
                        Glide.with(getApplication()).load("https://graph.facebook.com/" + userID + "/picture?type=large").diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) findViewById(R.id.profile_picture));
                        final View header = findViewById(R.id.header_layout);

                        Glide.with(getApplication()).load(object.getJSONObject("cover").getString("source")).diskCacheStrategy(DiskCacheStrategy.SOURCE).into((ImageView) findViewById(R.id.cover));

                    } catch (NullPointerException e) {
                        CookingAToast.cooking(MainActivity.this, R.string.error_facebook_noconnection, Color.WHITE, Color.parseColor("#ffbb33"), R.drawable.ic_warning, false).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CookingAToast.cooking(MainActivity.this, R.string.error_facebook_error, Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        CookingAToast.cooking(MainActivity.this, R.string.error_super_wrong, Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
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
    public void setMessagessNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_message, null), num);
        } else {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_message, null), Integer.MIN_VALUE);
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
    }
    public void setRequestsNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_friendreq), num);
        } else {
            // Hide the badge and show the washed-out button
            ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_friendreq), Integer.MIN_VALUE);
        }
    }

    private String chooseUrl() {
        // Handle intents
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (URLUtil.isValidUrl(intent.getStringExtra(Intent.EXTRA_TEXT))) {
                try {
                    return "https://mbasic.facebook.com/composer/?text=" + URLEncoder.encode(intent.getStringExtra(Intent.EXTRA_TEXT), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!mPreferences.getBoolean("save_data", false)) {
            return FACEBOOK_URL_BASE;
        } else {
            return FACEBOOK_URL_BASE_BASIC;
        }
    }
}
