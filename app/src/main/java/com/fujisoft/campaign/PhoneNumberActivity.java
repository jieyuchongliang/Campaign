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

/**
 * 手机号页面
 */
public class PhoneNumberActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "campaign";
    EditText phone_num;
    EditText phone_check_num;
    Button send;
    String strPhoneNum;
    String strPhoneCheckNum;

    private Intent mIntent;
    private String userId = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            Toast.makeText(PhoneNumberActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_phone_number);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG, "=== PhoneNumberActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);

        phone_num = (EditText) findViewById(R.id.phone_num);
        phone_check_num = (EditText) findViewById(R.id.phone_check_num);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
        Button bind_nm = (Button) findViewById(R.id.bind_nm);
        bind_nm.setOnClickListener(this);
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
        tool_bar_center_text_view.setText(R.string.phone_number_title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (checkPhone()) {
                    RegisterCodeTimerService.setHandler(mCodeHandler);
                    mIntent = new Intent(PhoneNumberActivity.this, RegisterCodeTimerService.class);
                    sendSMS();
                }
                break;
            case R.id.bind_nm:
                if (checkEmpty()) {
                    updatePhone();
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
                send.setText(msg.obj.toString());
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                send.setEnabled(true);
                send.setText(msg.obj.toString());
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
                    params.add(new BasicNameValuePair("phone", strPhoneNum));
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_SEND_SMS, params);
                    Log.d(TAG, "=== PhoneNumberActivity#sendSMS json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            // 获取数据成功后的处理
                            startService(mIntent);    // 开启倒计时服务
                            send.setEnabled(false);
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

        strPhoneNum = phone_num.getText().toString().trim();
        if (strPhoneNum == null || "".equals(strPhoneNum)) {
            showConfirmDialog(getString(R.string.phone_number_check_null));
        } else {
            showProgressDialog();
            return true;
        }
        return false;
    }

    private boolean checkEmpty() {

        strPhoneNum = phone_num.getText().toString().trim();
        strPhoneCheckNum = phone_check_num.getText().toString().trim();
        if (strPhoneNum == null || "".equals(strPhoneNum)) {
            showConfirmDialog(getString(R.string.phone_number_check_null));
        } else if (strPhoneCheckNum == null || "".equals(strPhoneCheckNum)) {
            showConfirmDialog(getString(R.string.phone_number_num_check));
        } else {
            return true;
        }
        return false;
    }

    private void updatePhone() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("phone", strPhoneNum));
                    params.add(new BasicNameValuePair("code", strPhoneCheckNum));
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_MODIFY_PHONE, params);
                    Log.d(TAG, "=== PhoneNumberActivity #updatePhone() json = " + json);
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
