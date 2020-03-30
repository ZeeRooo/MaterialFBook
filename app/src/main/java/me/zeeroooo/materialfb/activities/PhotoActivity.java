package me.zeeroooo.materialfb.activities;

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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.ui.CookingAToast;

public class PhotoActivity extends MFBActivity implements View.OnTouchListener {

    private ImageView mImageView;
    private boolean download = false, countdown = false;
    private Matrix matrix = new Matrix(), savedMatrix = new Matrix();
    private int NONE = 0, mode = NONE, share = 0;
    private PointF start = new PointF(), mid = new PointF();
    private float oldDist = 1f;
    private View imageTitle, topGradient;
    private Toolbar mToolbar;
    private String imageUrl;
    private WebView webView;

    @Override
    protected void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);

        setContentView(R.layout.activity_photo);

        mImageView = findViewById(R.id.container);
        mImageView.setOnTouchListener(this);
        topGradient = findViewById(R.id.photoViewerTopGradient);
        mToolbar = findViewById(R.id.toolbar_ph);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        imageTitle = findViewById(R.id.photo_title);
        ((TextView) imageTitle).setText(getIntent().getStringExtra("title"));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        webView = new WebView(this);
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= 19)
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.loadUrl(getIntent().getStringExtra("link"));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                imageUrl = url;
                Load();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int DRAG = 1, ZOOM = 2;

        if (!mToolbar.isShown()) {
            setVisibility(View.VISIBLE, android.R.anim.fade_in);
            setCountDown();
        }

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
        final float x = event.getX(0) - event.getX(1);
        final float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    } // https://stackoverflow.com/a/6650484 all the credits to Chirag Raval

    private void Load() {
        Glide.with(this)
                .load(imageUrl)
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
        switch (item.getItemId()) {
            case R.id.download_image:
                download = true;
                RequestStoragePermission();
                break;
            case R.id.share_image:
                share = 1;
                RequestStoragePermission();
                break;
            case R.id.oopy_url_image:
                final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clip = ClipData.newUri(this.getContentResolver(), "", Uri.parse(imageUrl));
                if (clipboard != null)
                    clipboard.setPrimaryClip(clip);
                CookingAToast.cooking(PhotoActivity.this, getString(R.string.content_copy_link_done), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_copy_url, true).show();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImg() {
        Glide.with(PhotoActivity.this).asBitmap().load(imageUrl).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(new Target<Bitmap>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {

            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                final String path = MediaStore.Images.Media.insertImage(getContentResolver(), resource, Uri.parse(imageUrl).getLastPathSegment(), null);
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.context_share_image)));
                CookingAToast.cooking(PhotoActivity.this, getString(R.string.context_share_image_progress), Color.WHITE, Color.parseColor("#00C851"), R.drawable.ic_share, false).show();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void getSize(@NonNull SizeReadyCallback cb) {

            }

            @Override
            public void removeCallback(@NonNull SizeReadyCallback cb) {

            }

            @Nullable
            @Override
            public Request getRequest() {
                return null;
            }

            @Override
            public void setRequest(@Nullable Request request) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {

            }

            @Override
            public void onDestroy() {

            }
        });
        share = 2;
    }

    private void RequestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (share == 1)
                shareImg();
            else if (download) {
                // Save the image
                final DownloadManager mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + "/MaterialFBook", System.currentTimeMillis() + ".jpg");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!(share == 2)) {
            if (mImageView != null)
                mImageView.setImageDrawable(null);
        }
        webView.clearCache(true);
        webView.clearHistory();
        webView.removeAllViews();
        webView.destroy();
    }

    private void setCountDown() {
        final CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
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
        final Animation a = AnimationUtils.loadAnimation(this, animation);

        topGradient.startAnimation(a);
        mToolbar.startAnimation(a);
        imageTitle.startAnimation(a);

        topGradient.setVisibility(visibility);
        mToolbar.setVisibility(visibility);
        imageTitle.setVisibility(visibility);
    }
}