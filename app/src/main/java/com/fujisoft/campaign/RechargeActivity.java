package com.fujisoft.campaign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fujisoft.campaign.adapter.RechargeRecyclerAdapter;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.RechargeItemModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 充值页面
 */
public class RechargeActivity extends BaseActivity implements RechargeRecyclerAdapter.OnItemClickListener, View.OnClickListener {
    private String TAG = "campaign";
    private RecyclerView recyclerView;
    private RechargeRecyclerAdapter adapter;

    private String mRechargeAmount2000 = "";                         // 从服务器端接收到的年费的充值金额数
    private String mSelectAmount = "";                               // 选择的充值金额

    private EditText mInputRechargeAmount;                      // 输入的充值金额EditView
    private TextView mGetScore;                                  // 充值对应得到的金币数TextView
    private TextView mRechargeSentGoldAmountNote;              // 输入的充值可兑换的金币数TextView
    private Button mRechargeButton;                      // 微信充值按钮

    private int rmbToPoints;                                   // 人民币兑换积分,从服务器端接收到的(注：1元 = %d金币)中的%d
    private String rechargePoint;                              // 充年费赠送的金币数
    private String payType = null;                            // 付款类型 1：年费 2：元宝

    private String userId = null;
    private String userType = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    showMsg(getString(R.string.loading_index_view_error), Toast.LENGTH_SHORT);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    adapter.replaceAll(getData());
                    mRechargeSentGoldAmountNote.setText(String.format(getString(R.string.recharge_sent_gold_note), rmbToPoints));
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    showMsg(getString(R.string.loading_index_view_exception), Toast.LENGTH_SHORT);
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_recharge);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        userType = sharedPreferences.getString(Constants.EXTRA_USER_TYPE, "");
        Log.d(TAG, "=== RechargeActivity#onCreate() 从SharedPreferences中取到的userId = " + userId);
        Log.d(TAG, "=== RechargeActivity#onCreate() 从SharedPreferences中取到的userType = " + userType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRechargeData();
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
            }
        });

        TextView tool_bar_center_text_view = (TextView) findViewById(R.id.tool_bar_center_text_view);
        tool_bar_center_text_view.setVisibility(View.VISIBLE);
        tool_bar_center_text_view.setText(R.string.recharge_title);
    }

    private void initView() {

        // 输入的充值金额
        mInputRechargeAmount = (EditText) findViewById(R.id.recharge_input_amount_et);
        mInputRechargeAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputRechargeAmount.setCursorVisible(true);
            }
        });
        mInputRechargeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (0 != count) {
                    // 选择充值金额后，再输入金额时，清空选择的金额
                    recyclerView.setAdapter(adapter = new RechargeRecyclerAdapter(RechargeActivity.this));
                    adapter.replaceAll(getData());
                } else {
                    mInputRechargeAmount.setCursorVisible(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s && !"".equals(s.toString()) && null != mGetScore) {
                    // 充值应得的金币数mGetScore计算
                    NumberFormat nf = new DecimalFormat("#");
                    mGetScore.setText(String.valueOf(nf.format(Float.parseFloat(s.toString()) * rmbToPoints)));
                }
            }
        });

        // 选择充值的金额数据
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 每行显示2个充值金额的选项
        recyclerView.setAdapter(adapter = new RechargeRecyclerAdapter(this));
