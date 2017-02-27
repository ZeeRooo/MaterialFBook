/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks.
 * - Folio for Facebook by creativetrendsapps. Thanks.
 */
package me.zeeroooo.materialfb.Notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import java.io.IOException;
import android.os.Looper;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.Theme;
import me.zeeroooo.materialfb.WebView.Helpers;
import android.util.Log;

public class NotificationsService extends IntentService {

    public NotificationsService() {
        super("NotificationsService");
    }

    // Facebook URL constants
    final String NOTIFICATIONS_URL = "https://m.facebook.com/notifications.php";
    final String MESSAGES_URL = "https://m.facebook.com/messages?soft=messages";
    private final int MAX_RETRY = 3;
    private final int JSOUP_TIMEOUT = 10000;
    private SharedPreferences mPreferences;
    boolean syncProblemOccurred = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Theme.getTheme(this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mPreferences.getBoolean("facebook_messages", false))
            SyncMessages();
        if (mPreferences.getBoolean("facebook_notifications", false))
            SyncNotifications();
    }

    // Sync the notifications
    private void SyncNotifications() {
        Element result = null;
        int tries = 0;
        syncCookies();
        while (tries++ < MAX_RETRY && result == null) {
            Log.i("CheckNotificationsTask", "doInBackground: Processing... Trial: " + tries);
            Log.i("CheckNotificationsTask", "Trying: " + NOTIFICATIONS_URL);
            Element notification = getElementNotif(NOTIFICATIONS_URL);
            if (notification != null)
                result = notification;

            try {
                if (result == null)
                    return;
                if (result.text() == null)
                    return;
                final String content = result.select("div.c").text();
                final String time = result.select("span.mfss.fcg").text();
                final String text = result.text().replace(time, "");
                final String pictureStyle = result.select("i.img.l.profpic").attr("style");

                if (!mPreferences.getString("last_notification_text", "").equals(text)) {
                    Bitmap picprofile = Helpers.getBitmapFromURL(Helpers.extractUrl(pictureStyle));
                    notifier(content, getString(R.string.app_name), text, "https://m.facebook.com/" + result.attr("href"), false, picprofile);
                }

                // save as shown (or ignored) to avoid showing it again
                mPreferences.edit().putString("last_notification_text", text).apply();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Nullable
    private Element getElementNotif(String connectUrl) {
        try {
            return Jsoup.connect(connectUrl).userAgent(MainActivity.UserAgent).timeout(JSOUP_TIMEOUT)
                    .cookie(("https://mobile.facebook.com"), CookieManager.getInstance().getCookie(("https://mobile.facebook.com"))).get()
                    .select("a.touchable").not("a._19no").not("a.button").not("a.touchable.primary").first();
        } catch (IllegalArgumentException ex) {
            Log.i("CheckNotificationsTask", "Cookie sync problem occurred");
            if (!syncProblemOccurred) {
                syncProblemSnackbar();
                syncProblemOccurred = true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Sync the messages
    private void SyncMessages() {
        Element result = null;
        int tries = 0;
        syncCookies();
        while (tries++ < MAX_RETRY && result == null) {
            Log.i("CheckNotificationsTask", "doInBackground: Processing... Trial: " + tries);
            Log.i("CheckNotificationsTask", "Trying: " + MESSAGES_URL);
            Element message = getElementMes(MESSAGES_URL);
            if (message != null)
                result = message;

            try {
                if (result == null)
                    return;
                final String name = result.select("div.title.thread-title.mfsl.fcb").text();
                final String content = result.select("div.oneLine.preview.mfss.fcg").text();
                final String time = result.select("div.time.r.nowrap.mfss.fcl").text();
                final String text = result.text().replace(time, "");
                final String pictureStyle = result.select("i.img.profpic").attr("style");

                if (!mPreferences.getString("last_message", "").equals(text)) {
                    Bitmap picprofile = Helpers.getBitmapFromURL(Helpers.extractUrl(pictureStyle));
                    notifier(content, name, text, "https://m.facebook.com/" + result.attr("href"), true, picprofile);
                }

                // save as shown (or ignored) to avoid showing it again
                mPreferences.edit().putString("last_message", text).apply();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Nullable
    private Element getElementMes(String connectUrl) {
        try {
            return Jsoup.connect(connectUrl).userAgent(MainActivity.UserAgent).timeout(JSOUP_TIMEOUT)
                    .cookie(("https://m.facebook.com/"), CookieManager.getInstance().getCookie(("https://m.facebook.com/"))).get()
                    .select("a.touchable.primary").first();
        } catch (IllegalArgumentException ex) {
            Log.i("CheckNotificationsTask", "Cookie sync problem occurred");
            if (!syncProblemOccurred) {
                syncProblemSnackbar();
                syncProblemOccurred = true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * CookieSyncManager was deprecated in API level 21.
     * We need it for API level lower than 21 though.
     * In API level >= 21 it's done automatically.
     */
    @SuppressWarnings("deprecation")
    private void syncCookies() {
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(getApplication());
            CookieSyncManager.getInstance().sync();
        }
    }

    // show a Sync Problem Snackbar while not being on UI Thread
    public void syncProblemSnackbar() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(MainActivity.mCoordinatorLayoutView, R.string.sync_problem, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // create a notification and display it
    private void notifier(String content, String name, String title, String url, boolean isMessage, Bitmap picprofile) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setColor(Theme.getColor(NotificationsService.this))
                        .setContentTitle(name)
                        .setContentText(content)
                        .setTicker(title)
                        .setWhen(System.currentTimeMillis())
                        .setLargeIcon(Helpers.Circle(picprofile))
                        .setSmallIcon(R.drawable.ic_material)
                        .setAutoCancel(true);

        // ringtone
        String ringtoneKey = "ringtone";
        if (isMessage)
            ringtoneKey = "ringtone_msg";

        Uri ringtoneUri = Uri.parse(mPreferences.getString(ringtoneKey, "content://settings/system/notification_sound"));
        mBuilder.setSound(ringtoneUri);

       /* // LED light
        if (mPreferences.getBoolean("led_light", false)) {
            Resources resources = getResources(), systemResources = Resources.getSystem();
            mBuilder.setLights(Color.CYAN,
                    resources.getInteger(systemResources.getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                    resources.getInteger(systemResources.getIdentifier("config_defaultNotificationLedOff", "integer", "android")));
        }*/

        // priority for Heads-up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mBuilder.setOngoing(false);
        mBuilder.setOnlyAlertOnce(true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (isMessage) {
            // vibration
            if (mPreferences.getBoolean("vibrate_msg", false)) {
                mBuilder.setVibrate(new long[]{500, 500});
                if (mPreferences.getBoolean("vibrate_double_msg", false)) {
                    mBuilder.setVibrate(new long[]{500, 500, 500, 500});
                }
            } else {
                mBuilder.setVibrate(new long[]{0L});
            }
           /* if (mPreferences.getBoolean("flashlight_as_led_msg", false)) {
                flashlight();
            }*/
            if (mPreferences.getBoolean("led_msj", false)) {
                mBuilder.setLights(Color.BLUE, 1000, 1000);
            }
            Notification note = mBuilder.build();
            mNotificationManager.notify(1, note);
        } else {
            if (mPreferences.getBoolean("vibrate_notif", false)) {
                mBuilder.setVibrate(new long[]{500, 500});
                if (mPreferences.getBoolean("vibrate_double_notif", false)) {
                    mBuilder.setVibrate(new long[]{500, 500, 500, 500});
                }
            } else {
                mBuilder.setVibrate(new long[]{0L});
            }
          /*  if (mPreferences.getBoolean("flashlight_as_led_notif", false)) {
                flashlight();
            }*/
            if (mPreferences.getBoolean("led_notif", false)) {
                mBuilder.setLights(Color.BLUE, 1000, 1000);
            }
            Notification note = mBuilder.build();
            mNotificationManager.notify(0, note);

            /*// LED light flag
            if (mPreferences.getBoolean("led_light", false))
                note.flags |= Notification.FLAG_SHOW_LIGHTS;*/
        }
    }
    public static void ClearMessages(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);
    }

    public static void ClearNotif(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }
  /*  // Flashlight
    private void flashlight() {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    Camera cam = Camera.open();
                    Parameters p = cam.getParameters();
                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    cam.setParameters(p);
                    cam.startPreview();
                    cam.stopPreview();
                    cam.release();
                } else {
                    Snackbar.make(MainActivity.mCoordinatorLayoutView, R.string.permission_denied, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
            }
        }, Manifest.permission.CAMERA);
    }*/
}