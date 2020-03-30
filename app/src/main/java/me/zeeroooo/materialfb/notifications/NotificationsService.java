package me.zeeroooo.materialfb.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.activities.MainActivity;
import me.zeeroooo.materialfb.misc.DatabaseHelper;
import me.zeeroooo.materialfb.webview.Helpers;

public class NotificationsService extends Worker {
    private SharedPreferences mPreferences;
    private boolean msg_notAWhiteList = false, notif_notAWhiteList = false;
    private String baseURL, channelId;
    private List<String> blist;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public NotificationsService(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    private void URLs() {
        if (!mPreferences.getBoolean("save_data", false))
            baseURL = "https://m.facebook.com/";
        else
            baseURL = "https://mbasic.facebook.com/";
    }

    @NonNull
    @Override
    public Result doWork() {
        final DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        final Cursor cursor = db.getReadableDatabase().rawQuery("SELECT BL FROM mfb_table", null);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        URLs();
        blist = new ArrayList<>();

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        try {
            while (cursor != null && cursor.moveToNext())
                if (cursor.getString(0) != null)
                    blist.add(cursor.getString(0));

            if (mPreferences.getBoolean("facebook_messages", false))
                SyncMessages();
            if (mPreferences.getBoolean("facebook_notifications", false))
                SyncNotifications();

            if (!cursor.isClosed()) {
                db.close();
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Worker.Result.failure();
        }
        return Worker.Result.success();
    }

    // Sync the notifications
    private void SyncNotifications() throws Exception {
        final Document doc = Jsoup.connect("https://m.facebook.com/notifications.php").cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).timeout(300000).get();
        final Elements notifications = doc.select("div.aclb > div.touchable-notification > a.touchable");
        String time, content, pictureNotif;
        final StringBuilder stringBuilder = new StringBuilder();
        int previousNotifLength;

        for (byte a = 0; a < notifications.size(); a++) {
            time = notifications.get(a).select("span.mfss.fcg").text();
            content = notifications.get(a).select("div.c").text().replace(time, "");
            previousNotifLength = stringBuilder.length();

            if (!blist.isEmpty())
                for (int position = 0; position < blist.size(); position++) {
                    if (content.toLowerCase().contains(blist.get(position).toLowerCase()))
                        notif_notAWhiteList = true;
                }

            if (!notif_notAWhiteList) {
                pictureNotif = notifications.get(a).select("div.ib > i").attr("style");

                stringBuilder.append(content.replace(time, ""));

                if (!mPreferences.getString("last_notification_text", "").contains(stringBuilder.substring(previousNotifLength)))
                    notifier(stringBuilder.substring(previousNotifLength), getApplicationContext().getString(R.string.app_name), baseURL + "notifications.php", pictureNotif.split("('*')")[1], (int) System.currentTimeMillis(), false);
            }
        }

        mPreferences.edit().putString("last_notification_text", stringBuilder.toString()).apply();
    }

    private void SyncMessages() throws Exception {
        final Document doc = Jsoup.connect("https://mbasic.facebook.com/messages").cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).timeout(300000).get();

        final Elements results = doc.getElementsByClass("bo bp bq br bs bt bu bv bw");

        if (results != null) {
            String name, pictureMsg;
            final StringBuilder stringBuilder = new StringBuilder();
            int previousMsgLength;

            for (byte a = 0; a < results.size(); a++) {
                previousMsgLength = stringBuilder.length();

                stringBuilder.append(results.get(a).selectFirst("tbody > tr > td > div > h3.cd.ba.ce > span").text());

                if (!blist.isEmpty())
                    for (int position = 0; position < blist.size(); position++) {
                        if (stringBuilder.toString().toLowerCase().contains(blist.get(position).toLowerCase()))
                            msg_notAWhiteList = true;
                    }

                if (!msg_notAWhiteList) {
                    name = results.get(a).selectFirst("tbody > tr > td > div > h3.bz.ca.cb > a").text();
                    pictureMsg = "https://graph.facebook.com/" + results.get(a).select("tbody > tr > td > div > h3.bz.ca.cb > a").attr("href").split("%3A")[1].split("&")[0] + "/picture?type=large";

                    if (!mPreferences.getString("last_message", "").contains(stringBuilder.substring(previousMsgLength)))
                        notifier(stringBuilder.substring(previousMsgLength), name, baseURL + "messages", pictureMsg, (int) System.currentTimeMillis(), true);
                }

            }

            mPreferences.edit().putString("last_message", stringBuilder.toString()).apply();
        }
    }

    // create a notification and display it
    private void notifier(final String content, final String title, final String url, final String image_url, int id, boolean isMessage) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isMessage)
                channelId = "me.zeeroooo.materialfb.notif.messages";
            else
                channelId = "me.zeeroooo.materialfb.notif.facebook";
        } else
            channelId = "me.zeeroooo.materialfb.notif";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create all channels at once so users can see/configure them all with the first notification
            if (mNotificationManager.getNotificationChannel("me.zeeroooo.materialfb.notif.messages") == null) {
                createNotificationChannel(channelId, getApplicationContext().getString(R.string.facebook_message), "vibrate_double_msg");
                createNotificationChannel(channelId, getApplicationContext().getString(R.string.facebook_notifications), "vibrate_double_notif");
            }
        } else {
            String ringtoneKey, vibrateKey, vibrateDoubleKey, ledKey;
            if (isMessage) {
                ringtoneKey = "ringtone_msg";
                vibrateKey = "vibrate_msg";
                vibrateDoubleKey = "vibrate_double_msg";
                ledKey = "led_msj";
            } else {
                ringtoneKey = "ringtone";
                vibrateDoubleKey = "vibrate_double_notif";
                vibrateKey = "vibrate_notif";
                ledKey = "led_notif";
            }

            if (mPreferences.getBoolean(vibrateKey, false)) {
                mBuilder.setVibrate(new long[]{500, 500});
                if (mPreferences.getBoolean(vibrateDoubleKey, false))
                    mBuilder.setVibrate(new long[]{500, 500, 500, 500});
            }

            if (mPreferences.getBoolean(ledKey, false))
                mBuilder.setLights(Color.BLUE, 1000, 1000);

            mBuilder.setSound(Uri.parse(mPreferences.getString(ringtoneKey, "content://settings/system/notification_sound")));

            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        mBuilder
                .setChannelId(channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setColor(MFB.colorPrimary)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(Glide.with(getApplicationContext()).asBitmap().load(Helpers.decodeImg(image_url)).apply(RequestOptions.circleCropTransform()).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get())
                .setSmallIcon(R.drawable.ic_material)
                .setAutoCancel(true);

        // priority for Heads-up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);

        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("notifUrl", url);

        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), id, intent, 0);
        mBuilder.setContentIntent(resultPendingIntent);

        if (mNotificationManager != null)
            mNotificationManager.notify(id, mBuilder.build());
    }

    @TargetApi(26)
    private void createNotificationChannel(String id, String name, String vibrateDouble) {
        final NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setShowBadge(true);
        notificationChannel.enableVibration(true);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.BLUE);

        if (mPreferences.getBoolean(vibrateDouble, false))
            notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500});

        mNotificationManager.createNotificationChannel(notificationChannel);
    }

  /*  public static void clearbyId(Context c, int id) {
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null)
            mNotificationManager.cancel(id);
    }*/
}
