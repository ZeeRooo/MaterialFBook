package me.zeeroooo.materialfb.WebView;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;
import me.zeeroooo.materialfb.Activities.MainActivity;

@SuppressWarnings("unused")
public class JavaScriptInterfaces {
    private final MainActivity mContext;
    private final SharedPreferences mPreferences;

    // Instantiate the interface and set the context
    public JavaScriptInterfaces(MainActivity c) {
        mContext = c;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    @JavascriptInterface
    public void getNums(final String notifications, final String messages, final String requests) {
        final int notifications_int = Helpers.isInteger(notifications) ? Integer.parseInt(notifications) : 0;
        final int messages_int = Helpers.isInteger(messages) ? Integer.parseInt(messages) : 0;
        final int setMessagessNum = Helpers.isInteger(messages) ? Integer.parseInt(messages) : 0;
        final int requests_int = Helpers.isInteger(requests) ? Integer.parseInt(requests): 0;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setNotificationNum(notifications_int);
                mContext.setMessagesNum(messages_int);
                mContext.setMessagessNum(messages_int);
                mContext.setRequestsNum(requests_int);
            }
        });
    }
}
