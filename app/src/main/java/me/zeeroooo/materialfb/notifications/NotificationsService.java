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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.activities.MainActivity;
import me.zeeroooo.materialfb.misc.DatabaseHelper;
import me.zeeroooo.materialfb.ui.Theme;
import me.zeeroooo.materialfb.webview.Helpers;

public class NotificationsService extends Worker {
    private SharedPreferences mPreferences;
    private boolean msg_notAWhiteList = false, notif_notAWhiteList = false;
    private String baseURL;
    private List<String> blist;

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
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT BL FROM mfb_table", null);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        URLs();
        blist = new ArrayList<>();

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
        Document doc = Jsoup.connect("https://m.facebook.com/notifications.php").cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).timeout(300000).get();
        Element notifications = doc.selectFirst("div.aclb > div.touchable-notification > a.touchable");

        final String time = notifications.select("span.mfss.fcg").text();
        final String content = notifications.select("div.c").text().replace(time, "");

        if (!blist.isEmpty())
            for (int position = 0; position < blist.size(); position++) {
                if (content.toLowerCase().contains(blist.get(position).toLowerCase()))
                    notif_notAWhiteList = true;
            }

        if (!notif_notAWhiteList) {
            final String text = content.replace(time, "");
            String pictureNotif = notifications.select("div.ib > i").attr("style");

            if (!mPreferences.getString("last_notification_text", "").contains(text))
                notifier(content, getApplicationContext().getString(R.string.app_name), baseURL + "notifications.php", pictureNotif.split("('*')")[1], 12);

            mPreferences.edit().putString("last_notification_text", text).apply();
        }
    }

    private void SyncMessages() throws Exception {
        Document doc = Jsoup.connect("https://mbasic.facebook.com/messages").cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).timeout(300000).get();
        Element result = doc.selectFirst("table.bn.bo.bp.bq.br.bs.bt.bu.bv");

        if (result != null) {
            String content = result.selectFirst("h3.cc.ba.cd > span.ce.cf.cd").html();

            if (!blist.isEmpty())
                for (int position = 0; position < blist.size(); position++) {
                    if (content.toLowerCase().contains(blist.get(position).toLowerCase()))
                        msg_notAWhiteList = true;
                }

            if (!msg_notAWhiteList) {
                final String name = result.selectFirst("h3.by.bz.ca > a").text();
                String pictureMsg = "https://graph.facebook.com/" + result.selectFirst("h3.by.bz.ca > a").attr("href").split("cid.c.")[1].split("%3A")[0] + "/picture?type=large";

                if (content.contains("<i class=\"cg ch\"")) {
                    Elements e_iemoji = result.getElementsByClass("cg ch");
                    for (Element em : e_iemoji) {
                        content = content.replace("<i class=\"cg ch\" style=\"" + em.attr("style") + "\"></i>", getEmoji(em.attr("style").replace("f0000", "1F44D")));
                    }
                }

                if (!mPreferences.getString("last_message", "").equals(content))
                    notifier(content.replaceAll("<img src=\"(.*)>", ""), name, baseURL + "messages", pictureMsg, 969);

                // save as shown (or ignored) to avoid showing it again
                mPreferences.edit().putString("last_message", content).apply();
            }
        }
    }

    private String getEmoji(String emojiUrl) {
        String[] emoji_sp = emojiUrl.split("/");
        String emoji_unicode = "0x" + emoji_sp[9].replace(".png)", "");
        int i = Integer.parseInt(emoji_unicode.substring(2), 16);
        return new String(Character.toChars(i));
    }

    // create a notification and display it
    private void notifier(final String content, final String title, final String url, final String image_url, int id) throws Exception {
        Bitmap picprofile = Glide.with(getApplicationContext()).asBitmap().load(Helpers.decodeImg(image_url)).apply(RequestOptions.circleCropTransform()).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (id == 969)
                channelId = "me.zeeroooo.materialfb.notif.messages";
            else
                channelId = "me.zeeroooo.materialfb.notif.facebook";
        } else
            channelId = "me.zeeroooo.materialfb.notif";

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);

        String ringtoneKey, vibrate_, vibrate_double_, led_;
        if (id == 969) {
            ringtoneKey = "ringtone_msg";
            vibrate_ = "vibrate_msg";
            vibrate_double_ = "vibrate_double_msg";
            led_ = "led_msj";
        } else {
            ringtoneKey = "ringtone";
            vibrate_ = "vibrate_notif";
            vibrate_double_ = "vibrate_double_notif";
            led_ = "led_notif";
        }

        Uri ringtoneUri = Uri.parse(mPreferences.getString(ringtoneKey, "content://settings/system/notification_sound"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create all channels at once so users can see/configure them all with the first notification
            createNotificationChannel(mNotificationManager, "me.zeeroooo.materialfb.notif.messages", getApplicationContext().getString(R.string.facebook_message), "vibrate_msg", "vibrate_double_msg", "led_msj", ringtoneUri);
            createNotificationChannel(mNotificationManager, "me.zeeroooo.materialfb.notif.facebook", getApplicationContext().getString(R.string.facebook_notifications), "vibrate_notif", "vibrate_double_notif", "led_notif", ringtoneUri);
        } else {
            if (mPreferences.getBoolean(vibrate_, false)) {
                mBuilder.setVibrate(new long[]{500, 500});
                if (mPreferences.getBoolean(vibrate_double_, false))
                    mBuilder.setVibrate(new long[]{500, 500, 500, 500});
            }

            if (mPreferences.getBoolean(led_, false))
                mBuilder.setLights(Color.BLUE, 1000, 1000);

            mBuilder.setSound(ringtoneUri);

            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        mBuilder
                .setChannelId(channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setColor(Theme.getColor(getApplicationContext()))
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(picprofile)
                .setSmallIcon(R.drawable.ic_material)
                .setAutoCancel(true);

        // priority for Heads-up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("notifUrl", url);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), id, intent, 0);
        mBuilder.setContentIntent(resultPendingIntent);

        if (mNotificationManager != null)
            mNotificationManager.notify(id, mBuilder.build());
    }

    @TargetApi(26)
    private void createNotificationChannel(NotificationManager notificationManager, String id, String name, String vibratePref, String doubleVibratePref, String ledPref, Uri uri) {
        NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setShowBadge(true);
        notificationChannel.enableVibration(mPreferences.getBoolean(vibratePref, false));
        notificationChannel.enableLights(mPreferences.getBoolean(ledPref, false));
        notificationChannel.setSound(uri, new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build());

        if (mPreferences.getBoolean(vibratePref, false)) {
            notificationChannel.setVibrationPattern(new long[]{500, 500});
            if (mPreferences.getBoolean(doubleVibratePref, false))
                notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500});
        }

        if (mPreferences.getBoolean(ledPref, false))
            notificationChannel.setLightColor(Color.BLUE);

        notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void clearbyId(Context c, int id) {
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null)
            mNotificationManager.cancel(id);
    }
}
