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
import org.jsoup.nodes.Document;

import me.zeeroooo.materialfb.R;

public class UserInfo extends AsyncTask<Activity, Void, Activity> {
    private String name, cover, propic;

    @Override
    protected Activity doInBackground(Activity[] activities) {
        try {
            if (!activities[0].isDestroyed()) {
                Document document = Jsoup.connect("https://mbasic.facebook.com/me").cookie(("https://m.facebook.com/"), CookieManager.getInstance().getCookie(("https://m.facebook.com/"))).timeout(300000).get();
                if (name == null)
                    name = document.getElementsByClass("br").select("div > a > img").attr("alt");
                if (cover == null)
                    cover = document.getElementById("profile_cover_photo_container").select("div > a > img").attr("src");
                if(propic == null)
                    propic = document.getElementsByClass("br").select("div > a > img").attr("src");
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
                if (propic != null && activity.findViewById(R.id.profile_picture) != null)
                    Glide.with(activity)
                            .load(propic)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).circleCrop())
                            .into((ImageView) activity.findViewById(R.id.profile_picture));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
