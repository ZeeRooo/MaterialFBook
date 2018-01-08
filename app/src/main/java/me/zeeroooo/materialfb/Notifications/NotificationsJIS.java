package me.zeeroooo.materialfb.Notifications;

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
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
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
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.Misc.DatabaseHelper;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.Theme;
import me.zeeroooo.materialfb.WebView.Helpers;

public class NotificationsJIS extends JobIntentService {
    private SharedPreferences mPreferences;
    private boolean msg_notAWhiteList = false, notif_notAWhiteList = false;
    private String baseURL, pictureNotif, pictureMsg, e = "", ringtoneKey, vibrate_, vibrate_double_, led_;
    private Bitmap picprofile;
    private String[] picMsg, picNotif;
    private Spanned emoji;
    private int mode = 0;
    private List<String> blist;
    private DatabaseHelper db;
    private NotificationManager mNotificationManager;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationsJIS.class, 2, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("JobIntentService_MFB", "Started");
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        URLs();
        blist = new ArrayList<>();
        db = new DatabaseHelper(this);
        final Cursor data = db.getListContents();
        while (data.moveToNext()) {
            if (data.getString(3) != null)
                blist.add(data.getString(3));
        }
        if (mPreferences.getBoolean("facebook_messages", false))
            SyncMessages();
        if (mPreferences.getBoolean("facebook_notifications", false))
            SyncNotifications();
    }

    private void URLs() {
        if (!mPreferences.getBoolean("save_data", false))
            baseURL = "https://m.facebook.com/";
        else
            baseURL = "https://mbasic.facebook.com/";
    }

    @Override
    public void onDestroy() {
        Log.i("JobIntentService_MFB", "Stopped");
        if (pictureMsg != null)
            pictureMsg = "";
        if (pictureNotif != null)
            pictureNotif = "";
        if (db != null)
            db.close();
        if (msg_notAWhiteList)
            msg_notAWhiteList = false;
        if (notif_notAWhiteList)
            notif_notAWhiteList = false;
        super.onDestroy();
    }

    // Sync the notifications
    void SyncNotifications() {
        Element result = null;
        Helpers.getCookie();
        Log.i("JobIntentService_MFB", "Trying: " + "https://m.facebook.com/notifications.php");

        try {
            Document doc = Jsoup.connect("https://m.facebook.com/notifications.php").timeout(5000).cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).get();
            if (doc != null)
                result = doc.select("a.touchable").not("a._19no").not("a.button").not("a.touchable.primary").first();
            if (result == null || result.text() == null)
                return;

            final String time = result.select("span.mfss.fcg").text();
            final String content = result.select("div.c").text().replace(time, "");
            if (!blist.isEmpty())
                for (String s : blist) {
                    if (content.contains(s))
                        notif_notAWhiteList = true;
                }
            if (!notif_notAWhiteList) {
                final String text = content.replace(time, "");
                pictureNotif = result.select("i.img.l.profpic").attr("style");

                if (pictureNotif != null)
                    picNotif = pictureNotif.split("('*')");

                if (!mPreferences.getString("last_notification_text", "").equals(text))
                    notifier(content, getString(R.string.app_name), baseURL + "notifications.php", false, picNotif[1]);

                // save as shown (or ignored) to avoid showing it again
                mPreferences.edit().putString("last_notification_text", text).apply();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void SyncMessages() {
        Element result = null;
        Helpers.getCookie();
        Log.i("JobIntentService_MFB", "Trying: " + "https://m.facebook.com/messages?soft=messages");
        try {
            Document doc = Jsoup.connect("https://m.facebook.com/messages?soft=messages").timeout(10000).cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).get();
            if (doc != null)
                result = doc.getElementsByClass("item messages-flyout-item aclb abt").select("a.touchable.primary").first();
            if (result == null || result.text() == null)
                return;
            final String content = result.select("div.oneLine.preview.mfss.fcg").text();
            if (!blist.isEmpty())
                for (String s : blist) {
                    if (content.contains(s))
                        msg_notAWhiteList = true;
                }
            if (!msg_notAWhiteList) {
                final String text = result.text().replace(result.select("div.time.r.nowrap.mfss.fcl").text(), "");
                final String name = result.select("div.title.thread-title.mfsl.fcb").text();
                pictureMsg = result.select("i.img.profpic").attr("style");
                String CtoDisplay = content;

                if (pictureMsg != null)
                    picMsg = pictureMsg.split("('*')");

                Elements e_iemoji = result.select("._47e3._3kkw");
                if (!e_iemoji.isEmpty())
                    for (Element em : e_iemoji) {
                        String emojiUrl = em.attr("style");
                        String[] emoji_sp = emojiUrl.split("/");
                        String emoji_unicode = "0x" + emoji_sp[9].replace(".png)", "");
                        int i = Integer.parseInt(emoji_unicode.substring(2), 16);
                        String emoji_char = new String(Character.toChars(i));
                        e = e + emoji_char;
                        emoji = Html.fromHtml(e);
                        mode = 1;
                    }
                Elements e_emoji = result.select("._1ift._2560.img");
                if (!e_emoji.isEmpty())
                    for (Element em : e_emoji) {
                        String emojiUrl = em.attr("src");
                        String[] emoji_sp = emojiUrl.split("/");
                        String emoji_unicode = "0x" + emoji_sp[9].replace(".png", "");
                        int i = Integer.parseInt(emoji_unicode.substring(2), 16);
                        String emoji_char = new String(Character.toChars(i));
                        e = e + emoji_char;
                        emoji = Html.fromHtml(e);
                        mode = 2;
                    }

                if (mode != 0)
                    CtoDisplay += " " + emoji;

                if (!mPreferences.getString("last_message", "").equals(text))
                    notifier(CtoDisplay, name, baseURL + "messages/", true, picMsg[1]);

                // save as shown (or ignored) to avoid showing it again
                mPreferences.edit().putString("last_message", text).apply();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // create a notification and display it
    private void notifier(@Nullable final String content, final String title, final String url, boolean isMessage, final String image_url) {

        try {
            picprofile = Glide.with(this).asBitmap().load(Helpers.decodeImg(image_url)).apply(RequestOptions.circleCropTransform()).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (Exception e) {
            e.getStackTrace();
        }

        if (isMessage) {
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

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("me.zeeroooo.materialfb.notif", "MFBNotifications", mNotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (mPreferences.getBoolean(vibrate_, false)) {
                notificationChannel.setVibrationPattern(new long[]{500, 500});
                if (mPreferences.getBoolean(vibrate_double_, false))
                    notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500});
                else
                    notificationChannel.enableVibration(false);
            }
            if (mPreferences.getBoolean(led_, false)) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
            }
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "me.zeeroooo.materialfb.notif")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setColor(Theme.getColor(this))
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(picprofile)
                .setSmallIcon(R.drawable.ic_material)
                .setAutoCancel(true);

        if (mPreferences.getBoolean(vibrate_, false)) {
            mBuilder.setVibrate(new long[]{500, 500});
            if (mPreferences.getBoolean(vibrate_double_, false))
                mBuilder.setVibrate(new long[]{500, 500, 500, 500});
        }

        if (mPreferences.getBoolean(led_, false))
            mBuilder.setLights(Color.BLUE, 1000, 1000);

        // priority for Heads-up
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);

        Uri ringtoneUri = Uri.parse(mPreferences.getString(ringtoneKey, "content://settings/system/notification_sound"));
        mBuilder.setSound(ringtoneUri);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Job_url", url);
        mBuilder.setOngoing(false);
        mBuilder.setOnlyAlertOnce(true);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        if (isMessage)
            mNotificationManager.notify(1, mBuilder.build());
        else
            mNotificationManager.notify(0, mBuilder.build());
    }

    public static void ClearbyId(Context c, int id) {
        NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }
}