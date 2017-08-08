package com.fujisoft.campaign.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.fujisoft.campaign.R;

/**
 * Created by luojy
 */
public class DownLoadButton extends Button {

    private Paint paint;

    private int textColor;

    private Drawable downLoadBackground;

    /**
     * 当前进度
     * 百分比
     */
    private int curPrecent = 0;


    private String display;

    private OnDownLoadButtonClickListener onDownLoadButtonClickListener;

    public DownLoadButton(Context context) {
        this(context, null);
    }

    public DownLoadButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownLoadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownLoadButton, defStyleAttr, 0);
        downLoadBackground = getResources().getDrawable(R.drawable.rect_downloaded_bg);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.DownLoadButton_normalBackground:
                    break;
                case R.styleable.DownLoadButton_downLoadedBackground:
                    downLoadBackground = a.getDrawable(attr);
                    break;
                case R.styleable.DownLoadButton_downLoadCompleteBackground:
                    break;
                case R.styleable.DownLoadButton_textColor:
                    textColor = a.getColor(attr, getResources().getColor(R.color.white));
                    break;
            }
        }
        /**
         * 设置button本身的文字为透明以免干扰我们自己绘制上去的文字
         */
        setTextColor(getResources().getColor(R.color.color_normal));
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        paint.setColor(textColor);

        setGravity(Gravity.CENTER);

    }


    /**
     * 设置进度
     *
     * @param precent
     *        完成进度百分比
     */
    public void setDownLoadProgress(int precent, String display) {
        this.curPrecent = precent;
        this.display = display;
//        postInvalidate();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;
        /**
         * 计算文本显示所需宽高
         */
        Rect textBound = new Rect();

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize+getPaddingLeft()+getPaddingRight();
        }else{
            width = textBound.width()+getPaddingLeft()+getPaddingRight();
        }

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize+getPaddingTop()+getPaddingBottom();
        }else{
            height = textBound.height()+getPaddingTop()+getPaddingBottom();
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        String tip = "";
//        tip = curPrecent + "%";
        //计算当前进度所需宽度
        int downLoadedWidth = (int) (getMeasuredWidth() * ((double) curPrecent / 100));
        Rect rect = new Rect(0, 0, downLoadedWidth, getMeasuredHeight());
        downLoadBackground.setBounds(rect);
        downLoadBackground.draw(canvas);

//        /**
//         * 绘制提示文本
//         */
//        Rect textBound = new Rect();
//        paint.getTextBounds(display, 0, display.length(), textBound);
//        canvas.drawText(display,(getMeasuredWidth()-textBound.width())/2,(getMeasuredHeight()+textBound.height())/2, paint);
    }

    public interface OnDownLoadButtonClickListener {
        void onClick(View v, int curState);
    }

}
