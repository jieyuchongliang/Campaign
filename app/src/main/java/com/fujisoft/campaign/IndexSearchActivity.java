package com.fujisoft.campaign;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.adapter.MyRecyclerAdapterNew;
import com.fujisoft.campaign.bean.Task;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;
import com.fujisoft.campaign.view.SearchEditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * index首页的搜索页面
 */
public class IndexSearchActivity extends BaseActivity {
    private String TAG = "campaign";
    private String userId;
    private List<Task> taskLists;

    private RecyclerView mRecyclerView;
    private String keyWord;

    private MyRecyclerAdapterNew.onItemClickListener mRecyclerAdapterListener = new MyRecyclerAdapterNew.onItemClickListener() {
        @Override
        public void onItemClick(int taskId, int completeFlag, int taskStatus) {
            Intent taskDetailsIntent = new Intent(IndexSearchActivity.this, TaskDetailsActivity.class);
            taskDetailsIntent.putExtra(Constants.EXTRA_TASK_ID, String.valueOf(taskId));
            taskDetailsIntent.putExtra(Constants.EXTRA_USER_ID, userId);
            startActivityForResult(taskDetailsIntent, Constants.CODE_REQUEST_TASK_DETAILS);
        }

        @Override
        public void onShareClick(int taskId, int completeFlag, int taskStatus, String taskTitle, String taskContent, String taskPicUrl, String shareWay) {

            switch (getUserTaskStatus(String.valueOf(completeFlag), String.valueOf(taskStatus))) {
                case 0:
                    showShare(taskTitle, taskContent, taskPicUrl, shareWay, userId, String.valueOf(taskId));
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
                    Utils.showToast(IndexSearchActivity.this, R.string.loading_index_view_error);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    MyRecyclerAdapterNew recycleAdapter = new MyRecyclerAdapterNew(IndexSearchActivity.this, getTaskLists(), null, mRecyclerAdapterListener, 1);
                    recycleAdapter.changeMoreStatus(recycleAdapter.NO_LOAD_MORE);
                    mRecyclerView.setAdapter(recycleAdapter);
                    recycleAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_search);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        Log.d(TAG, "=== IndexSearchActivity#onResume() userId = " + userId);
        initViews();
        initData();
    }

    private void initViews() {
        // 任务List的刷新View
        mRecyclerView = (RecyclerView) findViewById(R.id.index_search_recycler);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置增加或删除条目的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initData() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = searchData(keyWord);
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
                }
            }
        }).start();
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        View toolbarView = getLayoutInflater().inflate(R.layout.toolbar_button, toolbar);
        LinearLayout toolbarIndexLayout = (LinearLayout) toolbarView.findViewById(R.id.tool_bar_index_search_layout);
        toolbarIndexLayout.setVisibility(View.VISIBLE);
        final SearchEditText searchEditText = (SearchEditText) toolbarIndexLayout.findViewById(R.id.index_search_searchView);
        searchEditText.requestFocus();
        TextView searchButton = (TextView) toolbarIndexLayout.findViewById(R.id.tool_bar_index_search_button);
        ImageButton backButton = (ImageButton) toolbarIndexLayout.findViewById(R.id.index_search_tool_bar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*隐藏软键盘*/
                InputMethodManager imm = (InputMethodManager) searchEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), 0);
                }
                onBackPressed();
            }
        });
        searchButton.setVisibility(View.VISIBLE);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Utils.hideKeyBoard(IndexSearchActivity.this);

                keyWord = searchEditText.getText().toString();
                initData();
            }
        });
    }

    private int searchData(String keyword) {
        try {
            List<Task> taskLists = new ArrayList<>();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id", userId));
            if (!TextUtils.isEmpty(keyWord)) {
                params.add(new BasicNameValuePair("keyword", keyword));
            }
            // 获取响应的结果信息
            String json = getPostData(Constants.URL_INDEX_SEARCH, params);
            Log.d(TAG, "=== IndexSearchActivity# searchData() json = " + json);
            // JSON的解析过程
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                boolean result = jsonObject.getBoolean("success");
                if (result) {
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    JSONArray taskListArray = dataObject.getJSONArray("taskList");

                    //获取该用户下所有的任务
                    Task mTask;
                    for (int i = 0; i < taskListArray.length(); i++) {
                        JSONObject taskData = taskListArray.getJSONObject(i);
                        mTask = new Task();
                        mTask.setTaskId(Integer.parseInt(taskData.get("id").toString()));
                        mTask.setTaskTitle(taskData.get("name").toString());
                        mTask.setTaskContent(taskData.get("content").toString());
                        mTask.setScore(Integer.parseInt(taskData.get("score").toString()));
                        mTask.setRequiredFlag(Integer.parseInt(taskData.get("requiredFlags").toString()));
                        mTask.setShareWays(taskData.get("shareWay").toString());

                        mTask.setCompleteFlag(Integer.parseInt(taskData.get("completeFlag").toString()));
                        mTask.setTaskStatus(Integer.parseInt(taskData.get("taskStatus").toString()));

                        mTask.setSubtitle(taskData.get("subtitle").toString());

                        mTask.setShareWays(taskData.get("shareWay").toString());
                        if (!TextUtils.isEmpty(taskData.get("picUrl").toString())) {
                            Log.d(TAG, "=== IndexSearchActivity#searchData() picUrl = " + taskData.get("picUrl").toString());
                            mTask.setTaskPic(taskData.get("picUrl").toString());
                        } else {
                            mTask.setTaskPic(null);
                        }
                        taskLists.add(mTask);
                    }
                    setTaskLists(taskLists);
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

    private void setTaskLists(List<Task> taskLists) {
        this.taskLists = taskLists;
    }

    private List<Task> getTaskLists() {
        return taskLists;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=== IndexSearchActivity#onDestroy() ===");
        if (taskLists != null) taskLists.clear();
        if (mRecyclerView != null) mRecyclerView.removeAllViews();
    }
}
