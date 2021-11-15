package com.luvsic.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luvsic.demo.bean.AfterSaleRecord;
import com.rokid.glass.instruct.Integrate.InstructionActivity;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructConfig;
import com.rokid.glass.instruct.entity.InstructEntity;

/**
 * @author zyy
 * @since 2021/10/28 10:30
 */
public class ServiceItemActivity extends InstructionActivity {
    private AfterSaleRecord afterSaleRecord = null;
    private Gson gson = null;
    String clientName = null;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceitem);

//
        next = findViewById(R.id.next);
        String recordBeanText = getIntent().getStringExtra("bean");
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
                .create();
        afterSaleRecord = gson.fromJson(recordBeanText, AfterSaleRecord.class);
        clientName = afterSaleRecord.getClientName();
        String dev[] = {"笼顶检查", "吊笼检查"};
        next.setOnClickListener(v -> {
            toNextIntent(clientName, dev[Math.random() > 0.5 ? 1 : 0]);
        });
    }

    @Override
    public InstructConfig configInstruct() {
        InstructConfig config = new InstructConfig();

        config.setActionKey(InstructActivity.class.getName() + InstructConfig.ACTION_SUFFIX)
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("吊笼检查", "diao long jian cha"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "check of diao long "))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "吊笼检查");
                                        toNextIntent(clientName, "吊笼检查");
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("笼顶检查", "long ding jian cha"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "check of long ding"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "笼顶检查");
                                        toNextIntent(clientName, "笼顶检查");
                                    }
                                })
                );
        return config;
    }

    @Override
    public boolean doReceiveCommand(String s) {
        return false;
    }

    public void toNextIntent(String param, String itemType) {
        Intent it1 = new Intent(this, ServiceItemDetailsActivity.class);
        it1.putExtra("client", param);
        it1.putExtra("bean", gson.toJson(afterSaleRecord));
        it1.putExtra("itemType", itemType);
        startActivity(it1);

    }
}