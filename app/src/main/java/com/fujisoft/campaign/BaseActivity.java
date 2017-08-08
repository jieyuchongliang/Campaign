package com.fujisoft.campaign;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.fujisoft.campaign.toolbar.ToolBarHelper;
import com.fujisoft.campaign.tools.CustomProgressDialog;
import com.fujisoft.campaign.utils.ActivitiesManager;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.MultipartEntityEx;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public abstract class BaseActivity extends AppCompatActivity {
    private String TAG = "campaign";
    private ToolBarHelper mToolBarHelper;
    public Toolbar toolbar;
    private CustomProgressDialog mProgressDialog;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    Intent intent = new Intent(SHARED_SUCCESS_ACTION);
                    intent.putExtra("data", "SUCCESS");
                    Log.d(TAG, "=== BaseActivity#mHandler 发送广播 ");
                    sendBroadcast(intent);
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
            }
        }
    };
    public static final String SHARED_SUCCESS_ACTION = "shared.success.broadcast.action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // activity加入栈方便管理
        ActivitiesManager.getInstance().addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {

        if(layoutResID!=R.layout.splash){
            mToolBarHelper = new ToolBarHelper(this, layoutResID);
            toolbar = mToolBarHelper.getToolBar();
            setContentView(mToolBarHelper.getContentView());
            setSupportActionBar(toolbar);
            onCreateCustomToolBar(toolbar);
        }else {

            super.setContentView(layoutResID);
        }
    }

    public void onCreateCustomToolBar(Toolbar toolbar) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.common_color));
        }
        toolbar.setContentInsetsRelative(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showExitDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BaseActivity.this);
        builder.setTitle(getString(R.string.login_dialog_hint))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 保存数据到SharedPreferences
                        // Context.MODE_PRIVATE : 指定该SharedPreferences数据只能被本应用程序读、写
                        SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(Constants.SHARE_DIALOG, "SHOWED");
                        editor.commit();
                    }
                });

        builder.setMessage(getString(R.string.share_dialog));
        builder.create().show();
    }

    /**
     * 分享用方法
     *
     * @param title            ：分享的标题
     * @param text             ：分享的文本内容
     * @param picUrl：分享的图片资源路径
     */
    public void showShare(final String title, final String text, final String picUrl, final String shareWay, final String userID, final String taskId) {


//        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
//        String showed = sharedPreferences.getString(Constants.SHARE_DIALOG, "");
//        if (!"SHOWED".equals(showed)) {
//            showExitDialog();
//            //return;
//        }

        Log.d(TAG, "=== BaseActivity#showShare() title = " + title);
        Log.d(TAG, "=== BaseActivity#showShare() text = " + text);
        Log.d(TAG, "=== BaseActivity#showShare() picUrl = " + picUrl);
        Log.d(TAG, "=== BaseActivity#showShare() shareWay = " + shareWay);
        ArrayList<String> list = new ArrayList<>();

        if (null != shareWay && !"".equals(shareWay) && shareWay.contains(",")) {
            String[] way = shareWay.split(",");
            for (int i = 0; i < way.length; i++) {
                list.add(way[i]);
            }

        } else if (null != shareWay && !"".equals(shareWay)) {
            list.add(shareWay);
        }
        ShareSDK.initSDK(this);
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        if (!list.contains("QQ")) {
            oks.addHiddenPlatform(QQ.NAME);
        }
        if (!list.contains("QZone")) {
            oks.addHiddenPlatform(QZone.NAME);
        }
        if (!list.contains("SinaWeibo")) {
            oks.addHiddenPlatform(SinaWeibo.NAME);
        }
        if (!list.contains("Wechat")) {
            oks.addHiddenPlatform(Wechat.NAME);
        }
        if (!list.contains("WechatMoments")) {
            oks.addHiddenPlatform(WechatMoments.NAME);
        }
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(Constants.PICTURE_BASE_URL + picUrl);
        oks.setTitleUrl(Constants.PICTURE_BASE_URL + "/App/Task/shareDetail?taskId=" + taskId);

        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片

        //oks.setImagePath(Constants.PICTURE_BASE_URL + picUrl);

        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl(Constants.PICTURE_BASE_URL + picUrl);
        oks.setUrl(Constants.PICTURE_BASE_URL + "/App/Task/shareDetail?taskId=" + taskId);

//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(Constants.PICTURE_BASE_URL + picUrl);
        //网络图片的url：所有平台
        oks.setImageUrl(Constants.PICTURE_BASE_URL + picUrl);

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                Log.d(TAG, "=== BaseActivity#showShare() platform = " + platform.getName());
                if ("QQ".equals(platform.getName())) {
                    // paramsToShare.setTitle(title);
                    paramsToShare.setText(text);
                    //paramsToShare.setImagePath(Constants.PICTURE_BASE_URL + picUrl);
                    paramsToShare.setImageUrl(Constants.PICTURE_BASE_URL + picUrl);
                    // paramsToShare.setShareType(0);
                }
                if ("QZone".equals(platform.getName())) {
                    paramsToShare.setTitle(title);
                    paramsToShare.setText(text);
                    paramsToShare.setImageUrl(Constants.PICTURE_BASE_URL + picUrl);
                    //paramsToShare.setImagePath(Constants.PICTURE_BASE_URL + picUrl);
                }
                if ("SinaWeibo".equals(platform.getName())) {
                    paramsToShare.setTitle(title);
                    paramsToShare.setText(text);
                    //paramsToShare.setImagePath(Constants.PICTURE_BASE_URL + picUrl);
                    paramsToShare.setImageUrl(Constants.PICTURE_BASE_URL + picUrl);
                }
                if ("Wechat".equals(platform.getName())) {
//                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                    paramsToShare.setImageData(imageData);
                    paramsToShare.setTitle(title);
                    paramsToShare.setText(text);
                    //paramsToShare.setImagePath(Constants.PICTURE_BASE_URL + picUrl);
                    paramsToShare.setImageUrl(Constants.PICTURE_BASE_URL + picUrl);
                }
                if ("WechatMoments".equals(platform.getName())) {
//                    Bitmap imageData = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
//                    paramsToShare.setImageData(imageData);
                    paramsToShare.setTitle(title);
                    paramsToShare.setText(text);
                    //paramsToShare.setImagePath(Constants.PICTURE_BASE_URL + picUrl);
                    paramsToShare.setImageUrl(Constants.PICTURE_BASE_URL + picUrl);
                }
            }
        });

        // 分享成功后回调的方法
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                Log.d(TAG, "=== BaseActivity#showShare() PlatformActionListener--onComplete ");
                shareSuccessRequest(userID, taskId);
