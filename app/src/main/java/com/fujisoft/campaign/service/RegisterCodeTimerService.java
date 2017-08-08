package com.fujisoft.campaign.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.fujisoft.campaign.utils.RegisterCodeTimer;

/**
 * 注册验证码计时服务
 *
 * @author zihao
 */
public class RegisterCodeTimerService extends Service {

    private static Handler mHandler;
    private static RegisterCodeTimer mCodeTimer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mCodeTimer = new RegisterCodeTimer(60000, 1000, mHandler);
        mCodeTimer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
            mCodeTimer.cancel();
    }

    /**
     * 设置Handler
     */
    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

}
