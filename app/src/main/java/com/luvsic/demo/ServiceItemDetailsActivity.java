package com.luvsic.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luvsic.demo.bean.AfterSaleRecord;
import com.rokid.glass.instruct.Integrate.InstructionActivity;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructConfig;
import com.rokid.glass.instruct.entity.InstructEntity;

import java.util.Random;

public class ServiceItemDetailsActivity extends InstructionActivity {

    private AfterSaleRecord afterSaleRecord = null;
    private Gson gson = null;
    private String itemType = null;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serviceitemdetails);
        next = findViewById(R.id.next);
        String recordBeanText = getIntent().getStringExtra("bean");
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
                .create();
        afterSaleRecord = gson.fromJson(recordBeanText, AfterSaleRecord.class);
        itemType = getIntent().getStringExtra("itemType");
        TextView tv = findViewById(R.id.tv_0);
        tv.setText(itemType);
        next.setOnClickListener(v -> {
            String[] l = {"维修点一", "维修点二", "维修点三"};
            toNextIntent(l[new Random().nextInt(3)]);
        });
    }

    @Override
    public InstructConfig configInstruct() {
        InstructConfig config = new InstructConfig();

        config.setActionKey(InstructActivity.class.getName() + InstructConfig.ACTION_SUFFIX)
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("维修点一", "wei xiu dian yi"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "check of diao long "))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "维修点一");
                                        toNextIntent("维修点一");
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("维修点二", "wei xiu dian er"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "check of long ding"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "维修点二");
                                        toNextIntent("维修点二");
                                    }
                                })
                )
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("维修点三", "wei xiu dian san"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "check of long ding"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "维修点三");
                                        toNextIntent("维修点三");
                                    }
                                })
                );
        return config;
    }

    private void toNextIntent(String point) {
        Intent it1 = new Intent(this, FaultDescriptionActivity.class);

        if (point.equals("维修点一")) {
            it1 = new Intent(this, PointDetailsActivity.class);
        }
        it1.putExtra("bean", gson.toJson(afterSaleRecord));
        it1.putExtra("problemNo", point);
        it1.putExtra("itemType", itemType);
        startActivity(it1);
    }

    @Override
    public boolean doReceiveCommand(String s) {
        return false;
    }
}