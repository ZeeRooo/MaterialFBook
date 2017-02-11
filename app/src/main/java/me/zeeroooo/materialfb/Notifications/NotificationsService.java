/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks.
 * - Folio for Facebook by creativetrendsapps. Thanks.
 */
package me.zeeroooo.materialfb.Notifications;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import android.os.Looper;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.MaterialFBook;
import me.zeeroooo.materialfb.R;
import android.util.Log;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.greysonparrelli.permiso.Permiso;

public class NotificationsService extends IntentService {

    public NotificationsService() {
        super("NotificationsService");
    }

    // Facebook URL constants
    final String NOTIFICATIONS_URL = "https://m.facebook.com/notifications/";
    final String MESSAGES_URL = "https://m.facebook.com/messages/";
    private static final String MESSAGES_URL_BACKUP = "https://mobile.facebook.com/messages";
    private static final String NOTIFICATION_OLD_MESSAGE_URL = "https://m.facebook.com/messages#";
    private final int MAX_RETRY = 3;
    private final int JSOUP_TIMEOUT = 10000;
    private static final String TAG;
    private SharedPreferences mPreferences;
    boolean syncProblemOccurred = false;
    public View mCoordinatorLayoutView;


    // static initializer
    static {
        TAG = NotificationsService.class.getSimpleName();
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "********** Service created! **********");
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: Service stopping...");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SyncNotifications();
        SyncMessages();
    }

    // Sync the messages
    private void SyncMessages() {
        String result = null;
        int tries = 0;
        syncCookies();
        while (tries++ < MAX_RETRY && result == null) {
            Log.i("CheckMessagesTask", "doInBackground: Processing... Trial: " + tries);

            // try to generate rss feed address
            Log.i("CheckMsgTask:getNumber", "Trying: " + MESSAGES_URL);
            String number = getNumberMessages(MESSAGES_URL);
            if (!number.matches("^[+-]?\\d+$")) {
                Log.i("CheckMsgTask:getNumber", "Trying: " + MESSAGES_URL_BACKUP);
                number = getNumberMessages(MESSAGES_URL_BACKUP);
            }
            if (number.matches("^[+-]?\\d+$"))
                result = number;
        }
        int newMessages = Integer.parseInt(result);

        if (newMessages == 1)
            notifier(getString(R.string.you_have_one_message), NOTIFICATION_OLD_MESSAGE_URL, true);
        else if (newMessages > 1)
            notifier(String.format(getString(R.string.you_have_n_messages), newMessages), NOTIFICATION_OLD_MESSAGE_URL, true);

        // save this check status
        mPreferences.edit().putBoolean("msg_last_status", true).apply();
        Log.i("CheckMessagesTask", "onPostExecute: Aight biatch ;)");
    }

    private String getNumberMessages(String connectUrl) {
        try {
            Elements message = Jsoup.connect(connectUrl).userAgent(MainActivity.UserAgent).timeout(JSOUP_TIMEOUT)
                    .cookie((MainActivity.FACEBOOK_URL_BASE), CookieManager.getInstance().getCookie((MainActivity.FACEBOOK_URL_BASE))).get()
                    .select("div#viewport").select("div#page").select("div._129-")
                    .select("#messages_jewel").select("span._59tg");

            return message.html();
        } catch (IllegalArgumentException ex) {
            Log.i("CheckMessagesTask", "Cookie sync problem occurred");
            if (!syncProblemOccurred) {
                syncProblemSnackbar();
                syncProblemOccurred = true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "failure";
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
        }
        // Handle the notification
        if (result == null)
            return;
        if (result.text() == null)
            return;

        String time = result.select("span.mfss.fcg").text();
        String text = result.text().replace(time, "");

        if (!mPreferences.getString("last_notification_text", "").equals(text))
            notifier(text, (MainActivity.FACEBOOK_URL_BASE) + result.attr("href"), false);
        mPreferences.edit().putString("last_notification_text", text).apply();
    }

    private Element getElementNotif(String connectUrl) {
        try {
            return Jsoup.connect(connectUrl).userAgent(MainActivity.UserAgent).timeout(JSOUP_TIMEOUT)
                    .cookie((MainActivity.FACEBOOK_URL_BASE), CookieManager.getInstance().getCookie((MainActivity.FACEBOOK_URL_BASE))).get()
                    .select("a.touchable").not("a._19no").not("a.button").first();
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
    private void notifier(String title, String url, boolean isMessage) {
        // let's display a notification, dude!
        final String contentTitle;
        contentTitle = getString(R.string.app_name);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setColor(ContextCompat.getColor(this, R.color.MFBPrimary))
                        .setContentTitle(contentTitle)
                        .setContentText(title)
                        .setTicker(title)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true);

        // ringtone
        String ringtoneKey = "ringtone";
        if (isMessage)
            ringtoneKey = "ringtone_msg";

        Uri ringtoneUri = Uri.parse(mPreferences.getString(ringtoneKey, "content://settings/system/notification_sound"));
        mBuilder.setSound(ringtoneUri);

        // vibration
        if (mPreferences.getBoolean("vibrate", false)) {
            mBuilder.setVibrate(new long[]{500, 500});
            if (mPreferences.getBoolean("vibrate_double", false)) {
                mBuilder.setVibrate(new long[]{500, 500, 500, 500});
            }
        } else {
            mBuilder.setVibrate(new long[]{0L});
        }

        // LED light
        if (mPreferences.getBoolean("led_light", false)) {
            Resources resources = getResources(), systemResources = Resources.getSystem();
            mBuilder.setLights(Color.CYAN,
                    resources.getInteger(systemResources.getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                    resources.getInteger(systemResources.getIdentifier("config_defaultNotificationLedOff", "integer", "android")));
        }

        // Flashlight
        if (mPreferences.getBoolean("flashlight_as_led", false)) {
            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                @Override
                public void onPermissionResult(Permiso.ResultSet resultSet) {
                    if (resultSet.areAllPermissionsGranted()) {
                        Camera cam = Camera.open();
                        Parameters p = cam.getParameters();
                        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        cam.setParameters(p);
                        cam.startPreview();
                    } else {
                        Snackbar.make(MainActivity.mCoordinatorLayoutView, R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                    Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                }
            }, Manifest.permission.CAMERA);
        }

        // priority for Heads-up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mBuilder.setPriority(Notification.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (isMessage) {
            // Messages
            Intent viewMessagesIntent = new Intent(this, MainActivity.class);
            viewMessagesIntent.setAction(Intent.ACTION_VIEW);
            viewMessagesIntent.setData(Uri.parse(url));
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplication(), 1, viewMessagesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setOngoing(false);
            mBuilder.setSmallIcon(R.drawable.ic_message);
            mBuilder.setOnlyAlertOnce(true);
            Notification note = mBuilder.build();
            mNotificationManager.notify(1, note);
        } else {
            // Notif
            Intent viewNotificationsIntent = new Intent(this, MainActivity.class);
            viewNotificationsIntent.setAction(Intent.ACTION_VIEW);
            viewNotificationsIntent.setData(Uri.parse(url));
            mBuilder.setSmallIcon(R.drawable.ic_menu_notifications_active_png);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(viewNotificationsIntent);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplication(), 0, viewNotificationsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setOngoing(false);
            Notification note = mBuilder.build();
            mNotificationManager.notify(0, note);

            // LED light flag
            if (mPreferences.getBoolean("led_light", false))
                note.flags |= Notification.FLAG_SHOW_LIGHTS;
        }
    }
    public static void ClearMessages() {
        NotificationManager notificationManager = (NotificationManager)
                MaterialFBook.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    public static void ClearNotif() {
        NotificationManager notificationManager = (NotificationManager)
                MaterialFBook.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
}