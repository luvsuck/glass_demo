package com.luvsic.demo;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luvsic.demo.bean.AfterSaleRecord;
import com.rokid.glass.instruct.Integrate.InstructionActivity;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructConfig;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;

/**
 * @author zyy
 * @since 2021/10/20 14:55
 */
public class TransactionRecordActivity extends InstructionActivity {
    private TextView largeView;
    private AVLoadingIndicatorView loadingIndicatorView;
    private MediaRecorder mr;
    private File soundFile = null;
    private Gson gson = null;
    private AfterSaleRecord afterSaleRecord = null;
    private CheckBox attachmentBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactionrecord);

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
                .create();

        String recordBeanText = getIntent().getStringExtra("bean");
        afterSaleRecord = gson.fromJson(recordBeanText, AfterSaleRecord.class);

        loadingIndicatorView = findViewById(R.id.loading);
        loadingIndicatorView.setIndicator("BallClipRotateMultipleIndicator");

        attachmentBox = findViewById(R.id.attachmentBox);

        largeView = findViewById(R.id.tv_large);
        largeView.setText("当前暂无内容，请使用录音完成录制。");
        largeView.setGravity(Gravity.CENTER);
    }

    public InstructConfig configInstruct() {
        InstructConfig config = new InstructConfig();
        config.setActionKey(InstructActivity.class.getName() + InstructConfig.ACTION_SUFFIX)
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("开始录音", "kai shi lu yin"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "start record audio"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        if (!voicePermission())
                                            ActivityCompat.requestPermissions(TransactionRecordActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                                        else {
                                            loadingIndicatorView.show();
                                            startRecord();
                                        }
                                    }
                                })
                ).addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("结束录音", "jie shu lu yin"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "stop record"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                stopRecord();
                                loadingIndicatorView.hide();
                            }
                        })
        ).addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("下一步", "xia yi bu"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "next step"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                Intent it1 = new Intent(TransactionRecordActivity.this, AttachmentManagementActivity.class);
                                it1.putExtra("bean", gson.toJson(afterSaleRecord));
                                startActivity(it1);
                            }
                        })
        );
        return config;
    }


    @Override
    public boolean doReceiveCommand(String s) {
        return false;
    }

    private boolean voicePermission() {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO));
    }

    private void startRecord() {
        System.out.println(voicePermission() + "-----------");
        if (!voicePermission())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);

        if (mr == null) {
            soundFile = new File(getExternalCacheDir() + "/" + "处理记录_" + System.currentTimeMillis() + ".wav");
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


    private void stopRecord() {
        if (mr != null) {
            try {
                mr.stop();
                attachmentBox.setChecked(true);
                String filePath = soundFile.getAbsolutePath();
                afterSaleRecord.setTransactionRecordAtt(filePath);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mr.reset();
                mr.release();
                mr = null;
            }
        }
    }
}