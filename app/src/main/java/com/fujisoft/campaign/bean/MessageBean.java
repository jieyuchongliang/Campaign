package com.fujisoft.campaign.bean;

import android.graphics.Bitmap;

/**
 * 消息列表用Bean
 */
public class MessageBean {

    /**
     * taskId : 209
     * picUrl : /Uploads/task/587c55dd1c779.jpg
     * title : 123
     */

    private String taskId;
    private String picUrl;
    private String picUrlBitmap;

    public String getCompleteFlag() {
        return completeFlag;
    }

    public void setCompleteFlag(String completeFlag) {
        this.completeFlag = completeFlag;
    }

    private String completeFlag;

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    private String taskStatus;

    private String title;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrlBitmap() {
        return picUrlBitmap;
    }

    public void setPicUrlBitmap(String picUrlBitmap) {
        this.picUrlBitmap = picUrlBitmap;
    }
}
