package com.fujisoft.campaign.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.AddressManageActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.bean.AddressBean;

import java.util.List;

/**
 * 地址管理用Adapter
 */
public class AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = "campaign";
    private AddressManageActivity mContext;
    private List<AddressBean> mDataList;

    public AddressAdapter(AddressManageActivity context, List<AddressBean> data) {
        if (null != this.mDataList && 0 != this.mDataList.size()) {
            this.mDataList.clear();
        }
        this.mContext = context;
        this.mDataList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_manager_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (null == mDataList.get(position))
            return;
        if (holder instanceof AddressAdapter.MyViewHolder) {
            ((MyViewHolder) holder).name.setText(mDataList.get(position).getName());
            ((MyViewHolder) holder).address.setText(mDataList.get(position).getProvince()
                    + mDataList.get(position).getCity()
                    + mDataList.get(position).getRegion()
                    + mDataList.get(position).getStreet());
            ((MyViewHolder) holder).tel.setText(mDataList.get(position).getPhone());
            if ("1".equals(mDataList.get(position).getIsDefault())) {
                ((MyViewHolder) holder).switch_address.setChecked(true);
                ((MyViewHolder) holder).switch_address.setClickable(false);
                //((MyViewHolder) holder).switch_address.setClickable(true);
            } else if ("0".equals(mDataList.get(position).getIsDefault())) {

                ((MyViewHolder) holder).switch_address.setChecked(false);
                ((MyViewHolder) holder).switch_address.setClickable(true);
            }

            ((MyViewHolder) holder).switch_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        try {
                                (mContext).changeDefault(mDataList.get(position).getId());
                                for (int i = 0; i < mDataList.size(); i++) {
                                    Log.d(TAG, "=== AddressAdapter#changeDefault() i = " + i);

                                    if (i == position) {
                                        mDataList.get(i).setIsDefault("1");
                                    } else {
                                        mDataList.get(i).setIsDefault("0");
                                    }
                                }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }/*else{
                        //(mContext).changeFirst(mDataList.get(position).getId());
                        try {
                            (mContext).changeFirst(mDataList.get(position).getId());
                            for (int i = 0; i < mDataList.size(); i++) {
                                Log.d(TAG, "=== AddressAdapter#changeDefault() i = " + i);

                                if (i == position) {
                                    mDataList.get(i).setIsDefault("0");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/

                }
            });
            ((MyViewHolder) holder).button_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if ("1".equals(mDataList.get(position).getIsDefault())) {
                            Toast.makeText(mContext, "默认地址不可删除！", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog dialog = new AlertDialog.Builder(mContext)
                                    .setTitle("确认")
                                    .setMessage("确认删除么？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (mContext instanceof AddressManageActivity) {
                                                if (mDataList.size() > 0) {
                                                    Log.d(TAG, "=== AddressAdapter#delAddress() delete = " + mDataList.get(position).getId());
                                                    (mContext).delAddress(mDataList.get(position).getId());
                                                    mDataList.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create();
                            dialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;
        TextView tel;
        Button button_del;
        Switch switch_address;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView
                    .findViewById(R.id.address_nm);
            address = (TextView) itemView
                    .findViewById(R.id.address);
            tel = (TextView) itemView
                    .findViewById(R.id.address_tel);
            button_del = (Button) itemView
                    .findViewById(R.id.button_del);
            switch_address = (Switch) itemView.findViewById(R.id.switch_address);
        }
    }
}