package com.fujisoft.campaign.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fujisoft.campaign.MessageListActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.utils.Constants;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * 接收个推的推送Service
 */
public class IntentService extends GTIntentService {

    private static int cnt;

    @Override
    public void onReceiveServicePid(Context context, int i) {
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        //将CID缓存
        SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME_CID, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.EXTRA_USER_CID, clientid);
        editor.commit();
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        cnt++;
        byte[] payload = msg.getPayload();
        String data = new String(payload);
        Notification.Builder mBuilder = new Notification.Builder(this);
        //定义一个PendingIntent点击Notification后启动一个Activity
        Intent it = new Intent(context, MessageListActivity.class);
        PendingIntent pit = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle("消息通知").setContentText(data).setTicker("收到点灿发送过来的信息~").setSmallIcon(R.mipmap.push).setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true).setContentIntent(pit);
        NotificationManager mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notify1 = mBuilder.build();
        mNManager.notify(cnt, notify1);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
    }
}