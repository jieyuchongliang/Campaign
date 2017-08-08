package com.fujisoft.campaign.service;

import com.fujisoft.campaign.bean.ShopCartBean;
import com.fujisoft.campaign.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by 860115003 on 2017/2/8.
 * 购物车
 */
public interface ShopCartService {

    @GET(Constants.URL_MALL_SHOP_CART)
    Call<ShopCartBean> getCartInfo(
            @Query("id") String id
    );

    @GET(Constants.URL_DEL_CART)
    Call<ShopCartBean> delCartInfo(
            @Query("id") String id,
            @Query("cartId") String cartId
    );

    @GET(Constants.URL_MALL_ORDER)
    Call<ShopCartBean> cartBalance(
            @Query("id") String id,
            @Query("scores") String scores,
            @Query("addId") String addId,
            @Query("payWay") String payWay
    );

    @GET(Constants.URL_MALL_RE_ORDER)
    Call<ShopCartBean> reOrder(
            @Query("id") String id,
            @Query("scores") String scores
    );

    @GET(Constants.URL_MALL_CHECK_ORDER)
    Call<ShopCartBean> checkOrder(
            @Query("orderCode") String orderCode
    );
}
