package com.fujisoft.campaign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.utils.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 财务页面
 */
public class FinanceActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "campaign";
    Button  finance_recharge_button, finance_recharge_record_button, edt_register_person_bath;
    TextView finance_gold_count_button;
    private String userType = "";
    // 从数据库中取的积分值
    private int integrationValue = 0;

    private String userId = null;  // 用户ID
    private String enterId = null; // 企业ID
    String openingFee = "";

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    finish();
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    initView();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    finish();
                    break;
                case Constants.CODE_USER_UNACTIVE:
                    // 用户是未激活状态(即未交年费)时，进入交费页面
                    Intent intent = new Intent();
                    intent.putExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT, openingFee);
                    intent.putExtra(Constants.EXTRA_USER_ID, userId);
                    intent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                    intent.putExtra(Constants.EXTRA_AMOUNT, openingFee);
                    intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_PAY_TYPE);
                    intent.setClass(FinanceActivity.this, CompanyPayOpeningFeeActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        userType = getIntent().getStringExtra(Constants.EXTRA_USER_TYPE);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
//            //监控/拦截菜单键
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        getFinanceData();
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        getLayoutInflater().inflate(R.layout.toolbar_button, toolbar);

        ImageButton tool_bar_back_button = (ImageButton) findViewById(R.id.tool_bar_back_button);
        tool_bar_back_button.setVisibility(View.VISIBLE);
        tool_bar_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        TextView tool_bar_center_text_view = (TextView) findViewById(R.id.tool_bar_center_text_view);
        tool_bar_center_text_view.setVisibility(View.VISIBLE);
        tool_bar_center_text_view.setText(R.string.finance);
    }

    private void initView() {

        // 积分按钮/元宝按钮
        finance_gold_count_button = (TextView) findViewById(R.id.finance_gold_count_button);
        finance_gold_count_button.setText(getResources().getString(R.string.finance_gold_count) + integrationValue);
        finance_gold_count_button.setOnClickListener(this);

        // 充值
        finance_recharge_button = (Button) findViewById(R.id.finance_recharge_button);
        finance_recharge_button.setOnClickListener(this);

        // 充值记录
        finance_recharge_record_button = (Button) findViewById(R.id.finance_recharge_record_button);
        finance_recharge_record_button.setOnClickListener(this);

        // 申请发票
        edt_register_person_bath = (Button) findViewById(R.id.edt_register_person_bath);
        edt_register_person_bath.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finance_gold_count_button:
                break;
            case R.id.finance_recharge_button:
                Intent rechargeIntent = new Intent();
                rechargeIntent.setClass(FinanceActivity.this, RechargeActivity.class);
                startActivity(rechargeIntent);
                break;
            case R.id.finance_recharge_record_button:
                Intent rechargeRecordIntent = new Intent();
                rechargeRecordIntent.setClass(FinanceActivity.this, RechargeRecordActivity.class);
                startActivity(rechargeRecordIntent);
                break;
            case R.id.edt_register_person_bath:
                Intent applyInvoiceIntent = new Intent();
                applyInvoiceIntent.setClass(FinanceActivity.this, ApplyInvoiceActivity.class);
                startActivity(applyInvoiceIntent);
                break;
        }
    }

    private void getFinanceData() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_FINANCE_INDEX, params);
                    Log.d(TAG, "=== FinanceActivity#startLogin() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            enterId = dataObject.get("enterId").toString();
                            integrationValue = Integer.parseInt(dataObject.get("score").toString());
                            int accountStatus = dataObject.getInt("accountStatus"); // 0:未激活  1：已激活
                            openingFee = dataObject.getString("opening_fee").toString();
                            if (accountStatus == 0) {// 用户是未激活状态(即未交年费)时，进入交费页面
                                // 获取数据成功后的处理
                                message.obj = msg;
                                message.what = Constants.CODE_USER_UNACTIVE;
                                mHandler.sendMessage(message);
                            } else {
                                // 执行成功后的处理
                                message.obj = msg;
                                message.what = Constants.CODE_EXECUTE_SUCCESS;
                                mHandler.sendMessage(message);
                            }
                        } else {
                            // 执行失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // 执行异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
}