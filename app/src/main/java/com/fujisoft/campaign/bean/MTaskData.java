package com.fujisoft.campaign.bean;

import java.util.List;

/**
 * Created by 860115007 on 2017/1/25.
 * 任务请求数据集
 */

public class MTaskData {

    private MTasks data;
    private String msg;
    private Boolean success;

    public MTasks getData() {
        return data;
    }

    public void setData(MTasks data) {
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
        return "MTaskData{" +
                "data=" + data +
                ", msg='" + msg + '\'' +
                ", success=" + success +
                '}';
    }

    /**
     * task合集
     */
    public static class MTasks {
        private List<MTask> completeTask;
        private List<MTask> optionalTask;
        private List<MTask> requiredTask;
        private Integer totalNum;
        private Integer userActive;
//
//        public List<MTask> allTask() {
//            if (optionalTask == null) {
//                optionalTask = new ArrayList<>(0);
//            }
//            List<MTask> all = new ArrayList<>(optionalTask.size());
//            for (MTask t : optionalTask) {
//                t.setRequiredFlags(false);
//                all.add(t);
//            }
//            if (requiredTask == null) {
//                requiredTask = new ArrayList<>(0);
//            }
//            for (MTask t : requiredTask) {
//                t.setRequiredFlags(true);
//                all.add(t);
//            }
//            return optionalTask;
//        }

        public List<MTask> getCompleteTask() {
            return completeTask;
        }

        public void setCompleteTask(List<MTask> completeTask) {
            this.completeTask = completeTask;
        }

        public List<MTask> getOptionalTask() {
            return optionalTask;
        }

        public void setOptionalTask(List<MTask> optionalTask) {
            this.optionalTask = optionalTask;
        }

        public List<MTask> getRequiredTask() {
            return requiredTask;
        }

        public void setRequiredTask(List<MTask> requiredTask) {
            this.requiredTask = requiredTask;
        }

        public Integer getTotalNum() {
            return totalNum;
        }

        public void setTotalNum(Integer totalNum) {
            this.totalNum = totalNum;
        }

        public Integer getUserActive() {
            return userActive;
        }

        public void setUserActive(Integer userActive) {
            this.userActive = userActive;
        }

        @Override
        public String toString() {
            return "MTasks{" +
                    "completeTask=" + completeTask +
                    ", optionalTask=" + optionalTask +
                    ", requiredTask=" + requiredTask +
                    ", totalNum=" + totalNum +
                    ", userActive=" + userActive +
                    '}';
        }
    }

    /**
     * task
     */
    public static class MTask {
        private String id;
        private String content;
        private String picUrl;
        private String staff_flower;
        private String name;
        private String completeFlag;

        private String taskStatus;

        private String shareWay;
        // 副标题
        private String subtitle;
        private String requiredFlags;
        private String minShareTime;
        private String maxShareTime;
        private String time;

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getShareWay() {
            return shareWay;
        }

        public void setShareWay(String shareWay) {
            this.shareWay = shareWay;
        }


        public String getCompleteFlag() {
            return completeFlag;
        }

        public void setCompleteFlag(String completeFlag) {
            this.completeFlag = completeFlag;
        }


        public String getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(String taskStatus) {
            this.taskStatus = taskStatus;
        }

        public String getRequiredFlags() {
            return requiredFlags;
        }

        public void setRequiredFlags(String requiredFlags) {
            this.requiredFlags = requiredFlags;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getStaff_flower() {
            return staff_flower;
        }

        public void setStaff_flower(String staff_flower) {
            this.staff_flower = staff_flower;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMinShareTime() {
            return minShareTime;
        }

        public void setMinShareTime(String minShareTime) {
            this.minShareTime = minShareTime;
        }

        public String getMaxShareTime() {
            return maxShareTime;
        }

        public void setMaxShareTime(String maxShareTime) {
            this.maxShareTime = maxShareTime;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "MTask{" +
                    "id='" + id + '\'' +
                    ", content='" + content + '\'' +
                    ", picUrl='" + picUrl + '\'' +
                    ", staff_flower='" + staff_flower + '\'' +
                    ", name='" + name + '\'' +
                    ", maxShareTime='" + maxShareTime + '\'' +
                    ", time='" + time + '\'' +
                    ", taskStatus='" + taskStatus + '\'' +
                    ", requiredFlags=" + requiredFlags +'\'' +
                    ", minShareTime='" + minShareTime +
                    '}';
        }
    }
}
