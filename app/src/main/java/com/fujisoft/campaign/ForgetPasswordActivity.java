package com.fujisoft.campaign;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.service.RegisterCodeTimerService;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.RegisterCodeTimer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "campaign";
    private EditText forget_phone_num;
    private EditText forget_phone_check_num;
    private EditText forget_new_password;
    private EditText forget_phone_num_check;
    private Button forget_send;
    private Button button_forget;
    private String strForgetPhoneNum;
    private String strForgetPhoneCheckNum;
    private String strForgetPasswordCheck;
    private String strForgetPassword;

    private Intent mIntent;
    private String userId = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            Toast.makeText(ForgetPasswordActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
                case Constants.CODE_UPDATE_PHONE_SUCCESS:
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
        setContentView(R.layout.activity_forget_password);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG, "=== ForgetPasswordActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);
        forget_phone_num = (EditText)findViewById(R.id.forget_phone_num);
        forget_phone_check_num = (EditText)findViewById(R.id.forget_phone_check_num);
        forget_new_password = (EditText)findViewById(R.id.forget_new_password);
        forget_phone_num_check=(EditText)findViewById(R.id.forget_phone_num_check);
        forget_send = (Button)findViewById(R.id.forget_send);
        button_forget = (Button)findViewById(R.id.button_forget);
        forget_send.setOnClickListener(this);
        button_forget.setOnClickListener(this);
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
        tool_bar_center_text_view.setText(R.string.find_password);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forget_send:
                if (checkPhone()) {
                    RegisterCodeTimerService.setHandler(mCodeHandler);
                    mIntent = new Intent(ForgetPasswordActivity.this, RegisterCodeTimerService.class);
                    sendSMS();
                }
                break;
            case R.id.button_forget:
                if (checkEmpty()) {
                    updateForgetPassword();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 倒计时Handler
     */
    @SuppressLint("HandlerLeak")
    Handler mCodeHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == RegisterCodeTimer.IN_RUNNING) {// 正在倒计时
                forget_send.setText(msg.obj.toString());
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                forget_send.setEnabled(true);
                forget_send.setText(msg.obj.toString());
            }
        }

        ;
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIntent != null) {
            stopService(mIntent);
        }
    }

    private void sendSMS() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("phone", strForgetPhoneNum));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_PASS_SEND_SMS, params);
                    Log.d(TAG, "=== ForgetPasswordActivity#sendSMS json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            // 获取数据成功后的处理
                            startService(mIntent);    // 开启倒计时服务
                            forget_send.setEnabled(false);
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mHandler.sendMessage(message);
                        } else {
                            // 获取数据失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 获取数据异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private boolean checkPhone() {

        strForgetPhoneNum = forget_phone_num.getText().toString().trim();
        if (strForgetPhoneNum == null || "".equals(strForgetPhoneNum)) {
            showConfirmDialog(getString(R.string.phone_number_check_null));
        } else {
            showProgressDialog();
            return true;
        }
        return false;
    }
    private boolean checkEmpty() {
        strForgetPasswordCheck = forget_new_password.getText().toString().trim();
        strForgetPassword = forget_phone_num_check.getText().toString().trim();
        strForgetPhoneNum = forget_phone_num.getText().toString().trim();
        strForgetPhoneCheckNum = forget_phone_check_num.getText().toString().trim();
        if (strForgetPhoneNum == null || "".equals(strForgetPhoneNum)) {
            showConfirmDialog(getString(R.string.phone_number_check_null));
        } else if (strForgetPhoneCheckNum == null || "".equals(strForgetPhoneCheckNum)) {
            showConfirmDialog(getString(R.string.phone_number_num_check));
        } else if (!(strForgetPasswordCheck.equals(strForgetPassword))) {
            showConfirmDialog("两次密码输入不一致，请重新输入！");
        }else {
            return true;
        }
        return false;
    }

    private void updateForgetPassword() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("phone", strForgetPhoneNum));
                    params.add(new BasicNameValuePair("code", strForgetPhoneCheckNum));
                    params.add(new BasicNameValuePair("password",strForgetPassword ));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_FORGET_PASSWORD, params);
                    Log.d(TAG, "=== ForgetPasswordActivity #updateForgetPassword() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            // 绑定手机号成功后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_UPDATE_PHONE_SUCCESS;
                            mHandler.sendMessage(message);
                        } else {
                            // 绑定手机号失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 绑定手机号发生异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
}
