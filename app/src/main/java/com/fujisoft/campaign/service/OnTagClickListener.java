package com.fujisoft.campaign.service;

import android.view.View;

import com.fujisoft.campaign.adapter.FlowTagLayout;

public interface OnTagClickListener {
    void onItemClick(FlowTagLayout parent, View view, int position);
}
