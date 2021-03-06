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
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rokid.glass.instruct.Integrate.InstructionActivity;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructConfig;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.speech.AsrCallBack;
import com.rokid.glass.speech.SpeechUserManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
/**
 * @author zyy
 * @since 2021/10/14 17:24
 */
public class AfterSaleSummaryActivity extends InstructionActivity {
    private static final String TAG = "AfterSaleSummaryActivity-----";
    private TextView afterSaleDateView;
    private TextView largeView;
    private SpeechUserManager speechUserManager;
    private MediaRecorder mr;
    private AVLoadingIndicatorView loadingIndicatorView;
    private Button nextStepBtn;
    private CheckBox attachmentBox;
    private String filename;
    private File soundFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aftersalesummary);
        afterSaleDateView = findViewById(R.id.afterSaleDat);

        loadingIndicatorView = findViewById(R.id.loading);
        loadingIndicatorView.setIndicator("BallClipRotateMultipleIndicator");
    }

    @Override
    public InstructConfig configInstruct() {
        InstructConfig config = new InstructConfig();
        config.setActionKey(InstructActivity.class.getName() + InstructConfig.ACTION_SUFFIX)
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("????????????", "yu yin shu ru"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "input text"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "????????????");
                                        if (largeView != null) {
                                            largeView.setText("");
                                        }
                                        speechUserManager.doSpeechAsr(asrCallBack);


                                    }
                                })
                ).addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("????????????", "kai shi lu yin"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "start record audio"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                if (!voicePermission())
                                    ActivityCompat.requestPermissions(AfterSaleSummaryActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                                else {
                                    loadingIndicatorView.show();
                                    startRecord();
                                }
                            }
                        })
        ).addInstructEntity(
                new InstructEntity()
                        .addEntityKey(new EntityKey("????????????", "jie shu lu yin"))
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
                        .addEntityKey(new EntityKey("?????????", "xia yi bu"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "next step"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                startActivity(new Intent(AfterSaleSummaryActivity.this, TransactionRecordActivity.class));
                            }
                        })
        );
        return config;
    }

    @Override
    public boolean doReceiveCommand(String s) {
        return false;
    }

    private AsrCallBack asrCallBack = new AsrCallBack() {
//        String id;
//        int resCode;
//        String result;

        @Override
        public void onStart(int id) throws RemoteException {
            Log.i(TAG, id + " ??????");
        }

        @Override
        public void onIntermediateResult(int id, String s1, String s2) throws RemoteException {
            Log.i(TAG, id + " ????????????:[" + s1 + "," + s2 + "]");
        }

        @Override
        public void onAsrComplete(int id, String s) throws RemoteException {
            Log.i(TAG, id + " ????????????: " + s);
            if (s != null) {
                largeView.setText(s);
                largeView.clearComposingText();
            }
        }

        @Override
        public void onComplete(int id, String nlp, String action) {
            Log.i(TAG, id + " ????????????: [" + nlp + "," + action + "]");
        }

        @Override
        public void onCancel(int id) throws RemoteException {
            Log.i(TAG, id + "??????");
        }

        @Override
        public void onError(int id, int i1) throws RemoteException {
            Log.d(TAG, id + "?????? " + i1);
        }
    };

    private boolean voicePermission() {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO));
    }

    private void startRecord() {
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

            soundFile = new File(getExternalCacheDir() + "/" + "????????????_" + System.currentTimeMillis() + ".wav");
            if (!soundFile.exists()) {
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //???????????????
            mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); //???????????????????????????  AAC_ADTS AMR_NB
            mr.setOutputFile(soundFile.getAbsolutePath());
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//  ??????????????????  default???AAC???HE_AAC???AAC_ELD???AMR_NB???AMR_WB???VORBIS
            mr.setAudioSamplingRate(16000);  //?????????16K
            mr.setAudioChannels(1);//?????????16bit
            mr.setAudioEncodingBitRate(AudioFormat.ENCODING_PCM_16BIT);//?????????????????????
            try {
                mr.prepare();
                mr.start();  //????????????
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