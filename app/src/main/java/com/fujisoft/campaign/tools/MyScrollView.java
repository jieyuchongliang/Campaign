package com.fujisoft.campaign.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

    View mHideLinearLayout;
    View mFixedLinearLayout;

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
        if (mHideLinearLayout != null && mFixedLinearLayout != null) {
            if (t >= mHideLinearLayout.getHeight()) {
                mFixedLinearLayout.setVisibility(View.VISIBLE);
            } else {
                mFixedLinearLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 监听浮动view的滚动状态
     *
     * @param topView  顶部区域view，即当ScrollView滑动的高度要大于等于哪个view的时候隐藏floatview
     * @param flowView 浮动view，即要哪个view停留在顶部
     */
    public void listenerFlowViewScrollState(View topView, View flowView) {
        mHideLinearLayout = topView;
        mFixedLinearLayout = flowView;
    }
}