package com.fujisoft.campaign;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.utils.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import addresswheel_master.model.AddressDtailsEntity;
import addresswheel_master.model.AddressModel;
import addresswheel_master.utils.JsonUtil;
import addresswheel_master.utils.Utils;
import addresswheel_master.view.ChooseAddressWheel;
import addresswheel_master.view.listener.OnAddressChangeListener;
import butterknife.OnClick;

/**
 * 地址管理之添加地址页面
 */
public class AddAddressActivity extends BaseActivity implements OnAddressChangeListener {
    private String TAG = "campaign";
    EditText address_name, address_phone, address_street;
    TextView address_province, address_city, address_area;
    Button address_add;

    String strAddName;
    String strAddPhone;
    String strAddStreet;
    String strAddProvince;
    String strAddCity;
    String strAddArea;

    private String userId = null;
    private ChooseAddressWheel chooseAddressWheel = null;
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
                    finish();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

        address_name = (EditText) findViewById(R.id.address_name);
        address_phone = (EditText) findViewById(R.id.address_phone);
        address_province = (TextView) findViewById(R.id.address_province);
        address_city = (TextView) findViewById(R.id.address_city);
        address_area = (TextView) findViewById(R.id.address_area);
        address_street = (EditText) findViewById(R.id.address_street);
        address_add = (Button) findViewById(R.id.address_add);
        address_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmpty()) {
                    addAddress();
                }
            }
        });
        initWheel();
        initData();
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
        tool_bar_center_text_view.setText(getString(R.string.address_manager));
    }

    private void initWheel() {
        chooseAddressWheel = new ChooseAddressWheel(this);
        chooseAddressWheel.setOnAddressChangeListener(this);
    }

    private void initData() {
        String address = Utils.readAssert(this, "address.txt");
        AddressModel model = JsonUtil.parseJson(address, AddressModel.class);
        if (model != null) {
            AddressDtailsEntity data = model.Result;
            if (data == null) return;

            if (data.ProvinceItems != null && data.ProvinceItems.Province != null) {
                chooseAddressWheel.setProvince(data.ProvinceItems.Province);
                chooseAddressWheel.defaultValue(data.Province, data.City, data.Area);
            }
        }
    }

    @OnClick(R.id.layoutClick)
    public void layoutClick(View view) {
        Utils.hideKeyBoard(this);
        chooseAddressWheel.show(view);
    }

    @OnClick(R.id.address_city)
    public void cityClick(View view) {
        Utils.hideKeyBoard(this);
        chooseAddressWheel.show(view);
    }

    @OnClick(R.id.address_area)
    public void areaClick(View view) {
        Utils.hideKeyBoard(this);
        chooseAddressWheel.show(view);
    }

    @OnClick(R.id.address_province)
    public void proClick(View view) {
        Utils.hideKeyBoard(this);
        chooseAddressWheel.show(view);
    }

    private boolean checkEmpty() {
        strAddName = address_name.getText().toString();
        strAddPhone = address_phone.getText().toString();
        strAddStreet = address_street.getText().toString();
        strAddProvince = address_province.getText().toString();
        strAddCity = address_city.getText().toString();
        strAddArea = address_area.getText().toString();
        if (strAddName == null || "".equals(strAddName)) {
            showConfirmDialog("姓名不能为空，请重新输入！");
        } else if (strAddPhone == null || "".equals(strAddPhone)) {
            showConfirmDialog("电话不能为空，请重新输入！");
        } else if (strAddProvince == null || "".equals(strAddProvince)) {
            showConfirmDialog("省份不能为空，请重新输入！");
        } else if (strAddCity == null || "".equals(strAddCity)) {
            showConfirmDialog("城市不能为空，请重新输入！");
        } else if (strAddArea == null || "".equals(strAddArea)) {
            showConfirmDialog("区域不能为空，请重新输入！");
        } else if (strAddStreet == null || "".equals(strAddStreet)) {
            showConfirmDialog("街道不能为空，请重新输入！");
        } else {
            showProgressDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onAddressChange(String province, String city, String district) {
        address_province.setText(province);
        address_city.setText(city);
        address_area.setText(district);

    }

    private void addAddress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = null;
                Message message = new Message();
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("name", strAddName));
                    params.add(new BasicNameValuePair("phone", strAddPhone));
                    params.add(new BasicNameValuePair("province", strAddProvince));
                    params.add(new BasicNameValuePair("city", strAddCity));
                    params.add(new BasicNameValuePair("region", strAddArea));
                    params.add(new BasicNameValuePair("street", strAddStreet));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_ADD_ADDRESS, params);
                    Log.d(TAG, "=== AddAddressActivity#addAddress() json = " + json);
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        msg = jsonObject.get("msg").toString();
                    }
                    if (!result) {
                        //添加失败后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //添加成功后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_SUCCESS;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //添加异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
}