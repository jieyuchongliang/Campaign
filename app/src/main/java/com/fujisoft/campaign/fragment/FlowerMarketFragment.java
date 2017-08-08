package com.fujisoft.campaign.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fujisoft.campaign.GoodsDetailActivity;
import com.fujisoft.campaign.LuckDrawActivity;
import com.fujisoft.campaign.R;
import com.fujisoft.campaign.TabMainActivity;
import com.fujisoft.campaign.adapter.GoodsGridAdapter;
import com.fujisoft.campaign.adapter.GoodsTypeAdapter;
import com.fujisoft.campaign.bean.MarketData;
import com.fujisoft.campaign.service.MarketService;
import com.fujisoft.campaign.utils.Constants;
import com.fujisoft.campaign.utils.Utils;
import com.fujisoft.campaign.view.CarouselView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fujisoft.campaign.utils.Utils.Retrofit;

/**
 * 鲜花商城
 */
public class FlowerMarketFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String PARAM_USER_ID = "userId";
    private View view;
    private CarouselView carousel;
    private TabMainActivity mTabActivity;
    private LayoutInflater mInflater;
    private String userId = null;
    private GoodsGridAdapter goodsGridAdapter;
    private String orderCol = null;
    private String goodsTypeId = null;
    List<MarketData.Advert> advertList = new ArrayList<>();
    List<MarketData.Goods> goodsList = new ArrayList<>();
    private MarketService marketService;
    private RecyclerView goodsListView;

    private int mCurrentSpinnerPos = 0;

    public FlowerMarketFragment() {
    }

    public static FlowerMarketFragment newInstance() {
        return new FlowerMarketFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != Retrofit) {
            marketService = Retrofit.create(MarketService.class);
        } else {
            Utils.getRetrofit(getContext());
            marketService = Retrofit.create(MarketService.class);
        }
        mTabActivity = (TabMainActivity) getActivity();
        if (getArguments() != null) {
            userId = getArguments().getString(PARAM_USER_ID);
        }
        mInflater = LayoutInflater.from(mTabActivity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_flower_market, container, false);
        // 轮播控件
        carousel = (CarouselView) view.findViewById(R.id.flower_market_carousel_view);

        carousel.setClickCallback(new CarouselView.ClickCallback() {
            @Override
            public void onClick(int id, int position) {
                //Toast.makeText(mTabActivity, "你点击了第" + position + "项", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                if (null != userId && !"".equals(userId)) {
                    intent.setClass(getContext(), LuckDrawActivity.class);
                    intent.putExtra(Constants.EXTRA_USER_ID, userId);
                } else {
                    mTabActivity.toLogin();
                    return;
                }
                startActivity(intent);
            }
        });
        initTabLayout();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = mTabActivity.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.EXTRA_USER_ID, "");
        pageNumber = 1;
        if (mTabActivity.mCurrentTab == 2) {
            mTabActivity.showProgressDialog();
        }
        dateLoad(pageNumber, orderCol, goodsTypeId, RefreshMode.Load);
    }

    /**
     * 刷新方式
     */
    public enum RefreshMode {
        Append,
        Reload,
        Load,
    }

    /**
     * 图片轮番
     *
     * @return
     */
    private CarouselView.Adapter carouselAdapter() {
        CarouselView.Adapter carouselAdapter = new CarouselView.Adapter() {
            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public View getView(int position) {
                View view = mInflater.inflate(R.layout.flower_market_carousel_item, null);
                SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.image);
                imageView.setImageURI(Constants.PICTURE_BASE_URL + advertList.get(position).getUrl());
                return view;
            }

            @Override
            public int getCount() {
                return advertList.size();
            }
        };
        return carouselAdapter;
    }

    /**
     * 筛选条件选择TabLayout
     */
    private void initTabLayout() {
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.flower_market_tab_layout);
        // 最新上架
        TabLayout.Tab newestTab = tabLayout.newTab().setText(R.string.flower_market_newest_commodity);
        tabLayout.addTab(newestTab);
        // 人气
        TabLayout.Tab popularityTab = tabLayout.newTab().setText(R.string.flower_market_popularity);
        tabLayout.addTab(popularityTab);
        // 鲜花
        TabLayout.Tab flowersTab = tabLayout.newTab().setText(R.string.flower_market_flowers);
        tabLayout.addTab(flowersTab);
        // 限量
        TabLayout.Tab limitTab = tabLayout.newTab().setText(R.string.flower_market_limit);
        tabLayout.addTab(limitTab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(ContextCompat.getColor(this.getContext(), R.color.flower_market_text_gray),
                ContextCompat.getColor(this.getContext(), R.color.flower_market_tab_selected));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        orderCol = "addTime";
                        break;
                    case 1:
                        orderCol = "buyCount";
                        break;
                    case 2:
                        orderCol = "scorePrice";
                        break;
                    case 3:
                        orderCol = "isLimited";
                        break;
                }
                pageNumber = 1;
                if (mTabActivity.mCurrentTab == 2) {
                    mTabActivity.showProgressDialog();
                }
                dateLoad(pageNumber, orderCol, goodsTypeId, RefreshMode.Reload);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        goodsListView = (RecyclerView) view.findViewById(R.id.flower_market_grid_view);
    }

    /**
     * 分类Spinner
     *
     * @param goodsTypeList
     */
    private void initSpinner(List<MarketData.GoodsType> goodsTypeList) {
        GoodsTypeAdapter goodsTypeAdapter = new GoodsTypeAdapter<MarketData.GoodsType>(goodsTypeList, R.layout.goods_type_spinner) {
            @Override
            public void bindView(ViewHolder holder, MarketData.GoodsType obj) {
                holder.setText(R.id.goodsType_Name, obj.getGoodsTypeName());
                holder.setText(R.id.goodsType_Id, obj.getGoodsTypeId());
            }
        };
        Spinner spinner = (Spinner) view.findViewById(R.id.flower_market_commodity_class_spinner);
        spinner.setAdapter(goodsTypeAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView goodsTypeIdView = (TextView) view.findViewById(R.id.goodsType_Id);
                goodsTypeIdView.setTextSize(14);
                goodsTypeId = goodsTypeIdView.getText().toString();
                pageNumber = 1;
                if (mCurrentSpinnerPos != position) {
                    if (mTabActivity.mCurrentTab == 2) {
                        mTabActivity.showProgressDialog();
                    }
                    dateLoad(pageNumber, orderCol, goodsTypeId, RefreshMode.Reload);
                }
                mCurrentSpinnerPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    SwipeRefreshLayout mSwipeRefreshLayout;
    private int lastVisibleItem;
    private int pageNumber = 1;

    /**
     * 商品信息显示View
     */
    private void initGoodsList() {
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
//        mSwipeRefreshLayout.setOnRefreshListener(this);
//        RecyclerView goodsListView = (RecyclerView) view.findViewById(R.id.flower_market_grid_view);
//        goodsGridAdapter = new GoodsGridAdapter(mTabActivity, goodsList);

        goodsGridAdapter = new GoodsGridAdapter(mTabActivity, goodsList);
        goodsListView.setAdapter(goodsGridAdapter);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mTabActivity, 2);
        goodsListView.setLayoutManager(gridLayoutManager);
        goodsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == recyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == goodsGridAdapter.getItemCount()) {
                    pageNumber = pageNumber + 1;
                    dateLoad(pageNumber, orderCol, goodsTypeId, RefreshMode.Append);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        goodsGridAdapter.setOnItemClickListener(new GoodsGridAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String goodsId) {
                Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
                intent.putExtra(Constants.EXTRA_GOODS_ID, goodsId);
                intent.putExtra(Constants.EXTRA_USER_ID, userId);
                startActivity(intent);
            }
        });
    }

    /**
     * 商城数据加载
     */
    public void dateLoad(final Integer pageNumber, String orderCol, String goodsTypeId, final FlowerMarketFragment.RefreshMode refreshMode) {

//        if( mTabActivity.mCurrentTab==2) {
//
//            mTabActivity.showProgressDialog();
//        }

        Call<MarketData> call = marketService.marketList(userId, pageNumber, orderCol, goodsTypeId);
        call.enqueue(new Callback<MarketData>() {
            @Override
            public void onResponse(Call<MarketData> call, Response<MarketData> response) {
                MarketData result = response.body();
                if (result != null) {
                    MarketData.MarketGoods ds = result.getData();
                    // 首页图片轮番
                    if (refreshMode == RefreshMode.Load) {
                        advertList = ds.getAdvert();
                        if (advertList != null) {
                            carousel.setAdapter(carouselAdapter());
                        }
                        // 用户信息
                        MarketData.User user = ds.getUser();
                        SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.flower_market_user_icon);
                        TextView textNickView = (TextView) view.findViewById(R.id.flower_market_user_alias);
                        TextView textBalView = (TextView) view.findViewById(R.id.flower_market_flower_balance);
                        if (user != null) {
                            imageView.setImageURI(Constants.PICTURE_BASE_URL + user.getHeadPicUrl());
                            textNickView.setText(user.getNickname());
                            textBalView.setText(user.getScore());
                            view.invalidate();
                        } else {
//                            imageView.setImageURI(Constants.PICTURE_BASE_URL + user.getHeadPicUrl());
                            imageView.setImageURI("");
                            textNickView.setText(getString(R.string.flower_market_tourist));
                            textBalView.setText("0");
                            view.invalidate();
                        }
                        // 商品分类
                        List<MarketData.GoodsType> goodsTypeList = ds.getGoodsType();
                        MarketData.GoodsType goodsType = new MarketData.GoodsType("000", "全部");
                        goodsTypeList.add(0, goodsType);
                        initSpinner(goodsTypeList);

                        // 商品列表
                        goodsList = ds.getGoodList();
                        if (goodsList != null) {
                            initGoodsList();
                        } else {
                            if (goodsGridAdapter != null) goodsGridAdapter.clear();
                        }
                    } else if (refreshMode == RefreshMode.Reload) {
                        // 商品列表
                        goodsList = ds.getGoodList();
                        if (goodsList != null) {
                            initGoodsList();
                        } else {
                            if (goodsGridAdapter != null) goodsGridAdapter.clear();
                        }
                    } else if (refreshMode == RefreshMode.Append) {
                        if (ds.getGoodList() != null) {
                            goodsGridAdapter.reloadData(ds.getGoodList());
                        }
                    }
                }
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (mTabActivity.mCurrentTab == 2) {
                    mTabActivity.dismissProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<MarketData> call, Throwable throwable) {
                Log.d("result", "网路链接失败!!");
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNumber = 1;
        dateLoad(pageNumber, orderCol, goodsTypeId, RefreshMode.Reload);
    }

}