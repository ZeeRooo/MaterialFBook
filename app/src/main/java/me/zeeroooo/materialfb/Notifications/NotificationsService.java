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
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.webkit.CookieManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import java.io.IOException;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.CookingAToast;
import me.zeeroooo.materialfb.Ui.Theme;
import me.zeeroooo.materialfb.WebView.Helpers;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class NotificationsService extends IntentService {
    private SharedPreferences mPreferences;
    boolean syncProblemOccurred = false;
    private String messageImg, notifImg, baseURL;
    private Bitmap picprofile;

    public NotificationsService() {
        super("NotificationsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        URLs();
        if (mPreferences.getBoolean("facebook_messages", false))
            SyncMessages();
        if (mPreferences.getBoolean("facebook_notifications", false))
            SyncNotifications();
    }

    @Override
    public void onDestroy() {
        if (picprofile != null)
            picprofile.recycle();
    }

    private void URLs() {
        if (!mPreferences.getBoolean("save_data", false)) {
            baseURL = "https://m.facebook.com/";
        } else {
            baseURL = "https://mbasic.facebook.com/";
        }
    }

    // Sync the notifications
    private void SyncNotifications() {
        Element result = null;
        int tries = 0;
        Helpers.getCookie();
        while (tries++ < 3 && result == null) {
            Log.i("CheckNotificationsTask", "doInBackground: Processing... Trial: " + tries);
            Log.i("CheckNotificationsTask", "Trying: " + "https://m.facebook.com/notifications.php");
            final Element notification = getElementNotif("https://m.facebook.com/notifications.php");
            if (notification != null)
                result = notification;
        }
        try {
            if (result == null || result.text() == null)
                return;
            final String content = result.select("div.c").text();
            final String time = result.select("span.mfss.fcg").text();
            final String text = result.text().replace(time, "");
            final String pictureStyle = result.select("i.img.l.profpic").attr("style");

            if (!pictureStyle.isEmpty())
                splitUrl(pictureStyle, true);

            if (!mPreferences.getString("last_notification_text", "").equals(text)) {
                notifier(content, getString(R.string.app_name), text, baseURL + "notifications.php", false, notifImg);
            }

            // save as shown (or ignored) to avoid showing it again
            mPreferences.edit().putString("last_notification_text", text).apply();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    private Element getElementNotif(String connectUrl) {
        try {
            return Jsoup.connect(connectUrl).timeout(10000)
                    .cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).get()
                    .select("a.touchable").not("a._19no").not("a.button").not("a.touchable.primary").first();
        } catch (IllegalArgumentException ex) {
            Log.i("CheckNotificationsTask", "Cookie sync problem occurred");
            if (!syncProblemOccurred) {
                syncProblemAlert();
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
        Helpers.getCookie();
        while (tries++ < 3 && result == null) {
            Log.i("CheckNotificationsTask", "doInBackground: Processing... Trial: " + tries);
            Log.i("CheckNotificationsTask", "Trying: " + "https://m.facebook.com/messages?soft=messages");
            final Element message = getElementMes("https://m.facebook.com/messages?soft=messages");
            if (message != null)
                result = message;

            try {
                if (result == null)
                    return;
                final String name = result.select("div.title.thread-title.mfsl.fcb").text();
                final String content = result.select("div.oneLine.preview.mfss.fcg").text();
                final String time = result.select("div.time.r.nowrap.mfss.fcl").text();
                final String text = result.text().replace(time, "");
                final String pictureStyle = result.select(".img").attr("style");

                if (!pictureStyle.isEmpty())
                    splitUrl(pictureStyle, false);

                if (!mPreferences.getString("last_message", "").equals(text)) {
                    notifier(content, name, text, baseURL + result.attr("href"), true, messageImg);
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
            return Jsoup.connect(connectUrl).timeout(10000).cookie(("https://m.facebook.com/"), CookieManager.getInstance().getCookie(("https://m.facebook.com/"))).get().getElementsByClass("item messages-flyout-item aclb abt").select("a.touchable.primary").first();
        } catch (IllegalArgumentException ex) {
            Log.i("CheckNotificationsTask", "Cookie sync problem occurred");
            if (!syncProblemOccurred) {
                syncProblemAlert();
                syncProblemOccurred = true;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void syncProblemAlert() {
        CookingAToast.cooking(NotificationsService.this, getString(R.string.sync_problem), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
    }

    // create a notification and display it
    private void notifier(final String content, final String name, final String title, final String url, boolean isMessage, final String image_url) {

        try {
            picprofile = Glide.with(this).load(Helpers.decodeImg(image_url)).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().into(100, 100).get();
        } catch (Exception e) {/**/}

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setColor(Theme.getColor(NotificationsService.this))
                        .setContentTitle(name)
                        .setContentText(content)
                        .setTicker(title)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_material)
                        .setLargeIcon(Helpers.Circle(picprofile))
                        .setAutoCancel(true);

        // ringtone
        String ringtoneKey = "ringtone";
        if (isMessage)
            ringtoneKey = "ringtone_msg";

        Uri ringtoneUri = Uri.parse(mPreferences.getString(ringtoneKey, "content://settings/system/notification_sound"));
        mBuilder.setSound(ringtoneUri);

        // priority for Heads-up
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("url", url);
        mBuilder.setOngoing(false);
        mBuilder.setOnlyAlertOnce(true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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
            if (mPreferences.getBoolean("led_notif", false)) {
                mBuilder.setLights(Color.BLUE, 1000, 1000);
            }
            Notification note = mBuilder.build();
            mNotificationManager.notify(0, note);
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

    private String splitUrl(String url, boolean isNotif) {
        String[] pic = url.split("\"");
        if (!isNotif) {
            messageImg = pic[0];
        } else {
            notifImg = pic[0];
        }
        return null;
    }
}