//        adapter.replaceAll(getData());

        // 充值对应得到的金币TextView
        mGetScore = (TextView) findViewById(R.id.recharge_sent_gold_amount_tv);

        // (注：1元 = %d金币)
        mRechargeSentGoldAmountNote = (TextView) findViewById(R.id.recharge_sent_gold_amount_note);

        mRechargeButton = (Button) findViewById(R.id.recharge_button);
        mRechargeButton.setOnClickListener(this);
    }

    /**
     * 充值金额的数据集
     *
     * @return
     */
    public ArrayList<RechargeItemModel> getData() {
        ArrayList<RechargeItemModel> list = new ArrayList<>();
        list.add(new RechargeItemModel(RechargeItemModel.AMOUNT, getString(R.string.recharge_select_amount_50)));
        list.add(new RechargeItemModel(RechargeItemModel.AMOUNT, getString(R.string.recharge_select_amount_100)));
        list.add(new RechargeItemModel(RechargeItemModel.AMOUNT, getString(R.string.recharge_select_amount_200)));
        list.add(new RechargeItemModel(RechargeItemModel.AMOUNT, getString(R.string.recharge_select_amount_500)));
        list.add(new RechargeItemModel(RechargeItemModel.AMOUNT, String.format(getString(R.string.recharge_select_amount_2000), mRechargeAmount2000)));
        return list;
    }

    @Override
    public void onSelectRechargeAmount(String selectAmount) {
        payType = "2";
        // 调用接口，获取选择的金额数据
        Log.d(TAG, "=== RechargeActivity#onSelectRechargeAmount() selectAmount = " + selectAmount);
        if (null != selectAmount && !"".equals(selectAmount)) { // 选择充值金额时
            if (getString(R.string.recharge_select_amount_50).equals(selectAmount))
                mSelectAmount = "50";
            if (getString(R.string.recharge_select_amount_100).equals(selectAmount))
                mSelectAmount = "100";
            if (getString(R.string.recharge_select_amount_200).equals(selectAmount))
                mSelectAmount = "200";
            if (getString(R.string.recharge_select_amount_500).equals(selectAmount))
                mSelectAmount = "500";
            if (String.format(getString(R.string.recharge_select_amount_2000), mRechargeAmount2000).equals(selectAmount)) {
                mSelectAmount = mRechargeAmount2000;
                payType = "1";
            }
            // 充值应得的金币数mGetScore计算
            if (String.format(getString(R.string.recharge_select_amount_2000), mRechargeAmount2000).equals(selectAmount)) {
                mGetScore.setText(rechargePoint);
            } else {
                NumberFormat nf = new DecimalFormat("#");
                mGetScore.setText(String.valueOf(nf.format(Float.parseFloat(mSelectAmount) * rmbToPoints)));
            }
        }
    }

    @Override
    public void clearInputAmount() {
        // 输入充值金额后，再选择充值金额时，清空输入的金额
        if (null != mInputRechargeAmount && null != mInputRechargeAmount.getText() && !"".equals(mInputRechargeAmount.getText())) {
            mInputRechargeAmount.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.recharge_button) {
            // 输入充值金额时
            if (null != mInputRechargeAmount && null != mInputRechargeAmount.getText() && !"".equals(mInputRechargeAmount.getText().toString())) {
                String inputAmount = String.valueOf(Float.parseFloat(mInputRechargeAmount.getText().toString().trim()));
                showMsg(getString(R.string.recharge_confirm) + inputAmount, Toast.LENGTH_SHORT);
                Intent inputIntent = new Intent();
                inputIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                inputIntent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                inputIntent.putExtra(Constants.EXTRA_AMOUNT, String.valueOf(inputAmount));
                inputIntent.putExtra("payType", payType);
                inputIntent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_RECHARGE_TYPE);
                inputIntent.setClass(RechargeActivity.this, PayAndRechargeActivity.class);
                startActivity(inputIntent);
            } else if (!"0".equals(mSelectAmount) && !"".equals(mSelectAmount)) { // 选择充值金额时
                showMsg(getString(R.string.recharge_confirm) + mSelectAmount, Toast.LENGTH_SHORT);
                Intent choseIntent = new Intent();
                choseIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                choseIntent.putExtra(Constants.EXTRA_USER_TYPE, userType);
                choseIntent.putExtra(Constants.EXTRA_AMOUNT, String.valueOf(mSelectAmount));
                choseIntent.putExtra("payType", payType);
                choseIntent.putExtra(Constants.EXTRA_PAY_RECHARGE_TYPE, Constants.EXTRA_RECHARGE_TYPE);
                choseIntent.setClass(RechargeActivity.this, PayAndRechargeActivity.class);
                startActivity(choseIntent);
            } else {
                showMsg(getString(R.string.recharge_confirm_msg), Toast.LENGTH_SHORT);
            }
        }
    }

    private void getRechargeData() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                String msg = "Exception";
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", null));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_FINANCE_RECHARGE_LOADING, params);
                    Log.d(TAG, "=== RechargeActivity#getRechargeData() json = " + json);
                    //JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = jsonObject.getBoolean("success");
                        msg = jsonObject.get("msg").toString();
                        if (result) {
                            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
                            rmbToPoints = dataJsonObject.getInt("rmbToPoints");
                            mRechargeAmount2000 = dataJsonObject.getString("annual_fee");  // 年费
                            rechargePoint = dataJsonObject.getString("rechargePoint");  // 充年费赠送的金币数

                            //充值页面加载成功后的处理
                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_SUCCESS;
                            mHandler.sendMessage(message);
                        } else {
                            // 充值页面加载失败后的处理

                            message.obj = msg;
                            message.what = Constants.CODE_EXECUTE_FAILURE;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    //充值页面加载异常后的处理
                    message.obj = msg;
                    message.what = Constants.CODE_EXECUTE_EXCEPTION;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }
}