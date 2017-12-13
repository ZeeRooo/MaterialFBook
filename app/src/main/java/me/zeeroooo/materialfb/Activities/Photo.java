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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;
import me.zeeroooo.materialfb.Ui.CookingAToast;
import me.zeeroooo.materialfb.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class Photo extends AppCompatActivity {

    private ImageView mImageView;
    PhotoViewAttacher mAttacher;
    TextView text;
    private DownloadManager mDownloadManager;
    private String url;
    private Target<Bitmap> ShareTarget;
    public static int closed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mImageView = findViewById(R.id.container);
        Toolbar mToolbar = findViewById(R.id.toolbar_ph);
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

        text = findViewById(R.id.photo_title);
        url = getIntent().getStringExtra("link");
        text.setText(getIntent().getStringExtra("title"));
        Load();
        mAttacher = new PhotoViewAttacher(mImageView);
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    private void Load() {
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        findViewById(android.R.id.progress).setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
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
        if (id == R.id.download_image)
            RequestStoragePermission();
        if (id == R.id.share_image) {
            // Share image
            ShareTarget = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    RequestStoragePermission();
                    final String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, Uri.parse(url).getLastPathSegment(), null);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.context_share_image)));
                    if (!bitmap.isRecycled())
                        bitmap.recycle();
                    CookingAToast.cooking(Photo.this, getString(R.string.context_share_image_progress), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_share, false).show();
                }
            };
            Glide.with(Photo.this).asBitmap().load(url).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(ShareTarget);
        }
        if (id == R.id.oopy_url_image) {
            final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData clip = ClipData.newUri(this.getContentResolver(), "", Uri.parse(url));
            clipboard.setPrimaryClip(clip);
            CookingAToast.cooking(Photo.this, getString(R.string.content_copy_link_done), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_copy_url, true).show();
        }
        if (id == android.R.id.home)
            onBackPressed();
        return false;
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
                        if (!downloads_dir.mkdirs())
                            return;
                    }
                    File destinationFile = new File(downloads_dir, Uri.parse(url).getLastPathSegment());
                    request.setDestinationUri(Uri.fromFile(destinationFile));

                    // Make notification stay after download
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    // Start the download
                    mDownloadManager.enqueue(request);

                    CookingAToast.cooking(this, getString(R.string.downloaded), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_download, false).show();
                } else
                    CookingAToast.cooking(this, getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closed = 1;
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ShareTarget != null)
            Glide.with(Photo.this).clear(ShareTarget);
        if (mImageView != null)
            mImageView.setImageDrawable(null);
        closed = 1;
    }
}