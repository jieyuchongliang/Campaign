package com.fujisoft.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fujisoft.campaign.adapter.TaskFinishedListAdapter;
import com.fujisoft.campaign.adapter.TaskListRVAdapter;
import com.fujisoft.campaign.bean.MTaskData;
import com.fujisoft.campaign.fragment.TaskFragment;
import com.fujisoft.campaign.service.TaskService;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.fujisoft.campaign.utils.Utils.Retrofit;

/**
 * 已完成任务单独画面
 */
public class TaskFinishedListActivity//
        extends AppCompatActivity//
        implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, View.OnClickListener, AdapterView.OnItemClickListener, TaskListRVAdapter.ITaskListExt {
    private String TAG = "campaign";
    private Toolbar toolbar;
    private StickyListHeadersListView stickyListHeadersListView;
    private TaskFinishedListAdapter taskFinishedListAdapter;
    private TaskService taskService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String keyword;
    private int pageNumber = 1;
    private String userId;
    private int lastItemIndex;
    private EditText editTxtSearch;
    private View taskFinishedListLayout;
    private View tvBtnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_finished_list);

        Log.d(TAG, "=== TaskFinishedListActivity#onCreate() Retrofit = " + Retrofit);
        if (null != Retrofit) {
            taskService = Retrofit.create(TaskService.class);
        } else {
            Utils.getRetrofit(this);
            taskService = Retrofit.create(TaskService.class);
        }
        taskFinishedListLayout = findViewById(R.id.task_finished_list_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskFinishedListActivity.this.finish();
            }
        });*/
        ImageButton tool_bar_back_button = (ImageButton) findViewById(R.id.tool_bar_back_button);
        tool_bar_back_button.setVisibility(View.VISIBLE);
        tool_bar_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        stickyListHeadersListView = (StickyListHeadersListView) swipeRefreshLayout.findViewById(R.id.list);
        stickyListHeadersListView.setOnScrollListener(this);
        taskFinishedListAdapter = new TaskFinishedListAdapter(this);
        stickyListHeadersListView.setAdapter(taskFinishedListAdapter);
        stickyListHeadersListView.setOnItemClickListener(this);

        editTxtSearch = (EditText) toolbar.findViewById(R.id.edit_txt_search);
        editTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pageNumber = 1;
                String searchTxt = s.toString().trim();
                if (searchTxt.length() > 0) {
                    //搜索框有值
                    keyword = searchTxt;
                } else {
                    //搜索框没值
                    keyword = null;
                }
                taskFinishedListAdapter.reloadData(null, TaskFragment.RefreshMode.Clear);
                load(pageNumber, userId, TaskFragment.RefreshMode.Reload, keyword);
            }
        });

        Bundle params = getIntent().getExtras();
        userId = params.getString(Constants.EXTRA_USER_ID);
        this.load(1, userId, TaskFragment.RefreshMode.Reload, null);

        tvBtnSearch = findViewById(R.id.tv_btn_search);
        tvBtnSearch.setOnClickListener(this);

        //默认焦点不在搜索框上
        swipeRefreshLayout.setFocusable(true);
        swipeRefreshLayout.setFocusableInTouchMode(true);
        swipeRefreshLayout.requestFocus();
    }

    /**
     * 加载数据
     */
    public void load(Integer pageNumber, String userId, final TaskFragment.RefreshMode refreshMode, String keyword) {
        Call<MTaskData> call = taskService.taskFinished(userId, pageNumber, keyword);
        call.enqueue(new Callback<MTaskData>() {
            @Override
            public void onResponse(Call<MTaskData> call, Response<MTaskData> response) {
                MTaskData result = response.body();
                Log.d(TAG, "=== TaskFinishedListActivity#load() result = " + result);
                if (result != null) {
                    MTaskData.MTasks ds = result.getData();
                    if(ds!=null){
                        List<MTaskData.MTask> completeTask = ds.getCompleteTask();
                        if (completeTask != null) {
                            taskFinishedListAdapter.reloadData(completeTask, refreshMode);
                        }
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<MTaskData> call, Throwable t) {
                Log.d(TAG, "=== TaskFinishedListActivity#load() task throwable = " + t.toString());
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNumber = 1;
        this.load(pageNumber, userId, TaskFragment.RefreshMode.Reload, keyword);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d(TAG, "=== TaskFinishedListActivity#onScrollStateChanged()  scrollState = " + scrollState);
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && lastItemIndex == taskFinishedListAdapter.getCount() - 1) {
            pageNumber = pageNumber + 1;
            this.load(pageNumber, userId, TaskFragment.RefreshMode.Append, this.keyword);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.d(TAG, "=== TaskFinishedListActivity#onScrollStateChanged() firstVisibleItem : " + firstVisibleItem + " , visibleItemCount : " + visibleItemCount + " , totalItemCount : " + totalItemCount);
        lastItemIndex = firstVisibleItem + visibleItemCount - 1;
    }

    @Override
    public void onClick(View v) {
        //暂时只有搜索按钮
        String keyword = editTxtSearch.getText().toString().trim();
        if (keyword.length() > 0) {
            //搜索框有值
            this.keyword = keyword;
        } else {
            //搜索框没值
            this.keyword = null;
        }
        pageNumber = 1;
        taskFinishedListAdapter.reloadData(null, TaskFragment.RefreshMode.Clear);
        load(pageNumber, userId, TaskFragment.RefreshMode.Reload, this.keyword);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskFinishedListAdapter.ViewHolder holder = (TaskFinishedListAdapter.ViewHolder) view.getTag();
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_TASK_ID, holder.taskId);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void toTaskDetails(String taskId) {
        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        intent.putExtra(Constants.EXTRA_TASK_ID, taskId);
        startActivity(intent);
    }
}