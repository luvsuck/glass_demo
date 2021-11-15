package com.luvsic.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.ui.toast.GlassToastUtil;

import java.io.File;

/**
 * @author zyy
 * @since 2021/10/14 11:54
 */
public class CameraActivity extends AppCompatActivity {
    private TextureView viewFinder;
    private InstructLifeManager mLifeManager;
    private static final String TAG = "Camera";
    private ImageCapture imageCapture;
    private String galleryPath = Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;

    public boolean checkPermission() {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
//        Manifest.permission.ACCESS_FINE_LOCATION;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        configInstruct();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

//        if (!checkPermission()) { }


        viewFinder = findViewById(R.id.view_finder);
        viewFinder.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                updateTransform();
            }
        });

        viewFinder.post(new Runnable() {
            @Override
            public void run() {
                startCamera();
            }
        });

    }

    private Preview getPreview() {
        // 1. preview
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(new Rational(1, 1))
                .setTargetResolution(new Size(1920, 1080))
                .build();
        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup) viewFinder.getParent();
                parent.removeView(viewFinder);
                parent.addView(viewFinder, 0);

                viewFinder.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });
        return preview;
    }

    public ImageCapture getCapture() {
        // 2. capture
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setTargetAspectRatio(new Rational(1, 1))
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .build();
        imageCapture = new ImageCapture(imageCaptureConfig);

        viewFinder.setOnClickListener(v -> {
            String fileName = System.currentTimeMillis() + ".jpg";
            File photo = new File(galleryPath + fileName);
//            File photo = new File(getExternalCacheDir() + "/" + System.currentTimeMillis() + ".jpg");
            imageCapture.takePicture(photo, new ImageCapture.OnImageSavedListener() {
                @Override
                public void onImageSaved(@NonNull File file) {
                    GlassToastUtil.showToast(CameraActivity.this, "已拍摄");
//                    try {

//                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));


//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    Log.d("cameraPath", file.getAbsolutePath());
                }

                @Override
                public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                    Log.d("cameraError", message);
                    cause.printStackTrace();
                }
            });
        });
        return imageCapture;
    }

    private ImageAnalysis getAnalyze() {
        // 3. analyze
        HandlerThread handlerThread = new HandlerThread("Analyze-thread");
        handlerThread.start();

        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder()
                .setCallbackHandler(new Handler(handlerThread.getLooper()))
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetAspectRatio(new Rational(2, 3))
//                .setTargetResolution(new Size(600, 600))
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
        imageAnalysis.setAnalyzer(new MyAnalyzer());
        return imageAnalysis;
    }

    private void startCamera() {
        CameraX.unbindAll();
        CameraX.bindToLifecycle(this, getPreview(), getCapture());

//        CameraX.bindToLifecycle(this, getPreview(), getCapture(), getAnalyze());
    }

    static class MyAnalyzer implements ImageAnalysis.Analyzer {

        @Override
        public void analyze(ImageProxy imageProxy, int rotationDegrees) {
            final Image image = imageProxy.getImage();
            if (image != null) {
                Log.d("contentWidth:", image.getWidth() + "," + image.getHeight());
//                    imageView.input(image);
            }
        }
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();
        // Compute the center of the view finder
        float centerX = viewFinder.getWidth() / 2f;
        float centerY = viewFinder.getHeight() / 2f;

        float[] rotations = {0, 90, 180, 270};
        // Correct preview output to account for display rotation
        float rotationDegrees = rotations[viewFinder.getDisplay().getRotation()];

        matrix.postRotate(-rotationDegrees, centerX, centerY);

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix);
    }


    private InstructLifeManager.IInstructLifeListener mInstructLifeListener = new InstructLifeManager.IInstructLifeListener() {
        @Override
        public boolean onInterceptCommand(String command) {
            if ("需要拦截的指令".equals(command)) {
                return true;
            }
            return false;
        }

        @Override
        public void onTipsUiReady() {
            Log.d("AudioAi", "onTipsUiReady Call ");
        }

        @Override
        public void onHelpLayerShow(boolean show) {

        }
    };


    public void configInstruct() {
        mLifeManager = new InstructLifeManager(this, getLifecycle(), mInstructLifeListener);
        mLifeManager.addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("全屏模式", "quan ping mo shi"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "fullscreen mode"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                Log.d(TAG, "全屏模式");
                            }
                        }))
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("缩小", "suo xiao"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "reduce"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d(TAG, "缩小");
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("拍照", "pai zhao"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "take photo"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (imageCapture != null) {
                                            File photo = new File(galleryPath + System.currentTimeMillis() + ".jpg");
                                            Log.d("filepath", galleryPath + System.currentTimeMillis() + ".jpg");
                                            imageCapture.takePicture(photo, new ImageCapture.OnImageSavedListener() {
                                                @Override
                                                public void onImageSaved(@NonNull File file) {
                                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                                                    GlassToastUtil.showToast(CameraActivity.this, "已拍摄");
                                                    Log.d("camera", file.getAbsolutePath());
                                                }

                                                @Override
                                                public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                                                    Log.d("fileError", photo.getAbsolutePath());
                                                    Log.d("cameraError", message);
                                                    cause.printStackTrace();
                                                }
                                            });
                                        } else {
                                        }
                                    }
                                })
                );
    }

}