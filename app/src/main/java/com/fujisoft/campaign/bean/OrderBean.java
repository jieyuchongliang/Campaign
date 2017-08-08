package com.fujisoft.campaign.bean;


/**
 * 订单列表用订单Bean
 */
public class OrderBean {

    private String goodsPicture;
    private String goodsPictureBitmap;
    private String goodsLogo;
    private String goodsName;
    private String ScorePrice;
    private String totalScorePrice;
    private String goodsQuantity;
    private String orderCode;
    private String orderId;

    public String getOrderExpress() {
        return orderExpress;
    }

    public void setOrderExpress(String orderExpress) {
        this.orderExpress = orderExpress;
    }

    private String orderExpress;

    private String orderStatus;
    private Object name;
    private Object phone;
    private Object address;

    public String getGoodsPicture() {
        return goodsPicture;
    }

    public void setGoodsPicture(String goodsPicture) {
        this.goodsPicture = goodsPicture;
    }

    public String getGoodsPictureBitmap() {
        return goodsPictureBitmap;
    }

    public void setGoodsPictureBitmap(String goodsPictureBitmap) {
        this.goodsPictureBitmap = goodsPictureBitmap;
    }

    public String getGoodsLogo() {
        return goodsLogo;
    }

    public void setGoodsLogo(String goodsLogo) {
        this.goodsLogo = goodsLogo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getScorePrice() {
        return ScorePrice;
    }

    public void setScorePrice(String ScorePrice) {
        this.ScorePrice = ScorePrice;
    }

    public String getTotalScorePrice() {
        return totalScorePrice;
    }

    public void setTotalScorePrice(String totalScorePrice) {
        this.totalScorePrice = totalScorePrice;
    }

    public String getGoodsQuantity() {
        return goodsQuantity;
    }

    public void setGoodsQuantity(String goodsQuantity) {
        this.goodsQuantity = goodsQuantity;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }
}
