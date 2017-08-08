package com.fujisoft.campaign;

import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.adapter.ColorTagAdapter;
import com.fujisoft.campaign.adapter.FlowTagLayout;
import com.fujisoft.campaign.adapter.StandardsTagAdapter;
import com.fujisoft.campaign.bean.GoodsDetailBean;
import com.fujisoft.campaign.service.MarketService;
import com.fujisoft.campaign.service.OnTagSelectListener;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;
import com.fujisoft.campaign.view.AmountView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fujisoft.campaign.utils.Utils.Retrofit;

/**
 * 商品详情页面
 */
public class GoodsDetailActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "campaign";
    private String goodsId = null;
    private String userId = null;
    private String goodsName = null;
    private String goodsDes = null;
    private int goodsQuantity = 1;
    private int mAmountValue = 1;
    private String goodsStandardsData = "";
    private String goodsColorData = null;

    private WebView goodsDesView;
    private TextView goodsDesTextView;

    private FlowTagLayout mColorFlowTagLayout;
    private FlowTagLayout mStandardsTagLayout01;
    private FlowTagLayout mStandardsTagLayout02;
    private FlowTagLayout mStandardsTagLayout03;
    private FlowTagLayout mStandardsTagLayout04;
    private FlowTagLayout mStandardsTagLayout05;
    private TextView color_text_view;
    private MarketService marketService;
    private int[] StandardsTagLayout = new int[]{R.id.goodsStandard01, R.id.goodsStandard02, R.id.goodsStandard03, R.id.goodsStandard04, R.id.goodsStandard05};
    private int[] StandardsName = new int[]{R.id.standardName01, R.id.standardName02, R.id.standardName03, R.id.standardName04, R.id.standardName05};
    private String[] StandardsTitle = new String[5];
    private String[] selectedStandDec = new String[5];

    public static enum CartMode {TOCART, TOBUY,}

    private RelativeLayout shoppingCartView;

    private String cartNum = null;
    private TextView shoppingCartNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        if (null != Retrofit) {
            marketService = Retrofit.create(MarketService.class);
        } else {
            Utils.getRetrofit(GoodsDetailActivity.this);
            marketService = Retrofit.create(MarketService.class);
        }
        mColorFlowTagLayout = (FlowTagLayout) findViewById(R.id.color_flow_layout);
        mStandardsTagLayout01 = (FlowTagLayout) findViewById(R.id.standard_value01);
        mStandardsTagLayout02 = (FlowTagLayout) findViewById(R.id.standard_value02);
        mStandardsTagLayout03 = (FlowTagLayout) findViewById(R.id.standard_value03);
        mStandardsTagLayout04 = (FlowTagLayout) findViewById(R.id.standard_value04);
        mStandardsTagLayout05 = (FlowTagLayout) findViewById(R.id.standard_value05);
        List<FlowTagLayout> mStandardsTagLayout = new ArrayList<FlowTagLayout>();
        mStandardsTagLayout.add(mStandardsTagLayout01);
        mStandardsTagLayout.add(mStandardsTagLayout02);
        mStandardsTagLayout.add(mStandardsTagLayout03);
        mStandardsTagLayout.add(mStandardsTagLayout04);
        mStandardsTagLayout.add(mStandardsTagLayout05);
        mColorFlowTagLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        mStandardsTagLayout01.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        mStandardsTagLayout02.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        mStandardsTagLayout03.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        mStandardsTagLayout04.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        mStandardsTagLayout05.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        color_text_view = (TextView) findViewById(R.id.color_text_view);
        mColorFlowTagLayout.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    for (int i : selectedList) {
                        goodsColorData = parent.getAdapter().getItem(i).toString();
                    }
                } else {
                    goodsColorData = null;
                }
            }
        });
        mStandardsTagLayout01.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    for (int i : selectedList) {
                        selectedStandDec[0] = parent.getAdapter().getItem(i).toString();
                    }
                } else {
                    selectedStandDec[0] = null;
                }
            }
        });
        mStandardsTagLayout02.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    for (int i : selectedList) {
                        selectedStandDec[1] = parent.getAdapter().getItem(i).toString();
                    }
                } else {
                    selectedStandDec[1] = null;
                }
            }
        });
        mStandardsTagLayout03.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    for (int i : selectedList) {
                        selectedStandDec[2] = parent.getAdapter().getItem(i).toString();
                    }
                } else {
                    selectedStandDec[2] = null;
                }
            }
        });
        mStandardsTagLayout04.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    for (int i : selectedList) {
                        selectedStandDec[3] = parent.getAdapter().getItem(i).toString();
                    }
                } else {
                    selectedStandDec[3] = null;
                }
            }
        });
        mStandardsTagLayout05.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    for (int i : selectedList) {
                        selectedStandDec[4] = parent.getAdapter().getItem(i).toString();
                    }
                } else {
                    selectedStandDec[4] = null;
                }
            }
        });
        goodsId = getIntent().getStringExtra(Constants.EXTRA_GOODS_ID);
