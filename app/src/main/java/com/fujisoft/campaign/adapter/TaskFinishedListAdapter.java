package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.MTaskData;
import com.fujisoft.campaign.fragment.TaskFragment;
import com.fujisoft.campaign.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * 已完成任务列表 适配器
 */
public class TaskFinishedListAdapter extends BaseAdapter implements StickyListHeadersAdapter, View.OnClickListener {

    private final Context mContext;
    private List<MTaskData.MTask> datas = new ArrayList<>();
    private LayoutInflater inflater;

    public TaskFinishedListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.fragment_task_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.content);
            holder.text.setTextColor(Color.parseColor("#FC8149"));
            holder.text.setTextSize(2, 16);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        MTaskData.MTask task = datas.get(position);
        holder.text.setText("已分享任务");
        return convertView;
    }

    /**
     * 只有一个头部
     *
     * @param position
     * @return
     */
    @Override
    public long getHeaderId(int position) {
        return 0;
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
        MTaskData.MTask task = datas.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_task, parent, false);
            convertView.setOnClickListener(this);
            holder.layout_task = (LinearLayout) convertView.findViewById(R.id.layout_task);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtContent = (TextView) convertView.findViewById(R.id.content);
            holder.flower_icon = (ImageView)convertView.findViewById(R.id.flower_icon);
            holder.txtIntegral = (TextView)convertView.findViewById(R.id.integral);
            if(task.getRequiredFlags().equals("3")){
                holder.flower_icon.setBackgroundResource(R.drawable.strawberry_2);
                holder.txtIntegral.setText("1");
            }else{
                holder.flower_icon.setBackgroundResource(R.mipmap.little_flower_icon);
                holder.txtIntegral.setText(String.valueOf(task.getStaff_flower()));
            }
            //holder.txtIntegral = (TextView) convertView.findViewById(R.id.integral);
            holder.mustFlags = (TextView) convertView.findViewById(R.id.mustFlags);
            holder.img = (SimpleDraweeView) convertView.findViewById(R.id.img);
            holder.button = (Button) convertView.findViewById(R.id.btn_share);
            convertView.setTag(holder);

//            TextView hua = (TextView) convertView.findViewById(R.id.hua);
//            hua.setTypeface(SplashActivity.iconTypeFace);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(task.getName());
//        holder.txtContent.setText(task.getContent());

        holder.txtContent.setText(task.getSubtitle());

        holder.txtIntegral.setText(String.valueOf(task.getStaff_flower()));
        holder.button.setText(R.string.string_shared_task_text);
        holder.img.setImageURI(Constants.PICTURE_BASE_URL + task.getPicUrl());
        if ("1".equals(task.getRequiredFlags())) {
            holder.mustFlags.setVisibility(View.VISIBLE);
        } else {
            holder.mustFlags.setVisibility(View.GONE);
        }
        holder.taskId = task.getId();
        if (null != task.getTime() && !"".equals(task.getTime()) && null != task.getMaxShareTime() && !"".equals(task.getMaxShareTime())) {
            if (task.getRequiredFlags().equals("3") && (Integer.valueOf(task.getTime()) < Integer.valueOf(task.getMaxShareTime()))) {
                //datas.remove(position);
                holder.layout_task.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public void reloadData(List<MTaskData.MTask> tasks, TaskFragment.RefreshMode refreshMode) {
        if (refreshMode == TaskFragment.RefreshMode.Reload) {
            //重新加载
            if (tasks == null) {
                //新的没有数据
            } else {
                this.datas.clear();
                this.datas = tasks;
            }
        } else if (refreshMode == TaskFragment.RefreshMode.Append) {
            //累加数据
            if (tasks != null) {
                this.datas.addAll(tasks);
            }
        } else if (refreshMode == TaskFragment.RefreshMode.Clear) {
            this.datas.clear();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        if (mContext instanceof TaskListRVAdapter.ITaskListExt) {
            TaskListRVAdapter.ITaskListExt taskListExt = (TaskListRVAdapter.ITaskListExt) mContext;
            taskListExt.toTaskDetails(holder.taskId);
        } else {
            throw new RuntimeException("接口com.fujisoft.campaign.adapter.TaskListRVAdapter.ITaskListExt未实现");
        }
    }

    private static class HeaderViewHolder {
        TextView text;
    }

    public static class ViewHolder {
        public String taskId;
        LinearLayout layout_task;
        TextView txtTitle;
        TextView txtContent;
        TextView txtIntegral;
        TextView mustFlags;
        SimpleDraweeView img;
        Button button;
        ImageView flower_icon;
    }
}
