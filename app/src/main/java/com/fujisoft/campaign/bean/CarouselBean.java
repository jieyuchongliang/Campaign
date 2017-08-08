package com.fujisoft.campaign.bean;

/**
 * 轮播控件Bean
 */
public class CarouselBean {
    private String id;
    private String url;
    private String createTime;

    public CarouselBean(String id, String createTime) {
        this.id = id;
        this.createTime = createTime;
    }

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
