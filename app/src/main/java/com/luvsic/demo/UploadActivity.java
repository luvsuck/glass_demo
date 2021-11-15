package com.luvsic.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Button;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;

/**
 * @author zyy
 * @since 2021/10/26 13:09
 */
public class UploadActivity extends AppCompatActivity {
    String path;
    private String host = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        host = this.getString(R.string.host_address);
        checkPermissions();
        Button chooseBtn = findViewById(R.id.choose);
        chooseBtn.setOnClickListener(v -> chooseFile());
        checkPermissions();
        findViewById(R.id.upload).setOnClickListener(v -> uploadFiles("/sdcard/Download/a.jpg", "22.jpg"));
    }

    private void checkPermissions() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if ((ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED)
                ||
                (ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(this, permissions, 100);
        }

    }

    private void chooseFile() {
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.setType("*/*");
        it.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(it, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File dir = getExternalFilesDir(null);
            if (dir != null) {
                path = dir.toString().subSequence(0, dir.toString().indexOf("0") + 2) +
                        DocumentsContract.getDocumentId(uri).split(":")[1];
            }
        }
    }

    public boolean uploadFiles(String path, String fileName) {
        String url = host + "upload";
        File file = new File(path);
        OkHttpUtils.post()
                .url(url)
                .addParams("filename", fileName)
                .addParams("uid", "")
                .addFile("file", "1a.jpg", file)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        Log.e("uploadFile", e.getMessage());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        System.out.println(s);
                        System.out.println(i);
                        Log.d("uploadFile", s);
                    }
                });
        return false;
    }
}