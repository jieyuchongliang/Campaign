package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.RechargeRecordBean;

import java.util.List;

/**
 * Created by 860116014 on 2017/1/17.
 */

public class RechargeRecordAdapter extends RecyclerView.Adapter {
    private Context context = null;
    private List<RechargeRecordBean> rechargeRecordDatas = null;
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

    public RechargeRecordAdapter(Context context, List<RechargeRecordBean> rechargeRecordDatas) {
        this.context = context;
        this.rechargeRecordDatas = rechargeRecordDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recharge_record_item, parent, false);
            return new RechargeRecordItemHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RechargeRecordItemHolder) {
            RechargeRecordItemHolder itemViewHolder = (RechargeRecordItemHolder) holder;
            RechargeRecordBean rechargeRecordBean = rechargeRecordDatas.get(position);
            int flag = Integer.parseInt(rechargeRecordBean.getPayWay());
            if (flag == 1) {
                itemViewHolder.icon.setBackgroundResource(R.drawable.pay_wx);
                itemViewHolder.way.setText("微信");
            } else if (flag == 2) {
                itemViewHolder.icon.setBackgroundResource(R.drawable.pay_zfb);
                itemViewHolder.way.setText("支付宝");
            }
            itemViewHolder.order_num.setText(rechargeRecordBean.getPriceOrderId());
            itemViewHolder.price.setText(rechargeRecordBean.getPrice());
            itemViewHolder.time_year.setText(rechargeRecordBean.getTime1());
            itemViewHolder.time_hour.setText(rechargeRecordBean.getTime2());
        } else if (holder instanceof RechargeRecordAdapter.FooterViewHolder) {
            RechargeRecordAdapter.FooterViewHolder footerViewHolder = (RechargeRecordAdapter.FooterViewHolder) holder;
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
                    footerViewHolder.mTvLoadText.setText("没有更多充值记录了");
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        if (rechargeRecordDatas != null) {

            return rechargeRecordDatas.size()+1;
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

    public class RechargeRecordItemHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView order_num;
        TextView way;
        TextView price;
        TextView time_year;
        TextView time_hour;

        public RechargeRecordItemHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            order_num = (TextView) itemView.findViewById(R.id.priceOrderId);
            way = (TextView) itemView.findViewById(R.id.way);
            price = (TextView) itemView.findViewById(R.id.price);
            time_year = (TextView) itemView.findViewById(R.id.time_year);
            time_hour = (TextView) itemView.findViewById(R.id.time_hour);
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

    public void addFooterItem(List<RechargeRecordBean> items) {
        rechargeRecordDatas.addAll(items);
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
