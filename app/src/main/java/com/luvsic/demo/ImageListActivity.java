package com.luvsic.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luvsic.demo.adapter.MyIMUAdapter;
import com.luvsic.demo.bean.AfterSaleRecord;
import com.luvsic.demo.bean.ImageEntity;
import com.rokid.glass.imusdk.core.IMUView;
import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import okhttp3.Call;

/**
 * @author zyy
 * @since 2021/10/22 12:01
 */
public class ImageListActivity extends AppCompatActivity {
    private IMUView mImuView;
    private List<ImageEntity> mItems;
    private MyIMUAdapter mAdapter;
    private InstructLifeManager mLifeManager;
    private String host = null;
    private AVLoadingIndicatorView loadingIndicatorView;
    private AfterSaleRecord afterSaleRecord = null;
    private Gson gson = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagelist);

//        loadingIndicatorView = findViewById(R.id.loading);
//        loadingIndicatorView.setIndicator("BallClipRotateMultipleIndicator");

        host = this.getString(R.string.host_address);
        configInstruct();


        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
                .create();

        String recordBeanText = getIntent().getStringExtra("bean");
        afterSaleRecord = gson.fromJson(recordBeanText, AfterSaleRecord.class);

        String urls = getIntent().getStringExtra("imgUrls");
//        List<String> ll = Arrays.stream(urls.split("\\|")).map(p -> p.split("emulated/0/DCIM/Camera/")[1]).collect(Collectors.toList());


        initViews();
        initData(urls);
    }

    private void initViews() {
        mImuView = findViewById(R.id.ui_recycler_view);
        getLifecycle().addObserver(mImuView);

        mAdapter = new MyIMUAdapter();
        mImuView.setAdapter(mAdapter);
        mImuView.setSlow();
    }

    private void initData(String urls) {
        mItems = new ArrayList<>();

        Arrays.stream(urls.split("\\|")).forEach(p -> {
            String name = p.split("emulated/0/DCIM/Camera/")[1];
            mItems.add(new ImageEntity(p, name, ""));
        });

        mAdapter.setData(mItems);
    }

    public void enterItem(ImageEntity data) {
        ComponentName component = new ComponentName("com.rokid.glass.document2",
                "com.rokid.glass.document2.activity.MainActivity");
        Intent intent = new Intent();
        intent.setComponent(component);
        intent.putExtra("file_name", data.getTitle());
        intent.putExtra("file_path_floder", data.getImageUrl().split(data.getTitle())[0]);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                Log.d("position", "current pos  =" + mAdapter.getCurrentPosition() + "    keyCode = " + keyCode);
                ImageEntity data = mAdapter.getItem(mAdapter.getCurrentPosition());
                if (null == data) {
                    return super.onKeyUp(keyCode, event);
                }
                enterItem(data);
                break;

        }
        return super.onKeyDown(keyCode, event);
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
                                .addEntityKey(new EntityKey("保存附件", "bao cun fu jian"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "save photos"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.e("saving", "正在保存");
                                        StringJoiner sj = uploadFiles();
                                        Log.e("join", sj.toString());
                                        Log.e("saved???", mItems.toString());
                                        Log.e("saving", "保存结束");
                                    }
                                })
                );
    }

    public StringJoiner uploadFiles() {
        String url = host + "upload";
        StringJoiner sj = new StringJoiner(",");

        mItems.forEach(i -> {
            String urlPath = i.getImageUrl();
            String fileName = i.getTitle();
            File file = new File(urlPath);
            OkHttpUtils.post()
                    .url(url)
                    .addParams("filename", fileName)
                    .addFile("file", fileName, file)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            Log.e("uploadFile", e.getMessage());
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            if (s != null) {
                                sj.add(s);
                            }
                        }
                    });
        });
        return sj;
    }
}