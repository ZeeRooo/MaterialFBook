package me.zeeroooo.materialfb.Notifications;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

@TargetApi(21)
public class NotificationsJS extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("JobService_MFB", "Started");
        NotificationsJIS.enqueueWork(this, new Intent(this, NotificationsJIS.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return repeat(params);
    }

    private boolean repeat(JobParameters params) {
        new Scheduler(this).schedule(params.getExtras().getInt("JobSyncTime"), false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("JobService_MFB", "Stopped");
        return false;
    }
}