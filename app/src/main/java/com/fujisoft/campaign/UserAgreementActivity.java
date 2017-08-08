package com.fujisoft.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 用户协议页面
 * 包括企业注册协议和个人注册协议
 */
public class UserAgreementActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);

        String from_fragment = String.valueOf(getIntent().getStringExtra("from_fragment"));

        Button userAgreementButton = (Button) findViewById(R.id.user_agreement_button);
        userAgreementButton.setOnClickListener(this);
        WebView agreementData = (WebView) findViewById(R.id.user_agreement_data);

        agreementData.getSettings().setBuiltInZoomControls(true);
        agreementData.getSettings().setUseWideViewPort(true);
        agreementData.getSettings().setLoadWithOverviewMode(true);
        agreementData.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        agreementData.setInitialScale(1);
        if (from_fragment.equals("CompanyRegisterFragment")) {
            agreementData.loadUrl("file:///android_asset/agreement/enterprise_user_agreement.html");
        } else {
            agreementData.loadUrl("file:///android_asset/agreement/personal_user_agreement.html");
        }
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
        tool_bar_center_text_view.setText(R.string.user_agreement_title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_agreement_button) {
            Intent intent = new Intent();
            intent.putExtra("USER_AGREEMENT_FLAG", true);

            //设置返回数据
            setResult(RESULT_OK, intent);
            //关闭Activity
            UserAgreementActivity.this.finish();
        }
    }
}