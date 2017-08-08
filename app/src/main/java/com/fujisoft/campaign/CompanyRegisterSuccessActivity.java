package com.fujisoft.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fujisoft.campaign.utils.Constants;

/**
 * 企业注册成功页面
 */
public class CompanyRegisterSuccessActivity extends BaseActivity implements View.OnClickListener{

    private String mCompanyRegisterAmount = "0";     // 从服务器端获取“交付开户费及年费”值
    private String userId = "";                         // 从服务器端获取企业的ID值
    private String userType = "";                      // 从服务器端获取企业的类型值
    private TextView mCompanyRegisterAmountTextView;
    private Button mCompanyRegisterPayButton;
    private Button mCompanyRegisterSkipPayButton;

    public static CompanyRegisterSuccessActivity companyRegisterSuccessActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register_result);

        companyRegisterSuccessActivity = this;

        userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        userType = getIntent().getStringExtra(Constants.EXTRA_USER_TYPE);
        mCompanyRegisterAmount = getIntent().getStringExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT);
        initView();
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
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
        tool_bar_center_text_view.setText(R.string.company_register_title);
        TextView tool_bar_right_bottom_button = (TextView) findViewById(R.id.tool_bar_right_bottom_button);
        tool_bar_right_bottom_button.setVisibility(View.VISIBLE);
        tool_bar_right_bottom_button.setText(R.string.login);
        tool_bar_right_bottom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startLoginIntent = new Intent();
                startLoginIntent.setClass(CompanyRegisterSuccessActivity.this, LoginActivity.class);
                startActivity(startLoginIntent);
                finish();
            }
        });
    }

    private void initView() {

        // 交付开户费及年费的金额数
        mCompanyRegisterAmountTextView = (TextView) findViewById(R.id.company_register_success_pay_amount_text);
        if (null != mCompanyRegisterAmount && !mCompanyRegisterAmount.contains(".")) {
            mCompanyRegisterAmountTextView.setText(String.format(getString(R.string.register_company_success_pay_amount), mCompanyRegisterAmount + ".00"));
        } else {
            mCompanyRegisterAmountTextView.setText(String.format(getString(R.string.register_company_success_pay_amount), mCompanyRegisterAmount));
        }

        // 微信支付按钮
        mCompanyRegisterPayButton = (Button) findViewById(R.id.company_register_success_pay_button);
        mCompanyRegisterPayButton.setOnClickListener(this);

        // 跳过按钮
        mCompanyRegisterSkipPayButton = (Button) findViewById(R.id.company_register_success_skip_pay_button);
        mCompanyRegisterSkipPayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.company_register_success_pay_button:
                Intent payIntent = new Intent();
                payIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                payIntent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                payIntent.putExtra(Constants.EXTRA_AMOUNT, mCompanyRegisterAmount);
                payIntent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_PAY_TYPE);
                payIntent.setClass(CompanyRegisterSuccessActivity.this, PayAndRechargeActivity.class);
                startActivity(payIntent);
                break;
            case R.id.company_register_success_skip_pay_button:
                Intent startIndexIntent = new Intent();
                startIndexIntent.setClass(CompanyRegisterSuccessActivity.this, LoginActivity.class);
                startActivity(startIndexIntent);
                this.finish();
                break;
        }
    }
}
