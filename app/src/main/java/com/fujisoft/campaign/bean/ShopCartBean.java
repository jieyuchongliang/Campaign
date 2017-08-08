package com.fujisoft.campaign.bean;

import java.util.List;

/**
 * Created by 860115003 on 2017/2/10.
 * 购物车详情
 */

public class ShopCartBean {

    private ShopCartBean.ShopCartData data;
    private String msg;
    private Boolean success;

    public ShopCartData getData() {
        return data;
    }

    public void setData(ShopCartData data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static class ShopCartData{
        private String cartNum;
        private String orderStatus;
        private Address address;
        private PayInfo payInfo;
        private List<Goods> list;
        private String money;
        public String getCartNum() {
            return cartNum;
        }

        public void setCartNum(String cartNum) {
            this.cartNum = cartNum;
        }
        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public List<Goods> getList() {
            return list;
        }

        public void setList(List<Goods> list) {
            this.list = list;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public PayInfo getPayInfo() {
            return payInfo;
        }

        public void setPayInfo(PayInfo payInfo) {
            this.payInfo = payInfo;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        @Override
        public String toString() {
            return "ShopCartData{" +
                    "address=" + address +
                    ", list=" + list +
                    ", money='" + money + '\'' +
                    '}';
        }
    }

    public static class PayInfo{
        private String appid;
        private String noncestr;
        private String partnerid;
        private String prepayid;
        private String sign;
        private String timestamp;
        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }


    public static class Goods{
        private String cartId;
        private String goodsLogo;
        private String goodsName;
        private int scorePrice;
        private int goodsCnt;
        public String getGoodsLogo() {
            return goodsLogo;
        }
        public String getCartId() {
            return cartId;
        }

        public void setCartId(String cartId) {
            this.cartId = cartId;
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

        public int getScorePrice() {
            return scorePrice;
        }

        public void setScorePrice(int scorePrice) {
            this.scorePrice = scorePrice;
        }

        public int getGoodsCnt() {
            return goodsCnt;
        }

        public void setGoodsCnt(int goodsCnt) {
            this.goodsCnt = goodsCnt;
        }
        @Override
        public String toString() {
            return "Goods{" +
                    "goodsLogo='" + goodsLogo + '\'' +
                    ", goodsName='" + goodsName + '\'' +
                    ", scorePrice='" + scorePrice + '\'' +
                    ", goodsCnt='" + goodsCnt + '\'' +
                    '}';
        }
    }

    public  static class Address{
        private String id;
        private String name;
        private String price;
        private String phone;
        private String province;
        private String city;
        private String region;
        private String street;
        private String address;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getPrice() {
            return price;
        }


        public void setPrice(String price) {
            this.price = price;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getAddress() {
            //return this.province + "> " + this.city + "> " + this.region + "> "+ this.street;

            return this.province + this.city + this.region +this.street;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "id='" + id + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", region='" + region + '\'' +
                    ", street='" + street + '\'' +
                    ", address='" + address + '\'' +

                    ", price='" + price + '\'' +

                    ", name='" + name + '\'' +

                    ", phone='" + phone + '\'' +
                    '}';
        }
    }
}