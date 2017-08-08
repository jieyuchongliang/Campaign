package com.fujisoft.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.fujisoft.campaign.bean.PayResult;
import com.fujisoft.campaign.utils.Constants;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 企业用户若是未激活状态时，调用此类，提示用户交付开户费及年费
 */
public class CompanyPayOpeningFeeActivity extends BaseActivity implements View.OnClickListener {

    Button alipayPay;
    Button wechatPay;
    private String TAG = "campaign";
    private String userId = null;
    private String userType = null;
    private String payRechargeType = null;
    private String mAmount = null;           // 充值/支付的金额数
    private String orderCode = null;        // 充值后，回调服务器的接口时，需要传递的参数
    private String orderStatus = null;     // 充值后，回调服务器接口，返回的支付状态  0：支付不成功  1：支付成功

    private String payOrRecharge = null;  // 支付或者充值的区分
    private String payType = null;        // 付款类型 1：年费 2：元宝
    //    private String price = null;          // 充值金额，当payType为【2】的时候必须
    TextView companyPayAmountText;
    String openingFee;
    // 支付开户费用Handler
    private Handler mPayHandler = new Handler() {
        public void handleMessage(Message msg) {
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_SUCCESS:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#handleMessage(mPayHandler) 9000");
                        payStatusValidate();
                    } else {
                        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#handleMessage(mPayHandler) !9000");
                        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#handleMessage(mPayHandler) resultStatus = " + resultStatus);
                        payCancelOrFail();
                    }
                    break;
                case Constants.CODE_EXECUTE_FAILURE:
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                case Constants.CODE_EXECUTE_CALL_BACK_SUCCESS:
                    Intent intent = new Intent(CompanyPayOpeningFeeActivity.this, PayAndRechargeResultActivity.class);
                    if ("1".equals(orderStatus)) {
                        intent.putExtra(Constants.EXTRA_RECHARGE_RESULT, true);
                        intent.putExtra(Constants.EXTRA_AMOUNT, mAmount);
                        intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_PAY_TYPE);
                        intent.putExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT, openingFee);
                        startActivity(intent);
                        if (null != CompanyRegisterSuccessActivity.companyRegisterSuccessActivity)
                            CompanyRegisterSuccessActivity.companyRegisterSuccessActivity.finish();
                        finish();
                    } else {
                        intent.putExtra(Constants.EXTRA_RECHARGE_RESULT, false);
                        intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_PAY_TYPE);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    // 充值用Handler
    private Handler mRechargeHandler = new Handler() {
        public void handleMessage(Message msg) {
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_SUCCESS:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#handleMessage(mRechargeHandler) 9000");
                        RechargeStatusValidate();
                    } else {
                        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#handleMessage(mRechargeHandler) !9000");
                        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#handleMessage(mRechargeHandler) resultStatus = " + resultStatus);
                        payCancelOrFail();
                    }
                    break;
                case Constants.CODE_EXECUTE_FAILURE:
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                case Constants.CODE_EXECUTE_CALL_BACK_SUCCESS:
                    Intent intent = new Intent(CompanyPayOpeningFeeActivity.this, PayAndRechargeResultActivity.class);
                    if ("1".equals(orderStatus)) {
                        intent.putExtra(Constants.EXTRA_RECHARGE_RESULT, true);
                        intent.putExtra(Constants.EXTRA_AMOUNT, mAmount);
                        intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_RECHARGE_TYPE);
                        intent.putExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT, openingFee);
                        startActivity(intent);
                        finish();
                    } else {
                        intent.putExtra(Constants.EXTRA_RECHARGE_RESULT, false);
                        intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_RECHARGE_TYPE);
                        intent.putExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT, openingFee);
                        intent.putExtra(Constants.EXTRA_AMOUNT, mAmount);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    /**
     * 查询开户费支付状态
     * 企业注册成功缴纳开户费同步回调
     */
    private void payStatusValidate() {
        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#payStatusValidate() 执行支付后的回调接口===");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("orderCode", orderCode));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_ENTERPRISE_PAY_FOR_ACCOUNT_CALL_BACK, params);
                    Log.d(TAG, "=== CompanyPayOpeningFeeActivity#payStatusValidate() json = " + json);
                    //JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#payStatusValidate() msg = " + msg);
                            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
                            orderStatus = dataJsonObject.getString("orderStatus");
                            String price = dataJsonObject.getString("price");
                            //回调成功后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_CALL_BACK_SUCCESS;
                            mPayHandler.sendMessage(message);
                        } else {
                            //回调失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mPayHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    //回调异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mPayHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void payCancelOrFail() {
        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#payCancelOrFail() ===");
    }

    /**
     * 查询开户费支付状态
     * 企业注册成功缴纳开户费同步回调
     */
    private void RechargeStatusValidate() {
        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#RechargeStatusValidate() 执行支付后的回调接口===");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("orderCode", orderCode));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_FINANCE_PAY_CALL_BACK, params);
                    Log.d(TAG, "=== CompanyPayOpeningFeeActivity#RechargeStatusValidate() json = " + json);
                    //JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#RechargeStatusValidate() msg = " + msg);
                            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
                            orderStatus = dataJsonObject.getString("orderStatus");
                            String price = dataJsonObject.getString("price");
                            //回调成功后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_CALL_BACK_SUCCESS;
                            mRechargeHandler.sendMessage(message);
                        } else {
                            //回调失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mRechargeHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    //回调异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mRechargeHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_pay_opening_fee);

        Toast.makeText(CompanyPayOpeningFeeActivity.this, "您是企业用户，请交费后使用！", Toast.LENGTH_SHORT).show();
        openingFee = getIntent().getStringExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT);
        userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        Log.d(TAG, "=== CompanyPayOpeningFeeActivity#onCreate() userId = " + userId);
        userType = getIntent().getStringExtra(Constants.EXTRA_USER_TYPE);
        mAmount = getIntent().getStringExtra(Constants.EXTRA_AMOUNT);
        payType = getIntent().getStringExtra("payType");

        initView();
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);

        payRechargeType = getIntent().getStringExtra(Constants.EXTRA_PAY_RECHARGE_TYPE);

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
        if (Constants.EXTRA_PAY_TYPE.equals(payRechargeType)) {
            tool_bar_center_text_view.setText(R.string.payment);
            payOrRecharge = "PAY";
        } else if (Constants.EXTRA_RECHARGE_TYPE.equals(payRechargeType)) {
            tool_bar_center_text_view.setText(R.string.recharge_title);
            payOrRecharge = "RECHARGE";
        }
    }

    private void initView() {


        // 要交付的开户费及年费
        companyPayAmountText = (TextView) findViewById(R.id.company_pay_amount_text);
        if (!String.valueOf(openingFee).contains(".")) {
            String tempAmount = String.valueOf(openingFee) + ".00";
            companyPayAmountText.setText(String.format(getString(R.string.register_company_success_pay_amount), tempAmount));
        } else {
            companyPayAmountText.setText(String.format(getString(R.string.register_company_success_pay_amount), openingFee));
        }

        wechatPay = (Button) findViewById(R.id.company_wechat_pay_button);
        wechatPay.setOnClickListener(this);
        alipayPay = (Button) findViewById(R.id.company_alipay_pay_button);
        alipayPay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.company_wechat_pay_button:
                if ("PAY".equals(payOrRecharge)) {
                    weChatPay();
                } else if ("RECHARGE".equals(payOrRecharge)) {
                    weChatRecharge();
                } else {
                    Toast.makeText(CompanyPayOpeningFeeActivity.this, "微信支付类型错误！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.company_alipay_pay_button:
                if ("PAY".equals(payOrRecharge)) {
                    aliPay();
                } else if ("RECHARGE".equals(payOrRecharge)) {
                    alipayRecharge();
                } else {
                    Toast.makeText(CompanyPayOpeningFeeActivity.this, "支付宝支付类型错误！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 支付宝支付
     * payWay : 1
     */
    private void aliPay() {
        showProgressDialog();
        Runnable aliPayRunnable = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("payWay", "1"));
                    String json = getPostData(Constants.URL_ENTERPRISE_PAY_FOR_ACCOUNT, params);
                    Log.d(TAG, "=== CompanyPayOpeningFeeActivity#aliPay() json = " + json);
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            JSONObject subJsonObject = jsonObject.getJSONObject("data");
                            String orderInfo = subJsonObject.getString("payInfo");
                            orderCode = subJsonObject.getString("orderCode");
                            String appId = subJsonObject.getString("appId");
                            PayTask alipay = new PayTask(CompanyPayOpeningFeeActivity.this);
                            dismissProgressDialog();
                            Map<String, String> returnValue = alipay.payV2(orderInfo, true);
                            message.obj = returnValue;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mPayHandler.sendMessage(message);
                        } else {
                            dismissProgressDialog();
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mPayHandler.sendMessage(message);
                        }
                    } else {
                        dismissProgressDialog();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mPayHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mPayHandler.sendMessage(message);
                }
            }
        };
        Thread payThread = new Thread(aliPayRunnable);
        payThread.start();
    }

    /**
     * 支付宝充值
     * payWay : 1
     * payType：1：年费 2：元宝
     */
    private void alipayRecharge() {
        showProgressDialog();
        Runnable aliPayRunnable = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("payWay", "1"));
                    if ("2".equals(payType)) {// 当payType为【2】的时候，需要传price的值
                        params.add(new BasicNameValuePair("payType", payType));
                        params.add(new BasicNameValuePair("price", mAmount));
                    } else {
                        params.add(new BasicNameValuePair("payType", payType));
                    }
                    String json = getPostData(Constants.URL_FINANCE_PAY, params);
                    Log.d(TAG, "=== CompanyPayOpeningFeeActivity#aliPay() json = " + json);
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            JSONObject subJsonObject = jsonObject.getJSONObject("data");
                            String orderInfo = subJsonObject.getString("payInfo");
                            orderCode = subJsonObject.getString("orderCode");
                            String appId = subJsonObject.getString("appId");
                            PayTask alipay = new PayTask(CompanyPayOpeningFeeActivity.this);
                            dismissProgressDialog();
                            Map<String, String> returnValue = alipay.payV2(orderInfo, true);
                            message.obj = returnValue;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mRechargeHandler.sendMessage(message);
                        } else {
                            dismissProgressDialog();
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mRechargeHandler.sendMessage(message);
                        }
                    } else {
                        dismissProgressDialog();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mRechargeHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mRechargeHandler.sendMessage(message);
                }
            }
        };
        Thread payThread = new Thread(aliPayRunnable);
        payThread.start();
    }

    /**
     * 微信支付
     * payWay : 2
     */
    private void weChatPay() {
        showProgressDialog();
        Runnable weChatPayRunnable = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "";
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", userId));
                params.add(new BasicNameValuePair("payWay", "2"));
                String json = getPostData(Constants.URL_ENTERPRISE_PAY_FOR_ACCOUNT, params);
                Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() json = " + json);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            JSONObject subJsonObject = jsonObject.getJSONObject("data");
                            String orderCode = subJsonObject.getString("orderCode");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() orderCode" + orderCode);

                            JSONObject payInfo = subJsonObject.getJSONObject("payInfo");
                            IWXAPI api = WXAPIFactory.createWXAPI(CompanyPayOpeningFeeActivity.this, payInfo.getString("appid"), true);
                            api.registerApp(payInfo.getString("appid"));
                            PayReq payRequest = new PayReq();

                            payRequest.appId = payInfo.getString("appid");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.appId" + payRequest.appId);

                            payRequest.partnerId = payInfo.getString("partnerid");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.partnerId" + payRequest.partnerId);

                            payRequest.prepayId = payInfo.getString("prepayid");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.prepayId" + payRequest.prepayId);

                            payRequest.packageValue = "Sign=WXPay";
                            payRequest.nonceStr = payInfo.getString("noncestr");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.nonceStr" + payRequest.nonceStr);

                            payRequest.timeStamp = payInfo.getString("timestamp");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.timeStamp" + payRequest.timeStamp);

                            payRequest.sign = payInfo.getString("sign");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.sign" + payRequest.sign);
                            api.sendReq(payRequest);
                        } else {
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mPayHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_EXCEPTION;
                        mPayHandler.sendMessage(message);
                    }
                }
            }
        };
        Thread weChatPayThread = new Thread(weChatPayRunnable);
        weChatPayThread.start();
    }

    /**
     * 微信充值
     * payWay : 2
     * payType：1：年费 2：元宝
     */
    private void weChatRecharge() {
        showProgressDialog();
        Runnable weChatPayRunnable = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "";
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", userId));
                params.add(new BasicNameValuePair("payWay", "2"));
                if ("2".equals(payType)) {// 当payType为【2】的时候，需要传price的值
                    params.add(new BasicNameValuePair("payType", payType));
                    params.add(new BasicNameValuePair("price", mAmount));
                } else {
                    params.add(new BasicNameValuePair("payType", payType));
                }
                String json = getPostData(Constants.URL_FINANCE_PAY, params);
                Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() json = " + json);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.getString("msg");
                        if (result) {
                            JSONObject subJsonObject = jsonObject.getJSONObject("data");
                            String orderCode = subJsonObject.getString("orderCode");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() orderCode" + orderCode);

                            JSONObject payInfo = subJsonObject.getJSONObject("payInfo");
                            IWXAPI api = WXAPIFactory.createWXAPI(CompanyPayOpeningFeeActivity.this, payInfo.getString("appid"), true);
                            api.registerApp(payInfo.getString("appid"));
                            PayReq payRequest = new PayReq();

                            payRequest.appId = payInfo.getString("appid");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.appId" + payRequest.appId);

                            payRequest.partnerId = payInfo.getString("partnerid");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.partnerId" + payRequest.partnerId);

                            payRequest.prepayId = payInfo.getString("prepayid");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.prepayId" + payRequest.prepayId);

                            payRequest.packageValue = "Sign=WXPay";
                            payRequest.nonceStr = payInfo.getString("noncestr");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.nonceStr" + payRequest.nonceStr);

                            payRequest.timeStamp = payInfo.getString("timestamp");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.timeStamp" + payRequest.timeStamp);

                            payRequest.sign = payInfo.getString("sign");
                            Log.d(TAG, "=== CompanyPayOpeningFeeActivity#weChatPay() payRequest.sign" + payRequest.sign);
                            api.sendReq(payRequest);
                        } else {
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mRechargeHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_EXCEPTION;
                        mRechargeHandler.sendMessage(message);
                    }
                }
            }
        };
        Thread weChatPayThread = new Thread(weChatPayRunnable);
        weChatPayThread.start();
    }
}
