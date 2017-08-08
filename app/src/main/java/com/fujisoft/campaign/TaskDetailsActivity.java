package com.fujisoft.campaign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.utils.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务详情页面
 */
public class TaskDetailsActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "campaign";
    Button btnTaskContinue;
    TextView txtTaskStatus;
    private String userId;
    private String taskId;

    private String imgTaskPictureUrl;
    private String strTaskName;
    private String strTaskCompany;
    private String strTaskPoints;

    private String strTaskRequirementDescription;
    private String completeFlag;
    private String requiredFlags;
    private String taskStatus;
    private String shareWay;
    private String maxShareTime, minShareTime, time;

    private String previousScreen;

    private LinearLayout center;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CODE_EXECUTE_SUCCESS:
                    refreshViews();
                    dismissProgressDialog();
                    setResult(Constants.CODE_TASK_GET_SUCCESS);
                    break;
                case Constants.CODE_EXECUTE_FAILURE:
                    dismissProgressDialog();
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    dismissProgressDialog();
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    finish();
                default:
                    break;
            }
        }

    };

    /**
     * 获取数据成功后，刷新显示页面
     */
    private void refreshViews() {

        SimpleDraweeView imgTaskPicture = (SimpleDraweeView) findViewById(R.id.img_task_picture);
        TextView txtTaskName = (TextView) findViewById(R.id.txt_task_name);
        txtTaskStatus = (TextView) findViewById(R.id.txt_task_status);
        WebView txt_task_content = (WebView) findViewById(R.id.txt_task_content);
        TextView textContent = (TextView) findViewById(R.id.txt_task_content_text);
        TextView txtTaskCompany = (TextView) findViewById(R.id.txt_task_company);
        TextView txtTaskPoints = (TextView) findViewById(R.id.txt_task_points);
        ImageView flower_icon_img = (ImageView) findViewById(R.id.flower_icon_img);

        btnTaskContinue = (Button) findViewById(R.id.btn_task_continue);
        btnTaskContinue.setOnClickListener(this);

//        // 更新任务状态 0：已结束 1：正在进行
//        if ("0".equals(taskStatus)) {
//            txtTaskStatus.setText(R.string.finish);
//        } else {
//            txtTaskStatus.setText(R.string.going);
//        }
/*        switch (getUserTaskStatus(completeFlag, taskStatus)) {
            case 0:
                txtTaskStatus.setText(getString(R.string.going));
                break;
            case 1:
                txtTaskStatus.setText(getString(R.string.string_shared_task_text));
                break;
            case 2:
                txtTaskStatus.setText(getString(R.string.finish));
                break;
            default:
                break;
        }*/
        switch (getUserTaskStatus(completeFlag, taskStatus, requiredFlags, maxShareTime, minShareTime, time)) {

            case 0:
                txtTaskStatus.setText(getString(R.string.going));
                break;
            case 1:
                txtTaskStatus.setText(getString(R.string.string_shared_task_text));
                break;
            case 2:
                txtTaskStatus.setText(getString(R.string.finish));
                break;
            case 3:
                txtTaskStatus.setText("继续分享");
                break;
            default:
                break;
        }
        // 更新任务图片、标题、企业名称、鲜花数量、任务具体内容
        imgTaskPicture.setImageURI(Constants.PICTURE_BASE_URL + imgTaskPictureUrl);
        txtTaskName.setText(strTaskName);
        txtTaskCompany.setText(strTaskCompany);
        if (requiredFlags.equals("3")) {
            flower_icon_img.setBackgroundResource(R.drawable.strawberry_2);
            txtTaskPoints.setText("1");
        } else {
            flower_icon_img.setBackgroundResource(R.mipmap.little_flower_icon);
            txtTaskPoints.setText(strTaskPoints);
        }

        //txtTaskPoints.setText(strTaskPoints);
        center.setVisibility(View.VISIBLE);
        txt_task_content.getSettings().setJavaScriptEnabled(true);
        txt_task_content.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 支持缩放(适配到当前屏幕)
        txt_task_content.getSettings().setSupportZoom(true);
        // 将图片调整到合适的大小
        txt_task_content.getSettings().setUseWideViewPort(true);
        // 设置可以被显示的屏幕控制
        txt_task_content.getSettings().setDisplayZoomControls(true);

        //WebView加载web资源
//        if (strTaskRequirementDescription.startsWith("http")) {
        if (null != strTaskRequirementDescription && !"".equals(strTaskRequirementDescription)) {
            txt_task_content.loadDataWithBaseURL(null, strTaskRequirementDescription, "text/html", "UTF-8", null);
        } else {
            txt_task_content.setVisibility(View.GONE);
            textContent.setVisibility(View.VISIBLE);
            textContent.setText(strTaskRequirementDescription);
        }
        // 迁移前的页面是已分享任务时，按钮显示“返回列表”，点击后返回到已分享任务页面
        if (previousScreen != null && previousScreen.equals("PublishedTasksActivity")) {
            btnTaskContinue.setText("返回列表");
            return;
        }
        switch (getUserTaskStatus(completeFlag, taskStatus, requiredFlags, maxShareTime, minShareTime, time)) {

            case 0:
                btnTaskContinue.setText(getString(R.string.task_share));
                break;
            case 1:
                btnTaskContinue.setText(getString(R.string.string_shared_task_text));
                break;
            case 2:
                btnTaskContinue.setText(getString(R.string.finish));
                btnTaskContinue.setBackgroundColor(getResources().getColor(R.color.gray));
                break;
            case 3:
                btnTaskContinue.setText("继续分享");
                break;
            default:
                break;
        /*switch (getUserTaskStatus(completeFlag, taskStatus)) {
            case 0:
                btnTaskContinue.setText(getString(R.string.task_share));
                break;
            case 1:
                btnTaskContinue.setText(getString(R.string.string_shared_task_text));
                break;
            case 2:
                btnTaskContinue.setText(getString(R.string.finish));
                btnTaskContinue.setBackgroundColor(getResources().getColor(R.color.gray));
                break;
            default:
                break;*/

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        center = (LinearLayout) findViewById(R.id.center);
        taskId = getIntent().getStringExtra(Constants.EXTRA_TASK_ID);
//        userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

        Log.d(TAG, "=== TaskDetailActivity#onCreate() taskId = " + taskId);
        Log.d(TAG, "=== TaskDetailActivity#onCreate() userId = " + userId);
        if (getIntent().hasExtra(Constants.EXTRA_PREVIOUS_SCREEN)) {
            previousScreen = getIntent().getStringExtra(Constants.EXTRA_PREVIOUS_SCREEN);
        }
        queryTaskDetails();

        Log.d(TAG, "===TaskDetailsActivity#onCreate() 广播注册=");
        IntentFilter filter = new IntentFilter(BaseActivity.SHARED_SUCCESS_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        getLayoutInflater().inflate(R.layout.toolbar_button, toolbar);

        ImageButton tool_bar_back_button = (ImageButton) findViewById(R.id.tool_bar_back_button);
        tool_bar_back_button.setVisibility(View.VISIBLE);
        tool_bar_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView tool_bar_center_text_view = (TextView) findViewById(R.id.tool_bar_center_text_view);
        tool_bar_center_text_view.setVisibility(View.VISIBLE);
        tool_bar_center_text_view.setText(R.string.task_details_title);
    }

    /**
     * 获取任务详情
     */
    private void queryTaskDetails() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("taskId", taskId));
                    params.add(new BasicNameValuePair("id", userId));

                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_ENTERPRISE_TASK_DETAILS, params);
                    Log.d(TAG, "=== TaskDetailActivity#queryTaskDetails() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        if (result) {
                            msg = jsonObject.get("msg").toString();
                            JSONObject dataObject = jsonObject.getJSONObject("data");

                            // 获取企业的相关信息
                            String enterprise = dataObject.get("enterprise").toString();
                            if (!"null".equals(enterprise) && !"".equals(enterprise)) {
                                JSONObject companyObject = dataObject.getJSONObject("enterprise");
                                strTaskCompany = companyObject.get("name").toString();// 获取企业的名称
                            } else {
                                strTaskCompany = "";
                            }

                            // 获取任务的相关信息
                            JSONObject dataObjectTask = dataObject.getJSONObject("task");
                            imgTaskPictureUrl = dataObjectTask.get("picUrl").toString();
                            strTaskName = dataObjectTask.get("name").toString();
                            strTaskRequirementDescription = dataObjectTask.getString("content");
                            Log.d(TAG, "=== content URL = " + strTaskRequirementDescription);
//                            if (null == strTaskRequirementDescription || "".equals(strTaskRequirementDescription) || !strTaskRequirementDescription.startsWith("http")) {
//                                // 获取content的URL数据失败后的处理
//                                message.obj = "获取的content是非法的:\n" + strTaskRequirementDescription;
//                                message.what = Constants.CODE_EXECUTE_EXCEPTION;
//                                mHandler.sendMessage(message);
//                            } else {
                            completeFlag = dataObjectTask.get("completeFlag").toString();
                            taskStatus = dataObjectTask.get("taskStatus").toString();
                            shareWay = dataObjectTask.get("shareWay").toString();
                            strTaskPoints = dataObjectTask.get("integral").toString();
                            // maxShareTime, minShareTime, time
                            maxShareTime = dataObjectTask.get("maxShareTime").toString();
                            minShareTime = dataObjectTask.get("minShareTime").toString();
                            requiredFlags = dataObjectTask.get("requiredFlags").toString();
                            if("3".equals(requiredFlags)){
                                time = dataObjectTask.get("time").toString();
                            }
                            //requiredFlags
                            // 获取数据成功后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mHandler.sendMessage(message);
//                            }
                        } else {
                            // 获取数据失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    // 获取数据发生异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (previousScreen != null && previousScreen.equals("PublishedTasksActivity")) {
            finish();
            return;
        }

        if (null != userId && !"".equals(userId)) {
            switch (getUserTaskStatus(completeFlag, taskStatus, requiredFlags, maxShareTime, minShareTime, time)) {
                //String completeFlag, String taskStatus, String requiredFlags, String maxShareTime, String minShareTime, String time
                case 0:
                    showShare(strTaskName, strTaskRequirementDescription, imgTaskPictureUrl.toString(), shareWay, userId, taskId);
                    break;
                case 1:
                    showMsg(getString(R.string.task_share_end), Toast.LENGTH_SHORT);
                    break;
                case 2:
                    showMsg(getString(R.string.task_end), Toast.LENGTH_SHORT);
                    break;
                case 3:
                    showShare(strTaskName, strTaskRequirementDescription, imgTaskPictureUrl.toString(), shareWay, userId, taskId);
                    break;
                default:
                    break;
            }

//            switch (getUserTaskStatus(completeFlag, taskStatus)) {
//                case 0:
//                    Log.d(TAG, "===TaskDetailsActivity#分享按钮点击，调用分享的方法===");
//                    showShare(strTaskName, strTaskRequirementDescription, imgTaskPictureUrl.toString(), shareWay, userId, taskId);
//                    break;
//                case 1:
//                    showMsg(getString(R.string.task_share_end), Toast.LENGTH_SHORT);
//                    break;
//                case 2:
//                    showMsg(getString(R.string.task_end), Toast.LENGTH_SHORT);
//                    break;
//                default:
//                    break;
//            }
        } else {
            toLogin();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("aaa", "=== TaskDetailsActivity#onReceive() 分享成功后，修改按钮的文言 ===");
            if ("SUCCESS".equals(intent.getExtras().getString("data"))) {
//                completeFlag = "1";
//                btnTaskContinue.setText(getString(R.string.string_shared_task_text));
//                txtTaskStatus.setText(getString(R.string.string_shared_task_text));
                queryTaskDetails();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}