package me.zeeroooo.materialfb;

import android.app.Application;
import android.content.Context;

public class MaterialFBook extends Application {

    private static Context mContext;

    // Correct context. Thanks to indywidualny (FaceSlim)
    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();

    }
    public static Context getContextOfApplication() {
        return mContext;
    }

}