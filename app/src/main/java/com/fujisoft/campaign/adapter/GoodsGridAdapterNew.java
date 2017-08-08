package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.GoodBean;
import com.fujisoft.campaign.utils.Constants;

import java.util.List;

/**
 * 鲜花商城商品列表的Adapter
 */
public class GoodsGridAdapterNew extends RecyclerView.Adapter implements View.OnClickListener {

    Context context;
    List<GoodBean> mData;

    private final View header;

    public GoodsGridAdapterNew(View header, Context context, List<GoodBean> mData) {
        this.context = context;
        this.mData = mData;

        this.header = header;
        mInflater = LayoutInflater.from(context);
    }

    public void reloadData(List<GoodBean> goodsData) {
        this.mData.addAll(goodsData);
        this.notifyDataSetChanged();
    }

    private int mHeaderCount = 1;//头部View个数
    public static final int ITEM_TYPE_HEADER = 2;

    LayoutInflater mInflater;
    private static final int ITEM_VIEW_TYPE_HEADER = 0;

    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == ITEM_VIEW_TYPE_HEADER && header != null) {
            return new TextViewHolder(header);
        }
        View view = View.inflate(viewGroup.getContext(), R.layout.flower_market_goods_item, null);
        BodyViewHolder holder = new BodyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (isHeader(position) && header != null) {
            return;
        }

        //其他条目中的逻辑在此
        final BodyViewHolder bodyViewHolder = (BodyViewHolder) viewHolder;
        GoodBean goods;
        if (header != null) {
            goods = mData.get(position - 1);

        } else {
            goods = mData.get(position);

        }
        bodyViewHolder.getGoodImg().setImageURI(Constants.PICTURE_BASE_URL + goods.getGoodsLogo());
        bodyViewHolder.getGoodTitleTex().setText(goods.getGoodsName());
        // 我的鲜花数
        String myScoreStr = "";
        int myScore = Integer.valueOf(goods.getScorePrice());
        if (myScore >= 10000) {
            float myScoreNum = ((float) Math.round(myScore / 100) / 100);
            myScoreStr = String.valueOf(myScoreNum) + "万";
        } else {
            myScoreStr = String.valueOf(myScore);
        }
        bodyViewHolder.getGoodPriceTex().setText(myScoreStr);
        /*        bodyViewHolder.getGoodPriceTex().setText(goods.getScorePrice());*/

        bodyViewHolder.getGoodsQuantity().setText("剩余：" + goods.getGoodsQuantity());
        viewHolder.itemView.setTag(goods.getGoodsId());

    }
    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }
//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if(manager instanceof GridLayoutManager) {
//            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
//            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    return getItemViewType(position) == ITEM_VIEW_TYPE_HEADER
//                            ? gridManager.getSpanCount() : 1;
//                }
//            });
//        }
//    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (header == null) {
            return mData.size();
        } else {
            return mData.size() + 1;
        }
    }
    public boolean isHeader(int position) {
        return position == 0;
    }

    public class BodyViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView goodImg;
        private TextView goodTitleTex;
        private TextView goodPriceTex;
        private TextView goodsQuantity;

        public BodyViewHolder(View itemView) {
            super(itemView);
            goodImg = (SimpleDraweeView) itemView.findViewById(R.id.flower_market_goods_img);
            goodTitleTex = (TextView) itemView.findViewById(R.id.flower_market_goods_title);
            goodPriceTex = (TextView) itemView.findViewById(R.id.flower_market_commodity_price);
            goodsQuantity = (TextView) itemView.findViewById(R.id.flower_market_goods_quantity);
        }

        public SimpleDraweeView getGoodImg() {
            return goodImg;
        }

        public TextView getGoodTitleTex() {
            return goodTitleTex;
        }

        public TextView getGoodPriceTex() {
            return goodPriceTex;
        }

        public TextView getGoodsQuantity() {
            return goodsQuantity;
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


}
