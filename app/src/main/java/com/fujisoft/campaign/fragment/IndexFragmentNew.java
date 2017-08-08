package com.fujisoft.campaign.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.GoodsDetailActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.RankingFlowerActivity;
import com.fujisoft.campaign.TabMainActivity;
import com.fujisoft.campaign.TaskDetailsActivity;
import com.fujisoft.campaign.adapter.GoodsGridAdapterNew;
import com.fujisoft.campaign.adapter.MyRecyclerAdapterNew;
import com.fujisoft.campaign.bean.CarouselBean;
import com.fujisoft.campaign.bean.GoodBean;
import com.fujisoft.campaign.bean.NoticeBean;
import com.fujisoft.campaign.bean.Task;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;
import com.fujisoft.campaign.view.CarouselView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页页面
 */
public class IndexFragmentNew extends Fragment implements View.OnClickListener {
    private String TAG = "campaign";
    private int pageNum = 1;
    private int pageSum;
    private String userId = null;

    private TabMainActivity mTabActivity;
    private LayoutInflater mInflater;
    private View view;
    private MyRecyclerAdapterNew recycleAdapter;
    private GoodsGridAdapterNew recycleAdapterGood;

    private LinearLayoutManager mLinearLayoutManager;

    private List<Task> taskLists;

    private List<GoodBean> goodLists;

    private int requiredNum;
    private String userScore;             // 鲜花余额
    private String userContributeScore; // 贡献元宝数

    private CarouselView mCarouselView;
    private LinearLayout none_content;

    private LinearLayout mIndexRankingListLayout;
    private LinearLayout main_content;
    private GridLayoutManager gridLayoutManager;

    private LinearLayout mIndexButtonsLayout;

    private TextView mIndexFlowerBalanceTxt;
    private TextView mIndexSumContributionTxt;


    private ImageView mNonePic;

    private RecyclerView mRecyclerView;

    private List<CarouselBean> listCarouselBeans = new ArrayList<>();
    private List<NoticeBean> listNoticeBeans = new ArrayList<>();


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mTabActivity.mCurrentTab == 0) {
                mTabActivity.dismissProgressDialog();
            }
            switch (msg.what) {
                case Constants.CODE_EXECUTE_FAILURE:
                    mTabActivity.showErrorDialog(Constants.CODE_EXECUTE_FAILURE);

                    none_content.setVisibility(View.VISIBLE);
                    break;
                case Constants.CODE_EXECUTE_SUCCESS:
                    if (!isAdded()) {
                        return;
                    }
                    mIndexButtonsLayout.setVisibility(View.VISIBLE);
                    // 更新页面上的鲜花余额和贡献元宝数
                    if (null == getUserScore() || "null".equals(getUserScore())) {
                        mIndexFlowerBalanceTxt.setText(getString(R.string.index_flower_balance) + "0");
                    } else {
                        mIndexFlowerBalanceTxt.setText(getString(R.string.index_flower_balance) + getUserScore());
                    }
                    if (null == getUserContributeScore() || "null".equals(getUserContributeScore())) {
                        mIndexSumContributionTxt.setText(getString(R.string.index_contribution) + "0");
                    } else {
                        mIndexSumContributionTxt.setText(getString(R.string.index_contribution) + getUserContributeScore());
                    }
                    if ((taskLists != null && taskLists.size() > 0) || (goodLists != null && goodLists.size() > 0)) {
                        mNonePic.setVisibility(View.GONE);
                    }
                    mCarouselView.setAdapter(getCarouselAdapter());
                    mCarouselView.setClickCallback(new CarouselView.ClickCallback() {
                        @Override
                        public void onClick(int id, int position) {
//                            Toast.makeText(mTabActivity, "你点击了第" + position + "项", Toast.LENGTH_SHORT).show();
                        }
                    });
                    pageNum = 1;
                    if (taskLists != null) {
                        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mTabActivity, 1);
                        mRecyclerView.setLayoutManager(gridLayoutManager);
                        recycleAdapter = new MyRecyclerAdapterNew(mTabActivity, taskLists, goodLists, mRecyclerAdapterListener, 0, getRequiredNum());
                        mRecyclerView.setAdapter(recycleAdapter);
                        recycleAdapter.notifyDataSetChanged();
                    } else {
                        View header = LayoutInflater.from(mTabActivity).inflate(
                                R.layout.index_goods_header_x, mRecyclerView, false);
                        header.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mTabActivity, 2);
                        mRecyclerView.setLayoutManager(gridLayoutManager);
                        recycleAdapterGood = new GoodsGridAdapterNew(header, mTabActivity, goodLists);
                        recycleAdapterGood.setOnItemClickListener(mGoodClickListener);
                        mRecyclerView.setAdapter(recycleAdapterGood);
                        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                return recycleAdapterGood.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
                            }
                        });
                        recycleAdapterGood.notifyDataSetChanged();
                    }

                    none_content.setVisibility(View.INVISIBLE);
                    main_content.setVisibility(View.VISIBLE);
                    break;
                case Constants.CODE_EXECUTE_EXCEPTION:
                    Utils.showToast(mTabActivity, R.string.data_receive);
                    mTabActivity.setResult(Constants.CODE_EXECUTE_EXCEPTION);
                    none_content.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    GoodsGridAdapterNew.OnRecyclerViewItemClickListener mGoodClickListener = new GoodsGridAdapterNew.OnRecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View view, String goodsId) {
            Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
            intent.putExtra(Constants.EXTRA_GOODS_ID, goodsId);
            intent.putExtra(Constants.EXTRA_USER_ID, userId);
            startActivity(intent);
        }
    };

    public IndexFragmentNew() {
    }

    public static IndexFragmentNew newInstance() {
        return new IndexFragmentNew();
    }
    private OnNoticeLoadCallback mOnNoticeLoadCallback;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnNoticeLoadCallback = (OnNoticeLoadCallback) context;
