package com.fujisoft.campaign.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fujisoft.campaign.BaseActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.TabMainActivity;
import com.fujisoft.campaign.adapter.TaskListRVAdapter;
import com.fujisoft.campaign.bean.MTaskData;
import com.fujisoft.campaign.popup.TaskFinishPopWin;
import com.fujisoft.campaign.service.TaskService;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.JsonUtils;
import com.fujisoft.campaign.utils.Utils;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.fujisoft.campaign.utils.Utils.Retrofit;

/**
 * 任务大厅Fragment
 */
public class TaskFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    private String TAG = "campaign";
    private String userId;
    private TaskListRVAdapter adapter;
    private TaskService taskService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StickyListHeadersListView stickList;

    private TabMainActivity mTabActivity;
    private String keyword;
    //    private ImageView mNonePic;
    private LinearLayout mNoneLayou;

    public TaskFragment() {
    }

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabActivity = (TabMainActivity) getActivity();

        IntentFilter filter = new IntentFilter(BaseActivity.SHARED_SUCCESS_ACTION);
        mTabActivity.registerReceiver(broadcastReceiver, filter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if ("SUCCESS".equals(intent.getExtras().getString("data"))) {
                Log.d(TAG, "=== TaskFragment#onReceive() 分享成功 ===");
                load(1, keyword, RefreshMode.Reload);
                notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTabActivity.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "=== TaskFragment#onResume() ===");
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG, "===TaskFragment#onResume() 从SP中取到的userId = " + userId);
        Log.d(TAG, "=== TaskFragment#onCreate() Retrofit = " + Retrofit);
        if (null != Retrofit) {
            taskService = Retrofit.create(TaskService.class);
        } else {
            Utils.getRetrofit(getContext());
            taskService = Retrofit.create(TaskService.class);
        }
//        adapter.clear();

        Log.d(TAG, "TaskFragment#Load --mTabActivity.mCurrentTab" + mTabActivity.mCurrentTab);
        if (mTabActivity.mCurrentTab == 1) {

            mTabActivity.showProgressDialog();
        }

        this.load(1, keyword, RefreshMode.Reload);
        notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "=== TaskFragment#onCreateView() Start====");
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

