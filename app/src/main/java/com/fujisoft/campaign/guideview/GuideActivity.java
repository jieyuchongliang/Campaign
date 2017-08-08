package com.fujisoft.campaign.guideview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fujisoft.campaign.R;
import com.fujisoft.campaign.TabMainActivity;
import com.fujisoft.campaign.service.IntentService;
import com.fujisoft.campaign.service.PushService;
import com.fujisoft.campaign.utils.Constants;
import com.igexin.sdk.PushManager;

import java.util.ArrayList;

public class GuideActivity extends Activity {

    // 显示导航页面的viewpager
    private ViewPager guideViewPager;

    // 页面适配器
    private ViewPagerAdapter guideViewAdapter;

    // 页面图片列表
    private ArrayList<View> mViews;

    // 图片资源
    private final int images[] = {R.drawable.guide_page1};

    // 底部导航的小点
    private ImageView[] guideDots;

    // 记录当前选中的图片
    private int currentIndex;

    // 还记得我们的开始按钮吗？
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_view);

        SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        // 读取SharedPreferences中的doneGuide值(默认为true，显示导航页)
        boolean doneGuide = sp.getBoolean("doneGuide", true);
        Log.d("aaa", "=== GuideActivity#onCreate() doneGuide = " +doneGuide);
        // 第一次Activity启动时表示
        if (doneGuide) {
            initView();
            initDot();

            // 修改SharedPreferences中的doneGuide值(false，不再显示导航页，除非重新安装本应用)
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("doneGuide", false);
            editor.commit();
            // 添加页面更换监听事件，来更新导航小点的状态。
            guideViewPager.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    setCurrentDot(arg0);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });

            // 开始按钮的点击事件监听
            startBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 我们随便跳转一个页面
                    Intent intent = new Intent(GuideActivity.this, TabMainActivity.class);
                    startActivity(intent);
                    GuideActivity.this.finish();
                }
            });

        } else {
            startMain();
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        guideViewPager = (ViewPager) findViewById(R.id.guide_view_pager);
        mViews = new ArrayList<View>();

        for (int i = 0; i < images.length; i++) {
            // 新建一个ImageView容器来放置我们的图片。
            ImageView iv = new ImageView(GuideActivity.this);
            iv.setBackgroundResource(images[i]);

            // 将容器添加到图片列表中
            mViews.add(iv);
        }

        View view = LayoutInflater.from(GuideActivity.this).inflate(R.layout.guide_content_view, null);
        mViews.add(view);

        // 现在为我们的开始按钮找到对应的控件
        startBtn = (Button) view.findViewById(R.id.start_btn);

        // 现在用到我们的页面适配器了
        guideViewAdapter = new ViewPagerAdapter(mViews);

        guideViewPager.setAdapter(guideViewAdapter);
    }

    /**
     * 初始化导航小点
     */
    private void initDot() {
        // 找到放置小点的布局
        LinearLayout layout = (LinearLayout) findViewById(R.id.guide_dots);

        // 初始化小点数组
        guideDots = new ImageView[mViews.size()];

        // 循环取得小点图片，让每个小点都处于正常状态
        for (int i = 0; i < mViews.size(); i++) {
            guideDots[i] = (ImageView) layout.getChildAt(i);
            guideDots[i].setSelected(false);
        }

        // 初始化第一个小点为选中状态
        currentIndex = 0;
        guideDots[currentIndex].setSelected(true);
    }

    /**
     * 页面更换时，更新小点状态
     *
     * @param position
     */
    private void setCurrentDot(int position) {
        if (position < 0 || position > mViews.size() - 1 || currentIndex == position) {
            return;
        }

        guideDots[position].setSelected(true);
        guideDots[currentIndex].setSelected(false);

        currentIndex = position;
    }

    private void startMain() {
        Intent intent = new Intent(getApplication(), TabMainActivity.class);
        startActivity(intent);
        GuideActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mViews) mViews.clear();
        if (null != guideViewPager) guideViewPager.removeAllViews();
    }
}