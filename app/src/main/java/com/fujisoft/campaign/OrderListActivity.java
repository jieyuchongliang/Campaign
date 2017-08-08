package com.fujisoft.campaign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fujisoft.campaign.adapter.OrderListRecyclerViewAdapter;
import com.fujisoft.campaign.bean.OrderBean;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表页面
 */
public class OrderListActivity extends BaseActivity {
    private String TAG = "campaign";
    /**
     * 当前页
     */
    private int pageNum = 1;
    /**
     * 总页数
     */
    private int pageSum;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private OrderListRecyclerViewAdapter recycleAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private ImageView noneImage;
    private String userId = null;
//    private String userType = null;

    private List<OrderBean> orderLists;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                   //showErrorDialog(Constants.CODE_EXECUTE_FAILURE);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    pageNum = 1;
                    recycleAdapter = new OrderListRecyclerViewAdapter(OrderListActivity.this, orderLists, true);
                    mRecyclerView.setAdapter(recycleAdapter);
                    break;
                case Constants.CODE_PULL_DOWN_REFRESH:
                    pageNum = 1;
                    recycleAdapter = new OrderListRecyclerViewAdapter(OrderListActivity.this, orderLists, true);
                    mRecyclerView.setAdapter(recycleAdapter);
                    //刷新完成
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case Constants.CODE_LOADED_ALL:
                    //没有加载更多了
                    recycleAdapter.changeMoreStatus(recycleAdapter.NO_LOAD_MORE);
                    recycleAdapter.notifyDataSetChanged();

                    break;
                case Constants.CODE_TOP_PULL_LOADING:
                    recycleAdapter.changeMoreStatus(recycleAdapter.LOADING_MORE);
                    recycleAdapter.addFooterItem(orderLists);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    Utils.showToast(OrderListActivity.this, R.string.login_toast_exception);
                    setResult(Constants.CODE_EXECUTE_EXCEPTION);
                    break;

