package com.luvsic.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.rokid.glass.instruct.Integrate.InstructionActivity;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructConfig;
import com.rokid.glass.instruct.entity.InstructEntity;
/**
 * @author zyy
 * @since 2021/10/13 16:23
 */
public class InstructActivity extends InstructionActivity {
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruct);


    }

    @Override
    public boolean doReceiveCommand(String s) {
        return false;
    }

    // 添加指令
    @Override
    public InstructConfig configInstruct() {
        InstructConfig config = new InstructConfig();
        config.setActionKey(InstructActivity.class.getName() + InstructConfig.ACTION_SUFFIX)
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("测试三", "ce shi san"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "last one"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        // 指令处理回调
                                        Log.d("instruct", "测试3");
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("测试一", "ce shi yi"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "test one"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        // 指令处理回调
//                                        doNext();
                                        Log.d("instruct", "测试一");
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("测试二", "ce shi er "))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "test two"))
                                .setShowTips(true)
                                .setIgnoreHelp(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        // 指令处理回调
//                                        openVideo();
                                        Log.d("instruct", "测试2");
                                    }
                                })
                );

        return config;
    }
}