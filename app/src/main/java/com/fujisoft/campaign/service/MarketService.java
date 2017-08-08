package com.fujisoft.campaign.service;

import com.fujisoft.campaign.bean.GoodsDetailBean;
import com.fujisoft.campaign.bean.MarketData;
import com.fujisoft.campaign.bean.ShopCartBean;
import com.fujisoft.campaign.utils.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by 860115003 on 2017/2/4.
 */

public interface MarketService {
    @GET(Constants.MARKET_LIST)
    Call<MarketData> marketList(
              @Query("id") String userId//用户id
            , @Query("pageNum") int pageNum//分页
            , @Query("orderCol") String orderCol // 排序方法
            , @Query("goodsTypeId") String goodsTypeId // 商品类别
             );

    @GET(Constants.GOODS_DETAIL)
    Call<GoodsDetailBean> getGoodsDetail(@Query("id") String userId,
                                         @Query("goodsId") String goodsId);

    @GET(Constants.ADD_Cart)
    Call<GoodsDetailBean> addToCart(
            @Query("id") String id
            , @Query("goodsCnt") int goodsCnt
            , @Query("goodsId") String goodsId
            , @Query("goodsStandards") String goodsStandards
            , @Query("goodsColor") String goodsColor
            , @Query("goodsName") String goodsName
    );

    @GET(Constants.URL_GET_CART_NUM)
    Call<ShopCartBean> getCardOrderNum(
            @Query("id") String id
    );
}
