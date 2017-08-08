package com.fujisoft.campaign.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fujisoft.campaign.R;

public class TextViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;
    public TextViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.flower_market_goods_title);
    }
}