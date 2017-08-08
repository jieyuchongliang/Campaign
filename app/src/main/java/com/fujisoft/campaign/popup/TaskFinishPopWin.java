package com.fujisoft.campaign.popup;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.fujisoft.campaign.R;
import com.fujisoft.campaign.adapter.TaskFinishedListAdapter;
import com.fujisoft.campaign.bean.MTaskData;
import com.fujisoft.campaign.fragment.TaskFragment;
import com.fujisoft.campaign.service.TaskService;
import com.fujisoft.campaign.utils.JsonUtils;
import com.fujisoft.campaign.utils.SplashActivity;
import com.fujisoft.campaign.utils.Utils;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.fujisoft.campaign.utils.Utils.Retrofit;

/**
 * 任务大厅页面中弹出的已分享窗口.
 */
public class TaskFinishPopWin extends BasePopupWindow implements View.OnClickListener {

    private String TAG = "campaign";
    private StickyListHeadersListView stickyListHeadersListView;
    public TaskFinishedListAdapter taskFinishedListAdapter;
    private TaskService taskService;
    private TextView upArr;

    public TaskFinishPopWin(Activity context, int w, int h) {
        super(context, w, h);
    }

    @Override
    protected Animation initShowAnimation() {
        return getDefaultAlphaAnimation();
    }

    @Override
    public View getClickToDismissView() {
        return getPopupWindowView();
    }

    @Override
    public View onCreatePopupView() {
        Log.d(TAG, "=== TaskFinishPopWin#onCreatePopupView() ===");
        View v = createPopupById(R.layout.popup_task_list);

        Log.d(TAG, "=== TaskFinishPopWin#onCreatePopupView() Retrofit = " + Retrofit);
        if (null != Retrofit) {
            taskService = Retrofit.create(TaskService.class);
        } else {
            Utils.getRetrofit(getContext());
            taskService = Retrofit.create(TaskService.class);
        }
        upArr = (TextView) v.findViewById(R.id.task_btn_up_arr);
        upArr.setTypeface(SplashActivity.iconTypeFace);
        upArr.setOnClickListener(this);
        stickyListHeadersListView = (StickyListHeadersListView) v.findViewById(R.id.list);
        taskFinishedListAdapter = new TaskFinishedListAdapter(this.getContext());
        stickyListHeadersListView.setAdapter(taskFinishedListAdapter);
        return v;
    }

    @Override
    public View initAnimaView() {
        return null;
    }

    /**
     * 加载弹出的已分享任务PopWin的数据
     * @param pageNumber
     * @param userId
     * @param refreshMode
     */
    public void load(Integer pageNumber, String userId, final TaskFragment.RefreshMode refreshMode) {
        Call<MTaskData> call = taskService.taskIndex(userId, pageNumber, null);
        call.enqueue(new Callback<MTaskData>() {
            @Override
            public void onResponse(Call<MTaskData> call, Response<MTaskData> response) {
                MTaskData result = response.body();
                if (result != null) {
                    Log.d(TAG, "=== TaskFinishPopWin#load() result json = " + JsonUtils.javaBeanToJson(result));
                    MTaskData.MTasks ds = result.getData();
                    List<MTaskData.MTask> completeTask = ds.getCompleteTask();
                    if (completeTask != null) {
                        taskFinishedListAdapter.reloadData(completeTask, refreshMode);//可选的分情况
                    }
                }
            }

            @Override
            public void onFailure(Call<MTaskData> call, Throwable t) {
                Log.d(TAG, "=== TaskFinishPopWin#load() task throwable = " + t.toString());
            }
        });
    }

    /**
     * 点击弹出的已分享任务PopWin上方的箭头按钮的事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_btn_up_arr:
                if (this.getContext() instanceof ITaskFinished) {
                    ITaskFinished taskFinished = (ITaskFinished) this.getContext();
                    taskFinished.showTaskFinishedActivity();
                } else {
                    throw new RuntimeException("实例必须实现接口com.fujisoft.campaign.popup.TaskFinishPopWin.ITaskFinished");
                }
                break;
        }
    }

    /**
     * activity必须实现
     */
    public interface ITaskFinished {
        /**
         * 显示任务完成activity
         */
        void showTaskFinishedActivity();
    }
}