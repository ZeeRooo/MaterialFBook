package me.zeeroooo.materialfb.Misc;

import me.zeeroooo.materialfb.Activities.MainActivity;

import android.os.AsyncTask;

import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;

import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.WebView.Helpers;

import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class UserInfo extends AsyncTask<Void, Void, String> {
    private MainActivity mActivity;
    private String name, cover;

    public UserInfo(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    protected String doInBackground(Void[] params) {
        try {
            Element e = Jsoup.connect("https://www.facebook.com/me").cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).timeout(300000).get().body();
            if (name == null)
                name = e.select("input[name=q]").attr("value");
            if (cover == null) {
                String[] s = e.toString().split("<img class=\"coverPhotoImg photo img\" src=\"");
                String[] c = s[1].split("\"");
                cover = Helpers.decodeImg(c[0]);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        try {
            if (name != null)
                ((TextView) mActivity.findViewById(R.id.profile_name)).setText(name);
            if (cover != null)
                Glide.with(mActivity)
                        .load(cover)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into((ImageView) mActivity.findViewById(R.id.cover));
            if (Helpers.getCookie() != null && mActivity.findViewById(R.id.profile_picture) != null)
                Glide.with(mActivity)
                        .load("https://graph.facebook.com/" + Helpers.getCookie() + "/picture?type=large")
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).circleCrop())
                        .into((ImageView) mActivity.findViewById(R.id.profile_picture));
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}