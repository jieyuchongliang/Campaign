package com.fujisoft.campaign.bean;

/**
 * 我的画面用鲜花总数排名信息
 */
public class OrderScoreSumBean {

    private String rowNo;
    private String id;
    private String headPicUrl;
    private String headPicBitmap;
    private String nickname;

    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadPicUrl() {
        return headPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        this.headPicUrl = headPicUrl;
    }

    public String getHeadPicBitmap() {
        return headPicBitmap;
    }

    public void setHeadPicBitmap(String headPicBitmap) {
        this.headPicBitmap = headPicBitmap;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
