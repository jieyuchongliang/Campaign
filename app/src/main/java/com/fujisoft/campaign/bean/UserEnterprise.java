package com.fujisoft.campaign.bean;

/**
 * 用户任务列表Bean
 */
public class UserEnterprise {

    // 用户Id
    public int userId;

    // 企业id
    public int enterpriseId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
