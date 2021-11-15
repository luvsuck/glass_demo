package com.luvsic.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.rokid.glass.ui.toast.GlassToastUtil;
import com.wang.avi.AVLoadingIndicatorView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * @author zyy
 * @since 2021-10-13 13:17
 */
public class TaskHeaderActivity extends InstructionActivity {
    private String host = null;
    private String recordBeanText = null;
    private Gson gson = null;

    //    private List<AfterServiceHeader> list;
    private AVLoadingIndicatorView loadingIndicatorView;
    private String 客户名称;


    public void updateUi(AfterSaleRecord record) {

        TextView clientName = findViewById(R.id.p_client);
        TextView clientAddress = findViewById(R.id.p_clientAddress);
        TextView manufactureDate = findViewById(R.id.p_manufactureDate);
        TextView deviceId = findViewById(R.id.p_deviceId);
        TextView deviceSpec = findViewById(R.id.p_deviceSpec);

        String 安全器型号 = record.getDeviceSpec();
        String 安全器编号 = record.getDeviceNo();
        客户名称 = record.getClientName();
        String 生产日期 = record.getManuDate();
        String 防伪码 = record.getAntiCode();

        clientName.setText(String.format("%s %s", clientName.getText(), 客户名称));
        manufactureDate.setText(String.format("%s %s", manufactureDate.getText(), 生产日期));
        deviceId.setText(String.format("%s %s", deviceId.getText(), 安全器编号));
        deviceSpec.setText(String.format("%s %s", deviceId.getText(), 安全器型号));

        CheckBox cbConfirm = findViewById(R.id.cb_confirm);
        cbConfirm.setFocusable(true);
        String[] dev = {"吊笼检查", "笼顶检查"};

        cbConfirm.setOnCheckedChangeListener((v, checked) -> {
            if (checked) {
                toNextIntent(客户名称);
            }
        });
//        loadingIndicatorView.hide();
    }

//    private final Handler uiHandler = new Handler();

//    final Runnable updateRunnable = new Runnable() {
//        public void run() {
//            updateUi();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskheader);

        host = this.getString(R.string.host_address);
        loadingIndicatorView = findViewById(R.id.loading);
        loadingIndicatorView.setIndicator("BallClipRotateMultipleIndicator");
        loadingIndicatorView.show();

        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm")
                .create();

        String code = getIntent().getStringExtra("code");
        if (code == null)
            GlassToastUtil.showToast(this, "code为空!");
        //DE669C939B
        String sql = "select top 1 防伪码,产品编号,出厂日期  from [防伪数据表_明细]  where 防伪码='" + code + "';";

        String sql2 =
                "SELECT a.防伪码,a.产品编号 AS '安全器编号',a.出厂日期 AS '生产日期', c.需方 AS '客户名称', b.型号 AS '安全器型号'" +
                        " FROM [防伪数据表_明细] a LEFT JOIN [产品编号分配_明细] b ON b.产品编号 = a.产品编号 LEFT JOIN [销售合同_主表] c ON c.合同编号 = b.合同编号" +
                        " WHERE a.防伪码 = '" + code + "';";

//        new Thread(() -> {
//            String[] columns = {};
//            list = DBUtil.QuerySQL(sql2, columns);
//            uiHandler.post(updateRunnable);
//           Log.d("sqlans", ans);
//        }).start();
        getData(code);
    }

    private void getData(String code) {
        String urlStr = host + "getOrCreate?antiCode=" + code;
        Log.i("bean!!!1", urlStr);
        OkHttpUtils
                .post()
                .url(urlStr)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e("recordText:::", s);
                        AfterSaleRecord afterSaleRecord = gson.fromJson(s, AfterSaleRecord.class);
                        Log.e("bean!!!!", afterSaleRecord.toString());
                        recordBeanText = s;
                        updateUi(afterSaleRecord);
//                        List<AfterServiceInfo> fw = gson.fromJson(s, new TypeToken<List<AfterServiceInfo>>() {
//                        }.getType());
//                        updateUi(fw);
//                        fw.forEach(System.out::println);
                    }
                });
    }

    @Override
    public InstructConfig configInstruct() {
        InstructConfig config = new InstructConfig();
        config.setActionKey(InstructActivity.class.getName() + InstructConfig.ACTION_SUFFIX)
                .addInstructEntity(
                        new InstructEntity()
                                .addEntityKey(new EntityKey("确认信息", "que ren xin xi"))
                                .addEntityKey(new EntityKey(EntityKey.Language.en, "confirm msg"))
                                .setShowTips(true)
                                .setCallback(new IInstructReceiver() {
                                    @Override
                                    public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                        Log.d("instruct", "确认信息");
                                        if (客户名称 != null && !客户名称.trim().equals(""))
                                            toNextIntent(客户名称);
                                    }
                                })
                );
        return config;
    }

    public void toNextIntent(String param) {
        if (recordBeanText == null) {
            GlassToastUtil.showToast(this, "未找到任务实体!");
            return;
        }
        Intent it1 = new Intent(this, ServiceItemActivity.class);
        it1.putExtra("client", param);
        it1.putExtra("bean", recordBeanText);
        startActivity(it1);

    }

    @Override
    public boolean doReceiveCommand(String s) {
        return false;
    }
}