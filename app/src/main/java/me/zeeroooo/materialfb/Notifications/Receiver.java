package me.zeeroooo.materialfb.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zeerooo on 18/12/17.
 * We'r using AlarmManager + BroadcastReceiver for API 19 and below.
 */

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new Scheduler(context).startAlarm();
    }
}
