package com.fujisoft.campaign.bean;

import java.util.List;

/**
 * Created by 860115003 on 2017/2/6.
 * 鲜花商城数据模型
 */

public class MarketData {
    private MarketGoods data;
    private String msg;
    private Boolean success;

    public MarketGoods getData() {
        return data;
    }

    public void setData(MarketGoods data) {
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

    @Override
    public String toString() {
        return "MarketData{" +
                "data=" + data +
                ", msg='" + msg + '\'' +
                ", success=" + success +
                '}';
    }

    public static class MarketGoods{
        private List<Advert> advert;
        private List<Goods> goodList;
        private List<GoodsType> goodsType;
        private Integer totalNum;
        private User user;

        public List<Advert> getAdvert() {
            return advert;
        }

        public void setAdvert(List<Advert> advert) {
            this.advert = advert;
        }

        public List<Goods> getGoodList() {
            return goodList;
        }

        public void setGoodList(List<Goods> goodList) {
            this.goodList = goodList;
        }

        public List<GoodsType> getGoodsType() {
            return goodsType;
        }

        public void setGoodsType(List<GoodsType> goodsType) {
            this.goodsType = goodsType;
        }

        public Integer getTotalNum() {
            return totalNum;
        }

        public void setTotalNum(Integer totalNum) {
            this.totalNum = totalNum;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "MarketGoods{" +
                    "advert=" + advert +
                    ", goodsList=" + goodList +
                    ", goodsType=" + goodsType +
                    ", totalNum=" + totalNum +
                    ", user=" + user +
                    '}';
        }
    }

    /***
     * 广告
     */
    public static class Advert{
        private String id;
        private String url;
        private String linkUrl;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Advert{" +
                    "id='" + id + '\'' +
                    ", url='" + url + '\'' +
                    ", linkUrl='" + linkUrl + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    /**
     * 商品类
     */
    public static class Goods{
        private String goodsId;
        private String goodsTypeId;
        private String goodsName;
        private String scorePrice;
        private String goodsLogo;
        private String goodsColor;
        private String goodsDes;
        private String goodsStandards;
        private String saleCount;
        private String scoreJiang;
        private String unit;
        private String goodsQuantity;
        public String getGoodsId() {
            return goodsId;
        }

        public String getGoodsQuantity() {
            return goodsQuantity;
        }
        public void setGoodsQuantity(String goodsQuantity) {
            this.goodsQuantity = goodsQuantity;
        }
        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getGoodsTypeId() {
            return goodsTypeId;
        }

        public void setGoodsTypeId(String goodsTypeId) {
            this.goodsTypeId = goodsTypeId;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getScorePrice() {
            return scorePrice;
        }

        public void setScorePrice(String scorePrice) {
            this.scorePrice = scorePrice;
        }

        public String getGoodsLogo() {
            return goodsLogo;
        }

        public void setGoodsLogo(String goodsLogo) {
            this.goodsLogo = goodsLogo;
        }

        public String getGoodsColor() {
            return goodsColor;
        }

        public void setGoodsColor(String goodsColor) {
            this.goodsColor = goodsColor;
        }

        public String getGoodsDes() {
            return goodsDes;
        }

        public void setGoodsDes(String goodsDes) {
            this.goodsDes = goodsDes;
        }

        public String getGoodsStandards() {
            return goodsStandards;
        }

        public void setGoodsStandards(String goodsStandards) {
            this.goodsStandards = goodsStandards;
        }

        public String getSaleCount() {
            return saleCount;
        }

        public void setSaleCount(String saleCount) {
            this.saleCount = saleCount;
        }

        public String getScoreJiang() {
            return scoreJiang;
        }

        public void setScoreJiang(String scoreJiang) {
            this.scoreJiang = scoreJiang;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
        @Override
        public String toString() {
            return "Goods{" +
                    "goodsId='" + goodsId + '\'' +
                    ", goodsTypeId='" + goodsTypeId + '\'' +
                    ", goodsName='" + goodsName + '\'' +
                    ", scorePrice='" + scorePrice + '\'' +
                    ", goodsLogo='" + goodsLogo + '\'' +
                    ", goodsColor='" + goodsColor + '\'' +
                    ", goodsDes='" + goodsDes + '\'' +
                    ", goodsStandards='" + goodsStandards + '\'' +
                    ", saleCount='" + saleCount + '\'' +
                    ", scoreJiang='" + scoreJiang + '\'' +
                    ", unit='" + unit + '\'' +
                    '}';
        }
    }

    /**
     * 商品类型
     */
    public static class GoodsType{

        private String goodsTypeId;
        private String goodsTypeName;
        private String goodsAdsImg;
        private String goodsTypeImg;
        private String goodsTypeSub;

        public GoodsType(String goodsTypeId,String goodsTypeName){
            this.goodsTypeId = goodsTypeId;
            this.goodsTypeName = goodsTypeName;
        }

        public String getGoodsTypeId() {
            return goodsTypeId;
        }

        public void setGoodsTypeId(String goodsTypeId) {
            this.goodsTypeId = goodsTypeId;
        }

        public String getGoodsTypeName() {
            return goodsTypeName;
        }

        public void setGoodsTypeName(String goodsTypeName) {
            this.goodsTypeName = goodsTypeName;
        }

        public String getGoodsAdsImg() {
            return goodsAdsImg;
        }

        public void setGoodsAdsImg(String goodsAdsImg) {
            this.goodsAdsImg = goodsAdsImg;
        }

        public String getGoodsTypeImg() {
            return goodsTypeImg;
        }

        public void setGoodsTypeImg(String goodsTypeImg) {
            this.goodsTypeImg = goodsTypeImg;
        }

        public String getGoodsTypeSub() {
            return goodsTypeSub;
        }

        public void setGoodsTypeSub(String goodsTypeSub) {
            this.goodsTypeSub = goodsTypeSub;
        }

        @Override
        public String toString() {
            return "GoodsType{" +
                    "goodsTypeId='" + goodsTypeId + '\'' +
                    ", goodsTypeName='" + goodsTypeName + '\'' +
                    ", goodsAdsImg='" + goodsAdsImg + '\'' +
                    ", goodsTypeImg='" + goodsTypeImg + '\'' +
                    ", goodsTypeSub='" + goodsTypeSub + '\'' +
                    '}';
        }
    }
    /**
     * 用户
     */
    public static class User{
        private String id;
        private String score;
        private String scoreSum;
        private String contributeScore;
        private String headPicUrl;
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadPicUrl() {
            return headPicUrl;
        }

        public void setHeadPicUrl(String headPicUrl) {
            this.headPicUrl = headPicUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getScoreSum() {
            return scoreSum;
        }

        public void setScoreSum(String scoreSum) {
            this.scoreSum = scoreSum;
        }

        public String getContributeScore() {
            return contributeScore;
        }

        public void setContributeScore(String contributeScore) {
            this.contributeScore = contributeScore;
        }
    }
}
