package com.fujisoft.campaign.bean;

/**
 * 排行榜Bean
 */

public class RankingBean {
    private String rowNo;
    private String scoreSum;
    private String id;
    private String headPicUrl;
    private String headPicBitmap;
    private String nickname;

    public RankingBean(String rowNo, String scoreSum, String id, String headPicUrl, String nickname) {
        this.rowNo = rowNo;
        this.scoreSum = scoreSum;
        this.id = id;
        this.headPicUrl = headPicUrl;
        this.nickname = nickname;
    }


    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }

    public String getScoreSum() {
        return scoreSum;
    }

    public void setScoreSum(String scoreSum) {
        this.scoreSum = scoreSum;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadPicBitmap() {
        return headPicBitmap;
    }

    public void setHeadPicBitmap(String headPicBitmap) {
        this.headPicBitmap = headPicBitmap;
    }


}
