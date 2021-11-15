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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luvsic.demo.bean.AfterSaleRecord;
import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.ui.toast.GlassToastUtil;

import java.io.File;
import java.util.StringJoiner;

/**
 * @author zyy
 * @since 2021/10/22 10:32
 */
public class AttachmentManagementActivity extends AppCompatActivity {
    private InstructLifeManager mLifeManager;
    private TextureView viewFinder;
    private static final String TAG = "AttachmentManagementActivity-------";
    private String imageName;
    private String galleryPath = Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;
    private ImageCapture imageCapture;
    private StringJoiner sj = new StringJoiner("|");
    private Gson gson = null;
    private AfterSaleRecord afterSaleRecord = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachmentmanagement);

        configInstruct();

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
                .create();

        String recordBeanText = getIntent().getStringExtra("bean");
        afterSaleRecord = gson.fromJson(recordBeanText, AfterSaleRecord.class);
        Log.e("附件管理中!!!!!", afterSaleRecord.toString());

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
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
                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .build();
        imageCapture = new ImageCapture(imageCaptureConfig);
        viewFinder.setOnClickListener(v -> {
            String fileName = "照片_" + System.currentTimeMillis() + ".jpg";
            File photo = new File(galleryPath + fileName);
            imageCapture.takePicture(photo, new ImageCapture.OnImageSavedListener() {
                @Override
                public void onImageSaved(@NonNull File file) {
                    GlassToastUtil.showToast(AttachmentManagementActivity.this, "已拍摄");
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
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
        imageAnalysis.setAnalyzer(new CameraActivity.MyAnalyzer());
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
        mLifeManager
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("拍照", "pai zhao"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "take photo"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (imageCapture != null) {
                                            imageName = "照片_" + System.currentTimeMillis() + ".jpg";
                                            File photo = new File(galleryPath + imageName);
                                            imageCapture.takePicture(photo, new ImageCapture.OnImageSavedListener() {
                                                @Override
                                                public void onImageSaved(@NonNull File file) {
                                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                                                    sj.add(file.getAbsolutePath());
                                                    GlassToastUtil.showToast(AttachmentManagementActivity.this, "已拍摄");
                                                    Log.d("cameraPath", file.getAbsolutePath());
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
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("查看相册", "cha kan xiang ce"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "view photos"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "查看相册");
                                        if (sj == null || sj.length() == 0) {
                                            GlassToastUtil.showToast(AttachmentManagementActivity.this, "请先拍照!");
                                            return;
                                        }

                                        if (imageName == null || "".equals(imageName.trim()))
                                            return;
                                        Intent it = new Intent(AttachmentManagementActivity.this, ImageListActivity.class);
                                        it.putExtra("imgUrls", sj.toString());
                                        it.putExtra("bean", gson.toJson(afterSaleRecord));
                                        startActivity(it);
                                    }
                                })
                );
    }

}