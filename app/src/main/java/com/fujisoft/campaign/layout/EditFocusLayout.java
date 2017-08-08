package com.fujisoft.campaign.layout;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by 860115007 on 2017/2/9.
 */

public class EditFocusLayout extends CoordinatorLayout {
    private final static String TAG = "EditFocusLayout";

    private Context mContext;

    public EditFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View targetView = getTouchTargetView(this, x, y);
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (targetView == null) {//为空直接收起键盘
                imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            } else if (!(targetView instanceof EditText)) {//不为edittext也收起键盘
                imm.hideSoftInputFromWindow(targetView.getWindowToken(), 0);
            }
            this.requestFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取点击到的view
     *
     * @param viewGroup
     * @param x
     * @param y
     * @return
     */
    private View getTouchTargetView(ViewGroup viewGroup, int x, int y) {
        View touchView = null;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                View tempView = getTouchTargetView((ViewGroup) view, x, y);
                if (tempView != null) touchView = tempView;
            } else {
                if (isTouchPointInView(view, x, y)) touchView = view;
            }
        }
        return touchView;
    }

    private boolean isTouchPointInView(View view, int x, int y) {
        Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect.contains(x, y);
    }
}