//                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
//                String showed = sharedPreferences.getString(Constants.SHARE_DIALOG, "");
//                if (!"SHOWED".equals(showed)) {
//                    showExitDialog();
//                   // return;
//                }

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d(TAG, "=== BaseActivity#showShare() PlatformActionListener--onError ");
                showMsg(getString(R.string.string_share_error), Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d(TAG, "=== BaseActivity#showShare() PlatformActionListener--onCancel ");
                showMsg(getString(R.string.string_share_cancel), Toast.LENGTH_SHORT);
            }
        });
        // 启动分享GUI
        oks.show(this);
    }

    /**
     * 分享成功后，通过回调接口请求服务器，更新任务的状态
     *
     * @param userId
     * @param taskId
     */
    public void shareSuccessRequest(final String userId, final String taskId) {
        if (Utils.isConnect(this)) {
            showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    String msg = "Exception";
                    try {
                        List<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair("id", userId));
                        params.add(new BasicNameValuePair("taskId", taskId));

                        // 获取响应的结果信息
                        String json = getPostData(Constants.URL_SHARE_SUCCESS, params);
                        // JSON的解析过程
                        if (json != null) {
                            JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                            Log.d(TAG, "=== BaseActivity#shareSuccessRequest() jsonObject = " + jsonObject.toString());
                            boolean result = jsonObject.getBoolean("success");
                            msg = jsonObject.getString("msg");
                            if (result) {
                                // 成功后的处理
                                message.obj = msg;
                                message.what = Constants.CODE_EXECUTE_SUCCESS;
                                mHandler.sendMessage(message);
                            } else {
                                // 失败后的处理
                                message.obj = msg;
                                message.what = Constants.CODE_EXECUTE_FAILURE;
                                mHandler.sendMessage(message);
                            }
                        }
                    } catch (Exception e) {
                        // 异常后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_EXCEPTION;
                        mHandler.sendMessage(message);
                    }
                }
            }).start();
        } else {
            Utils.showToast(this, R.string.netWrong);
        }
    }

    /**
     * 用Post方式请求服务器，并(采用证书方式访问服务器)
     *
     * @param url    :访问服务器的URL
     * @param params ：传递的参数-->List型
     * @return
     */
    public String getPostData(String url, final List params) {
        AssetManager am = BaseActivity.this.getAssets();
        InputStream ins = null;
        try {
            ins = am.open("certificate.crt");
            //读取证书
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(ins);
            //创建一个证书库，并将证书导入证书库
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", cer);
            //把证书库作为信任证书库
            //不需要密码
            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
            //需要密码
//            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore,"123456");
            Scheme sch = new Scheme("https", socketFactory, 443);
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);

            //发送请求数据
            HttpPost requestPost = new HttpPost(url);
            requestPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            //获取响应的结果
            HttpResponse response = httpClient.execute(requestPost);
            //获取HttpEntity
            HttpEntity entity = response.getEntity();
            //获取响应的结果信息
            return EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception";
            }
        }
    }

    /**
     * 用Post方式请求服务器，并(采用证书方式访问服务器)
     *
     * @param url             :访问服务器的URL
     * @param multiPartEntity ：传递的参数-->multiPartEntity型
     * @return
     */
    public String getPostData(String url, final MultipartEntityEx multiPartEntity) {
        AssetManager am = BaseActivity.this.getAssets();
        InputStream ins = null;
        try {
            ins = am.open("certificate.crt");
            //读取证书
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(ins);
            //创建一个证书库，并将证书导入证书库
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", cer);
            //把证书库作为信任证书库
            //不需要密码
            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
            //需要密码
//            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore,"123456");
            Scheme sch = new Scheme("https", socketFactory, 443);
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);

            //发送请求数据
            HttpPost requestPost = new HttpPost(url);
            requestPost.setEntity(multiPartEntity);
            //获取响应的结果
            HttpResponse response = httpClient.execute(requestPost);
            //获取HttpEntity
            HttpEntity entity = response.getEntity();
            //获取响应的结果信息
            return EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception";
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception";
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivitiesManager.getInstance().moveActivity(this);
        ShareSDK.stopSDK(this);
    }

    /**
     * 关闭ProgressDialog
     */
    public void dismissProgressDialog() {
        if (this.mProgressDialog != null && !isFinishing()) {
            try {
                this.mProgressDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * show toast
     *
     * @param msg
     * @param duration
     */
    public void showMsg(String msg, int duration) {
        if (!(Utils.isNull(msg)))
            Toast.makeText(this, msg, duration).show();
    }

    /**
     * 提示警告的Dialog
     *
     * @param message：提示的内容
     */
    public void showConfirmDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage(message)
                .setPositiveButton("确定", null).create();
        dialog.show();
    }

    /**
     * 提示错误的Dialog
     *
     * @param message：提示的内容
     * @param activity：要结束的Activity
     */
    public void showErrorDialog(String message, final Activity activity) {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                }).create();
        dialog.show();
    }

    /**
     * showProgressDialog
     */
    public void showProgressDialog() {
        if (this.mProgressDialog == null) {
//            this.mProgressDialog = new CustomProgressDialog(this);

////            this.mProgressDialog = new ProgressDialog(this);
//
////            **设置透明度*/
//            Window window = mProgressDialog.getWindow();
//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.alpha = 0.7f;// 透明度
//            lp.dimAmount = 0.8f;// 黑暗度
//            window.setAttributes(lp);

//            this.mProgressDialog = new CustomProgressDialog(this);

            if (mProgressDialog == null) {
                mProgressDialog = CustomProgressDialog.createDialog(BaseActivity.this, R.anim.my_anim);
            }

            this.mProgressDialog.setCancelable(true);
            this.mProgressDialog.setCanceledOnTouchOutside(false);
        }
        if (!isFinishing()) {
            try {
                this.mProgressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取得本程序的包名
     */
    public String getPackage() {
        String packageName = "";
        ApplicationInfo info;
        try {
            info = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            packageName = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }

    /**
     * 页面迁移至登录页面
     */
    public void toLogin() {
        showMsg(getString(R.string.mine_message), Toast.LENGTH_SHORT);
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    /**
     * 通过任务的完成状态、进行状态判断当前的任务处于何种状态
     *
     * @param completeFlag
     * @param taskStatus
     * @return
     */
    public static int getUserTaskStatus(String completeFlag, String taskStatus, String requiredFlags, String maxShareTime, String minShareTime, String time) {
/*        if (task.getRequiredFlags().equals("3")) {
            if (Integer.valueOf(task.getTime()) < Integer.valueOf(task.getMinShareTime())) {
                holder.button.setText("一键分享");
            }else if( Integer.valueOf(task.getMinShareTime())<=Integer.valueOf(task.getTime())&&Integer.valueOf(task.getTime())<=Integer.valueOf(task.getMaxShareTime()) ){
                holder.button.setText("继续分享");
            }else if( Integer.valueOf(task.getTime())>Integer.valueOf(task.getMaxShareTime()) ){
                holder.button.setText("已完成");
            }
        }*/
        if ("3".equals(requiredFlags) && "1".equals(taskStatus)) {
            if (0 <= Integer.valueOf(time) && Integer.valueOf(time) < Integer.valueOf(minShareTime)) {
                //一键分享
                return 0;
            } else if (Integer.valueOf(minShareTime) <= Integer.valueOf(time) && Integer.valueOf(time) < Integer.valueOf(maxShareTime)) {
                //继续分享
                return 3;
            } else {
                //已完成
                return 1;
            }
        } else if ("0".equals(completeFlag) && "1".equals(taskStatus)) {
            //未完成
            return 0;
        } else if ("1".equals(completeFlag) && "1".equals(taskStatus)) {
            // 已分享
            return 1;
        } else {
            // 已结束
            return 2;
        }
    }

    public static int getUserTaskStatus(String completeFlag, String taskStatus) {
        if ("0".equals(completeFlag) && "1".equals(taskStatus)) {
            //未完成
            return 0;
        } else if ("1".equals(completeFlag) && "1".equals(taskStatus)) {
            // 已分享
            return 1;
        } else {
            // 已结束
            return 2;
        }
    }
}