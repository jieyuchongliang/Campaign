package com.fujisoft.campaign;

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

import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 密码修改页面
 */
public class ModifyPasswordActivity extends BaseActivity {
    private String TAG="campaign";
    EditText old_password;
    EditText new_password;
    EditText check_password;
    String strOldPassword;
    String strNewPassword;
    String strCheckPassword;
    private String userId = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    finish();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG,"=== ModifyNickNameActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);

        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        check_password = (EditText) findViewById(R.id.check_password);

        Button resetPswButton = (Button) findViewById(R.id.reset_psw);
        resetPswButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isConnect(ModifyPasswordActivity.this)) {
                    if (checkEmpty()) {
                        startModify();
                    }
                } else {
                    Utils.showToast(ModifyPasswordActivity.this, R.string.netWrong);
                }
            }
        });
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
        tool_bar_center_text_view.setText(getString(R.string.reset_password_title));
    }

    /**
     * 进行判断
     */
    private void startModify() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "Exception";
                Message message = new Message();
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("oldPwd", strOldPassword));
                    params.add(new BasicNameValuePair("newPwd", strNewPassword));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_RESET_PWD, params);
                    Log.d(TAG, "===ModifyPasswordActivity#startModify() json = " + json);
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        msg = jsonObject.get("msg").toString();
                    }
                    if (!result) {
                        //修改失败后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //修改成功后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_SUCCESS;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //修改异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 判空处理
     *
     * @return true : 正常 false : 异常
     */
    private boolean checkEmpty() {

        strOldPassword = old_password.getText().toString().trim();
        strNewPassword = new_password.getText().toString().trim();
        strCheckPassword = check_password.getText().toString().trim();

        if (strOldPassword == null || "".equals(strOldPassword)) {
            showConfirmDialog("旧密码不能为空，请重新输入！");
        } else if (strNewPassword == null || "".equals(strNewPassword)) {
            showConfirmDialog("新密码不能为空，请重新输入！");
        } else if (strCheckPassword == null || "".equals(strCheckPassword)) {
            showConfirmDialog("确认新密码不能为空，请重新输入！");
        } else if (!(strCheckPassword.equals(strNewPassword))) {
            showConfirmDialog("两次密码输入不一致，请重新输入！");
        } else {
            return true;
        }
        return false;
    }
}