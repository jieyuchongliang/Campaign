package com.fujisoft.campaign.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fujisoft.campaign.PayErrorActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.utils.Constants;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d("aaa", "=== WXPayEntryActivity#onResp() onPayFinish, errCode = " + resp.errCode);
//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.app_tip);
//            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//            builder.show();
//        }
        // TODO: 2017/8/7 王强
        Log.i(TAG, "--------------状态码: " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
            if (resp.errCode == 0){
                // TODO: 2017/8/7 支付成功，后期可添加相应操作
            }else if(resp.errCode == -1){
                // 支付失败跳转到失败界面
                startActivity(new Intent(this,PayErrorActivity.class));
            }else if(resp.errCode == -2) {
                // TODO: 2017/8/8 取消支付，后期可添加相应操作
            }
        }
        finish();
    }
    private static final String TAG = "campaign";
}