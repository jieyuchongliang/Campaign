package com.fujisoft.campaign.bean;


/**
 * 员工列表bean
 */
public class StaffBean {
    private Object name_s;
    private String fTaskCnt;
    private String hTCnt;
    private String aTCnt;
    private String id;
    private String score;
    private String scoreSum;
    private String contributeScore;
    private String password;
    private String address;
    private Object headPicUrl;
    private String headPicBitmap;
    private String email;
    private Object name;
    private Object nickname;
    private String phone;
    private Object openId;
    private String userType;
    private Object sex;
    private Object birthday;
    private Object friendCount;
    private Object interest;
    private String adminId;
    private Object lastLoginTime;
    private String shareTime;
    private String maxShareTime;

    public StaffBean(Object name_s, String fTaskCnt, String hTCnt, String aTCnt, Object name, Object lastLoginTime, String shareTime, String maxShareTime) {
        if ("null".equals(name_s)) {
            this.name_s = "";
        } else {
            this.name_s = name_s;
        }
        this.fTaskCnt = fTaskCnt;
        this.hTCnt = hTCnt;
        this.aTCnt = aTCnt;
        if ("null".equals(name)) {
            this.name = "";
        } else {
            this.name = name;
        }
        this.lastLoginTime = lastLoginTime;
        this.shareTime = shareTime;
        this.maxShareTime = maxShareTime;
    }

    public Object getName_s() {
        return name_s;
    }

    public void setName_s(Object name_s) {
        this.name_s = name_s;
    }

    public String getFTaskCnt() {
        return fTaskCnt;
    }

    public void setFTaskCnt(String fTaskCnt) {
        this.fTaskCnt = fTaskCnt;
    }

    public String getHTCnt() {
        return hTCnt;
    }

    public void setHTCnt(String hTCnt) {
        this.hTCnt = hTCnt;
    }

    public String getATCnt() {
        return aTCnt;
    }

    public void setATCnt(String aTCnt) {
        this.aTCnt = aTCnt;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getHeadPicUrl() {
        return headPicUrl;
    }

    public void setHeadPicUrl(Object headPicUrl) {
        this.headPicUrl = headPicUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getName() {
        if (name == null) {
            this.name = "";
        }
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getNickname() {
        return nickname;
    }

    public void setNickname(Object nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Object getOpenId() {
        return openId;
    }

    public void setOpenId(Object openId) {
        this.openId = openId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Object getSex() {
        return sex;
    }

    public void setSex(Object sex) {
        this.sex = sex;
    }

    public Object getBirthday() {
        return birthday;
    }

    public void setBirthday(Object birthday) {
        this.birthday = birthday;
    }

    public Object getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(Object friendCount) {
        this.friendCount = friendCount;
    }

    public Object getInterest() {
        return interest;
    }

    public void setInterest(Object interest) {
        this.interest = interest;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public Object getLastLoginTime() {
        if (lastLoginTime == null) {
            lastLoginTime = "";
        }
        return lastLoginTime;
    }

    public void setLastLoginTime(Object lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getHeadPicBitmap() {
        return headPicBitmap;
    }

    public void setHeadPicBitmap(String headPicBitmap) {
        this.headPicBitmap = headPicBitmap;
    }

    public String getfTaskCnt() {
        return fTaskCnt;
    }

    public void setfTaskCnt(String fTaskCnt) {
        this.fTaskCnt = fTaskCnt;
    }

    public String gethTCnt() {
        return hTCnt;
    }

    public void sethTCnt(String hTCnt) {
        this.hTCnt = hTCnt;
    }

    public String getaTCnt() {
        return aTCnt;
    }

    public void setaTCnt(String aTCnt) {
        this.aTCnt = aTCnt;
    }

    public String getShareTime() {
        return shareTime;
    }

    public void setShareTime(String shareTime) {
        this.shareTime = shareTime;
    }

    public String getMaxShareTime() {
        return maxShareTime;
    }

    public void setMaxShareTime(String maxShareTime) {
        this.maxShareTime = maxShareTime;
    }
}
