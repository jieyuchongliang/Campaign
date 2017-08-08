package com.fujisoft.campaign.bean;

/**
 * Created by 860116014 on 2017/2/8.
 */

public class RechargeRecordBean {

    private String price;
    private String payWay;
    private String invoice;
    private String time1;
    private String time2;
    private String priceOrderId;

    public RechargeRecordBean(String price, String payWay, String invoice, String time1, String time2, String priceOrderId) {
        this.price = price;
        this.payWay = payWay;
        this.invoice = invoice;
        this.time1 = time1;
        this.time2 = time2;
        this.priceOrderId = priceOrderId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getTime1() {
        return time1;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public String getPriceOrderId() {
        return priceOrderId;
    }

    public void setPriceOrderId(String priceOrderId) {
        this.priceOrderId = priceOrderId;
    }
}
