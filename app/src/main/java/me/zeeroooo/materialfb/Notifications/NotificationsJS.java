package me.zeeroooo.materialfb.Notifications;

import android.content.Intent;
import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class NotificationsJS extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.i("JobService_MFB", "Started");

        NotificationsJIS.enqueueWork(this, new Intent(this, NotificationsJIS.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.i("JobService_MFB", "Stopped");
        return false;
    }
}