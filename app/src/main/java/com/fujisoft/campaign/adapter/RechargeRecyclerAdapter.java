package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fujisoft.campaign.R;
import com.fujisoft.campaign.utils.RechargeItemModel;

import java.util.ArrayList;

/**
 * 充值页面用Adapter
 */
public class RechargeRecyclerAdapter extends RecyclerView.Adapter<RechargeRecyclerAdapter.BaseViewHolder> {

    private ArrayList<RechargeItemModel> mRechargeAmountList = new ArrayList<>();
    private int lastPressIndex = -1;
    private OnItemClickListener mOnItemClickListener;

    //定义接口
    public interface OnItemClickListener {
        void onSelectRechargeAmount(String selectAmount);
        void clearInputAmount();
    }

    public RechargeRecyclerAdapter(Context context) {
        mOnItemClickListener = (OnItemClickListener) context;
    }

    public void replaceAll(ArrayList<RechargeItemModel> list) {
        mRechargeAmountList.clear();
        if (list != null && list.size() > 0) {
            mRechargeAmountList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case RechargeItemModel.AMOUNT:
                return new RechargeAmountViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recharge_amount_layout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        holder.setData(mRechargeAmountList.get(position).data);
    }

    @Override
    public int getItemViewType(int position) {
        return mRechargeAmountList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mRechargeAmountList != null ? mRechargeAmountList.size() : 0;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        public TextView mRechargeAmount;

        public BaseViewHolder(View itemView) {
            super(itemView);
            mRechargeAmount = (TextView) itemView.findViewById(R.id.recharge_amount_tv);
        }

        void setData(Object data) {
        }
    }

    public class RechargeAmountViewHolder extends BaseViewHolder {

        public RechargeAmountViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (lastPressIndex == position) {
                        lastPressIndex = -1;
                    } else {
                        lastPressIndex = position;
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        void setData(Object data) {
            if (data != null) {
                String text = (String) data;
                mRechargeAmount.setText(text);
                if (getAdapterPosition() == lastPressIndex) {
                    // 当金额被选中时，调用接口传递选中的值并清除输入的值
                    mOnItemClickListener.onSelectRechargeAmount(mRechargeAmount.getText().toString());
                    mOnItemClickListener.clearInputAmount();

                    mRechargeAmount.setSelected(true);
                    mRechargeAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.common_color));

                } else {
                    mRechargeAmount.setSelected(false);
                    mRechargeAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
                }
            }
        }
    }
}