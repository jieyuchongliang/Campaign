package com.fujisoft.campaign;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.fragment.CompanyRegisterFragment;
import com.fujisoft.campaign.fragment.PagerSlidingTabStrip;
import com.fujisoft.campaign.fragment.PersonalRegisterFragment;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.MultipartEntityEx;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员注册页面
 * 包括个人注册、企业注册
 */
public class RegistrationActivity extends BaseActivity implements PersonalRegisterFragment.onGetUserDataListener, CompanyRegisterFragment.onGetCompanyDataListener {
    private String TAG = "campaign";
    /**
     * 个人注册的Fragment
     */
    private PersonalRegisterFragment personalFragment;

    /**
     * 企业注册的Fragment
     */
    private CompanyRegisterFragment enterpriseFragment;

    /**
     * PagerSlidingTabStrip
     */
    private PagerSlidingTabStrip tabs;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    public static RegistrationActivity staticRegistrationActivity;

    /**
     * 用户ID
     */
    private String userId = null;
    /**
     * 用户类型
     */
    private String userType = null;
    /**
     * 企业需要交付的开户费及年费
     */
    private String totalFee = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                // 个人注册结果
                case Constants.CODE_REGISTER_PERSON_ERROR:
                    Toast.makeText(RegistrationActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CODE_REGISTER_PERSON_EXCEPTION:
                    Toast.makeText(RegistrationActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    setResult(Constants.CODE_EXECUTE_EXCEPTION);
                    finish();
                    break;
                case Constants.CODE_REGISTER_PERSON_SUCCESS:
                    Toast.makeText(RegistrationActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, FavoriteActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_ID, userId);
                    intent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                    startActivity(intent);
                    break;
                // 企业注册结果
                case Constants.CODE_REGISTER_COMPANY_ERROR:
                    Toast.makeText(RegistrationActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
                case Constants.CODE_REGISTER_COMPANY_EXCEPTION:
                    Toast.makeText(RegistrationActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case Constants.CODE_REGISTER_COMPANY_SUCCESS:
                    Toast.makeText(RegistrationActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();

                    // 企业注册成功后，数据不能保存到SharedPreferences，因为还需要审核
                    if (null != RegistrationActivity.staticRegistrationActivity)
                        RegistrationActivity.staticRegistrationActivity.finish();
                    if (null != LoginActivity.staticLoginActivity)
                        LoginActivity.staticLoginActivity.finish();

                    Intent companyIntent = new Intent(RegistrationActivity.this, CompanyRegisterSuccessActivity.class);
                    companyIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                    companyIntent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                    companyIntent.putExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT, totalFee);
                    startActivity(companyIntent);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        staticRegistrationActivity = this;
        setOverflowShowingAlways();
        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();
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
        tool_bar_center_text_view.setText(getString(R.string.member_registration));
        TextView tool_bar_right_bottom_button = (TextView) findViewById(R.id.tool_bar_right_bottom_button);
//        tool_bar_right_bottom_button.setVisibility(View.VISIBLE);
        tool_bar_right_bottom_button.setText(getString(R.string.login));
        tool_bar_right_bottom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AddAddressIntent = new Intent();
                AddAddressIntent.setClass(RegistrationActivity.this, LoginActivity.class);
                startActivity(AddAddressIntent);
            }
        });
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#E87300"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#E87300"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {getString(R.string.person_registration), getString(R.string.company_registration)};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (personalFragment == null) {
                        personalFragment = new PersonalRegisterFragment();
                    }
                    return personalFragment;
                case 1:
                    if (enterpriseFragment == null) {
                        enterpriseFragment = new CompanyRegisterFragment();
                    }
                    return enterpriseFragment;
                default:
                    return null;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击提交按钮后，获取个人注册输入的数据
     *
     * @param userPhone
     * @param checkNumber
     * @param userPassword
     * @param userSex
     */
    @Override
    public void onSetUserData(String userPhone, String checkNumber, String userPassword, int userSex,String clientId) {
        registerPerson(userPhone, checkNumber, userPassword, userSex,clientId);
    }