//        userId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

        goodsDesView = (WebView) findViewById(R.id.goods_content_des);
        goodsDesTextView = (TextView) findViewById(R.id.goods_content_des_text);

        Button mExchangeFlowersButton = (Button) findViewById(R.id.goods_detail_exchange_flowers_button);
        Button toCardBtn = (Button) findViewById(R.id.toCardBtn);
        mExchangeFlowersButton.setOnClickListener(this);
        toCardBtn.setOnClickListener(this);
        showProgressDialog();
        if (Utils.isConnect(this)) {
            loadData(mStandardsTagLayout);
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
        RelativeLayout shoppingCartView = (RelativeLayout) findViewById(R.id.tool_bar_shopping_car_button);
        shoppingCartView.setVisibility(View.VISIBLE);
//        shoppingCartView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 跳转到购物车画面
//                Intent intent = new Intent();
//                intent.setClass(GoodsDetailActivity.this, ShoppingCartActivity.class);
//                intent.putExtra(Constants.EXTRA_USER_ID, userId);
//                startActivity(intent);
//            }
//        });

        TextView tool_bar_center_text_view = (TextView) findViewById(R.id.tool_bar_center_text_view);
        tool_bar_center_text_view.setVisibility(View.VISIBLE);
        tool_bar_center_text_view.setText(R.string.goods_detail_title);


        shoppingCartNum = (TextView) findViewById(R.id.tool_bar_shopping_car_num);
    }


    /**
     * 点击事件
     *
     * @param view
     */
    public void onButtonClick(View view) {
        if (view == null) {
            return;
        }
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tool_bar_shopping_car:       // 购物车
                if (null != userId && !"".equals(userId)) {
                    intent = new Intent();
                    intent.setClass(this, ShoppingCartActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_ID, userId);
                } else {
                    toLogin();
                    return;
                }
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }

    }


    @Override
    public void onResume() {
        Log.d(TAG, "=== GoodsDetailActivity#onResume() ===");
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");

        getCartNum();
    }


    @Override
    public void onClick(View v) {
        if (null == userId || "".equals(userId)) {
            toLogin();
            return;
        }
        goodsStandardsData = "";
        if (goodsColorData == null) {
            showMsg("请选择商品的颜色！", Toast.LENGTH_SHORT);
            return;
        }
        for (int i = 0; i < 4; i++) {
            LinearLayout linearLayout = (LinearLayout) findViewById(StandardsTagLayout[i]);
            if (linearLayout.getVisibility() == View.VISIBLE) {
                if (selectedStandDec[i] == null) {
                    showMsg("请选择" + StandardsTitle[i] + "！", Toast.LENGTH_SHORT);
                    return;
                } else {
                    goodsStandardsData += selectedStandDec[i] + ',';
                }
            }
        }
        if (!"".equals(goodsStandardsData)) {

            goodsStandardsData = "[" + goodsStandardsData.substring(0, goodsStandardsData.length() - 1) + "]";
        }
        if (v.getId() == R.id.goods_detail_exchange_flowers_button) {
            addToCart(CartMode.TOCART);
        } else {
            addToCart(CartMode.TOBUY);
        }
    }

    /**
     * 加载商品详情的数据
     *
     * @param mStandardsTagLayout
     */
    private void loadData(final List<FlowTagLayout> mStandardsTagLayout) {
        Call<GoodsDetailBean> call = marketService.getGoodsDetail(userId, goodsId);
        call.enqueue(new Callback<GoodsDetailBean>() {
            @Override
            public void onResponse(Call<GoodsDetailBean> call, Response<GoodsDetailBean> response) {
                GoodsDetailBean result = response.body();
                if (result != null) {
                    GoodsDetailBean.GoodsData ds = result.getData();
                    GoodsDetailBean.Detail goodsData = ds.getGoods();
                    if (goodsData != null) {
                        SimpleDraweeView goodsLogo = (SimpleDraweeView) findViewById(R.id.goods_detail_goods_picture);
                        goodsLogo.setImageURI(Constants.PICTURE_BASE_URL + goodsData.getgoodsPicture());
                        TextView txtScorePrice = (TextView) findViewById(R.id.goods_detail_exchange_flowers_text);
                        txtScorePrice.setText(String.format("鲜花:%s", goodsData.getScorePrice()));
                        goodsName = goodsData.getGoodsName();
                        goodsDes = goodsData.getGoodsDes();
                        Log.d("aaa", "=== GoodsDetailActivity#loadData() goodsDes = " + goodsDes);
                        goodsDesView.getSettings().setJavaScriptEnabled(true);
                        // 支持缩放(适配到当前屏幕)
                        goodsDesView.getSettings().setSupportZoom(true);
                        // 将图片调整到合适的大小
                        goodsDesView.getSettings().setUseWideViewPort(true);
                        // 支持内容重新布局,一共有四种方式
                        // 默认的是NARROW_COLUMNS
                        goodsDesView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                        // 设置可以被显示的屏幕控制
                        goodsDesView.getSettings().setDisplayZoomControls(true);
//                        if (null != goodsDes && !"".equals(goodsDes) && (goodsDes.startsWith("http") || goodsDes.startsWith("<div"))) {

                        if (null != goodsDes && !"".equals(goodsDes)) {
                            goodsDesView.loadData(goodsDes, "text/html", "utf-8");
                            goodsDesView.loadDataWithBaseURL(null, goodsDes, "text/html", "UTF-8", null);
                        } else {
                            goodsDesView.setVisibility(View.GONE);
                            goodsDesTextView.setVisibility(View.VISIBLE);
                            goodsDesTextView.setText(goodsDes);
                        }
                        TextView txtGoodsName = (TextView) findViewById(R.id.goods_detail_goods_name);
                        txtGoodsName.setText(goodsName);
                        AmountView mAmountView = (AmountView) findViewById(R.id.amount_view);
                        goodsQuantity = goodsData.getGoodsQuantity();
                        mAmountView.setGoods_storage(goodsQuantity);
                        mAmountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
                            @Override
                            public void onAmountChange(View view, int amount) {
                                mAmountValue = amount;
                                if (amount > goodsQuantity) {
                                    Toast.makeText(getApplicationContext(), "您输入的数量“" + amount + "”大于商品的库存了，请重新输入！", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
                    }
                    List<GoodsDetailBean.GoodsColor> colorList = ds.getGoodsColor();
                    if (colorList != null) {
                        ColorTagAdapter colorTagAdapter = new ColorTagAdapter(GoodsDetailActivity.this);
                        mColorFlowTagLayout.setAdapter(colorTagAdapter);
                        color_text_view.setText(getString(R.string.goods_detail_goods_color));
                        goodsColorData = colorList.get(0).toString();
                        colorTagAdapter.onlyAddAll(colorList);
                    }
                    List<GoodsDetailBean.GoodsStandards> StandardsList = ds.getGoodsStandards();
                    if (StandardsList != null && StandardsList.size() > 0) {
                        for (int i = 0; i < StandardsList.size(); i++) {
                            StandardsTagAdapter standardsAdapter = new StandardsTagAdapter(GoodsDetailActivity.this);
                            GoodsDetailBean.GoodsStandards goodsStandards = StandardsList.get(i);
                            LinearLayout linearLayout = (LinearLayout) findViewById(StandardsTagLayout[i]);
                            linearLayout.setVisibility(View.VISIBLE);
                            TextView textView = (TextView) findViewById(StandardsName[i]);
                            textView.setText(goodsStandards.getStandardZh());
                            FlowTagLayout flowTagLayout = mStandardsTagLayout.get(i);
                            flowTagLayout.setAdapter(standardsAdapter);
                            List<GoodsDetailBean.Standards> decList = new ArrayList<GoodsDetailBean.Standards>();
                            StandardsTitle[i] = goodsStandards.getStandardZh();
                            for (String dec : goodsStandards.getStandardDec()) {
                                GoodsDetailBean.Standards standards = new GoodsDetailBean.Standards();
                                standards.setStandardDec(dec);
                                standards.setStandardZh(goodsStandards.getStandardZh());
                                standards.setStandardEn(goodsStandards.getStandardEn());
                                decList.add(standards);
                            }
                            standardsAdapter.onlyAddAll(decList);
                            selectedStandDec[i] = decList.get(0).toString();
                        }
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<GoodsDetailBean> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }

    /**
     * 追加商品到购物车
     *
     * @param cardmode
     */
    private void addToCart(final CartMode cardmode) {
        Call<GoodsDetailBean> call = marketService.addToCart(userId, mAmountValue, goodsId, goodsStandardsData, goodsColorData, goodsName);
        call.enqueue(new Callback<GoodsDetailBean>() {
            @Override
            public void onResponse(Call<GoodsDetailBean> call, Response<GoodsDetailBean> response) {
                GoodsDetailBean result = response.body();
                if (result != null) {
                    if (result.getSuccess()) {
                        if (cardmode == CartMode.TOBUY) {
                            showMsg("购物车添加成功", Toast.LENGTH_SHORT);

                            getCartNum();

                        } else {
                            Intent intent = new Intent(GoodsDetailActivity.this, ShoppingCartActivity.class);
                            intent.putExtra(Constants.EXTRA_USER_ID, userId);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GoodsDetailBean> call, Throwable throwable) {
            }
        });
    }


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            dismissProgressDialog();
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:

                    if (cartNum != null && !"".equals(cartNum) && !"null".equals(cartNum)) {
                        shoppingCartNum.setText(cartNum);
                        shoppingCartNum.setVisibility(View.VISIBLE);
                    } else {
                        shoppingCartNum.setVisibility(View.GONE);
                    }
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取购物车右上角的数值
     */
    public void getCartNum() {
//        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = getPostData(Constants.URL_CARTNUM, params);
                    Log.d(TAG, "=== TabMainActivity#getCartNum() json = " + json);

                    //JSON的解析过程
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        boolean result = (boolean) jsonObject.get("success");
                        if (result) {
                            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
                            if (dataJsonObject != null) {
                                cartNum = dataJsonObject.getString("cartNum");
                            }
                            //成功后的处理
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //异常后的处理
                    mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                }
            }
        }).start();
    }
}