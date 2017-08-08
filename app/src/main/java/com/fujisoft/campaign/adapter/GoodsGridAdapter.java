package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.MarketData;
import com.fujisoft.campaign.utils.Constants;

import java.util.List;

/**
 * 鲜花商城商品列表的Adapter
 */
public class GoodsGridAdapter extends RecyclerView.Adapter implements View.OnClickListener{

     Context context;
     List<MarketData.Goods> mData;

    public GoodsGridAdapter(Context context, List<MarketData.Goods> mData) {
        this.context = context;
        this.mData = mData;
    }

    public void reloadData(List<MarketData.Goods> goodsData) {
        this.mData.addAll(goodsData);
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(viewGroup.getContext(), R.layout.flower_market_goods_item, null);
        BodyViewHolder holder = new BodyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        //其他条目中的逻辑在此
        final BodyViewHolder bodyViewHolder = (BodyViewHolder) viewHolder;
        MarketData.Goods goods = mData.get(position);
        bodyViewHolder.getGoodImg().setImageURI(Constants.PICTURE_BASE_URL +goods.getGoodsLogo());
        bodyViewHolder.getGoodImg().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        bodyViewHolder.getGoodTitleTex().setText(goods.getGoodsName());
        // 我的鲜花数
        String myScoreStr = "";
        int myScore = Integer.valueOf(goods.getScorePrice());
        if (myScore >= 10000) {
            float myScoreNum = ((float) Math.round(myScore / 100) / 100);
            //int myScoreNum = ((int) Math.round(myScore / 100) / 100);
            myScoreStr = String.valueOf(myScoreNum) + "万";
        } else {
            myScoreStr = String.valueOf(myScore);
        }
        bodyViewHolder.getGoodPriceTex().setText(myScoreStr);
/*        bodyViewHolder.getGoodPriceTex().setText(goods.getScorePrice());*/
        bodyViewHolder.getGoodsQuantity().setText("剩余："+goods.getGoodsQuantity());
        viewHolder.itemView.setTag(goods.getGoodsId());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public class BodyViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder{
        private SimpleDraweeView goodImg;
        private TextView goodTitleTex;
        private TextView goodPriceTex;
        private TextView goodsQuantity;

        public BodyViewHolder(View itemView) {
            super(itemView);
            goodImg = (SimpleDraweeView) itemView.findViewById(R.id.flower_market_goods_img);
            goodTitleTex = (TextView) itemView.findViewById(R.id.flower_market_goods_title);
            goodPriceTex =  (TextView)  itemView.findViewById(R.id.flower_market_commodity_price);
            goodsQuantity = (TextView) itemView.findViewById(R.id.flower_market_goods_quantity);
        }
        public SimpleDraweeView getGoodImg() {
            return goodImg;
        }
        public TextView getGoodTitleTex() {return goodTitleTex;}
        public TextView getGoodPriceTex() {
            return goodPriceTex;
        }
        public TextView getGoodsQuantity() {
            return goodsQuantity;
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(String)v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void clear() {
        mData.clear();

        this.notifyDataSetChanged();
    }
}
