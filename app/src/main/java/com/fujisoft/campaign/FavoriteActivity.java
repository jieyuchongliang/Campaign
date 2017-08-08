package com.fujisoft.campaign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 兴趣爱好页面
 * (只有在新用户注册时，才显示此页面)
 */
public class FavoriteActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "campaign";
    private Button button_edu;
    private Button button_travel;
    private Button button_finance;
    private Button button_car;
    private Button button_house;
    private Button button_family;
    private Button button_clothes;
    private Button button_food;
    private Button button_life;
    private Button button_hairdressing;
    private Button button_internet;
    private Button button_electron;
    private Button button_sport;
    private Button button_health;
    private Button button_child;
    private Button button_pet;
    private Button button_register;
    private ArrayList favoriteCodeList;
    private String interest = ""; // 向服务器提交选中的兴趣爱好的数据
    private EditText edit_num;


    int flag_edu = 0;
    int flag_travel = 0;
    int flag_finance = 0;
    int flag_car = 0;
    int flag_house = 0;
    int flag_family = 0;
    int flag_clothes = 0;
    int flag_food = 0;
    int flag_life = 0;
    int flag_hairdressing = 0;
    int flag_internet = 0;
    int flag_electron = 0;
    int flag_sport = 0;
    int flag_health = 0;
    int flag_child = 0;
    int flag_pet = 0;

    private String userId = null;
    private String userType = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    Toast.makeText(FavoriteActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    Toast.makeText(FavoriteActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    // 保存数据到SharedPreferences
                    // Context.MODE_PRIVATE : 指定该SharedPreferences数据只能被本应用程序读、写
                    SharedPreferences sp = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(Constants.EXTRA_USER_ID, userId);
                    editor.putString(Constants.EXTRA_USER_TYPE, userType);
                    editor.commit();

                    if (null != RegistrationActivity.staticRegistrationActivity)
                        RegistrationActivity.staticRegistrationActivity.finish();
                    if (null != LoginActivity.staticLoginActivity)
                        LoginActivity.staticLoginActivity.finish();

                    Toast.makeText(FavoriteActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FavoriteActivity.this, TabMainActivity.class);
                    setResult(Constants.CODE_EXECUTE_SUCCESS, intent);
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
        setContentView(R.layout.activity_favorite);

        userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        userType = getIntent().getStringExtra(Constants.EXTRA_USER_TYPE);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        LinearLayout layout_id = (LinearLayout) findViewById(R.id.layout_id);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout_id.getLayoutParams();

        //获取当前控件的布局对象
        params.height = width;//设置当前控件布局的高度
        params.weight = width;
        layout_id.setLayoutParams(params);//将设置好的布局参数应用到控件中

        favoriteCodeList = new ArrayList();

        button_edu = (Button) findViewById(R.id.button_edu);
        button_travel = (Button) findViewById(R.id.button_travel);
        button_finance = (Button) findViewById(R.id.button_finance);
        button_car = (Button) findViewById(R.id.button_car);
        button_house = (Button) findViewById(R.id.button_house);
        button_family = (Button) findViewById(R.id.button_family);
        button_clothes = (Button) findViewById(R.id.button_clothes);
        button_food = (Button) findViewById(R.id.button_food);
        button_life = (Button) findViewById(R.id.button_life);
        button_hairdressing = (Button) findViewById(R.id.button_hairdressing);
        button_internet = (Button) findViewById(R.id.button_internet);
        button_electron = (Button) findViewById(R.id.button_electron);
        button_sport = (Button) findViewById(R.id.button_sport);
        button_health = (Button) findViewById(R.id.button_health);
        button_child = (Button) findViewById(R.id.button_child);
        button_pet = (Button) findViewById(R.id.button_pet);
        button_register = (Button) findViewById(R.id.button_register);

        edit_num = (EditText) findViewById(R.id.edit_num);
        edit_num.setText("0");

        button_edu.setOnClickListener(this);
        button_travel.setOnClickListener(this);
        button_finance.setOnClickListener(this);
        button_car.setOnClickListener(this);
        button_house.setOnClickListener(this);
        button_family.setOnClickListener(this);
        button_clothes.setOnClickListener(this);
        button_food.setOnClickListener(this);
        button_life.setOnClickListener(this);
        button_hairdressing.setOnClickListener(this);
        button_internet.setOnClickListener(this);
        button_electron.setOnClickListener(this);
        button_sport.setOnClickListener(this);
        button_health.setOnClickListener(this);
        button_child.setOnClickListener(this);
        button_pet.setOnClickListener(this);
        button_register.setOnClickListener(this);
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
        tool_bar_center_text_view.setText(getString(R.string.favorite_hobby));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_edu:
                if (flag_edu == 0) {
                    button_edu.setBackgroundResource(R.drawable.favorite_checked);
                    button_edu.setTextColor(Color.WHITE);
                    flag_edu = 1;
                    favoriteCodeList.add("001");
                } else {
                    button_edu.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_edu.setTextColor(getResources().getColor(R.color.common_color));
                    flag_edu = 0;
                    favoriteCodeList.remove("001");
                }
                break;
            case R.id.button_travel:
                if (flag_travel == 0) {
                    button_travel.setBackgroundResource(R.drawable.favorite_checked);
                    button_travel.setTextColor(Color.WHITE);
                    flag_travel = 1;
                    favoriteCodeList.add("002");
                } else {
                    button_travel.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_travel.setTextColor(getResources().getColor(R.color.common_color));
                    flag_travel = 0;
                    favoriteCodeList.remove("002");
                }
                break;
            case R.id.button_finance:
                if (flag_finance == 0) {
                    button_finance.setBackgroundResource(R.drawable.favorite_checked);
                    button_finance.setTextColor(Color.WHITE);
                    flag_finance = 1;
                    favoriteCodeList.add("003");
                } else {
                    button_finance.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_finance.setTextColor(getResources().getColor(R.color.common_color));
                    flag_finance = 0;
                    favoriteCodeList.remove("003");
                }
                break;
            case R.id.button_car:
                if (flag_car == 0) {
                    button_car.setBackgroundResource(R.drawable.favorite_checked);
                    button_car.setTextColor(Color.WHITE);
                    flag_car = 1;
                    favoriteCodeList.add("004");
                } else {
                    button_car.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_car.setTextColor(getResources().getColor(R.color.common_color));
                    flag_car = 0;
                    favoriteCodeList.remove("004");
                }
                break;
            case R.id.button_house:
                if (flag_house == 0) {
                    button_house.setBackgroundResource(R.drawable.favorite_checked);
                    button_house.setTextColor(Color.WHITE);
                    flag_house = 1;
                    favoriteCodeList.add("005");
                } else {
                    button_house.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_house.setTextColor(getResources().getColor(R.color.common_color));
                    flag_house = 0;
                    favoriteCodeList.remove("005");
                }
                break;
            case R.id.button_family:
                if (flag_family == 0) {
                    button_family.setBackgroundResource(R.drawable.favorite_checked);
                    button_family.setTextColor(Color.WHITE);
                    flag_family = 1;
                    favoriteCodeList.add("006");
                } else {
                    button_family.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_family.setTextColor(getResources().getColor(R.color.common_color));
                    flag_family = 0;
                    favoriteCodeList.remove("006");
                }
                break;
            case R.id.button_clothes:
                if (flag_clothes == 0) {
                    button_clothes.setBackgroundResource(R.drawable.favorite_checked);
                    button_clothes.setTextColor(Color.WHITE);
                    flag_clothes = 1;
                    favoriteCodeList.add("007");
                } else {
                    button_clothes.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_clothes.setTextColor(getResources().getColor(R.color.common_color));
                    flag_clothes = 0;
                    favoriteCodeList.remove("007");
                }
                break;
            case R.id.button_food:
                if (flag_food == 0) {
                    button_food.setBackgroundResource(R.drawable.favorite_checked);
                    button_food.setTextColor(Color.WHITE);
                    flag_food = 1;
                    favoriteCodeList.add("008");
                } else {
                    button_food.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_food.setTextColor(getResources().getColor(R.color.common_color));
                    flag_food = 0;
                    favoriteCodeList.remove("008");
                }
                break;
            case R.id.button_life:
                if (flag_life == 0) {
                    button_life.setBackgroundResource(R.drawable.favorite_checked);
                    button_life.setTextColor(Color.WHITE);
                    flag_life = 1;
                    favoriteCodeList.add("009");
                } else {
                    button_life.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_life.setTextColor(getResources().getColor(R.color.common_color));
                    flag_life = 0;
                    favoriteCodeList.remove("009");
                }
                break;
            case R.id.button_hairdressing:
                if (flag_hairdressing == 0) {
                    button_hairdressing.setBackgroundResource(R.drawable.favorite_checked);
                    button_hairdressing.setTextColor(Color.WHITE);
                    flag_hairdressing = 1;
                    favoriteCodeList.add("010");
                } else {
                    button_hairdressing.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_hairdressing.setTextColor(getResources().getColor(R.color.common_color));
                    flag_hairdressing = 0;
                    favoriteCodeList.remove("010");
                }
                break;
            case R.id.button_internet:
                if (flag_internet == 0) {
                    button_internet.setBackgroundResource(R.drawable.favorite_checked);
                    button_internet.setTextColor(Color.WHITE);
                    flag_internet = 1;
                    favoriteCodeList.add("011");
                } else {
                    button_internet.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_internet.setTextColor(getResources().getColor(R.color.common_color));
                    flag_internet = 0;
                    favoriteCodeList.remove("011");
                }
                break;
            case R.id.button_electron:
                if (flag_electron == 0) {
                    button_electron.setBackgroundResource(R.drawable.favorite_checked);
                    button_electron.setTextColor(Color.WHITE);
                    flag_electron = 1;
                    favoriteCodeList.add("012");
                } else {
                    button_electron.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_electron.setTextColor(getResources().getColor(R.color.common_color));
                    flag_electron = 0;
                    favoriteCodeList.remove("012");
                }
                break;
            case R.id.button_sport:
                if (flag_sport == 0) {
                    button_sport.setBackgroundResource(R.drawable.favorite_checked);
                    button_sport.setTextColor(Color.WHITE);
                    flag_sport = 1;
                    favoriteCodeList.add("013");
                } else {
                    button_sport.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_sport.setTextColor(getResources().getColor(R.color.common_color));
                    flag_sport = 0;
                    favoriteCodeList.remove("013");
                }
                break;
            case R.id.button_health:
                if (flag_health == 0) {
                    button_health.setBackgroundResource(R.drawable.favorite_checked);
                    button_health.setTextColor(Color.WHITE);
                    flag_health = 1;
                    favoriteCodeList.add("014");
                } else {
                    button_health.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_health.setTextColor(getResources().getColor(R.color.common_color));
                    flag_health = 0;
                    favoriteCodeList.remove("014");
                }
                break;
            case R.id.button_child:
                if (flag_child == 0) {
                    button_child.setBackgroundResource(R.drawable.favorite_checked);
                    button_child.setTextColor(Color.WHITE);
                    flag_child = 1;
                    favoriteCodeList.add("015");
                } else {
                    button_child.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_child.setTextColor(getResources().getColor(R.color.common_color));
                    flag_child = 0;
                    favoriteCodeList.remove("015");
                }
                break;
            case R.id.button_pet:
                if (flag_pet == 0) {
                    button_pet.setBackgroundResource(R.drawable.favorite_checked);
                    button_pet.setTextColor(Color.WHITE);
                    flag_pet = 1;
                    favoriteCodeList.add("016");
                } else {
                    button_pet.setBackgroundResource(R.drawable.favorite_uncheck);
                    button_pet.setTextColor(getResources().getColor(R.color.common_color));
                    flag_pet = 0;
                    favoriteCodeList.remove("016");
                }
                break;
            case R.id.button_register:
                if (Utils.isConnect(this)) {
                    if (favoriteCodeList.size() > 0) {
                        for (int i = 0; i < favoriteCodeList.size(); i++) {
                            interest += favoriteCodeList.get(i) + ",";
                        }
                        Log.d(TAG, "===FavoriteActivity#onClick() 要提交的兴趣爱好： = " + interest);
                        registerFavorite();
                    } else {
                        showMsg(getString(R.string.register_toast_unchose_favorite), Toast.LENGTH_SHORT);
                        return;
                    }
                } else {
                    Utils.showToast(this, R.string.netWrong);
                }
                break;
        }
    }

    private void registerFavorite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Post
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    params.add(new BasicNameValuePair("interest", interest));
                    params.add(new BasicNameValuePair("friendCount", edit_num.getText().toString().trim()));
                    //获取响应的结果信息
                    String json = getPostData(Constants.URL_USER_HOBBY, params);
                    Log.d(TAG, "===FavoriteActivity#registerFavorite() json" + json);
                    //JSON的解析
                    boolean result = false;
                    String msg = null;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        result = (boolean) jsonObject.get("success");
                        Log.d(TAG, "===FavoriteActivity#registerFavorite() result = " + result);
                        msg = jsonObject.get("msg").toString();
                        Log.d(TAG, "===FavoriteActivity#registerFavorite() msg = " + msg);
                    }

                    if (!result) {
                        //注册失败后的处理
                        Message message = new Message();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_FAILURE;
                        mHandler.sendMessage(message);
                    } else {
                        //注册成功后的处理
                        Message message = new Message();
                        message.obj = msg;
                        message.what = Constants.CODE_EXECUTE_SUCCESS;
                        mHandler.sendMessage(message);
                    }

                } catch (Exception e) {
                    //登录异常后的处理
                    Message message = new Message();
                    message.obj = e;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
}