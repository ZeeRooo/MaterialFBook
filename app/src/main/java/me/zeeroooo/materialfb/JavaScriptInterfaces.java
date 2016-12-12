package me.zeeroooo.materialfb;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;

@SuppressWarnings("unused")
class JavaScriptInterfaces {
    private final MainActivity mContext;
    private final SharedPreferences mPreferences;

    // Instantiate the interface and set the context
    JavaScriptInterfaces(MainActivity c) {
        mContext = c;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    @JavascriptInterface
    public void loadingCompleted() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setLoading(false);
            }
        });
    }

    @JavascriptInterface
    public void getCurrent(final String value) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (value) {
                    case "top_stories":
                            mContext.mNavigationView.setCheckedItem(R.id.nav_top_stories);
                            mContext.mNavigationView.setCheckedItem(R.id.nav_news);
                        break;
                    case "most_recent":
                            mContext.mNavigationView.setCheckedItem(R.id.nav_most_recent);
                            mContext.mNavigationView.setCheckedItem(R.id.nav_news);
                        break;
                    case "requests_jewel":
                            mContext.mNavigationView.setCheckedItem(R.id.nav_friendreq);
                        break;
                    case "messages_jewel":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_messages);
                        break;
                    case "messages":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_messages);
                        break;
                    case "notifications_jewel":
                        Helpers.uncheckRadioMenu(mContext.mNavigationView.getMenu());
                        break;
                    case "search_jewel":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_search);
                        break;
                    case "bookmarks_jewel":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_mainmenu);
                        break;
                    default:
                        Helpers.uncheckRadioMenu(mContext.mNavigationView.getMenu());
                        break;
                }
            }
        });
    }

    @JavascriptInterface
    public void getNums(final String notifications, final String messages, final String requests) {
      //  final int notifications_int = Helpers.isInteger(notifications) ? Integer.parseInt(notifications) : 0;
        final int messages_int = Helpers.isInteger(messages) ? Integer.parseInt(messages) : 0;
      //  final int setMessagessNum = Helpers.isInteger(messages) ? Integer.parseInt(messages) : 0;
        final int requests_int = Helpers.isInteger(requests) ? Integer.parseInt(requests): 0;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // mContext.setNotificationNum(notifications_int);
                mContext.setMessagesNum(messages_int);
               // mContext.setMessagessNum(messages_int);
                mContext.setRequestsNum(requests_int);
            }
        });
    }
}
