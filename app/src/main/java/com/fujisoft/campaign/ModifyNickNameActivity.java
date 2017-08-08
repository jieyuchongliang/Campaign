package com.fujisoft.campaign;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * 昵称修改页面
 */
public class ModifyNickNameActivity extends BaseActivity {
    private String TAG="campaign";
    private EditText mInputNickName;
    String strNewNickName;
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
        setContentView(R.layout.activity_modify_nickname);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG,"=== ModifyNickNameActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);

        mInputNickName = (EditText) findViewById(R.id.new_nickName_edit);
        mInputNickName.addTextChangedListener(mTextWatcher);
        Button mModifyNickNameButton = (Button) findViewById(R.id.modify_nickName_button);
        mModifyNickNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isConnect(ModifyNickNameActivity.this)) {
                    if (checkEmpty()) {
                        startModify();
                    }
                } else {
                    Utils.showToast(ModifyNickNameActivity.this, R.string.netWrong);
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
        tool_bar_center_text_view.setText(getString(R.string.modify_nickName_title));
    }

    /**
     * 限制昵称长度12字节
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;
        private int maxLen = 12;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mInputNickName.getSelectionStart();
            editEnd = mInputNickName.getSelectionEnd();
            // 先去掉监听器，否则会出现栈溢出
            mInputNickName.removeTextChangedListener(mTextWatcher);
            if (!TextUtils.isEmpty(mInputNickName.getText())) {
                while (calculateLength(s.toString()) > maxLen) {
                    s.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
            }

            mInputNickName.setText(s);
            mInputNickName.setSelection(editStart);

            mInputNickName.addTextChangedListener(mTextWatcher);
        }

        private int calculateLength(String etstring) {
            char[] ch = etstring.toCharArray();
            int varlength = 0;
            for (int i = 0; i < ch.length; i++) {
                if ((ch[i] >= 0x2E80 && ch[i] <= 0xFE4F) || (ch[i] >= 0xA13F && ch[i] <= 0xAA40) || ch[i] >= 0x80) {
                    varlength = varlength + 2;
                } else {
                    varlength++;
                }
            }
            return varlength;
        }
    };

    /**
     * 进行昵称的修改
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
                    params.add(new BasicNameValuePair("nickname", strNewNickName));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_MODIFY_NICK, params);
                    Log.d(TAG, "=== ModifyNickNameActivity#startModify() json = " + json);
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

        strNewNickName = mInputNickName.getText().toString().trim();
        if (strNewNickName == null || "".equals(strNewNickName)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("昵称不能为空，请重新输入！")
                    .setPositiveButton("确定", null).create();
            dialog.show();
        } else {
            return true;
        }
        return false;
    }
}