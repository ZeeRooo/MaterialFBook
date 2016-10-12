/**
 * Code taken from FaceSlim by indywidualny. Thanks.
 */
package me.zeeroooo.materialfb.Utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import me.zeeroooo.materialfb.MaterialFBook;
import me.zeeroooo.materialfb.R;

import java.io.File;
import java.lang.ref.WeakReference;

/** Singleton pattern */
public final class Logger {

    private static volatile Logger instance;
    private static final int MSG_SHOW_TOAST = 1;
    private static final Context context = MaterialFBook.getContextOfApplication();
    private final SharedPreferences mPreferences;
    private final String logFilePath;
    private final MyHandler messageHandler;

    private Logger() {
        messageHandler = new MyHandler(this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        logFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + context.getString(R.string.app_name).replace(" ", "") + ".log";
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null)
                    instance = new Logger();
            }
        }
        return instance;
    }

    private boolean checkStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(context, storagePermission);
        return hasPermission == PackageManager.PERMISSION_GRANTED;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<Logger> mLogger;

        public MyHandler(Logger logger) {
            mLogger = new WeakReference<>(logger);
        }

        @Override
        public void handleMessage(Message msg) {
            Logger logger = mLogger.get();
            if (logger != null) {
                if (msg.what == MSG_SHOW_TOAST) {
                    String message = (String) msg.obj;
                    Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void displayStoragePermissionRefused() {
        Message msg = new Message();
        msg.what = MSG_SHOW_TOAST;
        msg.obj = context.getString(R.string.file_logger_needs_permission);
        messageHandler.sendMessage(msg);
    }

    public synchronized void i(String tag, String msg) {
        final boolean fileLoggingEnabled = mPreferences.getBoolean("file_logging", false);
        final boolean mounted = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        final boolean storageReady = mounted && checkStoragePermission();

        if (fileLoggingEnabled && storageReady) {
            FileLog.open(logFilePath, Log.VERBOSE, 1000000);  // 1 megabyte
            FileLog.i(tag, msg);
        } else if (fileLoggingEnabled) {
            displayStoragePermissionRefused();
            Log.i(tag, msg);
        } else {
            // use a standard logger instead
            Log.i(tag, msg);
        }
    }

}
