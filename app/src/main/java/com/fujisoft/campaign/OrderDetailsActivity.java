package com.fujisoft.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.bean.OrderBean;
import com.fujisoft.campaign.utils.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends BaseActivity {
    private String TAG="campaign";
    private SimpleDraweeView order_goods_image;
    private TextView order_goods_title;
    private TextView order_scores;
    private TextView order_amount;
    private TextView order_order_number;
    private TextView order_detail_receiver;
    private TextView order_detail_contact_number;
    private TextView order_detail_express;
    private TextView order_detail_ship_address;
    private Button order_detail_ship_status_button;
    private String orderId;
    private OrderBean mOrder;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_ORDER_GET_SUCCESS:
                    refreshViews();
                    setResult(Constants.CODE_ORDER_GET_SUCCESS);
                    break;
                case Constants.CODE_ORDER_GET_EXCEPTION:
                    //showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    finish();
                default:
                    break;
            }
        }

    };

    private void refreshViews() {
        if (mOrder != null) {

            order_goods_image.setImageURI(Constants.PICTURE_BASE_URL + mOrder.getGoodsPicture());
            order_goods_title.setText(mOrder.getGoodsName());
            order_scores.setText(mOrder.getScorePrice());
            order_amount.setText(mOrder.getGoodsQuantity());
            order_order_number.setText(mOrder.getOrderCode());
            order_detail_receiver.setText(mOrder.getName().toString());
            order_detail_contact_number.setText(mOrder.getPhone().toString());
            order_detail_ship_address.setText(mOrder.getAddress().toString());
            order_detail_express.setText(mOrder.getOrderExpress().toString());

            if ("1".equals(mOrder.getOrderStatus())) {
                order_detail_ship_status_button.setText(getString(R.string.goods_detail_delivery_wait));
            } else if ("2".equals(mOrder.getOrderStatus())) {
                order_detail_ship_status_button.setText(getString(R.string.goods_detail_delivery_already));
            } else {
                order_detail_ship_status_button.setText(getString(R.string.goods_detail_finish));
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Intent getIntent = getIntent();
        orderId = String.valueOf(getIntent.getIntExtra(Constants.EXTRA_ORDER_ID, 0));
        initViews();
        queryTaskDetails();
    }

    private void queryTaskDetails() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("orderId", orderId));
//                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_ORDER_DETAILS, params);
                    Log.d(TAG, "===OrderDetailsActivity#queryTaskDetails() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        if (result) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            Log.d(TAG, "===OrderDetailsActivity#queryTaskDetails() jsonObject = " + jsonObject);
                            JSONObject orderData = dataObject.getJSONObject("orderInfo");
                            mOrder = new OrderBean();
                            mOrder.setOrderId(orderData.get("id").toString());
                            mOrder.setGoodsName(orderData.get("goodsName").toString());
                            mOrder.setGoodsPicture(orderData.get("goodsPicture").toString());
                            mOrder.setScorePrice(orderData.get("goodsScorePrice").toString());
                            mOrder.setGoodsQuantity(orderData.get("goodsQuantity").toString());
                            mOrder.setOrderCode(orderData.get("orderCode").toString());
                            mOrder.setName(orderData.get("name").toString());
                            mOrder.setPhone(orderData.get("phone").toString());
                            mOrder.setAddress(orderData.get("address").toString());
                            mOrder.setOrderStatus(orderData.get("orderStatus").toString());
                            String expressInfoString = "";
                            //物流信息
                            JSONArray expressInfoListArray = dataObject.getJSONArray("expressInfo");

                            if (expressInfoListArray.length() != 0) {
                                Log.d(TAG, "===OrderDetailsActivity#queryTaskDetails() expressInfoListArray = " + expressInfoListArray);
                                int listLength = expressInfoListArray.length();
                                for (int i = 0; i < listLength; i++) {
                                    JSONObject expressData = expressInfoListArray.getJSONObject(i);
                                    Log.d(TAG, "===OrderDetailsActivity#queryTaskDetails() expressData = " + expressData);
                                    expressInfoString += expressData.get("AcceptStation").toString();
                                    expressInfoString += "\n".toString();
                                    expressInfoString += expressData.get("AcceptTime").toString();
                                    expressInfoString += "\n".toString();
                                    expressInfoString += "\n".toString();
                                }
                            } else {
                                expressInfoString = "暂无物流信息";
                            }
                            mOrder.setOrderExpress(expressInfoString);
                        }
                        mHandler.sendEmptyMessage(Constants.CODE_ORDER_GET_SUCCESS);
                    }

                } catch (Exception e) {
                    // 获取订单详情异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_ORDER_GET_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void initViews() {
        order_goods_image = (SimpleDraweeView) findViewById(R.id.order_goods_image);
        order_goods_title = (TextView) findViewById(R.id.order_goods_title);
        order_scores = (TextView) findViewById(R.id.order_scores);
        order_amount = (TextView) findViewById(R.id.order_amount);
        order_order_number = (TextView) findViewById(R.id.order_order_number);
        order_detail_receiver = (TextView) findViewById(R.id.order_detail_receiver);
        order_detail_contact_number = (TextView) findViewById(R.id.order_detail_contact_number);
        order_detail_ship_address = (TextView) findViewById(R.id.order_detail_ship_address);
        order_detail_ship_status_button = (Button) findViewById(R.id.order_detail_ship_status_button);
        order_detail_express = (TextView) findViewById(R.id.order_detail_express);
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
        tool_bar_center_text_view.setText(R.string.order_details_title);
    }
}
