package com.fujisoft.campaign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fujisoft.campaign.adapter.MineSharedTaskRecyclerAdapter;
import com.fujisoft.campaign.bean.Task;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 已分享任务页面
 */
public class SharedTasksActivity extends BaseActivity {
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
    private MineSharedTaskRecyclerAdapter recycleAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private ImageView noneImage;
    private String userId = null;

    private List<Task> taskLists;
    private MineSharedTaskRecyclerAdapter.onItemClickListener mListener = new MineSharedTaskRecyclerAdapter.onItemClickListener() {
        @Override
        public void onItemClick(int taskId) {
            Intent taskDetailsIntent = new Intent(SharedTasksActivity.this, TaskDetailsActivity.class);
            taskDetailsIntent.putExtra(Constants.EXTRA_TASK_ID, String.valueOf(taskId));
            taskDetailsIntent.putExtra(Constants.EXTRA_USER_ID, userId);
            taskDetailsIntent.putExtra(Constants.EXTRA_PREVIOUS_SCREEN, "SharedTasksActivity");
            startActivityForResult(taskDetailsIntent, Constants.CODE_REQUEST_TASK_DETAILS);
        }

        @Override
        public void onOnPicClick(Uri uri) {

        }
    };
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    showErrorDialog(Constants.CODE_EXECUTE_FAILURE);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    pageNum = 1;
                    recycleAdapter = new MineSharedTaskRecyclerAdapter(SharedTasksActivity.this, taskLists, mListener, true);
                    mRecyclerView.setAdapter(recycleAdapter);
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    Utils.showToast(SharedTasksActivity.this, R.string.data_receive);
                    setResult(Constants.CODE_EXECUTE_EXCEPTION);
                    break;
                case Constants.CODE_PULL_DOWN_REFRESH:
                    pageNum = 1;
                    recycleAdapter = new MineSharedTaskRecyclerAdapter(SharedTasksActivity.this, taskLists, mListener, true);
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
                    recycleAdapter.addFooterItem(taskLists);
                    recycleAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_published_tasks);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

        initData();
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
        shoppingCarTxt.setText(R.string.mine_shared_task_text);
    }


    private void initListener() {
        initPullRefresh();

        initLoadMoreListener();
    }

    private void initPullRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isConnect(SharedTasksActivity.this)) {
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
                    Utils.showToast(SharedTasksActivity.this, R.string.netWrong);
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

                if (taskLists != null && taskLists.size() > 0) {
                    //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recycleAdapter.getItemCount()) {
                        if (Utils.isConnect(SharedTasksActivity.this)) {
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
                            Utils.showToast(SharedTasksActivity.this, R.string.netWrong);
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.published_tasks_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.published_tasks_recycler_view);

        noneImage = (ImageView) findViewById(R.id.order_list_none_image);
        noneImage.setVisibility(View.GONE);

        initRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case Constants.CODE_EXECUTE_SUCCESS:
                initIndexView(userId);
                break;
            case Constants.CODE_TASK_GET_SUCCESS:
                initData();
            default:
                break;
        }
    }

    /**
     * 登录后初始化index页面
     *
     * @param userId:用户ID
     */
    private void initIndexView(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_SHARED_TASK_LIST, params);
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                    }
                    if (!result) {
                        mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                    } else {
                        //登录成功后的处理
                        mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_SUCCESS);
                    }

                } catch (Exception e) {
                    //登录异常后的处理
                    mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                }
            }
        }).start();

    }

    /**
     * 显示Error Dialog
     *
     * @param msgId
     */
    private void showErrorDialog(int msgId) {
        String dialogMessage = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(SharedTasksActivity.this);
        builder.setTitle(getString(R.string.login_dialog_hint))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        switch (msgId) {
            case Constants.CODE_EXECUTE_FAILURE:
                dialogMessage = getString(R.string.login_dialog_error);
                break;
        }
        builder.setMessage(dialogMessage);
        builder.create().show();
    }

    private int updateData(int pageNumber) {
        try {
            taskLists = new ArrayList<>();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pageNum", String.valueOf(pageNumber)));
            params.add(new BasicNameValuePair("id", userId));
            // 获取响应的结果信息
            String json = getPostData(Constants.URL_SHARED_TASK_LIST, params);
            // JSON的解析过程
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                Log.d(TAG, "===SharedTasksActivity#upData() jsonObject = " + jsonObject);
                boolean result = jsonObject.getBoolean("success");
                if (result) {
                    if ("无数据".equals(jsonObject.getString("msg"))) {
                        return Constants.CODE_INIT_VIEW_NULL;
                    }
                    JSONObject dataObject = jsonObject.getJSONObject("data");

                    JSONArray taskListArray = dataObject.getJSONArray("completeTask");
                    pageSum = Integer.parseInt(dataObject.get("totalNum").toString());
                    Task mTask;
                    int listLength = taskListArray.length();

                    for (int i = 0; i < listLength; i++) {

                        JSONObject taskData = taskListArray.getJSONObject(i);
                        mTask = new Task();
                        mTask.setTaskId(Integer.parseInt(taskData.get("id").toString()));
                        mTask.setTaskTitle(taskData.get("name").toString());
                        mTask.setTaskContent(taskData.get("content").toString());

                        mTask.setSubtitle(taskData.get("subtitle").toString());

                        mTask.setRequiredFlag(Integer.parseInt(taskData.get("requiredFlags").toString()));
                        //任务可得积分
                        int integral = Integer.parseInt(taskData.get("staff_flower").toString());
                        mTask.setIntegral(integral);

                        //图片加载太慢，暂时保留
                        if (!TextUtils.isEmpty(taskData.get("picUrl").toString())) {
                            mTask.setTaskPic(taskData.get("picUrl").toString());
                        } else {
                            mTask.setTaskPic(null);
                        }
                        taskLists.add(mTask);
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