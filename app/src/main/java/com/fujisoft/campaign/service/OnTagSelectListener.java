package com.fujisoft.campaign.service;

import com.fujisoft.campaign.adapter.FlowTagLayout;

import java.util.List;

public interface OnTagSelectListener {
    void onItemSelect(FlowTagLayout parent, List<Integer> selectedList);
}
