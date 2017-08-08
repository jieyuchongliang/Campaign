package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.Task;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.view.DownLoadButton;

import java.util.List;

/**
 * 已发任务
 */

public class MinePublishedTaskRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    LayoutInflater mInflater;
    List<Task> mDatas;
    private static final int TYPE_ITEM = 0;

    private static final int TYPE_FOOTER = 1;

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;

    public static final int PULLUP_LOAD_NULL = 3;
    //默认状态
    private int mLoadMoreStatus = 3;

    private onItemClickListener mListener;
    private boolean isPublishedTask = false;
    private String onePicUrl;


    public MinePublishedTaskRecyclerAdapter(Context context, List<Task> datas, onItemClickListener mListener, boolean isPublishedTask) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        this.mListener = mListener;
        this.isPublishedTask = isPublishedTask;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = mInflater.inflate(R.layout.mine_published_task_card_view, parent, false);
            return new ItemViewHolder(itemView);
        }
         else if (viewType == TYPE_FOOTER) {
            View itemView = mInflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Task task = mDatas.get(position);
            itemViewHolder.taskPic.setImageURI(Constants.PICTURE_BASE_URL + task.getTaskPic());
            itemViewHolder.taskTitle.setText(task.getTaskTitle());
            //itemViewHolder.taskScore.setText(task.getIntegral() + "");
            if(task.getRequiredFlags().equals("3")){
                itemViewHolder.flower_icon.setBackgroundResource(R.drawable.strawberry_2);
                itemViewHolder.taskScore.setText("1");
            }else{
                itemViewHolder.flower_icon.setBackgroundResource(R.mipmap.little_flower_icon);
                itemViewHolder.taskScore.setText(String.valueOf(task.getIntegral()));
            }
//            itemViewHolder.taskContent.setText(Html.fromHtml(task.getTaskContent()));
            if ("1".equals(task.getRequiredFlags())) {
                itemViewHolder.mustFlags.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.mustFlags.setVisibility(View.GONE);
            }
            itemViewHolder.taskContent.setText(task.getSubtitle());
            itemViewHolder.setTaskId(task.getTaskId());
            itemViewHolder.downLoadButton.setDownLoadProgress((int) (Float.parseFloat(task.getExecution())),task.getExecution()+"%");

            itemViewHolder.downLoadButton_text.setText(task.getExecution()+"%");
            
//            itemViewHolder.downLoadButton.setText(task.getExecution()+"%");
        } else if (holder instanceof FooterViewHolder) {

            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;

            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_NULL:
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.mTvLoadText.setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    footerViewHolder.mTvLoadText.setText("没有更多任务了");
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;

            }
        }

    }

    @Override
    public int getItemCount() {
        if (mDatas.size() > 0) {
            //RecyclerView的count设置为数据总条数+ 1（footerView）
            return mDatas.size() + 1;
        } else {
            // 无内容时显示空
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView taskPic;
        private TextView taskTitle;
        private TextView taskContent;
        private TextView taskScore;
        private TextView  downLoadButton_text;
        private ImageView indexImage;
        private int taskId;
        private TextView mustFlags;
        private DownLoadButton downLoadButton;
        private ImageView flower_icon;

        public ItemViewHolder(View itemView) {
            super(itemView);
            flower_icon = (ImageView)itemView.findViewById(R.id.flower_icon);
            taskPic = (SimpleDraweeView) itemView.findViewById(R.id.img);
            taskTitle = (TextView) itemView.findViewById(R.id.title);
            taskContent = (TextView) itemView.findViewById(R.id.content);
            taskScore = (TextView) itemView.findViewById(R.id.integral);
            mustFlags= (TextView) itemView.findViewById(R.id.mustFlags);

            downLoadButton_text = (TextView) itemView.findViewById(R.id.downLoadButton_text);
            indexImage = (ImageView) itemView.findViewById(R.id.img);

            downLoadButton = (DownLoadButton) itemView.findViewById(R.id.downLoadButton);

            initListener(itemView);

            if (isPublishedTask) {

            }
        }

        private void initListener(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(taskId);
                }
            });
            indexImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onOnPicClick(Uri.parse(onePicUrl));
                }
            });
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mPbLoad;
        TextView mTvLoadText;
        LinearLayout mLoadLayout;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mPbLoad = (ProgressBar) itemView.findViewById(R.id.pbLoad);
            mTvLoadText = (TextView) itemView.findViewById(R.id.tvLoadText);
            mLoadLayout = (LinearLayout) itemView.findViewById(R.id.loadLayout);
        }
    }

    public void addFooterItem(List<Task> items) {
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
            mLoadMoreStatus = status;
            notifyDataSetChanged();
    }

    public interface onItemClickListener {
        void onItemClick(int taskId);

        void onOnPicClick(Uri uri);
    }
}
