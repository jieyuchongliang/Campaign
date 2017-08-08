package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.GoodsDetailBean;
import com.fujisoft.campaign.service.OnInitSelectedPosition;

import java.util.ArrayList;
import java.util.List;


public class ColorTagAdapter extends BaseAdapter implements OnInitSelectedPosition {
    private final Context mContext;
    private final List<GoodsDetailBean.GoodsColor> mDataList;

    public ColorTagAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<GoodsDetailBean.GoodsColor>();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_tag);
        GoodsDetailBean.GoodsColor t = mDataList.get(position);
        textView.setText(t.getTextZh());
        return view;
    }

    public void onlyAddAll(List<GoodsDetailBean.GoodsColor> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<GoodsDetailBean.GoodsColor> datas) {
        mDataList.clear();
        onlyAddAll(datas);
    }

    @Override
    public boolean isSelectedPosition(int position) {
        if (position % 2 == 0) {
            return true;
        }
        return false;
    }
}
