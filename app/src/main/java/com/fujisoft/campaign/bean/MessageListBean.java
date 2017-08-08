package com.fujisoft.campaign.bean;

/**
 * 消息列表用Bean
 */
public class MessageListBean {
    private int id;
    private String name;
    private String content;
    private String picUrl;
    private String requiredFlags;
    private String flower;
    private String createTime;
    private String picBitmap;
    private String shareWay;
    private int completeFlag;
    private int taskStatus;
    private String userId;
    private String invitationId;
    private String state;
    private String enterpriseId;
    private String enterpriseName;

    public MessageListBean(int id, String name, String content, String requiredFlags, String picUrl,
                           String flower, String shareWay, int completeFlag, int taskStatus, String subtitle,
                           String userId, String invitationId, String state, String enterpriseId, String enterpriseName) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.requiredFlags = requiredFlags;
        this.picUrl = picUrl;
        this.flower = flower;
        this.shareWay = shareWay;
        this.completeFlag = completeFlag;
        this.taskStatus = taskStatus;
        this.subtitle = subtitle;
        this.userId = userId;
        this.invitationId = invitationId;
        this.state = state;
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
    }
    public MessageListBean(int id, String name, String content, String requiredFlags, String picUrl,
                           String flower, String shareWay, int completeFlag, int taskStatus, String subtitle) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.requiredFlags = requiredFlags;
        this.picUrl = picUrl;
        this.flower = flower;
        this.shareWay = shareWay;
        this.completeFlag = completeFlag;
        this.taskStatus = taskStatus;
        this.subtitle = subtitle;
    }

    public MessageListBean(String userId, String invitationId, String state, String enterpriseId, String enterpriseName) {
        this.userId = userId;
        this.invitationId = invitationId;
        this.state = state;
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
    }

    // 副标题
    private String subtitle;

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getRequiredFlags() {
        return requiredFlags;
    }

    public void setRequiredFlags(String requiredFlags) {
        this.requiredFlags = requiredFlags;
    }

    public String getFlower() {
        return flower;
    }

    public void setFlower(String flower) {
        this.flower = flower;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPicBitmap() {
        return picBitmap;
    }

    public void setPicBitmap(String picBitmap) {
        this.picBitmap = picBitmap;
    }

    public int getCompleteFlag() {
        return completeFlag;
    }

    public void setCompleteFlag(int completeFlag) {
        this.completeFlag = completeFlag;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getShareWay() {
        return shareWay;
    }

    public void setShareWay(String shareWay) {
        this.shareWay = shareWay;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }
}