//        listNoticeBeans.add(new NoticeBean("测试数据1"));
//        listNoticeBeans.add(new NoticeBean("测试数据2"));
//        listNoticeBeans.add(new NoticeBean("测试数据3"));
//        mOnNoticeLoadCallback.onDataCallback(listNoticeBeans);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabActivity = (TabMainActivity) getActivity();
        mInflater = LayoutInflater.from(mTabActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_index_new, container, false);
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTabActivity.mCurrentTab == 0) {
            mTabActivity.showProgressDialog();
        }
        SharedPreferences sharedPreferences = mTabActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
//        initViews();
        initData();
    }


    public List<Task> getTaskDataList() {
        return taskLists;
    }

    public void setTaskLists(List<Task> taskLists) {
        this.taskLists = taskLists;
    }

    public List<GoodBean> getGoodsList() {
        return goodLists;
    }

    public void setGoodsLists(List<GoodBean> goodLists) {
        this.goodLists = goodLists;
    }


    /**
     * 首页初始化，若未登录，则userId为null。显示可选的默认的任务列表数据
     */
    private void initData() {

        if (Utils.isConnect(mTabActivity)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = updateData(1, null);
                    taskLists = getTaskDataList();

                    goodLists = getGoodsList();

                    pageSum = getPageSum();
                    listCarouselBeans = getListCarouselBeans();
                    switch (result) {
                        case Constants.CODE_EXECUTE_SUCCESS:
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_SUCCESS);
                            break;
                        case Constants.CODE_EXECUTE_FAILURE:
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_FAILURE);
                            break;
                        case Constants.CODE_EXECUTE_EXCEPTION:
                            mHandler.sendEmptyMessage(Constants.CODE_EXECUTE_EXCEPTION);
                            break;
                    }
                }
            }).start();
        } else {
            Utils.showToast(mTabActivity, R.string.netWrong);

            none_content.setVisibility(View.VISIBLE);
            if (mTabActivity.mCurrentTab == 0) {
                mTabActivity.dismissProgressDialog();
            }
        }
    }

    private void initViews() {
        main_content = (LinearLayout) view.findViewById(R.id.main_content);

        // 轮播View
        mCarouselView = (CarouselView) view.findViewById(R.id.index_carousel_view);

        // 排行榜View
        mIndexRankingListLayout = (LinearLayout) view.findViewById(R.id.lay_index_ranking_list);
        mIndexRankingListLayout.setOnClickListener(this);

        // 排行榜、鲜花余额、贡献元宝数View
        mIndexButtonsLayout = (LinearLayout) view.findViewById(R.id.index_buttons);

        // 鲜花余额Text、贡献元宝数Text
        mIndexFlowerBalanceTxt = (TextView) view.findViewById(R.id.txt_index_flower_balance);
        mIndexSumContributionTxt = (TextView) view.findViewById(R.id.txt_index_sum_contribution);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.index_recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(mTabActivity, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //设置增加或删除条目的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        none_content = (LinearLayout) view.findViewById(R.id.none_content);
        mNonePic = (ImageView) view.findViewById(R.id.order_list_none_image);

    }

    public void updateDate() {
        (view.findViewById(R.id.appbar)).setVisibility(View.GONE);
    }

    public int updateData(int pageNumber, String keyword) {
        try {
            List<Task> taskLists = new ArrayList<>();

            List<GoodBean> goodLists = new ArrayList<>();

            List<CarouselBean> listCarouselBeans = new ArrayList<>();
            final List<NoticeBean> listNoticeBeans = new ArrayList<>();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pageNum", String.valueOf(pageNumber)));
            params.add(new BasicNameValuePair("id", userId));
            if (!TextUtils.isEmpty(keyword)) {
                params.add(new BasicNameValuePair("keyword", keyword));
            }
            // 获取响应的结果信息
            String json = mTabActivity.getPostData(Constants.URL_INDEX, params);
            Log.d(TAG, "=== IndexFragmentNew#updateData() json = " + json);
            // JSON的解析过程
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);//转换为JSONObject
                boolean result = jsonObject.getBoolean("success");
                if (result) {
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    //获取抽奖信息
                    JSONArray NoticeJsonArray = dataObject.getJSONArray("noticeList");
                    for (int m = 0; m < NoticeJsonArray.length(); m++) {
                        JSONObject NoticeJsonObject = NoticeJsonArray.getJSONObject(m);
                        NoticeBean noticeBean = new NoticeBean(NoticeJsonObject.get("title").toString());
                        listNoticeBeans.add(noticeBean);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mOnNoticeLoadCallback.onDataCallback(listNoticeBeans);
                        }
                    });
                    //setListNoticeBeans(listNoticeBeans);
                    //获取轮播图片
                    JSONArray carouselJsonArray = dataObject.getJSONArray("CarouselList");
                    for (int j = 0; j < carouselJsonArray.length(); j++) {
                        JSONObject carouselJsonObject = carouselJsonArray.getJSONObject(j);
                        CarouselBean carouselBean = new CarouselBean(carouselJsonObject.get("id").toString(), carouselJsonObject.get("createTime").toString());
                        if (!TextUtils.isEmpty(carouselJsonObject.get("url").toString())) {
                            carouselBean.setUrl(carouselJsonObject.get("url").toString());
                        } else {
                            carouselBean.setUrl(null);
                        }
                        listCarouselBeans.add(carouselBean);
                    }
                    setListCarouselBeans(listCarouselBeans);
                    pageSum = 1;

                    // 获取鲜花余额数
                    setUserScore(dataObject.get("userScore").toString());
                    // 获取贡献元宝数
                    setUserContributeScore(dataObject.get("userContributeScore").toString());

                    // 任务列表数据
                    JSONArray taskListArray = dataObject.getJSONArray("taskList");
                    if (taskListArray != null && taskListArray.length() > 0) {

                        Task mTask;
                        for (int i = 0; i < taskListArray.length(); i++) {
                            JSONObject taskData = taskListArray.getJSONObject(i);
                            mTask = new Task();
                            mTask.setTaskId(Integer.parseInt(taskData.get("id").toString()));
                            mTask.setTaskTitle(taskData.get("name").toString());
                            mTask.setTaskContent(taskData.get("content").toString());
                            mTask.setScore(Integer.parseInt(taskData.get("score").toString()));
                            mTask.setRequiredFlag(Integer.parseInt(taskData.get("requiredFlags").toString()));
                            mTask.setCompleteFlag(Integer.parseInt(taskData.get("completeFlag").toString()));
                            mTask.setTaskStatus(Integer.parseInt(taskData.get("taskStatus").toString()));
                            mTask.setShareWays(taskData.get("shareWay").toString());

                            mTask.setSubtitle(taskData.get("subtitle").toString());

                            if (!TextUtils.isEmpty(taskData.get("picUrl").toString())) {
                                mTask.setTaskPic(taskData.get("picUrl").toString());
                            } else {
                                mTask.setTaskPic(null);
                            }
                            taskLists.add(mTask);
                        }
                        setTaskLists(taskLists);
                    } else {
                        setTaskLists(null);
                    }
                    // 商品列表数据
                    JSONArray goodsListArray = dataObject.getJSONArray("goodsList");
                    if (goodsListArray != null && goodsListArray.length() > 0) {
                        GoodBean mGood;

                        for (int i = 0; i < goodsListArray.length(); i++) {
                            JSONObject goodsData = goodsListArray.getJSONObject(i);
                            mGood = new GoodBean();
                            mGood.setGoodsId(goodsData.get("goodsId").toString());
                            mGood.setGoodsName(goodsData.get("goodsName").toString());
                            Log.d("aaa", "=== IndexFragmentNew# updateData() goodsDes = " + goodsData.get("goodsDes").toString());
                            mGood.setGoodsDes(goodsData.get("goodsDes").toString());
                            mGood.setScorePrice(goodsData.get("scorePrice").toString());
                            mGood.setGoodsLogo(goodsData.get("goodsLogo").toString());
                            mGood.setGoodsQuantity(goodsData.get("goodsQuantity").toString());
                            goodLists.add(mGood);
                        }
                        setGoodsLists(goodLists);
                    }
                    // 成功后的处理
                    return Constants.CODE_EXECUTE_SUCCESS;
                } else {
                    // 失败后的处理
                    return Constants.CODE_EXECUTE_FAILURE;
                }
            } else {
                // 异常后的处理
                return Constants.CODE_EXECUTE_EXCEPTION;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 异常后的处理
            return Constants.CODE_EXECUTE_EXCEPTION;
        }
    }

    public List<CarouselBean> getListCarouselBeans() {
        return listCarouselBeans;
    }

    public void setListCarouselBeans(List<CarouselBean> listCarouselBeans) {
        this.listCarouselBeans = listCarouselBeans;
    }
    public int getPageSum() {
        return pageSum;
    }

    public String getUserScore() {
        return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    public String getUserContributeScore() {
        return userContributeScore;
    }

    public void setUserContributeScore(String userContributeScore) {
        this.userContributeScore = userContributeScore;
    }

    public void setRequiredNum(int requiredNum) {
        this.requiredNum = requiredNum;
    }

    public int getRequiredNum() {
        return requiredNum;
    }

    private CarouselView.Adapter getCarouselAdapter() {
        CarouselView.Adapter carouselAdapter = new CarouselView.Adapter() {
            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public View getView(int position) {
                View view = mInflater.inflate(R.layout.flower_market_carousel_item, null);
                SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.image);
                imageView.setImageURI(Constants.PICTURE_BASE_URL + listCarouselBeans.get(position).getUrl());
                return view;
            }

            @Override
            public int getCount() {
                return listCarouselBeans.size();
            }
        };
        return carouselAdapter;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lay_index_ranking_list) {// 进入排行榜页面
            if (null != userId && !"".equals(userId)) {
                Intent rankIntent = new Intent();
                rankIntent.putExtra(Constants.EXTRA_USER_ID, userId);
                rankIntent.setClass(mTabActivity, RankingFlowerActivity.class);
                startActivity(rankIntent);
            } else {
                mTabActivity.toLogin();
            }
        }
    }

    private MyRecyclerAdapterNew.onItemClickListener mRecyclerAdapterListener = new MyRecyclerAdapterNew.onItemClickListener() {
        @Override
        public void onItemClick(int taskId, int completeFlag, int taskStatus) {
//            if (null != userId && !"".equals(userId)) {
            Intent taskDetailsIntent = new Intent(mTabActivity, TaskDetailsActivity.class);
            taskDetailsIntent.putExtra(Constants.EXTRA_TASK_ID, String.valueOf(taskId));
            taskDetailsIntent.putExtra(Constants.EXTRA_USER_ID, userId);
            taskDetailsIntent.putExtra("CompleteFlag", String.valueOf(completeFlag));
            taskDetailsIntent.putExtra("taskStatus", String.valueOf(taskStatus));
            startActivityForResult(taskDetailsIntent, Constants.CODE_REQUEST_TASK_DETAILS);
//            } else {
//                mTabActivity.toLogin();
//            }
        }

        @Override
        public void onShareClick(int taskId, int completeFlag, int taskStatus, String taskTitle, String taskContent, String taskPicUrl, String shareWay) {

            if (null != userId && !"".equals(userId)) {

                switch (mTabActivity.getUserTaskStatus(String.valueOf(completeFlag), String.valueOf(taskStatus))) {
                    case 0:
                        mTabActivity.showShare(taskTitle, taskContent, taskPicUrl, shareWay, userId, String.valueOf(taskId));
                        break;
                    case 1:
                        mTabActivity.showMsg(getString(R.string.task_share_end), Toast.LENGTH_SHORT);
                        break;
                    case 2:
                        mTabActivity.showMsg(getString(R.string.task_end), Toast.LENGTH_SHORT);
                        break;
                    default:
                        break;
                }
            } else {
                mTabActivity.toLogin();
            }
        }

        @Override
        public void onOnPicClick(Uri uri) {
            if (null != userId && !"".equals(userId)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                mTabActivity.toLogin();
            }
        }
    };


    public interface OnNoticeLoadCallback {
        /**
         * 传递获取到的数据
         * @param data
         */
        void onDataCallback(List<NoticeBean> data);
    }

}