package com.fujisoft.campaign;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.fujisoft.campaign.adapter.ShoppingCartAdapter;
import com.fujisoft.campaign.bean.PayResult;
import com.fujisoft.campaign.bean.ShopCartBean;
import com.fujisoft.campaign.service.ShopCartService;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fujisoft.campaign.utils.Utils.Retrofit;

/**
 * 购物车页面
 */
public class ShoppingCartActivity extends BaseActivity {

    private String TAG = "campaign";
    private ListView shoppingCartList = null;
    private String userId = null;
    private String scores = null;
    private String addId = null;
    private String orderCode = null;
    private ShopCartService shopCartService;
    private RadioButton txt_aliPay;
    private RadioButton txt_weChat;
    private LinearLayout  txt_payTyle_layout;
    private TextView txtAddress;
    private TextView txtName;
    private TextView txtTel;

    private TextView txtFreightPrice;

    SwitchCompat aSwitchCompat;
    private LinearLayout list_adres = null;
    ShopCartBean.Address address;
    String payWay;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_SUCCESS:
                    if ("3".equals(payWay)) {

                        showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                        Intent intent = new Intent(ShoppingCartActivity.this, OrderListActivity.class);
                        intent.putExtra(Constants.EXTRA_USER_ID, userId);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Log.d(TAG, "=== PayAndRechargeActivity#handleMessage(mPayHandler) 9000");
                        payStatusValidate();
                    } else {
                        Log.d(TAG, "=== PayAndRechargeActivity#handleMessage(mPayHandler) !9000");
                        Log.d(TAG, "=== PayAndRechargeActivity#handleMessage(mPayHandler) resultStatus = " + resultStatus);
                        payCancelOrFail();
                    }
                    break;
                case Constants.CODE_EXECUTE_FAILURE:
                    Toast.makeText(ShoppingCartActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

        if (null != Retrofit) {
            shopCartService = Retrofit.create(ShopCartService.class);
        } else {
            Utils.getRetrofit(ShoppingCartActivity.this);
            shopCartService = Retrofit.create(ShopCartService.class);
        }
        initView();
        loadShoppingCartData(RefreshMode.Load);
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        getLayoutInflater().inflate(R.layout.toolbar_button, toolbar);
        ImageButton backIB = (ImageButton) findViewById(R.id.tool_bar_back_button);
        backIB.setVisibility(View.VISIBLE);
        backIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        TextView shoppingCarTxt = (TextView) findViewById(R.id.tool_bar_center_text_view);
        shoppingCarTxt.setVisibility(View.VISIBLE);
        shoppingCarTxt.setText(R.string.shopping_cart_title);
    }

