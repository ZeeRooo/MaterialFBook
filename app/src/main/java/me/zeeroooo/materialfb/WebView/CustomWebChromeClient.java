package me.zeeroooo.materialfb.WebView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import com.greysonparrelli.permiso.Permiso;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.zeeroooo.materialfb.Activities.MainActivity;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.CookingAToast;

public class CustomWebChromeClient extends WebChromeClient {
    private final MainActivity mActivity;
    private final WebView mWebView;
    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;


    public CustomWebChromeClient(MainActivity activity, WebView webview) {
        mActivity = activity;
        mWebView = webview;
    }

    @Override
    public void onShowCustomView(View view,
                                 WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            onHideCustomView();
            return;
        }

        // 1. Stash the current state
        mCustomView = view;
        mOriginalSystemUiVisibility = mActivity.getWindow().getDecorView().getSystemUiVisibility();
        mOriginalOrientation = mActivity.getRequestedOrientation();

        // 2. Stash the custom view callback
        mCustomViewCallback = callback;

        // 3. Add the custom view to the view hierarchy
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.addView(mCustomView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


        // 4. Change the state of the window
        mActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onHideCustomView() {
        // 1. Remove the custom view
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.removeView(mCustomView);
        mCustomView = null;

        // 2. Restore the state to it's original form
        mActivity.getWindow().getDecorView()
                .setSystemUiVisibility(mOriginalSystemUiVisibility);
        mActivity.setRequestedOrientation(mOriginalOrientation);

        // 3. Call the custom view callback
        mCustomViewCallback.onCustomViewHidden();
        mCustomViewCallback = null;

    }
   // @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

        // Double check that we don't have any existing callbacks
        if(mActivity.mFilePathCallback != null) {
            mActivity.mFilePathCallback.onReceiveValue(null);
        }
        mActivity.mFilePathCallback = filePathCallback;

        // Set up the take picture intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mActivity.mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mActivity.mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        // Set up the intent to get an existing image
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        // Set up the intents for the Intent chooser
        Intent[] intentArray;
        if(takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        mActivity.startActivityForResult(chooserIntent, mActivity.INPUT_FILE_REQUEST_CODE);
        return true;
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }

    // openFileChooser for Android 3.0+
    void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){

        // Update message
        mActivity.mUploadMessage = uploadMsg;

        try{

            // Create AndroidExampleFolder at sdcard

            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");

            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }

            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            mActivity.mCapturedImageURI = Uri.fromFile(file);

            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mActivity.mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[] { captureIntent });

            // On select image call onActivityResult method of activity
            mActivity.startActivityForResult(chooserIntent, mActivity.FILECHOOSER_RESULTCODE);

        }
        catch(Exception e){

        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }
    public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
        Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
            @Override
            public void onPermissionResult(Permiso.ResultSet resultSet) {
                if (resultSet.areAllPermissionsGranted()) {
                    callback.invoke(origin, true, false);
                } else {
                    CookingAToast.cooking(mActivity, R.string.permission_denied, Color.WHITE, Color.parseColor("#ff4444"), R.drawable.ic_error, true).show();
                }
            }

            @Override
            public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                // TODO Permiso.getInstance().showRationaleInDialog("Title", "Message", null, callback);
                callback.onRationaleProvided();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION);
    }
    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (title.contains("Facebook")) {
            mActivity.setTitle(R.string.menu_most_recent);
        } else {
            mActivity.setTitle(title);
        }
    }
}