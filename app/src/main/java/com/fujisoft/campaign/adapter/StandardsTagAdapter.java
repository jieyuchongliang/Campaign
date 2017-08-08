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

/**
 * Created by dell on 2017/2/7.
 */
public class StandardsTagAdapter extends BaseAdapter implements OnInitSelectedPosition {
    private final Context mContext;
    private List<GoodsDetailBean.Standards> mDataList;

    public StandardsTagAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<GoodsDetailBean.Standards>();
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
        GoodsDetailBean.Standards t= mDataList.get(position);
        textView.setText(t.getStandardDec());
        return view;
    }

    public void onlyAddAll(List<GoodsDetailBean.Standards> datas) {
        mDataList.addAll(datas);
       // this.mDataList =datas;
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<GoodsDetailBean.Standards> datas) {
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
