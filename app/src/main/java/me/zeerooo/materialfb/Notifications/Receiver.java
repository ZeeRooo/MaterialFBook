package me.zeerooo.materialfb.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class Receiver extends BroadcastReceiver {

    // Lets start the {@code NotificationsService.java}
    public static void ScheduleNotif(Context context) {
        AlarmManager AlarmM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationsService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, 0);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (mPreferences.getBoolean("notif", false) && (activeNetwork != null && activeNetwork.isConnected())) {
            // Lets stop the AlarmManager if the device is not connected :) ====== less battery drain
            int interval = Integer.parseInt(mPreferences.getString("notif_interval", "300000"));
            AlarmM.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, pendingIntent);
            Log.d("Riiiiiiing!", "Alarm started");
        } else {
            AlarmM.cancel(pendingIntent);
            Log.d("Chau", "Alarm stopped");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleNotif(context);
    }
}
