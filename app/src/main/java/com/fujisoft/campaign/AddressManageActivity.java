package com.fujisoft.campaign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.adapter.AddressAdapter;
import com.fujisoft.campaign.bean.AddressBean;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.fujisoft.campaign.utils.Constants.CODE_EXECUTE_SUCCESS_CHANGE;

/**
 * 地址管理页面
 */
public class AddressManageActivity extends BaseActivity {
    private String TAG = "campaign";
    private RecyclerView mRecyclerView;
    private AddressAdapter mAdapter;
    private String userId = null;
    List<AddressBean> listAddress = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    initView();
                    dismissProgressDialog();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS_CHANGE:
                    // 判断网络是否连接，请求服务器，接收页面数据
                    if (Utils.isConnect(AddressManageActivity.this)) {
                        // 从服务器端获取数据
                        getData();
                    } else {
                        Utils.showToast(AddressManageActivity.this, R.string.netWrong);
                    }
                    showMsg(msg.obj.toString(), Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manage);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.address_manager_list);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG, "=== AddressManagerActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);
        showProgressDialog();
        // 判断网络是否连接，请求服务器，接收页面数据
        if (Utils.isConnect(this)) {
            // 从服务器端获取数据
            getData();
        } else {
            Utils.showToast(this, R.string.netWrong);
        }
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
        tool_bar_center_text_view.setText(R.string.address_manager);
        TextView tool_bar_right_bottom_button = (TextView) findViewById(R.id.tool_bar_right_bottom_button);
        tool_bar_right_bottom_button.setVisibility(View.VISIBLE);
        tool_bar_right_bottom_button.setText(R.string.add_address);
        tool_bar_right_bottom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AddAddressIntent = new Intent();
                AddAddressIntent.setClass(AddressManageActivity.this, AddAddressActivity.class);
                startActivityForResult(AddAddressIntent, Constants.CODE_EXECUTE_SUCCESS);
            }
        });
    }


    private void initView() {
        mAdapter = new AddressAdapter(AddressManageActivity.this, listAddress);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        Log.d(TAG, "=== AddressManagerActivity#initView() listAddress = " + listAddress);
        //设置Adapter
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String resultMessage = "Exception";
                Message message = new Message();
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_ADDRESS, params);
                    Log.d(TAG, "=== AddressManagerActivity#getData() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        resultMessage = jsonObject.getString("msg");
                        if (result) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            JSONArray addArray = dataObject.getJSONArray("addList");
                            if (null != listAddress && 0 != listAddress.size()) {
                                listAddress.clear();
                            }

                            for (int i = 0; i < addArray.length(); i++) {
                                JSONObject addJsonObject = addArray.getJSONObject(i);
                                AddressBean addressBean = new AddressBean(addJsonObject.get("id").toString(),
                                        addJsonObject.get("name").toString(), addJsonObject.get("phone").toString(),
                                        addJsonObject.get("province").toString(), addJsonObject.get("city").toString(),
                                        addJsonObject.get("region").toString(), addJsonObject.get("street").toString(),
                                        addJsonObject.get("isDefault").toString());
                                listAddress.add(addressBean);
                            }

                            // 获取数据成功后的处理
                            message.obj = resultMessage;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mHandler.sendMessage(message);
                        } else {
                            // 获取数据失败后的处理
                            message.obj = resultMessage;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    // 获取数据异常后的处理
                    message.obj = resultMessage;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    public void delAddress(final String addId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = null;
                Message message = new Message();
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("addId", addId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_DEL_ADDRESS, params);
                    Log.d(TAG, "===AddressMessageActivity#delAddress() json = " + json);
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        msg = jsonObject.get("msg").toString();
                    }
                    if (!result) {
                        //删除失败后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //删除成功后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_SUCCESS_CHANGE;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //删除异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
   /* public void changeFirst(final String addId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = null;
                Message message = new Message();
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("addId", addId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_CHANGE_ADDRESS, params);
                    Log.d(TAG, "===AddressMessageActivity#changeDefault() json = " + json);
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        msg = jsonObject.get("msg").toString();
                    }
                    if (!result) {
                        //修改失败后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //修改成功后的处理
                        message.obj = msg;
                        message.what = CODE_EXECUTE_SUCCESS_CHANGE;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //修改异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }*/

    public void changeDefault(final String addId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = null;
                Message message = new Message();
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("addId", addId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_DEFAULT_ADDRESS, params);
                    Log.d(TAG, "===AddressMessageActivity#changeDefault() json = " + json);
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        msg = jsonObject.get("msg").toString();
                    }
                    if (!result) {
                        //修改失败后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //修改成功后的处理
                        message.obj = msg;
                        message.what = CODE_EXECUTE_SUCCESS_CHANGE;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //修改异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getData();
    }
}
