/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks. 
 * - Folio for Facebook by creativetrendsapps. Thanks.
 * - Toffed by JakeLane. Thanks.
 */
package me.zeeroooo.materialfb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;
import android.Manifest;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.GravityCompat;
import android.webkit.URLUtil;
import com.squareup.picasso.Target;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import im.delight.android.webview.AdvancedWebView;
import me.zeeroooo.materialfb.Notifications.NotificationsService;
import me.zeeroooo.materialfb.Ui.Theme;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String FACEBOOK_URL_BASE = "https://m.facebook.com/";
    private static final String FACEBOOK_URL_BASE_ENCODED = "https%3A%2F%2Fm.facebook.com%2F";
    private static final List<String> HOSTNAMES = Arrays.asList("facebook.com", "*.facebook.com", "*.fbcdn.net", "*.akamaihd.net");
    private final BadgeStyle BADGE_SIDE_FULL = new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_badge_full, R.color.colorAccent, R.color.colorAccent, Color.WHITE);

    // Members
    private static AppCompatActivity MaterialFBookAct;
    SwipeRefreshLayout swipeView;
    NavigationView mNavigationView;
    View mCoordinatorLayoutView;
    private FloatingActionMenu mMenuFAB;
    private AdvancedWebView mWebView;
    private final View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.textFAB:
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "%3Fpageload%3Dcomposer%22%7D%7D)()");
                    break;
                case R.id.photoFAB:
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "%3Fpageload%3Dcomposer_photo%22%7D%7D)()");
                    break;
                case R.id.checkinFAB:
                    mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "%3Fpageload%3Dcomposer_checkin%22%7D%7D)()");
                    break;
				case R.id.topFAB:
                    mWebView.scrollTo(0, 0);
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
    private Snackbar loginSnackbar = null;
    @SuppressWarnings("FieldCanBeLocal") // Will be garbage collected as a local variable
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private boolean requiresReload = false;
    private String mUserLink = null;
    private SharedPreferences mPreferences;


    @Override
    @SuppressLint({"setJavaScriptEnabled"})
    protected void onCreate(Bundle savedInstanceState) {
        MaterialFBookAct = this;
        boolean MFB = Theme.getInstance(this).setTheme().equals("MFB");
        final boolean Pink = Theme.getInstance(this).setTheme().equals("Pink");
        final boolean Grey = Theme.getInstance(this).setTheme().equals("Grey");
        final boolean Green = Theme.getInstance(this).setTheme().equals("Green");
        final boolean Red = Theme.getInstance(this).setTheme().equals("Red");
        final boolean Lime = Theme.getInstance(this).setTheme().equals("Lime");
        final boolean Yellow = Theme.getInstance(this).setTheme().equals("Yellow");
        final boolean Purple = Theme.getInstance(this).setTheme().equals("Purple");
        final boolean LightBlue = Theme.getInstance(this).setTheme().equals("LightBlue");
        final boolean Black = Theme.getInstance(this).setTheme().equals("Black");
        boolean mCreatingActivity = true;
        if (!mCreatingActivity) {
            if (MFB)
                setTheme(R.style.MFB);
        } else {
            if (Pink)
                setTheme(R.style.Pink);
            if (Grey)
                setTheme(R.style.Grey);
            if (Green)
                setTheme(R.style.Green);
            if (Red)
                setTheme(R.style.Red);
            if (Lime)
                setTheme(R.style.Lime);
            if (Yellow)
                setTheme(R.style.Yellow);
            if (Purple)
                setTheme(R.style.Purple);
            if (LightBlue)
                setTheme(R.style.LightBlue);
            if (Black)
                setTheme(R.style.Black);

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        Permiso.getInstance().setActivity(this);

        // Preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                switch (key) {
                    case SettingsActivity.KEY_PREF_STOP_IMAGES:
                        mWebView.getSettings().setBlockNetworkImage(prefs.getBoolean(key, false));
                        requiresReload = true;
                        break;
                    case SettingsActivity.KEY_PREF_MESSAGING:
                        mNavigationView.getMenu().findItem(R.id.nav_messages).setVisible(prefs.getBoolean(key, false));
                        break;
                    case SettingsActivity.KEY_PREF_LOCATION:
                        if (prefs.getBoolean(key, false)) {
                            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                                @Override
                                public void onPermissionResult(Permiso.ResultSet resultSet) {
                                    if (resultSet.areAllPermissionsGranted()) {
                                        mWebView.setGeolocationEnabled(true);
                                    } else {
                                        Snackbar.make(mCoordinatorLayoutView, R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                                    // TODO Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                                    callback.onRationaleProvided();
                                }
                            }, Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                        break;
                    case SettingsActivity.KEY_PREF_FAB_SCROLL:
                        mMenuFAB.showMenuButton(true);
                        break;
                    case SettingsActivity.KEY_PREF_HIDE_EDITOR:
                        requiresReload = true;
                        break;
                    case SettingsActivity.KEY_PREF_HIDE_SPONSORED:
                        requiresReload = true;
                        break;
                    case SettingsActivity.KEY_PREF_HIDE_BIRTHDAYS:
                        requiresReload = true;
                        break;
					case SettingsActivity.KEY_PREF_HIDE_MESSENGERDOWN:
						requiresReload = true;
						break;
                    default:
                        break;
                }
            }
        };
        mPreferences.registerOnSharedPreferenceChangeListener(listener);

        // start the service when it's activated but somehow it's not running
        // when it's already running nothing happens so it's ok
        if (mPreferences.getBoolean("notifications_activated", false) || mPreferences.getBoolean("message_notifications", false)) {
            final Intent intent = new Intent(MaterialFBook.getContextOfApplication(), NotificationsService.class);
            MaterialFBook.getContextOfApplication().startService(intent);
        }

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
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_messages).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_GROUPS, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_groups).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_SEARCH, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_search).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_MAINMENU, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_mainmenu).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_MOST_RECENT, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_most_recent).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_FBLOGOUT, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_fblogout).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_EVENTS, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_events).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_PHOTOS, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_photos).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_BACK, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_back).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_EXIT, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_exitapp).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_TOP_STORIES, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_top_stories).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_NEWS, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_news).setVisible(false);
        }
        if (!mPreferences.getBoolean(SettingsActivity.KEY_PREF_NAV_FRIENDREQ, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_friendreq).setVisible(false);
        }

        // Bind the Coordinator to member
        mCoordinatorLayoutView = findViewById(R.id.coordinatorLayout);

        // Start the Swipe to reload listener
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeView.setColorSchemeResources(R.color.colorPrimary);
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
                        // Show your View after 3 seconds
                        mMenuFAB.showMenu(true);
                    }
                }, 3000);
                return false;
            }
        });
        findViewById(R.id.textFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.photoFAB).setOnClickListener(mFABClickListener);
        findViewById(R.id.checkinFAB).setOnClickListener(mFABClickListener);
		findViewById(R.id.topFAB).setOnClickListener(mFABClickListener);

        // Load the WebView
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        assert mWebView != null;
        mWebView.addPermittedHostnames(HOSTNAMES);
        mWebView.setGeolocationEnabled(mPreferences.getBoolean(SettingsActivity.KEY_PREF_LOCATION, false));

        mWebView.setListener(this, new WebViewListener(this, mWebView));
        mWebView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");
        registerForContextMenu(mWebView);

        mWebView.getSettings().setBlockNetworkImage(mPreferences.getBoolean(SettingsActivity.KEY_PREF_STOP_IMAGES, false));
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.1; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.80 Mobile Safari/537.36");

        // Long press
        registerForContextMenu(mWebView);
        mWebView.setLongClickable(true);
        mWebView.setWebChromeClient(new CustomWebChromeClient(this, mWebView, (FrameLayout) findViewById(R.id.fullscreen_custom_content)));

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
                Snackbar.make(mCoordinatorLayoutView, R.string.error_login, Snackbar.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                checkLoggedInState();
            }
        };

        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
        LoginManager.getInstance().registerCallback(callbackManager, loginResult);

        if (checkLoggedInState()) {
            mWebView.loadUrl(chooseUrl());
            updateUserInfo();
        }
    }
}

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        Permiso.getInstance().setActivity(this);

        registerForContextMenu(mWebView);
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWebView.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mNotificationButton = menu.findItem(R.id.action_notifications);
        ActionItemBadge.update(this, mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
        mMessagesButton = menu.findItem(R.id.nav_messages);
        ActionItemBadge.update(this, mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_messages, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23notifications_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "notifications.php'%7D%7D)()");
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
        }
        if (id == R.id.nav_messages) {
            mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23messages_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "messages%2F'%7D%7D)()");
            Helpers.uncheckRadioMenu(mNavigationView.getMenu());
        }

        // Update the notifications
        JavaScriptHelpers.updateNums(mWebView);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_news:
                mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23feed_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "home.php'%7D%7D)()");
                item.setChecked(true);
            case R.id.nav_top_stories:
                mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('a%5Bhref*%3D%22%2Fhome.php%3Fsk%3Dh_nor%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "home.php%3Fsk%3Dh_nor%22%7D%7D)()");
                item.setChecked(true);
                break;
            case R.id.nav_most_recent:
                mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('a%5Bhref*%3D%22%2Fhome.php%3Fsk%3Dh_chr%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK_URL_BASE_ENCODED + "home.php%3Fsk%3Dh_chr%22%7D%7D)()");
                item.setChecked(true);
                break;
            case R.id.nav_friendreq:
                mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23requests_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "friends%2Fcenter%2Frequests%2F'%7D%7D)()");
                item.setChecked(true);
                break;
            case R.id.nav_messages:
                mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23messages_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "messages%2F'%7D%7D)()");
                JavaScriptHelpers.updateNums(mWebView);
                item.setChecked(true);
                break;
            case R.id.nav_search:
                mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23search_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "search%2F'%7D%7D)()");
                item.setChecked(true);
                break;
			case R.id.nav_groups:
                mWebView.loadUrl(FACEBOOK_URL_BASE + "groups/?category=membership");
                item.setChecked(true);
                break;
            case R.id.nav_mainmenu:
                mWebView.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('%23bookmarks_jewel%20%3E%20a').click()%7Dcatch(_)%7Bwindow.location.href%3D'" + FACEBOOK_URL_BASE_ENCODED + "home.php'%7D%7D)()");
                item.setChecked(true);
                break;
			case R.id.nav_events:
                mWebView.loadUrl(FACEBOOK_URL_BASE + "events");
                item.setChecked(true);
                break;
			case R.id.nav_photos:
                mWebView.loadUrl(FACEBOOK_URL_BASE + "profile.php?v=photos&soft=composer");
                item.setChecked(true);
                break;
			case R.id.nav_fblogout:
                LoginManager.getInstance().logOut();
                finish();
                return true;
            case R.id.nav_fblogin:
                LoginManager.getInstance().logInWithReadPermissions(this, Helpers.FB_PERMISSIONS);
                break;
            case R.id.nav_settings:
                Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsActivity);
                break;
            case R.id.nav_back:
                mWebView.goBack();
                break;
            case R.id.nav_exitapp:
                android.os.Process.killProcess(android.os.Process.myPid());
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
        if (loginSnackbar != null) {
            loginSnackbar.dismiss();
        }

        if (AccessToken.getCurrentAccessToken() != null && Helpers.getCookie() != null) {
            // Logged in, show webview
            mWebView.setVisibility(View.VISIBLE);

            // Hide login button
            mNavigationView.getMenu().findItem(R.id.nav_fblogin).setVisible(false);

            // Enable navigation buttons
            mNavigationView.getMenu().setGroupEnabled(R.id.group_fbnav, true);
            return true;
        } else {
            // Not logged in (possibly logged into Facebook OAuth and/or webapp)
            loginSnackbar = Helpers.loginPrompt(mCoordinatorLayoutView);
            setLoading(false);
            mWebView.setVisibility(View.GONE);

            // Show login button
            mNavigationView.getMenu().findItem(R.id.nav_fblogin).setVisible(true);

            // Disable navigation buttons
            mNavigationView.getMenu().setGroupEnabled(R.id.group_fbnav, false);
            return false;
        }
    }

    private void updateUserInfo() {
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
                    Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + userID + "/picture?type=large").into((ImageView) findViewById(R.id.profile_picture));

                    final View header = findViewById(R.id.header_layout);
                    Picasso.with(getApplicationContext()).load(object.getJSONObject("cover").getString("source")).resize(header.getWidth(), header.getHeight()).centerCrop().error(R.drawable.side_nav_bar).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            header.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {}

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {}
                    });
                } catch (NullPointerException e) {
                    Snackbar.make(mCoordinatorLayoutView, R.string.error_facebook_noconnection, Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(mCoordinatorLayoutView, R.string.error_facebook_error, Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(mCoordinatorLayoutView, R.string.error_super_wrong, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,cover,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void setNotificationNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications_active, null), num);
        } else {
            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications, null), Integer.MIN_VALUE);
        }
    }
    public void setMessagessNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_messages_active, null), num);
        } else {
            ActionItemBadge.update(mMessagesButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_messages, null), Integer.MIN_VALUE);
        }
    }

    public void setMessagesNum(int num) {
        // Only update message count if enabled
        if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false)) {
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
        } else if (Intent.ACTION_VIEW.equals(action) && intent.getData() != null && URLUtil.isValidUrl(intent.getData().toString())) {
            // If there is a intent containing a facebook link, go there
            return intent.getData().toString();
        }

        // If nothing has happened at this point, we want the default url
        return FACEBOOK_URL_BASE;
    }
}
