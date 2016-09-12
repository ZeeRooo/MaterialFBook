package me.zeeroooo.materialfb;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.CookieManager;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationService extends IntentService {
    private static final DateFormat DATEFORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    private static final String NOTIFICATION_URL = "https://www.facebook.com/notifications";
    private static final String DESKTOP_USERAGENT = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";

    private Date mLastNotification = null;
    private String feedURI = null;
    private SharedPreferences mPreferences;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Try to find a feed uri
        feedURI = mPreferences.getString("feed_uri", null);
        Log.v(Helpers.LogTag, "Found Feed in preferences");

        // Try to find the most recent notification
        String dateString = mPreferences.getString("last_notification_date", null);
        if (dateString != null) {
            try {
                mLastNotification = DATEFORMAT.parse(dateString);
            } catch (ParseException e) {
                mLastNotification = null;
                Log.i(Helpers.LogTag, "Last notification timestamp could parsed");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Save the most recent notification to preferences
        if (mLastNotification != null) {
            String datetime = DATEFORMAT.format(mLastNotification);
            mPreferences.edit().putString("last_notification_date", datetime).apply();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(Helpers.LogTag, "Notification alarm running");

        // Check if we have a Feed URL
        if (feedURI == null) {
            // Try to find the Feed URL
            updateFeed();
        }

        List<RSSItem> notificationsBlob = fetchNotifications();

        // If we can't get the notifications, don't waste resources
        if (notificationsBlob == null) {
            return;
        }

        if (mLastNotification != null) {
            List<RSSItem> unread = new ArrayList<>();

            for (RSSItem rssItem : notificationsBlob) {
                if (!rssItem.getPubDate().after(mLastNotification)) {
                    // Only add notifications that have not been posted
                    break;
                }
                unread.add(rssItem);
            }

            // Only proceed if there is a new notification
            if (unread.size() > 0) {
                // Update the last notification
                mLastNotification = unread.get(0).getPubDate();

                // Send the unread notifications to be posted
                sendNotification(unread);
            }
        } else {
            // No previous notification, set it to the most recent notification
            if (notificationsBlob.size() > 0) {
                mLastNotification = notificationsBlob.get(0).getPubDate();
            }
        }
    }

    private void updateFeed() {
        Log.i(Helpers.LogTag, "Updating Feed URL");
        try {
            Elements element = Jsoup.connect(NOTIFICATION_URL).userAgent(DESKTOP_USERAGENT).timeout(10000)
                    .cookie(MainActivity.FACEBOOK_URL_BASE, CookieManager.getInstance().getCookie(MainActivity.FACEBOOK_URL_BASE)).get()
                    .select("div#content").select("div.fwn").select("a[href*=rss20]");
            feedURI = "https://www.facebook.com/" + element.attr("href");
            mPreferences.edit().putString("feed_uri", feedURI).apply();
            Log.i(Helpers.LogTag, "Feed URL set");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Helpers.LogTag, "Failed to find Feed URL");
        }
    }

    private List<RSSItem> fetchNotifications() {
        RSSReader reader = new RSSReader();
        try {
            return reader.load(feedURI).getItems();
        } catch (RSSReaderException e) {
            Log.e(Helpers.LogTag, "Some error occurred with the RSS Reader");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(Helpers.LogTag, "Some error occurred when attempting to get RSS result");
            e.printStackTrace();
        }
        return null;
    }

    private void sendNotification(List<RSSItem> notifications) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.notify_logo)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setDefaults(-1);

        // Intent depends on context
        Intent resultIntent;

        if (notifications.size() > 1) {
            // If there are multiple notifications, mention the number
            String text = getString(R.string.notification_multiple_text, notifications.size());
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(text)).setContentText(text);

            // Set the url to the notification centre
            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.setAction(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(MainActivity.FACEBOOK_URL_BASE + "notifications/"));
        } else {
            // Set the title
            RSSItem notification = notifications.get(0);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getTitle())).setContentText(notification.getTitle());

            // View all Notifications button
            Intent viewNotificationsIntent = new Intent(this, MainActivity.class);
            viewNotificationsIntent.setAction(Intent.ACTION_VIEW);
            viewNotificationsIntent.setData(Uri.parse(MainActivity.FACEBOOK_URL_BASE + "notifications/"));
            PendingIntent pendingViewNotifications = PendingIntent.getActivity(getApplicationContext(), 0, viewNotificationsIntent, 0);
            mBuilder.addAction(R.drawable.ic_menu_notifications_active, getString(R.string.notification_viewall), pendingViewNotifications);

            // Creates an explicit intent for an Activity in your app
            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.setAction(Intent.ACTION_VIEW);
            resultIntent.setData(notification.getLink());
        }

        // Notification Priority (make LED blink)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        // Vibration
        if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_NOTIFICATION_VIBRATE, true)) {
            mBuilder.setVibrate(new long[]{500, 500});
        } else {
            mBuilder.setVibrate(null);
        }

        // Create TaskStack to ensure correct back button behaviour
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        // Build the notification
        Notification notification = mBuilder.build();

        // Set the LED colour
        notification.ledARGB = ContextCompat.getColor(this, R.color.colorPrimary);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // notifyID allows you to update the notification later on.
        int notifyID = 1;
        mNotificationManager.notify(notifyID, notification);

        Log.i(Helpers.LogTag, "Notification posted");
    }
}