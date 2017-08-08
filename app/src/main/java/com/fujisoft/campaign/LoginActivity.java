package com.fujisoft.campaign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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
 * 会员登录页面
 */
public class LoginActivity extends LocationActivity implements View.OnClickListener {
    private String TAG = "campaign";
    private EditText edtLoginTel;
    private EditText edtLoginPwd;
    private Button btnLogin;
    private String strEdtLoginTel;
    private String strEdtLoginPwd;
    private CheckBox checkBox_agree;
    private String clientId;
    private TextView forget_password;

    private TextView agree_company;
    public static LoginActivity staticLoginActivity;
    /**
     * 用户ID
     */
    private String userId = null;
    /**
     * 用户类型
     */
    private String userType = null;
    /**
     * 用户是否激活(0:未激活 1：已激活)
     */
    private String userActive = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            Intent requestIntent = getIntent();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    showErrorDialog(Constants.CODE_EXECUTE_FAILURE, msg.obj.toString());
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:

                    Log.d(TAG, "=== LoginActivity# handleMessage()CODE_EXECUTE_SUCCESS ==="+userId);
                    if((userId!=null)&&(!"null".equals(userId))&&(!"".equals(userId))){
                        if (Utils.isConnect(LoginActivity.this)){
                            Log.d(TAG, "=== LoginActivity# SendLocation() ===");
                            SendLocation(userId);
                        }
                    }

                    Utils.showToast(LoginActivity.this, R.string.login_toast_success);
                    // 登录成功后，隐藏输入法键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    if ("0".equals(userActive)) {
                        Intent favoriteIntent = new Intent();
                        favoriteIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                        favoriteIntent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                        favoriteIntent.setClass(LoginActivity.this, FavoriteActivity.class);
                        startActivity(favoriteIntent);
                        LoginActivity.this.finish();
                    } else {
                        // 保存数据到SharedPreferences
                        // Context.MODE_PRIVATE : 指定该SharedPreferences数据只能被本应用程序读、写
                        SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                        Editor editor = sp.edit();
                        editor.putString(Constants.EXTRA_USER_ID, userId);
                        editor.putString(Constants.EXTRA_USER_TYPE, userType);
                        editor.commit();

                    }
                    setResult(Constants.CODE_EXECUTE_SUCCESS, requestIntent);
                    finish();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    Utils.showToast(LoginActivity.this, R.string.login_toast_exception);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        staticLoginActivity = this;
        initViews();
    }

    private void initViews() {
        edtLoginTel = (EditText) findViewById(R.id.edt_login_tel);
        edtLoginPwd = (EditText) findViewById(R.id.edt_login_pwd);
        btnLogin = (Button) findViewById(R.id.btn_login);
        checkBox_agree = (CheckBox) findViewById(R.id.checkBox_agree);
        btnLogin.setOnClickListener(this);
        agree_company = (TextView) findViewById(R.id.agree_company);
        agree_company.setOnClickListener(this);
        forget_password = (TextView)findViewById(R.id.forget_password);
        forget_password.setOnClickListener(this);

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
                Utils.hideKeyBoard(LoginActivity.this);
                onBackPressed();
            }
        });
        TextView tool_bar_center_text_view = (TextView) findViewById(R.id.tool_bar_center_text_view);
        tool_bar_center_text_view.setVisibility(View.VISIBLE);
        tool_bar_center_text_view.setText(getString(R.string.login_person));
        TextView tool_bar_right_bottom_button = (TextView) findViewById(R.id.tool_bar_right_bottom_button);
        tool_bar_right_bottom_button.setVisibility(View.VISIBLE);
        tool_bar_right_bottom_button.setText(R.string.sign_in);
        tool_bar_right_bottom_button.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        strEdtLoginTel = edtLoginTel.getText().toString().trim();
        strEdtLoginPwd = edtLoginPwd.getText().toString().trim();
        switch (view.getId()) {
            case R.id.btn_login:
                if(!checkBox_agree.isChecked()){
                    Toast.makeText(this, getResources().getString(R.string.toast_login_rule), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Utils.isConnect(this)) {
                    if (checkEmpty()) {
                        startLogin();
                    }
                } else {
                    Utils.showToast(this, R.string.netWrong);
                }
                break;
            case R.id.tool_bar_right_bottom_button:
                Intent personRegisterIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(personRegisterIntent);
                break;
            case R.id.agree_company:
                Intent intent = new Intent(this, UserAgreementActivity.class);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
                break;
            case R.id.forget_password:
                Intent forgetPasswordIntent = new Intent(this, ForgetPasswordActivity.class);
                startActivityForResult(forgetPasswordIntent, Activity.RESULT_FIRST_USER);
                break;
        }
    }

    /**
     * 进行登录
     */
    private void startLogin() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "Exception";
                Message message;
                try {
                    List<NameValuePair> params = new ArrayList<>();

                    SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME_CID, Context.MODE_MULTI_PROCESS);
                    clientId = sp.getString(Constants.EXTRA_USER_CID,"0");

                    params.add(new BasicNameValuePair("phone", strEdtLoginTel));
                    params.add(new BasicNameValuePair("password", strEdtLoginPwd));
                    params.add(new BasicNameValuePair("clientId",clientId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_LOGIN, params);
                    Log.d(TAG, "=== LoginActivity#startLogin() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            userId = dataObject.get("userId").toString();
                            userType = dataObject.get("userType").toString();
                            userActive = dataObject.get("userActive").toString();
                            // 登录成功后的处理
                            message = new Message();
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mHandler.sendMessage(message);
                        } else {
                            // 登录失败后的处理
                            message = new Message();
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // 登录异常后的处理
                    message = new Message();
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
        if (strEdtLoginTel == null || "".equals(strEdtLoginTel)
                || strEdtLoginPwd == null || "".equals(strEdtLoginPwd)) {
            showErrorDialog(0, getString(R.string.login_dialog_input_empty));
            return false;
        } else {
            return true;
        }
    }

    /**
     * 显示不同的Error Dialog
     *
     * @param msgId
     */
    private void showErrorDialog(int msgId, String message) {
        String dialogMessage = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(getString(R.string.login_dialog_hint))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        switch (msgId) {
            case 0:
                dialogMessage = message;
                break;
            case Constants.CODE_EXECUTE_FAILURE:
                dialogMessage = message;
                break;
        }
        builder.setMessage(dialogMessage);
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data && data.getExtras().getBoolean("USER_AGREEMENT_FLAG")) {
            checkBox_agree.setChecked(true);
        }
    }
}