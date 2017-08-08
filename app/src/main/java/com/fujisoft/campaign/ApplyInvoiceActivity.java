package com.fujisoft.campaign;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
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
 * 申请发票页面
 */
public class ApplyInvoiceActivity extends BaseActivity implements OnAddressChangeListener {
    private String TAG = "campaign";
    private String userId = null;
    EditText edt_invoice_receiver_name, edt_invoice_receiver_detail_address, edt_invoice_receiver_phone;
    Button applyInvoiceButton;
    String enterId = null;
    String money = null;
    String invoiceCompanyName = null;              // 发票单位名称
    String invoiceMoney = null;
    String invoiceReceiverName = "";             // 收件人姓名
    String invoiceReceiverDetailAddress = "";  // 地址
    String invoiceReceiverPhone = "";           // 电话
    String invoiceReceiverCity = "";
    String invoiceReceiverDistrict = "";
    String invoiceReceiverProvince = "";
    private ChooseAddressWheel chooseAddressWheel = null;
    TextView tv_invoice_company_name, tv_invoice_company_money, invoice_province_wheel, invoice_city_wheel, invoice_district_wheel;
    LinearLayout layoutClick;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
//                    Intent invoice = new Intent();
//                    invoice.setClass(ApplyInvoiceActivity.this,FinanceActivity.class);
//                    startActivity(invoice);
                    finish();
                    Toast.makeText(ApplyInvoiceActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
//                    showErrorDialog(msg.obj.toString());
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    tv_invoice_company_name.setText(invoiceCompanyName);
                    tv_invoice_company_money.setText(money);
                    break;
                case Constants.CODE_APPLY_INVOICE_SUCCESS:
                    Toast.makeText(ApplyInvoiceActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                   // showErrorDialog(msg.obj.toString());
//                    Intent inv = new Intent();
//                    inv.setClass(ApplyInvoiceActivity.this,FinanceActivity.class);
//                    startActivity(inv);
                    finish();
                    Toast.makeText(ApplyInvoiceActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };

/*    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }*/

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
//            //监控/拦截菜单键
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_invoice);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG, "=== ApplyInvoiceActivity#onCreate 从SharedPreferences中取到的userId = " + userId);

        initView();
        initWheel();
        initData();
        if (com.fujisoft.campaign.utils.Utils.isConnect(this)) {
            showProgressDialog();
            // 从服务器端获取数据
            upload();
        } else {
            com.fujisoft.campaign.utils.Utils.showToast(this, R.string.netWrong);
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
        tool_bar_center_text_view.setText(R.string.finance_apply_invoice);
    }

