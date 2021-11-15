package com.luvsic.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luvsic.demo.bean.AntiCounterfeitingData;
import com.luvsic.demo.fragment.ContainerActivity;
import com.rokid.glass.speech.AsrCallBack;
import com.rokid.glass.speech.SpeechUserManager;
import com.rokid.glass.ui.toast.GlassToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.List;

import okhttp3.Call;

/**
 * @author zyy
 * @since 2021/10/09 13:13
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "luvTest " + MainActivity.class.getName();
    private String host = null;

    private SpeechUserManager speechUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        host = this.getString(R.string.host_address);

        speechUserManager = new SpeechUserManager(this, true);

        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_10).setOnClickListener(this);
        findViewById(R.id.btn_11).setOnClickListener(this);
        findViewById(R.id.btn_12).setOnClickListener(this);

        findViewById(R.id.btn_4).setFocusable(true);
        findViewById(R.id.btn_4).setFocusableInTouchMode(true);
        findViewById(R.id.btn_4).requestFocus();
        findViewById(R.id.btn_4).requestFocusFromTouch();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            String url = "http://jkjxjc.com/kj/j.aspx?p=DE669C939B";
            if (result != null && result.startsWith("http://jkjxjc.com/") && result.contains("?p=")) {
                String code = result.split("p=")[1];
                Log.d("scan getP:", result);
                Intent it = new Intent(this, TaskHeaderActivity.class);
                it.putExtra("code", code);
                startActivity(it);
            } else {
                GlassToastUtil.showToast(this, "二维码未包含必要信息!");
            }

        } else
            GlassToastUtil.showToast(this, "intent为null!");
    }

    private AsrCallBack asrCallBack = new AsrCallBack() {
//        String id;
//        int resCode;
//        String result;

        @Override
        public void onStart(int id) throws RemoteException {
            Log.i(TAG, id + " 启动");
        }

        @Override
        public void onIntermediateResult(int id, String s1, String s2) throws RemoteException {
            Log.i(TAG, id + " 中间结果:[" + s1 + "," + s2 + "]");
        }

        @Override
        public void onAsrComplete(int id, String s) throws RemoteException {
            Log.i(TAG, id + " 完整结果: " + s);
        }

        @Override
        public void onComplete(int id, String s, String s1) throws RemoteException {
            Log.i(TAG, id + " 保留结果: [" + s + "," + s1 + "]");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
//                Intent intent = new Intent();
//                ComponentName comp = new ComponentName("com.rokid.glass.scan2", "com.rokid.glass.scan2.activity.QrCodeActivity");
//                intent.putExtra("scan_model", "wifi_connect");
//                intent.setComponent(comp);
//                startActivityForResult(intent, 101);
                break;
            case R.id.btn_2:
                speechUserManager.doSpeechAsr(asrCallBack);
                break;
            case R.id.btn_3:
                startActivity(new Intent(this, IMUActivity.class));
                break;
            case R.id.btn_4:
//                Intent intent1 = new Intent();
//                ComponentName comp1 = new ComponentName("com.rokid.glass.scan2", "com.rokid.glass.scan2.activity.QrCodeActivity");
//                intent1.putExtra("scan_model", "wifi_connect");
//                intent1.putExtra("scan_by", "scanByTask");
//                intent1.setComponent(comp1);
//                startActivityForResult(intent1, 101);

                String code = "DE669C939B";
                Intent it = new Intent(this, TaskHeaderActivity.class);
                it.putExtra("code", code);
                startActivity(it);
                break;
            case R.id.btn_5:
//                String sql = "select top 100 * from [防伪数据表_主表]";
//                new Thread(() -> {
//                    String ans = DBUtil.QuerySQL(sql, new String[]{});
//                    Log.d("sql", ans);
//                }).start();
                break;
            case R.id.btn_6:
                startActivity(new Intent(this, InstructActivity.class));
                break;
            case R.id.btn_7:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, CameraActivity.class));
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
                }
                break;
            case R.id.btn_8:
                startActivity(new Intent(this, SoundRecordActivity.class));
                break;
            case R.id.btn_9:
//                new Thread(this::testHttp).start();
                testHttp();
                break;
            case R.id.btn_10:
                startActivity(new Intent(this, UploadActivity.class));
//                String urlPath = "";
//                uploadFile(urlPath);
                break;
            case R.id.btn_11:
                startActivity(new Intent(this, PointDetailsActivity.class));
                break;
            case R.id.btn_12:
                startActivity(new Intent(this, ContainerActivity.class));
                break;
        }

    }

    private void uploadFile(String url) {
        File file = new File(url);
        OkHttpUtils
                .postFile()
                .url(url)
                .file(file)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {

                    }
                });
    }

    private void testHttp() {
        String urlStr = host + "get";
        OkHttpUtils
                .get()
                .url(urlStr)
//                .addParams("username", "hyman")
//                .addParams("password", "123")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        List<AntiCounterfeitingData> fw = new Gson().fromJson(s, new TypeToken<List<AntiCounterfeitingData>>() {
                        }.getType());
                        fw.forEach(System.out::println);
                    }
                });

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(2, TimeUnit.MINUTES)
//                .readTimeout(2, TimeUnit.MINUTES)
//                .build();
//        Request request = new Request.Builder()
//                .url(urlStr)
//                .build();
//        Response response = null;
//        String responseStr = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            responseStr = response.body().string();
//            System.out.println(responseStr);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

