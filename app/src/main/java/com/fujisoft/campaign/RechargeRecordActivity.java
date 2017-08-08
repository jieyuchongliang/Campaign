package com.fujisoft.campaign;

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
import android.widget.Toast;

import com.fujisoft.campaign.adapter.RechargeRecordAdapter;
import com.fujisoft.campaign.bean.RechargeRecordBean;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 充值记录页面
 */
public class RechargeRecordActivity extends BaseActivity {
    private String TAG = "campaign";
    private String userId = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rechargeRecordRecyclerList = null;
    private LinearLayoutManager linearLayoutManager;
    private RechargeRecordAdapter recycleAdapter = null;
    private List<RechargeRecordBean> rechargeRecordList = null;
    private ImageView noneImage;

    private int pageNum = 1;
    private int pageSum = 0;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            String toastMsg = null;
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    toastMsg = "数据请求失败！";
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    //toastMsg = "数据请求成功！";
                    pageNum = 1;
                    recycleAdapter = new RechargeRecordAdapter(RechargeRecordActivity.this, rechargeRecordList);
                    rechargeRecordRecyclerList.setAdapter(recycleAdapter);
                    break;
                case Constants.CODE_PULL_DOWN_REFRESH:
                    //toastMsg = "刷新成功！";
                    pageNum = 1;
                    recycleAdapter = new RechargeRecordAdapter(RechargeRecordActivity.this, rechargeRecordList);
                    rechargeRecordRecyclerList.setAdapter(recycleAdapter);
                    //刷新完成
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case Constants.CODE_LOADED_ALL:
                    //toastMsg = "没有加载更多了！";
                    //没有加载更多了
                    recycleAdapter.changeMoreStatus(recycleAdapter.NO_LOAD_MORE);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case Constants.CODE_TOP_PULL_LOADING:
                    //toastMsg = "上拉加载成功！";
                    recycleAdapter.changeMoreStatus(recycleAdapter.LOADING_MORE);
                    recycleAdapter.addFooterItem(rechargeRecordList);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    toastMsg = getResources().getString(R.string.login_toast_exception);
                    setResult(Constants.CODE_EXECUTE_EXCEPTION);
                    break;
                case Constants.CODE_INIT_VIEW_NULL:
                    noneImage.setVisibility(View.VISIBLE);
                    setResult(Constants.CODE_INIT_VIEW_NULL);
                    break;
                default:
                    break;
            }
            if (toastMsg != null) {
                Toast.makeText(RechargeRecordActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
            }

        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);
        //userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        // 请求数据
        initData();
        // 初始化View
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
        tool_bar_center_text_view.setText(getString(R.string.recharge_record_title));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化View
     */
    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        rechargeRecordRecyclerList = (RecyclerView) findViewById(R.id.recharge_recycle_view);
        noneImage = (ImageView) findViewById(R.id.recharge_record_none_image);
        noneImage.setVisibility(View.GONE);
        initRecyclerView();
    }

    private void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rechargeRecordRecyclerList.setLayoutManager(linearLayoutManager);
        //设置增加或删除条目的动画
        rechargeRecordRecyclerList.setItemAnimator(new DefaultItemAnimator());
        initListener();
    }

    private void initListener() {
        initPullRefresh();

        initLoadMoreListener();
    }

    private void initPullRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isConnect(RechargeRecordActivity.this)) {
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
                    Utils.showToast(RechargeRecordActivity.this, R.string.netWrong);
                }
            }
        });
    }

    private void initLoadMoreListener() {

        rechargeRecordRecyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (rechargeRecordList != null && rechargeRecordList.size() > 0) {
                    //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recycleAdapter.getItemCount()) {
                        if (Utils.isConnect(RechargeRecordActivity.this)) {
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
                            Utils.showToast(RechargeRecordActivity.this, R.string.netWrong);
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

    private int updateData(int pageNumber) {
        try {
            rechargeRecordList = new ArrayList<>();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pageNum", String.valueOf(pageNumber)));
            params.add(new BasicNameValuePair("id", userId));
            // 获取响应的结果信息
            String json = getPostData(Constants.URL_FINANCE_RECHARGE_RECORD, params);
            // JSON的解析过程
            boolean result = false;
            String msg = "";
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                Log.d(TAG, "===RechargeRecordActivity #updateData()  jsonObject = " + jsonObject.toString());
                result = jsonObject.getBoolean("success");
                if (result) {
                    if("无数据".equals(jsonObject.getString("msg"))){

                        return Constants.CODE_INIT_VIEW_NULL;
                    }
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    Log.d(TAG, "===RechargeRecordActivity #updateData()   dataObject = " + dataObject.toString());
                    JSONArray taskListArray = dataObject.getJSONArray("list");
                    Log.d(TAG, "===RechargeRecordActivity #updateData()   taskListArray = " + taskListArray.toString());

                    pageSum = Integer.parseInt(dataObject.get("totalNum").toString());
                    //RechargeRecordBean rechargeRecordBean = null;
                    int listLength = taskListArray.length();
                    for (int i = 0; i < listLength; i++) {
                        JSONObject rechargeRecordData = taskListArray.getJSONObject(i);
                        Log.d(TAG, "===RechargeRecordActivity #updateData()   rechargeRecordData = " + rechargeRecordData.toString());
                        // rechargeRecordBean = JsonUtils.jsonStrToBean(rechargeRecordData.toString(), RechargeRecordBean.class);
                        RechargeRecordBean rechargeRecordBean =
                                new RechargeRecordBean(rechargeRecordData.get("price").toString(),
                                        rechargeRecordData.get("payWay").toString(),
                                        rechargeRecordData.get("invoice").toString(),
                                        rechargeRecordData.get("time1").toString(),
                                        rechargeRecordData.get("time2").toString(),
                                        rechargeRecordData.get("priceOrderId").toString());
                        Log.d(TAG, "===RechargeRecordActivity #updateData()   rechargeRecordBean = " + rechargeRecordBean.getPayWay() + "\n" + i);
 /*                       if (rechargeRecordBean.getHeadPicUrl() != null && !"".equals(rechargeRecordBean.getHeadPicUrl().toString())) {
                            //staffBean.setHeadPicBitmap(Utils.getBitmap(Constants.PICTURE_BASE_URL + staffBean.getHeadPicUrl().toString()));
                            rechargeRecordBean.setHeadPicBitmap(rechargeRecordBean.get("headPicUrl").toString());
                        }*/
                        Log.d(TAG, "===RechargeRecordActivity #updateData()   rechargeRecordBean = " + rechargeRecordBean);
                        rechargeRecordList.add(rechargeRecordBean);
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
