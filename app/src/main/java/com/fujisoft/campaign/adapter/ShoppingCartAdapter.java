package com.fujisoft.campaign.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.utils.Constants;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 860115003 on 2017/2/7.
 */

public abstract class ShoppingCartAdapter<T> extends BaseAdapter{

    private int mLayoutRes;
    private List<T> mData;

    public ShoppingCartAdapter(List<T> mData, int mLayoutRes) {
        this.mData = mData;
        this.mLayoutRes = mLayoutRes;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract void bindView(ShoppingCartAdapter.ViewHolder holder, T obj);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShoppingCartAdapter.ViewHolder holder = ShoppingCartAdapter.ViewHolder.bind(parent.getContext(), convertView, parent, mLayoutRes
                , position);
        bindView(holder,getItem(position));
        return holder.getItemView();
    }
    public void add(T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    public void add(int position, T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(position,data);
        notifyDataSetChanged();
    }


    public void remove(T data) {
        if (mData != null) {
            mData.remove(data);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder{
        private SparseArray<View> mViews;
        private View item;
        private int position;
        private Context context;
        private ViewHolder(Context context, ViewGroup parent, int layoutRes){
            mViews = new SparseArray<>();
            this.context = context;
            View convertView = LayoutInflater.from(context).inflate(layoutRes,parent,false);
            convertView.setTag(this);
            item = convertView;
        }

        public static ShoppingCartAdapter.ViewHolder bind(Context context, View convertView, ViewGroup parent, int layoutRes, int position) {
            ShoppingCartAdapter.ViewHolder holder;
            if (convertView == null) {
                holder = new ShoppingCartAdapter.ViewHolder(context,parent,layoutRes);
            } else  {
                holder = (ShoppingCartAdapter.ViewHolder)convertView.getTag();
                holder.item = convertView;
            }
            holder.position = position;
            return holder;
        }
        public <T extends View> T getView(int id) {
            T t = (T)mViews.get(id);
            if(t == null) {
                t = (T)item.findViewById(id);
                mViews.put(id,t);
            }
            return t;
        }
        public View getItemView() {
            return item;
        }
        public int getItemPosition() {
            return position;
        }
        public ShoppingCartAdapter.ViewHolder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }
        public ShoppingCartAdapter.ViewHolder setImageResource(int id, CharSequence drawableRes) {
            View view = getView(id);
            ((SimpleDraweeView) view).setImageURI(Constants.PICTURE_BASE_URL + drawableRes);
            return this;
        }
        public ShoppingCartAdapter.ViewHolder setOnClickListener(int id, View.OnClickListener listener) {
            getView(id).setOnClickListener(listener);
            return this;
        }
        public ShoppingCartAdapter.ViewHolder setVisibility(int id, int visible) {
            getView(id).setVisibility(visible);
            return this;
        }
        public ShoppingCartAdapter.ViewHolder setTag(int id, Object obj) {
            getView(id).setTag(obj);
            return this;
        }
    }
}