    /**
     * View的初始化
     */
    private void initView() {
        shoppingCartList = (ListView) findViewById(R.id.shopping_cart_goods_list);
        // 商品列表的头部
        View headerView = LayoutInflater.from(ShoppingCartActivity.this).inflate(R.layout.shopping_cart_goods_header, null);
        // 商品列表的尾部
        View footerView = LayoutInflater.from(ShoppingCartActivity.this).inflate(R.layout.shopping_cart_goods_footer, null);
        txtAddress = (TextView) footerView.findViewById(R.id.txt_address);
        txtName = (TextView) footerView.findViewById(R.id.txt_name);
        txtTel = (TextView) footerView.findViewById(R.id.txt_tel);

        if (shoppingCartList.getHeaderViewsCount() == 0) {
            shoppingCartList.addHeaderView(headerView);
        }
        if (shoppingCartList.getFooterViewsCount() == 0) {
            shoppingCartList.addFooterView(footerView);
        }
        txt_payTyle_layout=(LinearLayout)footerView.findViewById(R.id.txt_payTyle_layout);
        txt_aliPay = (RadioButton) footerView.findViewById(R.id.txt_aliPay);
        txt_weChat = (RadioButton) footerView.findViewById(R.id.txt_weChat);


        txtFreightPrice = (TextView) footerView.findViewById(R.id.txt_freightPrice);

        aSwitchCompat = (SwitchCompat) footerView.findViewById(R.id.swi_payment);
        aSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txt_aliPay.setEnabled(false);
                    txt_weChat.setEnabled(false);
                    txt_payTyle_layout.setVisibility(View.GONE);
                    txtFreightPrice.setText("￥" + "0");
                } else {
                    txt_aliPay.setEnabled(true);
                    txt_weChat.setEnabled(true);

                    txt_payTyle_layout.setVisibility(View.VISIBLE);
                    if(address!= null){

                        if (address.getPrice() != null) {

                            txtFreightPrice.setText("￥" + address.getPrice());
                        }
                    }

                }
            }
        });
        list_adres = (LinearLayout) findViewById(R.id.list_adres);
        list_adres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingCartActivity.this, AddressManageActivity.class);
                ShoppingCartActivity.this.startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * 购物车数据初始化
     */
    private void loadShoppingCartData(final RefreshMode Mode) {
        showProgressDialog();
        Call<ShopCartBean> call = shopCartService.getCartInfo(userId);
        call.enqueue(new Callback<ShopCartBean>() {
            @Override
            public void onResponse(Call<ShopCartBean> call, Response<ShopCartBean> response) {
                dismissProgressDialog();
                ShopCartBean result = response.body();
                if (null != result) {
                    ShopCartBean.ShopCartData ds = result.getData();
                    Log.i(TAG, "onResponse: " + result.toString());
                    if (Mode == RefreshMode.LoadAdd) {
                        address = ds.getAddress();
                        if (null != address) {
                            txtAddress.setText(address.getAddress());
                            addId = address.getId();
                            txtName.setText(address.getName());
                            txtTel.setText(address.getPhone());
                            if (aSwitchCompat.isChecked()) {
                                txtFreightPrice.setText("￥" + "0");
                            } else {
                                txtFreightPrice.setText("￥" + address.getPrice());
                            }
                        } else {
                            txtAddress.setText("收货地址为空，点击添加地址");
                        }
                        return;
                    }
                    // 购物车商品展示
                    List<ShopCartBean.Goods> goodsList = ds.getList();
                    if (goodsList != null && goodsList.size() > 0) {
                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout_balance);
                        relativeLayout.setVisibility(View.VISIBLE);
                        ShoppingCartAdapter shoppingCartAdapter = new ShoppingCartAdapter<ShopCartBean.Goods>(goodsList, R.layout.shopping_cart_item) {
                            @Override
                            public void bindView(ViewHolder holder, ShopCartBean.Goods obj) {
                                holder.setImageResource(R.id.shopping_cart_good_img, obj.getGoodsLogo());
                                holder.setText(R.id.shopping_cart_good_title, obj.getGoodsName());
                                holder.setText(R.id.flower_price_txt, String.valueOf(obj.getScorePrice()));
                                holder.setText(R.id.good_num, String.format("×%1$s", obj.getGoodsCnt()));
                                holder.setTag(R.id.shopping_cart_remove_button, obj.getCartId());
                                holder.setOnClickListener(R.id.shopping_cart_remove_button, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDeleteDialog(String.valueOf(v.getTag()));
                                    }
                                });
                            }
                        };
                        shoppingCartList.setAdapter(shoppingCartAdapter);
                        TextView txtSubtotal = (TextView) findViewById(R.id.txt_subtotal);
                        scores = ds.getMoney();
                        txtSubtotal.setText(scores + "鲜花");
                        if (Mode == RefreshMode.Load) {
                            address = ds.getAddress();
                            if (null != address) {
                                addId = address.getId();
                                txtAddress.setText(address.getAddress());
                                txtName.setText(address.getName());
                                txtTel.setText(address.getPhone());

                                if (aSwitchCompat.isChecked()) {
                                    txtFreightPrice.setText("￥" + "0");
                                } else {
                                    txtFreightPrice.setText("￥" + address.getPrice());
                                }

                            } else {
                                txtAddress.setText("收货地址为空，点击添加地址");
                            }
                        }
                    } else {
                        RelativeLayout layoutNone = (RelativeLayout) findViewById(R.id.layout_none);
                        layoutNone.setVisibility(View.VISIBLE);
                        RelativeLayout layoutBalance = (RelativeLayout) findViewById(R.id.layout_balance);
                        layoutBalance.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ShopCartBean> call, Throwable throwable) {
            }
        });
    }

    /**
     * 商品删除提示Dialog
     *
     * @param cartId
     */
    private void showDeleteDialog(final String cartId) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定要删除此商品吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delShoppingCartData(cartId);
                    }
                }).create();
        dialog.show();
    }

    /**
     * 删除购物车
     *
     * @param cartId
     */
    private void delShoppingCartData(String cartId) {
        showProgressDialog();
        Call<ShopCartBean> call = shopCartService.delCartInfo(userId, cartId);
        call.enqueue(new Callback<ShopCartBean>() {
            @Override
            public void onResponse(Call<ShopCartBean> call, Response<ShopCartBean> response) {
                loadShoppingCartData(RefreshMode.Reload);
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ShopCartBean> call, Throwable throwable) {
            }
        });
    }

    /**
     * 结算按钮点击后，调用相应的支付方式
     *
     * @param target
     */
    public void balanceClick(View target) {
        payWay = "1";

        if (aSwitchCompat.isChecked()) {
            payWay = "3";
        } else {
            if (txt_aliPay.isChecked()) {
                payWay = "1";
            } else {
                payWay = "2";
            }
        }
        Log.d(TAG, "=== ShoppingCartActivity#balanceClick() payWay = " + payWay);
        showProgressDialog();
        if (TextUtils.equals(payWay, "1")) {
            aliPay();
        } else if (TextUtils.equals(payWay, "2")) {
            weChatPay();
        } else {
            noPay();
        }
    }

    /**
     * 貨到付款
     */
    private void noPay() {
        Runnable aliPayRunnable = new Runnable() {
            Message message = new Message();
            String msg = "Exception";

            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", userId));
                params.add(new BasicNameValuePair("scores", scores));
                params.add(new BasicNameValuePair("addId", addId));
                params.add(new BasicNameValuePair("payWay", "3"));
                String json = getPostData(Constants.URL_MALL_ORDER, params);
                Log.d(TAG, "=== ShoppingCartActivity#noPay() json = " + json);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            message.obj = msg;
                            mHandler.sendMessage(message);
                        } else {
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_EXCEPTION;
                        mHandler.sendMessage(message);
                    }
                }
            }
        };
        Thread payThread = new Thread(aliPayRunnable);
        payThread.start();
    }


    /**
     * 支付宝支付
     */
    private void aliPay() {
        Runnable aliPayRunnable = new Runnable() {
            Message message = new Message();
            String msg = "Exception";

            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", userId));
                params.add(new BasicNameValuePair("scores", scores));
                params.add(new BasicNameValuePair("addId", addId));
                params.add(new BasicNameValuePair("payWay", "1"));
                Log.d("tag", "------------------------------------------params: " + userId +"---" + scores +"---" + addId);
                String json = getPostData(Constants.URL_MALL_ORDER, params);
                Log.d(TAG, "=== ShoppingCartActivity#aliPay() json = " + json);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            JSONObject subJsonObject = jsonObject.getJSONObject("data");
                            String orderInfo = subJsonObject.getString("payInfo");
                            orderCode = subJsonObject.getString("orderCode");
                            PayTask alipay = new PayTask(ShoppingCartActivity.this);
                            Map<String, String> returnValue = alipay.payV2(orderInfo, true);
                            Log.d(TAG, "=== ShoppingCartActivity#aliPay() returnValue = " + returnValue.toString());
                            message.obj = returnValue;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mHandler.sendMessage(message);
                        } else {
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_EXCEPTION;
                        mHandler.sendMessage(message);
                    }
                }
            }
        };
        Thread payThread = new Thread(aliPayRunnable);
        payThread.start();
    }

    /**
     * 微信支付
     */
    private void weChatPay() {
        Runnable weChatPayRunnable = new Runnable() {
            @Override
            public void run() {
                String msg = "Exception";
                Message message = new Message();
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", userId));
                params.add(new BasicNameValuePair("scores", scores));
                params.add(new BasicNameValuePair("addId", addId));
                params.add(new BasicNameValuePair("payWay", "2"));
                Log.d("tag", "------------------------------------------params: " + userId +"---" + scores +"---" + addId);
                String json = getPostData(Constants.URL_MALL_ORDER, params);
                Log.d(TAG, "=== ShoppingCartActivity#weChatPay() json = " + json);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            JSONObject subJsonObject = jsonObject.getJSONObject("data");
                            JSONObject payInfo = subJsonObject.getJSONObject("payInfo");
                            IWXAPI api = WXAPIFactory.createWXAPI(ShoppingCartActivity.this, null);
                            api.registerApp(payInfo.getString("appid"));
                            PayReq payRequest = new PayReq();
                            payRequest.appId = payInfo.getString("appid");
                            payRequest.partnerId = payInfo.getString("partnerid");
                            payRequest.prepayId = payInfo.getString("prepayid");
                            payRequest.packageValue = "Sign=WXPay";
                            payRequest.nonceStr = payInfo.getString("noncestr");
                            payRequest.timeStamp = payInfo.getString("timestamp");
                            payRequest.sign = payInfo.getString("sign");
                            Log.i(TAG, "--------------------" + payRequest.appId);
                            Log.i(TAG, "--------------------" +payRequest.partnerId);
                            Log.i(TAG, "--------------------"+payRequest.prepayId);
                            Log.i(TAG, "--------------------"+ payRequest.packageValue);
                            Log.i(TAG, "--------------------"+payRequest.nonceStr);
                            Log.i(TAG, "--------------------"+payRequest.timeStamp);
                            Log.i(TAG, "--------------------"+payRequest.sign);
                            api.sendReq(payRequest);
                            dismissProgressDialog();//启动微信支付后，关闭进度提示。
                        } else {
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_EXCEPTION;
                        mHandler.sendMessage(message);
                    }
                }
            }
        };
        Thread weChatPayThread = new Thread(weChatPayRunnable);
        weChatPayThread.start();
    }

    /**
     * 支付结果验证
     */
    private void payStatusValidate() {
        Log.d(TAG, "=== ShoppingCartActivity#payStatusValidate() orderCode" + orderCode);
        Call<ShopCartBean> call = shopCartService.checkOrder(orderCode);
        call.enqueue(new Callback<ShopCartBean>() {
            @Override
            public void onResponse(Call<ShopCartBean> call, Response<ShopCartBean> response) {
                ShopCartBean result = response.body();
                Log.d(TAG, "=== ShoppingCartActivity#payStatusValidate() result" + result.toString());

                if (null != result) {
                    ShopCartBean.ShopCartData ds = result.getData();
                    if (ds == null) {
                        return;
                    }

                    String orderStatus = ds.getOrderStatus();
                    /*if ((!TextUtils.equals(orderStatus, "0")) && (!TextUtils.equals(orderStatus, ""))) {
                        showMsg(result.getMsg(), Toast.LENGTH_SHORT);
                        Intent intent = new Intent(ShoppingCartActivity.this, OrderListActivity.class);
                        intent.putExtra(Constants.EXTRA_USER_ID, userId);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(ShoppingCartActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }*/
                    Intent intent = new Intent(ShoppingCartActivity.this, PayAndRechargeResultActivity.class);
                    if ((!TextUtils.equals(orderStatus, "0")) && (!TextUtils.equals(orderStatus, ""))){
                        intent.putExtra(Constants.EXTRA_RECHARGE_RESULT, true);
                        intent.putExtra(Constants.EXTRA_AMOUNT, address.getPrice());
                        intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_PAY_TYPE);
                        startActivity(intent);
                        finish();
                    } else {
                        intent.putExtra(Constants.EXTRA_RECHARGE_RESULT, false);
                        intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_PAY_TYPE);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<ShopCartBean> call, Throwable throwable) {
            }
        });
    }

    /**
     * 支付取消/失败
     */
    private void payCancelOrFail() {
        Call<ShopCartBean> call = shopCartService.reOrder(userId, orderCode);
        call.enqueue(new Callback<ShopCartBean>() {
            @Override
            public void onResponse(Call<ShopCartBean> call, Response<ShopCartBean> response) {
            }

            @Override
            public void onFailure(Call<ShopCartBean> call, Throwable throwable) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadShoppingCartData(RefreshMode.LoadAdd);
    }

    /**
     * 刷新方式
     */
    public static enum RefreshMode {
        Reload,
        Load,
        LoadAdd,
    }
}