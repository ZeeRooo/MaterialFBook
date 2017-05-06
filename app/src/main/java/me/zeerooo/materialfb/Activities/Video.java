package me.zeerooo.materialfb.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.VideoView;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import me.zeerooo.materialfb.R;
import me.zeerooo.materialfb.Ui.CookingAToast;

public class Video extends AppCompatActivity {

    private VideoView mVideoView;
    private int position = 0;
    private DownloadManager mDownloadManager;
    private RelativeLayout mButtonsHeader;
    private AppCompatSeekBar mSeekbar;
    private String url;
    private AppCompatTextView mElapsedTime, mRemainingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        url = getIntent().getStringExtra("video_url");

        mVideoView = (VideoView) findViewById(R.id.video_view);
        mButtonsHeader = (RelativeLayout) findViewById(R.id.buttons_header);
        mSeekbar = (AppCompatSeekBar) findViewById(R.id.progress);
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        mElapsedTime = (AppCompatTextView) findViewById(R.id.elapsed_time);
        mRemainingTime = (AppCompatTextView) findViewById(R.id.remaining_time);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSeekbar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mSeekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        mVideoView.setVideoURI(Uri.parse(url));

        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                mVideoView.seekTo(position);
                mSeekbar.setMax(mVideoView.getDuration());
                mSeekbar.postDelayed(Update, 1000);
                mElapsedTime.postDelayed(Update, 1000);
                mRemainingTime.postDelayed(Update, 1000);
                mButtonsHeader.setVisibility(View.GONE);
                if (position == 0)
                    mVideoView.start();
            }
        });

        // Buttons
        final AppCompatImageButton pause = (AppCompatImageButton) findViewById(R.id.pauseplay_btn);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    ((ImageButton) v).setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mVideoView.start();
                    ((ImageButton) v).setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        final AppCompatImageButton previous = (AppCompatImageButton) findViewById(R.id.previous_btn);
        previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mVideoView.seekTo(0);
                mSeekbar.setProgress(0);
            }
        });

        final AppCompatImageButton download = (AppCompatImageButton) findViewById(R.id.download_btn);
        download.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RequestStoragePermission();
            }
        });

        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mButtonsHeader.setVisibility(View.GONE);
                    }
                }, 3000);
                mButtonsHeader.setVisibility(View.VISIBLE);
                return false;
            }
        });

        final AppCompatImageButton share = (AppCompatImageButton) findViewById(R.id.share_btn);
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.context_share_link)));
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mVideoView.seekTo(progress);
            }
        });
    }

    private final Runnable Update = new Runnable() {
        @Override
        public void run() {
            if(mSeekbar != null) {
                mSeekbar.setProgress(mVideoView.getCurrentPosition());
            }
            if(mVideoView.isPlaying()) {
                mSeekbar.postDelayed(Update, 1000);
                mElapsedTime.setText(Time(mVideoView.getCurrentPosition()));
                mRemainingTime.setText(Time(mVideoView.getDuration() - mVideoView.getCurrentPosition()));
            }
        }};

    private String Time(long ms) {
        return String.format(Locale.getDefault(), "%d:%d", TimeUnit.MILLISECONDS.toMinutes(ms), TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((ms))));
    }

    private void RequestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    // Set the download directory
                    File downloads_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
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
}