                case Constants.CODE_INIT_VIEW_NULL:
                    noneImage.setVisibility(View.VISIBLE);
                    setResult(Constants.CODE_INIT_VIEW_NULL);
                    break;
                default:
                    break;
            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        initView();
        initListener();
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
        shoppingCarTxt.setText(R.string.mine_my_order_list_text);

    }

    @Override
    protected void onResume() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG, "=OrderListActivity== onResume() 从SP中取到的userId = " + userId);
        initData();
        super.onResume();
    }

    private void initListener() {
        initPullRefresh();

        initLoadMoreListener();
    }

    private void initPullRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isConnect(OrderListActivity.this)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int result = updateData(1);
                            switch (result) {
                                case Constants.CODE_EXECUTE_SUCCESS:
                                    mHandler.sendEmptyMessage(Constants.CODE_PULL_DOWN_REFRESH);
                                    break;
                                case Constants.CODE_EXECUTE_FAILURE:
                                    mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                                    break;
                                case Constants.CODE_EXECUTE_EXCEPTION:
                                    mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                                    break;
                                case Constants.CODE_INIT_VIEW_NULL:
                                    mHandler.sendEmptyMessage(Constants.CODE_INIT_VIEW_NULL);
                                    break;
                            }
                        }
                    }).start();
                } else {
                    Utils.showToast(OrderListActivity.this, R.string.netWrong);
                }


            }
        });
    }

    private void initLoadMoreListener() {

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (orderLists != null && orderLists.size() > 0) {
                    //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recycleAdapter.getItemCount()) {
                        if (Utils.isConnect(OrderListActivity.this)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (pageNum < pageSum) {
                                        pageNum = pageNum + 1;
                                        int result = updateData(pageNum);
                                        switch (result) {
                                            case Constants.CODE_EXECUTE_SUCCESS:
                                                mHandler.sendEmptyMessage(Constants.CODE_TOP_PULL_LOADING);
                                                break;
                                            case Constants.CODE_EXECUTE_FAILURE:
                                                mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                                                break;
                                            case Constants.CODE_EXECUTE_EXCEPTION:
                                                mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                                                break;
                                            case Constants.CODE_INIT_VIEW_NULL:
                                                mHandler.sendEmptyMessage(Constants.CODE_INIT_VIEW_NULL);
                                                break;
                                        }
                                    } else {
                                        mHandler.sendEmptyMessage(Constants.CODE_LOADED_ALL);
                                    }
                                }
                            }).start();
                        } else {
                            Utils.showToast(OrderListActivity.this, R.string.netWrong);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

    }

    private void initData() {
        if (Utils.isConnect(this)) {
            showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = updateData(1);
                    switch (result) {
                        case Constants.CODE_EXECUTE_SUCCESS:
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_SUCCESS);
                            break;
                        case Constants.CODE_EXECUTE_FAILURE:
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                            break;
                        case Constants.CODE_EXECUTE_EXCEPTION:
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                            break;
                        case Constants.CODE_INIT_VIEW_NULL:
                            mHandler.sendEmptyMessage(Constants.CODE_INIT_VIEW_NULL);
                            break;
                    }
                }
            }).start();
        } else {
            Utils.showToast(this, R.string.netWrong);
        }
    }

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置增加或删除条目的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.order_list_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.order_list_recycler_view);

        noneImage = (ImageView) findViewById(R.id.order_list_none_image);
        noneImage.setVisibility(View.GONE);

        initRecyclerView();
    }

    /**
     * 显示Error Dialog
     *
     * @param msgId
     */
    private void showErrorDialog(int msgId) {
        String dialogMessage = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderListActivity.this);
        builder.setTitle(getString(R.string.login_dialog_hint))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        switch (msgId) {
            case Constants.CODE_EXECUTE_FAILURE:
                dialogMessage = getString(R.string.loading_index_view_exception);
                break;
        }
        builder.setMessage(dialogMessage);
        builder.create().show();
    }

    private int updateData(int pageNumber) {
        try {
            orderLists = new ArrayList<>();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pageNum", String.valueOf(pageNumber)));
            params.add(new BasicNameValuePair("id", userId));

            // 获取响应的结果信息
            String json = getPostData(Constants.URL_ORDER_INDEX, params);
            // JSON的解析过程
            boolean result = false;
            String msg = "";
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                Log.d(TAG, "===OrderListActivity #updateData() jsonObject = " + jsonObject);
                result = jsonObject.getBoolean("success");
                if (result) {
                    if ("无数据".equals(jsonObject.getString("msg"))) {
                        return Constants.CODE_INIT_VIEW_NULL;
                    }
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    JSONArray orderListArray = dataObject.getJSONArray("orderList");
                    pageSum = Integer.parseInt(dataObject.get("totalNum").toString());
                    OrderBean mOrder;
                    if (pageSum == 0) {
                        return Constants.CODE_INIT_VIEW_NULL;
                    }
                    int listLength = orderListArray.length();
                    for (int i = 0; i < listLength; i++) {

                        JSONObject orderData = orderListArray.getJSONObject(i);
                        mOrder = new OrderBean();
                        mOrder.setOrderId(orderData.get("id").toString());
                        mOrder.setGoodsName(orderData.get("goodsName").toString());
                        mOrder.setGoodsPicture(orderData.get("goodsPicture").toString());
                        mOrder.setScorePrice(orderData.get("ScorePrice").toString());
                        mOrder.setGoodsQuantity(orderData.get("goodsQuantity").toString());
                        mOrder.setOrderCode(orderData.get("orderCode").toString());
                        mOrder.setName(orderData.get("name").toString());
                        mOrder.setPhone(orderData.get("phone").toString());
                        mOrder.setAddress(orderData.get("address").toString());
                        mOrder.setOrderStatus(orderData.get("orderStatus").toString());
                        orderLists.add(mOrder);
                    }
                    // 成功后的处理
                    return Constants.CODE_EXECUTE_SUCCESS;
                } else {
                    // 失败后的处理
                    return Constants.CODE_EXECUTE_FAILURE;
                }
            } else {
                // 异常后的处理
                return Constants.CODE_EXECUTE_EXCEPTION;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 异常后的处理
            return Constants.CODE_EXECUTE_EXCEPTION;
        }
    }
}
