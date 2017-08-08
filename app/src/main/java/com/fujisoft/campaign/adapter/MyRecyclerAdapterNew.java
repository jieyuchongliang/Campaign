package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.BaseActivity;
import com.fujisoft.campaign.GoodsDetailActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.CarouselBean;
import com.fujisoft.campaign.bean.GoodBean;
import com.fujisoft.campaign.bean.Task;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.view.CarouselView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * 首页用RecyclerViewAdapter
 */
public class MyRecyclerAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    LayoutInflater mInflater;
    List<Task> mDatas;

    List<GoodBean> mGoodDatas;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;

    private onItemClickListener mListener;
    //    private boolean isPublishedTask = false;
    private int isSearch = 0;
    private String onePicUrl;
    private int requiredNum = -1;

    private GoodsGridAdapterNew goodsGridAdapter;

    private String userScore;             // 鲜花余额
    private String userContributeScore; // 贡献元宝数

    public MyRecyclerAdapterNew(Context context, List<Task> datas, List<GoodBean> datasgood, onItemClickListener mListener, int isSearch) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        mGoodDatas = datasgood;
        this.mListener = mListener;
        this.isSearch = isSearch;
    }

    public MyRecyclerAdapterNew(Context context, List<Task> datas, List<GoodBean> datasgood, onItemClickListener mListener, int isSearch, int requiredNum) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
       mDatas = datas;

        mGoodDatas = datasgood;


        this.mListener = mListener;
        this.isSearch = isSearch;
        this.requiredNum = requiredNum;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new HeaderViewHolder(mInflater.inflate(R.layout.index_goods_header, parent, false));
        }
        if (viewType == 0) { // 任务列表的View
//            View itemView = mInflater.inflate(R.layout.index_card_view, parent, false);

            View itemView = mInflater.inflate(R.layout.fragment_task, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == 1) { //// 任务列表的footView
            View itemView = mInflater.inflate(R.layout.index_goods_foot, parent, false);
            return new GoodViewHolder(itemView);
        }
        return null;
    }


    private List<CarouselBean> listCarouselBeans = new ArrayList<>();

    private CarouselView.Adapter getCarouselAdapter() {
        CarouselView.Adapter carouselAdapter = new CarouselView.Adapter() {
            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public View getView(int position) {
                View view = mInflater.inflate(R.layout.flower_market_carousel_item, null);
                SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.image);
                imageView.setImageURI(Constants.PICTURE_BASE_URL + listCarouselBeans.get(position).getUrl());
                return view;
            }

            @Override
            public int getCount() {
                return listCarouselBeans.size();
            }
        };
        return carouselAdapter;
    }

    public void clear() {
        mDatas.clear();

        mGoodDatas.clear();
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            if(mDatas==null){
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.title.setVisibility(View.GONE);
            }
        }
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Task task;
            if (mGoodDatas != null) {
                task = mDatas.get(position - 1);
            } else {
                task = mDatas.get(position);
            }

            if (task.getRequiredFlag() == 1) {
                itemViewHolder.taskRequiredTitle.setText(R.string.string_required_task_icon_text);
            } else {
                itemViewHolder.taskRequiredTitle.setText(R.string.string_not_required_task_icon_text);
                itemViewHolder.taskRequiredTitle.setVisibility(View.GONE);
            }

            switch (BaseActivity.getUserTaskStatus(String.valueOf(task.getCompleteFlag()), String.valueOf(task.getTaskStatus()))) {
                case 0:
                    itemViewHolder.taskShare.setText(mContext.getString(R.string.string_not_share_task_text));
                    break;
                case 1:
                    itemViewHolder.taskShare.setText(mContext.getString(R.string.string_shared_task_text));
                    break;
                case 2:
                    itemViewHolder.taskShare.setText(mContext.getString(R.string.finish));
                    break;
                default:
                    break;
            }

//            if (requiredNum == -1) {
//                itemViewHolder.indexHeader.setVisibility(View.GONE);
//            } else {
//                if (requiredNum > 0 && position == 0) {
//                    itemViewHolder.indexHeader.setVisibility(View.VISIBLE);
//                    itemViewHolder.indexHeader.setText(mContext.getString(R.string.index_recycler_required_header));
//                } else if (requiredNum > 0 && position == requiredNum) {
//                    itemViewHolder.indexHeader.setVisibility(View.VISIBLE);
//                    itemViewHolder.indexHeader.setText(mContext.getString(R.string.index_recycler_required_header));
//                } else if (requiredNum <= 0 && position == 0) {
//                    itemViewHolder.indexHeader.setVisibility(View.VISIBLE);
//                    itemViewHolder.indexHeader.setText(mContext.getString(R.string.index_recycler_required_header));
//                } else {
//                    itemViewHolder.indexHeader.setVisibility(View.GONE);
//                }
//            }

            itemViewHolder.taskPic.setImageURI(Constants.PICTURE_BASE_URL + task.getTaskPic());

            itemViewHolder.taskTitle.setText(task.getTaskTitle());
            itemViewHolder.taskScore.setText(task.getScore() + "");
