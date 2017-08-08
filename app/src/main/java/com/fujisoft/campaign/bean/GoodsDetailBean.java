package com.fujisoft.campaign.bean;

import java.util.List;

/**
 * 商品详情用Bean.
 */
public class GoodsDetailBean {
    private String msg;
    private Boolean success;
    private GoodsData data;

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

    public GoodsData getData() {
        return data;
    }

    public void setData(GoodsData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GoodsDetailBean{" +
                "msg='" + msg + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }

    public static class GoodsData{
        private Detail goods;
        private List<String> goodsPic;
        private List<GoodsColor> goodsColor;
        private List<GoodsStandards> goodsStandards;
        private Integer goodsStandardsCount;

        public Integer getGoodsStandardsCount() {
            return goodsStandardsCount;
        }

        public void setGoodsStandardsCount(Integer goodsStandardsCount) {
            this.goodsStandardsCount = goodsStandardsCount;
        }

        public Detail getGoods() {
            return goods;
        }

        public void setGoods(Detail goods) {
            this.goods = goods;
        }

        public List<String> getGoodsPic() {
            return goodsPic;
        }

        public void setGoodsPic(List<String> goodsPic) {
            this.goodsPic = goodsPic;
        }

        public List<GoodsColor> getGoodsColor() {
            return goodsColor;
        }

        public void setGoodsColor(List<GoodsColor> goodsColor) {
            this.goodsColor = goodsColor;
        }

        public List<GoodsStandards> getGoodsStandards() {
            return goodsStandards;
        }

        public void setGoodsStandards(List<GoodsStandards> goodsStandards) {
            this.goodsStandards = goodsStandards;
        }

        @Override
        public String toString() {
            return "GoodsData{" +
                    "goods=" + goods +
                 ", goodsPic=" + goodsPic +
                    ", goodsColor=" + goodsColor +
                    ", goodsStandards=" + goodsStandards +
                    '}';
        }
    }

    public static class  Detail{
        private String goodsId;
        private String goodsName;
        private String goodsPicture;
        private String scorePrice;
        private String goodsDes;
        private int goodsQuantity;

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public int getGoodsQuantity() {
            return goodsQuantity;
        }

        public void setGoodsQuantity(int goodsQuantity) {
            this.goodsQuantity = goodsQuantity;
        }

        public String getgoodsPicture() {
            return goodsPicture;
        }

        public String getScorePrice() {
            return scorePrice;
        }

        public void setScorePrice(String scorePrice) {
            this.scorePrice = scorePrice;
        }

        public void setgoodsPicture(String goodsLogo) {
            this.goodsPicture = goodsPicture;
        }

        public String getGoodsDes() {
            return goodsDes;
        }

        public void setGoodsDes(String goodsDes) {
            this.goodsDes = goodsDes;
        }

        @Override
        public String toString() {
            return "Detail{" +
                    "goodsId='" + goodsId + '\'' +
                    ", goodsName='" + goodsName + '\'' +
                    ", goodsDes='" + goodsDes + '\'' +
                    ", goodsQuantity='" + goodsQuantity + '\'' +
                    '}';
        }
    }

    public static class GoodsStandards{
        private String standardZh;
        private String  standardEn;
        private List<String> standardDec;
        public String getStandardZh() {
            return standardZh;
        }

        public void setStandardZh(String standardZh) {
            this.standardZh = standardZh;
        }

        public String getStandardEn() {
            return standardEn;
        }

        public void setStandardEn(String standardEn) {
            this.standardEn = standardEn;
        }

        public  List<String> getStandardDec() {
            return standardDec;
        }

        public void setStandardDec( List<String> standardDec) {
            this.standardDec = standardDec;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"standardZh\":\"" + standardZh + '\"' +
                    ", \"standardEn\":\"" + standardEn + '\"' +
                    ", \"standardDec\":\"" + standardDec + "\"" + '}';
        }
    }

    public static class GoodsColor{
        private String textZh;
        private String textEn;
        private String color;

        public String getTextZh() {
            return textZh;
        }

        public void setTextZh(String textZh) {
            this.textZh = textZh;
        }

        public String getTextEn() {
            return textEn;
        }

        public void setTextEn(String textEn) {
            this.textEn = textEn;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"textZh\":\"" + textZh + '\"' +
                    ", \"textEn\":\"" + textEn + '\"' +
                    ", \"color\":\"" + color + '\"' + '}';
        }
    }

    public static class Standards{
        private String standardZh;
        private String  standardEn;
        private String standardDec;

        public String getStandardZh() {
            return standardZh;
        }

        public void setStandardZh(String standardZh) {
            this.standardZh = standardZh;
        }

        public String getStandardEn() {
            return standardEn;
        }

        public void setStandardEn(String standardEn) {
            this.standardEn = standardEn;
        }

        public String getStandardDec() {
            return standardDec;
        }

        public void setStandardDec(String standardDec) {
            this.standardDec = standardDec;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"standardZh\":\"" + standardZh + '\"' +
                    ", \"standardEn\":\"" + standardEn + '\"' +
                    ", \"standardDec\":\"" + standardDec + '\"' + '}';
        }
    }
}