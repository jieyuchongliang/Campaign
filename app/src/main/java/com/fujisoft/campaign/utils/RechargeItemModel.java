package com.fujisoft.campaign.utils;

import java.io.Serializable;

/**
 * 充值页面充值数据类
 */
public class RechargeItemModel implements Serializable {

    public static final int AMOUNT = 1001;

    public int type;
    public Object data;

    public RechargeItemModel(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
