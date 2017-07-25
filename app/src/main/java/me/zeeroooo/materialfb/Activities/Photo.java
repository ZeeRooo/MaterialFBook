package me.zeeroooo.materialfb.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import java.io.File;
import me.zeeroooo.materialfb.Ui.CookingAToast;
import me.zeeroooo.materialfb.R;
import uk.co.senab.photoview.PhotoViewAttacher;
import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;

public class Photo extends AppCompatActivity {

    private AppCompatImageView mImageView;
    PhotoViewAttacher mAttacher;
    AppCompatTextView text;
    private DownloadManager mDownloadManager;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mImageView = (AppCompatImageView) findViewById(R.id.container);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_ph);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        text = (AppCompatTextView) findViewById(R.id.photo_title);
        url = getIntent().getStringExtra("url");
        text.setText(getIntent().getStringExtra("title"));
        Load();
        mAttacher = new PhotoViewAttacher(mImageView);
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    private void Load() {
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        findViewById(android.R.id.progress).setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.download_image) {
            RequestStoragePermission();
        }
        if (id == R.id.share_image) {
            // Share image
            Glide.with(Photo.this).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).format(PREFER_ARGB_8888).into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                    final String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, Uri.parse(url).getLastPathSegment(), null);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.context_share_image)));
                    bitmap.recycle();
                }
            });
            CookingAToast.cooking(Photo.this, getString(R.string.context_share_image_progress), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_share, false).show();
        }
        if (id == R.id.oopy_url_image) {
            final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData clip = ClipData.newUri(this.getContentResolver(), "", Uri.parse(url));
            clipboard.setPrimaryClip(clip);
            CookingAToast.cooking(Photo.this, getString(R.string.content_copy_link_done), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_copy_url, true).show();
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void RequestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Save the image
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    // Set the download directory
                    File downloads_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    if (!downloads_dir.exists()) {
                        if (!downloads_dir.mkdirs()) {
                            return;
                        }
                    }
                    File destinationFile = new File(downloads_dir, Uri.parse(url).getLastPathSegment());
                    request.setDestinationUri(Uri.fromFile(destinationFile));

                    // Make notification stay after download
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    // Start the download
                    mDownloadManager.enqueue(request);

                    CookingAToast.cooking(this, getString(R.string.downloaded), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_download, false).show();
                } else {
                    CookingAToast.cooking(this, getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Glide.clear(mImageView);
        mImageView.setImageDrawable(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}