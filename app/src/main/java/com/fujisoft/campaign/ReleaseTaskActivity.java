package com.fujisoft.campaign;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.MultipartEntityEx;
import com.fujisoft.campaign.utils.PhotoUtils;
import com.fujisoft.campaign.view.MatrixImageView;
import com.fujisoft.campaign.wheelview.DateUtils;
import com.fujisoft.campaign.wheelview.JudgeDate;
import com.fujisoft.campaign.wheelview.ScreenInfo;
import com.fujisoft.campaign.wheelview.WheelMain;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import addresswheel_master.model.AddressDtailsEntity;
import addresswheel_master.model.AddressModel;
import addresswheel_master.utils.JsonUtil;
import addresswheel_master.utils.Utils;
import addresswheel_master.view.ChooseAddressWheel;
import addresswheel_master.view.listener.OnAddressChangeListener;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布任务页面
 */
public class ReleaseTaskActivity extends BaseActivity implements View.OnClickListener, OnAddressChangeListener {
    private String TAG = "campaign";
    private String userId = null;
    private TextView tv_start_time;
    private TextView tv_end_time;
    private WheelMain wheelMainDate;
    private String beginTime;
    private TextView release_address_province;
    private TextView release_address_city;
    private TextView release_address_area;
    private EditText share_title;
    private Button btn_image;
    private EditText release_num;
    private EditText release_min;
    private EditText release_share;
    private ImageView img_register;
    private ChooseAddressWheel chooseAddressWheel = null;
    String maxShareTime;
    String minPeo;
    String maxPeo;
    String openingFee = "";
    private String userType = "";                      // 从服务器端获取企业的类型值
    String task_share_title, task_start_time, task_end_time, task_address_province, task_address_city, task_address_area, task_num, task_min, task_share, task_total_gold, task_gold;
    int task_flag_staff = -1;
    int task_flag_man = -1;
    int task_flag_woman = -1;
    String task_flag_sex = null;
    private AlertDialog licenseDialog;
    private String basePath = Environment.getExternalStorageDirectory().getPath() + "/capture/";
    private String compressedLicenseFilePath = basePath + "compressedLicense.jpeg";
    File fileTest = new File(basePath);
    private File picFile = null;
    private String pictureFilePath;
    private Uri uri;
    private PopupWindow popupWindow;
    private TextView text_nm, text_min;
    private LinearLayout Linear_min, Linear_num;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            String toastMsg = null;
            switch (msg.what) {
                case Constants.CODE_EXECUTE_SUCCESS:
                    //toastMsg = msg.obj.toString();
                    break;
                case Constants.CODE_RELEASE_TASK_SUCCESS:
                    toastMsg = msg.obj.toString();
                    //finish();
                    Intent examineIntent = new Intent();
                    examineIntent.setClass(ReleaseTaskActivity.this, ExamineActivity.class);
                    startActivity(examineIntent);
                    finish();
                    break;
                case Constants.CODE_USER_UNACTIVE:
                    // 用户是未激活状态(即未交年费)时，进入交费页面
                    Intent intent = new Intent();
                    intent.putExtra(Constants.EXTRA_COMPANY_REGISTER_AMOUNT, openingFee);
                    intent.putExtra(Constants.EXTRA_USER_ID, userId);
                    intent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                    intent.putExtra(Constants.EXTRA_AMOUNT, openingFee);
                    intent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_PAY_TYPE);
                    intent.setClass(ReleaseTaskActivity.this, CompanyPayOpeningFeeActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    toastMsg = msg.obj.toString();
                    break;
                case Constants.CODE_EXECUTE_FAILURE:
                    toastMsg = msg.obj.toString();
                    break;
                default:
                    break;
            }
            if (toastMsg != null) {
                Toast.makeText(ReleaseTaskActivity.this, toastMsg, Toast.LENGTH_SHORT).show();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_task);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        Log.d(TAG, "=== ReleaseTaskActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);
        userType = getIntent().getStringExtra(Constants.EXTRA_USER_TYPE);
        if (com.fujisoft.campaign.utils.Utils.isConnect(this)) {
            showProgressDialog();
            // 从服务器端获取数据
            upload();
        } else {
            com.fujisoft.campaign.utils.Utils.showToast(this, R.string.netWrong);
        }

        if (!fileTest.exists()) {
            fileTest.mkdirs();
        }
        chooseAddressWheel = new ChooseAddressWheel(this);
        chooseAddressWheel.setOnAddressChangeListener(this);

        initData();
        initView();
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
                finish();
            }
        });

        TextView tool_bar_center_text_view = (TextView) findViewById(R.id.tool_bar_center_text_view);
        tool_bar_center_text_view.setVisibility(View.VISIBLE);
        tool_bar_center_text_view.setText(getString(R.string.release_task));

    }

    private void initData() {
        String address = Utils.readAssert(this, "address.txt");
        AddressModel model = JsonUtil.parseJson(address, AddressModel.class);
        if (model != null) {
            AddressDtailsEntity data = model.Result;
            if (data == null) return;
            if (data.ProvinceItems != null && data.ProvinceItems.Province != null) {
                AddressDtailsEntity.ProvinceEntity province = new AddressDtailsEntity.ProvinceEntity();
                List<AddressDtailsEntity.ProvinceEntity.CityEntity> cityList = new ArrayList<>();
                AddressDtailsEntity.ProvinceEntity.CityEntity city = new AddressDtailsEntity.ProvinceEntity.CityEntity();
                List<AddressDtailsEntity.ProvinceEntity.AreaEntity> areaList = new ArrayList<>();
                AddressDtailsEntity.ProvinceEntity.AreaEntity area = new AddressDtailsEntity.ProvinceEntity.AreaEntity();
                province.City = cityList;
                province.City.add(city);
                province.Name = "不限";
                city.Area = areaList;
                city.Area.add(area);
                city.Name = "不限";
                area.Name = "不限";
                data.ProvinceItems.Province.add(0,province);
                chooseAddressWheel.setProvince(data.ProvinceItems.Province);
                chooseAddressWheel.defaultValue(data.Province, data.City, data.Area);
            }
        }
    }

    @OnClick(R.id.release_address_province)
    public void proClick(View view) {
        chooseAddressWheel.show(view);
    }

    @OnClick(R.id.release_address_city)
    public void cityClick(View view) {
        chooseAddressWheel.show(view);
    }

    @OnClick(R.id.release_address_area)
    public void areaClick(View view) {
        chooseAddressWheel.show(view);
    }

    @Override
    public void onAddressChange(String province, String city, String district) {
        release_address_province.setText(province);
        release_address_city.setText(city);
        release_address_area.setText(district);
    }

    private void initView() {
        text_nm = (TextView) findViewById(R.id.text_nm);
        text_min = (TextView) findViewById(R.id.text_min);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_start_time.setOnClickListener(this);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_end_time.setOnClickListener(this);
        Linear_min = (LinearLayout) findViewById(R.id.Linear_min);
        Linear_num = (LinearLayout) findViewById(R.id.Linear_num);
        release_address_province = (TextView) findViewById(R.id.release_address_province);
        release_address_city = (TextView) findViewById(R.id.release_address_city);
        release_address_area = (TextView) findViewById(R.id.release_address_area);

        share_title = (EditText) findViewById(R.id.share_title);
        share_title.addTextChangedListener(mTextWatcher);

        btn_image = (Button) findViewById(R.id.btn_image);
        btn_image.setOnClickListener(this);

        img_register = (ImageView) findViewById(R.id.img_register);
        img_register.setOnClickListener(this);
        RadioGroup release_radioGroup = (RadioGroup) findViewById(R.id.release_radioGroup);
        RadioButton release_yes = (RadioButton) findViewById(R.id.release_yes);
        RadioButton release_no = (RadioButton) findViewById(R.id.release_no);
        CheckBox release_man = (CheckBox) findViewById(R.id.release_man);
        CheckBox release_woman = (CheckBox) findViewById(R.id.release_woman);
        final TextView release_gold_num = (TextView) findViewById(R.id.release_gold_num);
        release_num = (EditText) findViewById(R.id.release_num);
        release_min = (EditText) findViewById(R.id.release_min);
        release_min.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    release_min.setHint(null);
                } else {
                    release_min.setHint("最少" + 0 + "次，最多" + maxShareTime + "次");
                }
            }
        });
        release_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    release_num.setHint(null);
                } else {
                    release_num.setHint("最低" + minPeo + "人，最高" + maxPeo + "人");
                }
            }
        });
        release_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s && !"".equals(s.toString()) && task_flag_staff != 1) {
                    task_num = release_num.getText().toString();
                    task_total_gold = String.valueOf((Integer.parseInt(task_gold)) * Integer.parseInt(task_num));
                    if (Integer.valueOf(task_total_gold) > 10000000) {
                        Toast.makeText(ReleaseTaskActivity.this, "输入数值太大了！", Toast.LENGTH_SHORT).show();
                    }
                    release_gold_num.setText(task_total_gold);
                } else {
                    task_num = release_num.getText().toString();
                    release_gold_num.setText("0");
                }
            }
        });

       /* release_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    task_num = release_num.getText().toString();
                    task_total_gold = String.valueOf((Integer.parseInt(task_gold)) * Integer.parseInt(task_num));
                    if (Integer.valueOf(task_total_gold) > 10000000) {
                        Toast.makeText(ReleaseTaskActivity.this, "输入数值太大了！", Toast.LENGTH_SHORT).show();
                    }
                    release_gold_num.setText(task_total_gold);
                }
            }
        });*/
        release_share = (EditText) findViewById(R.id.release_share);
        Button btn_preview = (Button) findViewById(R.id.btn_pre);
        btn_preview.setOnClickListener(this);
        Button btn_release = (Button) findViewById(R.id.btn_rel);
        btn_release.setOnClickListener(this);
        Button btn_pre = (Button) findViewById(R.id.btn_pre);
        btn_pre.setOnClickListener(this);
        //绑定一个匿名监听器
        release_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.release_yes) {
                    //本企业员工
                    task_flag_staff = 1;
                    task_num = release_num.getText().toString();
/*                    release_num.setVisibility(View.GONE);
                    text_nm.setVisibility(View.GONE);
                    text_min.setVisibility(View.VISIBLE);
                    release_min.setVisibility(View.VISIBLE);*/
                    Linear_min.setVisibility(View.VISIBLE);
                    Linear_num.setVisibility(View.GONE);
                    release_min.setText("");
                    release_gold_num.setText("0");

                    //Toast.makeText(ReleaseTaskActivity.this, release_yes.getText(), Toast.LENGTH_SHORT).show();
                } else if (i == R.id.release_no) {
                    //全员
                    task_flag_staff = 0;
/*                    release_num.setVisibility(View.VISIBLE);
                    text_nm.setVisibility(View.VISIBLE);
                    text_min.setVisibility(View.GONE);
                    release_min.setVisibility(View.GONE);*/
                    Linear_num.setVisibility(View.VISIBLE);
                    Linear_min.setVisibility(View.GONE);
                    //release_gold_num.setText(task_total_gold);
                    release_num.setText("");
                    release_gold_num.setText("0");

                } else {
                    task_flag_staff = -1;
                }
            }
        });

        release_man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    task_flag_man = 1;
                } else {
                    task_flag_man = -1;
                }
            }
        });
        release_woman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    task_flag_woman = 0;
                } else {
                    task_flag_woman = -1;
                }
            }
        });
    }

    private java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void showBottoPopupWindow() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.show_popup_window, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width * 0.8),
                ActionBar.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(this);
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        wheelMainDate.initDateTimePicker(year, month, day + 2, hours, minute);
        final String currentTime = wheelMainDate.getTime().toString();
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        //mPopupWindow.showAtLocation(tv_center, Gravity.CENTER, 0, 0);
        mPopupWindow.showAtLocation(tv_start_time, Gravity.CENTER, 0, 0);
        //mPopupWindow.showAtLocation(tv_end_time, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText(getString(R.string.select_start_time));
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                beginTime = wheelMainDate.getTime();
                boolean isSelectOk = isSelectOk(false);
                if (isSelectOk) {
                    try {
                        Date begin = dateFormat.parse(currentTime);
                        Date end = dateFormat.parse(beginTime);
                        // tv_house_time.setText(DateUtils.currentTimeDeatil(begin));
                        tv_start_time.setText(DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm));
                        //tv_end_time.setText(DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mPopupWindow.dismiss();
                    backgroundAlpha(1f);
                }

            }
        });
    }

    public void showBottoPopupWindowEnd() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.show_popup_window, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width * 0.8),
                ActionBar.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(this);
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelMainDate.initDateTimePicker(year, month, day + 2, hours, minute);
        final String currentTime = wheelMainDate.getTime().toString();
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        //mPopupWindow.showAtLocation(tv_center, Gravity.CENTER, 0, 0);
        //mPopupWindow.showAtLocation(tv_start_time, Gravity.CENTER, 0, 0);
        mPopupWindow.showAtLocation(tv_end_time, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText(getString(R.string.select_end_time));
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                beginTime = wheelMainDate.getTime();
                boolean isSelectOk = isSelectOk(true);
                if (isSelectOk) {
                    try {
                        Date begin = dateFormat.parse(currentTime);
                        Date end = dateFormat.parse(beginTime);
                        // tv_house_time.setText(DateUtils.currentTimeDeatil(begin));
                        //tv_start_time.setText(DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm));
                        tv_end_time.setText(DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mPopupWindow.dismiss();
                    backgroundAlpha(1f);
                }

            }
        });
    }

    /**
     * 判断选择的时间是否符合条件
     *
     * @return
     */
    private boolean isSelectOk(boolean isEndTime) {
        boolean isSelectOk;
        String startTime;
        String endTime;

        if (getTime(beginTime) < getIssueTasksTime()) {//判断当前的时间是否小于当前的时间
            isSelectOk = false;
            Toast.makeText(this, "请至少提前48小时发布任务", Toast.LENGTH_SHORT).show();
        } else {
            if (isEndTime) {
                startTime = tv_start_time.getText().toString();
                endTime = beginTime;
                if (TextUtils.isEmpty(startTime))
                    return true;
            } else {
                startTime = beginTime;
                endTime = tv_end_time.getText().toString();
                if (TextUtils.isEmpty(endTime))
                    return true;
            }
            isSelectOk = getTime(startTime) <= getTime(endTime);//判断开始的时间是否大于结束时间
            if (!isSelectOk)
                Toast.makeText(this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
        }
        return isSelectOk;
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    private long getTime(String time) {
        return DateUtils.parseDate(time, DateUtils.yyyyMMddHHmm).getTime();
    }

    /**
     * 获取当前发布任务的时间
     *
     * @return
     */
    private long getIssueTasksTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 2;
        String time = year + "-" + month + "-" + day;
        return DateUtils.parseDate(time, DateUtils.yyyyMMddHHmm).getTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_time:

                Utils.hideKeyBoard(ReleaseTaskActivity.this);

                showBottoPopupWindow();
                break;
            case R.id.tv_end_time:


                Utils.hideKeyBoard(ReleaseTaskActivity.this);

                showBottoPopupWindowEnd();
                break;
            case R.id.btn_pre:
                if (check()) {
                    preTask();
                }
                break;
            case R.id.btn_rel:
                if (checkEmpty()) {
                    releaseTask();
                }
                break;
            case R.id.btn_image:
                showLicenseChooseDialog();
                break;

            case R.id.img_register:
                // PopupWindow
                if (getImageThumbnail(picFile.getPath(), 360, 60) != null) {
                    showPopupWindow(findViewById(R.id.tool_bar_center_text_view));
                }
                break;

            case R.id.license_choose_takePhoto: // 拍照
                if (licenseDialog != null && licenseDialog.isShowing()) {
                    licenseDialog.dismiss();
                }
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                picFile = new File(basePath + "license.jpg");
                uri = Uri.fromFile(picFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(takePhotoIntent, Constants.CODE_CHOOSE_LICENSE_FROM_TAKE);
                break;

            case R.id.license_choose_choosePhoto: // 从相册中选择
                if (licenseDialog != null && licenseDialog.isShowing()) {
                    licenseDialog.dismiss();
                }
                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                choosePhotoIntent.addCategory(Intent.CATEGORY_OPENABLE);
                choosePhotoIntent.setType("image/*");
                startActivityForResult(choosePhotoIntent, Constants.CODE_CHOOSE_LICENSE_FROM_PHOTOS);
                break;
            default:
                break;
        }
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }

    private boolean check() {
        task_share_title = share_title.getText().toString().trim();
        task_share = release_share.getText().toString().trim();

        if (task_share_title == null || "".equals(task_share_title)) {
            showErrorDialog("分享标题");
        } else if (task_share == null || "".equals(task_share)) {
            showErrorDialog("分享内容");
        } else if (picFile == null || !picFile.exists()) {
            showErrorDialog("图片");
        } else {
            return true;
        }
        return false;
    }

    private boolean checkEmpty() {

        if (task_flag_man != -1 && task_flag_woman == -1) {
            task_flag_sex = String.valueOf(task_flag_man);
        }
        if (task_flag_man == -1 && task_flag_woman != -1) {
            task_flag_sex = String.valueOf(task_flag_woman);
        }
        if (task_flag_man != -1 && task_flag_woman != -1) {
            task_flag_sex = task_flag_man + "," + task_flag_woman;
        }
        task_share_title = share_title.getText().toString().trim();
        task_start_time = tv_start_time.getText().toString().trim();
        task_end_time = tv_end_time.getText().toString().trim();
        // task_gold = release_gold_num.getText().toString().trim();
        task_address_province = release_address_province.getText().toString().trim();
        task_address_city = release_address_city.getText().toString().trim();
        task_address_area = release_address_area.getText().toString().trim();
        //   task_flag_sex = share_title.getText().toString().trim();
        //task_num = release_num.getText().toString().trim();
        task_min = release_min.getText().toString();
        task_share = release_share.getText().toString().trim();

        if (task_share_title == null || "".equals(task_share_title)) {
            showErrorDialog("分享标题");
        } else if (task_start_time == null || "".equals(task_start_time)) {
            showErrorDialog("开始时间");
        } else if (task_end_time == null || "".equals(task_end_time)) {
            showErrorDialog("结束时间");
        } else if (task_start_time == null || "".equals(task_start_time)) {
            showErrorDialog("开始时间");
        } else if (task_address_province == null || "".equals(task_address_province)) {
            showErrorDialog("省份");
        } else if (task_address_city == null || "".equals(task_address_city)) {
            showErrorDialog("城市");
        } else if (task_address_area == null || "".equals(task_address_area)) {
            showErrorDialog("区域");
        } else if ((task_flag_staff == 0 && task_num == null) || (task_flag_staff == 0 && "".equals(task_num))) {
            showErrorDialog("投放人数");
        } else if ((task_flag_staff == 1 && task_min == null) || (task_flag_staff == 1 && "".equals(task_min))) {
            showErrorDialog("最小完成数");
        } else if (task_share == null || "".equals(task_share)) {
            showErrorDialog("分享内容");
        } else if (task_flag_staff == -1) {
            showErrorDialog("面向群体");
        } else if (task_flag_sex == null || "".equals(task_flag_sex)) {
            showErrorDialog("性别");
        } else if (picFile == null || !picFile.exists()) {
            showErrorDialog("图片");
        } else if ((task_flag_staff == 0 && Integer.parseInt(minPeo) > Integer.parseInt(task_num)) || (task_flag_staff == 0 && Integer.parseInt(maxPeo) < Integer.parseInt(task_num))) {
            Toast.makeText(getApplicationContext(), "投放人数最低不能低于" + minPeo + "最高不能高于" + maxPeo,
                    Toast.LENGTH_SHORT).show();
        } else if ((task_flag_staff == 1 && 1 > Integer.parseInt(task_min)) || (task_flag_staff == 1 && Integer.parseInt(maxShareTime) < Integer.parseInt(task_min))) {
            Toast.makeText(getApplicationContext(), "最小完成数最低不能低于" + 0 + "最高不能高于" + maxShareTime,
                    Toast.LENGTH_SHORT).show();
        } else {
            showProgressDialog();
            return true;
        }
        return false;
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(ReleaseTaskActivity.this)
                .setTitle("提示")
                .setMessage(String.format(getString(R.string.register_person_dialog_input_empty), message))
                .setPositiveButton("确定", null).create();
        dialog.show();
    }

    /**
     * 点击相机按钮后，显示“从相册中选择”和“拍照”的窗口
     */
    private void showLicenseChooseDialog() {
        LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(ReleaseTaskActivity.this).inflate(R.layout.license_choose_dialog, null);
        licenseDialog = new AlertDialog.Builder(ReleaseTaskActivity.this).create();
        licenseDialog.show();

        Window window = licenseDialog.getWindow();
        window.setContentView(dialogLayout);
        window.setGravity(Gravity.BOTTOM);

        Button chooseFromPhotos = (Button) dialogLayout.findViewById(R.id.license_choose_takePhoto);
        Button chooseFromTake = (Button) dialogLayout.findViewById(R.id.license_choose_choosePhoto);
        chooseFromPhotos.setOnClickListener(this);
        chooseFromTake.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.CODE_CHOOSE_LICENSE_FROM_TAKE:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    if (data != null) {
                        pictureFilePath = data.getData().toString();
                    } else {
                        pictureFilePath = uri.getPath();
                    }
                    break;
                case Constants.CODE_CHOOSE_LICENSE_FROM_PHOTOS:
                    if (data != null) {
                        pictureFilePath = PhotoUtils.getPath(ReleaseTaskActivity.this, data.getData());
                    } else {
                        return;
                    }
                    break;
            }
            compressedLicenseFilePath = PhotoUtils.compressImage(pictureFilePath, compressedLicenseFilePath);
            if (compressedLicenseFilePath != null) {
                picFile = new File(compressedLicenseFilePath);
                btn_image.setVisibility(View.GONE);
                img_register.setVisibility(View.VISIBLE);
                img_register.setImageBitmap(getImageThumbnail(picFile.getPath(), 360, 60));
            }
        }
    }

    Bitmap bitmap = null;

    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * PopupWindow
     *
     * @param view
     */
    private void showPopupWindow(View view) {

        View contentView = LayoutInflater.from(ReleaseTaskActivity.this).inflate(R.layout.pop_window, null);

        MatrixImageView captureBig = (MatrixImageView) contentView.findViewById(R.id.capture_big);
        // PopupWindow
        popupWindow = new PopupWindow(contentView, 870, 1330);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent));
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(compressedLicenseFilePath, options);
        captureBig.setImageBitmap(bitmap);

        popupWindow.showAsDropDown(view, 100, 0);
    }

    /**
     * 发布任务页面初始化
     */
    private void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_RELEASE_LOAD, params);
                    Log.d(TAG, "=== ReleaseTaskActivity#upload() json = " + json);
                    // JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            task_gold = dataObject.get("forward").toString();
                            minPeo = dataObject.get("minPeo").toString();
                            maxPeo = dataObject.get("maxPeo").toString();
                            maxShareTime = dataObject.get("maxShareTime").toString();
                            release_min.setHint("最低" + 0 + "人，最高" + maxShareTime + "人");
                            release_num.setHint("最低" + minPeo + "人，最高" + maxPeo + "人");
                            int accountStatus = dataObject.getInt("accountStatus"); // 0:未激活  1：已激活
                            openingFee = dataObject.getString("opening_fee").toString();


                            if (accountStatus == 1) {// 用户已是激活状态(即已交年费)
                                // 获取数据成功后的处理
                                message.obj = msg;
                                message.what = Constants.CODE_EXECUTE_SUCCESS;
                                mHandler.sendMessage(message);
                            } else {// 用户是未激活状态(即未交年费)时，进入交费页面
                                message.obj = msg;
                                message.what = Constants.CODE_USER_UNACTIVE;
                                mHandler.sendMessage(message);
                            }
                        } else {
                            // 获取数据失败后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    // 获取数据异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void preTask() {
        Intent intent = new Intent(this, PreTaskActivity.class);
        intent.putExtra("name", task_share_title);
        intent.putExtra("content", task_share);
        intent.putExtra("file", picFile.getPath());
        startActivity(intent);
    }

    /**
     * 点击“发布”按钮后，向服务器提交发布的数据
     */
    private void releaseTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // showProgressDialog();
                String msg = "Exception";
                Message message = new Message();
                try {
                    //Post发送请求数据
                    MultipartEntityEx multiPartEntity = new MultipartEntityEx();
                    multiPartEntity.addPart("id", userId);
                    multiPartEntity.addPart("startTime", task_start_time);
                    multiPartEntity.addPart("endTime", task_end_time);
                    multiPartEntity.addPart("file", picFile);
                    multiPartEntity.addPart("firstSeft", String.valueOf(task_flag_staff));
                    multiPartEntity.addPart("province", task_address_province);
                    multiPartEntity.addPart("city", task_address_city);
                    multiPartEntity.addPart("area", task_address_area);
                    multiPartEntity.addPart("sex", task_flag_sex);
                    if (task_num.equals("") || task_num == null) {
                        task_num = "0";
                    }
                    multiPartEntity.addPart("maxNum", task_num);
                    if (task_min.equals("") || task_min == null) {
                        task_min = "0";
                    }
                    multiPartEntity.addPart("minShareTime", task_min);
                    multiPartEntity.addPart("content", task_share);
                    multiPartEntity.addPart("name", task_share_title);

                    //获取响应的结果信息
                    String json = getPostData(Constants.URL_RELEASE_TASK, multiPartEntity);
                    Log.d(TAG, "=== ReleaseTaskActivity#releaseTask() json = " + json);
                    //JSON的解析
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
                        message.what = Constants.CODE_RELEASE_TASK_SUCCESS;
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

    /**
     * 限制标题长度40字节
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;
        private int maxLen = 40;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = share_title.getSelectionStart();
            editEnd = share_title.getSelectionEnd();
            // 先去掉监听器，否则会出现栈溢出
            share_title.removeTextChangedListener(mTextWatcher);
            if (!TextUtils.isEmpty(share_title.getText())) {
                while (calculateLength(s.toString()) > maxLen) {
                    s.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
            }

            share_title.setText(s);
            share_title.setSelection(editStart);

            share_title.addTextChangedListener(mTextWatcher);
        }

        private int calculateLength(String etstring) {
            char[] ch = etstring.toCharArray();

            int varlength = 0;
            for (int i = 0; i < ch.length; i++) {
                if ((ch[i] >= 0x2E80 && ch[i] <= 0xFE4F) || (ch[i] >= 0xA13F && ch[i] <= 0xAA40) || ch[i] >= 0x80) {
                    varlength = varlength + 2;
                } else {
                    varlength++;
                }
            }
            return varlength;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != bitmap) bitmap.recycle();
    }
}