package com.fujisoft.campaign.utils;

import java.io.Serializable;

/**
 * 商品详情的规格的值的数据类
 */
public class GoodsStandardsValueItemModel implements Serializable {

    public static final int VALUES = 1003;

    public int type;
    public String standardName;
    public Object value;

    public GoodsStandardsValueItemModel(int type, String standardName, Object value) {
        this.type = type;
        this.standardName = standardName;
        this.value = value;
    }
}
