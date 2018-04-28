package me.zeeroooo.materialfb.Activities;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by ZeeRooo on 15/04/18
 */

@ReportsCrashes(mailTo = "putYourOwnEmail...")
public class MFB extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        ACRA.init(this);
    }
}
