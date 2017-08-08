package com.fujisoft.campaign;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.fujisoft.campaign.service.LocationService;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 定位画面
 */
public class LocationActivity extends BaseActivity {
    protected LocationService locationService;

    private String userId = null;

    private String TAG = "campaign";

    String currentProvince;
    String currentCity;
    String currentDistrict;

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {

        Log.d(TAG, "=== LocationActivity# onStop() ===");
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {

        Log.d(TAG, "=== LocationActivity# onStart() ===");
        super.onStart();
        // -----------location config ------------
        locationService = ((CampaignApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

        if (Utils.isConnect(this)) {

            Log.d(TAG, "=== LocationActivity# locationService() ===");
            locationService.start();
        }
    }

    private Handler mLocationHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
                default:
                    break;
            }
        }
    };

    public void SendLocation(final String userId) {


        Log.d(TAG, "=== LocationActivity# SendLocation() ===");

        Log.d(TAG, "=== LocationActivity# SendLocation() ===userId"+userId);

        Log.d(TAG, "=== LocationActivity# SendLocation() ===currentProvince"+currentProvince);

        Log.d(TAG, "=== LocationActivity# SendLocation() ===currentCity"+currentCity);

        Log.d(TAG, "=== LocationActivity# SendLocation() ===currentDistrict"+currentDistrict);
        if (((userId != null) && (!"null".equals(userId)) && (!"".equals(userId)))) {
//            if (((currentProvince != null) && (!"null".equals(currentProvince)) && (!"".equals(currentProvince)))) {
//                if (((currentCity != null) && (!"null".equals(currentCity)) && (!"".equals(currentCity)))) {
//                    if (((currentDistrict != null) && (!"null".equals(currentDistrict)) && (!"".equals(currentDistrict)))) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<NameValuePair> params = new ArrayList<>();

                                    params.add(new BasicNameValuePair("id", userId));
                                    params.add(new BasicNameValuePair("province", currentProvince));
                                    params.add(new BasicNameValuePair("city", currentCity));
                                    params.add(new BasicNameValuePair("area", currentDistrict));

                                    // 获取响应的结果信息
                                    String json = getPostData(Constants.URL_UPDATE_USER_LOCATION, params);

                                    Log.d(TAG, "=== LocationActivity#SendLocation() json = " + json);

                                    //JSON的解析过程
                                    if (json != null) {
                                        JSONObject jsonObject = new JSONObject(json);
                                        boolean result = (boolean) jsonObject.get("success");
                                        if (result) {
                                            //成功后的处理
                                            mLocationHandler.sendEmptyMessage(Constants.CODE_EXECUTE_SUCCESS);
                                        } else {
                                            mLocationHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    //异常后的处理
                                    mLocationHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                                }
                            }
                        }).start();
                    }
//                }
//            }
//        }

    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d(TAG, "=== LocationActivity#onReceiveLocation()");
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                currentProvince = location.getProvince();
                currentCity = location.getCity();
                currentDistrict = location.getDistrict();

                Log.d(TAG, "=== LocationActivity#onReceiveLocation()-SendLocation");
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
                userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
                SendLocation(userId);
                locationService.stop();
            }
        }

        public void onConnectHotSpotMessage(String s, int i) {
        }
    };
}