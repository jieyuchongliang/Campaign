package com.fujisoft.campaign.adapter;

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
import com.fujisoft.campaign.RankingFlowerActivity;
import com.fujisoft.campaign.RankingGoldActivity;
import com.fujisoft.campaign.bean.RankingBean;
import com.fujisoft.campaign.utils.Constants;

import java.util.List;

/**
 * 排行榜容器配置
 */
public class RankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    private RankingGoldActivity mContext;
//    private RankingFlowerActivity context;
    private List<RankingBean> rankDatas;
//    private static final int TYPE_ITEM = 2;
    private static final int TYPE_FOOTER = 3;
    private static final int first_card = 0;
    private static final int other_card = 1;
    public static final int PULLUP_LOAD_NULL = 3;
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;
    //默认状态
    private int loadMoreStatus = 3;
//    LayoutInflater mInflater;

    int flag;
    int sortFlag;

    public RankingAdapter(RankingFlowerActivity context, List<RankingBean> rankDatas, int flag, int sortFlag) {
//        this.context = context;
        this.rankDatas = rankDatas;
        this.flag = flag;
        this.sortFlag = sortFlag;
    }

    public RankingAdapter(RankingGoldActivity mContext, List<RankingBean> rankDatas, int flag, int sortFlag) {
//        this.mContext = mContext;
        this.rankDatas = rankDatas;
        this.flag = flag;
        this.sortFlag = sortFlag;
    }

    /**
     * 渲染具体的ViewHolder
     *
     * @param parent   ViewHolder的容器
     * @param viewType 一个标志，我们根据该标志可以实现渲染不同类型的ViewHolder
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        } else if (viewType == first_card) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_first_card, parent, false);

            return new RankingAdapter.firstItemHolder(itemView);
        } else if (viewType == other_card) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_card, parent, false);
            return new RankingAdapter.otherItemHolder(itemView);
        }
        return null;
    }

    /**
     * 绑定ViewHolder的数据。
     *
     * @param viewHolder
     * @param position   数据源list的下标
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RankingAdapter.firstItemHolder) {
            RankingAdapter.firstItemHolder itemViewHolder = (RankingAdapter.firstItemHolder) viewHolder;
            RankingBean rankingBean = rankDatas.get(position);
            itemViewHolder.headPicBitmap.setImageURI(Constants.PICTURE_BASE_URL + rankingBean.getHeadPicBitmap());
            itemViewHolder.no.setText(rankingBean.getRowNo());
            itemViewHolder.gold_num.setText(rankingBean.getScoreSum());
            itemViewHolder.first_name.setText(rankingBean.getNickname());
            if (flag == 1) {
                ((firstItemHolder) viewHolder).flower_gold.setBackgroundResource(R.mipmap.little_gold_icon);
            } else {
                ((firstItemHolder) viewHolder).flower_gold.setBackgroundResource(R.mipmap.little_flower_icon);
            }

        } else if (viewHolder instanceof RankingAdapter.otherItemHolder) {
            RankingAdapter.otherItemHolder itemViewHolder = (RankingAdapter.otherItemHolder) viewHolder;
            RankingBean rankingBean = rankDatas.get(position);
            itemViewHolder.headPicBitmap.setImageURI(Constants.PICTURE_BASE_URL + rankingBean.getHeadPicBitmap());
            //itemViewHolder.no.setText(rankingBean.getRowNo());
            if (sortFlag == 0) {
                itemViewHolder.no.setText(String.valueOf(position + 1));
            } else {
                itemViewHolder.no.setText(String.valueOf(position));
            }
            itemViewHolder.gold_num.setText(rankingBean.getScoreSum());
            itemViewHolder.name.setText(rankingBean.getNickname());
            if (flag == 1) {
                ((otherItemHolder) viewHolder).flower_gold.setBackgroundResource(R.mipmap.little_gold_icon);
            } else {
                ((otherItemHolder) viewHolder).flower_gold.setBackgroundResource(R.mipmap.little_flower_icon);
            }
        } else if (viewHolder instanceof RankingAdapter.FooterViewHolder) {
            RankingAdapter.FooterViewHolder footerViewHolder = (RankingAdapter.FooterViewHolder) viewHolder;
            switch (loadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
                    break;
                case PULLUP_LOAD_NULL:
                    footerViewHolder.mTvLoadText.setText("");
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    footerViewHolder.mTvLoadText.setText("正加载更多...");
                    break;
                case NO_LOAD_MORE:
                    footerViewHolder.mTvLoadText.setText("没有更多数据了");
                    footerViewHolder.mPbLoad.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (rankDatas != null) {
            return rankDatas.size() + 1;
        } else {
            return 0;
        }
    }

    /**
     * 决定元素的布局使用哪种类型
     *
     * @param position 数据源List的下标
     * @return 一个int型标志，传递给onCreateViewHolder的第二个参数
     */

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            if (position == 0 && sortFlag != 0) {
                return first_card;
            } else {
                return other_card;
            }
        }
    }

    public class firstItemHolder extends RecyclerView.ViewHolder {
        TextView no;
        TextView first_name;
        TextView gold_num;
        ImageView flower_gold;
        SimpleDraweeView headPicBitmap;

        public firstItemHolder(View itemView) {
            super(itemView);
            headPicBitmap = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            no = (TextView) itemView.findViewById(R.id.no);
            first_name = (TextView) itemView.findViewById(R.id.first_name);
            gold_num = (TextView) itemView.findViewById(R.id.gold_num);
            flower_gold = (ImageView) itemView.findViewById(R.id.flower_gold);
        }
    }

    public void addFooterItem(List<RankingBean> items) {
        rankDatas.addAll(items);
        notifyDataSetChanged();
    }

    public class otherItemHolder extends RecyclerView.ViewHolder {

        TextView no;
        TextView name;
        TextView gold_num;
        ImageView flower_gold;
        SimpleDraweeView headPicBitmap;

        public otherItemHolder(View itemView) {
            super(itemView);
            headPicBitmap = (SimpleDraweeView) itemView.findViewById(R.id.user_icon);
            no = (TextView) itemView.findViewById(R.id.no);
            name = (TextView) itemView.findViewById(R.id.name);
            gold_num = (TextView) itemView.findViewById(R.id.gold_num);
            flower_gold = (ImageView) itemView.findViewById(R.id.flower_gold);

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

    public void clear() {
        if(this.rankDatas!=null){

            this.rankDatas = null;
        }
        this.notifyDataSetChanged();
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

