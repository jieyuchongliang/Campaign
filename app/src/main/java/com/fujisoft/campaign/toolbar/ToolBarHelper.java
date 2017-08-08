package com.fujisoft.campaign.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fujisoft.campaign.R;

public class ToolBarHelper {

    private Context mContext;

    /*base view*/
    private FrameLayout mContentView;

    /* ユーザーのビュー */
    private View mUserView;

    /* toolbar */
    private Toolbar mToolBar;

    private LayoutInflater mInflater;

    /*
    * 二つ属性
    * 1、toolbarはウィンドウの上にオーバーレイするか
    * 2、toolbarの高さ
    * */
    private static int[] ATTRS = {
            R.attr.windowActionBarOverlay,
            R.attr.actionBarSize
    };

    public ToolBarHelper(Context context, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        initContentView();
        initUserView(layoutId);
        initToolBar();
    }

    private void initContentView() {
        /* ビューのAdapterとして、フレームレイアウトを作る */
        mContentView = new FrameLayout(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);

    }

    private void initToolBar() {
        View toolbar = mInflater.inflate(R.layout.toolbar, mContentView);
        mToolBar = (Toolbar) toolbar.findViewById(R.id.id_tool_bar);
    }

    @SuppressWarnings("ResourceType")
    private void initUserView(int id) {
        mUserView = mInflater.inflate(id, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(ATTRS);
        boolean overly = typedArray.getBoolean(0, false);
        int toolBarSize = (int) typedArray.getDimension(1, (int) mContext.getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
        typedArray.recycle();
        params.topMargin = overly ? 0 : toolBarSize;
        mContentView.addView(mUserView, params);

    }

    public FrameLayout getContentView() {
        return mContentView;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }
}
