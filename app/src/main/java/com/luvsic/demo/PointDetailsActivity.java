package com.luvsic.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
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

public class PointDetailsActivity extends InstructionActivity {
    private static final String TAG = "PointDetailsActivity";
    private TextView heightTv;
    private TextView widthTv;
    private TextView differenceTv;
    private SpeechUserManager speechUserManager;
    int currentId;
    private String currentEvent;
    private Gson gson = null;
    private AfterSaleRecord afterSaleRecord = null;
    private String itemType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointdetails);

        String recordBeanText = getIntent().getStringExtra("bean");
        itemType = getIntent().getStringExtra("itemType");
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
                .create();
        afterSaleRecord = gson.fromJson(recordBeanText, AfterSaleRecord.class);
        String problemNo = getIntent().getStringExtra("problemNo");
        TextView tv = findViewById(R.id.title);
        tv.setText(problemNo);
        heightTv = findViewById(R.id.height);
        widthTv = findViewById(R.id.width);
        differenceTv = findViewById(R.id.difference);
        speechUserManager = new SpeechUserManager(PointDetailsActivity.this, true);

    }

    @Override
    public InstructConfig configInstruct() {
        InstructConfig config = new InstructConfig();
        config.setActionKey(InstructActivity.class.getName() + InstructConfig.ACTION_SUFFIX)
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("高度信息", "gao du xin xi"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "input height"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "输入高度");
                                        speechUserManager.cancelAsr(currentId);
                                        currentEvent = "height";
                                        speechUserManager.doSpeechAsr(asrCallBack);
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("宽度信息", "kuan du xin xi"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "width msg"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "宽度信息");
                                        speechUserManager.cancelAsr(currentId);
                                        currentEvent = "width";
                                        speechUserManager.doSpeechAsr(asrCallBack);
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("误差信息", "wu cha xin xi"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "input difference"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "输入误差");
                                        speechUserManager.cancelAsr(currentId);
                                        currentEvent = "difference";
                                        speechUserManager.doSpeechAsr(asrCallBack);
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("下一步", "xia yi bu"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "next step"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "下一步");
                                        toNextIntent();
                                    }
                                })
                );
        return config;
    }

    private void toNextIntent() {
        Intent it1 = new Intent(this, FaultDescriptionActivity.class);
        it1.putExtra("bean", gson.toJson(afterSaleRecord));
        it1.putExtra("problemNo", "维修点一");
        it1.putExtra("itemType", itemType);
        startActivity(it1);
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
            currentId = id;
            Log.i(TAG, id + " 启动");
        }

        @Override
        public void onIntermediateResult(int id, String s1, String s2) throws RemoteException {
            Log.i(TAG, id + " 中间结果:[" + s1 + "," + s2 + "]");
        }

        @Override
        public void onAsrComplete(int id, String s) throws RemoteException {
            Log.i(TAG, id + " 完整结果: " + s);
            if (s != null) {
                updateUi(id, s, currentEvent);
            }
        }

        @Override
        public void onComplete(int id, String nlp, String action) {
            Log.i(TAG, id + " 保留结果: [" + nlp + "," + action + "]");
        }

        @Override
        public void onCancel(int id) throws RemoteException {
            Log.i(TAG, id + "取消");
        }

        @Override
        public void onError(int id, int i1) throws RemoteException {
            Log.d(TAG, id + "报错 " + i1);
        }
    };

    private void updateUi(int id, String s, String e) {
        Log.e("uui:::", e + ":" + s);
        switch (e) {
            case "width":
                widthTv.setText(s);
                break;
            case "height":
                heightTv.setText(s);
                break;
            case "difference":
                differenceTv.setText(s);
                break;
        }
    }

}