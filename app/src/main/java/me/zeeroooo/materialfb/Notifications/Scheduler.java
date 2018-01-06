package me.zeeroooo.materialfb.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by zeerooo on 18/12/17.
 * If the OS version is L+ let's use JobService, otherwise AlarmManager.
 */

@SuppressWarnings("NewApi")
public class Scheduler {
    private AlarmManager mAlarm = null;
    private JobScheduler mJobScheduler;
    private PendingIntent pIntent;
    private Context mContext;
    private Receiver receiver = null;
    private int syncTime, syncExact = 0;

    public Scheduler(Context context) {
        this.mContext = context;
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (mPreferences.getBoolean("notif_exact", false))
            syncExact = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mJobScheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        else {
            pIntent = PendingIntent.getService(mContext, 1, new Intent(mContext, NotificationsJIS.class), PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        }
    }

    public void schedule(int time, boolean startOnBoot) {
        if (mAlarm == null) {
            JobInfo.Builder job = new JobInfo.Builder(1, new ComponentName(mContext, NotificationsJS.class));
            PersistableBundle pb = new PersistableBundle();
            pb.putInt("JobSyncTime", time);
            job.setPersisted(startOnBoot)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(time)
                    .setExtras(pb);
            if (connected())
                if (syncExact == 1)
                    job.setOverrideDeadline(time);
                else
                    job.setOverrideDeadline(time * 2);
            mJobScheduler.schedule(job.build());
            Log.i("MFB_Scheduler", "JobScheduler started");
        } else {
            syncTime = time;
            receiver = new Receiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            mContext.getPackageManager().setComponentEnabledSetting(new ComponentName(mContext, Receiver.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            mContext.registerReceiver(receiver, filter);
            Log.i("MFB_Scheduler", "AlarmManager started");
        }
    }

    public void cancel() {
        if (mAlarm == null) {
            mJobScheduler.cancelAll();
            Log.i("MFB_Scheduler", "JobScheduler finished");
        } else {
            mAlarm.cancel(pIntent);
            if (receiver != null)
                mContext.unregisterReceiver(receiver);
            mContext.getPackageManager().setComponentEnabledSetting(new ComponentName(mContext, Receiver.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            Log.i("MFB_Scheduler", "AlarmManager finished");
        }
    }

    void startAlarm() {
        if (connected())
            if (syncExact == 1)
                mAlarm.setExact(AlarmManager.RTC_WAKEUP, syncTime, pIntent);
            else
                mAlarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), syncTime, pIntent);
        else
            cancel();
    }

    private boolean connected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