//            itemViewHolder.taskContent.setText(Html.fromHtml(task.getTaskContent()));

            itemViewHolder.taskContent.setText(task.getSubtitle());
            itemViewHolder.setTaskId(task.getTaskId());
            itemViewHolder.setCompleteFlag(task.getCompleteFlag());
            itemViewHolder.setTaskStatus(task.getTaskStatus());
            itemViewHolder.setTaskTitleData(task.getTaskTitle());
            itemViewHolder.setTaskContentData(task.getTaskContent());
            itemViewHolder.setTaskPicUrl(task.getTaskPic());
            itemViewHolder.setShareWay(task.getShareWays());

        } else if (holder instanceof GoodViewHolder) {

            GoodViewHolder goodViewHolder = (GoodViewHolder) holder;

            goodsGridAdapter = new GoodsGridAdapterNew(null,mContext, mGoodDatas);
            goodViewHolder.goods.setAdapter(goodsGridAdapter);
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
            goodViewHolder.goods.setLayoutManager(gridLayoutManager);


            goodsGridAdapter.setOnItemClickListener(new GoodsGridAdapterNew.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, String goodsId) {
                    Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_GOODS_ID, goodsId);
                    //  intent.putExtra(Constants.EXTRA_USER_ID, userId);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    private int mHeaderCount = 1;//头部View个数
    public static final int ITEM_TYPE_HEADER = 2;

    //判断当前item是否是HeadView
    public boolean isHeaderView(int position) {
        return mHeaderCount != 0 && position < mHeaderCount;
    }

    @Override
    public int getItemCount() {
        if(mDatas==null){
            return 0;
        }
        if (mDatas.size() > 0) {

            if (mGoodDatas == null) {
                return mDatas.size();
            }
            //RecyclerView的count设置为数据总条数+ 1（footerView）
            return mDatas.size() + 2;
//            return mDatas.size();
        } else {
            // 无内容时显示空
            return 0;
        }
    }

//    //头部 ViewHolder
//    public static class HeaderViewHolder extends ViewHolder {
//        public HeaderViewHolder(View view) {
//            super(view);
//        }
//    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderCount != 0 && position < mHeaderCount) {
            //头部View
            if (mGoodDatas == null) {
                return 0;
            }
            return ITEM_TYPE_HEADER;
        }
        if ((position + 1 == getItemCount())) {
            //最后一个item设置为footerView

            if (mGoodDatas == null) {
                return 0;
            }
            return 1;
        } else {
            return 0; // 任务列表的View
        }
    }

    public class ItemViewHolder extends ViewHolder {
        private SimpleDraweeView taskPic;
        private TextView taskTitle;
        private TextView taskContent;
        private TextView taskScore;
        private TextView taskRequiredTitle;
        //        private TextView taskShare;
        private SimpleDraweeView indexImage;
        private TextView indexHeader;
        private int taskId;
        private String taskTitleData;
        private String taskContentData;
        private String taskPicUrl;
        private int completeFlag;
        private int taskStatus;
        private String shareWay;
        Button taskShare;

        public ItemViewHolder(View itemView) {
            super(itemView);
            taskPic = (SimpleDraweeView) itemView.findViewById(R.id.img);
            taskTitle = (TextView) itemView.findViewById(R.id.title);
            taskContent = (TextView) itemView.findViewById(R.id.content);
            taskScore = (TextView) itemView.findViewById(R.id.integral);
            taskRequiredTitle = (TextView) itemView.findViewById(R.id.mustFlags);


//            taskShare = (TextView) itemView.findViewById(R.id.index_task_share);
            taskShare = (Button) itemView.findViewById(R.id.btn_share);

            indexImage = (SimpleDraweeView) itemView.findViewById(R.id.img);

            indexHeader = (TextView) itemView.findViewById(R.id.index_header);


            initListener(itemView);
        }

        private void initListener(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(taskId, completeFlag, taskStatus);
                }
            });
            taskShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onShareClick(taskId, completeFlag, taskStatus, taskTitleData, taskContentData, taskPicUrl, shareWay);
                }
            });
            /*indexImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onOnPicClick(Uri.parse(onePicUrl));
                }
            });*/
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public void setCompleteFlag(int completeFlag) {
            this.completeFlag = completeFlag;
        }

        public void setTaskStatus(int taskStatus) {
            this.taskStatus = taskStatus;
        }

        public void setTaskTitleData(String taskTitleData) {
            this.taskTitleData = taskTitleData;
        }

        public void setTaskContentData(String taskContentData) {
            this.taskContentData = taskContentData;
        }

        public void setTaskPicUrl(String taskPicUrl) {
            this.taskPicUrl = taskPicUrl;
        }

        public void setShareWay(String shareWay) {
            this.shareWay = shareWay;
        }
    }


    public class HeaderViewHolder extends ViewHolder {
        private TextView title;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.flower_market_goods_title);
        }

    }

    public class GoodViewHolder extends ViewHolder {
        private RecyclerView goods;

        public GoodViewHolder(View itemView) {
            super(itemView);
            goods = (RecyclerView) itemView.findViewById(R.id.flower_market_grid_view);
        }

        public RecyclerView getGood() {
            return goods;
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
        void onItemClick(int taskId, int completeFlag, int taskStatus);

        void onShareClick(int taskId, int completeFlag, int taskStatus, String taskTitle, String taskContent, String taskPicUrl, String shareWay);

        void onOnPicClick(Uri uri);
    }
}
