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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.adapter.RankingAdapter;
import com.fujisoft.campaign.bean.RankingBean;
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
 * 排行榜页面
 */
public class RankingFlowerActivity extends BaseActivity {

    private String TAG = "campaign";
    private int flag = 2;
    private ImageView noneImage;
    private int spinner_flag = 1;
    private RecyclerView rankRecyclerList = null;
    private LinearLayoutManager linearLayoutManager;
    private RankingAdapter recycleAdapter = null;
    private List<RankingBean> rankList = null;
    private int sortFlag = 1;
    private int pageNum = 1;
    private int pageSum = 0;
    private int listLength;
    private String userId = null;
    private SwipeRefreshLayout swipeRefreshLayout;

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
                    //toastMsg = "数据请求成功！";
                    pageNum = 1;
                    recycleAdapter = new RankingAdapter(RankingFlowerActivity.this, rankList, flag, sortFlag);
                    rankRecyclerList.setAdapter(recycleAdapter);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case Constants.CODE_PULL_DOWN_REFRESH:
                    //toastMsg = "刷新成功！";
                    pageNum = 1;
                    recycleAdapter = new RankingAdapter(RankingFlowerActivity.this, rankList, flag, sortFlag);
                    rankRecyclerList.setAdapter(recycleAdapter);
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
                    recycleAdapter.addFooterItem(rankList);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    toastMsg = getResources().getString(R.string.login_toast_exception);
                    setResult(Constants.CODE_EXECUTE_EXCEPTION);
                    break;
                case Constants.CODE_INIT_VIEW_NULL:
                    recycleAdapter.changeMoreStatus(recycleAdapter.PULLUP_LOAD_NULL);
                    recycleAdapter.clear();
                    swipeRefreshLayout.setRefreshing(false);
                    noneImage.setVisibility(View.VISIBLE);
                    setResult(Constants.CODE_INIT_VIEW_NULL);
                    break;
                default:
                    break;
            }
            if (toastMsg != null) {
                Toast.makeText(RankingFlowerActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
            }
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
//        userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

        Spinner spinner = (Spinner) findViewById(R.id.ranking_spinner);
        final TextView spinner_name = (TextView) findViewById(R.id.spinner_name);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                String[] languages = getResources().getStringArray(R.array.ranking);
                // Toast.makeText(RankingFlowerActivity.this, "你点击的是:" + languages[pos], Toast.LENGTH_SHORT).show();
                if (languages[pos].equals("所有排行")) {
                    spinner_name.setText("鲜花总排行");
                    spinner_flag = 1;
                    // Toast.makeText(RankingFlowerActivity.this, "spinner_flag:" + spinner_flag, Toast.LENGTH_SHORT).show();
                    if (rankList != null) {
                        recycleAdapter.clear();
                    }
                    if (rankRecyclerList != null) {
                        rankRecyclerList.clearOnScrollListeners();
                    }
                    pageNum = 1;
                    // 请求数据
                    initData();
                    // 初始化View
                    initView();
                } else if (languages[pos].equals("今日排行")) {
                    spinner_name.setText("鲜花排行");
                    spinner_flag = 2;
                    if (rankList != null) {
                        recycleAdapter.clear();
                    }
                    if (rankRecyclerList != null) {
                        rankRecyclerList.clearOnScrollListeners();
                    }
                    pageNum = 1;
                    // 请求数据
                    initData();
                    // 初始化View
                    initView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

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
        tool_bar_center_text_view.setText(R.string.ranking);
        TextView tool_bar_right_bottom_button = (TextView) findViewById(R.id.tool_bar_right_bottom_button);
        tool_bar_right_bottom_button.setVisibility(View.VISIBLE);
        tool_bar_right_bottom_button.setText(R.string.ranking_gold);
        tool_bar_right_bottom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RankingFlowerIntent = new Intent();
                RankingFlowerIntent.setClass(RankingFlowerActivity.this, RankingGoldActivity.class);
                startActivity(RankingFlowerIntent);
                recycleAdapter.clear();
                rankList.clear();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化View
     */
    private void initView() {

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        rankRecyclerList = (RecyclerView) findViewById(R.id.ranking_list);
        noneImage = (ImageView) findViewById(R.id.ranking_none_image);
        noneImage.setVisibility(View.GONE);
        initRecyclerView();
    }

    private void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rankRecyclerList.setLayoutManager(linearLayoutManager);
        //设置增加或删除条目的动画
        rankRecyclerList.setItemAnimator(new DefaultItemAnimator());
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
                if (Utils.isConnect(RankingFlowerActivity.this)) {
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
                    Utils.showToast(RankingFlowerActivity.this, R.string.netWrong);
                }
            }
        });
    }

    private void initLoadMoreListener() {

        rankRecyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (rankList != null && rankList.size() > 0) {
                    //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recycleAdapter.getItemCount()) {
                        if (Utils.isConnect(RankingFlowerActivity.this)) {
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
                            Utils.showToast(RankingFlowerActivity.this, R.string.netWrong);
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

    /**
     * 获取排行榜的数据
     *
     * @param pageNumber
     * @return
     */
    private int updateData(int pageNumber) {
        String json;
        String msg;
        JSONArray taskListArr;
        JSONArray taskListArray;
        RankingBean rankingBean;
        try {
            rankList = new ArrayList<>();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pageNum", String.valueOf(pageNumber)));
            params.add(new BasicNameValuePair("id", userId));

            // JSON的解析过程
            // 获取响应的结果信息
            if (spinner_flag == 1) {
                json = getPostData(Constants.URL_RANKING_ALLFLOWER, params);
            } else {
                json = getPostData(Constants.URL_RANKING_FLOWER, params);
            }
            Log.d(TAG, "===RankingFlowerActivity #updateData() json = " + json);

            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                boolean result = jsonObject.getBoolean("success");
                msg = jsonObject.getString("msg");
                Log.d(TAG, "===RankingFlowerActivity #updateData() msg = " + msg);
                if (result) {
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    Log.d(TAG, "===RankingFlowerActivity #updateData() dataObject = " + dataObject.toString());
                    rankingBean = null;
                    Log.d("liuyq", "pageNum: " + pageNum);
                    if (pageNumber == 1) {
                        taskListArray = dataObject.getJSONArray("sort");
                        Log.d(TAG, "===RankingFlowerActivity #updateData() taskListArray = " + taskListArray.toString());
                        pageSum = Integer.parseInt(dataObject.get("totalNum").toString());
                        if (pageSum == 0) {
                            return Constants.CODE_INIT_VIEW_NULL;
                        }
                        listLength = taskListArray.length();
                        if (listLength == 0) {
                            sortFlag = 0;
                        } else {
                            sortFlag = 1;
                        }

                        for (int i = 0; i < listLength; i++) {
                            JSONObject rankData = taskListArray.getJSONObject(i);
                            Log.d(TAG, "===RankingFlowerActivity #updateData() rankData = " + rankData.toString());
                            rankingBean = JsonUtils.jsonStrToBean(rankData.toString(), RankingBean.class);
                            if (rankingBean.getHeadPicUrl() != null && !"".equals(rankingBean.getHeadPicUrl().toString())) {
//                                Bitmap logoBitmap = Utils.getBitmap(Constants.PICTURE_BASE_URL + rankData.get("headPicUrl").toString());
                                rankingBean.setHeadPicBitmap(rankData.get("headPicUrl").toString());
                            }
                            rankList.add(rankingBean);
                        }
                    }
                    if (spinner_flag == 1) {
                        taskListArr = dataObject.getJSONArray("flowerAllRankList");
                    } else {
                        taskListArr = dataObject.getJSONArray("flowerRankList");
                    }

                    Log.d(TAG, "===RankingFlowerActivity #updateData() taskListArr = " + taskListArr.toString());

                    int listLen = taskListArr.length();
                    for (int i = 0; i < listLen; i++) {
                        JSONObject rankData = taskListArr.getJSONObject(i);
                        Log.d(TAG, "===RankingFlowerActivity #updateData()  rankData = " + rankData.toString());
                        rankingBean = JsonUtils.jsonStrToBean(rankData.toString(), RankingBean.class);
                        if (rankingBean.getHeadPicUrl() != null && !"".equals(rankingBean.getHeadPicUrl().toString())) {
/*                            Bitmap logoBitmap = Utils.getBitmap(Constants.PICTURE_BASE_URL + rankData.get("headPicUrl").toString());
                            rankingBean.setHeadPicBitmap(logoBitmap);*/
                            rankingBean.setHeadPicBitmap(rankData.get("headPicUrl").toString());
                        }
                        rankList.add(rankingBean);
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
}