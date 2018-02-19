package com.softonic.instamaterial.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
/*import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;*/
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.softonic.instamaterial.R;
import com.softonic.instamaterial.ui.activity.publish.PublishActivity;
import com.softonic.instamaterial.ui.utils.Utils;
import com.softonic.instamaterial.ui.view.RevealBackgroundView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TakePhotoActivity extends BaseActivity
    implements RevealBackgroundView.OnStateChangeListener
        //,CameraHostProvider
{
  private static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

  private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
  private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

  @BindView(R.id.vRevealBackground)
  RevealBackgroundView vRevealBackground;
  @BindView(R.id.vPhotoRoot)
  View vTakePhotoRoot;
  @BindView(R.id.vShutter)
  View vShutter;
  @BindView(R.id.ivTakenPhoto)
  ImageView ivTakenPhoto;
  @BindView(R.id.vUpperPanel)
  View vUpperPanel;
  @BindView(R.id.vLowerPanel)
  View vLowerPanel;
  @BindView(R.id.cameraView)
  CameraView cameraView;
  @BindView(R.id.btnTakePhoto)
  Button btnTakePhoto;
  FileOutputStream fos;
  public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
    Intent intent = new Intent(startingActivity, TakePhotoActivity.class);
    intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
    startingActivity.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_take_photo);
    updateStatusBarColor();
    setupRevealBackground(savedInstanceState);

    cameraView.addCameraListener(new CameraListener() {
      @Override
      public void onPictureTaken(byte[] picture) {
        // Create a bitmap or a file...
        // CameraUtils will read EXIF orientation for you, in a worker thread.
        CameraUtils.decodeBitmap(picture, new CameraUtils.BitmapCallback() {
          @Override
          public void onBitmapReady(Bitmap bitmap) {
            showTakenPicture(bitmap);

            //create a file to write bitmap data

            publishTakenPicture(saveImageToExternalStorage(bitmap));
          }
        });
      }
    });
  }

  private File saveImageToExternalStorage(Bitmap finalBitmap) {
    String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
    File myDir = new File(root + "/saved_images_1");
    myDir.mkdirs();
    Random generator = new Random();
    int n = 10000;
    n = generator.nextInt(n);
    String fname = "Image-" + n + ".jpg";
    File file = new File(myDir, fname);
    if (file.exists())
      file.delete();
    try {
      FileOutputStream out = new FileOutputStream(file);
      finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
      out.flush();
      out.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }


    // Tell the media scanner about the new file so that it is
    // immediately available to the user.
    MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
            new MediaScannerConnection.OnScanCompletedListener() {
              public void onScanCompleted(String path, Uri uri) {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
              }
            });
    return file;
  }


  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void updateStatusBarColor() {
    if (Utils.isAndroid5()) {
      getWindow().setStatusBarColor(0xff111111);
    }
  }

  private void setupRevealBackground(Bundle savedInstanceState) {
    vRevealBackground.setFillPaintColor(0xFF16181a);
    vRevealBackground.setOnStateChangeListener(this);
    if (savedInstanceState == null) {
      final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
      vRevealBackground.getViewTreeObserver()
          .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
              vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
              vRevealBackground.startFromLocation(startingLocation);
              return true;
            }
          });
    } else {
      vRevealBackground.setToFinishedFrame();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    cameraView.start();
    btnTakePhoto.setEnabled(true);
  }

  @Override
  protected void onPause() {
    super.onPause();
    cameraView.stop();
  }

  @OnClick(R.id.btnTakePhoto)
  public void onTakePhotoClick() {
    btnTakePhoto.setEnabled(false);
    //cameraView.takePicture(true, true);
    cameraView.captureSnapshot();
    animateShutter();
  }

  @OnClick(R.id.btnCancel)
  public void onCancelClick() {
    finish();
  }

  private void animateShutter() {
    vShutter.setVisibility(View.VISIBLE);
    vShutter.setAlpha(0.f);

    ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
    alphaInAnim.setDuration(100);
    alphaInAnim.setStartDelay(100);
    alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

    ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
    alphaOutAnim.setDuration(200);
    alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        vShutter.setVisibility(View.GONE);
      }
    });
    animatorSet.start();
  }

  @Override
  public void onStateChange(int state) {
    if (RevealBackgroundView.STATE_FINISHED == state) {
      vTakePhotoRoot.setVisibility(View.VISIBLE);
      startIntroAnimation();
    } else {
      vTakePhotoRoot.setVisibility(View.INVISIBLE);
    }
  }

  private void startIntroAnimation() {
    vUpperPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
    vLowerPanel.animate()
        .translationY(0)
        .setDuration(400)
        .setInterpolator(DECELERATE_INTERPOLATOR)
        .start();
  }

 /* @Override
  public CameraHost getCameraHost() {
    return new MyCameraHost(this);
  }*/

/*  class MyCameraHost extends SimpleCameraHost {

    private Camera.Size previewSize;

    public MyCameraHost(Context context) {
      super(context);
    }

    @Override
    public boolean useFullBleedPreview() {
      return true;
    }

    @Override
    public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
      return previewSize;
    }

    @Override
    public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
      Camera.Parameters parameters1 = super.adjustPreviewParameters(parameters);
      previewSize = parameters1.getPreviewSize();
      return parameters1;
    }

    @Override
    public void saveImage(PictureTransaction xact, final Bitmap bitmap) {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          showTakenPicture(bitmap);
        }
      });
    }

    @Override
    public void saveImage(PictureTransaction xact, byte[] image) {
      super.saveImage(xact, image);
      publishTakenPicture(getPhotoPath());
    }

    @Override protected String getPhotoFilename() {
      return super.getPhotoFilename();
    }
  }*/
  private void showTakenPicture(Bitmap bitmap) {
    ivTakenPhoto.setImageBitmap(bitmap);
  }

  private void publishTakenPicture(File photoPath) {
    PublishActivity.openWithPhotoUri(this, Uri.fromFile(photoPath));
  }
}