    private void initView() {
        layoutClick = (LinearLayout) findViewById(R.id.layoutClick);
        // 发票单位名称：名称要固定显示，不可入力
        tv_invoice_company_name = (TextView) findViewById(R.id.tv_invoice_company_name);
        tv_invoice_company_money = (TextView) findViewById(R.id.tv_invoice_company_money);

        // 收件人姓名
        edt_invoice_receiver_name = (EditText) findViewById(R.id.edt_invoice_receiver_name);

        // 地址 - （省市县）
        invoice_province_wheel = (TextView) findViewById(R.id.invoice_province_wheel);
        invoice_city_wheel = (TextView) findViewById(R.id.invoice_city_wheel);
        invoice_district_wheel = (TextView) findViewById(R.id.invoice_district_wheel);

        // 地址 - （详细地址）
        edt_invoice_receiver_detail_address = (EditText) findViewById(R.id.edt_invoice_receiver_detail_address);

        // 电话
        edt_invoice_receiver_phone = (EditText) findViewById(R.id.edt_invoice_receiver_phone);
        applyInvoiceButton = (Button) findViewById(R.id.btn_apply_invoice);
        applyInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 提交申请
                if (checkEmpty()) {
                    applyInvoice();
                }
            }
        });
    }

    private boolean checkEmpty() {
        invoiceCompanyName = tv_invoice_company_name.getText().toString();
        invoiceMoney = tv_invoice_company_money.getText().toString();
        invoiceReceiverName = edt_invoice_receiver_name.getText().toString();
        invoiceReceiverDetailAddress = edt_invoice_receiver_detail_address.getText().toString();
        invoiceReceiverPhone = edt_invoice_receiver_phone.getText().toString();

        invoiceReceiverProvince = invoice_province_wheel.getText().toString().trim();
        invoiceReceiverCity = invoice_city_wheel.getText().toString().trim();
        invoiceReceiverDistrict = invoice_district_wheel.getText().toString().trim();
        Log.d(TAG, "ApplyInvoiceActivity#checkEmpty() invoiceReceiverProvince: " + invoiceReceiverProvince + "/" + invoiceReceiverCity + "/" + invoiceReceiverDistrict);

        if (invoiceReceiverName == null || "".equals(invoiceReceiverName)) {
            showConfirmDialog("收件人姓名不能为空，请重新输入！");
        } else if (invoiceReceiverProvince == null || "".equals(invoiceReceiverProvince)) {
            showConfirmDialog("省份不能为空，请重新输入！");
        } else if (invoiceReceiverCity == null || "".equals(invoiceReceiverCity)) {
            showConfirmDialog("城市不能为空，请重新输入！");
        } else if (invoiceReceiverDistrict == null || "".equals(invoiceReceiverDistrict)) {
            showConfirmDialog("区域不能为空，请重新输入！");
        } else if (invoiceReceiverDetailAddress == null || "".equals(invoiceReceiverDetailAddress)) {
            showConfirmDialog("详细地址不能为空，请重新输入！");
        } else if (invoiceReceiverPhone == null || "".equals(invoiceReceiverPhone)) {
            showConfirmDialog("电话不能为空，请重新输入！");
        }else if (invoiceCompanyName == null || "".equals(invoiceCompanyName)) {
            showConfirmDialog("无可开具的发票！");
        } else if (invoiceMoney == null || "".equals(invoiceMoney)) {
            showConfirmDialog("无可开具的发票！");
        } else {
            return true;
        }
        return false;
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
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

    @OnClick(R.id.invoice_layout)
    public void layoutClick(View view) {
        Utils.hideKeyBoard(this);
        chooseAddressWheel.show(view);
    }

    @OnClick(R.id.invoice_province_wheel)
    public void proClick(View view) {
        Utils.hideKeyBoard(this);
        chooseAddressWheel.show(view);
    }

    @OnClick(R.id.invoice_city_wheel)
    public void cityClick(View view) {
        Utils.hideKeyBoard(this);
        chooseAddressWheel.show(view);
    }

    @OnClick(R.id.invoice_district_wheel)
    public void areaClick(View view) {
        Utils.hideKeyBoard(this);
        chooseAddressWheel.show(view);
    }

    @Override
    public void onAddressChange(String province, String city, String district) {
        invoice_province_wheel.setText(province);
        invoice_city_wheel.setText(city);
        invoice_district_wheel.setText(district);

    }

    /**
     * 加载申请发票页面
     */
    private void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                ;
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_FINANCE_APPLY_INVOICE, params);
                    Log.d(TAG, "ApplyInvoiceActivity#upload() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        Log.d(TAG, "=== ApplyInvoiceActivity#upload() msg = " + msg);
                        if (result) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            money = dataObject.get("money").toString();
                            JSONObject data = dataObject.getJSONObject("enterpriseInfo");
                            enterId = data.get("id").toString();
                            invoiceCompanyName = data.get("name").toString();
                            // 加载页面成功后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mHandler.sendMessage(message);
                        } else {
                            // 加载页面失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    //加载页面发生异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 点击“申请”按钮后，提交申请发票所需要的数据
     */
    private void applyInvoice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = null;
                Message message = new Message();
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("price", money));
                    params.add(new BasicNameValuePair("enterId", enterId));
                    params.add(new BasicNameValuePair("enterpriseName", invoiceCompanyName));
                    params.add(new BasicNameValuePair("userName", invoiceReceiverName));
                    params.add(new BasicNameValuePair("phone", invoiceReceiverPhone));
                    params.add(new BasicNameValuePair("province", invoiceReceiverProvince));
                    params.add(new BasicNameValuePair("city", invoiceReceiverCity));
                    params.add(new BasicNameValuePair("district", invoiceReceiverDistrict));
                    params.add(new BasicNameValuePair("address", invoiceReceiverDetailAddress));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_FINANCE_RECHARGE, params);
                    Log.d(TAG, "=== ApplyInvoiceActivity#applyInvoice() json2 = " + json);
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        msg = jsonObject.get("msg").toString();
                        Log.d(TAG, "=== ApplyInvoiceActivity#applyInvoice() msg = " + msg);
                    }
                    if (!result) {
                        //提交数据失败后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //提交数据成功后的处理
                        message.obj = msg;
                        message.what = Constants.CODE_APPLY_INVOICE_SUCCESS;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //提交数据发生异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
}