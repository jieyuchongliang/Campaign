package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.OrderDetailsActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.OrderBean;
import com.fujisoft.campaign.utils.Constants;

import java.util.List;

/**
 * 订单列表Adapter
 */

public class OrderListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = "campaign";
    Context mContext;
    LayoutInflater mInflater;
    List<OrderBean> mDatas;
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
    //    private boolean isPublishedTask = false;
    private String onePicUrl;
    private OnItemClickListener mOnItemClickListener;


    public OrderListRecyclerViewAdapter(Context context, List<OrderBean> datas, boolean isPublishedTask) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = mInflater.inflate(R.layout.order_list_item_card_view, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = mInflater.inflate(R.layout.load_more_footview_layout, parent, false);
            return new FooterViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {

            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            OrderBean order = mDatas.get(position);

            itemViewHolder.order_list_goods_image.setImageURI(Constants.PICTURE_BASE_URL + order.getGoodsPicture());

            itemViewHolder.order_list_goods_title.setText(order.getGoodsName());

            itemViewHolder.order_list_scores.setText(order.getScorePrice());

            itemViewHolder.order_list_amount.setText(order.getGoodsQuantity());

            itemViewHolder.order_list_order_number.setText(order.getOrderCode());

            itemViewHolder.order_list_detail_receiver.setText(order.getName().toString());

            itemViewHolder.order_list_detail_contact_number.setText(order.getAddress().toString());

            itemViewHolder.order_list_detail_ship_address.setText(order.getAddress().toString());

            itemViewHolder.order_list_detail_ship_status_button.setText(order.getOrderStatus().toString());

            itemViewHolder.setOrderId(Integer.parseInt(order.getOrderId()));

            itemViewHolder.read_detail_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "=== OrderListRecyclerViewAdapter#orderDetailsIntent.putExtra() itemViewHolder.orderId= " + itemViewHolder.orderId);
                    Intent orderDetailsIntent = new Intent(mContext, OrderDetailsActivity.class);
                    orderDetailsIntent.putExtra(Constants.EXTRA_ORDER_ID, itemViewHolder.orderId);
//                        orderDetailsIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                    mContext.startActivity(orderDetailsIntent);
                }
            });
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
                    footerViewHolder.mTvLoadText.setText("没有更多订单了");
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
        public Button read_detail_button;         // 订单列表中的查看详情按钮
        public ImageButton close_detail_button;   // 订单列表-详细页面中的关闭按钮

        public SimpleDraweeView order_list_goods_image;  // 订单列表中的图片
        public TextView order_list_goods_title;   // 订单列表中的标题
        public TextView order_list_scores;        // 订单列表中的积分数
        public TextView order_list_amount;        // 订单列表中的订单个数
        public TextView order_list_order_number; // 订单列表中的订单编号

        public RelativeLayout order_list_detail_layout;
        public TextView order_list_detail_receiver;           // 订单列表-详情中的收货人
        public TextView order_list_detail_contact_number;   // 订单列表-详情中的联系方式
        public TextView order_list_detail_ship_address;      // 订单列表-详情中的收货地址
        public Button order_list_detail_ship_status_button; // 订单列表-详情中的收货状态(未查收、已查收)

        private int orderId;


        public ItemViewHolder(View view) {
            super(view);
//            super(view);
            order_list_goods_image = (SimpleDraweeView) view.findViewById(R.id.order_list_goods_image);
            order_list_goods_title = (TextView) view.findViewById(R.id.order_list_goods_title);
            order_list_scores = (TextView) view.findViewById(R.id.order_list_scores);
            order_list_amount = (TextView) view.findViewById(R.id.order_list_amount);
            order_list_order_number = (TextView) view.findViewById(R.id.order_list_order_number);
            read_detail_button = (Button) view.findViewById(R.id.read_detail_button);
            close_detail_button = (ImageButton) view.findViewById(R.id.close_detail_button);

            // 订单列表-详情部分
            order_list_detail_layout = (RelativeLayout) view.findViewById(R.id.order_list_detail_layout);
            order_list_detail_receiver = (TextView) view.findViewById(R.id.order_list_detail_receiver);
            order_list_detail_contact_number = (TextView) view.findViewById(R.id.order_list_detail_contact_number);
            order_list_detail_ship_address = (TextView) view.findViewById(R.id.order_list_detail_ship_address);
            order_list_detail_ship_status_button = (Button) view.findViewById(R.id.order_list_detail_ship_status_button);
        }

        private void initListener(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(orderId);
                }
            });
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
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

    public void addFooterItem(List<OrderBean> items) {
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
        void onItemClick(int orderId);
        void onOnPicClick(Uri uri);
    }
}
