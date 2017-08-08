package com.fujisoft.campaign.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.R;
import com.fujisoft.campaign.RegistrationActivity;
import com.fujisoft.campaign.UserAgreementActivity;
import com.fujisoft.campaign.service.RegisterCodeTimerService;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.RegisterCodeTimer;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 个人注册用Fragment
 */
public class PersonalRegisterFragment extends Fragment implements View.OnClickListener {

    private String TAG = "campaign";
    private CheckBox checkBox_agree_personal;
    private Button button_check_person;
    private EditText edt_register_person_tel;
    private EditText tel_check_person;
    private EditText edt_register_person_pwd;
    private Spinner spinner_register_person_sex;
    private Button btn_register_person_submit;
    private TextView agree_person;

    private RegistrationActivity mActivity;

    private Intent mIntent;

    private onGetUserDataListener mCallback;

    public interface onGetUserDataListener {
        void onSetUserData(String userPhone, String checkNumber, String userPassword, int userSex, String clientid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        mActivity = (RegistrationActivity) getActivity();

        View mView = inflater.inflate(R.layout.fragment_personal_view, container, false);

        // 手机号
        edt_register_person_tel = (EditText) mView.findViewById(R.id.edt_register_person_tel);

        // 验证码按钮
        button_check_person = (Button) mView.findViewById(R.id.button_check_person);
        button_check_person.setOnClickListener(this);

        // 验证码
        tel_check_person = (EditText) mView.findViewById(R.id.tel_check_person);

        // 密码
        edt_register_person_pwd = (EditText) mView.findViewById(R.id.edt_register_person_pwd);

        // 性别
        spinner_register_person_sex = (Spinner) mView.findViewById(R.id.spinner_register_person_sex);

        // 提交按钮
        btn_register_person_submit = (Button) mView.findViewById(R.id.btn_register_person_submit);
        btn_register_person_submit.setOnClickListener(this);

        agree_person = (TextView) mView.findViewById(R.id.agree_person);
        agree_person.setOnClickListener(this);

        checkBox_agree_personal = (CheckBox) mView.findViewById(R.id.checkBox_agree_personal);
        return mView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onGetUserDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onGetUserDataListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 倒计时Handler
     */
    @SuppressLint("HandlerLeak")
    Handler mCodeHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == RegisterCodeTimer.IN_RUNNING) {// 正在倒计时
                button_check_person.setText(msg.obj.toString());
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                button_check_person.setEnabled(true);
                button_check_person.setText(msg.obj.toString());
            }
        }

        ;
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_check_person) {
//            RegisterCodeTimerService.setHandler(mCodeHandler);
//            mIntent = new Intent(getContext(), RegisterCodeTimerService.class);
//            button_check_person.setEnabled(false);
//            getContext().startService(mIntent);    // 开启倒计时服务

            if(checkPhone()){

                RegisterCodeTimerService.setHandler(mCodeHandler);
                mIntent = new Intent(mActivity, RegisterCodeTimerService.class);
                sendSMS();

            }

        } else if (v.getId() == R.id.agree_person) {
            Intent intent = new Intent(getContext(), UserAgreementActivity.class);
//            getContext().startActivity(intent);
            intent.putExtra("from_fragment", "PersonalRegisterFragment");
            startActivityForResult(intent, Activity.RESULT_FIRST_USER);

        } else if (v.getId() == R.id.btn_register_person_submit) {

            if (!checkBox_agree_personal.isChecked()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_login_rule), Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.isConnect(getContext())) {
                if (checkEmpty()) {


                    SharedPreferences sp = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME_CID, Context.MODE_MULTI_PROCESS);
                    String clientId = sp.getString(Constants.EXTRA_USER_CID, "0");
                    mCallback.onSetUserData(edt_register_person_tel.getText().toString().trim(), tel_check_person.getText().toString().trim(),
                            edt_register_person_pwd.getText().toString().trim(), spinner_register_person_sex.getSelectedItemPosition(), clientId);
                }
            } else {
                Utils.showToast(getContext(), R.string.netWrong);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data.getExtras().getBoolean("USER_AGREEMENT_FLAG")) {
            checkBox_agree_personal.setChecked(true);
        }
    }
    private boolean checkPhone() {

//        strPhoneNum = phone_num.getText().toString().trim();
        if (edt_register_person_tel.getText().toString().trim() == null || "".equals(edt_register_person_tel.getText().toString().trim())) {
            showErrorDialog("手机号不能为空，请重新输入！");
        } else {
            mActivity.showProgressDialog();
            return true;
        }
        return false;
    }
    private boolean checkEmpty() {
        if (edt_register_person_tel.getText().toString().trim() == null || "".equals(edt_register_person_tel.getText().toString().trim())) {
            showErrorDialog("手机号不能为空，请重新输入！");
        } else if (tel_check_person.getText().toString().trim() == null || "".equals(tel_check_person.getText().toString().trim())) {
            showErrorDialog("验证码不能为空，请重新输入！");
        } else if (edt_register_person_pwd.getText().toString().trim() == null || "".equals(edt_register_person_pwd.getText().toString().trim())) {
            showErrorDialog("密码不能为空，请重新输入！");
        } else if (edt_register_person_pwd.getText().toString().trim().length() < 6 || edt_register_person_pwd.getText().toString().trim().length() > 32) {
            showErrorDialog("密码位数不正确！请输入6-32位密码");
        } else {
            return true;
        }
        return false;
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", null).create();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null && null != mIntent) {
            getContext().stopService(mIntent);
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
                    params.add(new BasicNameValuePair("phone", edt_register_person_tel.getText().toString().trim()));

//                    params.add(new BasicNameValuePair("id", userId));


                    // 获取响应的结果信息
                    String json = mActivity.getPostData(Constants.URL_SEND_SMS, params);
                    Log.d(TAG, "=== PersonalRegisterFragment#sendSMS json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            // 获取数据成功后的处理
                            mActivity.startService(mIntent);    // 开启倒计时服务
                            button_check_person.setEnabled(false);
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
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mActivity.dismissProgressDialog();

            mActivity.showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
//                case Constants.CODE_UPDATE_PHONE_SUCCESS:
//                    getActivity().finish();
//                    break;
                default:
                    break;
            }
        }
    };


}