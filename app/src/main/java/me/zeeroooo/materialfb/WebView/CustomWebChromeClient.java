package me.zeeroooo.materialfb.WebView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.R;

public class CustomWebChromeClient extends WebChromeClient {
    private final MainActivity mActivity;
    private final WebView mWebView;
    private final ViewGroup mCustomViewContainer;
    private final FloatingActionMenu mMenuFAB;
    private View mVideoProgressView;
    private View mCustomView;
    private CustomViewCallback customViewCallback;

    public CustomWebChromeClient(MainActivity activity, WebView webview, FrameLayout viewcontainer) {
        mActivity = activity;
        mWebView = webview;
        mCustomViewContainer = viewcontainer;
        mMenuFAB = (FloatingActionMenu) activity.findViewById(R.id.menuFAB);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        mCustomView = view;
        mWebView.setVisibility(View.GONE);
        mMenuFAB.hideMenuButton(false);
        mCustomViewContainer.setVisibility(View.VISIBLE);
        mCustomViewContainer.addView(view);
        customViewCallback = callback;
        mActivity.fullscreen();
    }



    @Override
    public View getVideoLoadingProgressView() {
        if (mVideoProgressView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            mVideoProgressView = inflater.inflate(R.layout.video_progress, mWebView, false);
        }
        return mVideoProgressView;
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
        if (mCustomView == null) {
            return;
        }

        mWebView.setVisibility(View.VISIBLE);
        mMenuFAB.showMenuButton(true);
        mCustomViewContainer.setVisibility(View.GONE);

        // Hide the custom view.
        mCustomView.setVisibility(View.GONE);

        // Remove the custom view from its container.
        mCustomViewContainer.removeView(mCustomView);
        customViewCallback.onCustomViewHidden();
        mCustomView = null;
        mActivity.exitfullscreen();
    }
}