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
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.CookingAToast;

public class PhotoTest extends AppCompatActivity implements View.OnTouchListener {

    private ImageView mImageView;
    private DownloadManager mDownloadManager;
    private Target<Bitmap> ShareTarget;
    private boolean download = false, countdown = false;
    private Matrix matrix = new Matrix(), savedMatrix = new Matrix();
    private int NONE = 0, DRAG = 1, ZOOM = 2, mode = NONE, share = 0;
    private PointF start = new PointF(), mid = new PointF();
    private float oldDist = 1f;
    private View imageTitle, topGradient;
    private Toolbar mToolbar;
    private String url;
    WebView webView;
    String genericImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mImageView = findViewById(R.id.container);
        mImageView.setOnTouchListener(this);
        topGradient = findViewById(R.id.photoViewerTopGradient);
        mToolbar = findViewById(R.id.toolbar_ph);
        webView = new WebView(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        url = getIntent().getStringExtra("link");
        imageTitle = findViewById(R.id.photo_title);
        ((TextView) imageTitle).setText(getIntent().getStringExtra("title"));
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (url != null) {
            webView.loadUrl(url);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Load();
                super.onPageFinished(view, url);
            }
        });

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        setVisibility(View.VISIBLE, android.R.anim.fade_in);
        setCountDown();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                oldDist = spacing(event);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    mid.set(event.getX(0) + event.getX(1) / 2, event.getY(0) + event.getY(1) / 2);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event), scale;
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        mImageView.setImageMatrix(matrix);
        v.performClick();

        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    } // https://stackoverflow.com/a/6650484 all the credits to Chirag Raval

    private void Load() {
        Glide.with(this)
                .load(webView.getUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mImageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        findViewById(android.R.id.progress).setVisibility(View.GONE);
                        setCountDown();
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
        if (id == R.id.download_image) {
            download = true;
            RequestStoragePermission();
        }
        if (id == R.id.share_image) {
            share = 1;
            RequestStoragePermission();
        }
        if (id == R.id.oopy_url_image) {
            final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            final ClipData clip = ClipData.newUri(this.getContentResolver(), "", Uri.parse(url));
            if (clipboard != null)
                clipboard.setPrimaryClip(clip);
            CookingAToast.cooking(PhotoTest.this, getString(R.string.content_copy_link_done), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_copy_url, true).show();
        }
        if (id == android.R.id.home)
            onBackPressed();
        return false;
    }

    private void shareImg() {
        ShareTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, Uri.parse(url).getLastPathSegment(), null);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.context_share_image)));
                CookingAToast.cooking(PhotoTest.this, getString(R.string.context_share_image_progress), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_share, false).show();
            }
        };
        Glide.with(PhotoTest.this).asBitmap().load(url).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(ShareTarget);
        share = 2;
    }

    private void RequestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (share == 1)
                        shareImg();
                    else if (download) {
                        // Save the image
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                        // Set the download directory
                        File downloads_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        if (!downloads_dir.exists())
                            if (!downloads_dir.mkdirs())
                                return;
                        File destinationFile = new File(downloads_dir, Uri.parse(url).getLastPathSegment());
                        request.setDestinationUri(Uri.fromFile(destinationFile));

                        // Make notification stay after download
                        request.setVisibleInDownloadsUi(true);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        // Start the download
                        mDownloadManager.enqueue(request);

                        CookingAToast.cooking(this, getString(R.string.downloaded), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_download, false).show();
                        download = false;
                    }
                } else
                    CookingAToast.cooking(this, getString(R.string.permission_denied), Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!(share == 2)) {
            if (ShareTarget != null)
                Glide.with(PhotoTest.this).clear(ShareTarget);
            if (mImageView != null)
                mImageView.setImageDrawable(null);
        }
    }

    private void setCountDown() {
        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown = true;
            }

            @Override
            public void onFinish() {
                setVisibility(View.INVISIBLE, android.R.anim.fade_out);
                countdown = false;
            }
        };
        if (!countdown)
            countDownTimer.start();
        else
            countDownTimer.cancel();
    }

    public void setVisibility(int visibility, int animation) {
        Animation a = AnimationUtils.loadAnimation(this, animation);

        topGradient.startAnimation(a);
        mToolbar.startAnimation(a);
        imageTitle.startAnimation(a);

        topGradient.setVisibility(visibility);
        mToolbar.setVisibility(visibility);
        imageTitle.setVisibility(visibility);
    }
}