    @Override
    public void onSetCompanyData(String companyName, String adminName, String adminPwd, File pic,String clientId) {
        registerCompany(companyName, adminName, adminPwd, pic,clientId);
    }

    /**
     * 个人注册用方法
     *
     * @param userPhone    ：11位手机号码
     * @param checkNumber  ： 手机验证码
     * @param userPassword ： 6～32位密码
     * @param userSex      ： 个人性别
     */
    private void registerPerson(final String userPhone, final String checkNumber, final String userPassword, final int userSex,final String clientId) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("phone", userPhone));
                    params.add(new BasicNameValuePair("code", checkNumber));
                    params.add(new BasicNameValuePair("password", userPassword));
                    params.add(new BasicNameValuePair("sex", String.valueOf(userSex)));

                    params.add(new BasicNameValuePair("clientId", clientId));
                    //获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_REGISTER, params);
                    Log.d(TAG, "===RegistrationActivity #registerPerson()  json=" + json);
                    //JSON的解析过程
                    boolean result = false;
                    String resultMsg = null;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        resultMsg = jsonObject.get("msg").toString();
                        if (result) {
                            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
                            userId = dataJsonObject.get("userId").toString();
                            userType = dataJsonObject.get("userType").toString();
                        }
                    }
                    if (!result) {
                        //注册失败后的处理
                        Message message = new Message();
                        message.obj = resultMsg;
                        message.what = Constants.CODE_REGISTER_PERSON_ERROR;
                        mHandler.sendMessage(message);
                    } else {
                        //注册成功后的处理
                        Message message = new Message();
                        message.obj = resultMsg;
                        message.what = Constants.CODE_REGISTER_PERSON_SUCCESS;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //登录异常后的处理
                    Message message = new Message();
                    message.obj = e;
                    message.what = Constants.CODE_REGISTER_PERSON_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 企业注册用方法
     *
     * @param companyName ： 企业名称
     * @param adminName   ： 用户名
     * @param adminPwd    ： 密码
     * @param pic         ： 营业执照
     */
    private void registerCompany(final String companyName, final String adminName, final String adminPwd, final File pic,final String clientId) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //Post发送请求数据
                    MultipartEntityEx multiPartEntity = new MultipartEntityEx();
                    multiPartEntity.addPart("name", companyName);
                    multiPartEntity.addPart("adminName", adminName);
                    multiPartEntity.addPart("adminPwd", adminPwd);
                    multiPartEntity.addPart("file", pic);

                    multiPartEntity.addPart("clientId", clientId);

                    //获取响应的结果
                    String json = getPostData(Constants.URL_ENTERPRISE_REGISTER, multiPartEntity);
                    Log.d(TAG, "===RegistrationActivity #registerCompany() ====== json = " + json);
                    //JSON的解析
                    boolean result = false;
                    String resultMsg = null;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        resultMsg = jsonObject.get("msg").toString();
                        if (result) {
                            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
                            Log.d(TAG, "===RegistrationActivity #registerCompany() dataJsonObject = " + dataJsonObject);
                            userId = dataJsonObject.get("userId").toString();
                            userType = dataJsonObject.get("userType").toString();
                            totalFee = dataJsonObject.get("total_fee").toString();
                        }
                    }

                    if (!result) {
                        //企业注册失败后的处理
                        Message message = new Message();
                        message.obj = resultMsg;
                        message.what = Constants.CODE_REGISTER_COMPANY_ERROR;
                        mHandler.sendMessage(message);
                    } else {
                        //企业注册成功后的处理
                        Message message = new Message();
                        message.obj = resultMsg;
                        message.what = Constants.CODE_REGISTER_COMPANY_SUCCESS;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //企业注册异常后的处理
                    Message message = new Message();
                    message.obj = e;
                    message.what = Constants.CODE_REGISTER_COMPANY_EXCEPTION;
                }
            }
        }).start();
    }
}