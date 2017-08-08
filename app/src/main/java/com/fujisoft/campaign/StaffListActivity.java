package com.fujisoft.campaign;

import android.content.Intent;
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

import com.fujisoft.campaign.adapter.StaffListAdapter;
import com.fujisoft.campaign.bean.StaffBean;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.JsonUtils;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 员工页面
 */
public class StaffListActivity extends BaseActivity {
    private String TAG = "campaign";
    private String userId = null;
    private String userType = "";                      // 从服务器端获取企业的类型值
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView staffRecyclerList = null;
    private StaffListAdapter recycleAdapter = null;
    private List<StaffBean> staffList = null;
    private ImageView noneImage;

    String openingFee = "";

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
                    toastMsg = getResources().getString(R.string.data_receive);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    pageNum = 1;
                    recycleAdapter = new StaffListAdapter(StaffListActivity.this, staffList);
                    staffRecyclerList.setAdapter(recycleAdapter);
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    toastMsg = getResources().getString(R.string.login_toast_exception);
                    setResult(Constants.CODE_EXECUTE_EXCEPTION);
                    break;
                case Constants.CODE_USER_UNACTIVE:
                    // 用户是未激活状态(即未交年费)时，进入交费页面
                    Intent intent = new Intent();
                    intent.putExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT, openingFee);
                    intent.putExtra(Constants.EXTRA_USER_ID, userId);
                    intent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                    intent.putExtra(Constants.EXTRA_AMOUNT, openingFee);
                    intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_PAY_TYPE);
                    intent.setClass(StaffListActivity.this, CompanyPayOpeningFeeActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case Constants.CODE_PULL_DOWN_REFRESH:
                    pageNum = 1;
                    recycleAdapter = new StaffListAdapter(StaffListActivity.this, staffList);
                    staffRecyclerList.setAdapter(recycleAdapter);
                    //刷新完成
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case Constants.CODE_LOADED_ALL:
                    //没有加载更多了
                    recycleAdapter.changeMoreStatus(recycleAdapter.NO_LOAD_MORE);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case Constants.CODE_TOP_PULL_LOADING:
                    recycleAdapter.changeMoreStatus(recycleAdapter.LOADING_MORE);
                    recycleAdapter.addFooterItem(staffList);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case Constants.CODE_REMIND_STAFF_ERROR:
                    toastMsg = "提醒任务失败！";
                    break;
                case Constants.CODE_REMIND_STAFF_SUCCESS:
                    toastMsg = "提醒任务成功！";
                    break;
                case Constants.CODE_REMIND_STAFF_EXCEPTION:
                    toastMsg = "发生异常，请与管理员联系!";
                    break;
                case Constants.CODE_INIT_VIEW_NULL:
                    noneImage.setVisibility(View.VISIBLE);
                    setResult(Constants.CODE_INIT_VIEW_NULL);
                    break;
                default:
                    break;
            }
            if (toastMsg != null) {
                Toast.makeText(StaffListActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
            }
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        userType = getIntent().getStringExtra(Constants.EXTRA_USER_TYPE);
        Log.d(TAG, "=== StaffListActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);
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

        ImageButton backIB = (ImageButton) findViewById(R.id.tool_bar_back_button);
        backIB.setVisibility(View.VISIBLE);
        backIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        TextView staffTxt = (TextView) findViewById(R.id.tool_bar_center_text_view);
        staffTxt.setVisibility(View.VISIBLE);
        staffTxt.setText(R.string.staff_list_title);
        TextView staffRemindTxt = (TextView) findViewById(R.id.tool_bar_right_bottom_button);
        staffRemindTxt.setVisibility(View.VISIBLE);
        staffRemindTxt.setText(R.string.staff_list_remind_button_text);
        staffRemindTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindStaffRequest();
            }
        });
    }

    /**
     * 初始化View
     */
    private void initView() {

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.staff_swipe_refresh_layout);
        staffRecyclerList = (RecyclerView) findViewById(R.id.staff_recycler_list);
        noneImage = (ImageView) findViewById(R.id.staff_none_image);
        noneImage.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        staffRecyclerList.setLayoutManager(linearLayoutManager);
        //设置增加或删除条目的动画
        staffRecyclerList.setItemAnimator(new DefaultItemAnimator());

        initPullRefresh();
        initLoadMoreListener();
    }

    private void initPullRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isConnect(StaffListActivity.this)) {
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
                    Utils.showToast(StaffListActivity.this, R.string.netWrong);
                }
            }
        });
    }

    private void initLoadMoreListener() {

        staffRecyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (staffList != null && staffList.size() > 0) {
                    //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recycleAdapter.getItemCount()) {
                        if (Utils.isConnect(StaffListActivity.this)) {
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
                            Utils.showToast(StaffListActivity.this, R.string.netWrong);
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
                        case Constants.CODE_USER_UNACTIVE:
                            mHandler.sendEmptyMessage(Constants.CODE_USER_UNACTIVE);
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
            staffList = new ArrayList<>();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pageNum", String.valueOf(pageNumber)));
            params.add(new BasicNameValuePair("id", userId));
            // 获取响应的结果信息
            String json = getPostData(Constants.URL_ENTERPRISE_ENLIST, params);
            Log.d(TAG, "=== StaffListActivity#updateData() json = " + json);
            // JSON的解析过程
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                boolean result = jsonObject.getBoolean("success");
                if (result) {
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    int accountStatus = dataObject.getInt("accountStatus"); // 0:未激活  1：已激活
                    openingFee = dataObject.getString("opening_fee").toString();
                    if (accountStatus != 1) {// 用户是未激活状态(即未交年费)时，进入交费页面
                        // 获取数据成功后的处理
                        return Constants.CODE_USER_UNACTIVE;
                    }
                    pageSum = Integer.parseInt(dataObject.get("totalNum").toString());
                    if (pageSum == 0) {
                        return Constants.CODE_INIT_VIEW_NULL;
                    }
                    JSONArray taskListArray = dataObject.getJSONArray("enList");
                    int listLength = taskListArray.length();
                    for (int i = 0; i < listLength; i++) {
                        JSONObject staffData = taskListArray.getJSONObject(i);
                        Log.d(TAG, "=== StaffListActivity#updateData() staffData = " + staffData.toString());
                        StaffBean staffBean = JsonUtils.jsonStrToBean(staffData.toString(), StaffBean.class);
                        if (staffBean.getHeadPicUrl() != null && !"".equals(staffBean.getHeadPicUrl().toString())) {
                            staffBean.setHeadPicBitmap(staffData.get("headPicUrl").toString());
                        }
                        staffList.add(staffBean);
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
            // 异常后的处理
            return Constants.CODE_EXECUTE_EXCEPTION;
        }
    }

    private int remindStaff() {
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id", userId));
            // 获取响应的结果信息
            String json = getPostData(Constants.URL_ENTERPRISE_REMIND, params);
            // JSON的解析过程
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                Log.d(TAG, "=== StaffListActivity#remindStaff()  jsonObject = " + jsonObject.toString());
                boolean result = jsonObject.getBoolean("success");
                if (result) {
                    // 成功后的处理
                    return Constants.CODE_REMIND_STAFF_SUCCESS;
                } else {
                    // 失败后的处理
                    return Constants.CODE_REMIND_STAFF_ERROR;
                }
            } else {
                // 异常后的处理
                return Constants.CODE_REMIND_STAFF_EXCEPTION;
            }

        } catch (Exception e) {
            // 异常后的处理
            return Constants.CODE_REMIND_STAFF_EXCEPTION;
        }
    }

    private void remindStaffRequest() {
        if (Utils.isConnect(this)) {
            showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = remindStaff();
                    switch (result) {
                        case Constants.CODE_REMIND_STAFF_SUCCESS:
                            mHandler.sendEmptyMessage(Constants.CODE_REMIND_STAFF_SUCCESS);
                            break;
                        case Constants.CODE_REMIND_STAFF_ERROR:
                            mHandler.sendEmptyMessage(Constants.CODE_REMIND_STAFF_ERROR);
                            break;
                        case Constants.CODE_REMIND_STAFF_EXCEPTION:
                            mHandler.sendEmptyMessage(Constants.CODE_REMIND_STAFF_EXCEPTION);
                            break;
                    }
                }
            }).start();
        } else {
            Utils.showToast(this, R.string.netWrong);
        }
    }
}