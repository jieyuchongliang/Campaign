package com.fujisoft.campaign.bean;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

/**
 * 用户信息Bean
 */
public class User {
    // 用户Id
    public int id;

    // 积分和鲜花
    public int score;

    // 累计积分
    public int scoreSum;

    public String contributeScore;
    // 密码
    public String password;

    // 地址
    public String address;

    // 头像图片url
    public String headPicUrl;

    // 头像图片Bitmap
    public String headPicBitmap;

    // 邮箱
    public String email;

    // 姓名
    public String name;

    // 昵称
    public String nickname;

    // 联系方式
    public String phone;


    public String openId;

    // 用户类型（1员工2个人3管理员）
    public String userType;

    // 性别   男(1)  女(0)
    public String sex;

    // 生日
    public ContactsContract.Data birthday;

    // 好友数量
    public int friendCount;

    public String interest;

    public int adminId;

    // 上次登录时间
    public ContactsContract.Data lastLoginTime;

    public String headPicUrl2;

    public User(){

    }

    public User(int scoreSum, String nickname, String userType) {
        this.scoreSum = scoreSum;
        this.nickname = nickname;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScoreSum() {
        return scoreSum;
    }

    public void setScoreSum(int scoreSum) {
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

    public String getHeadPicUrl() {
        return headPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        this.headPicUrl = headPicUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public ContactsContract.Data getBirthday() {
        return birthday;
    }

    public void setBirthday(ContactsContract.Data birthday) {
        this.birthday = birthday;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public ContactsContract.Data getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(ContactsContract.Data lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getHeadPicBitmap() {
        return headPicBitmap;
    }

    public void setHeadPicBitmap(String headPicBitmap) {
        this.headPicBitmap = headPicBitmap;
    }


    public String getHeadPicUrl2() {
        return headPicUrl2;
    }

    public void setHeadPicUrl2(String headPicUrl2) {
        this.headPicUrl2 = headPicUrl2;
    }

}
