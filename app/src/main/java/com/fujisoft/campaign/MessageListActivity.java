package com.fujisoft.campaign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.fujisoft.campaign.adapter.MessageListAdapter;
import com.fujisoft.campaign.bean.MessageListBean;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.JsonUtils;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.fujisoft.campaign.utils.Constants.CODE_EXECUTE_EXCEPTION;
import static com.fujisoft.campaign.utils.Constants.CODE_EXECUTE_FAILURE;
import static com.fujisoft.campaign.utils.Constants.CODE_EXECUTE_SUCCESS;
import static com.fujisoft.campaign.utils.Constants.CODE_MESSAGE_SUCCESS_CHANGE;

/**
 * 消息列表页面
 */
public class MessageListActivity extends BaseActivity {

    private String TAG = "campaign";
    private String userId;
    private List<MessageListBean> messageLists;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView MessageListRecyclerList;
    private LinearLayoutManager linearLayoutManager;
    private MessageListAdapter recycleAdapter;
    private ImageView noneImage;
    private int messFlag = 1;
    private int mesFlag = 1;
    private int pageNum = 1;
    private int pageSum = 0;
    int listLen;
    private MessageListAdapter.onItemClickListener mListener = new MessageListAdapter.onItemClickListener() {

        @Override
        public void onItemClick(int taskId, int completeFlag, int taskStatus, String shareWay) {
            Intent taskDetailsIntent = new Intent(MessageListActivity.this, TaskDetailsActivity.class);
            taskDetailsIntent.putExtra(Constants.EXTRA_TASK_ID, String.valueOf(taskId));
            taskDetailsIntent.putExtra(Constants.EXTRA_USER_ID, userId);
            taskDetailsIntent.putExtra("CompleteFlag", String.valueOf(completeFlag));
            taskDetailsIntent.putExtra("taskStatus", String.valueOf(taskStatus));
            taskDetailsIntent.putExtra("shareWay", shareWay);
            startActivityForResult(taskDetailsIntent, Constants.CODE_REQUEST_TASK_DETAILS);

        }

        @Override
        public void onShareClick(int id, int completeFlag, int taskStatus, String taskTitle, String taskContent, String taskPicUrl, String shareWay) {

            switch (getUserTaskStatus(String.valueOf(completeFlag), String.valueOf(taskStatus))) {
                case 0:
                    showShare(taskTitle, taskContent, taskPicUrl, shareWay, userId, String.valueOf(id));
                    break;
                case 1:
                    showMsg(getString(R.string.task_share_end), Toast.LENGTH_SHORT);
                    break;
                case 2:
                    showMsg(getString(R.string.task_end), Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }

    };

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
                case CODE_EXECUTE_SUCCESS:
                    //toastMsg = "数据请求成功！";
                    pageNum = 1;
                    recycleAdapter = new MessageListAdapter(MessageListActivity.this, getMessageLists(), mListener, messFlag, listLen, mesFlag);
                    //  recycleAdapter.changeMoreStatus(recycleAdapter.NO_LOAD_MORE);
                    MessageListRecyclerList.setAdapter(recycleAdapter);
                    //  recycleAdapter.notifyDataSetChanged();
                    break;
                case CODE_EXECUTE_EXCEPTION:
                    Utils.showToast(MessageListActivity.this, R.string.login_toast_exception);
                    setResult(CODE_EXECUTE_EXCEPTION);
                    break;

                case Constants.CODE_PULL_DOWN_REFRESH:
                    //toastMsg = "刷新成功！";
                    pageNum = 1;
                    recycleAdapter = new MessageListAdapter(MessageListActivity.this, getMessageLists(), mListener, messFlag, listLen, mesFlag);
                    MessageListRecyclerList.setAdapter(recycleAdapter);
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
                    // toastMsg = "上拉加载成功！";
                    recycleAdapter.changeMoreStatus(recycleAdapter.LOADING_MORE);
                    recycleAdapter.addFooterItem(messageLists);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case Constants.CODE_INIT_VIEW_NULL:
                    noneImage.setVisibility(View.VISIBLE);
                    setResult(Constants.CODE_INIT_VIEW_NULL);
                    break;
                case CODE_MESSAGE_SUCCESS_CHANGE:
                    recycleAdapter.clear();
                    // 请求数据
                    initData();
                    // 初始化View
                    //initView();
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
            if (toastMsg != null) {
                Toast.makeText(MessageListActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        Log.d(TAG, "===MessageListActivity#onCreate() 广播注册");
        IntentFilter filter = new IntentFilter(BaseActivity.SHARED_SUCCESS_ACTION);
        registerReceiver(broadcastReceiver, filter);

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "=== MessageListActivity#onReceive() 分享成功 ===");
            if ("SUCCESS".equals(intent.getExtras().getString("data"))) {
                // 请求数据
                initData();
                // 初始化View
                initView();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
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
        tool_bar_center_text_view.setText("消息列表");
    }

    /**
     * 初始化View
     */
    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        MessageListRecyclerList = (RecyclerView) findViewById(R.id.message_list_view);
        noneImage = (ImageView) findViewById(R.id.order_list_none_image);
        noneImage.setVisibility(View.GONE);
        initRecyclerView();
    }

    private void initRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        MessageListRecyclerList.setLayoutManager(linearLayoutManager);
        //设置增加或删除条目的动画
        MessageListRecyclerList.setItemAnimator(new DefaultItemAnimator());
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
                if (Utils.isConnect(MessageListActivity.this)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int result = updateData(1);
                            switch (result) {
                                case CODE_EXECUTE_SUCCESS:
                                    mHandler.sendEmptyMessage(Constants.CODE_PULL_DOWN_REFRESH);
                                    break;
                                case CODE_EXECUTE_FAILURE:
                                    mHandler.sendEmptyMessage(CODE_EXECUTE_FAILURE);
                                    break;
                                case CODE_EXECUTE_EXCEPTION:
                                    mHandler.sendEmptyMessage(CODE_EXECUTE_EXCEPTION);
                                    break;
                            }
                        }
                    }).start();
                } else {
                    Utils.showToast(MessageListActivity.this, R.string.netWrong);
                }
            }
        });
    }

    private void initLoadMoreListener() {

        MessageListRecyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (messageLists != null && messageLists.size() > 0) {
                    //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recycleAdapter.getItemCount()) {
                        if (Utils.isConnect(MessageListActivity.this)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (pageNum < pageSum) {
                                        pageNum = pageNum + 1;
                                        int result = updateData(pageNum);
                                        switch (result) {
                                            case CODE_EXECUTE_SUCCESS:
                                                mHandler.sendEmptyMessage(Constants.CODE_TOP_PULL_LOADING);
                                                break;
                                            case CODE_EXECUTE_FAILURE:
                                                mHandler.sendEmptyMessage(CODE_EXECUTE_FAILURE);
                                                break;
                                            case CODE_EXECUTE_EXCEPTION:
                                                mHandler.sendEmptyMessage(CODE_EXECUTE_EXCEPTION);
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
                            Utils.showToast(MessageListActivity.this, R.string.netWrong);
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
                        case CODE_EXECUTE_SUCCESS:
                            mHandler.sendEmptyMessage(CODE_EXECUTE_SUCCESS);
                            break;
                        case CODE_EXECUTE_FAILURE:
                            mHandler.sendEmptyMessage(CODE_EXECUTE_FAILURE);
                            break;
                        case CODE_EXECUTE_EXCEPTION:
                            mHandler.sendEmptyMessage(CODE_EXECUTE_EXCEPTION);
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

    private int updateData(final int pageNumber) {
        String msg = null;
        Message message = new Message();
        try {
            MessageListBean messageListBean;
            messageLists = new ArrayList<>();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pageNum", String.valueOf(pageNumber)));
            params.add(new BasicNameValuePair("id", userId));
            // 获取响应的结果信息
            String json = getPostData(Constants.URL_MESSAGE_LIST, params);
            Log.d(TAG, "====MessageListActivity#updateData() json = " + json);
            // JSON的解析过程
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                boolean result = jsonObject.getBoolean("success");
                if (result) {
                    if ("无数据".equals(jsonObject.getString("msg"))) {
                        return Constants.CODE_INIT_VIEW_NULL;
                    }
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    pageSum = Integer.parseInt(dataObject.get("totalNum").toString());
/*                    if (pageSum == 0) {
                        return Constants.CODE_INIT_VIEW_NULL;
                    }*/
                    // RechargeRecordBean rechargeRecordBean = null;
                    JSONArray taskListArr = dataObject.getJSONArray("invitationList");
                    listLen = taskListArr.length();
                    if (listLen == 0) {
                        messFlag = 0;
                    } else {
                        messFlag = 1;
                    }

                    for (int i = 0; i < listLen; i++) {
                        JSONObject messageListData = taskListArr.getJSONObject(i);
                        messageListBean = JsonUtils.jsonStrToBean(messageListData.toString(), MessageListBean.class);
                        messageLists.add(messageListBean);
                    }

                    JSONArray taskListArray = dataObject.getJSONArray("messageList");
                    int listLength = taskListArray.length();
                    if (listLength == 0) {
                        mesFlag = 0;
                    } else {
                        mesFlag = 1;
                    }
                    for (int i = 0; i < listLength; i++) {
                        JSONObject messageListData = taskListArray.getJSONObject(i);
                        messageListBean = JsonUtils.jsonStrToBean(messageListData.toString(), MessageListBean.class);
                        messageListBean.setSubtitle(messageListData.get("subtitle").toString());
                        if (messageListBean.getPicUrl() != null && !"".equals(messageListBean.getPicUrl().toString())) {
                            messageListBean.setPicBitmap(messageListData.get("picUrl").toString());
                        }
                        messageLists.add(messageListBean);
                    }

                    setMessageLists(messageLists);
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

    public void checkMess(final String state, final String enterpriseId, final String userId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = null;
                Message message = new Message();
                MessageListBean messageListBean = null;
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", String.valueOf(userId)));
                    params.add(new BasicNameValuePair("enterpriseId", String.valueOf(enterpriseId)));
                    params.add(new BasicNameValuePair("state", String.valueOf(state)));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_INVITE, params);
                    Log.d(TAG, "===MessageListActivity#checkMess() json = " + json);
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        msg = jsonObject.get("msg").toString();
                    }
                    if (!result) {
                        //修改失败后的处理
                        message.obj = msg;
                        message.what = CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //修改成功后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_MESSAGE_SUCCESS_CHANGE;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //修改异常后的处理
                    message.obj = msg;
                    message.what = CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }


    private void setMessageLists(List<MessageListBean> messageLists) {
        this.messageLists = messageLists;
    }

    private List<MessageListBean> getMessageLists() {
        return messageLists;
    }
}