//        mNonePic=(ImageView)view.findViewById(R.id.order_list_none_image);
//        mNoneLayou=(LinearLayout)view.findViewById(R.id.none_layout);
        if (view instanceof SwipeRefreshLayout) {
            swipeRefreshLayout = (SwipeRefreshLayout) view;
            swipeRefreshLayout.setOnRefreshListener(this);
            stickList = (StickyListHeadersListView) swipeRefreshLayout.findViewById(R.id.list);
            stickList.setOnScrollListener(this);
            adapter = new TaskListRVAdapter(this.getContext(), mRecyclerAdapterListener);
            stickList.setAdapter(adapter);
        }
        Log.d(TAG, "=== TaskFragment#onCreateView() End====");
        return view;
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 加载任务大厅的数据
     *
     * @param pageNumber
     * @param keyword
     * @param refreshMode
     */
    public void load(Integer pageNumber, String keyword, final RefreshMode refreshMode) {

//        if( mTabActivity.mCurrentTab==1) {
//
//            mTabActivity.showProgressDialog();
//        }

        Log.d(TAG, "===TaskFragment#load() ===");
        Log.d(TAG, "==查询参数== page = " + pageNumber);
        Call<MTaskData> call = taskService.taskIndex(userId, pageNumber, keyword);
        call.enqueue(new Callback<MTaskData>() {
            @Override
            public void onResponse(Call<MTaskData> call, Response<MTaskData> response) {
                MTaskData result = response.body();
                if (result != null) {
                    Log.d(TAG, "=== TaskFragment#load() javaBeanToJson = " + JsonUtils.javaBeanToJson(result));
                    MTaskData.MTasks ds = result.getData();
                    if (ds.getTotalNum() == 0) {
                        if (adapter != null) adapter.clear();
                    }
                    if (refreshMode == RefreshMode.Reload) {
                        Log.d(TAG, "===TaskFragment#load() ==RefreshMode.Reload=");
                        //必做任务刷新
                        List<MTaskData.MTask> rt = ds.getRequiredTask();
                        //为了修改 向下滑动，数据重复加载问题
                        if (null == userId || "".equals(userId) || rt == null) {
                            if (adapter != null) adapter.clear();
                        }
                        if (rt != null) {
                            if (adapter != null)
                                adapter.reloadRequiredData(rt, RefreshMode.Reload);//必做的重载

//                            mNoneLayou.setVisibility(View.GONE);
                        }
                        //可选任务累加
                        List<MTaskData.MTask> ot = ds.getOptionalTask();
                        if (ot != null) {
                            if (adapter != null) adapter.reloadData(ot, RefreshMode.Append);//可选的分情况

//                            mNoneLayou.setVisibility(View.GONE);
                        }
                    } else {
                        //只累加可选
                        List<MTaskData.MTask> ot = ds.getOptionalTask();
                        if (ot != null) {
                            if (adapter != null) adapter.reloadData(ot, refreshMode);//可选的分情况

//                            mNoneLayou.setVisibility(View.GONE);
                        }
                    }
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                }
                if (mTabActivity.mCurrentTab == 1) {
                    mTabActivity.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(Call<MTaskData> call, Throwable t) {
            }
        });
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "=== TaskFragment#onRefresh() ===");
        pageNumber = 1;
        this.load(1, keyword, RefreshMode.Reload);
    }

    private int lastItemIndex;
    private int pageNumber = 1;


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && lastItemIndex == adapter.getCount() - 1) {
            Log.d(TAG, "加载更多啊===onScrollStateChanged");
            pageNumber = pageNumber + 1;
            this.load(pageNumber, keyword, RefreshMode.Append);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemIndex = firstVisibleItem + visibleItemCount - 1;
    }

    public void select(String keyword) {
        this.keyword=keyword;
        this.load(1, keyword, RefreshMode.Reload);
    }

    /**
     * 刷新方式
     */
    public enum RefreshMode {
        Append,
        Reload,
        Clear
    }

    /**
     * 弹出已分享任务的PopWin
     */
    public void showPopWin() {
        int popWinHeight = TabMainActivity.getScreenHeight(this.getActivity()) / 2;
        TaskFinishPopWin taskFinishPopWin = new TaskFinishPopWin(this.getActivity(), ViewGroup.LayoutParams.MATCH_PARENT, popWinHeight);
        taskFinishPopWin.setOffsetY(stickList.getHeight() - popWinHeight);
        // 设置弹出PopWin后，背景阴影的效果
        WindowManager.LayoutParams lp = mTabActivity.getWindow().getAttributes();
        //lp.alpha = 0.5f; // 0.0-1.0
        lp.alpha = 0.99f;
        mTabActivity.getWindow().setAttributes(lp);
        // 添加弹出的popWin关闭的事件，主要是为了将背景透明度改回来
        taskFinishPopWin.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        taskFinishPopWin.showPopupWindow(stickList);
        taskFinishPopWin.load(1, userId, RefreshMode.Reload);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mTabActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        mTabActivity.getWindow().setAttributes(lp);
    }

    private TaskListRVAdapter.onItemClickListener mRecyclerAdapterListener = new TaskListRVAdapter.onItemClickListener() {

        @Override
        public void onShareClick(int taskId, int completeFlag, int taskStatus, String taskTitle, String taskContent, String taskPicUrl, String shareWay, String requiredFlags, String maxShareTime, String minShareTime, String time) {

            if (null != userId && !"".equals(userId)) {

                switch (mTabActivity.getUserTaskStatus(String.valueOf(completeFlag), String.valueOf(taskStatus), String.valueOf(requiredFlags), maxShareTime, minShareTime, time)) {
                    //String completeFlag, String taskStatus, String requiredFlags, String maxShareTime, String minShareTime, String time
                    case 0:
                        mTabActivity.showShare(taskTitle, taskContent, taskPicUrl, shareWay, userId, String.valueOf(taskId));
                        break;
                    case 1:
                        mTabActivity.showMsg(getString(R.string.task_share_end), Toast.LENGTH_SHORT);
                        break;
                    case 2:
                        mTabActivity.showMsg(getString(R.string.task_end), Toast.LENGTH_SHORT);
                        break;
                    case 3:
                        mTabActivity.showShare(taskTitle, taskContent, taskPicUrl, shareWay, userId, String.valueOf(taskId));
                        break;
                    default:
                        break;
                }
            } else {
                mTabActivity.toLogin();
            }
        }
    };
}