package com.fujisoft.campaign;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fujisoft.campaign.utils.Constants;

/**
 * 支付/充值结果画面
 */
public class PayAndRechargeResultActivity extends BaseActivity {
    private String TAG = "campaign";
    private boolean rechargeResult = false;  // 充值结果
    private String rechargeAmount = "";       // 充值/支付成功后，从接口中返回的金额数
    private String payRechargeType = null;   // 类型：支付/充值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_result);

        // 获取充值结果(默认为false,充值失败)
        rechargeResult = getIntent().getBooleanExtra(Constants.EXTRA_RECHARGE_RESULT, false);
        rechargeAmount = getIntent().getStringExtra(Constants.EXTRA_AMOUNT);
        Log.d(TAG, "=== PayAndRechargeResultActivity#onCreate() rechargeResult = " + rechargeResult);
        initView();
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        getLayoutInflater().inflate(R.layout.toolbar_button, toolbar);

        payRechargeType = getIntent().getStringExtra(Constants.EXTRA_PAY_RECHARGE_TYPE);
        Log.d(TAG, "=== PayAndRechargeResultActivity#onCreateCustomToolBar() payRechargeType = " + payRechargeType);

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
        if (Constants.EXTRA_PAY_TYPE.equals(payRechargeType)) {
            tool_bar_center_text_view.setText(R.string.payment);
        } else if (Constants.EXTRA_RECHARGE_TYPE.equals(payRechargeType)) {
            tool_bar_center_text_view.setText(R.string.recharge_title);
        }
    }

    private void initView() {

        // 充值/支付成功
        LinearLayout mRechargeSuccessLayout = (LinearLayout) findViewById(R.id.recharge_success_layout);
        TextView successResultTitle = (TextView) findViewById(R.id.success_result_title);

        // 充值/支付失败
        LinearLayout mRechargeFailureLayout = (LinearLayout) findViewById(R.id.recharge_failure_layout);
        TextView payRechargeResultTitle = (TextView) findViewById(R.id.pay_recharge_result_title);

        if (rechargeResult) {
            mRechargeSuccessLayout.setVisibility(View.VISIBLE); // 充值成功，显示成功页面
            if (Constants.EXTRA_PAY_TYPE.equals(payRechargeType)) {
                successResultTitle.setText(R.string.pay_success_title);
            } else if (Constants.EXTRA_RECHARGE_TYPE.equals(payRechargeType)) {
                successResultTitle.setText(R.string.recharge_success_title);
            }
        } else {
            mRechargeFailureLayout.setVisibility(View.VISIBLE); // 充值失败，显示失败页面
            if (Constants.EXTRA_PAY_TYPE.equals(payRechargeType)) {
                payRechargeResultTitle.setText(R.string.pay_failure_title);
            } else if (Constants.EXTRA_RECHARGE_TYPE.equals(payRechargeType)) {
                payRechargeResultTitle.setText(R.string.recharge_failure_title);
            }
        }

        TextView mRechargeAmountTextView = (TextView) findViewById(R.id.recharge_success_amount_text);
        if (null != rechargeAmount && !rechargeAmount.contains(".")) {
            mRechargeAmountTextView.setText(String.format(getString(R.string.recharge_success_amount), rechargeAmount + ".00"));
        } else {
            mRechargeAmountTextView.setText(String.format(getString(R.string.recharge_success_amount), rechargeAmount));
        }
    }
}