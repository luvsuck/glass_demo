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
import android.os.Environment;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
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
import com.rokid.glass.speech.AsrCallBack;
import com.rokid.glass.speech.SpeechUserManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zyy
 * @since 2021/10/13 14:50
 */
public class FaultDescriptionActivity extends InstructionActivity {
    private static final String TAG = "FaultDescriptionActivity-----";
    private TextView largeView;
    private SpeechUserManager speechUserManager;
    private TextView checkItem;
    private MediaRecorder mr;
    private AVLoadingIndicatorView loadingIndicatorView;
    private Button nextStepBtn;
    private CheckBox attachmentBox;
    private File soundFile = null;
    private Gson gson = null;
    private AfterSaleRecord afterSaleRecord = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faultdescription);

        Map<String, List<String>> subItem = new HashMap<>();
        List<String> dtList = Arrays.asList("????????????", "????????????", "????????????");
        subItem.put("????????????", dtList);
        subItem.put("????????????", dtList);

        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;

        speechUserManager = new SpeechUserManager(FaultDescriptionActivity.this, true);
        loadingIndicatorView = findViewById(R.id.loading);
        loadingIndicatorView.setIndicator("BallClipRotateMultipleIndicator");

        attachmentBox = findViewById(R.id.attachmentBox);

        String recordBeanText = getIntent().getStringExtra("bean");

        checkItem = findViewById(R.id.check_item);
        String itemType = getIntent().getStringExtra("itemType");
        String problemNo = getIntent().getStringExtra("problemNo");
        TextView pn = findViewById(R.id.check_problem);
        pn.setText(problemNo);
        checkItem.setText(String.format("%s %s", checkItem.getText(), itemType));

        subItem.get(itemType);

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
                .create();
        afterSaleRecord = gson.fromJson(recordBeanText, AfterSaleRecord.class);
        String clientName = afterSaleRecord.getClientName();

        TextView client = findViewById(R.id.p_client);
        client.setText(String.format("%s%s", client.getText(), clientName));

//        String str = "?????????2013???3????????????????????????10??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????11???????????????????????????????????????????????????????????????????????????????????????";

        largeView = findViewById(R.id.tv_large);
        largeView.setMovementMethod(ScrollingMovementMethod.getInstance());
//        largeView.setText(str);
        largeView.clearComposingText();

        nextStepBtn = findViewById(R.id.nextStep);
        nextStepBtn.setOnClickListener(v -> {
            Intent it1 = new Intent(this, TransactionRecordActivity.class);
            it1.putExtra("bean", gson.toJson(afterSaleRecord));
            startActivity(it1);
        });

    }

    @Override
    public InstructConfig configInstruct() {
        InstructConfig config = new InstructConfig();

        config.setActionKey(InstructActivity.class.getName() + InstructConfig.ACTION_SUFFIX)
//                .addInstructEntity(
//                        new InstructEntity()
//                                .addEntityKey(new EntityKey("????????????", "qing kong shuo ming"))
//                                .addEntityKey(new EntityKey(EntityKey.Language.en, "clear illustrate"))
//                                .setShowTips(true)
//                                .setCallback(new IInstructReceiver() {
//                                    @Override
//                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
//                                        Log.d("instruct", "????????????");
//                                        if (largeView != null) {
//                                            largeView.setText("");
//                                        }
//                                    }
//                                })
//                )
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
                                    ActivityCompat.requestPermissions(FaultDescriptionActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
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
                                Intent it1 = new Intent(FaultDescriptionActivity.this, TransactionRecordActivity.class);
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
                afterSaleRecord.setFaultDescription(s);
                Log.e("?????????::1!!!!!!!!", afterSaleRecord.toString());
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

            soundFile = new File(getExternalCacheDir() + "/" + "????????????_" + System.currentTimeMillis() + ".wav");
            Log.i("soundfilepath!!!", soundFile.getAbsolutePath());

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
                //String filePath="/storage/emulated/0/Android/data/com.luvsic.jkdemo/cache/????????????_1635328173799.wav";
                String filePath = soundFile.getAbsolutePath();
                afterSaleRecord.setFaultDescriptionAtt(filePath);
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