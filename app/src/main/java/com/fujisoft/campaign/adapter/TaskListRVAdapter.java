package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.BaseActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.MTaskData;
import com.fujisoft.campaign.fragment.TaskFragment;
import com.fujisoft.campaign.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * 任务大厅：必选任务&可选任务 列表适配器
 */
public class TaskListRVAdapter extends BaseAdapter implements StickyListHeadersAdapter, View.OnClickListener {

    private Context mContext;
    private List<MTaskData.MTask> datas = new ArrayList<>();
    private LayoutInflater inflater;
    private String userId = null;
    private onItemClickListener mListener;

    public TaskListRVAdapter(Context context, onItemClickListener mListener) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mListener = mListener;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.fragment_task_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        MTaskData.MTask task = datas.get(position);

        if ("1".equals(task.getRequiredFlags())) {
            holder.text.setText(R.string.task_necessary);
        } else {
            holder.text.setText(R.string.task_select);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        MTaskData.MTask task = datas.get(position);
        if ("1".equals(task.getRequiredFlags())) {
            return 10;
        } else {
            return 20;
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_task, parent, false);
            convertView.setOnClickListener(this);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtContent = (TextView) convertView.findViewById(R.id.content);
            holder.flower_icon = (ImageView)convertView.findViewById(R.id.flower_icon);
            holder.txtIntegral = (TextView) convertView.findViewById(R.id.integral);
            holder.mustFlags = (TextView) convertView.findViewById(R.id.mustFlags);
            holder.img = (SimpleDraweeView) convertView.findViewById(R.id.img);
            Button btnShare = (Button) convertView.findViewById(R.id.btn_share);
            convertView.setTag(holder);
//            TextView hua = (TextView) convertView.findViewById(R.id.hua);
//            hua.setTypeface(SplashActivity.iconTypeFace);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MTaskData.MTask task = datas.get(position);
        if (task == null) {
            return convertView;
        }
        holder.txtTitle.setText(task.getName());
        holder.txtContent.setText(task.getSubtitle());
        if(task.getRequiredFlags().equals("3")){
            holder.flower_icon.setBackgroundResource(R.drawable.strawberry_2);
            holder.txtIntegral.setText("1");
        }else{
            holder.flower_icon.setBackgroundResource(R.mipmap.little_flower_icon);
            holder.txtIntegral.setText(String.valueOf(task.getStaff_flower()));
        }

        holder.button = (Button) convertView.findViewById(R.id.btn_share);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onShareClick(Integer.parseInt(task.getId()), Integer.parseInt(task.getCompleteFlag()),
                        Integer.parseInt(task.getTaskStatus()), task.getName(), task.getContent(), task.getPicUrl(), task.getShareWay(), task.getRequiredFlags(), task.getMaxShareTime(), task.getMinShareTime(), task.getTime());
            }
        });
        switch (BaseActivity.getUserTaskStatus(task.getCompleteFlag(), task.getTaskStatus(), task.getRequiredFlags(), task.getMaxShareTime(), task.getMinShareTime(), task.getTime())) {

            case 0:
                holder.button.setText(R.string.string_not_share_task_text);
                break;
            case 1:
                holder.button.setText(R.string.string_shared_task_text);
                break;
            case 2:
                holder.button.setText(R.string.finish);
                break;
            case 3:
                holder.button.setText("继续分享");
                break;
            default:
                break;
        }
/*        if (task.getRequiredFlags().equals("3")) {
            if (Integer.valueOf(task.getTime()) < Integer.valueOf(task.getMinShareTime())) {
                holder.button.setText("一键分享");
            }else if( Integer.valueOf(task.getMinShareTime())<=Integer.valueOf(task.getTime())&&Integer.valueOf(task.getTime())<=Integer.valueOf(task.getMaxShareTime()) ){
                holder.button.setText("继续分享");
            }else if( Integer.valueOf(task.getTime())>Integer.valueOf(task.getMaxShareTime()) ){
                holder.button.setText("已完成");
            }
        }*/


        holder.img.setImageURI(Constants.PICTURE_BASE_URL + task.getPicUrl());
        if ("1".equals(task.getRequiredFlags())) {
            holder.mustFlags.setVisibility(View.VISIBLE);
        } else {
            holder.mustFlags.setVisibility(View.GONE);
        }
        holder.taskId = task.getId();
        return convertView;
    }

    public void reloadRequiredData(List<MTaskData.MTask> tasks, TaskFragment.RefreshMode refreshMode) {
        if (refreshMode == TaskFragment.RefreshMode.Reload) {
            this.datas.clear();
            this.datas = tasks;
        } else if (refreshMode == TaskFragment.RefreshMode.Append) {
            if (tasks != null) {
                this.datas.addAll(tasks);
            }
        }
        this.notifyDataSetChanged();
    }

    public void clear() {

        this.datas.clear();

        this.notifyDataSetChanged();
    }

    public void reloadData(List<MTaskData.MTask> tasks, TaskFragment.RefreshMode refreshMode) {
        if (refreshMode == TaskFragment.RefreshMode.Reload) {
            if (tasks != null) {
                this.datas.addAll(tasks);
            } else {
                this.datas.clear();
                this.datas = tasks;
            }
        } else if (refreshMode == TaskFragment.RefreshMode.Append) {
            if (tasks != null) {
                this.datas.addAll(tasks);
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        if (mContext instanceof ITaskListExt) {
            ITaskListExt taskListExt = (ITaskListExt) mContext;
            taskListExt.toTaskDetails(holder.taskId);
        } else {
            throw new RuntimeException("接口com.fujisoft.campaign.adapter.TaskListRVAdapter.ITaskListExt未实现");
        }
        Log.d("项item===", "单击");
    }

    private static class HeaderViewHolder {
        TextView text;
    }

    public static class ViewHolder {
        public String taskId;
        TextView txtTitle;
        TextView txtContent;
        TextView txtIntegral;
        ImageView flower_icon;
        TextView mustFlags;
        SimpleDraweeView img;
        Button button;
    }

    public interface ITaskListExt {
        /**
         * 跳转到任务详情
         *
         * @param taskId
         */
        void toTaskDetails(String taskId);
    }

    public interface onItemClickListener {
        void onShareClick(int taskId, int completeFlag, int taskStatus, String taskTitle, String taskContent, String taskPicUrl, String shareWay, String requiredFlags, String maxShareTime, String minShareTime, String time);
    }
}
