package com.luvsic.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import com.rokid.glass.ui.button.GlassButton;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;

/**
 * @author zyy
 * @since 2021/10/20 09:21
 */
public class SoundRecordActivity extends AppCompatActivity {
    MediaRecorder mr;
    boolean isStart = false;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundrecord);
        GlassButton btn = findViewById(R.id.btn_1);
        btn.setOnClickListener(v -> {
            if (!isStart) {
                //开始录制
                startRecord();
                btn.setText("停止录制");
                isStart = true;
            } else {
                stopRecord();
                btn.setText("开始录制");
                isStart = false;
            }
        });
        GlassButton btn2 = findViewById(R.id.btn_2);
        AVLoadingIndicatorView loadingIndicatorView = findViewById(R.id.loading);
        loadingIndicatorView.setIndicator("BallClipRotateMultipleIndicator");
//        loadingIndicatorView.setIndicator("CubeTransitionIndicator");


        btn2.setOnClickListener(v -> {
            System.out.println(flag);
            if (!flag) {
                loadingIndicatorView.show();
                flag = true;
            } else {
                loadingIndicatorView.hide();
                flag = false;
            }

        });
    }

    private boolean voicePermission() {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO));
    }

    String filename;
    File soundFile;

    private void startRecord() {
        System.out.println(voicePermission() + "-----------");
        if (!voicePermission())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);

        if (mr == null) {
//            File dir = new File(Environment.getExternalStorageDirectory(), "sounds");
//            System.out.println(Environment.getExternalStorageDirectory());
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            filename = System.currentTimeMillis() + ".wav";
//            soundFile = new File(dir, filename);

            soundFile = new File(getExternalCacheDir() + "/" + System.currentTimeMillis() + ".wav");
            if (!soundFile.exists()) {
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
            mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); //录音文件保存的格式  AAC_ADTS AMR_NB
            mr.setOutputFile(soundFile.getAbsolutePath());
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//  文件输出格式  default，AAC，HE_AAC，AAC_ELD，AMR_NB，AMR_WB，VORBIS
            mr.setAudioSamplingRate(16000);  //采样率16K
            mr.setAudioChannels(1);//比特率16bit
            mr.setAudioEncodingBitRate(AudioFormat.ENCODING_PCM_16BIT);//音频编码比特率
            try {
                mr.prepare();
                mr.start();  //开始录制
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //停止录制，资源释放
    private void stopRecord() {
        if (mr != null) {
            try {
                mr.stop();
            } catch (IllegalStateException e) {
                Log.i("error", e.getMessage());
                // TODO 如果当前java状态和jni里面的状态不一致，
                e.printStackTrace();
                mr = null;
                mr = new MediaRecorder();
            } finally {
                Log.i("error", "finally");
                if (mr != null) {
//                    mr.stop();
                    mr.reset();
                    mr.release();
                    mr = null;
                }
            }
        }
    }

}