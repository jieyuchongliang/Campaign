package com.fujisoft.campaign.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.fujisoft.campaign.LocationActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.guideview.GuideActivity;
import com.fujisoft.campaign.service.IntentService;
import com.fujisoft.campaign.service.PushService;
import com.fujisoft.campaign.toolbar.ToolBarHelper;
import com.igexin.sdk.PushManager;

/**
 * 应用启动欢迎页
 * 3秒淡入效果
 */
public class SplashActivity extends LocationActivity {

    public static Typeface iconTypeFace;
    public static boolean doneSplash = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.splash);

            PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);

            PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), IntentService.class);

            Log.d("aaa", "=== SplashActivity#onCreate() doneSplash = " + doneSplash);
            if (doneSplash == false) {
                doneSplash = true;
                //高性能图片库、网络访问库
                Utils.getRetrofit(this);
                //加载图标字体
                iconTypeFace = Typeface.createFromAsset(getAssets(), "fonts/iconfont.ttf");

                ImageView img = (ImageView) findViewById(R.id.welcome_page_img);
                // 淡入
                AlphaAnimation fadein_anim = new AlphaAnimation(0, 1);
                // 图片表示时间
                fadein_anim.setDuration(3000);
                img.startAnimation(fadein_anim);

                Handler hdl = new Handler();
                // 延迟4秒执行splashHandler
                hdl.postDelayed(new splashHandler(), 3000);
            } else {
                startMain();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    private void startMain() {
        Intent intent = new Intent(getApplication(), GuideActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    class splashHandler implements Runnable {
        public void run() {
            startMain();
        }
    }

    /**
     * 歡迎頁面中，屏蔽返回鍵
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}