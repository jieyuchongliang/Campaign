package com.fujisoft.campaign.bean;

import android.graphics.Bitmap;

/**
 * 任务列表Bean
 */
public class Task {

    // 任务Id
    private int taskId;
    // 公司名称
    private String companyName;
    //可选必选
    private String requiredFlags;

    // 标题
    private String taskTitle;
    // 任务内容
    private String taskContent;

    // 图片(企业)
    private Bitmap companyPic;

    /**
     * 图片地址,图片bitmap用上面的那个
     */
    private String companyPicUrl;

    // 图片(任务)
    private String taskPic;

    /**
     * 图片地址,图片bitmap用上面的那个
     */
    private String taskPicUrl;

    // 面向群体 1：公司员工；2：外部群体
    private String typeId;

    // 最大年龄
    private int bigAge;

    // 最小年龄
    private int minAge;

    // 适应性别(编码：1-男，0-女，10-男女)
    private String sexName;
    // 适应性别
    private String sex;

    // 分享次数
    private int shareCount;

    // 分享方式
    private String shareWays;

    // 是否可以执行(0：可做->正在进行；1：已完了->已结束)
    private int taskStatus;

    // 任务是否为必须(0：可选；1：必选)
    private int requiredFlag;

    // 任务是否已分享(0：未分享；1：已分享)
    private int completeFlag;

    // 时间
    private String createTime;

    // 用户积分
    private int score;

    // 任务可得积分
    private int integral;

    // 哪个用户发的
    private int userId;

    // 首页广告图片
    private String onePic;

    // 首页广告图片链接
    private String onePicLink;

    //当前百分比
    private String execution;



    // 副标题
    private String subtitle;
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }



    public String getExecution() {
        return execution;
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String taskName) {
        this.companyName = taskName;
    }

    public String getRequiredFlags() {
        return requiredFlags;
    }

    public void setRequiredFlags(String requiredFlags) {
        this.requiredFlags = requiredFlags;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public Bitmap getCompanyPic() {
        return companyPic;
    }

    public void setCompanyPic(Bitmap companyPic) {
        this.companyPic = companyPic;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public int getBigAge() {
        return bigAge;
    }

    public void setBigAge(int bigAge) {
        this.bigAge = bigAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public String getTaskPic() {
        return taskPic;
    }

    public void setTaskPic(String taskPic) {
        this.taskPic = taskPic;
    }

    public int getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(int requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public String getShareWays() {
        return shareWays;
    }

    public void setShareWays(String shareWays) {
        this.shareWays = shareWays;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getCompleteFlag() {
        return completeFlag;
    }

    public void setCompleteFlag(int completeFlag) {
        this.completeFlag = completeFlag;
    }

    public String getCompanyPicUrl() {
        return companyPicUrl;
    }

    public void setCompanyPicUrl(String companyPicUrl) {
        this.companyPicUrl = companyPicUrl;
    }

    public String getTaskPicUrl() {
        return taskPicUrl;
    }

    public void setTaskPicUrl(String taskPicUrl) {
        this.taskPicUrl = taskPicUrl;
    }

    public String getOnePic() {
        return onePic;
    }

    public void setOnePic(String onePic) {
        this.onePic = onePic;
    }

    public String getOnePicLink() {
        return onePicLink;
    }

    public void setOnePicLink(String onePicLink) {
        this.onePicLink = onePicLink;
    }
}
