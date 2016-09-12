package me.zeeroooo.materialfb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PollReceiver extends BroadcastReceiver {
    static void scheduleAlarms(Context ctxt, boolean cancel) {
        // Prepare the intent for the notification alarm
        AlarmManager mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctxt, NotificationService.class);
        PendingIntent pi = PendingIntent.getService(ctxt, 0, i, 0);

        // Start the alarm
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctxt);
        if (preferences.getBoolean(SettingsActivity.KEY_PREF_NOTIFICATIONS_ENABLED, false) && !cancel) {
            int interval = Integer.parseInt(preferences.getString(SettingsActivity.KEY_PREF_NOTIFICATION_INTERVAL, "600000"));
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 5000, interval, pi);
            Log.v(Helpers.LogTag, "Notification repeating alarm started");
        } else {
            // Cancel the alarm if notifications are disabled
            mgr.cancel(pi);
            Log.v(Helpers.LogTag, "Notification repeating alarm canceled");
        }
    }

    @Override
    public void onReceive(Context ctxt, Intent i) {
        scheduleAlarms(ctxt, false);
    }
}