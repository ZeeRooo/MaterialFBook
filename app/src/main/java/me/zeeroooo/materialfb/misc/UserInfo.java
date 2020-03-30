package me.zeeroooo.materialfb.misc;

import android.app.Activity;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.webview.Helpers;

public class UserInfo extends AsyncTask<Activity, Void, Activity> {
    private String name, cover;

    @Override
    protected Activity doInBackground(Activity[] activities) {
        try {
            if (!activities[0].isDestroyed()) {
                final Element e = Jsoup.connect("https://mbasic.facebook.com/me").cookie(("https://m.facebook.com"), CookieManager.getInstance().getCookie(("https://m.facebook.com"))).timeout(300000).get().body();
                if (name == null)
                    name = e.getElementsByClass("profpic img").attr("alt");
                if (cover == null)
                    cover = Helpers.decodeImg(e.selectFirst("div#profile_cover_photo_container > a > img").attr("src"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activities[0];
    }

    @Override
    protected void onPostExecute(Activity activity) {
        if (!activity.isDestroyed())
            try {
                if (name != null && activity.findViewById(R.id.profile_name) != null)
                    ((TextView) activity.findViewById(R.id.profile_name)).setText(name);
                if (cover != null && activity.findViewById(R.id.cover) != null)
                    Glide.with(activity)
                            .load(cover)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into((ImageView) activity.findViewById(R.id.cover));
                if (Helpers.getCookie() != null && activity.findViewById(R.id.profile_picture) != null)
                    Glide.with(activity)
                            .load("https://graph.facebook.com/" + Helpers.getCookie() + "/picture?type=large")
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).circleCrop())
                            .into((ImageView) activity.findViewById(R.id.profile_picture));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}