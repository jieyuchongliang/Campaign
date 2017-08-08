package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.StaffBean;
import com.fujisoft.campaign.utils.Constants;

import java.util.List;


/**
 * 员工列表的容器配置
 */
public class StaffListAdapter extends RecyclerView.Adapter {
    private Context context = null;
    private List<StaffBean> staffDatas = null;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;
    //默认状态
    private int loadMoreStatus = 3;
    public static final int PULLUP_LOAD_NULL = 3;

    public StaffListAdapter(Context context, List<StaffBean> staffDatas) {
        this.context = context;
        this.staffDatas = staffDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_list_item, parent, false);
            return new StaffItemHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StaffItemHolder) {
            StaffItemHolder itemViewHolder = (StaffItemHolder) holder;
            StaffBean staffBean = staffDatas.get(position);
            itemViewHolder.headPicBitmap.setImageURI(Constants.PICTURE_BASE_URL + staffBean.getHeadPicBitmap());
            itemViewHolder.name.setText(staffBean.getNickname() + "");
            itemViewHolder.lastLoginTime.setText(staffBean.getLastLoginTime() + "");
            itemViewHolder.fTaskCnt.setText(staffBean.getFTaskCnt() + "");
            itemViewHolder.hTCnt.setText("/" + staffBean.getHTCnt() + "");
            itemViewHolder.aTCnt.setText(staffBean.getATCnt() + "个");
            itemViewHolder.shareTime.setText(staffBean.getShareTime());
            itemViewHolder.maxShareTime.setText(staffBean.getMaxShareTime());

        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (loadMoreStatus) {
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
                    footerViewHolder.mTvLoadText.setText("没有更多员工了");
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (staffDatas != null) {
            return staffDatas.size() + 1;
        } else {
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


    public class StaffItemHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView headPicBitmap;
        private TextView name;
        private TextView lastLoginTime;
        private TextView fTaskCnt;
        private TextView hTCnt;
        private TextView aTCnt;
        private TextView shareTime;
        private TextView maxShareTime;

        public StaffItemHolder(View itemView) {
            super(itemView);
            headPicBitmap = (SimpleDraweeView) itemView.findViewById(R.id.staff_list_item_img);
            name = (TextView) itemView.findViewById(R.id.staff_item_user_name_text);
            lastLoginTime = (TextView) itemView.findViewById(R.id.staff_item_last_login_time);
            fTaskCnt = (TextView) itemView.findViewById(R.id.staff_list_item_today_task_finished_sum_text);
            hTCnt = (TextView) itemView.findViewById(R.id.staff_list_item_today_task_sum_text);
            aTCnt = (TextView) itemView.findViewById(R.id.staff_list_item_task_sum_num);
            shareTime = (TextView)itemView.findViewById(R.id.shareTime);
            maxShareTime = (TextView)itemView.findViewById(R.id.maxShareTime);
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

    public void addFooterItem(List<StaffBean> items) {
        staffDatas.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        loadMoreStatus = status;
        notifyDataSetChanged();
    }
}
