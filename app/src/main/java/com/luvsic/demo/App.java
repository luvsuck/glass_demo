package com.luvsic.demo;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.rokid.glass.imusdk.IMUSdk;
import com.rokid.glass.instruct.VoiceInstruction;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;
import com.rokid.glass.ui.autosize.AutoSizeConfig;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //.addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        AutoSizeConfig.getInstance().setExcludeFontScale(true);
        IMUSdk.init(this);
        VoiceInstruction.init(this);


        VoiceInstruction.getInstance()
                .addGlobalInstruct(new InstructEntity()
                        .setGlobal(true)
                        .addEntityKey(new EntityKey("选择检查设施", "xuan ze jian cha she shi"))
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "choose device to support"))
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                try {
                                    if (act != null) {
                                        Log.e("选择检查设施", "全局指令1");
                                        act.finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                ).addGlobalInstruct(new InstructEntity()
                .setGlobal(true)
                .addEntityKey(new EntityKey("选择维修点", "xuan ze wei xiu dian"))
                .addEntityKey(new EntityKey(EntityKey.Language.en, "choose problem to support"))
                .setCallback(new IInstructReceiver() {
                    @Override
                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                        try {
                            if (act != null) {
                                Log.e("选择维修点", "全局指令2");
                                act.finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
    }
}