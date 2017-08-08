package com.fujisoft.campaign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fujisoft.campaign.utils.Constants;

/**
 * 个人信息页面
 */
public class PersonalInformationActivity extends BaseActivity implements View.OnClickListener {

    public String userId;

    RelativeLayout modify_image_layout;
    RelativeLayout modify_nickName_layout;
    RelativeLayout address_manager_layout;
    RelativeLayout phone_number_layout;
    RelativeLayout reset_password_layout;
    RelativeLayout exit_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        initView();
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
        tool_bar_center_text_view.setText(R.string.person_message);
    }

    private void initView() {

        // 头像修改
        modify_image_layout = (RelativeLayout) findViewById(R.id.modify_image_layout);
        modify_image_layout.setOnClickListener(this);

        // 昵称修改
        modify_nickName_layout = (RelativeLayout) findViewById(R.id.modify_nickName_layout);
        modify_nickName_layout.setOnClickListener(this);

        // 地址管理
        address_manager_layout = (RelativeLayout) findViewById(R.id.address_manager_layout);
        address_manager_layout.setOnClickListener(this);

        // 手机号
        phone_number_layout = (RelativeLayout) findViewById(R.id.phone_number_layout);
        phone_number_layout.setOnClickListener(this);

        // 密码修改
        reset_password_layout = (RelativeLayout) findViewById(R.id.reset_password_layout);
        reset_password_layout.setOnClickListener(this);

        // 退出
        exit_layout = (RelativeLayout) findViewById(R.id.exit_layout);
        exit_layout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        switch (v.getId()) {
            case R.id.modify_image_layout:
                intent.setClass(PersonalInformationActivity.this, ChangePhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.modify_nickName_layout:
                intent.setClass(PersonalInformationActivity.this, ModifyNickNameActivity.class);
                startActivity(intent);
                break;
            case R.id.address_manager_layout:
                intent.setClass(PersonalInformationActivity.this, AddressManageActivity.class);
                startActivity(intent);
                break;
            case R.id.phone_number_layout:
                intent.setClass(PersonalInformationActivity.this, PhoneNumberActivity.class);
                startActivity(intent);
                break;
            case R.id.reset_password_layout:
                intent.setClass(PersonalInformationActivity.this, ModifyPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.exit_layout:
                // 清除用户登录过的信息，并退出当前Activity
                showExitDialog();
                break;
        }
    }

    /**
     * 显示退出当前应用的提示Dialog
     */
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformationActivity.this);
        builder.setTitle(getString(R.string.login_dialog_hint))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.commit();
                        PersonalInformationActivity.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setMessage(getString(R.string.determine_exit));
        builder.create().show();
    }
}