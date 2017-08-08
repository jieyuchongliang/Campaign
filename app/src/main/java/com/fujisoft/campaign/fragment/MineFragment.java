package com.fujisoft.campaign.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.LoginActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.TabMainActivity;
import com.fujisoft.campaign.bean.MessageBean;
import com.fujisoft.campaign.bean.OrderBean;
import com.fujisoft.campaign.bean.OrderScoreSumBean;
import com.fujisoft.campaign.bean.User;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.JsonUtils;
import com.fujisoft.campaign.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MineFragment extends Fragment {

    private String TAG = "campaign";
    private TabMainActivity mTabActivity;

    private String userId = null;
    private View view = null;
    private Map mineDate = null;

    private TextView unLoginText;
    private LinearLayout mineLayout;
    // 头像
    private SimpleDraweeView changePhotoIcon;
    private SimpleDraweeView userIcon;
    // 昵称
    private TextView userNickName;
    private TextView myNickName;
    // 用户类型
    private TextView myUserType;
    // 鲜花数量
    private TextView myFlowerNum;

    //草莓
    private TextView mine_run_to_strawberry_num_text;
    private ImageView mine_strawberry_icon_img;

    //enterAcer
    private TextView mine_enter_acer_num_text;
    private LinearLayout mine_enter_acer_linearlayout;

    // 元宝
    private ImageView goldIcon;
    private TextView goldNum;
    // 个人信息栏
    private TextView publishTask;
    private TextView staffButton;
    // 排行榜
    private TextView myOrderSort;
    private TextView orderSort1;
    private TextView orderSort2;
    private TextView orderSort3;
    private SimpleDraweeView orderSortImg1;
    private SimpleDraweeView orderSortImg2;
    private SimpleDraweeView orderSortImg3;
    private TextView orderSortName1;
    private TextView orderSortName2;
    private TextView orderSortName3;
    // 订单列表
    private LinearLayout orderListLinearLayout;
    private SimpleDraweeView orderListImg;
    private TextView orderListScoreNum;
    private TextView orderListTitle;

    // 消息列表
    private LinearLayout messageListLinearLayout;
    private SimpleDraweeView messageListImg;
    private TextView messageListTaskState;
    private TextView messageListTitle;

    private LinearLayout activity_mine;

    //【我的财务】【已发任务】
    private LinearLayout linearLayoutFooter;
    private LinearLayout mine_info_layout;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    if( mTabActivity.mCurrentTab==3) {
                        mTabActivity.dismissProgressDialog();
                    }
                    mTabActivity.showErrorDialog(Constants.CODE_EXECUTE_FAILURE);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    if (!isAdded()) {
                        return;
                    }
                    updateView();
                    if( mTabActivity.mCurrentTab==3) {
                        mTabActivity.dismissProgressDialog();
                    }
                    activity_mine.setVisibility(View.VISIBLE);
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    if( mTabActivity.mCurrentTab==3) {
                        mTabActivity.dismissProgressDialog();
                    }
                    Utils.showToast(mTabActivity, R.string.loading_index_view_exception);
                    break;
                default:
                    if( mTabActivity.mCurrentTab==3) {
                        mTabActivity.dismissProgressDialog();
                    }
                    break;
            }
        }
    };

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabActivity = (TabMainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        activity_mine = (LinearLayout) view.findViewById(R.id.activity_mine);
        return view;
    }


    @Override
    public void onResume() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        super.onResume();
        activity_mine.setVisibility(View.INVISIBLE);
        initView();
    }

    private void initView() {
        unLoginText = (TextView) view.findViewById(R.id.unlogin_text);
        mineLayout = (LinearLayout) view.findViewById(R.id.mine_layout);

        // 头像
        changePhotoIcon = (SimpleDraweeView) view.findViewById(R.id.mine_change_photo_user_icon);
        userIcon = (SimpleDraweeView) view.findViewById(R.id.mine_user_icon);
        // 昵称
        userNickName = (TextView) view.findViewById(R.id.user_alias_text);
        myNickName = (TextView) view.findViewById(R.id.mine_user_alis_name);
        // 用户类型
        myUserType = (TextView) view.findViewById(R.id.user_type_text);
        // 鲜花数量
        myFlowerNum = (TextView) view.findViewById(R.id.mine_run_to_flower_num_text);
        //草莓
        mine_run_to_strawberry_num_text = (TextView) view.findViewById(R.id.mine_run_to_strawberry_num_text);
        mine_strawberry_icon_img=(ImageView)view.findViewById(R.id.mine_strawberry_icon_img);

        //企业元宝
        mine_enter_acer_num_text = (TextView) view.findViewById(R.id.mine_enter_acer_num_text);
        mine_enter_acer_linearlayout = (LinearLayout) view.findViewById(R.id.mine_enter_acer_linearlayout);

        // 元宝
        goldIcon = (ImageView) view.findViewById(R.id.mine_run_to_gold_icon_img);
        goldNum = (TextView) view.findViewById(R.id.mine_run_to_gold_num_text);
        // 个人信息栏
        publishTask = (TextView) view.findViewById(R.id.mine_publish_task_btn);
        TextView sharedTask = (TextView) view.findViewById(R.id.mine_shared_task_btn);
        staffButton = (TextView) view.findViewById(R.id.mine_staff_btn);
        // 排行榜
        myOrderSort = (TextView) view.findViewById(R.id.mine_user_oder_sort_num);
        orderSort1 = (TextView) view.findViewById(R.id.mine_order_friend_1_text);
        orderSort2 = (TextView) view.findViewById(R.id.mine_order_friend_2_text);
        orderSort3 = (TextView) view.findViewById(R.id.mine_order_friend_3_text);
        orderSortImg1 = (SimpleDraweeView) view.findViewById(R.id.mine_order_friend_1_icon);
        orderSortImg2 = (SimpleDraweeView) view.findViewById(R.id.mine_order_friend_2_icon);
        orderSortImg3 = (SimpleDraweeView) view.findViewById(R.id.mine_order_friend_3_icon);
        orderSortName1 = (TextView) view.findViewById(R.id.mine_order_friend_1_name);
        orderSortName2 = (TextView) view.findViewById(R.id.mine_order_friend_2_name);
        orderSortName3 = (TextView) view.findViewById(R.id.mine_order_friend_3_name);

        // 订单列表
        orderListLinearLayout = (LinearLayout) view.findViewById(R.id.mine_my_order_list_linearlayout);
        orderListImg = (SimpleDraweeView) view.findViewById(R.id.mine_my_order_list_img);
        orderListTitle = (TextView) view.findViewById(R.id.mine_order_list_title);
        orderListScoreNum = (TextView) view.findViewById(R.id.mine_order_list_score_num);

        // 消息列表

        messageListLinearLayout = (LinearLayout) view.findViewById(R.id.mine_my_message_list_linearlayout);
        messageListImg = (SimpleDraweeView) view.findViewById(R.id.mine_my_message_list_img);
        messageListTaskState = (TextView) view.findViewById(R.id.mine_message_list_task_state);
        messageListTaskState.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        messageListTitle = (TextView) view.findViewById(R.id.mine_message_list_task_title);

        //【我的财务】【已发任务】
        linearLayoutFooter = (LinearLayout) view.findViewById(R.id.mine_my_footer);

        activity_mine = (LinearLayout) view.findViewById(R.id.activity_mine);

        mine_info_layout=(LinearLayout) view.findViewById(R.id.mine_info_layout);

        if (null == userId || "".equals(userId)) {
            unLoginText.setVisibility(View.VISIBLE);
            mineLayout.setVisibility(View.GONE);

            activity_mine.setVisibility(View.VISIBLE);

            unLoginText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startLoginIntent = new Intent();
                    startLoginIntent.setClass(mTabActivity, LoginActivity.class);
                    startActivityForResult(startLoginIntent, Constants.CODE_EXECUTE_SUCCESS);
                }
            });

        } else {
            unLoginText.setVisibility(View.GONE);
            mineLayout.setVisibility(View.VISIBLE);
            initMineData(userId);
        }
    }

    private void updateView() {
        if (mineDate != null) {
            User user = (User) mineDate.get("user");

            if (user != null) {
                changePhotoIcon.setImageURI(Constants.PICTURE_BASE_URL + user.getHeadPicBitmap());
                userIcon.setImageURI(Constants.PICTURE_BASE_URL + user.getHeadPicBitmap());
                if (user.getNickname() != null) {
                    userNickName.setText(user.getNickname());

                    myNickName.setText(user.getNickname());
                } else {
                    userNickName.setText("");

                    myNickName.setText("");
                }

                //企业绑定会员
                if (Constants.USER_TYPE_STAFF.equals(user.getUserType())) {
                    myUserType.setText(getString(R.string.mine_my_user_type_staff));

                    // 显示元宝项
/*                    if (goldIcon.getVisibility() == View.GONE) {
                        goldIcon.setVisibility(View.VISIBLE);
                    }
                    if (goldNum.getVisibility() == View.GONE) {
                        goldNum.setVisibility(View.VISIBLE);
                    }*/
                    goldIcon.setVisibility(View.VISIBLE);
                    goldNum.setVisibility(View.VISIBLE);
                    // 不显示【员工】【发布任务】
                    publishTask.setVisibility(View.GONE);
                    staffButton.setVisibility(View.GONE);

                    // 不显示【我的财务】【已发任务】
                    linearLayoutFooter.setVisibility(View.GONE);


                    // 不显示【企业元宝】
                    mine_enter_acer_linearlayout.setVisibility(View.GONE);

                    // 显示【草莓】
                    mine_run_to_strawberry_num_text.setVisibility(View.VISIBLE);
                    mine_strawberry_icon_img.setVisibility(View.VISIBLE);


                    mine_info_layout.setWeightSum(2);

                    //个人用户
                } else if (Constants.USER_TYPE_NORMAL.equals(user.getUserType())) {
                    myUserType.setText(getString(R.string.mine_my_user_type_normal));
                    // 不显示元宝项
/*                    if (goldIcon.getVisibility() == View.VISIBLE) {
                        goldIcon.setVisibility(View.INVISIBLE);
                    }
                    if (goldNum.getVisibility() == View.VISIBLE) {
                        goldNum.setVisibility(View.INVISIBLE);
                    }*/
                    goldIcon.setVisibility(View.INVISIBLE);
                    goldNum.setVisibility(View.INVISIBLE);
                    // 不显示【草莓】
                    mine_run_to_strawberry_num_text.setVisibility(View.INVISIBLE);
                    mine_strawberry_icon_img.setVisibility(View.INVISIBLE);

                    // 不显示【企业元宝】
                    mine_enter_acer_linearlayout.setVisibility(View.INVISIBLE);


                    // 不显示【员工】【发布任务】
                    publishTask.setVisibility(View.GONE);
                    staffButton.setVisibility(View.GONE);

                    // 不显示【我的财务】【已发任务】
                    linearLayoutFooter.setVisibility(View.GONE);


                    mine_info_layout.setWeightSum(2);

                    //企业管理员
                } else if (Constants.USER_TYPE_MANAGER.equals(user.getUserType())) {
                    myUserType.setText(getString(R.string.mine_my_user_type_manager));

                    // 显示元宝项
/*                    if (goldIcon.getVisibility() == View.GONE) {
                        goldIcon.setVisibility(View.VISIBLE);
                    }
                    if (goldNum.getVisibility() == View.GONE) {
                        goldNum.setVisibility(View.VISIBLE);
                    }*/
                    goldIcon.setVisibility(View.VISIBLE);
                    goldNum.setVisibility(View.VISIBLE);
                    // 显示【草莓】
                    mine_run_to_strawberry_num_text.setVisibility(View.VISIBLE);
                    mine_strawberry_icon_img.setVisibility(View.VISIBLE);

                    // 显示【企业元宝】
                    mine_enter_acer_linearlayout.setVisibility(View.VISIBLE);

                    // 显示【员工】【发布任务】
                    publishTask.setVisibility(View.VISIBLE);
                    staffButton.setVisibility(View.VISIBLE);

                    // 显示【我的财务】【已发任务】
                    linearLayoutFooter.setVisibility(View.VISIBLE);

                    mine_info_layout.setWeightSum(4);

                }
                // 我的鲜花数
                String myScoreStr = "";
                int myScore = Integer.valueOf(user.getScore());
/*                if (myScore >= 10000) {
                    float myScoreNum = ((float) Math.round(myScore / 100) / 100);
                    myScoreStr = String.valueOf(myScoreNum) + "万";
                } else {
                    myScoreStr = String.valueOf(myScore);
                }*/
                myScoreStr = String.valueOf(myScore);
                myFlowerNum.setText(myScoreStr);

            }
            // 我的元宝数
            String myGoldStr = "";
            if (mineDate.get("acer") != null&& !"null".equals(mineDate.get("acer"))) {

                int myGold = Integer.valueOf((String) mineDate.get("acer"));
                if (myGold >= 10000) {
                    float myScoreNum = ((float) Math.round(myGold / 100) / 100);
                    myGoldStr = String.valueOf(myScoreNum) + "万";
                } else {
                    myGoldStr = String.valueOf(myGold);
                }
                goldNum.setText(myGoldStr);
            }

            // 我的strawberry数
            String mystrawberryStr = "";
            if (mineDate.get("strawberry") != null&& !"null".equals(mineDate.get("strawberry"))) {

                int mystrawberry = Integer.valueOf((String) mineDate.get("strawberry"));
                if (mystrawberry >= 10000) {
                    float myScoreNum = ((float) Math.round(mystrawberry / 100) / 100);
                    mystrawberryStr = String.valueOf(myScoreNum) + "万";
                } else {
                    mystrawberryStr = String.valueOf(mystrawberry);
                }
                mine_run_to_strawberry_num_text.setText(mystrawberryStr);
            }

            // 我的enter_acer数
            String myenterAcerStr = "";
            if (mineDate.get("enterAcer") != null&& !"null".equals(mineDate.get("enterAcer"))) {

                int mystrawberry = Integer.valueOf((String) mineDate.get("enterAcer"));
                if (mystrawberry >= 10000) {
                    float myScoreNum = ((float) Math.round(mystrawberry / 100) / 100);
                    myenterAcerStr = String.valueOf(myScoreNum) + "万";
                } else {
                    myenterAcerStr = String.valueOf(mystrawberry);
                }
                mine_enter_acer_num_text.setText(myenterAcerStr);

            }
            // 我的排行
            if (mineDate.get("sort") != null && !"null".equals(mineDate.get("sort"))) {

                myOrderSort.setText((String) mineDate.get("sort"));
                OrderScoreSumBean orderScoreSumBean = null;
                for (int i = 0; i < 3; i++) {
                    orderScoreSumBean = (OrderScoreSumBean) mineDate.get("orderScoreSum" + i);
                    if (i == 0) {
                        orderSort1.setText(orderScoreSumBean.getRowNo());
                        if (orderScoreSumBean.getHeadPicBitmap() == null) {
                            orderSortImg1.setImageDrawable(getResources().getDrawable(R.mipmap.default_user_icon));
                        } else {
                            orderSortImg1.setImageURI(Constants.PICTURE_BASE_URL + orderScoreSumBean.getHeadPicBitmap());
                        }
                        orderSortName1.setText(orderScoreSumBean.getNickname());
                    } else if (i == 1) {
                        orderSort2.setText(orderScoreSumBean.getRowNo());
                        if (orderScoreSumBean.getHeadPicBitmap() == null) {
                            orderSortImg2.setImageResource(R.mipmap.default_user_icon);
                        } else {
                            orderSortImg2.setImageURI(Constants.PICTURE_BASE_URL + orderScoreSumBean.getHeadPicBitmap());
                        }
                        orderSortName2.setText(orderScoreSumBean.getNickname());
                    } else if (i == 2) {
                        orderSort3.setText(orderScoreSumBean.getRowNo());
                        if (orderScoreSumBean.getHeadPicBitmap() == null) {
                            orderSortImg3.setImageDrawable(getResources().getDrawable(R.mipmap.default_user_icon));
                        } else {
                            orderSortImg3.setImageURI(Constants.PICTURE_BASE_URL + orderScoreSumBean.getHeadPicBitmap());
                        }
                        orderSortName3.setText(orderScoreSumBean.getNickname());
                    }
                }

            }
            // 订单列表
            if (mineDate.get("order") != null && !"null".equals(mineDate.get("order"))) {
                Log.d(TAG, "=== MineFragment#mineDate.get(order) = " + mineDate.get("order"));

                OrderBean orderBean = (OrderBean) mineDate.get("order");
                if (orderBean != null) {
                    orderListLinearLayout.setVisibility(View.VISIBLE);
                    Log.d(TAG, "=== MineFragment#orderBean = " + orderBean);
                    if (orderBean.getGoodsPictureBitmap() != null) {
                        orderListImg.setImageURI(Constants.PICTURE_BASE_URL + orderBean.getGoodsPictureBitmap());
                    }
                    if (orderBean.getGoodsName() != null) {

                        Log.d(TAG, "=== MineFragment#orderBean.getGoodsName() = " + orderBean.getGoodsName());
                        orderListTitle.setText(orderBean.getGoodsName());
                    }
                    if (orderBean.getTotalScorePrice() != null) {
                        orderListScoreNum.setText(orderBean.getTotalScorePrice());
                    }
                } else {
                    orderListLinearLayout.setVisibility(View.INVISIBLE);
                }

            } else {
                orderListLinearLayout.setVisibility(View.INVISIBLE);
            }
            // 消息列表

            if (mineDate.get("message") != null && !"null".equals(mineDate.get("message"))) {
                MessageBean messageBean = (MessageBean) mineDate.get("message");
                if (messageBean != null) {
                    messageListLinearLayout.setVisibility(View.VISIBLE);
                    if (messageBean.getPicUrlBitmap() != null) {
                        messageListImg.setImageURI(Constants.PICTURE_BASE_URL + messageBean.getPicUrlBitmap());
                    }
                    if (messageBean.getTitle() != null) {
                        messageListTitle.setText(messageBean.getTitle());
                    }
                    if (messageBean.getCompleteFlag() != null && messageBean.getTaskStatus() != null) {

                        switch (mTabActivity.getUserTaskStatus(messageBean.getCompleteFlag(), messageBean.getTaskStatus())) {
                            case 0:
                                messageListTaskState.setText(getString(R.string.task_share_start));
                                break;
                            case 1:
                                messageListTaskState.setText(getString(R.string.string_shared_task_text));
                                break;
                            case 2:
                                messageListTaskState.setText(getString(R.string.finish));
                                break;
                            default:
                                break;
                        }

                    }
                } else {
                    messageListLinearLayout.setVisibility(View.INVISIBLE);
                }
            } else {
                messageListLinearLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 我的页面数据初始化
     *
     * @param userId
     */
    public void initMineData(final String userId) {
        if( mTabActivity.mCurrentTab==3) {
            mTabActivity.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id", userId));
                    // 获取响应的结果信息
                    String json = mTabActivity.getPostData(Constants.URL_USER_MY, params);
                    Log.d(TAG, "=== MineFragment#initMineData() json = " + json);

                    if ("Exception".equals(json)) {
                        if( mTabActivity.mCurrentTab==3) {

                            mTabActivity.dismissProgressDialog();
                        }
                        return;
                    }
                    //JSON的解析过程
                    boolean result = false;
                    if (json != null) {
                        JSONObject jsonObject = new JSONObject(json);
                        Log.d(TAG, "=== MineFragment#initMineData()  jsonObject = " + jsonObject);
                        result = (boolean) jsonObject.get("success");
                        if (result) {
                            mineDate = new HashMap<String, Objects>();
                            String resultMsg = jsonObject.get("msg").toString();
                            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
                            if (dataJsonObject != null) {
                                Log.d(TAG, "=== MineFragment#initMineData()  dataJsonObject = " + dataJsonObject.toString());

                                // 获取排名
                                String sortMine = dataJsonObject.getString("sort");
                                if (sortMine == null || "null".equals(sortMine)) {
                                    Log.d(TAG, "=== MineFragment#initMineData()  sortMine = " + sortMine);
                                    sortMine = "";
                                }
                                mineDate.put("sort", sortMine);

                                // 获取用户信息
                                User userBean = null;
                                if (dataJsonObject.getString("user") != null && !dataJsonObject.getString("user").equals("null")) {
                                    JSONObject userJsonObject = (JSONObject) dataJsonObject.get("user");
                                    Log.d(TAG, "=== MineFragment#initMineData() userJsonObject = " + userJsonObject);
                                    if (userJsonObject != null) {
                                        userBean = new User();

                                        //页面显示昵称
                                        if ((userJsonObject.getString("nickname") != null) && (!"null".equals(userJsonObject.getString("nickname")))) {
                                            userBean.setNickname(userJsonObject.getString("nickname"));
                                        }
                                        //UserType
                                        userBean.setUserType(userJsonObject.getString("userType"));

                                        //页面显示鲜花数
                                        userBean.setScore(userJsonObject.getInt("score"));
                                        if (!"null".equals(userJsonObject.getString("headPicUrl"))) {
                                            userBean.setHeadPicBitmap(userJsonObject.getString("headPicUrl"));
                                        }
                                    }
                                }
                                mineDate.put("user", userBean);

                                // 获取订单
                                OrderBean orderBean = null;
                                if (dataJsonObject.getString("order") != null && !dataJsonObject.getString("order").equals("null")) {
                                    JSONArray orderJSONArray = (JSONArray) dataJsonObject.get("order");
                                    if (orderJSONArray != null && orderJSONArray.length() > 0) {
                                        JSONObject orderJsonObject = (JSONObject) orderJSONArray.get(0);
                                        orderBean = JsonUtils.jsonStrToBean(orderJsonObject.toString(), OrderBean.class);
                                        if ((orderJsonObject.getString("goodsPicture") != null) && (!"null".equals(orderJsonObject.getString("goodsPicture")))) {
                                            orderBean.setGoodsPictureBitmap(orderJsonObject.getString("goodsPicture"));
                                        }
                                    }
                                }
                                mineDate.put("order", orderBean);

                                // 获取消息
                                MessageBean messageBean = null;
                                if (dataJsonObject.getString("message") != null && !dataJsonObject.getString("message").equals("null")) {

                                    JSONArray messageJsonArray = (JSONArray) dataJsonObject.get("message");
                                    if (messageJsonArray != null && messageJsonArray.length() > 0) {
                                        JSONObject messageJsonObject = (JSONObject) messageJsonArray.get(0);
                                        messageBean = JsonUtils.jsonStrToBean(messageJsonObject.toString(), MessageBean.class);
                                        if ((messageJsonObject.getString("picUrl") != null) && (!"null".equals(messageJsonObject.getString("picUrl")))) {
                                            messageBean.setPicUrlBitmap(messageJsonObject.getString("picUrl"));
                                        }

                                        if ((messageJsonObject.getString("name") != null) && (!"null".equals(messageJsonObject.getString("name")))) {
                                            messageBean.setTitle(messageJsonObject.getString("name"));
                                        }
                                    }
                                }
                                mineDate.put("message", messageBean);

                                // 获取排名情况
                                JSONArray orderScoreSumJsonArray = (JSONArray) dataJsonObject.get("orderScoreSum");

                                if (orderScoreSumJsonArray != null) {
                                    int arrayLength = orderScoreSumJsonArray.length();
                                    for (int i = 0; i < arrayLength; i++) {
                                        JSONObject orderScoreSumJsonObject = (JSONObject) orderScoreSumJsonArray.get(i);
                                        if (orderScoreSumJsonObject != null) {
                                            OrderScoreSumBean orderScoreSumBean = null;
                                            Log.d(TAG, "=== MineFragment#initMineData()  orderScoreSumJsonObject = " + orderScoreSumJsonObject.toString());
                                            orderScoreSumBean = JsonUtils.jsonStrToBean(orderScoreSumJsonObject.toString(), OrderScoreSumBean.class);
                                            Log.d(TAG, "=== MineFragment#initMineData()  orderScoreSumBean = " + "i" + orderScoreSumBean.getHeadPicUrl());
                                            if (!"null".equals(orderScoreSumJsonObject.getString("headPicUrl"))) {
                                                orderScoreSumBean.setHeadPicBitmap(orderScoreSumJsonObject.getString("headPicUrl"));
                                            }
                                            mineDate.put("orderScoreSum" + i, orderScoreSumBean);
                                        }
                                    }
                                }
                                // 获取元宝数
                                String acerMine = dataJsonObject.getString("acer");
                                mineDate.put("acer", acerMine);

                                // 获取strawberry数
                                String strawberryMine = dataJsonObject.getString("strawberry");
                                mineDate.put("strawberry", strawberryMine);

                                // 获取enterAcer数
                                String enterAcer = dataJsonObject.getString("enterAcer");
                                mineDate.put("enterAcer", enterAcer);
                            }
                            //登录成功后的处理
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //登录异常后的处理
                    mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                }
            }
        }).start();
    }

    /**
     * 登录成功后，显示我的页面
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null != data && !"".equals(data)) {
            unLoginText.setVisibility(View.GONE);
            mineLayout.setVisibility(View.VISIBLE);
        }
    }
}